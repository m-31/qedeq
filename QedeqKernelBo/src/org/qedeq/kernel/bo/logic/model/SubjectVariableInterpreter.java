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

    /** List of subject variables allocations. */
    private List subjectVariableAllocations;

    /** List of counters for subject variables. */
    private List subjectVariableCounters;

    /** Model contains entities. */
    private Model model;

    private double oldOrder = 0;

    private int delta;

    /**
     * Constructor.
     *
     * @param   model   Model we work on.
     */
    public SubjectVariableInterpreter(final Model model) {
        this.model = model;
        subjectVariableAllocations = new ArrayList();
        subjectVariableCounters = new ArrayList();
    }

    /**
     * Change to next valuation.
     *
     * @return  Is there a next new valuation?
     */
    public synchronized boolean next() {
        checkOrder();
        boolean next = true;
        for (int i = subjectVariableAllocations.size() - 1; i >= -1; i--) {
            if (i < 0) {
                next = false;
                break;
            }
            final SubjectVariableAllocation allocation
                = (SubjectVariableAllocation) subjectVariableAllocations.get(i);
            final Enumerator counter = (Enumerator) subjectVariableCounters.get(i);
            if (allocation.getValue() + 1 < model.getEntitiesSize()) {
                allocation.increaseNumber();
                counter.increaseNumber();
                break;
            } else {
                allocation.resetNumber();
                counter.reset();
            }
        }
        return next;
    }

    public synchronized double getCompleteness() {
        double result = 0;
        for (int i = subjectVariableCounters.size() - 1; i >= 0; i--) {
            if (!((SubjectVariableAllocation) subjectVariableAllocations.get(i)).isFixed()) {
                int c = ((Enumerator) subjectVariableCounters.get(i)).getNumber();
                if (i == 0) {
                    c += delta;
                }
                if (c > model.getEntitiesSize()) {
                    result = (result + model.getEntitiesSize() + (1 - model.getEntitiesSize() / c))
                        / (model.getEntitiesSize() + 2);
                } else {
                    result = (result + c + 1) / (model.getEntitiesSize() + 2);
                }
            }
        }
        System.out.println();
        System.out.println("SubjectVariableCompleteness: " + result);
        return result;
    }

    public synchronized void addSubjectVariable(final SubjectVariable var) {
        checkOrder();
        // FIXME 20101014 m31: just for testing
//        if (subjectVariables.contains(var)) {
//            throw new RuntimeException("variable already exists: " + var);
//        }
//        System.out.println("added subject variable " + var);
        subjectVariableAllocations.add(new SubjectVariableAllocation(var));
        subjectVariableCounters.add(new Enumerator());
        checkOrder();
    }

    /**
     * Add subject variable even if already existing.
     *
     * @param   var     Remove this subject variable.
     * @param   value   Set interpretation to this entity number.
     */
    public synchronized void forceAddSubjectVariable(final SubjectVariable var, final int value) {
        checkOrder();
        subjectVariableAllocations.add(new SubjectVariableAllocation(var, value));
        subjectVariableCounters.add(new Enumerator());
        checkOrder();
    }

    /**
     * Remove existing subject variable interpretation.
     *
     * @param   var Remove this subject variable.
     */
    public synchronized void forceRemoveSubjectVariable(final SubjectVariable var) {
        checkOrder();
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
        subjectVariableCounters.remove(index);
//        System.out.println("removed subject variable " + var);
        checkOrder();
    }
    /**
     * Remove existing subject variable interpretation.
     *
     * @param   var Remove this subject variable.
     */
    public synchronized void removeSubjectVariable(final SubjectVariable var) {
        checkOrder();
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
        subjectVariableCounters.remove(index);
        if (index > 0) {
            ((Enumerator) subjectVariableCounters.get(index - 1)).increaseNumber();
        } else {
            delta += model.getEntitiesSize() + 2;
            System.out.println("increased delta to " + delta);
        }
//        System.out.println("removed subject variable " + var);
        checkOrder();
    }

    private synchronized int getSubjectVariableSelection(final SubjectVariable var) {
        checkOrder();
        int selection = 0;
        int index = getIndex(var);
        if (index >= 0) {
            selection = ((SubjectVariableAllocation) subjectVariableAllocations.get(index))
                .getValue();
        } else {
            addSubjectVariable(var);
        }
        checkOrder();
        return selection;
    }

    public synchronized Entity getEntity(final SubjectVariable var) {
        checkOrder();
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
        checkOrder();
        final int index = getIndex(var);
        ((SubjectVariableAllocation) subjectVariableAllocations.get(index)).increaseNumber();
        ((Enumerator) subjectVariableCounters.get(index)).increaseNumber();
        checkOrder();
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

    private void checkOrder() {
        double newOrder = getCompleteness();
        if (oldOrder > newOrder) {
            System.out.println("old: " + oldOrder);
            System.out.println("new: " + newOrder);
            throw new Error("failure");
        }
        oldOrder = newOrder;
    }

    /**
     * Clear variable interpretation.
     */
    public synchronized void clear() {
        delta = 0;
        oldOrder = 0;
        subjectVariableAllocations.clear();
        subjectVariableCounters.clear();
    }

}
