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

package org.qedeq.kernel.bo.logic.proof.checker;

import java.util.HashMap;
import java.util.Map;

import org.qedeq.base.utility.Enumerator;
import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.base.utility.Version;
import org.qedeq.kernel.bo.logic.common.FormulaUtility;
import org.qedeq.kernel.bo.logic.common.LogicalCheckExceptionList;
import org.qedeq.kernel.bo.logic.common.Operators;
import org.qedeq.kernel.bo.logic.common.ReferenceResolver;
import org.qedeq.kernel.bo.logic.common.RuleKey;
import org.qedeq.kernel.bo.logic.proof.common.ProofChecker;
import org.qedeq.kernel.bo.logic.proof.common.RuleChecker;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.list.ElementList;
import org.qedeq.kernel.se.base.module.Add;
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
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.dto.list.DefaultElementList;
import org.qedeq.kernel.se.dto.list.ElementSet;


/**
 * Formal proof checker for basic rules.
 *
 * @author  Michael Meyling
 */
public class ProofChecker1Impl implements ProofChecker {

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

    /** Rule version we can check. */
    private final Version ruleVersion;

    /** Rule existence checker. */
    private RuleChecker checker;

    /**
     * Constructor.
     *
     */
    public ProofChecker1Impl() {
        this.ruleVersion = new Version("0.01.00");
    }

    public LogicalCheckExceptionList checkRule(final Rule rule,
            final ModuleContext context, final RuleChecker checker,
            final ReferenceResolver resolver) {
        exceptions = new LogicalCheckExceptionList();
        final RuleKey ruleKey = new RuleKey(rule.getName(), rule.getVersion());
        if (rule.getVersion() == null || !ruleVersion.equals(rule.getVersion())) {
            final ProofCheckException ex = new ProofCheckException(
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_CODE,
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT + ruleKey
                + BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT2 + "{" + ruleVersion + "}",
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
        this.proof = proof;
        this.resolver = resolver;
        this.moduleContext = moduleContext;
        this.checker = checker;
        // use copy constructor for changing context
        currentContext = new ModuleContext(moduleContext);
        exceptions = new LogicalCheckExceptionList();
        final String context = moduleContext.getLocationWithinModule();
        lineProved = new boolean[proof.size()];
        label2line = new HashMap();
        for (int i = 0; i < proof.size(); i++) {
            boolean ok = true;
            setLocationWithinModule(context + ".get("  + i + ")");
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
            setLocationWithinModule(context + ".get("  + i + ").getReason()");
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
                final Integer n = (Integer) label2line.get(line.getLabel());
                if (n != null) {
                    final ModuleContext lc = new ModuleContext(moduleContext.getModuleLocation(),
                        moduleContext.getLocationWithinModule() + ".get("
                        + ((Integer) label2line.get(line.getLabel()))
                        + ").getLabel()");
                    setLocationWithinModule(context + ".get("  + i + ").getLabel()");
                    handleProofCheckException(
                        BasicProofErrors.LOCAL_LABEL_ALREADY_EXISTS_CODE,
                        BasicProofErrors.LOCAL_LABEL_ALREADY_EXISTS_TEXT
                        + line.getLabel(),
                        getCurrentContext(),
                        lc);
                }
                label2line.put(line.getLabel(), new Integer(i));
            }
            // check if only basis rules are used
            // TODO 20110316 m31: this is a dirty trick to get the context of the reason
            //                    perhaps we can solve this more elegantly?
            String getReason = ".get" + StringUtility.getClassName(reason.getClass());
            if (getReason.endsWith("Vo")) {
                getReason = getReason.substring(0, getReason.length() - 2) + "()";
                setLocationWithinModule(context + ".get("  + i + ").getReason()"
                    + getReason);
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
                        BasicProofErrors.LAST_PROOF_LINE_MUST_BE_IDENTICAL_TO_PROPOSITION_TEXT
                        + reason.getName(),
                        getModuleContextOfProofLineFormula(i));
                }
            }
        }
        return exceptions;
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
        } else if (!ruleVersion.equals(defined.getVersion())) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_CODE,
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT + defined.getVersion(),
                getCurrentContext());
            return ok;
        }
        return ok;
    }

    private boolean check(final Rename rename, final int i, final Element element) {
        final String context = currentContext.getLocationWithinModule();
        boolean ok = true;
        final Integer n = (Integer) label2line.get(rename.getReference());
        if (n == null) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_CODE,
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_TEXT
                + rename.getReference(),
                getCurrentContext());
