/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2013,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.logic.proof.checker;

import java.util.HashMap;
import java.util.Map;

import org.qedeq.base.io.Version;
import org.qedeq.base.io.VersionSet;
import org.qedeq.base.utility.Enumerator;
import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.bo.logic.common.FormulaChecker;
import org.qedeq.kernel.bo.logic.common.FormulaUtility;
import org.qedeq.kernel.bo.logic.common.LogicalCheckExceptionList;
import org.qedeq.kernel.bo.logic.common.Operators;
import org.qedeq.kernel.bo.logic.common.ReferenceResolver;
import org.qedeq.kernel.bo.logic.proof.common.ProofChecker;
import org.qedeq.kernel.bo.logic.proof.common.RuleChecker;
import org.qedeq.kernel.bo.logic.wf.FormulaCheckerImpl;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.list.ElementList;
import org.qedeq.kernel.se.base.module.Add;
import org.qedeq.kernel.se.base.module.ConditionalProof;
import org.qedeq.kernel.se.base.module.Existential;
import org.qedeq.kernel.se.base.module.FormalProofLine;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.base.module.ModusPonens;
import org.qedeq.kernel.se.base.module.Reason;
import org.qedeq.kernel.se.base.module.Rename;
import org.qedeq.kernel.se.base.module.Rule;
import org.qedeq.kernel.se.base.module.SubstFree;
import org.qedeq.kernel.se.base.module.SubstFunc;
import org.qedeq.kernel.se.base.module.SubstPred;
import org.qedeq.kernel.se.base.module.Universal;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.common.RuleKey;
import org.qedeq.kernel.se.dto.list.DefaultElementList;
import org.qedeq.kernel.se.dto.list.ElementSet;


/**
 * Formal proof checker for basic rules and conditional proof.
 *
 * @author  Michael Meyling
 */
public class ProofChecker2Impl implements ProofChecker, ReferenceResolver {

    /** Proof we want to check. */
    private FormalProofLineList proof;

    /** Module context of proof line list. */
    private ModuleContext moduleContext;

    /** Current context. */
    private ModuleContext currentContext;

    /** Resolver for external references. */
    private ReferenceResolver resolver;

    /** All exceptions that occurred during checking. */
    private LogicalCheckExceptionList exceptions;

    /** Array with proof status for each proof line. */
    private boolean[] lineProved;

    /** Maps local proof line labels to local line number Integers. */
    private Map label2line;

    /** Rule version we support. */
    private final VersionSet supported;

    /** These preconditions apply. This is a conjunction with 0 to n elements. */
    private ElementList conditions;

    /** Rule existence checker. */
    private RuleChecker checker;

    /**
     * Constructor.
     */
    public ProofChecker2Impl() {
        supported = new VersionSet();
        supported.add("0.01.00");
        supported.add("0.02.00");
    }

    public LogicalCheckExceptionList checkRule(final Rule rule,
            final ModuleContext context, final RuleChecker checker,
            final ReferenceResolver resolver) {
        exceptions = new LogicalCheckExceptionList();
        final RuleKey ruleKey = new RuleKey(rule.getName(), rule.getVersion());
        if (rule.getVersion() == null || !supported.contains(rule.getVersion())) {
            final ProofCheckException ex = new ProofCheckException(
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_CODE,
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT + ruleKey
                + BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT2 + supported,
                context);
            exceptions.add(ex);
        }
        return exceptions;
    }

    public LogicalCheckExceptionList checkProof(final Element formula,
            final FormalProofLineList proof,
            final RuleChecker checker,
            final ModuleContext moduleContext,
            final ReferenceResolver resolver) {
        final DefaultElementList con = new DefaultElementList(
            Operators.CONJUNCTION_OPERATOR);
        // we have no conditions, so we add an empty condition
        return checkProof(con, formula, proof, checker, moduleContext, resolver);
    }

