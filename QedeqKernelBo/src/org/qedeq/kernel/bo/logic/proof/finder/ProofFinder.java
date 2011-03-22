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

package org.qedeq.kernel.bo.logic.proof.finder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.qedeq.base.utility.Enumerator;
import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.bo.logic.common.FormulaUtility;
import org.qedeq.kernel.bo.logic.common.Operators;
import org.qedeq.kernel.bo.logic.proof.basic.BasicProofErrors;
import org.qedeq.kernel.bo.logic.proof.basic.ProofCheckException;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.list.ElementList;
import org.qedeq.kernel.se.base.module.Add;
import org.qedeq.kernel.se.base.module.Existential;
import org.qedeq.kernel.se.base.module.ModusPonens;
import org.qedeq.kernel.se.base.module.ReasonType;
import org.qedeq.kernel.se.base.module.Rename;
import org.qedeq.kernel.se.base.module.SubstFree;
import org.qedeq.kernel.se.base.module.SubstFunc;
import org.qedeq.kernel.se.base.module.SubstPred;
import org.qedeq.kernel.se.base.module.Universal;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.dto.list.DefaultElementList;
import org.qedeq.kernel.se.dto.list.ElementSet;
import org.qedeq.kernel.se.dto.module.FormalProofLineListVo;
import org.qedeq.kernel.se.dto.module.ModusPonensVo;


/**
 * Find basic proofs for formulas.
 *
 * @author  Michael Meyling
 */
public class ProofFinder {

    /** Proof we extended. */
    private FormalProofLineListVo proof;

    /** List of proof lines. */
    private List lines;

    /** List of reasons. */
    private List reasons;

    /** Maps local proof line labels to local line number Integers. */
    private Map label2line;

    /**
     * Constructor.
     *
     */
    public ProofFinder() {
    }

    /**
     * Find proof for formula with basic rules.
     *
     * @param   formula This formula we want to prove.
     * @param   proof   Begin of proof with some formulas we start with.
     * @return  Did we find a proof?
     */
    public boolean findProof(final Element formula,
            final FormalProofLineListVo proof) {
        this.proof = proof;
        lines = new ArrayList();
        reasons = new ArrayList();
        // add first proof formulas to our proof line list
        for (int i = 0; i < proof.size(); i++) {
            lines.add(proof.get(0).getFormula().getElement());
            reasons.add(proof.get(0).getReasonType());
        }
        return false;
    }

    public void addModusPonens() {
        int until = lines.size();
        for (int i = 0; i < until; i++) {
            final Element first = (Element) lines.get(i);
            if (!FormulaUtility.isImplication(first)) {
                continue;
            }
            for (int j = 0; j < until; j++) {
                if (first.getList().getElement(0).equals(
                        (Element) lines.get(j))) {
                    final ModusPonens mp = new ModusPonensVo("" + i, "" + j);
                }
            }
        }
    }

