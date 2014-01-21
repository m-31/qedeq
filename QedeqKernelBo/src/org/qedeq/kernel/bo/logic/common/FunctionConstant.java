/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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

import java.util.ArrayList;
import java.util.List;

import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.list.ElementList;
import org.qedeq.kernel.se.common.ModuleContext;

/**
 * Function constant.
 *
 * @author  Michael Meyling
 */
public final class FunctionConstant {

    /** Predicate key. */
    private final FunctionKey key;

    /** Complete formula. */
    private final ElementList completeFormula;

    /** In this context the function was defined. */
    private final ModuleContext context;

    /** Function with subject variables. */
    private final ElementList function;

    /** Defining term. */
    private final ElementList definingTerm;

    /** Parameter subject variables. */
    private final List subjectVariables;

    /**
     * Constructor.
     *
     * @param   key             Function name.
     * @param   completeFormula Complete formula defining function. Includes function.
     *                          Must be like: f(x, y) = {z | z \in x & z \in y}
     * @param   context         Module context we are in.
     */
    public FunctionConstant(final FunctionKey key, final ElementList completeFormula,
            final ModuleContext context) {
        this.key = key;
        this.completeFormula = completeFormula;
        this.context = new ModuleContext(context);  // using copy constructor to fix context
        final ElementList list = completeFormula.getList();
        function =  list.getElement(1).getList();
        definingTerm =  list.getElement(2).getList();
        subjectVariables = new ArrayList(function.size() - 1);
        for (int i = 0; i < function.size() - 1; i++) {
            subjectVariables.add(new SubjectVariable(
                function.getElement(i + 1).getList().getElement(0).getAtom().getString()));
        }
    }

    /**
     * Get function key.
     *
     * @return  Function key.
     */
    public FunctionKey getKey() {
        return key;
    }

    /**
     * Get function name.
     *
     * @return  Function name.
     */
    public String getName() {
        return key.getName();
    }

    /**
     * Get function argument number.
     *
     * @return  Number of arguments.
     */
    public String getArguments() {
        return key.getArguments();
    }

    /**
     * Get complete defining formula. Includes function itself.
     *
     * @return  Complete defining formula.
     */
    public Element getCompleteFormula() {
        return completeFormula;
    }

    /**
     * Get context where the complete formula is.
     *
     * @return  Module context for complete formula.
     */
    public ModuleContext getContext() {
        return context;
    }
    /**
     * Get function with parameters.
     *
     * @return  Function term with subject variables.
     */
    public ElementList getFunction() {
        return function;
    }

    /**
     * Get list of parameter subject variables.
     *
     * @return  Parameter subject variables as a list.
     */
    public List getSubjectVariables() {
        return subjectVariables;
    }

    /**
     * Get defining term. Function itself is not included.
     *
     * @return  Defining term only.
     */
    public Element getDefiningTerm() {
        return definingTerm;
    }

    public int hashCode() {
        return (getKey() != null ? getKey().hashCode() : 0)
            ^ (getCompleteFormula() != null ? getCompleteFormula().hashCode() : 0);
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof FunctionConstant)) {
            return false;
        }
        final FunctionConstant other = (FunctionConstant) obj;
        return EqualsUtility.equals(getKey(), other.getKey())
            && EqualsUtility.equals(getCompleteFormula(), other.getCompleteFormula());
    }

    public String toString() {
        return getName() + "[" + getArguments() + "]";
    }


}
