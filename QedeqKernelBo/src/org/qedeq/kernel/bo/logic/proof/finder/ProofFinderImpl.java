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

package org.qedeq.kernel.bo.logic.proof.finder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.qedeq.base.io.Parameters;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.bo.common.Element2Utf8;
import org.qedeq.kernel.bo.log.ModuleLogListener;
import org.qedeq.kernel.bo.logic.common.FormulaUtility;
import org.qedeq.kernel.bo.logic.common.Operators;
import org.qedeq.kernel.bo.logic.proof.common.ProofException;
import org.qedeq.kernel.bo.logic.proof.common.ProofFinder;
import org.qedeq.kernel.bo.logic.proof.common.ProofFoundException;
import org.qedeq.kernel.bo.logic.proof.common.ProofNotFoundException;
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

    /** Skip this number list of formulas. */
    private String skipFormulas;

    /** Log proof line after this number of new proof lines. */
    private int logFrequence;

    /** Ordered substitution methods that implement {@link Substitute}. */
    private SortedSet substitutionMethods;

    /** Maximum number of proof lines. */
    private int maxProofLines;

    /** Here are we. */
    private ModuleContext context;

    /** Log progress herein. */
    private ModuleLogListener log;

    /** Transformer to get UTF-8 out of formulas. */
    private Element2Utf8 trans;

    /**
     * Constructor.
     */
    public ProofFinderImpl() {
    }

    public void findProof(final Element formula,
            final FormalProofLineList proof, final ModuleContext context,
            final Parameters parameters, final ModuleLogListener log, final Element2Utf8 trans)
            throws ProofException, InterruptException {
        this.goalFormula = formula;
        this.context = new ModuleContext(context);  // use copy constructor to fix it
        this.log = log;
        this.trans = trans;
        substitutionMethods = new TreeSet();
        extraVars = parameters.getInt("extraVars");
        maxProofLines = parameters.getInt("maximumProofLines");
        skipFormulas = parameters.getString("skipFormulas").trim();
        if (skipFormulas.length() > 0) {
            log.logMessageState("skipping the following formula numbers: " + skipFormulas);
            skipFormulas = "," + StringUtility.replace(skipFormulas, " ", "") + ",";
        }
        // TODO 20110606 m31: check that we have the correct format (e.g. only "," as separator)
        System.out.println("maximumProofLines = " + maxProofLines);
        int weight = 0;
        weight = parameters.getInt("propositionVariableWeight");
        if (weight > 0) {
            substitutionMethods.add(new SubstituteBase(weight, parameters.getInt("propositionVariableOrder")) {
                public void substitute(final int i) throws ProofException {
                    substituteByPropositionVariables(i);
                }
            });
        }
        weight = parameters.getInt("partFormulaWeight");
        if (weight > 0) {
            substitutionMethods.add(new SubstituteBase(weight, parameters.getInt("partFormulaOrder")) {
                public void substitute(final int i) throws ProofException {
                    substitutePartGoalFormulas(i);
                }
            });
        }
        weight = parameters.getInt("disjunctionWeight");
        if (weight > 0) {
            substitutionMethods.add(new SubstituteBase(weight, parameters.getInt("disjunctionOrder")) {
                public void substitute(final int i) throws ProofException {
                    substituteDisjunction(i);
                }
            });
        }
        weight = parameters.getInt("implicationWeight");
        if (weight > 0) {
            substitutionMethods.add(new SubstituteBase(weight, parameters.getInt("implicationOrder")) {
                public void substitute(final int i) throws ProofException {
                    substituteImplication(i);
                }
            });
        }
        weight = parameters.getInt("negationWeight");
        if (weight > 0) {
            substitutionMethods.add(new SubstituteBase(weight, parameters.getInt("negationOrder")) {
                public void substitute(final int i) throws ProofException {
                    substituteNegation(i);
                }
            });
        }
        weight = parameters.getInt("conjunctionWeight");
        if (weight > 0) {
            substitutionMethods.add(new SubstituteBase(weight, parameters.getInt("conjunctionOrder")) {
                public void substitute(final int i) throws ProofException {
                    substituteConjunction(i);
                }
            });
        }
        weight = parameters.getInt("equivalenceWeight");
        if (weight > 0) {
            substitutionMethods.add(new SubstituteBase(weight, parameters.getInt("equivalenceOrder")) {
                public void substitute(final int i) throws ProofException {
                    substituteEquivalence(i);
                }
            });
        }
        logFrequence = parameters.getInt("logFrequence");

        lines = new ArrayList();
        reasons = new ArrayList();
        setAllPredVars(proof);
        partGoalFormulas = FormulaUtility.getPartFormulas(goalFormula);
        log.logMessageState("our goal: " + trans.getUtf8(formula));
        int size2 = 0;
        while (true) {
            // check if the thread should be interrupted
            if (Thread.interrupted()) {
                throw new InterruptException(this.context);
            }
            int size1 = lines.size();
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
//            System.out.println(lines.size());
            if (size2 == lines.size()) {
                // we didn't generate new lines, so we just quit
                log.logMessageState(FinderErrors.PROOF_NOT_FOUND_TEXT + size2);
                throw new ProofNotFoundException(FinderErrors.PROOF_NOT_FOUND_CODE,
                    FinderErrors.PROOF_NOT_FOUND_TEXT + size2, context);
            }
            size2 = size1;
        }
    }

    private void setAllPredVars(final FormalProofLineList proof) {
        log.logMessageState("using the following formulas:");
        allPredVars = new ElementSet();
        // add all "add" proof formulas to our proof line list
        for (int i = 0; i < proof.size(); i++) {
            // should we skip this formula
            if (skipFormulas.indexOf("," + (i + 1) + ",") >= 0) {
                continue;
            }
            final Reason reason = proof.get(i).getReason();
            if (!(reason instanceof Add)) {
                continue;
            }
            final Element formula = proof.get(i).getFormula().getElement();
            lines.add(formula);
            reasons.add(reason);
            log.logMessageState(ProofFinderUtility.getUtf8Line(formula, reason, i, trans));
            allPredVars.union(FormulaUtility.getPropositionVariables(
                formula));
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

    /**
     * Try all Modus Ponens with all new lines. Remember tested maximum.
     *
     * @throws  ProofException  Proof found, or limits exceeded.
     */
    private void tryModusPonensAll() throws ProofException {
        int until = lines.size();
        for (int i = 0; i < until; i++) {
            final Element first = (Element) lines.get(i);
            if (!FormulaUtility.isImplication(first)) {
                continue;
            }
            for (int j = (i < mpLast ? mpLast : 0); j < until; j++) {
                if (first.getList().getElement(0).equals(
                        lines.get(j))) {
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
                log.logMessageState(FinderErrors.PROOF_FOUND_TEXT + size);
                throw new ProofFoundException(FinderErrors.PROOF_FOUND_CODE,
                    FinderErrors.PROOF_FOUND_TEXT + size,
                    ProofFinderUtility.shortenProof(lines, reasons, log, trans), context);
            }
            // did we reach our maximum?
            if (lines.size() >= maxProofLines) {
                final int size = lines.size();
                if (logFrequence > 0) {
                    log.logMessageState(ProofFinderUtility.getUtf8Line(lines, reasons, lines.size() - 1,
                        trans));
                }
                log.logMessageState(FinderErrors.PROOF_NOT_FOUND_TEXT + size);
                throw new ProofNotFoundException(FinderErrors.PROOF_NOT_FOUND_CODE,
                    FinderErrors.PROOF_NOT_FOUND_TEXT + size, context);
            }
            if (logFrequence > 0 && (lines.size() - 1) % logFrequence == 0) {
                log.logMessageState(ProofFinderUtility.getUtf8Line(lines, reasons, lines.size() - 1,
                        trans));
            }
        }
    }

    public String getExecutionActionDescription() {
        return ProofFinderUtility.getUtf8Line(lines, reasons, lines.size() - 1, trans);
    }

    /**
     * These is the basis implementation for substitution methods.
     */
    private abstract class SubstituteBase implements Substitute {

        /** Next proof line we will work on. */
        private int next = 0;

        /** Weight of proof method. */
        private final int weight;

        /** Order of proof method. */
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