//        } else if (!lineProved[n.intValue()]) {
//            ok = false;
//            setLocationWithinModule(context + ".getReference()");
//            handleProofCheckException(
//                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_CODE,
//                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_TEXT
//                + rename.getReference(),
//                getCurrentContext());
        } else {
            final Element f = getNormalizedProofLine(n);
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
        } else if (!ruleVersion.equals(defined.getVersion())) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_CODE,
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT + defined.getVersion(),
                getCurrentContext());
            return ok;
        }
        return ok;
    }

    private boolean check(final SubstFree substFree, final int i, final Element element) {
        final String context = currentContext.getLocationWithinModule();
        boolean ok = true;
        final Integer n = (Integer) label2line.get(substFree.getReference());
        if (n == null) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_CODE,
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_TEXT
                + substFree.getReference(),
                getCurrentContext());
//        } else if (!lineProved[n.intValue()]) {
//            ok = false;
//            setLocationWithinModule(context + ".getReference()");
//            handleProofCheckException(
//                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_CODE,
//                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_TEXT
//                + substfree.getReference(),
//                getCurrentContext());
        } else {
            final Element f = getNormalizedProofLine(n);
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
        final RuleKey defined = checker.getRule(substFree.getName());
        if (defined == null) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_WAS_NOT_DEFINED_YET_CODE,
                BasicProofErrors.PROOF_METHOD_WAS_NOT_DEFINED_YET_TEXT
                + substFree.getName(),
                getCurrentContext());
            return ok;
        } else if (!ruleVersion.equals(defined.getVersion())) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_CODE,
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT + defined.getVersion(),
                getCurrentContext());
            return ok;
        }
        return ok;
    }

    private boolean check(final SubstPred substPred, final int i, final Element element) {
        final String context = currentContext.getLocationWithinModule();
        boolean ok = true;
        final Integer n = (Integer) label2line.get(substPred.getReference());
        if (n == null) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_CODE,
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_TEXT
                + substPred.getReference(),
                getCurrentContext());
//        } else if (!lineProved[n.intValue()]) {
//            ok = false;
//            setLocationWithinModule(context + ".getReference()");
//            handleProofCheckException(
//                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_CODE,
//                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_TEXT
//                + substpred.getReference(),
//                getCurrentContext());
        } else {
            final Element alpha = getNormalizedProofLine(n);
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
            // check precondition: resulting formula is well formed was already done by well formed
            // checker
        }
        final RuleKey defined = checker.getRule(substPred.getName());
        if (defined == null) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_WAS_NOT_DEFINED_YET_CODE,
                BasicProofErrors.PROOF_METHOD_WAS_NOT_DEFINED_YET_TEXT
                + substPred.getName(),
                getCurrentContext());
            return ok;
        } else if (!ruleVersion.equals(defined.getVersion())) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_CODE,
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT + defined.getVersion(),
                getCurrentContext());
            return ok;
        }
        return ok;
    }

    private boolean check(final SubstFunc substFunc, final int i, final Element element) {
        final String context = currentContext.getLocationWithinModule();
        boolean ok = true;
        final Integer n = (Integer) label2line.get(substFunc.getReference());
        if (n == null) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_CODE,
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_TEXT
                + substFunc.getReference(),
                getCurrentContext());
