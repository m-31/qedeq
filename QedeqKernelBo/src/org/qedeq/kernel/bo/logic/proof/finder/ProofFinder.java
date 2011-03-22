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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.qedeq.kernel.bo.logic.common.FormulaUtility;
import org.qedeq.kernel.bo.logic.common.Operators;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.list.ElementList;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.base.module.ModusPonens;
import org.qedeq.kernel.se.base.module.Reason;
import org.qedeq.kernel.se.base.module.SubstPred;
import org.qedeq.kernel.se.dto.list.DefaultElementList;
import org.qedeq.kernel.se.dto.list.ElementSet;


/**
 * Find basic proofs for formulas.
 *
 * @author  Michael Meyling
 */
public class ProofFinder {

    /** Proof we extended. */
    private FormalProofLineList proof;

    /** List of proof lines. */
    private List lines;

    /** List of reasons. */
    private List reasons;

    /** Set of all predicate variables that occur in this proof anywhere. */
    private ElementSet allPredVars;

    /** Below this number MP was tried. */
    private int mpLast;

    /** Goal to prove. */
    private Element goalFormula;

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
            final FormalProofLineList proof) {
        this.goalFormula = formula;
        this.proof = proof;
        lines = new ArrayList();
        reasons = new ArrayList();
        allPredVars = new ElementSet();
        // add first proof formulas to our proof line list
        for (int i = 0; i < proof.size(); i++) {
            lines.add(proof.get(i).getFormula().getElement());
            reasons.add(proof.get(i).getReasonType());
            // FIXME only predvars with null arguments!
            allPredVars.union(FormulaUtility.getPredicateVariables(
                proof.get(i).getFormula().getElement()));
            allPredVars.add(FormulaUtility.createPredicateVariable("E"));
            allPredVars.add(FormulaUtility.createPredicateVariable("F"));
        }
        int i = 0;
        while (lines.size() < 100000) {
            try {
                tryModusPonensAll();
                trySubstitution(i++);
            } catch (ProofFoundException e) {
                System.out.println("proof found. lines: " + lines.size());
                shortenProof();
                return true;
            }
        }
        System.out.println("proof not found. lines: " + lines.size());
        return false;
    }

    private void shortenProof() {
        final boolean[] used = new boolean[lines.size()];
        int n = lines.size() - 1;
        used[n] = true;
        while (n >= proof.size()) {
            if (used[n]) {
                Reason r = (Reason) reasons.get(n);
                if (r instanceof ModusPonensBo) {
                    final ModusPonensBo mp = (ModusPonensBo) reasons.get(n);
                    used[mp.getN1()] = true;
                    used[mp.getN2()] = true;
                    n = Math.max(mp.getN1(), mp.getN2());
                } else if (r instanceof SubstPredBo) {
                    final SubstPredBo substPred = (SubstPredBo) reasons.get(n);
                    used[substPred.getN()] = true;
                    n = substPred.getN();
                }
            }
        }
        for (int j = 0; j < proof.size(); j++) {
            System.out.print(j + ": ");
            FormulaUtility.println(proof.get(j).getFormula().getElement());
        }
        for (int j = proof.size(); j < lines.size(); j++) {
            if (used[j]) {
                printLine(j);
            }
        }
    }

    private void tryModusPonensAll() throws ProofFoundException {
        int until = lines.size();
        for (int i = 0; i < until; i++) {
            final Element first = (Element) lines.get(i);
            if (!FormulaUtility.isImplication(first)) {
                continue;
            }
            for (int j = (i < mpLast ? mpLast : 0); j < until; j++) {
                if (first.getList().getElement(0).equals(
                        (Element) lines.get(j))) {
//                    System.out.println("MP found!");
                    final ModusPonens mp = new ModusPonensBo(i, j);
                    addFormula(first.getList().getElement(1), mp);
                }
            }
        }
        mpLast = until;
    }

    private void trySubstitution(final int i) throws ProofFoundException {
//        System.out.print("subst " + i + " ");
        final Element f = (Element) lines.get(i);
//        FormulaUtility.println(f);
        final ElementSet vars = FormulaUtility.getPredicateVariables(f);
//        System.out.print("vars=");
//        System.out.println(vars);
        final Iterator iter = vars.iterator();
        while (iter.hasNext()) {
            final ElementList var = (ElementList) iter.next();
//            System.out.print("var=");
//            FormulaUtility.println(var);
            // substitute by different variable
            {
//                System.out.print("allPredVars=");
//                System.out.println(allPredVars);
                final Iterator all = allPredVars.iterator();
                while (all.hasNext()) {
                    final ElementList subst = (ElementList) all.next();
                    if (var.equals(subst)) {
                        continue;
                    }
                    final Element created = FormulaUtility.replaceOperatorVariable(
                        f, var, subst);
                    addFormula(created, new SubstPredBo(i, var, subst));
                }
            }
//            // substitute by negation
//            {
//                final Iterator all = allPredVars.iterator();
//                while (all.hasNext()) {
//                    final ElementList var2 = (ElementList) all.next();
//                    final ElementList subst = new DefaultElementList(Operators.NEGATION_OPERATOR);
//                    subst.add(var2);
//                    final Element created = FormulaUtility.replaceOperatorVariable(
//                        f, var, subst);
//                    addFormula(created, new SubstPredBo(i, var, subst));
//                }
//            }
//            // substitute by conjunction with another variable
//            createReplacement(i, f, var, Operators.CONJUNCTION_OPERATOR, true);
//            createReplacement(i, f, var, Operators.CONJUNCTION_OPERATOR, false);
            // substitute by disjunction with another variable
            createReplacement(i, f, var, Operators.DISJUNCTION_OPERATOR, true);
            createReplacement(i, f, var, Operators.DISJUNCTION_OPERATOR, false);
//            // substitute by implication with another variable
//            createReplacement(i, f, var, Operators.EQUIVALENCE_OPERATOR, true);
//            createReplacement(i, f, var, Operators.EQUIVALENCE_OPERATOR, false);
        }
    }

    /**
     * @param i
     * @param f
     * @param var
     * @param operator
     * @param right
     * @throws ProofFoundException 
     */
    private void createReplacement(final int i, final Element f,
            final ElementList var, final String operator, final boolean right) throws ProofFoundException {
        final Iterator a = allPredVars.iterator();
        while (a.hasNext()) {
            final ElementList var2 = (ElementList) a.next();
            final ElementList subst = new DefaultElementList(operator);
            if (right) {
                subst.add(var);
                subst.add(var2);
            } else {
                subst.add(var2);
                subst.add(var);
            }
            final Element created = FormulaUtility.replaceOperatorVariable(
                f, var, subst);
            addFormula(created, new SubstPredBo(i, var, subst));
        }
    }

    private void printLine(final int i) {
        System.out.print(i + ": ");
        Reason reason = (Reason) reasons.get(i);
        Element formula = (Element) lines.get(i);
        FormulaUtility.print(formula);
        System.out.print("  : ");
        if (reason instanceof SubstPred) {
            SubstPred s = (SubstPred) reason;
            System.out.print("Subst " + s.getReference() + " ");
            FormulaUtility.print(s.getPredicateVariable());
            System.out.print(" by ");
            FormulaUtility.print(s.getSubstituteFormula());
        } else {
            System.out.print(reason);
        }
        System.out.println();
    }

    private void addFormula(final Element formula, final Reason reason) throws ProofFoundException {
        if (!lines.contains(formula)) {
            lines.add(formula);
            reasons.add(reason);
//            printLine(lines.size() - 1);
            if (goalFormula.equals(formula)) {
                throw new ProofFoundException();
            }
            if ((lines.size() - 1) % 1000 == 0) {
                printLine(lines.size() - 1);
            }
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
    private static class ProofFoundException extends Exception {
        
    }

}