    private LogicalCheckExceptionList checkProof(final ElementList conditions, final Element formula,
            final FormalProofLineList proof,
            final RuleChecker checker,
            final ModuleContext moduleContext,
            final ReferenceResolver resolver) {
        this.conditions = conditions;
        this.proof = proof;
        this.checker = checker;
        this.resolver = resolver;
        this.moduleContext = moduleContext;
        // use copy constructor for changing context
        currentContext = new ModuleContext(moduleContext);
        exceptions = new LogicalCheckExceptionList();
        final String context = moduleContext.getLocationWithinModule();
        lineProved = new boolean[proof.size()];
        label2line = new HashMap();
        for (int i = 0; i < proof.size(); i++) {
            boolean ok = true;
            setLocationWithinModule(context + ".get(" + i + ")");
            final FormalProofLine line = proof.get(i);
            if (line == null || line.getFormula() == null
                    || line.getFormula().getElement() == null) {
                ok = false;
                handleProofCheckException(
                    BasicProofErrors.PROOF_LINE_MUST_NOT_BE_NULL_CODE,
                    BasicProofErrors.PROOF_LINE_MUST_NOT_BE_NULL_TEXT,
                    getCurrentContext());
                continue;
            }
            setLocationWithinModule(context + ".get(" + i + ").getReason()");
            final Reason reason = line.getReason();
            if (reason == null) {
                ok = false;
                handleProofCheckException(
                    BasicProofErrors.REASON_MUST_NOT_BE_NULL_CODE,
                    BasicProofErrors.REASON_MUST_NOT_BE_NULL_TEXT,
                    getCurrentContext());
                continue;
            }
            if (line.getLabel() != null && line.getLabel().length() > 0) {
                setLocationWithinModule(context + ".get(" + i + ").getLabel()");
                addLocalLineLabel(i, line.getLabel());
            }

            // check if the formula together with the conditions is well formed
            if (hasConditions()) {
                setLocationWithinModule(context + ".get(" + i + ").getFormula.getElement()");
                ElementList full = new DefaultElementList(Operators.IMPLICATION_OPERATOR);
                if (conditions.size() > 1) {
                    full.add(conditions);
                } else {
                    full.add(conditions.getElement(0));
                }
                full.add(line.getFormula().getElement());
                FormulaChecker checkWf = new FormulaCheckerImpl();  // TODO 20110612 m31: use factory?
                final LogicalCheckExceptionList list = checkWf.checkFormula(full, getCurrentContext());
                if (list.size() > 0) {
                    ok = false;
                    handleProofCheckException(
                        BasicProofErrors.CONDITIONS_AND_FORMULA_DONT_AGREE_CODE,
                        BasicProofErrors.CONDITIONS_AND_FORMULA_DONT_AGREE_TEXT
                        + list.get(0).getMessage(),
                        getCurrentContext());
                    continue;
                }
            }

            // check if only defined rules are used
            // TODO 20110316 m31: this is a dirty trick to get the context of the reason
            //                    perhaps we can solve this more elegantly?
            String getReason = ".get" + StringUtility.getClassName(reason.getClass());
            if (getReason.endsWith("Vo")) {
                getReason = getReason.substring(0, getReason.length() - 2) + "()";
                setLocationWithinModule(context + ".get(" + i + ").getReason()"
                    + getReason);
//                System.out.println(getCurrentContext());
            }
            if (reason instanceof Add) {
                ok = check((Add) reason, i, line.getFormula().getElement());
            } else if (reason instanceof Rename) {
                ok = check((Rename) reason, i, line.getFormula().getElement());
            } else if (reason instanceof ModusPonens) {
                ok = check((ModusPonens) reason, i, line.getFormula().getElement());
            } else if (reason instanceof SubstFree) {
                ok = check((SubstFree) reason, i, line.getFormula().getElement());
            } else if (reason instanceof SubstPred) {
                ok = check((SubstPred) reason, i, line.getFormula().getElement());
            } else if (reason instanceof SubstFunc) {
                ok = check((SubstFunc) reason, i, line.getFormula().getElement());
            } else if (reason instanceof Universal) {
                ok = check((Universal) reason, i, line.getFormula().getElement());
            } else if (reason instanceof Existential) {
                ok = check((Existential) reason, i, line.getFormula().getElement());
            } else if (reason instanceof ConditionalProof) {
                setLocationWithinModule(context + ".get(" + i + ")");
                ok = check((ConditionalProof) reason, i, line.getFormula().getElement());
            } else {
                ok = false;
                handleProofCheckException(
                    BasicProofErrors.THIS_IS_NO_ALLOWED_BASIC_REASON_CODE,
                    BasicProofErrors.THIS_IS_NO_ALLOWED_BASIC_REASON_TEXT
                    + reason.getName(),
                    getCurrentContext());
            }
            lineProved[i] = ok;
            // check if last proof line is identical with proposition formula
            if (i == proof.size() - 1) {
                if (!formula.equals(line.getFormula().getElement())) {
                    handleProofCheckException(
                        BasicProofErrors.LAST_PROOF_LINE_MUST_BE_IDENTICAL_TO_PROPOSITION_CODE,
                        BasicProofErrors.LAST_PROOF_LINE_MUST_BE_IDENTICAL_TO_PROPOSITION_TEXT,
                        getModuleContextOfProofLineFormula(i));
                }
            }
        }
        return exceptions;
    }

    private void addLocalLineLabel(final int i, final String label) {
        if (label != null && label.length() > 0) {
            final Integer n = (Integer) label2line.get(label);
            if (n != null) {
                final ModuleContext lc = new ModuleContext(moduleContext.getModuleLocation(),
                    moduleContext.getLocationWithinModule() + ".get("
                    + label2line.get(label)
                    + ").getLabel()");
                handleProofCheckException(
                    BasicProofErrors.LOCAL_LABEL_ALREADY_EXISTS_CODE,
                    BasicProofErrors.LOCAL_LABEL_ALREADY_EXISTS_TEXT
                    + label,
                    getCurrentContext(),
                    lc);
            } else {
                if (isLocalProofLineReference(label)) {
                    handleProofCheckException(
                        BasicProofErrors.LOCAL_LABEL_ALREADY_EXISTS_CODE,
                        BasicProofErrors.LOCAL_LABEL_ALREADY_EXISTS_TEXT
                        + label,
                        getCurrentContext(),
                        resolver.getReferenceContext(label));
//                    System.out.println("we hava label already: " + label);
//                    ProofFinderUtility.println(getNormalizedLocalProofLineReference(label));
                }
            }
//            System.out.println("adding label: " + label);
            label2line.put(label, new Integer(i));
        }
    }

