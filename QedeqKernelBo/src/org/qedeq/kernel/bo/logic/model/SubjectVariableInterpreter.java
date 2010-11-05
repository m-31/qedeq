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

/**
 * This class interpretation.
 *
 * @author  Michael Meyling
 */
public final class SubjectVariableInterpreter {

    /** List of subject variables allocations. */
    private List subjectVariableAllocations;

    /** Model contains entities. */
    private Model model;

    /**
     * Constructor.
     *
     * @param   model   Model we work on.
     */
    public SubjectVariableInterpreter(final Model model) {
        this.model = model;
        subjectVariableAllocations = new ArrayList();
    }

    /**
     * Change to next valuation.
     *
     * @return  Is there a next new valuation?
     */
    public synchronized boolean next() {
        boolean next = true;
        for (int i = subjectVariableAllocations.size() - 1; i >= -1; i--) {
            if (i < 0) {
                next = false;
                break;
            }
            final SubjectVariableAllocation allocation
                = (SubjectVariableAllocation) subjectVariableAllocations.get(i);
            if (allocation.getValue() + 1 < model.getEntitiesSize()) {
                allocation.increaseNumber();
                break;
            } else {
                allocation.resetNumber();
            }
        }
        return next;
    }

    /**
     * Add subject variable. This is usually done for interpreting a quantifier.
     *
     * @param   var Subject variable to add to our interpretation.
     */
    public synchronized void addSubjectVariable(final SubjectVariable var) {
        // FIXME 20101014 m31: just for testing
//        if (subjectVariables.contains(var)) {
//            throw new RuntimeException("variable already exists: " + var);
//        }
//        System.out.println("added subject variable " + var);
        subjectVariableAllocations.add(new SubjectVariableAllocation(var));
    }

    /**
     * Add subject variable even if already existing.
     *
     * @param   var     Remove this subject variable.
     * @param   value   Set interpretation to this entity number.
     */
    public synchronized void forceAddSubjectVariable(final SubjectVariable var, final int value) {
        subjectVariableAllocations.add(new SubjectVariableAllocation(var, value));
    }

    /**
     * Remove existing subject variable interpretation.
     *
     * @param   var Remove this subject variable.
     */
    public synchronized void forceRemoveSubjectVariable(final SubjectVariable var) {
        int index = getIndex(var);
        if (index < 0) {
            throw new RuntimeException("variable does not exist: " + var);
        }
        final SubjectVariableAllocation current
            = (SubjectVariableAllocation) subjectVariableAllocations.get(index);
        if (!current.isFixed()) {
            throw new RuntimeException("trying to remove not fixed allocation: " + current);
        }
        subjectVariableAllocations.remove(index);
//        System.out.println("removed subject variable " + var);
    }
    /**
     * Remove existing subject variable interpretation.
     *
     * @param   var Remove this subject variable.
     */
    public synchronized void removeSubjectVariable(final SubjectVariable var) {
        int index = getIndex(var);
        if (index < 0) {
            throw new RuntimeException("variable does not exist: " + var);
        }
        final SubjectVariableAllocation current
            = (SubjectVariableAllocation) subjectVariableAllocations.get(index);
        if (current.isFixed()) {
            throw new RuntimeException("trying to remove fixed allocation: " + current);
        }
        subjectVariableAllocations.remove(index);
//        System.out.println("removed subject variable " + var);
    }

    private synchronized int getSubjectVariableSelection(final SubjectVariable var) {
        int selection = 0;
        int index = getIndex(var);
        if (index >= 0) {
            selection = ((SubjectVariableAllocation) subjectVariableAllocations.get(index))
                .getValue();
        } else {
            addSubjectVariable(var);
        }
        return selection;
    }

    public synchronized Entity getEntity(final SubjectVariable var) {
        return model.getEntity(getSubjectVariableSelection(var));
    }

    private int getIndex(final SubjectVariable var) {
        int index = -1;
        for (index = subjectVariableAllocations.size() - 1; index >= 0; index--) {
            final SubjectVariableAllocation current
                = (SubjectVariableAllocation) subjectVariableAllocations.get(index);
            if (var.equals(current.getVariable())) {
                break;
            }
        }
        return index;
    }

    public synchronized void increaseSubjectVariableSelection(final SubjectVariable var) {
        ((SubjectVariableAllocation) subjectVariableAllocations.get(getIndex(var))).increaseNumber();
    }

    public synchronized String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("subject variables {");
        for (int i = 0; i < subjectVariableAllocations.size(); i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            SubjectVariableAllocation var = (SubjectVariableAllocation) subjectVariableAllocations.get(i);
            buffer.append(var.getVariable());
            buffer.append("=");
            buffer.append(model.getEntity(var.getValue()));
        }
        buffer.append("}");
        return buffer.toString();
    }

    /**
     * Clear variable interpretation.
     */
    public synchronized void clear() {
        subjectVariableAllocations.clear();
    }

}
