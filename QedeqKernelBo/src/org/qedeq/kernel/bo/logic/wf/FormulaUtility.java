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

import org.qedeq.kernel.bo.logic.common.Operators;
import org.qedeq.kernel.se.base.list.Atom;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.list.ElementList;
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
            final String operator = list.getOperator();
            if (operator.equals(EXISTENTIAL_QUANTIFIER_OPERATOR)
                || operator.equals(UNIQUE_EXISTENTIAL_QUANTIFIER_OPERATOR)
                || operator.equals(UNIVERSAL_QUANTIFIER_OPERATOR)
                || operator.equals(CLASS_OP)) {
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
            final String operator = list.getOperator();
            // if operator is quantifier or class term
            if (operator.equals(EXISTENTIAL_QUANTIFIER_OPERATOR)
                    || operator.equals(UNIQUE_EXISTENTIAL_QUANTIFIER_OPERATOR)
                    || operator.equals(UNIVERSAL_QUANTIFIER_OPERATOR)
                    || operator.equals(CLASS_OP)) {
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

}