    private boolean check(final Add add, final int i, final Element element) {
        final String context = currentContext.getLocationWithinModule();
        boolean ok = true;
        if (add.getReference() == null) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.REFERENCE_TO_PROVED_FORMULA_IS_MISSING_CODE,
                BasicProofErrors.REFERENCE_TO_PROVED_FORMULA_IS_MISSING_TEXT,
                getCurrentContext());
            return ok;
        }
        if (!resolver.isProvedFormula(add.getReference())) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_CODE,
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_TEXT
                + add.getReference(),
                getCurrentContext());
            return ok;
        }
        final Element expected = resolver.getNormalizedReferenceFormula(add.getReference());
        final Element current = resolver.getNormalizedFormula(element);
        if (!EqualsUtility.equals(expected, current)) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.EXPECTED_FORMULA_DIFFERS_CODE,
                BasicProofErrors.EXPECTED_FORMULA_DIFFERS_TEXT
                + add.getReference(),
                getDiffModuleContextOfProofLineFormula(i, expected));
            return ok;
        }
        final RuleKey defined = checker.getRule(add.getName());
        if (defined == null) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_WAS_NOT_DEFINED_YET_CODE,
                BasicProofErrors.PROOF_METHOD_WAS_NOT_DEFINED_YET_TEXT
                + add.getName(),
                getCurrentContext());
            return ok;
        } else if (!supported.contains(defined.getVersion())) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_CODE,
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT + defined.getVersion()
                + BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT2 + supported,
                getCurrentContext());
            return ok;
        } else if (hasConditions() && !Version.equals("0.02.00", defined.getVersion())) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.HIGHER_PROOF_RULE_VERSION_NEEDED_CODE,
                BasicProofErrors.HIGHER_PROOF_RULE_VERSION_NEEDED_TEXT + "0.02.00"
                + BasicProofErrors.HIGHER_PROOF_RULE_VERSION_NEEDED_TEXT2 + defined.getVersion(),
                getCurrentContext());
            return ok;
        }
        return ok;
    }

    private boolean check(final Rename rename, final int i, final Element element) {
        final String context = currentContext.getLocationWithinModule();
        boolean ok = true;
        final Element f = getNormalizedLocalProofLineReference(rename.getReference());
        if (f == null) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_CODE,
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_TEXT
                + rename.getReference(),
                getCurrentContext());
        } else {
            final Element expected = FormulaUtility.replaceSubjectVariableQuantifier(
                rename.getOriginalSubjectVariable(),
                rename.getReplacementSubjectVariable(), f, rename.getOccurrence(),
                new Enumerator());
            final Element current = resolver.getNormalizedFormula(element);
            if (!EqualsUtility.equals(expected, current)) {
                ok = false;
                handleProofCheckException(
                    BasicProofErrors.EXPECTED_FORMULA_DIFFERS_CODE,
                    BasicProofErrors.EXPECTED_FORMULA_DIFFERS_TEXT
                    + rename.getReference(),
                    getDiffModuleContextOfProofLineFormula(i, expected));
            } else {
                ok = true;
            }
        }
        final RuleKey defined = checker.getRule(rename.getName());
        if (defined == null) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_WAS_NOT_DEFINED_YET_CODE,
                BasicProofErrors.PROOF_METHOD_WAS_NOT_DEFINED_YET_TEXT
                + rename.getName(),
                getCurrentContext());
            return ok;
        } else if (!supported.contains(defined.getVersion())) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_CODE,
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT + defined.getVersion()
                + BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT2 + supported,
                getCurrentContext());
            return ok;
        } else if (hasConditions() && !Version.equals("0.02.00", defined.getVersion())) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.HIGHER_PROOF_RULE_VERSION_NEEDED_CODE,
                BasicProofErrors.HIGHER_PROOF_RULE_VERSION_NEEDED_TEXT + "0.02.00"
                + BasicProofErrors.HIGHER_PROOF_RULE_VERSION_NEEDED_TEXT2 + defined.getVersion(),
                getCurrentContext());
            return ok;
        }
        return ok;
    }

    private boolean check(final SubstFree substFree, final int i, final Element element) {
        final String context = currentContext.getLocationWithinModule();
        boolean ok = true;
        final Element f = getNormalizedLocalProofLineReference(substFree.getReference());
        if (f == null) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_CODE,
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_TEXT
                + substFree.getReference(),
                getCurrentContext());
        } else {
            final Element current = resolver.getNormalizedFormula(element);
            final Element expected = f.replace(substFree.getSubjectVariable(),
                resolver.getNormalizedFormula(substFree.getSubstituteTerm()));
            if (!EqualsUtility.equals(current, expected)) {
                ok = false;
                handleProofCheckException(
                    BasicProofErrors.EXPECTED_FORMULA_DIFFERS_CODE,
                    BasicProofErrors.EXPECTED_FORMULA_DIFFERS_TEXT
                    + substFree.getReference(),
                    getDiffModuleContextOfProofLineFormula(i, expected));
                return ok;
            }
        }
        // check precondition: subject variable doesn't occur in a precondition
        if (FormulaUtility.containsOperatorVariable(conditions, substFree.getSubjectVariable())) {
            ok = false;
            setLocationWithinModule(context + ".getSubstituteFormula()");
            handleProofCheckException(
                BasicProofErrors.SUBSTITUTION_OPERATOR_FOUND_IN_PRECONDITION_CODE,
                BasicProofErrors.SUBSTITUTION_OPERATOR_FOUND_IN_PRECONDITION_TEXT,
                getCurrentContext());
            return ok;
        }
        final RuleKey defined = checker.getRule(substFree.getName());
        if (defined == null) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_WAS_NOT_DEFINED_YET_CODE,
                BasicProofErrors.PROOF_METHOD_WAS_NOT_DEFINED_YET_TEXT
                + substFree.getName(),
                getCurrentContext());
            return ok;
        } else if (!supported.contains(defined.getVersion())) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_CODE,
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT + defined.getVersion()
                + BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT2 + supported,
                getCurrentContext());
            return ok;
        } else if (hasConditions() && !Version.equals("0.02.00", defined.getVersion())) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.HIGHER_PROOF_RULE_VERSION_NEEDED_CODE,
                BasicProofErrors.HIGHER_PROOF_RULE_VERSION_NEEDED_TEXT + "0.02.00"
                + BasicProofErrors.HIGHER_PROOF_RULE_VERSION_NEEDED_TEXT2 + defined.getVersion(),
                getCurrentContext());
            return ok;
        }
        return ok;
    }

    private boolean check(final SubstPred substPred, final int i, final Element element) {
        final String context = currentContext.getLocationWithinModule();
        boolean ok = true;
        final Element alpha = getNormalizedLocalProofLineReference(substPred.getReference());
        if (alpha == null) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_CODE,
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_TEXT
                + substPred.getReference(),
                getCurrentContext());
            return ok;
        }
        final Element current = resolver.getNormalizedFormula(element);
        if (substPred.getSubstituteFormula() == null) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.SUBSTITUTION_FORMULA_IS_MISSING_CODE,
                BasicProofErrors.SUBSTITUTION_FORMULA_IS_MISSING_TEXT,
                getCurrentContext());
            return ok;
        }
        final Element p = resolver.getNormalizedFormula(substPred.getPredicateVariable());
        final Element beta = resolver.getNormalizedFormula(substPred.getSubstituteFormula());
        final Element expected = FormulaUtility.replaceOperatorVariable(alpha, p, beta);
        if (!EqualsUtility.equals(current, expected)) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.EXPECTED_FORMULA_DIFFERS_CODE,
                BasicProofErrors.EXPECTED_FORMULA_DIFFERS_TEXT
                + substPred.getReference(),
                getDiffModuleContextOfProofLineFormula(i, expected));
            return ok;
        }
        // check precondition: predicate variable p must have n pairwise different free subject
        // variables as arguments
        final ElementSet predFree = FormulaUtility.getFreeSubjectVariables(p);
        if (predFree.size() != p.getList().size() - 1) {
            ok = false;
            setLocationWithinModule(context + ".getPredicateVariable()");
            handleProofCheckException(
                BasicProofErrors.ONLY_FREE_SUBJECT_VARIABLES_ALLOWED_CODE,
                BasicProofErrors.ONLY_FREE_SUBJECT_VARIABLES_ALLOWED_TEXT,
                getDiffModuleContextOfProofLineFormula(i, expected));
            return ok;
        }
        for (int j = 1; j < p.getList().size(); j++) {
            if (!FormulaUtility.isSubjectVariable(p.getList().getElement(j))) {
                ok = false;
                setLocationWithinModule(context + ".getPredicateVariable()");
                handleProofCheckException(
                    BasicProofErrors.ONLY_FREE_SUBJECT_VARIABLES_ALLOWED_CODE,
                    BasicProofErrors.ONLY_FREE_SUBJECT_VARIABLES_ALLOWED_TEXT,
                    getCurrentContext());
                return ok;
            }
        }
        // check precondition: the free variables of $\beta(x_1, \ldots, x_n)$ without
        // $x_1$, \ldots, $x_n$ do not occur as bound variables in $\alpha$
        final ElementSet fBound = FormulaUtility.getBoundSubjectVariables(alpha);
        final ElementSet betaFree = FormulaUtility.getFreeSubjectVariables(beta);
        if (!fBound.intersection(betaFree.minus(predFree)).isEmpty()) {
            ok = false;
            setLocationWithinModule(context + ".getSubstituteFormula()");
            handleProofCheckException(
                BasicProofErrors.FREE_SUBJECT_VARIABLES_SHOULD_NOT_GET_BOUND_CODE,
                BasicProofErrors.FREE_SUBJECT_VARIABLES_SHOULD_NOT_GET_BOUND_TEXT,
                getCurrentContext());
            return ok;
        }
        // check precondition: each occurrence of $p(t_1, \ldots, t_n)$ in $\alpha$ contains
        // no bound variable of $\beta(x_1, \ldots, x_n)$
        final ElementSet betaBound = FormulaUtility.getBoundSubjectVariables(beta);
        if (!FormulaUtility.testOperatorVariable(alpha, p, betaBound)) {
            ok = false;
            setLocationWithinModule(context + ".getSubstituteFormula()");
            handleProofCheckException(
                BasicProofErrors.SUBSTITUTION_LOCATION_CONTAINS_BOUND_SUBJECT_VARIABLE_CODE,
                BasicProofErrors.SUBSTITUTION_LOCATION_CONTAINS_BOUND_SUBJECT_VARIABLE_TEXT,
                getCurrentContext());
            return ok;
        }
        // check precondition: $\sigma(...)$ dosn't occur in a precondition
        if (FormulaUtility.containsOperatorVariable(conditions, p)) {
            ok = false;
            setLocationWithinModule(context + ".getPredicateVariable()");
            handleProofCheckException(
                BasicProofErrors.SUBSTITUTION_OPERATOR_FOUND_IN_PRECONDITION_CODE,
                BasicProofErrors.SUBSTITUTION_OPERATOR_FOUND_IN_PRECONDITION_TEXT,
                getCurrentContext());
            return ok;
        }
        // check precondition: resulting formula is well formed was already done by well formed
        // checker

        final RuleKey defined = checker.getRule(substPred.getName());
        if (defined == null) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_WAS_NOT_DEFINED_YET_CODE,
                BasicProofErrors.PROOF_METHOD_WAS_NOT_DEFINED_YET_TEXT
                + substPred.getName(),
                getCurrentContext());
            return ok;
        } else if (!supported.contains(defined.getVersion())) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_CODE,
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT + defined.getVersion()
                + BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT2 + supported,
                getCurrentContext());
            return ok;
        } else if (hasConditions() && !Version.equals("0.02.00", defined.getVersion())) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.HIGHER_PROOF_RULE_VERSION_NEEDED_CODE,
                BasicProofErrors.HIGHER_PROOF_RULE_VERSION_NEEDED_TEXT + "0.02.00"
                + BasicProofErrors.HIGHER_PROOF_RULE_VERSION_NEEDED_TEXT2 + defined.getVersion(),
                getCurrentContext());
            return ok;
        }
        return ok;
    }

    private boolean check(final SubstFunc substFunc, final int i, final Element element) {
        final String context = currentContext.getLocationWithinModule();
        boolean ok = true;
        final Element alpha = getNormalizedLocalProofLineReference(substFunc.getReference());
        if (alpha == null) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_CODE,
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_TEXT
                + substFunc.getReference(),
                getCurrentContext());
            return ok;
        }
        final Element current = resolver.getNormalizedFormula(element);
        if (substFunc.getSubstituteTerm() == null) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.SUBSTITUTION_FORMULA_IS_MISSING_CODE,
                BasicProofErrors.SUBSTITUTION_FORMULA_IS_MISSING_TEXT,
                getCurrentContext());
            return ok;
        }
        final Element sigma = resolver.getNormalizedFormula(substFunc.getFunctionVariable());
        final Element tau = resolver.getNormalizedFormula(substFunc.getSubstituteTerm());
        final Element expected = FormulaUtility.replaceOperatorVariable(alpha, sigma, tau);
        if (!EqualsUtility.equals(current, expected)) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.EXPECTED_FORMULA_DIFFERS_CODE,
                BasicProofErrors.EXPECTED_FORMULA_DIFFERS_TEXT
                + substFunc.getReference(),
                getDiffModuleContextOfProofLineFormula(i, expected));
            return ok;
        }
        // check precondition: function variable $\sigma$ must have n pairwise different free
        // subject variables as arguments
        final ElementSet funcFree = FormulaUtility.getFreeSubjectVariables(sigma);
        if (funcFree.size() != sigma.getList().size() - 1) {
            ok = false;
            setLocationWithinModule(context + ".getPredicateVariable()");
            handleProofCheckException(
                BasicProofErrors.ONLY_FREE_SUBJECT_VARIABLES_ALLOWED_CODE,
                BasicProofErrors.ONLY_FREE_SUBJECT_VARIABLES_ALLOWED_TEXT,
                getDiffModuleContextOfProofLineFormula(i, expected));
            return ok;
        }
        for (int j = 1; j < sigma.getList().size(); j++) {
            if (!FormulaUtility.isSubjectVariable(sigma.getList().getElement(j))) {
                ok = false;
                setLocationWithinModule(context + ".getPredicateVariable()");
                handleProofCheckException(
                    BasicProofErrors.ONLY_FREE_SUBJECT_VARIABLES_ALLOWED_CODE,
                    BasicProofErrors.ONLY_FREE_SUBJECT_VARIABLES_ALLOWED_TEXT,
                    getCurrentContext());
                return ok;
            }
        }
        // check precondition: the free variables of $\tau(x_1, \ldots, x_n)$
        // without $x_1$, \ldots, $x_n$ do not occur as bound variables in $\alpha$
        final ElementSet fBound = FormulaUtility.getBoundSubjectVariables(alpha);
        final ElementSet sigmaFree = FormulaUtility.getFreeSubjectVariables(tau);
        if (!fBound.intersection(sigmaFree.minus(funcFree)).isEmpty()) {
            ok = false;
            setLocationWithinModule(context + ".getSubstituteFormula()");
            handleProofCheckException(
                BasicProofErrors.FREE_SUBJECT_VARIABLES_SHOULD_NOT_GET_BOUND_CODE,
                BasicProofErrors.FREE_SUBJECT_VARIABLES_SHOULD_NOT_GET_BOUND_TEXT,
                getCurrentContext());
            return ok;
        }
        // check precondition: each occurrence of $\sigma(t_1, \ldots, t_n)$ in $\alpha$
        // contains no bound variable of $\tau(x_1, \ldots, x_n)$
        final ElementSet sigmaBound = FormulaUtility.getBoundSubjectVariables(tau);
        if (!FormulaUtility.testOperatorVariable(alpha, sigma, sigmaBound)) {
            ok = false;
            setLocationWithinModule(context + ".getSubstituteFormula()");
            handleProofCheckException(
                BasicProofErrors.SUBSTITUTION_LOCATION_CONTAINS_BOUND_SUBJECT_VARIABLE_CODE,
                BasicProofErrors.SUBSTITUTION_LOCATION_CONTAINS_BOUND_SUBJECT_VARIABLE_TEXT,
                getCurrentContext());
            return ok;
        }
        // check precondition: $\sigma(...)$ dosn't occur in a precondition
        if (FormulaUtility.containsOperatorVariable(conditions, sigma)) {
            ok = false;
            setLocationWithinModule(context + ".getPredicateVariable()");
            handleProofCheckException(
                BasicProofErrors.SUBSTITUTION_OPERATOR_FOUND_IN_PRECONDITION_CODE,
                BasicProofErrors.SUBSTITUTION_OPERATOR_FOUND_IN_PRECONDITION_TEXT,
                getCurrentContext());
            return ok;
        }
        // check precondition: resulting formula is well formed was already done by well formed
        // checker

        final RuleKey defined = checker.getRule(substFunc.getName());
        if (defined == null) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_WAS_NOT_DEFINED_YET_CODE,
                BasicProofErrors.PROOF_METHOD_WAS_NOT_DEFINED_YET_TEXT
                + substFunc.getName(),
                getCurrentContext());
            return ok;
        } else if (!supported.contains(defined.getVersion())) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_CODE,
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT + defined.getVersion()
                + BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT2 + supported,
                getCurrentContext());
            return ok;
        } else if (hasConditions() && !Version.equals("0.02.00", defined.getVersion())) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.HIGHER_PROOF_RULE_VERSION_NEEDED_CODE,
                BasicProofErrors.HIGHER_PROOF_RULE_VERSION_NEEDED_TEXT + "0.02.00"
                + BasicProofErrors.HIGHER_PROOF_RULE_VERSION_NEEDED_TEXT2 + defined.getVersion(),
                getCurrentContext());
            return ok;
        }
        return ok;
    }

    private boolean check(final ModusPonens mp, final int i, final Element element) {
        final String context = currentContext.getLocationWithinModule();
        boolean ok = true;
        final Element f1 = getNormalizedLocalProofLineReference(mp.getReference1());
        if (f1 == null) {
            ok = false;
            setLocationWithinModule(context + ".getReference1()");
            handleProofCheckException(
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_CODE,
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_TEXT
                + mp.getReference1(),
                getCurrentContext());
        }
        final Element f2 = getNormalizedLocalProofLineReference(mp.getReference2());
        if (f2 == null) {
            ok = false;
            setLocationWithinModule(context + ".getReference2()");
            handleProofCheckException(
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_CODE,
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_TEXT
                + mp.getReference2(),
                getCurrentContext());
        }
        if (ok) {
            final Element current = getNormalizedFormula(element);
            if (!FormulaUtility.isImplication(f1)) {
                ok = false;
                setLocationWithinModule(context + ".getReference1()");
                handleProofCheckException(
                    BasicProofErrors.IMPLICATION_EXPECTED_CODE,
                    BasicProofErrors.IMPLICATION_EXPECTED_TEXT
                    + mp.getReference1(),
                    getCurrentContext());
            } else if (!f2.equals(f1.getList().getElement(0))) {
                ok = false;
                setLocationWithinModule(context + ".getReference2()");
                handleProofCheckException(
                    BasicProofErrors.MUST_BE_HYPOTHESIS_OF_FIRST_REFERENCE_CODE,
                    BasicProofErrors.MUST_BE_HYPOTHESIS_OF_FIRST_REFERENCE_TEXT
                    + mp.getReference2(),
                    getCurrentContext(),
                    resolver.getReferenceContext(mp.getReference1()));
            } else if (!current.equals(f1.getList().getElement(1))) {
                ok = false;
                setLocationWithinModule(context + ".getReference1()");
                handleProofCheckException(
                    BasicProofErrors.CURRENT_MUST_BE_CONCLUSION_CODE,
                    BasicProofErrors.CURRENT_MUST_BE_CONCLUSION_TEXT
                    + mp.getReference1(),
                    getCurrentContext(),
                    resolver.getReferenceContext(mp.getReference1()));
            } else {
                ok = true;
            }
        }
        final RuleKey defined = checker.getRule(mp.getName());
        if (defined == null) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_WAS_NOT_DEFINED_YET_CODE,
                BasicProofErrors.PROOF_METHOD_WAS_NOT_DEFINED_YET_TEXT
                + mp.getName(),
                getCurrentContext());
            return ok;
        } else if (!supported.contains(defined.getVersion())) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_CODE,
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT + defined.getVersion()
                + BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT2 + supported,
                getCurrentContext());
            return ok;
        } else if (hasConditions() && !Version.equals("0.02.00", defined.getVersion())) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.HIGHER_PROOF_RULE_VERSION_NEEDED_CODE,
                BasicProofErrors.HIGHER_PROOF_RULE_VERSION_NEEDED_TEXT + "0.02.00"
                + BasicProofErrors.HIGHER_PROOF_RULE_VERSION_NEEDED_TEXT2 + defined.getVersion(),
                getCurrentContext());
            return ok;
        }
        return ok;
    }

    private boolean check(final Universal universal, final int i, final Element element) {
        final String context = currentContext.getLocationWithinModule();
        boolean ok = true;
        final Element reference = getNormalizedLocalProofLineReference(universal.getReference());
        if (reference == null) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_CODE,
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_TEXT
                + universal.getReference(),
                getCurrentContext());
