/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2010,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.logic.model;

import java.util.ArrayList;
import java.util.List;

import org.qedeq.kernel.base.list.Element;
import org.qedeq.kernel.base.list.ElementList;
import org.qedeq.kernel.bo.logic.wf.Operators;


/**
 * This class calculates a new truth value for a given formula for a given interpretation.
 *
 * @author  Michael Meyling
 */
public final class Interpretation {

    /** List of subject variables. */
    private List subjectVariables;

    /** List of predicate variables. */
    private List predicateVariables;

    private Model model;

    
    /**
     * Constructor.
     */
    public Interpretation() {
        predicateVariables = new ArrayList();
        subjectVariables = new ArrayList();
        model = new Model();
    }

/**
    public boolean getPredValue(final String identifier) {
        boolean result = false;
        if (predVars.contains(identifier)) {
            result = ((Boolean) predValues.get(predVars.indexOf(identifier))).booleanValue();
        } else {
            System.out.println("added predvar " + identifier);
            result = false;
            predVars.add(identifier);
            predValues.add(Boolean.FALSE);
        }
        return result;
    }
**/

/**
    public boolean getFormulaValue(final ElementList list) {
        final String op = list.getOperator();
        boolean result = false;
        if (Operators.PREDICATE_VARIABLE.equals(op)) {
            final String identifier = list.getElement(0).getAtom().getString() + "$" + list.size();
            if (predVars.contains(identifier)) {
                result = ((Boolean) predValues.get(predVars.indexOf(identifier))).booleanValue();
            } else {
                System.out.println("added predvar " + identifier);
                result = false;
                predVars.add(identifier);
                predValues.add(Boolean.FALSE);
            }
        }
        return result;
    }
*/

    public boolean getFormulaValue(final ElementList list) {
        final String op = list.getOperator();
        boolean result = false;
        int selection = -1;
        if (Operators.PREDICATE_VARIABLE.equals(op)) {
            final PredicateVariable var = new PredicateVariable(list.getElement(0).getAtom().getString(),
                list.size() - 1, 0);
            if (predicateVariables.contains(var)) {
                int index = predicateVariables.indexOf(var);
                selection = ((PredicateVariable) predicateVariables.get(index)).getSelection();
            } else {
                System.out.println("added predvar " + var);
                predicateVariables.add(var);
                selection = 0;
            }
            Predicate predicate = model.getPredicate(list.size(), selection);
            result = predicate.calculate(getEntities(list));
        }
        return result;
    }

    public Entity[] getEntities(final ElementList list) {
        final Entity[] result =  new Entity[list.size() - 1];    // strip first argument
        for (int i = 0; i < result.length; i++) {
            result[i] = getEntity(list.getElement(i + 1));
        }
        return result;
    }

    private Entity getEntity(final Element element) {
        final ElementList list = element.getList(); // FIXME test before
        final String op = list.getOperator();
        Entity result = null;
        int selection = -1;
        if (Operators.SUBJECT_VARIABLE.equals(op)) {
            final SubjectVariable var = new SubjectVariable(list.getElement(0).getAtom().getString(), 0);
            if (subjectVariables.contains(var)) {
                final int index = subjectVariables.indexOf(var);
                selection = ((SubjectVariable) subjectVariables.get(index)).getSelection();
            } else {
                System.out.println("added subject variable " + var);
                selection = 0;
                subjectVariables.add(var);
            }
            result = model.getEntity(selection);
        }
        return result;
    }

    public boolean iterationIsNotFinished() {
        return !(predicateVariables.isEmpty() && subjectVariables.isEmpty());
    }

    /**
     * Change to next valuation.
     */
    public void iterate() {
        if (predicateVariables.isEmpty() && subjectVariables.isEmpty()) {
            System.out.println("empty");
            return;
        }
        System.out.println("iterate");
        for (int i = subjectVariables.size() - 1; i >= -1; i--) {
            if (i < 0) {
                subjectVariables.clear();
                break;
            }
            final SubjectVariable var = (SubjectVariable) subjectVariables.get(i);
            int selection = var.getSelection() + 1;
            if (selection < model.getEntitiesSize()) {
                var.setSelection(selection);
                break;
            } else {
                var.setSelection(0);
            }
        }
        for (int i = predicateVariables.size() - 1; i >= -1; i--) {
            if (i < 0) {
                predicateVariables.clear();
                break;
            }
            final PredicateVariable var = (PredicateVariable) predicateVariables.get(i);
            int selection = var.getSelection() + 1;
            if (selection < model.getPredicateSize(var.getArgumentNumber())) {
                var.setSelection(selection);
                break;
            } else {
                var.setSelection(0);
            }
        }
    }

    public String toString() {
        if (!iterationIsNotFinished()) {
            return "{}";
        }
        final StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        for (int i = 0; i < predicateVariables.size(); i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(predicateVariables.get(i));
        }
        buffer.append("}");
        buffer.append("{");
        for (int i = 0; i < subjectVariables.size(); i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(subjectVariables.get(i));
        }
        buffer.append("}");
        return buffer.toString();
    }



}
