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
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.qedeq.kernel.bo.logic.common.FormulaUtility;
import org.qedeq.kernel.bo.logic.common.Operators;
import org.qedeq.kernel.bo.logic.common.ProofException;
import org.qedeq.kernel.bo.logic.common.ProofFinder;
import org.qedeq.kernel.bo.logic.common.ProofFoundException;
import org.qedeq.kernel.bo.logic.common.ProofNotFoundException;
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
public class ProofFinderImpl2 implements ProofFinder {

    /** Search parameters. */
    private Map parameters;

    /** List of proof lines. */
    private List lines;

    /** List of reasons. */
    private List reasons;

    /** Set of all predicate variables that occur in this proof anywhere. */
    private ElementSet allPredVars;

    /** Set of all substitution formulas we try. */
    private ElementSet partGoalFormulas;

    /** Below this number MP was tried. */
    private int mpLast;

    /** Goal to prove. */
    private Element goalFormula;

    /** Number of extra propositional variables. */
    private int extraVars;

    /** Ordered substitution methods that implement {@link Substitute}. */
    private SortedSet substitutionMethods;

    /** Maximum number of proof lines. */
    private int maxProofLines;

    /** Here are we. */
    private ModuleContext context;

    /**
     * Constructor.
     */
    public ProofFinderImpl2() {
    }

    public FormalProofLineList findProof(final Element formula,
            final FormalProofLineList proof, final ModuleContext context,
            final Map parameters) throws ProofException, InterruptException {
        this.goalFormula = formula;
        this.parameters = parameters;
        this.context = new ModuleContext(context);
        substitutionMethods = new TreeSet();
        extraVars = getInt("extraVars", 1);
        maxProofLines = getInt("maximumProofLines", Integer.MAX_VALUE - 2);
        System.out.println("maximumProofLines = " + maxProofLines);
        int weight = 0;
        weight = getInt("propositionVariableWeight", 3);
        if (weight > 0) {
            substitutionMethods.add(new SubstituteBase(weight, getInt("propositionVariableOrder", 1)) {
                public void substitute(final int i) throws ProofException {
                    substituteByPropositionVariables(i);
                }
            });
        }
        weight = getInt("partFormulaWeight", 1);
        if (weight > 0) {
            substitutionMethods.add(new SubstituteBase(weight, getInt("partFormulaOrder", 2)) {
                public void substitute(final int i) throws ProofException {
                    substitutePartGoalFormulas(i);
                }
            });
        }
        weight = getInt("disjunctionWeight", 3);
        if (weight > 0) {
            substitutionMethods.add(new SubstituteBase(weight, getInt("disjunctionOrder", 3)) {
                public void substitute(final int i) throws ProofException {
                    substituteDisjunction(i);
                }
            });
        }
        weight = getInt("implicationWeight", 1);
        if (weight > 0) {
            substitutionMethods.add(new SubstituteBase(weight, getInt("implicationOrder", 4)) {
                public void substitute(final int i) throws ProofException {
                    substituteImplication(i);
                }
            });
        }
        weight = getInt("negationWeight", 1);
        if (weight > 0) {
            substitutionMethods.add(new SubstituteBase(weight, getInt("negationOrder", 5)) {
                public void substitute(final int i) throws ProofException {
                    substituteNegation(i);
                }
            });
        }
        weight = getInt("conjunctionWeight", 1);
        if (weight > 0) {
            substitutionMethods.add(new SubstituteBase(weight, getInt("conjunctionOrder", 6)) {
                public void substitute(final int i) throws ProofException {
                    substituteConjunction(i);
                }
            });
        }
        weight = getInt("equivalenceWeight", 1);
        if (weight > 0) {
            substitutionMethods.add(new SubstituteBase(weight, getInt("equivalenceOrder", 7)) {
                public void substitute(final int i) throws ProofException {
                    substituteEquivalence(i);
                }
            });
        }

        lines = new ArrayList();
        reasons = new ArrayList();
        setAllPredVars(proof);
        partGoalFormulas = FormulaUtility.getPartFormulas(goalFormula);
        System.out.println(partGoalFormulas);
        for (int i = 0; i < lines.size(); i++) {
            ProofFinderUtility.printUtf8Line(lines, reasons, i);
        }
        System.out.println("Goal: ");
        ProofFinderUtility.println(formula);
        int i = 0;
        while (true) {
            // check if the thread should be
            if (Thread.interrupted()) {
                throw new InterruptException(this.context);
            }
            tryModusPonensAll();
            final Iterator iter = substitutionMethods.iterator();
            while (iter.hasNext()) {
                final Substitute method = (Substitute) iter.next();
//                System.out.println(method.getClass());
                for (int j = 0; j < method.getWeight(); j++) {
                    if (method.nextLine() >= lines.size()) {
                        break;
                    }
                    method.substitute();
                    tryModusPonensAll();
                }
            }
        }
    }