//        } else if (!lineProved[n.intValue()]) {
//            ok = false;
//            setLocationWithinModule(context + ".getReference()");
//            handleProofCheckException(
//                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_CODE,
//                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_TEXT
//                + universal.getReference(),
//                getCurrentContext());
        } else {
            final Element current = getNormalizedFormula(element);
            if (!FormulaUtility.isImplication(current)) {
                ok = false;
                setLocationWithinModule(context + ".getReference()");
                handleProofCheckException(
                    BasicProofErrors.IMPLICATION_EXPECTED_CODE,
                    BasicProofErrors.IMPLICATION_EXPECTED_TEXT
                    + universal.getReference(),
                    getCurrentContext());
                return ok;
            }
            if (!FormulaUtility.isSubjectVariable(universal.getSubjectVariable())) {
                ok = false;
                setLocationWithinModule(context + ".getSubjectVariable()");
                handleProofCheckException(
                    BasicProofErrors.SUBJECT_VARIABLE_IS_MISSING_CODE,
                    BasicProofErrors.SUBJECT_VARIABLE_IS_MISSING_TEXT,
                    getCurrentContext());
                return ok;
            }
            final DefaultElementList expected = new DefaultElementList(Operators.IMPLICATION_OPERATOR);
            expected.add(reference.getList().getElement(0));
            final ElementList uni = new DefaultElementList(Operators.UNIVERSAL_QUANTIFIER_OPERATOR);
            uni.add(universal.getSubjectVariable());
            uni.add(reference.getList().getElement(1));
            expected.add(uni);
//            System.out.print("Expected: ");
//            ProofFinderUtility.println(expected);
//            System.out.print("Current : ");
//            ProofFinderUtility.println(current);
            if (!EqualsUtility.equals(current, expected)) {
                ok = false;
                handleProofCheckException(
                    BasicProofErrors.EXPECTED_FORMULA_DIFFERS_CODE,
                    BasicProofErrors.EXPECTED_FORMULA_DIFFERS_TEXT
                    + universal.getReference(),
                    getDiffModuleContextOfProofLineFormula(i, expected));
                return ok;
            }
        }
        final RuleKey defined = checker.getRule(universal.getName());
        if (defined == null) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_WAS_NOT_DEFINED_YET_CODE,
                BasicProofErrors.PROOF_METHOD_WAS_NOT_DEFINED_YET_TEXT
                + universal.getName(),
                getCurrentContext());
            return ok;
        } else if (!supported.contains(defined.getVersion())) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_CODE,
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT + defined.getVersion()
                + BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT2 + supported,
                getCurrentContext());
            return ok;
        } else if (hasConditions() && !Version.equals("0.02.00", defined.getVersion())) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.HIGHER_PROOF_RULE_VERSION_NEEDED_CODE,
                BasicProofErrors.HIGHER_PROOF_RULE_VERSION_NEEDED_TEXT + "0.02.00"
                + BasicProofErrors.HIGHER_PROOF_RULE_VERSION_NEEDED_TEXT2 + defined.getVersion(),
                getCurrentContext());
            return ok;
        }
        return ok;
    }

    private boolean check(final Existential existential, final int i, final Element element) {
        final String context = currentContext.getLocationWithinModule();
        boolean ok = true;
        final Element reference = getNormalizedLocalProofLineReference(existential.getReference());
        if (reference == null) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_CODE,
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_TEXT
                + existential.getReference(),
                getCurrentContext());
