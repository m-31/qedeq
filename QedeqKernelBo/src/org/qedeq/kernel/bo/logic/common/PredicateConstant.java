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
import org.qedeq.kernel.se.base.list.ElementList;
import org.qedeq.kernel.se.common.ModuleContext;

/**
 * Predicate constant.
 *
 * @author  Michael Meyling
 */
public final class PredicateConstant {

    /** Predicate key. */
    private final PredicateKey key;

    /** Complete formula. */
    private final ElementList completeFormula;

    /** In this context the predicate was defined. */
    private final ModuleContext context;

    /** Predicate with subject variables. */
    private final ElementList predicate;

    /** Defining formula. */
    private final ElementList definingFormula;

    /** Subject variable strings. */
    private final List subjectVariables;

    /**
     * Constructor.
     *
     * @param   key             Predicate name.
     * @param   completeFormula Complete formula defining predicate. Includes predicate.
     *                          Must be like: A(x, y) <->  \forall z (z \in x <-> z \in y)
     * @param   context         Module context we are in.
     */
    public PredicateConstant(final PredicateKey key, final ElementList completeFormula,
            final ModuleContext context) {
        this.key = key;
        this.completeFormula = completeFormula;
        this.context = new ModuleContext(context);  // using copy constructor to fix context
        final ElementList list = completeFormula.getList();
        predicate =  list.getElement(0).getList();
        definingFormula =  list.getElement(1).getList();
        subjectVariables = new ArrayList(predicate.size() - 1);
        for (int i = 0; i < predicate.size() - 1; i++) {
            subjectVariables.add(new SubjectVariable(
                predicate.getElement(i + 1).getList().getElement(0).getAtom().getString()));
        }
    }

    /**
     * Get predicate key.
     *
     * @return  Predicate key.
     */
    public PredicateKey getKey() {
        return key;
    }

    /**
     * Get predicate name.
     *
     * @return  Predicate name.
     */
    public String getName() {
        return key.getName();
    }

    /**
     * Get predicate argument number.
     *
     * @return  Number of arguments.
     */
    public String getArguments() {
        return key.getArguments();
    }

    /**
     * Get complete defining formula. Includes predicate itself.
     *
     * @return  Complete defining formula.
     */
    public ElementList getCompleteFormula() {
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
     * Get predicate with parameters.
     *
     * @return  Predicate part of definition formula.
     */
    public ElementList getPredicate() {
        return predicate;
    }

    /**
     * Get parameter subject variables.
     *
     * @return  Parameter subject variables as within a list.
     */
    public List getSubjectVariables() {
        return subjectVariables;
    }

    /**
     * Get defining formula. Predicate itself is not included.
     *
     * @return  Defining formula only.
     */
    public ElementList getDefiningFormula() {
        return definingFormula;
    }

    public int hashCode() {
        return (getKey() != null ? getKey().hashCode() : 0)
            ^ (getCompleteFormula() != null ? getCompleteFormula().hashCode() : 0);
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof PredicateConstant)) {
            return false;
        }
        final PredicateConstant other = (PredicateConstant) obj;
        return EqualsUtility.equals(getKey(), other.getKey())
            && EqualsUtility.equals(getCompleteFormula(), other.getCompleteFormula());
    }

    public String toString() {
        return getName() + "[" + getArguments() + "]";
    }


}
