/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
 *
 * "Hilbert II" is free software; you can redistribute
 * it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 */

package org.qedeq.kernel.bo.service.logic;


import org.qedeq.base.io.Parameters;
import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.base.utility.Version;
import org.qedeq.kernel.bo.common.PluginExecutor;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.logic.ProofCheckerFactoryImpl;
import org.qedeq.kernel.bo.logic.common.FormulaUtility;
import org.qedeq.kernel.bo.logic.common.FunctionKey;
import org.qedeq.kernel.bo.logic.common.LogicalCheckExceptionList;
import org.qedeq.kernel.bo.logic.common.PredicateKey;
import org.qedeq.kernel.bo.logic.common.ReferenceResolver;
import org.qedeq.kernel.bo.logic.proof.checker.ProofCheckException;
import org.qedeq.kernel.bo.logic.proof.common.ProofCheckerFactory;
import org.qedeq.kernel.bo.logic.proof.common.RuleChecker;
import org.qedeq.kernel.bo.module.ControlVisitor;
import org.qedeq.kernel.bo.module.KernelModuleReferenceList;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.Reference;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.list.ElementList;
import org.qedeq.kernel.se.base.module.Axiom;
import org.qedeq.kernel.se.base.module.ChangedRule;
import org.qedeq.kernel.se.base.module.ChangedRuleList;
import org.qedeq.kernel.se.base.module.FormalProof;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.base.module.FunctionDefinition;
import org.qedeq.kernel.se.base.module.Header;
import org.qedeq.kernel.se.base.module.InitialFunctionDefinition;
import org.qedeq.kernel.se.base.module.InitialPredicateDefinition;
import org.qedeq.kernel.se.base.module.PredicateDefinition;
import org.qedeq.kernel.se.base.module.Proposition;
import org.qedeq.kernel.se.base.module.Rule;
import org.qedeq.kernel.se.common.CheckLevel;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.Plugin;
import org.qedeq.kernel.se.common.RuleKey;
import org.qedeq.kernel.se.common.SourceFileException;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.dto.list.DefaultAtom;
import org.qedeq.kernel.se.dto.list.DefaultElementList;


/**
 * Checks if all propositions have a correct formal proof.
 *
 * @author  Michael Meyling
 */
