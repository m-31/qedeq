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

    /** List of non counting subject variables. Contains Booleans*/
    private List forcedSubjectVariableCounters;

    /** Model contains entities. */
    private Model model;

    private String oldOrder = "";

    /**
     * Constructor.
     *
     * @param   model   Model we work on.
     */
    public SubjectVariableInterpreter(final Model model) {
        this.model = model;
        subjectVariables = new ArrayList();
        subjectVariableCounters = new ArrayList();
        forcedSubjectVariableCounters = new ArrayList();
    }

    /**
     * Change to next valuation.
     *
     * @return  Is there a next new valuation?
     */
    public synchronized boolean next() {
        checkOrder();
        boolean next = true;
        for (int i = subjectVariables.size() - 1; i >= -1; i--) {
            if (i < 0) {
                next = false;
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
        checkOrder();
        return next;
    }

    public synchronized double getCompleteness() {
        checkOrder();
        double result = 0;
        for (int i = subjectVariableCounters.size() - 1; i >= 0; i--) {
            if (!((Boolean) forcedSubjectVariableCounters.get(i)).booleanValue()) {
                result = (result + ((Enumerator) subjectVariableCounters.get(i)).getNumber() + 1)
                    / (model.getEntitiesSize() + 1);
            }
        }
        for (int i = 0; i < subjectVariableCounters.size(); i++) {
            if (!((Boolean) forcedSubjectVariableCounters.get(i)).booleanValue()) {
                System.out.print("" + (((Enumerator) subjectVariableCounters.get(i)).getNumber() + 1));
            }
        }
        System.out.println();
        System.out.println("SubjectVariableCompleteness: " + result);
        checkOrder();
        return result;
    }

    public synchronized void addSubjectVariable(final SubjectVariable var) {
        checkOrder();
        // FIXME 20101014 m31: just for testing
//        if (subjectVariables.contains(var)) {
//            throw new RuntimeException("variable already exists: " + var);
//        }
//        System.out.println("added subject variable " + var);
        subjectVariables.add(var);
        subjectVariableCounters.add(new Enumerator());
        forcedSubjectVariableCounters.add(Boolean.FALSE);
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
        subjectVariables.add(var);
        subjectVariableCounters.add(new Enumerator(value));
        forcedSubjectVariableCounters.add(Boolean.TRUE);
        checkOrder();
    }

    /**
     * Remove existing subject variable interpretation.
     *
     * @param   var Remove this subject variable.
     */
    public synchronized void forceRemoveSubjectVariable(final SubjectVariable var) {
        checkOrder();
        final int index = subjectVariables.lastIndexOf(var);
        if (index < 0) {
            throw new RuntimeException("variable does not exist: " + var);
        }
//        System.out.println("removed subject variable " + var);
        subjectVariables.remove(index);
        subjectVariableCounters.remove(index);
        forcedSubjectVariableCounters.remove(index);
        checkOrder();
    }
    /**
     * Remove existing subject variable interpretation.
     *
     * @param   var Remove this subject variable.
     */
    public synchronized void removeSubjectVariable(final SubjectVariable var) {
        checkOrder();
        final int index = subjectVariables.lastIndexOf(var);
        if (index < 0) {
            throw new RuntimeException("variable does not exist: " + var);
        }
//        System.out.println("removed subject variable " + var);
        subjectVariables.remove(index);
        subjectVariableCounters.remove(index);
        forcedSubjectVariableCounters.remove(index);
        checkOrder();
    }

    private synchronized int getSubjectVariableSelection(final SubjectVariable var) {
        checkOrder();
        int selection;
        if (subjectVariables.contains(var)) {
            final int index = subjectVariables.lastIndexOf(var);
            selection = ((Enumerator) subjectVariableCounters.get(index)).getNumber();
        } else {
//            System.out.println("added subject variable " + var);
            selection = 0;
            addSubjectVariable(var);
        }
        checkOrder();
        return selection;
    }

    public synchronized Entity getEntity(final SubjectVariable var) {
        checkOrder();
        return model.getEntity(getSubjectVariableSelection(var));
    }

    public synchronized void increaseSubjectVariableSelection(final SubjectVariable var) {
        checkOrder();
        final int index = subjectVariables.lastIndexOf(var);
        ((Enumerator) subjectVariableCounters.get(index)).increaseNumber();
        checkOrder();
    }

    public synchronized String toString() {
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

    private void checkOrder() {
        String newOrder = "";
        for (int i = 0; i < subjectVariableCounters.size(); i++) {
            if (!((Boolean) forcedSubjectVariableCounters.get(i)).booleanValue()) {
                newOrder += "" + (((Enumerator) subjectVariableCounters.get(i)).getNumber() + 1);
            }
        }
        if (-1 == newOrder.compareTo(oldOrder)) {
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
        oldOrder = "";
        subjectVariables.clear();
        subjectVariableCounters.clear();
        forcedSubjectVariableCounters.clear();
    }

}
