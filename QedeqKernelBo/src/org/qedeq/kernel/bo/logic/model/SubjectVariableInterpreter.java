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

import org.qedeq.base.utility.Enumerator;


/**
 * This class interpretation.
 *
 * @author  Michael Meyling
 */
public final class SubjectVariableInterpreter {

    /** List of subject variables. */
    private List subjectVariables;

    /** List of counters for subject variables. */
    private List subjectVariableCounters;

    /** Model contains entities. */
    private Model model;

    /** Counter for next calls for one cycle. */
    private double counter;

    /**
     * Constructor.
     *
     * @param   model   Model we work on.
     */
    public SubjectVariableInterpreter(final Model model) {
        this.model = model;
        subjectVariables = new ArrayList();
        subjectVariableCounters = new ArrayList();
    }

    /**
     * Change to next valuation.
     *
     * @return  Is there a next new valuation?
     */
    public boolean next() {
        counter++;
        boolean next = true;
        for (int i = subjectVariables.size() - 1; i >= -1; i--) {
            if (i < 0) {
                next = false;
                counter = 0;
                break;
            }
            Enumerator number = (Enumerator) subjectVariableCounters.get(i);
            if (number.getNumber() + 1 < model.getEntitiesSize()) {
                number.increaseNumber();
                break;
            } else {
                number.reset();
            }
        }
        return next;
    }

    public double getCompleteness() {
        double result = counter + 1;
        for (int i = 0; i < subjectVariableCounters.size(); i++) {
            result /= model.getEntitiesSize();
        }
//        System.out.println("SubjectVariableCompleteness: " + result);
        return result;
    }

    public void addSubjectVariable(final SubjectVariable var) {

        // FIXME 20101014 m31: just for testing
//        if (subjectVariables.contains(var)) {
//            throw new RuntimeException("variable already exists: " + var);
//        }
//        System.out.println("added subject variable " + var);
        subjectVariables.add(var);
        subjectVariableCounters.add(new Enumerator());
    }

    /**
     * Add subject variable even if already existing.
     *
     * @param   var     Remove this subject variable.
     * @param   value   Set interpretation to this entity number.
     */
    public void forceAddSubjectVariable(final SubjectVariable var, final int value) {
        subjectVariables.add(var);
        subjectVariableCounters.add(new Enumerator(value));
    }

    /**
     * Remove existing subject variable interpretation.
     *
     * @param   var Remove this subject variable.
     */
    public void removeSubjectVariable(final SubjectVariable var) {
        final int index = subjectVariables.lastIndexOf(var);
        if (index < 0) {
            throw new RuntimeException("variable does not exist: " + var);
        }
//        System.out.println("removed subject variable " + var);
        subjectVariables.remove(index);
        subjectVariableCounters.remove(index);
    }

    private int getSubjectVariableSelection(final SubjectVariable var) {
        int selection;
        if (subjectVariables.contains(var)) {
            final int index = subjectVariables.lastIndexOf(var);
            selection = ((Enumerator) subjectVariableCounters.get(index)).getNumber();
        } else {
//            System.out.println("added subject variable " + var);
            selection = 0;
            subjectVariables.add(var);
            subjectVariableCounters.add(new Enumerator());
        }
        return selection;
    }

    public Entity getEntity(final SubjectVariable var) {
        return model.getEntity(getSubjectVariableSelection(var));

    }

    public void increaseSubjectVariableSelection(final SubjectVariable var) {
        final int index = subjectVariables.lastIndexOf(var);
        ((Enumerator) subjectVariableCounters.get(index)).increaseNumber();
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("subject variables {");
        for (int i = 0; i < subjectVariables.size(); i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            SubjectVariable var = (SubjectVariable) subjectVariables.get(i);
            buffer.append(subjectVariables.get(i));
            buffer.append("=");
            buffer.append(getEntity(var));
        }
        buffer.append("}");
        return buffer.toString();
    }

    /**
     * Clear variable interpretation.
     */
    public void clear() {
        subjectVariables.clear();
        subjectVariableCounters.clear();
    }

}