    private void addFormula(final Element formula, final ReasonType reasonType) {
        if (!lines.contains(formula)) {
            lines.add(formula);
        }
    }

/*
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
        if (!resolver.hasProvedFormula(add.getReference())) {
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
        return ok;
    }

    private boolean check(final Rename rename, final int i, final Element element) {
        System.out.println("Testing rename");
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
        } else if (!lineProved[n.intValue()]) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_CODE,
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_TEXT
                + rename.getReference(),
                getCurrentContext());
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
        System.out.println("rename status: " + ok);
        return ok;
    }

    private boolean check(final SubstFree substfree, final int i, final Element element) {
        final String context = currentContext.getLocationWithinModule();
        boolean ok = true;
        final Integer n = (Integer) label2line.get(substfree.getReference());
        if (n == null) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_CODE,
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_TEXT
                + substfree.getReference(),
                getCurrentContext());
        } else if (!lineProved[n.intValue()]) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_CODE,
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_TEXT
                + substfree.getReference(),
                getCurrentContext());
        } else {
            final Element f = getNormalizedProofLine(n);
            final Element current = resolver.getNormalizedFormula(element);
            final Element expected = f.replace(substfree.getSubjectVariable(),
                resolver.getNormalizedFormula(substfree.getSubstituteTerm()));
            if (!EqualsUtility.equals(current, expected)) {
                ok = false;
                handleProofCheckException(
                    BasicProofErrors.EXPECTED_FORMULA_DIFFERS_CODE,
                    BasicProofErrors.EXPECTED_FORMULA_DIFFERS_TEXT
                    + substfree.getReference(),
                    getDiffModuleContextOfProofLineFormula(i, expected));
                return ok;
            }
        }
        return ok;
    }

    private boolean check(final SubstPred substpred, final int i, final Element element) {
        final String context = currentContext.getLocationWithinModule();
        boolean ok = true;
        final Integer n = (Integer) label2line.get(substpred.getReference());
        if (n == null) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_CODE,
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_TEXT
                + substpred.getReference(),
                getCurrentContext());
        } else if (!lineProved[n.intValue()]) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_CODE,
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_TEXT
                + substpred.getReference(),
                getCurrentContext());
        } else {
            final Element alpha = getNormalizedProofLine(n);
            final Element current = resolver.getNormalizedFormula(element);
            if (substpred.getSubstituteFormula() == null) {
                ok = false;
                handleProofCheckException(
                    BasicProofErrors.SUBSTITUTION_FORMULA_IS_MISSING_CODE,
                    BasicProofErrors.SUBSTITUTION_FORMULA_IS_MISSING_TEXT,
                    getCurrentContext());
                return ok;
            }
            final Element p = resolver.getNormalizedFormula(substpred.getPredicateVariable());
            final Element beta = resolver.getNormalizedFormula(substpred.getSubstituteFormula());
            final Element expected = FormulaUtility.replaceOperatorVariable(alpha, p, beta);
            if (!EqualsUtility.equals(current, expected)) {
                ok = false;
                handleProofCheckException(
                    BasicProofErrors.EXPECTED_FORMULA_DIFFERS_CODE,
                    BasicProofErrors.EXPECTED_FORMULA_DIFFERS_TEXT
                    + substpred.getReference(),
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
        return ok;
    }

    private boolean check(final SubstFunc substfunc, final int i, final Element element) {
        final String context = currentContext.getLocationWithinModule();
        boolean ok = true;
        final Integer n = (Integer) label2line.get(substfunc.getReference());
        if (n == null) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_CODE,
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_TEXT
                + substfunc.getReference(),
                getCurrentContext());
        } else if (!lineProved[n.intValue()]) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_CODE,
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_TEXT
                + substfunc.getReference(),
                getCurrentContext());
        } else {
            final Element alpha = getNormalizedProofLine(n);
            final Element current = resolver.getNormalizedFormula(element);
            if (substfunc.getSubstituteTerm() == null) {
                ok = false;
                handleProofCheckException(
                    BasicProofErrors.SUBSTITUTION_FORMULA_IS_MISSING_CODE,
                    BasicProofErrors.SUBSTITUTION_FORMULA_IS_MISSING_TEXT,
                    getCurrentContext());
                return ok;
            }
            final Element sigma = resolver.getNormalizedFormula(substfunc.getFunctionVariable());
            final Element tau = resolver.getNormalizedFormula(substfunc.getSubstituteTerm());
            final Element expected = FormulaUtility.replaceOperatorVariable(alpha, sigma, tau);
            if (!EqualsUtility.equals(current, expected)) {
                ok = false;
                handleProofCheckException(
                    BasicProofErrors.EXPECTED_FORMULA_DIFFERS_CODE,
                    BasicProofErrors.EXPECTED_FORMULA_DIFFERS_TEXT
                    + substfunc.getReference(),
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
        } else if (!lineProved[n1.intValue()]) {
            ok = false;
            setLocationWithinModule(context + ".getReference1()");
            handleProofCheckException(
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_CODE,
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_TEXT
                + mp.getReference1(),
                getCurrentContext());
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
        } else if (!lineProved[n2.intValue()]) {
            ok = false;
            setLocationWithinModule(context + ".getReference2()");
            handleProofCheckException(
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_CODE,
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_TEXT
                + mp.getReference1(),
                getCurrentContext());
        }
        if (ok) {
            final Element f1 = getNormalizedProofLine(n1);
            final Element f2 = getNormalizedProofLine(n2);
            final Element current = getNormalizedProofLine(i);
            if (!Operators.IMPLICATION_OPERATOR.equals(f1.getList().getOperator())
                    || f1.getList().size() != 2) {
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
        } else if (!lineProved[n.intValue()]) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_CODE,
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_TEXT
                + universal.getReference(),
                getCurrentContext());
        } else {
            final Element f = getNormalizedProofLine(n);
            final Element current = resolver.getNormalizedFormula(element);
            if (!Operators.IMPLICATION_OPERATOR.equals(f.getList().getOperator())
                    || f.getList().size() != 2) {
                ok = false;
                setLocationWithinModule(context + ".getReference()");
                handleProofCheckException(
                    BasicProofErrors.IMPLICATION_EXPECTED_CODE,
                    BasicProofErrors.IMPLICATION_EXPECTED_TEXT
                    + universal.getReference(),
                    getCurrentContext());
                return ok;
            }
            final DefaultElementList expected = new DefaultElementList(f.getList().getOperator());
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
        } else if (!lineProved[n.intValue()]) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_CODE,
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_TEXT
                + existential.getReference(),
                getCurrentContext());
        } else {
            final Element f = getNormalizedProofLine(n);
            final Element current = resolver.getNormalizedFormula(element);
            if (!Operators.IMPLICATION_OPERATOR.equals(f.getList().getOperator())
                    || f.getList().size() != 2) {
                ok = false;
                setLocationWithinModule(context + ".getReference()");
                handleProofCheckException(
                    BasicProofErrors.IMPLICATION_EXPECTED_CODE,
                    BasicProofErrors.IMPLICATION_EXPECTED_TEXT
                    + existential.getReference(),
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


*/

}