//        } else if (!lineProved[n.intValue()]) {
//            ok = false;
//            setLocationWithinModule(context + ".getReference()");
//            handleProofCheckException(
//                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_CODE,
//                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_TEXT
//                + substfunc.getReference(),
//                getCurrentContext());
        } else {
            final Element alpha = getNormalizedProofLine(n);
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
            // check precondition: resulting formula is well formed was already done by well formed
            // checker
        }
        final RuleKey defined = checker.getRule(substFunc.getName());
        if (defined == null) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_WAS_NOT_DEFINED_YET_CODE,
                BasicProofErrors.PROOF_METHOD_WAS_NOT_DEFINED_YET_TEXT
                + substFunc.getName(),
                getCurrentContext());
            return ok;
        } else if (!ruleVersion.equals(defined.getVersion())) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_CODE,
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT + defined.getVersion(),
                getCurrentContext());
            return ok;
        }
        return ok;
    }

    private boolean check(final ModusPonens mp, final int i, final Element element) {
        final String context = currentContext.getLocationWithinModule();
        boolean ok = true;
        final Integer n1 = (Integer) label2line.get(mp.getReference1());
        if (n1 == null) {
            ok = false;
            setLocationWithinModule(context + ".getReference1()");
            handleProofCheckException(
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_CODE,
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_TEXT
                + mp.getReference1(),
                getCurrentContext());
//        } else if (!lineProved[n1.intValue()]) {
//            ok = false;
//            setLocationWithinModule(context + ".getReference1()");
//            handleProofCheckException(
//                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_CODE,
//                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_TEXT
//                + mp.getReference1(),
//                getCurrentContext());
        }
        final Integer n2 = (Integer) label2line.get(mp.getReference2());
        if (n2 == null) {
            ok = false;
            setLocationWithinModule(context + ".getReference2()");
            handleProofCheckException(
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_CODE,
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_TEXT
                + mp.getReference2(),
                getCurrentContext());
//        } else if (!lineProved[n2.intValue()]) {
//            ok = false;
//            setLocationWithinModule(context + ".getReference2()");
//            handleProofCheckException(
//                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_CODE,
//                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_TEXT
//                + mp.getReference1(),
//                getCurrentContext());
        }
        if (ok) {
            final Element f1 = getNormalizedProofLine(n1);
            final Element f2 = getNormalizedProofLine(n2);
            final Element current = getNormalizedProofLine(i);
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
                    getModuleContextOfProofLineFormula(n1.intValue()));
            } else if (!current.equals(f1.getList().getElement(1))) {
                ok = false;
                setLocationWithinModule(context + ".getReference1()");
                handleProofCheckException(
                    BasicProofErrors.CURRENT_MUST_BE_CONCLUSION_CODE,
                    BasicProofErrors.CURRENT_MUST_BE_CONCLUSION_TEXT
                    + mp.getReference1(),
                    getCurrentContext(),
                    getModuleContextOfProofLineFormula(n1.intValue()));
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
        } else if (!ruleVersion.equals(defined.getVersion())) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_CODE,
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT + defined.getVersion(),
                getCurrentContext());
            return ok;
        }
        return ok;
    }

    private boolean check(final Universal universal, final int i, final Element element) {
        final String context = currentContext.getLocationWithinModule();
        boolean ok = true;
        final Integer n = (Integer) label2line.get(universal.getReference());
        if (n == null) {
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
            final Element f = getNormalizedProofLine(n);
            final Element current = resolver.getNormalizedFormula(element);
            if (!FormulaUtility.isImplication(f)) {
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
            expected.add((f.getList().getElement(0)));
            final ElementList uni = new DefaultElementList(Operators.UNIVERSAL_QUANTIFIER_OPERATOR);
            uni.add(universal.getSubjectVariable());
            uni.add(f.getList().getElement(1));
            expected.add(uni);
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
        } else if (!ruleVersion.equals(defined.getVersion())) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_CODE,
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT + defined.getVersion(),
                getCurrentContext());
            return ok;
        }
        return ok;
    }

    private boolean check(final Existential existential, final int i, final Element element) {
        final String context = currentContext.getLocationWithinModule();
        boolean ok = true;
        final Integer n = (Integer) label2line.get(existential.getReference());
        if (n == null) {
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
            final Element f = getNormalizedProofLine(n);
            final Element current = resolver.getNormalizedFormula(element);
            if (!FormulaUtility.isImplication(f)) {
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
            final DefaultElementList expected = new DefaultElementList(f.getList().getOperator());
            final ElementList exi = new DefaultElementList(
                Operators.EXISTENTIAL_QUANTIFIER_OPERATOR);
            exi.add(existential.getSubjectVariable());
            exi.add(f.getList().getElement(0));
            expected.add(exi);
            expected.add((f.getList().getElement(1)));
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
        } else if (!ruleVersion.equals(defined.getVersion())) {
            ok = false;
            handleProofCheckException(
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_CODE,
                BasicProofErrors.PROOF_METHOD_IS_NOT_SUPPORTED_TEXT + defined.getVersion(),
                getCurrentContext());
            return ok;
        }
        return ok;
    }

    private ModuleContext getModuleContextOfProofLineFormula(final int i) {
        return new ModuleContext(moduleContext.getModuleLocation(),
            moduleContext.getLocationWithinModule() + ".get(" + i
            + ").getFormula().getElement()");
    }

    private ModuleContext getDiffModuleContextOfProofLineFormula(final int i,
            final Element expected) {
        final String diff = FormulaUtility.getDifferenceLocation(
            proof.get(i).getFormula().getElement(),  expected);
        return new ModuleContext(moduleContext.getModuleLocation(),
            moduleContext.getLocationWithinModule() + ".get(" + i
            + ").getFormula().getElement()" + diff);
    }

    private Element getNormalizedProofLine(final Integer n) {
        if (n == null) {
            return null;
        }
        return getNormalizedProofLine(n.intValue());
    }

    private Element getNormalizedProofLine(final int i) {
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
     * Set location information where are we within the original module.
     *
     * @param   locationWithinModule    Location within module.
     */
    protected void setLocationWithinModule(final String locationWithinModule) {
        getCurrentContext().setLocationWithinModule(locationWithinModule);
    }

    /**
     * Get current context within original.
     *
     * @return  Current context.
     */
    protected final ModuleContext getCurrentContext() {
        return currentContext;
    }


}
