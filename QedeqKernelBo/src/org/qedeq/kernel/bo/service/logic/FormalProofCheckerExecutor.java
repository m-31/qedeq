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

import java.util.Map;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.bo.common.PluginExecutor;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.logic.ProofCheckerFactoryImpl;
import org.qedeq.kernel.bo.logic.common.LogicalCheckExceptionList;
import org.qedeq.kernel.bo.logic.common.ProofCheckerFactory;
import org.qedeq.kernel.bo.logic.common.ReferenceResolver;
import org.qedeq.kernel.bo.module.ControlVisitor;
import org.qedeq.kernel.bo.module.KernelModuleReferenceList;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.se.base.module.Axiom;
import org.qedeq.kernel.se.base.module.FormalProof;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.base.module.FunctionDefinition;
import org.qedeq.kernel.se.base.module.InitialFunctionDefinition;
import org.qedeq.kernel.se.base.module.InitialPredicateDefinition;
import org.qedeq.kernel.se.base.module.PredicateDefinition;
import org.qedeq.kernel.se.base.module.Proposition;
import org.qedeq.kernel.se.base.module.Rule;
import org.qedeq.kernel.se.common.CheckLevel;
import org.qedeq.kernel.se.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.Plugin;
import org.qedeq.kernel.se.common.SourceFileException;
import org.qedeq.kernel.se.common.SourceFileExceptionList;


/**
 * Checks if all propositions have a correct formal proof.
 *
 * @author  Michael Meyling
 */
public final class FormalProofCheckerExecutor extends ControlVisitor implements PluginExecutor {

    /** This class. */
    private static final Class CLASS = FormalProofCheckerExecutor.class;

    /** Existence checker for predicate and function constants. */
    private ModuleConstantsExistenceCheckerImpl existence;

    /** Factory for generating new checkers. */
    private ProofCheckerFactory checkerFactory = null;

    /** Parameters for checker. */
    private Map parameters;