//        } else if (!lineProved[n.intValue()]) {
//            ok = false;
//            setLocationWithinModule(context + ".getReference()");
//            handleProofCheckException(
//                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_CODE,
//                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_TEXT
//                + existential.getReference(),
//                getCurrentContext());
        } else {
            final Element current = getNormalizedFormula(element);
            if (!FormulaUtility.isImplication(current)) {
                ok = false;
                setLocationWithinModule(context + ".getReference()");
                handleProofCheckException(
                    BasicProofErrors.IMPLICATION_EXPECTED_CODE,
                    BasicProofErrors.IMPLICATION_EXPECTED_TEXT
                    + existential.getReference(),
                    getCurrentContext());
                return ok;
            }
            if (!FormulaUtility.isSubjectVariable(existential.getSubjectVariable())) {
                ok = false;
                setLocationWithinModule(context + ".getSubjectVariable()");
                handleProofCheckException(
                    BasicProofErrors.SUBJECT_VARIABLE_IS_MISSING_CODE,
                    BasicProofErrors.SUBJECT_VARIABLE_IS_MISSING_TEXT,
                    getCurrentContext());
                return ok;
            }
            final DefaultElementList expected = new DefaultElementList(Operators.IMPLICATION_OPERATOR);
            final ElementList exi = new DefaultElementList(
                Operators.EXISTENTIAL_QUANTIFIER_OPERATOR);
            exi.add(existential.getSubjectVariable());
            exi.add(reference.getList().getElement(0));
            expected.add(exi);
            expected.add((reference.getList().getElement(1)));
            if (!EqualsUtility.equals(current, expected)) {
                ok = false;
                handleProofCheckException(
                    BasicProofErrors.EXPECTED_FORMULA_DIFFERS_CODE,
                    BasicProofErrors.EXPECTED_FORMULA_DIFFERS_TEXT
                    + existential.getReference(),
                    getDiffModuleContextOfProofLineFormula(i, expected));
                return ok;
            }
        }
        final RuleKey defined = checker.getRule(existential.getName());
        if (defined == null) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_WAS_NOT_DEFINED_YET_CODE,
                BasicProofErrors.PROOF_METHOD_WAS_NOT_DEFINED_YET_TEXT
                + existential.getName(),
                getCurrentContext());
            return ok;
        } else if (!supported.contains(defined.getVersion())) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_CODE,
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT + defined.getVersion()
                + BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT2 + supported,
                getCurrentContext());
            return ok;
        } else if (hasConditions() && !Version.equals("0.02.00", defined.getVersion())) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.HIGHER_PROOF_RULE_VERSION_NEEDED_CODE,
                BasicProofErrors.HIGHER_PROOF_RULE_VERSION_NEEDED_TEXT + "0.02.00"
                + BasicProofErrors.HIGHER_PROOF_RULE_VERSION_NEEDED_TEXT2 + defined.getVersion(),
                getCurrentContext());
            return ok;
        }
        return ok;
    }

    private boolean check(final ConditionalProof cp, final int i, final Element element) {
        final ModuleAddress address = currentContext.getModuleLocation();
        final String context = currentContext.getLocationWithinModule();
//        System.out.println(getCurrentContext());
        boolean ok = true;
        if (cp.getHypothesis() == null || cp.getHypothesis().getFormula() == null
                || cp.getHypothesis().getFormula().getElement() == null) {
            ok = false;
            setLocationWithinModule(context + ".getHypothesis()");
            handleProofCheckException(
                BasicProofErrors.PROOF_LINE_MUST_NOT_BE_NULL_CODE,
                BasicProofErrors.PROOF_LINE_MUST_NOT_BE_NULL_TEXT,
                getCurrentContext());
            return ok;
        }
        if (cp.getFormalProofLineList() == null || cp.getFormalProofLineList().size() <= 0) {
            ok = false;
            setLocationWithinModule(context + ".getFormalProofLineList()");
            handleProofCheckException(
                BasicProofErrors.MISSING_PROOF_LINE_FOR_CONDITIONAL_PROOF_CODE,
                BasicProofErrors.MISSING_PROOF_LINE_FOR_CONDITIONAL_PROOF_TEXT,
                getCurrentContext());
            return ok;
        }
        final ReferenceResolver newResolver = new ReferenceResolver() {

            public Element getNormalizedFormula(final Element formula) {
                return ProofChecker2Impl.this.getNormalizedFormula(formula);
            }

            public Element getNormalizedReferenceFormula(final String reference) {
//                System.out.println("Looking for " + reference);
                if (EqualsUtility.equals(reference, cp.getHypothesis().getLabel())) {
                    return resolver.getNormalizedFormula(cp.getHypothesis().getFormula()
                        .getElement());
                }
                return ProofChecker2Impl.this.getNormalizedReferenceFormula(reference);
            }

            public boolean isProvedFormula(final String reference) {
                if (EqualsUtility.equals(reference, cp.getHypothesis().getLabel())) {
                    return true;
                }
                return ProofChecker2Impl.this.isProvedFormula(reference);
            }

            public boolean isLocalProofLineReference(final String reference) {
                if (EqualsUtility.equals(reference, cp.getHypothesis().getLabel())) {
                    return true;
                }
                return ProofChecker2Impl.this.isLocalProofLineReference(reference);
            }

            public ModuleContext getReferenceContext(final String reference) {
                if (EqualsUtility.equals(reference, cp.getHypothesis().getLabel())) {
                    return new ModuleContext(address, context
                        + ".getHypothesis().getLabel()");
                }
                return ProofChecker2Impl.this.getReferenceContext(reference);
            }

            public Element getNormalizedLocalProofLineReference(final String reference) {
//                System.out.println("\t resolver looks for " + reference);
                if (EqualsUtility.equals(reference, cp.getHypothesis().getLabel())) {
//                    System.out.println("\t resolver found local " + reference);
                    return resolver.getNormalizedFormula(
                        cp.getHypothesis().getFormula().getElement());
                }
                return ProofChecker2Impl.this.getNormalizedLocalProofLineReference(reference);
            }

        };
        final int last = cp.getFormalProofLineList().size() - 1;
        setLocationWithinModule(context + ".getFormalProofLineList().get(" + last + ")");
        final FormalProofLine line = cp.getFormalProofLineList().get(last);
        if (line == null || line.getFormula() == null
                || line.getFormula().getElement() == null) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_LINE_MUST_NOT_BE_NULL_CODE,
                BasicProofErrors.PROOF_LINE_MUST_NOT_BE_NULL_TEXT,
                getCurrentContext());
            return ok;
        }
        final Element lastFormula = resolver.getNormalizedFormula(line.getFormula().getElement());
        final ElementList newConditions = (ElementList) conditions.copy();
        // add hypothesis as new condition
        newConditions.add(cp.getHypothesis().getFormula().getElement());
        setLocationWithinModule(context + ".getFormalProofLineList()");
        final LogicalCheckExceptionList eList = (new ProofChecker2Impl()).checkProof(
            newConditions, lastFormula, cp.getFormalProofLineList(), checker, getCurrentContext(),
            newResolver);
        exceptions.add(eList);
        ok = eList.size() == 0;
        setLocationWithinModule(context + ".getConclusion()");
        if (cp.getConclusion() == null || cp.getConclusion().getFormula() == null
                || cp.getConclusion().getFormula().getElement() == null) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_LINE_MUST_NOT_BE_NULL_CODE,
                BasicProofErrors.PROOF_LINE_MUST_NOT_BE_NULL_TEXT,
                getCurrentContext());
            return ok;
        }
        final Element c = resolver.getNormalizedFormula(cp.getConclusion().getFormula().getElement());
        setLocationWithinModule(context + ".getConclusion().getFormula().getElement()");
        if (!FormulaUtility.isImplication(c)) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.IMPLICATION_EXPECTED_CODE,
                BasicProofErrors.IMPLICATION_EXPECTED_TEXT,
                getCurrentContext());
            return ok;
        }
        final DefaultElementList expected = new DefaultElementList(Operators.IMPLICATION_OPERATOR);
        expected.add(cp.getHypothesis().getFormula().getElement());
        expected.add(lastFormula);
