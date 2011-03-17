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

package org.qedeq.kernel.bo.logic.wf;

import org.qedeq.base.utility.Enumerator;
import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.bo.logic.common.Operators;
import org.qedeq.kernel.se.base.list.Atom;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.list.ElementList;
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
     * Is {@link Element} a predicate constant?
     *
     * @param   element    Element to look onto.
     * @return  Is it a predicate constant?
     */
    public static final boolean isPredicateConstant(final Element element) {
        if (element == null || !element.isList() || element.getList() == null) {
            return false;
        }
        final ElementList list = element.getList();
        if (list.getOperator().equals(PREDICATE_CONSTANT)) {
            if (list.size() < 1) {
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
     * Is {@link Element} a function constant?
     *
     * @param   element    Element to look onto.
     * @return  Is it a function constant?
     */
    public static final boolean isFunctionConstant(final Element element) {
        if (element == null || !element.isList() || element.getList() == null) {
            return false;
        }
        final ElementList list = element.getList();
        if (list.getOperator().equals(FUNCTION_CONSTANT)) {
            if (list.size() < 1) {
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

    public static Element replaceSubjectVariableQuantifier(final Element originalSubjectVariable,
            final Element replacementSubjectVariable, final Element formula,
            final int occurrenceGoal, final Enumerator occurreneCurrent) {
        if (formula.isAtom()) {
            return formula.copy();
        }
        return replaceSubjectVariableQuantifier(originalSubjectVariable,
            replacementSubjectVariable, formula.getList(), occurrenceGoal,
            occurreneCurrent);
    }

    private static Element replaceSubjectVariableQuantifier(final Element originalSubjectVariable,
            final Element replacementSubjectVariable, final ElementList formula,
            final int occurrenceGoal, final Enumerator occurrenceCurrent) {
        if (occurrenceCurrent.getNumber() > occurrenceGoal) {
            return formula.copy();
        }
        final ElementList result = new DefaultElementList(formula.getOperator());
        if (isBindingOperator(formula)
                && formula.getElement(0).equals(originalSubjectVariable)) {
            occurrenceCurrent.increaseNumber();
            System.out.println("found: " + occurrenceCurrent);
            if (occurrenceGoal == occurrenceCurrent.getNumber()) {
                System.out.println("match: " + occurrenceGoal);
                return formula.replace(originalSubjectVariable,
                    replacementSubjectVariable);
            }
        }
        for (int i = 0; i < formula.size(); i++) {
            result.add(replaceSubjectVariableQuantifier(originalSubjectVariable,
                replacementSubjectVariable, formula.getElement(i), occurrenceGoal,
                occurrenceCurrent));
        }
        return result;
    }

}