    private void setAllPredVars(final FormalProofLineList proof) {
        allPredVars = new ElementSet();
        // add all "add" proof formulas to our proof line list
        for (int i = 0; i < proof.size(); i++) {
            final Reason r = proof.get(i).getReason();
            if (!(r instanceof Add)) {
                continue;
            }
            lines.add(proof.get(i).getFormula().getElement());
            reasons.add(r);
            allPredVars.union(FormulaUtility.getPropositionVariables(
                proof.get(i).getFormula().getElement()));
        }
        String max = "A";
        final Iterator iter = allPredVars.iterator();
        while (iter.hasNext()) {
            final ElementList v = (ElementList) iter.next();
            final String name = v.getElement(0).getAtom().getString();
            if (-1 == max.compareTo(name)) {
                max = name;
            }
        }
        System.out.println("Adding extra variables:");
        for (int i = 1; i <= extraVars; i++) {
            max = (char) (max.charAt(0) + i) + "";
            System.out.println("\t" + max);
            // add extra predicate variable
            allPredVars.add(FormulaUtility.createPredicateVariable(max));
        }
    }

    private int getInt(final String key, final int std) {
        int result = std;
        if (parameters != null) {
            final String extraVarsString = (String) parameters.get(key);
            try {
                result = Integer.parseInt(extraVarsString);
            } catch (RuntimeException e) {
            }
        }
        return result;
    }

