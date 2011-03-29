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

import org.qedeq.kernel.bo.logic.common.FormulaUtility;
import org.qedeq.kernel.bo.logic.common.Operators;
import org.qedeq.kernel.bo.logic.common.ProofFinder;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.list.ElementList;
import org.qedeq.kernel.se.base.module.Add;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.base.module.ModusPonens;
import org.qedeq.kernel.se.base.module.Reason;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.dto.list.DefaultElementList;
import org.qedeq.kernel.se.dto.list.ElementSet;
import org.qedeq.kernel.se.visitor.InterruptException;


/**
 * Find basic proofs for formulas.
 *
 * @author  Michael Meyling
 */
public class ProofFinderImpl implements ProofFinder {

    /** Proof with basic formulas added by "Add". */
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
    public ProofFinderImpl() {
    }

    public FormalProofLineList findProof(final Element formula,
            final FormalProofLineList proof, final ModuleContext context) throws InterruptException {
        this.goalFormula = formula;
        this.proof = proof;
        lines = new ArrayList();
        reasons = new ArrayList();
        allPredVars = new ElementSet();
        // add first proof formulas to our proof line list
        for (int i = 0; i < proof.size(); i++) {
            final Reason r = proof.get(i).getReasonType().getReason();
            if (!(r instanceof Add)) {
                continue;
            }
            lines.add(proof.get(i).getFormula().getElement());
            reasons.add(r);
            // FIXME only predvars with null arguments!
            allPredVars.union(FormulaUtility.getPredicateVariables(
                proof.get(i).getFormula().getElement()));
        }
        String max = "A";
        final Iterator j = allPredVars.iterator();
        while (j.hasNext()) {
            final ElementList v = (ElementList) j.next();
            final String name = v.getElement(0).getAtom().getString();
            if (-1 == max.compareTo(name)) {
                max = name;
            }
        }
        max = (char) (max.charAt(0) + 1) + "";
        System.out.println(max);
        // add one extra predicate variable
        allPredVars.add(FormulaUtility.createPredicateVariable(max));
        for (int i = 0; i < lines.size(); i++) {
            ProofFinderUtility.printLine(lines, reasons, i);
        }
        System.out.println("Goal: ");
        FormulaUtility.println(formula);
        int i = 0;
        while (lines.size() < Integer.MAX_VALUE - 1000) {
            try {
                tryModusPonensAll();
                if (Thread.interrupted()) {
                    throw new InterruptException(context);
                }
                trySubstitution(i++);
            } catch (ProofFoundException e) {
                System.out.println("proof found. lines: " + lines.size());
                return ProofFinderUtility.shortenProof(lines, reasons);
            }
        }
        System.out.println("proof not found. lines: " + lines.size());
        return null;
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

    /**
     * Make all possible substitutions in a proof line.
     *
     * @param   i   Proof line number we want to try substitution.
     * @throws  ProofFoundException     We found a proof!
     */
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
     * Substitute predicate variable <code>var</code> by binary operator with old variable
     * and new variable and add this as a new proof line.
     *
     * @param   i           Proof line number we work on.
     * @param   f           Proof line formula.
     * @param   var         Predicate variable we want to replace.
     * @param   operator    Operator of replacement formula.
     * @param   left        Is old variable at left hand of new operator?
     * @throws  ProofFoundException     We found a proof!
     */
    private void createReplacement(final int i, final Element f,
            final ElementList var, final String operator, final boolean left) throws ProofFoundException {
        final Iterator a = allPredVars.iterator();
        while (a.hasNext()) {
            final ElementList var2 = (ElementList) a.next();
            final ElementList subst = new DefaultElementList(operator);
            if (left) {
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

    private void addFormula(final Element formula, final Reason reason) throws ProofFoundException {
        if (!lines.contains(formula)) {
            lines.add(formula);
            reasons.add(reason);
//            printLine(lines.size() - 1);
            if (goalFormula.equals(formula)) {
                throw new ProofFoundException();
            }
            if ((lines.size() - 1) % 1000 == 0) {
                ProofFinderUtility.printLine(lines, reasons, lines.size() - 1);
            }
        }
    }

    /**
     * Indicates we fond a proof.
     *
     * @author  Michael Meyling.
     *
     */
    private static class ProofFoundException extends Exception {

    }

}
