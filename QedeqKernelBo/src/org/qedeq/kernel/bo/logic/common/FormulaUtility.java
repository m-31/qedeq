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

package org.qedeq.kernel.bo.logic.common;

import org.qedeq.base.utility.Enumerator;
import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.bo.module.ModuleLabels;
import org.qedeq.kernel.bo.service.Element2LatexImpl;
import org.qedeq.kernel.bo.service.Element2Utf8Impl;
import org.qedeq.kernel.se.base.list.Atom;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.list.ElementList;
import org.qedeq.kernel.se.dto.list.DefaultAtom;
import org.qedeq.kernel.se.dto.list.DefaultElementList;
import org.qedeq.kernel.se.dto.list.ElementSet;


/**
 * Some useful static methods for formulas and terms.
 *
 * @author  Michael Meyling
 */
public final class FormulaUtility implements Operators {

    /**
     * Constructor.
     *
     */
    private FormulaUtility() {
    }

    /**
     * Is {@link Element} a subject variable?
     *
     * @param   element    Element to look onto.
     * @return  Is it a subject variable?
     */
    public static final boolean isSubjectVariable(final Element element) {
        if (element == null || !element.isList() || element.getList() == null) {
            return false;
        }
        final ElementList list = element.getList();
        if (list.getOperator().equals(SUBJECT_VARIABLE)) {
            if (list.size() != 1) {
                return false;
            }
            final Element first = element.getList().getElement(0);
            if (first == null || !first.isAtom() || first.getAtom() == null) {
                return false;
            }
            final Atom atom = first.getAtom();
            if (atom.getString() == null || atom.getAtom().getString() == null
                    || atom.getString().length() == 0) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Is {@link Element} a predicate variable?
     *
     * @param   element    Element to look onto.
     * @return  Is it a predicate constant?
     */
    public static final boolean isPredicateVariable(final Element element) {
        return isOperator(PREDICATE_VARIABLE, element);
    }

    /**
     * Is {@link Element} a function variable?
     *
     * @param   element    Element to look onto.
     * @return  Is it a function variable?
     */
    public static final boolean isFunctionVariable(final Element element) {
        return isOperator(FUNCTION_VARIABLE, element);
    }

    /**
     * Is {@link Element} a predicate constant?
     *
     * @param   element    Element to look onto.
     * @return  Is it a predicate constant?
     */
    public static final boolean isPredicateConstant(final Element element) {
        return isOperator(PREDICATE_CONSTANT, element);
    }

    /**
     * Is {@link Element} a function constant?
     *
     * @param   element    Element to look onto.
     * @return  Is it a function constant?
     */
    public static final boolean isFunctionConstant(final Element element) {
        return isOperator(FUNCTION_CONSTANT, element);
    }

    /**
     * Is the given element an list with given operator and has as first element an non empty
     * string atom?
     *
     * @param   operator    Operator.
     * @param   element     Check this element.
     * @return  Check successful?
     */
    private static boolean isOperator(final String operator,
            final Element element) {
        return isOperator(operator, element, 0);
    }

    /**
     * Is the given element an list with given operator and has as first element an non empty
     * string atom?
     *
     * @param   operator        Operator.
     * @param   element         Check this element.
     * @param   minArguments    Minimum number of arguments (beside the first string atom).
     * @return  Check successful?
     */
    private static boolean isOperator(final String operator,
            final Element element, final int minArguments) {
        if (element == null || !element.isList() || element.getList() == null) {
            return false;
        }
        final ElementList list = element.getList();
        if (list.getOperator().equals(operator)) {
            if (list.size() < 1 + minArguments) {
                return false;
            }
            final Element first = element.getList().getElement(0);
            if (first == null || !first.isAtom() || first.getAtom() == null) {
                return false;
            }
            final Atom atom = first.getAtom();
            if (atom.getString() == null || atom.getAtom().getString() == null
                    || atom.getString().length() == 0) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Return all free subject variables of an element.
     *
     * @param   element    Work on this element.
     * @return  All free subject variables.
     */
    public static final ElementSet getFreeSubjectVariables(final Element element) {
        final ElementSet free = new ElementSet();
        if (isSubjectVariable(element)) {
            free.add(element);
        } else if (element.isList()) {
            final ElementList list = element.getList();
            if (isBindingOperator(list)) {
                for (int i = 1; i < list.size(); i++) {
                    free.union(getFreeSubjectVariables(list.getElement(i)));
                }
                free.remove(list.getElement(0));
            } else {
                for (int i = 0; i < list.size(); i++) {
                    free.union(getFreeSubjectVariables(list.getElement(i)));
                }
            }
        }
        return free;
    }

    /**
     * Return all bound subject variables of an element.
     *
     * @param   element    Work on this element.
     * @return  All bound subject variables.
     */
    public static final ElementSet getBoundSubjectVariables(final Element element) {
        final ElementSet bound = new ElementSet();
        if (element.isList()) {
            ElementList list = element.getList();
            // if operator is quantifier or class term
            if (isBindingOperator(list)) {
                // add subject variable to bound list
                bound.add(list.getElement(0));
                // add all bound variables of sub-elements
                for (int i = 1; i < list.size(); i++) {
                    bound.union(getBoundSubjectVariables(
                        list.getElement(i)));
                }
            } else {
                // add all bound variables of sub-elements
                for (int i = 0; i < list.size(); i++) {
                    bound.union(getBoundSubjectVariables(list.getElement(i)));
                }
            }
        }
        return bound;
    }

    /**
     * Return all subject variables of an element.
     *
     * @param   element    Work on this element.
     * @return  All subject variables.
     */
    public static final ElementSet getSubjectVariables(final Element element) {
        final ElementSet all = new ElementSet();
        if (isSubjectVariable(element)) {
            all.add(element);
        } else if (element.isList()) {
            final ElementList list = element.getList();
            for (int i = 1; i < list.size(); i++) {
                all.union(getSubjectVariables(list.getElement(i)));
            }
        }
        return all;
    }

    /**
     * Return all predicate variables of an element. The arguments are normalized to subject
     * variables "x_1", "x_2" and so on.
     *
     * @param   element    Work on this element.
     * @return  All normalized predicate variables of that formula.
     */
    public static final ElementSet getPredicateVariables(final Element element) {
        final ElementSet all = new ElementSet();
        if (isPredicateVariable(element)) {
            final ElementList pred = element.getList();
            final DefaultElementList normalized = new DefaultElementList(pred.getOperator());
            normalized.add(pred.getElement(0));
            for (int i = 1; i < pred.size(); i++) {
                normalized.add(createSubjectVariable("x_" + i));
            }
            all.add(element);
        } else if (element.isList()) {
            final ElementList list = element.getList();
            for (int i = 0; i < list.size(); i++) {
                all.union(getPredicateVariables(list.getElement(i)));
            }
        }
        return all;
    }

    /**
     * Has the given list an operator that binds a subject variable?
     *
     * @param   list    Check for this operator.
     * @return  Has it an binding operator with subject variable?
     */
    public static boolean isBindingOperator(final ElementList list) {
        final String operator = list.getOperator();
        if (operator == null || list.size() <= 0 || !isSubjectVariable(list.getElement(0))) {
            return false;
        }
        return operator.equals(EXISTENTIAL_QUANTIFIER_OPERATOR)
                || operator.equals(UNIQUE_EXISTENTIAL_QUANTIFIER_OPERATOR)
                || operator.equals(UNIVERSAL_QUANTIFIER_OPERATOR)
                || operator.equals(CLASS_OP);
    }

    /**
     * Get relative context of the first difference between the given elements.
     *
     * @param   first   First element.
     * @param   second  Second element.
     * @return  Relative path (beginning with ".") of first difference.
     */
    public static String getDifferenceLocation(final Element first, final Element second) {
        final StringBuffer diff = new StringBuffer();
        equal(diff, first, second);
        return diff.toString();
    }

    /**
     * Is there any difference between the two elements? If so, the context has the difference
     * position.
     *
     * @param   firstContext    Context (might be relative) for first element.
     * @param   first           First element.
     * @param   second          Second element.
     * @return  Are both elements equal?
     */
    private static boolean equal(final StringBuffer firstContext,
            final Element first, final Element second) {
        if (first == null) {
            return second == null;
        }
        if (first.isAtom()) {
            if (!second.isAtom()) {
                return false;
            }
            return EqualsUtility.equals(first.getAtom().getString(), second.getAtom().getString());
        }
        if (second.isAtom()) {
            return false;
        }
        if (!EqualsUtility.equals(first.getList().getOperator(), second.getList().getOperator())) {
            return false;
        }
        if (first.getList().size() != second.getList().size()) {
            return false;
        }
        for (int i = 0; i < first.getList().size(); i++) {
            final int length = firstContext.length();
            firstContext.append(".getList().getElement(" + i + ")");
            if (!equal(firstContext, first.getList().getElement(i),
                    second.getList().getElement(i))) {
                return false;
            }
            firstContext.setLength(length);
        }
        return true;
    }

    /**
     * Replace bound subject variables at given occurrence by another one.
     * If goal occurrence number is below number of occurrences within the formula nothing is
     * changed.
     *
     * @param   originalSubjectVariable     Replace this subject variable.
     * @param   replacementSubjectVariable  By this subject variable.
     * @param   formulaElement              This is the formula we work on.
     * @param   occurrenceGoal              This is the binding occurrence number.
     * @param   occurreneCurrent            Counter how much occurrences we already had.
     * @return  Formula with replaced subject variables.
     */
    public static Element replaceSubjectVariableQuantifier(final Element originalSubjectVariable,
            final Element replacementSubjectVariable, final Element formulaElement,
            final int occurrenceGoal, final Enumerator occurreneCurrent) {
        if (formulaElement.isAtom()) {
            return formulaElement.copy();
        }
        final ElementList formula = formulaElement.getList();
        if (occurreneCurrent.getNumber() > occurrenceGoal) {
            return formula.copy();
        }
        final ElementList result = new DefaultElementList(formula.getOperator());
        if (isBindingOperator(formula)
                && formula.getElement(0).equals(originalSubjectVariable)) {
            occurreneCurrent.increaseNumber();
            System.out.println("found: " + occurreneCurrent);
            if (occurrenceGoal == occurreneCurrent.getNumber()) {
                System.out.println("match: " + occurrenceGoal);
                return formula.replace(originalSubjectVariable,
                    replacementSubjectVariable);
            }
        }
        for (int i = 0; i < formula.size(); i++) {
            result.add(replaceSubjectVariableQuantifier(originalSubjectVariable,
                replacementSubjectVariable, formula.getElement(i), occurrenceGoal,
                occurreneCurrent));
        }
        return result;
    }

    /**
     * Replace function or predicate variable by given term or formula.
     *
     * @param   formula             Formula we want the replacement take place.
     * @param   operatorVariable    Predicate variable or function variable with only subject
     *                              variables as arguments.
     * @param   replacement         Replacement formula or term with matching subject variables.
     *                              <code>operatorVariable</code> might have some subject variables
     *                              that are not in <code>replacement</code> and vice versa.
     * @return  Formula where operatorVariable was replaced.
     */
    public static Element replaceOperatorVariable(final Element formula,
            final Element operatorVariable, final Element replacement) {
        if (formula.isAtom() || operatorVariable.isAtom() || replacement.isAtom()) {
            return formula.copy();
        }
        final ElementList f = formula.getList();
        final ElementList ov = operatorVariable.getList();
        final ElementList r = replacement.getList();
        // check elementary preconditions
//        System.out.println("ov.size=" + ov.size());
        if (f.size() < 1 || ov.size() < 1) {
//            System.out.println("failed elementary conditions");
            return formula.copy();
        }
        // construct replacement formula with meta variables
        Element rn = r;
        for (int i = 1; i < ov.size(); i++) {
            rn = rn.replace(ov.getElement(i), createMeta(ov.getElement(i)));
        }
        return replaceOperatorVariableMeta(formula, operatorVariable, rn);
    }

    /**
     * Replace function or predicate variable by given term or formula.
     *
     * @param   formula             Formula we want the replacement take place.
     * @param   operatorVariable    Predicate variable or function variable with only subject
     *                              variables as arguments.
     * @param   replacement         Replacement formula or term with meta variables instead of
     *                              subject variables to replace (matching
     *                              <code>operatorVariable</code>).
     * @return  Formula where operatorVariable was replaced.
     */
    private static Element replaceOperatorVariableMeta(final Element formula,
            final Element operatorVariable, final Element replacement) {
        if (formula.isAtom() || operatorVariable.isAtom() || replacement.isAtom()) {
            return formula.copy();
        }
        final ElementList f = formula.getList();
        final ElementList ov = operatorVariable.getList();
        final ElementList r = replacement.getList();
        if (f.size() < 1 || ov.size() < 1) {
            return formula.copy();
        }
        ElementList result;
        if (f.getOperator() == ov.getOperator() && f.size() == ov.size()
                && f.getElement(0).equals(ov.getElement(0))) {
            // replace meta variables by matching entries
            result = r;
            for (int i = 1; i < ov.size(); i++) {
                result = (ElementList) result.replace(createMeta(ov.getElement(i)),
                    replaceOperatorVariableMeta(f.getElement(i), operatorVariable, replacement));
            }
        } else {
            result = new DefaultElementList(f.getOperator());
            for (int i = 0; i < f.size(); i++) {
                result.add(replaceOperatorVariableMeta(f.getElement(i), operatorVariable,
                    replacement));
            }
        }
        return result;
    }

    /**
     * Test if operator occurrence in formula matches always to a formula that contains no subject
     * variable that is in the given ElementSet of bound subject variables..
     *
     * @param   formula             Formula we want to test the condition.
     * @param   operatorVariable    Predicate variable or function variable with only subject
     *                              variables as arguments.
     * @param   bound               Set of subject variables that are tabu.
     * @return  Formula test was successful.
     */
    public static boolean testOperatorVariable(final Element formula,
            final Element operatorVariable, final ElementSet bound) {
        if (formula.isAtom() || operatorVariable.isAtom()) {
            return true;
        }
        final ElementList f = formula.getList();
        final ElementList ov = operatorVariable.getList();
        if (f.size() < 1 || ov.size() < 1) {
            return true;
        }
        boolean ok = true;
        if (f.getOperator() == ov.getOperator() && f.size() == ov.size()
                && f.getElement(0).equals(ov.getElement(0))) {
            if (!getSubjectVariables(f).intersection(bound).isEmpty()) {
                return false;
            }
        } else {
            for (int i = 0; ok && i < f.size(); i++) {
                ok = testOperatorVariable(f.getElement(i), operatorVariable, bound);
            }
        }
        return ok;
    }

    /**
     * Is the given formula a simple implication like A =&gt; B.
     *
     * @param   formula Check this formula.
     * @return  Is the formula a simple implication?
     */
    public static boolean isImplication(final Element formula) {
        if (formula.isAtom()) {
            return false;
        }
        final ElementList f = formula.getList();
        return Operators.IMPLICATION_OPERATOR.equals(f.getList().getOperator())
            && f.getList().size() == 2;
    }

    /**
     * Create meta variable for subject variable.
     *
     * @param   subjectVariable Subject variable, we want to have a meta variable for.
     * @return  Resulting meta variable. If we didn't got a subject variable we just
     *          return the original.
     */
    public static Element createMeta(final Element subjectVariable) {
        if (!isSubjectVariable(subjectVariable)) {
            return subjectVariable.copy();
        }
        final DefaultElementList result = new DefaultElementList(META_VARIABLE);
        result.add(subjectVariable.getList().getElement(0).copy());
        return result;
    }

    /**
     * Create subject variable out of variable name.
     *
     * @param   subjectVariableName     Subject variable name.
     * @return  Resulting subject variable.
     */
    public static Element createSubjectVariable(final String subjectVariableName) {
        final DefaultElementList result = new DefaultElementList(SUBJECT_VARIABLE);
        result.add(new DefaultAtom(subjectVariableName));
        return result;
    }

    /**
     * Create predicate variable out of variable name with no further arguments.
     *
     * @param   predicateVariableName     Predicate variable name.
     * @return  Resulting predicate variable.
     */
    public static Element createPredicateVariable(final String predicateVariableName) {
        final DefaultElementList result = new DefaultElementList(PREDICATE_VARIABLE);
        result.add(new DefaultAtom(predicateVariableName));
        return result;
    }

    public static void print(final Element element) {
        ModuleLabels labels = new ModuleLabels();
        Element2LatexImpl converter = new Element2LatexImpl(labels);
        Element2Utf8Impl textConverter = new Element2Utf8Impl(converter);
        System.out.print(textConverter.getUtf8(element));
    }

    public static void println(final Element element) {
        ModuleLabels labels = new ModuleLabels();
        Element2LatexImpl converter = new Element2LatexImpl(labels);
        Element2Utf8Impl textConverter = new Element2Utf8Impl(converter);
        System.out.println(textConverter.getUtf8(element));
    }

}