    private void tryModusPonensAll() throws ProofException {
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
     * Make all possible substitutions by propositional variables in a proof line.
     *
     * @param   i   Proof line number we want to try substitution.
     * @throws  ProofException We found a proof or have to end the search!
     */
    private void substituteByPropositionVariables(final int i) throws ProofException {
        final Element f = (Element) lines.get(i);
        final ElementSet vars = FormulaUtility.getPropositionVariables(f);
        final Iterator iter = vars.iterator();
        while (iter.hasNext()) {
            final ElementList var = (ElementList) iter.next();
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
    }

    /**
     * Make all substitutions by part goal formulas in a proof line.
     *
     * @param   i   Proof line number we want to try substitution.
     * @throws  ProofException We found a proof or have to end the search!
     */
    private void substitutePartGoalFormulas(final int i) throws ProofException {
        final Element f = (Element) lines.get(i);
        final ElementSet vars = FormulaUtility.getPropositionVariables(f);
        final Iterator iter = vars.iterator();
        while (iter.hasNext()) {
            final ElementList var = (ElementList) iter.next();
            {
                final Iterator all = partGoalFormulas.iterator();
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
        }
    }

    /**
     * Make all substitutions by negation in a proof line.
     *
     * @param   i   Proof line number we want to try substitution.
     * @throws  ProofException We found a proof or have to end the search!
     */
    private void substituteNegation(final int i) throws ProofException {
        final Element f = (Element) lines.get(i);
        final ElementSet vars = FormulaUtility.getPropositionVariables(f);
        final Iterator iter = vars.iterator();
        while (iter.hasNext()) {
            final ElementList var = (ElementList) iter.next();
            final Iterator all = allPredVars.iterator();
            while (all.hasNext()) {
                final ElementList var2 = (ElementList) all.next();
                final ElementList subst = new DefaultElementList(Operators.NEGATION_OPERATOR);
                subst.add(var2);
                final Element created = FormulaUtility.replaceOperatorVariable(
                    f, var, subst);
                addFormula(created, new SubstPredBo(i, var, subst));
            }
        }
    }

    /**
     * Make all substitutions by conjunction in a proof line.
     *
     * @param   i   Proof line number we want to try substitution.
     * @throws  ProofException We found a proof or have to end the search!
     */
    private void substituteConjunction(final int i) throws ProofException {
        final Element f = (Element) lines.get(i);
        final ElementSet vars = FormulaUtility.getPropositionVariables(f);
        final Iterator iter = vars.iterator();
        while (iter.hasNext()) {
            final ElementList var = (ElementList) iter.next();
            createReplacement(i, f, var, Operators.CONJUNCTION_OPERATOR, true);
            createReplacement(i, f, var, Operators.CONJUNCTION_OPERATOR, false);
        }
    }

    /**
     * Make all substitutions by disjunction in a proof line.
     *
     * @param   i   Proof line number we want to try substitution.
     * @throws  ProofException We found a proof or have to end the search!
     */
    private void substituteDisjunction(final int i) throws ProofException {
        final Element f = (Element) lines.get(i);
        final ElementSet vars = FormulaUtility.getPropositionVariables(f);
        final Iterator iter = vars.iterator();
        while (iter.hasNext()) {
            final ElementList var = (ElementList) iter.next();
            createReplacement(i, f, var, Operators.DISJUNCTION_OPERATOR, true);
            createReplacement(i, f, var, Operators.DISJUNCTION_OPERATOR, false);
        }
    }

    /**
     * Make all substitutions by implication in a proof line.
     *
     * @param   i   Proof line number we want to try substitution.
     * @throws  ProofException We found a proof or have to end the search!
     */
    private void substituteImplication(final int i) throws ProofException {
        final Element f = (Element) lines.get(i);
        final ElementSet vars = FormulaUtility.getPropositionVariables(f);
        final Iterator iter = vars.iterator();
        while (iter.hasNext()) {
            final ElementList var = (ElementList) iter.next();
            createReplacement(i, f, var, Operators.IMPLICATION_OPERATOR, true);
            createReplacement(i, f, var, Operators.IMPLICATION_OPERATOR, false);
        }
    }

    /**
     * Make all substitutions by equivalence in a proof line.
     *
     * @param   i   Proof line number we want to try substitution.
     * @throws  ProofException We found a proof or have to end the search!
     */
    private void substituteEquivalence(final int i) throws ProofException {
        final Element f = (Element) lines.get(i);
        final ElementSet vars = FormulaUtility.getPropositionVariables(f);
        final Iterator iter = vars.iterator();
        while (iter.hasNext()) {
            final ElementList var = (ElementList) iter.next();
            createReplacement(i, f, var, Operators.EQUIVALENCE_OPERATOR, true);
            createReplacement(i, f, var, Operators.EQUIVALENCE_OPERATOR, false);
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
     * @throws  ProofException We found a proof or have to end the search!
     */
    private void createReplacement(final int i, final Element f,
            final ElementList var, final String operator, final boolean left) throws ProofException {
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

    private void addFormula(final Element formula, final Reason reason) throws ProofException {
        if (!lines.contains(formula)) {
            lines.add(formula);
            reasons.add(reason);
//            printLine(lines.size() - 1);
            if (goalFormula.equals(formula)) {
                final int size = lines.size();
                System.out.println("proof found. lines: " + size);
                throw new ProofFoundException(FinderErrors.PROOF_FOUND_CODE,
                    FinderErrors.PROOF_FOUND_TEXT + size,
                    ProofFinderUtility.shortenProof(lines, reasons), context);
            }
            // did we reach our maximum?
            if (lines.size() >= maxProofLines) {
                final int size = lines.size();
                ProofFinderUtility.printUtf8Line(lines, reasons, lines.size() - 1);
                throw new ProofNotFoundException(FinderErrors.PROOF_NOT_FOUND_CODE,
                    FinderErrors.PROOF_NOT_FOUND_TEXT + size, context);
            }
            if ((lines.size() - 1) % 1000 == 0) {
                ProofFinderUtility.printUtf8Line(lines, reasons, lines.size() - 1);
            }
        }
    }

    public String getExecutionActionDescription() {
        return ProofFinderUtility.getUtf8Line(lines, reasons, lines.size() - 1);
    }

    private abstract class SubstituteBase implements Substitute {

        /** Next proof line we will work on. */
        private int next = 0;

        private final int weight;

        private final int order;

        SubstituteBase(final int weight, final int order) {
            this.weight = weight;
            this.order = order;
        }

        public int getWeight() {
            return weight;
        }

        public int getOrder() {
            return order;
        }
        public int nextLine() {
            return next;
        }

        public int compareTo(final Object obj) {
            if (obj instanceof Substitute) {
                Substitute sub = (Substitute) obj;
                // we don't return 0 because the TreeSet gets no more elements
                // if we have methods with same order value
                if (order == sub.getOrder()) {
                    return -1;
                }
                if (order < sub.getOrder()) {
                    return -1;
                }
                return 1;
            }
            return -1;
        }

        public void substitute() throws ProofException {
            substitute(next);
            next++;
        }

        public abstract void substitute(int i) throws ProofException;

    }


    /**
     * Work on proof line and substitute proposition variables.
     *
     */
    interface Substitute extends Comparable {

        /**
         * Return next proof line number we will work on.
         *
         * @return  Next proof line number we will worked on.
         */
        public int nextLine();

        /**
         * Substitute next proof line number.
         *
         * @throws  ProofException We found a proof or have to end the search!
         */
        public void substitute() throws ProofException;

        /**
         * Get weight of substitution rule. This is the number of runs for this
         * substitution.
         *
         * @return  Weight.
         */
        public int getWeight();

        /**
         * Get order position of substitution rule. This says about the sequence position of this
         * substitution method.
         *
         * @return  Order position.
         */
        public int getOrder();

    }

}