public final class FormalProofCheckerExecutor extends ControlVisitor implements PluginExecutor,
        ReferenceResolver, RuleChecker {

    /** This class. */
    private static final Class CLASS = FormalProofCheckerExecutor.class;

    /** Factory for generating new checkers. */
    private ProofCheckerFactory checkerFactory = null;

    /** Parameters for checker. */
    private Parameters parameters;

    /** Rule version the module claims to use at maximum. */
    private Version ruleVersion;

    /**
     * Constructor.
     *
     * @param   plugin      This plugin we work for.
     * @param   qedeq       QEDEQ BO object.
     * @param   parameters  Parameters.
     */
    FormalProofCheckerExecutor(final Plugin plugin, final KernelQedeqBo qedeq,
            final Parameters parameters) {
        super(plugin, qedeq);
        final String method = "FormalProofCheckerExecutor(Plugin, KernelQedeqBo, Map)";
        this.parameters = parameters;
        final String checkerFactoryClass = parameters.getString("checkerFactory");
        if (checkerFactoryClass != null && checkerFactoryClass.length() > 0) {
            try {
                Class cl = Class.forName(checkerFactoryClass);
                checkerFactory = (ProofCheckerFactory) cl.newInstance();
            } catch (ClassNotFoundException e) {
                Trace.fatal(CLASS, this, method, "ProofCheckerFactory class not in class path: "
                    + checkerFactoryClass, e);
            } catch (InstantiationException e) {
                Trace.fatal(CLASS, this, method, "ProofCheckerFactory class could not be instanciated: "
                    + checkerFactoryClass, e);
            } catch (IllegalAccessException e) {
                Trace.fatal(CLASS, this, method,
                    "Programming error, access for instantiation failed for model: "
                    + checkerFactoryClass, e);
            } catch (RuntimeException e) {
                Trace.fatal(CLASS, this, method,
                    "Programming error, instantiation failed for model: " + checkerFactoryClass, e);
            }
        }
        // fallback is the default checker factory
        if (checkerFactory == null) {
            checkerFactory = new ProofCheckerFactoryImpl();
        }
    }

    private Parameters getParameters() {
        return parameters;
    }

    public Object executePlugin() {
        ruleVersion = new Version("0.00.00");  // we set this as module rule version, and hope it will be changed
        QedeqLog.getInstance().logRequest(
                "Check logical correctness", getQedeqBo().getUrl());
        getServices().checkModule(getQedeqBo().getModuleAddress());
        if (!getQedeqBo().isChecked()) {
            final String msg = "Check of logical correctness failed";
            QedeqLog.getInstance().logFailureReply(msg, getQedeqBo().getUrl(),
                "Module is not even well formed.");
            return Boolean.FALSE;
        }
//        getQedeqBo().setLogicalProgressState(LogicalModuleState.STATE_EXTERNAL_CHECKING);
        KernelModuleReferenceList list = (KernelModuleReferenceList) getQedeqBo().getRequiredModules();
        for (int i = 0; i < list.size(); i++) {
            Trace.trace(CLASS, "check(DefaultQedeqBo)", "checking label", list.getLabel(i));
            final FormalProofCheckerExecutor checker = new FormalProofCheckerExecutor(getPlugin(),
                    list.getKernelQedeqBo(i), getParameters());
            checker.executePlugin();
            if (list.getKernelQedeqBo(i).hasErrors()) {
                addError(new CheckRequiredModuleException(
                    LogicErrors.MODULE_IMPORT_CHECK_FAILED_CODE,
                    LogicErrors.MODULE_IMPORT_CHECK_FAILED_TEXT
                    + list.getQedeqBo(i).getModuleAddress(),
                    list.getModuleContext(i)));
            }
        }
        // has at least one import errors?
        if (getQedeqBo().hasErrors()) {
            final String msg = "Check of logical correctness failed";
            QedeqLog.getInstance().logFailureReply(msg, getQedeqBo().getUrl(),
                 StringUtility.replace(getQedeqBo().getErrors().getMessage(), "\n", "\n\t"));
            return Boolean.FALSE;
        }
        try {
            traverse();
        } catch (SourceFileExceptionList e) {
            final String msg = "Check of logical correctness failed";
            QedeqLog.getInstance().logFailureReply(msg, getQedeqBo().getUrl(),
                 StringUtility.replace(e.getMessage(), "\n", "\n\t"));
            return Boolean.FALSE;
        } finally {
            getQedeqBo().addPluginErrorsAndWarnings(getPlugin(), getErrorList(), getWarningList());
        }
        QedeqLog.getInstance().logSuccessfulReply(
            "Check of logical correctness successful", getQedeqBo().getUrl());
        return Boolean.TRUE;
    }

    public void visitEnter(final Header header) throws ModuleDataException {
        if (header.getSpecification() == null
                || header.getSpecification().getRuleVersion() == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        setLocationWithinModule(context + ".getSpecification().getRuleVersion()");
        final String version = header.getSpecification().getRuleVersion().trim();
        if (!checkerFactory.isRuleVersionSupported(version)) {
            addError(new ProofCheckException(
                LogicErrors.RULE_VERSION_HAS_STILL_NO_PROOF_CHECKER_CODE,
                LogicErrors.RULE_VERSION_HAS_STILL_NO_PROOF_CHECKER_TEXT + version,
                getCurrentContext()));
        } else {
            try {
                ruleVersion = new Version(version);
            } catch (RuntimeException e) {
                addError(new ProofCheckException(
                    LogicErrors.THIS_IS_NOT_VALID_VERSION_FORMAT_CODE,
                    LogicErrors.THIS_IS_NOT_VALID_VERSION_FORMAT_TEXT + version,
                    getCurrentContext()));
            }
        }
        setLocationWithinModule(context);
    }

    public void visitEnter(final Axiom axiom) throws ModuleDataException {
        if (getNodeBo().isWellFormed()) {
            getNodeBo().setProved(CheckLevel.SUCCESS);
        } else {
            getNodeBo().setProved(CheckLevel.FAILURE);
            addError(new ProofCheckException(
                LogicErrors.NODE_FORMULAS_MUST_BE_WELL_FORMED_CODE,
                LogicErrors.NODE_FORMULAS_MUST_BE_WELL_FORMED_TEXT,
                getCurrentContext()));
            return;
        }
        setBlocked(true);
    }

    public void visitLeave(final Axiom axiom) {
        setBlocked(false);
    }

    public void visitEnter(final PredicateDefinition definition)
            throws ModuleDataException {
        if (getNodeBo().isWellFormed()) {
            getNodeBo().setProved(CheckLevel.SUCCESS);
        } else {
            getNodeBo().setProved(CheckLevel.FAILURE);
            addError(new ProofCheckException(
                LogicErrors.NODE_FORMULAS_MUST_BE_WELL_FORMED_CODE,
                LogicErrors.NODE_FORMULAS_MUST_BE_WELL_FORMED_TEXT,
                getCurrentContext()));
            return;
        }
        setBlocked(true);
    }

    public void visitLeave(final PredicateDefinition definition) {
        setBlocked(false);
    }

    public void visitEnter(final InitialPredicateDefinition definition)
            throws ModuleDataException {
        if (getNodeBo().isWellFormed()) {
            getNodeBo().setProved(CheckLevel.SUCCESS);
        } else {
            getNodeBo().setProved(CheckLevel.FAILURE);
            addError(new ProofCheckException(
                LogicErrors.NODE_FORMULAS_MUST_BE_WELL_FORMED_CODE,
                LogicErrors.NODE_FORMULAS_MUST_BE_WELL_FORMED_TEXT,
                getCurrentContext()));
            return;
        }
        setBlocked(true);
    }

    public void visitLeave(final InitialPredicateDefinition definition) {
        setBlocked(false);
    }

    public void visitEnter(final InitialFunctionDefinition definition)
            throws ModuleDataException {
        if (getNodeBo().isWellFormed()) {
            getNodeBo().setProved(CheckLevel.SUCCESS);
        } else {
            getNodeBo().setProved(CheckLevel.FAILURE);
            addError(new ProofCheckException(
                LogicErrors.NODE_FORMULAS_MUST_BE_WELL_FORMED_CODE,
                LogicErrors.NODE_FORMULAS_MUST_BE_WELL_FORMED_TEXT,
                getCurrentContext()));
            return;
        }
        setBlocked(true);
    }

    public void visitLeave(final InitialFunctionDefinition definition) {
        setBlocked(false);
    }

    public void visitEnter(final FunctionDefinition definition)
            throws ModuleDataException {
        if (getNodeBo().isWellFormed()) {
            getNodeBo().setProved(CheckLevel.SUCCESS);
        } else {
            getNodeBo().setProved(CheckLevel.FAILURE);
            addError(new ProofCheckException(
                LogicErrors.NODE_FORMULAS_MUST_BE_WELL_FORMED_CODE,
                LogicErrors.NODE_FORMULAS_MUST_BE_WELL_FORMED_TEXT,
                getCurrentContext()));
            return;
        }
        setBlocked(true);
    }

    public void visitLeave(final FunctionDefinition definition) {
        setBlocked(false);
    }

    public void visitEnter(final Proposition proposition)
            throws ModuleDataException {
        // we only check this node, if the well formed check was successful
        if (!getNodeBo().isWellFormed()) {
            getNodeBo().setProved(CheckLevel.FAILURE);
            addError(new ProofCheckException(
                LogicErrors.NODE_FORMULAS_MUST_BE_WELL_FORMED_CODE,
                LogicErrors.NODE_FORMULAS_MUST_BE_WELL_FORMED_TEXT,
                getCurrentContext()));
            return;
        }
        getNodeBo().setProved(CheckLevel.UNCHECKED);
        if (proposition.getFormula() == null) {
            getNodeBo().setProved(CheckLevel.FAILURE);
            addError(new ProofCheckException(
                LogicErrors.PROPOSITION_FORMULA_MUST_NOT_BE_NULL_CODE,
                LogicErrors.PROPOSITION_FORMULA_MUST_NOT_BE_NULL_TEXT,
                getCurrentContext()));
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        boolean correctProofFound = false;
        // we start checking
        if (proposition.getFormalProofList() != null) {
            for (int i = 0; i < proposition.getFormalProofList().size(); i++) {
                final FormalProof proof = proposition.getFormalProofList().get(i);
                if (proof != null) {
                    final FormalProofLineList list = proof.getFormalProofLineList();
                    if (list != null) {
                        setLocationWithinModule(context + ".getFormalProofList().get("
                           + i + ").getFormalProofLineList()");
                        LogicalCheckExceptionList eList
                            = checkerFactory.createProofChecker(ruleVersion).checkProof(
                            proposition.getFormula().getElement(), list, this,
                            getCurrentContext(),
                            this);
                        if (!correctProofFound && eList.size() == 0) {
                            correctProofFound = true;
                        }
                        for (int j = 0; j < eList.size(); j++) {
                            addError(eList.get(j));
                        }
                    }
                }
            }
        }
        setLocationWithinModule(context + ".getFormula()");
        // only if we found at least one error free formal proof
        if (correctProofFound) {
            getNodeBo().setProved(CheckLevel.SUCCESS);
        } else {
            getNodeBo().setProved(CheckLevel.FAILURE);
            addError(new ProofCheckException(
                LogicErrors.NO_FORMAL_PROOF_FOUND_CODE,
                LogicErrors.NO_FORMAL_PROOF_FOUND_TEXT,
                getCurrentContext()));
        }
        setBlocked(true);
    }

    public void visitLeave(final Proposition definition) {
        setBlocked(false);
    }

    public void visitEnter(final Rule rule) throws ModuleDataException {
        final String context = getCurrentContext().getLocationWithinModule();
        // FIXME 20110618 m31: check if this is really a higher version than before?
        getNodeBo().setProved(CheckLevel.UNCHECKED);
        final ChangedRuleList list = rule.getChangedRuleList();
        for (int i = 0; list != null && i < list.size(); i++) {
            setLocationWithinModule(context + ".getChangedRuleList().get(" + i + ").getVersion()");
            final ChangedRule r = list.get(i);
            if (!Version.equals(rule.getVersion(), r.getVersion())) {
                addError(new ProofCheckException(
                    LogicErrors.OTHER_RULE_VERSION_EXPECTED_CODE,
                    LogicErrors.OTHER_RULE_VERSION_EXPECTED_TEXT1 + rule.getVersion()
                    + LogicErrors.OTHER_RULE_VERSION_EXPECTED_TEXT2 + r.getVersion(),
                    getCurrentContext()));
            }
        }

        if (getNodeBo().isNotProved()) {
            getNodeBo().setProved(CheckLevel.SUCCESS);
        } else {
            getNodeBo().setProved(CheckLevel.FAILURE);
        }
    }

    protected void addError(final ModuleDataException me) {
        if (getNodeBo() != null) {
            getNodeBo().setProved(CheckLevel.FAILURE);
        }
        super.addError(me);
    }

    protected void addError(final SourceFileException me) {
        if (getNodeBo() != null) {
            getNodeBo().setProved(CheckLevel.FAILURE);
        }
        super.addError(me);
    }

    public boolean isProvedFormula(final String reference) {
        final String method = "hasProvedFormula";
        final Reference ref = getReference(reference, getCurrentContext(), false, false);
        if (ref == null) {
            Trace.info(CLASS, method, "ref == null");
            return false;
        }
        if (ref.isExternalModuleReference()) {
            Trace.info(CLASS, method, "ref is external module");
            return false;
        }
        if (!ref.isNodeReference()) {
            Trace.info(CLASS, method, "ref is no node reference");
            return false;
        }
        if (null == ref.getNode()) {
            Trace.info(CLASS, method, "ref node == null");
            return false;
        }
        if (ref.isSubReference()) {
            return false;
        }
        if (!ref.isProofLineReference()) {
            if (!ref.getNode().isProved()) {
                Trace.info(CLASS, method, "ref node is not marked as proved: " + reference);
            }
            if (!ref.getNode().isProved()) {
                return false;
            }
            if (!ref.getNode().hasFormula()) {
                Trace.info(CLASS, method, "node has no formula: " + reference);
                return false;
            }
            return ref.getNode().isProved();
        } else {
            Trace.info(CLASS, method, "proof line references are not ok!");
            return false;
        }
    }

    public Element getNormalizedReferenceFormula(final String reference) {
        if (!isProvedFormula(reference)) {
            return null;
        }
        final Reference ref = getReference(reference, getCurrentContext(), false, false);
        final Element formula = ref.getNode().getFormula();
        return getNormalizedFormula(ref.getNode().getQedeqBo(), formula);
    }

    public Element getNormalizedFormula(final Element formula) {
        return getNormalizedFormula(getQedeqBo(), formula);
    }

    private Element getNormalizedFormula(final KernelQedeqBo qedeq, final Element formula) {
        if (formula == null) {
            return null;
        }
        if (formula.isAtom()) {
            return new DefaultAtom(formula.getAtom().getString());
        }
        return getNormalizedFormula(qedeq, formula.getList());
    }

    private ElementList getNormalizedFormula(final KernelQedeqBo qedeq, final ElementList formula) {
        final ElementList result = new DefaultElementList(formula.getOperator());
        if (FormulaUtility.isPredicateConstant(formula)) {
            final PredicateKey key = new PredicateKey(formula.getElement(0).getAtom().getString(),
                "" + (formula.getList().size() - 1));
            final DefaultAtom atom = new DefaultAtom(
                qedeq.getExistenceChecker().get(key).getContext().getModuleLocation().getUrl()
                + "$" + key.getName());
            result.add(atom);
            for (int i = 1; i < formula.size(); i++) {
                result.add(getNormalizedFormula(qedeq, formula.getElement(i)));
            }
        } else if (FormulaUtility.isFunctionConstant(formula)) {
            final FunctionKey key = new FunctionKey(formula.getElement(0).getAtom().getString(),
                    "" + (formula.getList().size() - 1));
            final DefaultAtom atom = new DefaultAtom(
                qedeq.getExistenceChecker().get(key).getContext().getModuleLocation().getUrl()
                + "$" + key.getName());
            result.add(atom);
            for (int i = 1; i < formula.size(); i++) {
                result.add(getNormalizedFormula(qedeq, formula.getElement(i)));
            }
        } else {
            for (int i = 0; i < formula.size(); i++) {
                result.add(getNormalizedFormula(qedeq, formula.getElement(i)));
            }
        }
        return result;
    }

    public boolean isLocalProofLineReference(final String reference) {
        // here we have no proof lines
        return false;
    }

    public ModuleContext getReferenceContext(final String reference) {
        // here we have no proof lines
        return null;
    }

    public Element getNormalizedLocalProofLineReference(final String reference) {
        // here we have no proof lines
        return null;
    }

    public RuleKey getRule(final String ruleName) {
        final RuleKey local = getLocalRuleKey(ruleName);
        if (local == null) {
            return getQedeqBo().getExistenceChecker().getParentRuleKey(
            ruleName);
        }
        return local;
    }


}