    /**
     * Constructor.
     *
     * @param   plugin      This plugin we work for.
     * @param   qedeq       QEDEQ BO object.
     * @param   parameters  Parameters.
     */
    FormalProofCheckerExecutor(final Plugin plugin, final KernelQedeqBo qedeq,
            final Map parameters) {
        super(plugin, qedeq);
        final String method = "FormalProofCheckerExecutor(Plugin, KernelQedeqBo, Map)";
        this.parameters = parameters;
        final String checkerFactoryClass
            = (parameters != null ? (String) parameters.get("checkerFactory") : null);
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

    private Map getParameters() {
        return parameters;
    }

    public Object executePlugin() {
        if (getQedeqBo().isChecked()) {
            return Boolean.TRUE;
        }
        QedeqLog.getInstance().logRequest(
                "Check logical correctness for \"" + IoUtility.easyUrl(getQedeqBo().getUrl()) + "\"");
        KernelContext.getInstance().checkModule(getQedeqBo().getModuleAddress());
        if (!getQedeqBo().isChecked()) {
            final String msg = "Check of well formedness failed for \"" + getQedeqBo().getUrl()
            + "\"";
            QedeqLog.getInstance().logFailureReply(msg, "Module is not even well formed.");
            return Boolean.FALSE;
        }
//        getQedeqBo().setLogicalProgressState(LogicalModuleState.STATE_EXTERNAL_CHECKING);
        final SourceFileExceptionList sfl = new DefaultSourceFileExceptionList();
        KernelModuleReferenceList list = (KernelModuleReferenceList) getQedeqBo().getRequiredModules();
        for (int i = 0; i < list.size(); i++) {
            Trace.trace(CLASS, "check(DefaultQedeqBo)", "checking label", list.getLabel(i));
            final FormalProofCheckerExecutor checker = new FormalProofCheckerExecutor(getPlugin(),
                    list.getKernelQedeqBo(i), getParameters());
            checker.executePlugin();
            if (list.getKernelQedeqBo(i).hasErrors()) {
                ModuleDataException md = new CheckRequiredModuleException(
                    LogicErrors.MODULE_IMPORT_CHECK_FAILED_CODE,
                    LogicErrors.MODULE_IMPORT_CHECK_FAILED_TEXT
                    + list.getQedeqBo(i).getModuleAddress(),
                    list.getModuleContext(i));
                sfl.add(getQedeqBo().createSourceFileException(getPlugin(), md));
            }
        }
        // has at least one import errors?
        if (sfl.size() > 0) {
//            getQedeqBo().setLogicalFailureState(LogicalModuleState.STATE_EXTERNAL_CHECKING_FAILED, sfl);
            getQedeqBo().addPluginErrorsAndWarnings(getPlugin(), sfl, null);
            final String msg = "Check of logical correctness failed for \"" + IoUtility.easyUrl(getQedeqBo().getUrl())
                + "\"";
            QedeqLog.getInstance().logFailureReply(msg, sfl.getMessage());
            return Boolean.FALSE;
        }
//        getQedeqBo().setLogicalProgressState(LogicalModuleState.STATE_INTERNAL_CHECKING);
//        getQedeqBo().setExistenceChecker(existence);
        try {
            traverse();
        } catch (SourceFileExceptionList e) {
            getQedeqBo().addPluginErrorsAndWarnings(getPlugin(), e, null);
//            getQedeqBo().setLogicalFailureState(LogicalModuleState.STATE_INTERNAL_CHECKING_FAILED, e);
            final String msg = "Check of logical correctness failed for \"" + IoUtility.easyUrl(getQedeqBo().getUrl())
                + "\"";
            QedeqLog.getInstance().logFailureReply(msg, sfl.getMessage());
            return Boolean.FALSE;
        }
//        getQedeqBo().setChecked(existence);
        QedeqLog.getInstance().logSuccessfulReply(
                "Check of logical correctness successful for \"" + IoUtility.easyUrl(getQedeqBo().getUrl()) + "\"");
        return Boolean.TRUE;
    }

    public void traverse() throws SourceFileExceptionList {
        try {
            this.existence = new ModuleConstantsExistenceCheckerImpl(getQedeqBo());
        } catch (ModuleDataException me) {
            addError(me);
            throw getErrorList();
        }
        super.traverse();
    }

    public void visitEnter(final Axiom axiom) throws ModuleDataException {
        if (axiom == null) {
            return;
        }
        if (getNodeBo().isWellFormed()) {
            getNodeBo().setProved(CheckLevel.SUCCESS);
        } else {
            getNodeBo().setProved(CheckLevel.FAILURE);
        }
        setBlocked(true);
    }

    public void visitLeave(final Axiom axiom) {
        setBlocked(false);
    }

    public void visitEnter(final PredicateDefinition definition)
            throws ModuleDataException {
        if (definition == null) {
            return;
        }
        if (getNodeBo().isWellFormed()) {
            getNodeBo().setProved(CheckLevel.SUCCESS);
        } else {
            getNodeBo().setProved(CheckLevel.FAILURE);
        }
        setBlocked(true);
    }

    public void visitLeave(final PredicateDefinition definition) {
        setBlocked(false);
    }

    public void visitEnter(final InitialPredicateDefinition definition)
            throws ModuleDataException {
        if (definition == null) {
            return;
        }
        if (getNodeBo().isWellFormed()) {
            getNodeBo().setProved(CheckLevel.SUCCESS);
        } else {
            getNodeBo().setProved(CheckLevel.FAILURE);
        }
        setBlocked(true);
    }

    public void visitLeave(final InitialPredicateDefinition definition) {
        setBlocked(false);
    }

    public void visitEnter(final InitialFunctionDefinition definition)
            throws ModuleDataException {
        if (definition == null) {
            return;
        }
        if (getNodeBo().isWellFormed()) {
            getNodeBo().setProved(CheckLevel.SUCCESS);
        } else {
            getNodeBo().setProved(CheckLevel.FAILURE);
        }
        setBlocked(true);
    }

    public void visitLeave(final InitialFunctionDefinition definition) {
        setBlocked(false);
    }

    public void visitEnter(final FunctionDefinition definition)
            throws ModuleDataException {
        if (definition == null) {
            return;
        }
        if (getNodeBo().isWellFormed()) {
            getNodeBo().setProved(CheckLevel.SUCCESS);
        } else {
            getNodeBo().setProved(CheckLevel.FAILURE);
        }
        setBlocked(true);
    }

    public void visitLeave(final FunctionDefinition definition) {
        setBlocked(false);
    }

    public void visitEnter(final Proposition proposition)
            throws ModuleDataException {
        if (proposition == null) {
            return;
        }
        if (!getNodeBo().isWellFormed()) {
            getNodeBo().setProved(CheckLevel.FAILURE);
            return;
        }
        getNodeBo().setProved(CheckLevel.UNCHECKED);
        if (proposition.getFormula() == null) {
            getNodeBo().setProved(CheckLevel.FAILURE);
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        boolean proved = false;
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
                            = checkerFactory.createProofChecker().checkProof(
                            proposition.getFormula().getElement(), list, getCurrentContext(),
                            (ReferenceResolver) null, existence);
                        if (!proved && eList.size() == 0) {
                            proved = true;
                        }
                        for (int j = 0; j < eList.size(); j++) {
                            addError(eList.get(j));
                        }
                    }
                }
            }
        }
        // only if we found at least one error free formal proof
        if (proved) {
            getNodeBo().setProved(CheckLevel.SUCCESS);
        } else {
            getNodeBo().setProved(CheckLevel.FAILURE);
        }
        setLocationWithinModule(context);
        // if we found no errors this node is ok
        setBlocked(true);
    }

    public void visitLeave(final Proposition definition) {
        setBlocked(false);
    }

    public void visitEnter(final Rule rule) throws ModuleDataException {
        if (rule == null) {
            return;
        }
        if (getNodeBo().isWellFormed()) {
            getNodeBo().setProved(CheckLevel.SUCCESS);
        } else {
            getNodeBo().setProved(CheckLevel.FAILURE);
        }
        setBlocked(true);
    }

    public void visitLeave(final Rule rule) {
        setBlocked(false);
    }

    protected void addError(final ModuleDataException me) {
        if (getNodeBo() != null) {
            getNodeBo().setWellFormed(CheckLevel.FAILURE);
        }
        super.addError(me);
    }

    protected void addError(final SourceFileException me) {
        if (getNodeBo() != null) {
            getNodeBo().setWellFormed(CheckLevel.FAILURE);
        }
        super.addError(me);
    }

    /**
     * Set location information where are we within the original module.
     *
     * @param   locationWithinModule    Location within module.
     */
    public void setLocationWithinModule(final String locationWithinModule) {
        getCurrentContext().setLocationWithinModule(locationWithinModule);
    }

}