//        System.out.println("expected: ");
//        ProofFinderUtility.println(cp.getConclusion().getFormula().getElement());
        if (!EqualsUtility.equals(cp.getConclusion().getFormula().getElement(), expected)) {
//            System.out.println("found: ");
//            ProofFinderUtility.println(cp.getConclusion().getFormula().getElement());
            ok = false;
            handleProofCheckException(
                BasicProofErrors.EXPECTED_FORMULA_DIFFERS_2_CODE,
                BasicProofErrors.EXPECTED_FORMULA_DIFFERS_2_TEXT,
                getDiffModuleContextOfProofLineFormula(i, expected));
            return ok;
        }
        final RuleKey defined = checker.getRule(cp.getName());
        if (defined == null) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_WAS_NOT_DEFINED_YET_CODE,
                BasicProofErrors.PROOF_METHOD_WAS_NOT_DEFINED_YET_TEXT
                + cp.getName(),
                getCurrentContext());
            return ok;
        } else if (!supported.contains(defined.getVersion())) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_CODE,
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT + defined.getVersion()
                + BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT2 + supported,
                getCurrentContext());
            return ok;
        } else if (hasConditions() && !Version.equals("0.02.00", defined.getVersion())) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.HIGHER_PROOF_RULE_VERSION_NEEDED_CODE,
                BasicProofErrors.HIGHER_PROOF_RULE_VERSION_NEEDED_TEXT + "0.02.00"
                + BasicProofErrors.HIGHER_PROOF_RULE_VERSION_NEEDED_TEXT2 + defined.getVersion(),
                getCurrentContext());
            return ok;
        }
        return ok;
    }

    private ModuleContext getModuleContextOfProofLineFormula(final int i) {
        if (proof.get(i) instanceof ConditionalProof) {
            return new ModuleContext(moduleContext.getModuleLocation(),
                    moduleContext.getLocationWithinModule() + ".get(" + i
                    + ").getConclusion().getFormula().getElement()");
        }
        return new ModuleContext(moduleContext.getModuleLocation(),
            moduleContext.getLocationWithinModule() + ".get(" + i
            + ").getFormula().getElement()");
    }

    private ModuleContext getDiffModuleContextOfProofLineFormula(final int i,
            final Element expected) {
        final String diff = FormulaUtility.getDifferenceLocation(
            proof.get(i).getFormula().getElement(),  expected);
        if (proof.get(i) instanceof ConditionalProof) {
            return new ModuleContext(moduleContext.getModuleLocation(),
                    moduleContext.getLocationWithinModule() + ".get(" + i
                    + ").getConclusion().getFormula().getElement()" + diff);
        }
        return new ModuleContext(moduleContext.getModuleLocation(),
            moduleContext.getLocationWithinModule() + ".get(" + i
            + ").getFormula().getElement()" + diff);
    }

    /**
     * Have we any conditions. If yes we are within an conditional proof argument.
     *
     * @return  Do we have conditions?
     */
    private boolean hasConditions() {
        return conditions.size() > 0;
    }

    private Element getNormalizedProofLine(final Integer n) {
        if (n == null) {
            return null;
        }
        int i = n.intValue();
        if (i < 0 || i >= proof.size()) {
            return null;
        }
        return resolver.getNormalizedFormula(proof.get(i).getFormula().getElement());
    }

    /**
     * Add new {@link ProofCheckException} to exception list.
     *
     * @param code      Error code.
     * @param msg       Error message.
     * @param context   Error context.
     */
    private void handleProofCheckException(final int code, final String msg,
            final ModuleContext context) {
//        System.out.println(context);
//        System.setProperty("qedeq.test.xmlLocationFailures", "true");
        final ProofCheckException ex = new ProofCheckException(code, msg, context);
        exceptions.add(ex);
    }

    /**
     * Add new {@link ProofCheckException} to exception list.
     *
     * @param code              Error code.
     * @param msg               Error message.
     * @param context           Error context.
     * @param referenceContext  Reference context.
     */
    private void handleProofCheckException(final int code, final String msg,
            final ModuleContext context, final ModuleContext referenceContext) {
//        System.out.println(context);
//        System.setProperty("qedeq.test.xmlLocationFailures", "true");
        final ProofCheckException ex = new ProofCheckException(code, msg, null, context,
            referenceContext);
        exceptions.add(ex);
    }

    /**
     * Get current context within original.
     *
     * @return  Current context.
     */
    protected final ModuleContext getCurrentContext() {
        return currentContext;
    }

    /**
     * Set location information where are we within the original module.
     *
     * @param   locationWithinModule    Location within module.
     */
    protected void setLocationWithinModule(final String locationWithinModule) {
        getCurrentContext().setLocationWithinModule(locationWithinModule);
    }

    public boolean isProvedFormula(final String reference) {
        if (label2line.containsKey(reference)) {
            return lineProved[((Integer) label2line.get(reference)).intValue()];
        }
        return resolver.isProvedFormula(reference);
    }

    public Element getNormalizedReferenceFormula(final String reference) {
//        System.out.println("looking for " + reference);
        if (label2line.containsKey(reference)) {
//            System.out.println("found in local " + reference);
            return getNormalizedProofLine((Integer) label2line.get(reference));
        }
//        System.out.println("not found in local " + reference);
        return resolver.getNormalizedReferenceFormula(reference);
    }

    public Element getNormalizedFormula(final Element element) {
        return resolver.getNormalizedFormula(element);
    }

    public boolean isLocalProofLineReference(final String reference) {
        if (label2line.containsKey(reference)) {
            return true;
        }
        return resolver.isLocalProofLineReference(reference);
    }

    public Element getNormalizedLocalProofLineReference(final String reference) {
//        System.out.println("\t resolver looks for " + reference);
        if (label2line.containsKey(reference)) {
//            System.out.println("\t resolver found local " + reference);
            return getNormalizedProofLine((Integer) label2line.get(reference));
        }
//        System.out.println("\t resolver asks super resolver for " + reference);
        final Element result = resolver.getNormalizedLocalProofLineReference(reference);
//        if (result == null) {
//            System.out.println("\t super resolver didn't find " + reference);
//        } else {
//            System.out.println("\t super resolver found " + reference);
//        }
        return result;
    }

    public ModuleContext getReferenceContext(final String reference) {
        if (label2line.containsKey(reference)) {
            final ModuleContext lc = new ModuleContext(moduleContext.getModuleLocation(),
                moduleContext.getLocationWithinModule() + ".get("
                + label2line.get(reference)
                + ").getLabel()");
            return lc;
        }
        return resolver.getReferenceContext(reference);
    }

}
