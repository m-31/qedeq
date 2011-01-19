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

package org.qedeq.kernel.bo.logic;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.list.ElementList;


/**
 * This class represents a set of {@link Element}s.
 *
 * @version $Revision: 1.1 $
 */
public class EqualFormulaSet {

    /** Here are the formulas stored. */
    private final Set formulas;

    /** Error message: NullPointer as element is not allowed. */
    private static final String NULLPOINTER_AS_WORD_IS_NOT_ALLOWED
        = "NullPointer as element is not allowed";

    /** Error message: NullPointer as set is not allowed. */
    private static final String NULLPOINTER_AS_SET_IS_NOT_ALLOWED
        = "NullPointer as set is not allowed";


    /**
     * Constructs an empty element set.
     *
     */
    public EqualFormulaSet() {
        this.formulas = new HashSet();
    }

    /**
     * Constructs an element set.
     *
     * @param  formulas the elements to put into the set
     * @throws IllegalArgumentException if <code>elements</code> was a
     *         NullPointer
     */
    public EqualFormulaSet(final EqualFormula[] formulas) {
        if (formulas == null) {
            throw new IllegalArgumentException(
                "NullPointer as element array is not allowed");
        }
        this.formulas = new HashSet(Arrays.asList(formulas));
    }

    /**
     * Constructs an equal formulas set.
     *
     * @param  set  Contains the elements to put into the set.
     * @throws IllegalArgumentException <code>set</code> was a
     *         NullPointer.
     */
    EqualFormulaSet(final EqualFormulaSet set) {
        if (set == null) {
            throw new IllegalArgumentException(
                NULLPOINTER_AS_SET_IS_NOT_ALLOWED);
        }
        this.formulas = new HashSet(set.formulas);
    }

    /**
     * Constructs an element set from all operands of an element.
     * The element must not be a symbol.
     *
     * @param  element  Contains the elements to put into the set
     * (without the operator).
     * @throws IllegalArgumentException <code>element</code> was a
     *         NullPointer or was an atom.
     */
    public EqualFormulaSet(final ElementList element) {
        if (element == null) {
            throw new IllegalArgumentException(NULLPOINTER_AS_WORD_IS_NOT_ALLOWED);
        }
        if (element.isAtom()) {
            throw new IllegalArgumentException(
                "atom as element is not allowed");
        }
        List list = element.getElements();
        this.formulas = new HashSet();
        for (int i = 0; i < list.size(); i++) {
            this.formulas.add(new EqualFormula((Element) list.get(i)));
        }
    }

    /**
     * Is element in set?
     *
     * @param   formula Element to check for.
     * @return  Is <code>formula</code> in this set?
     * @throws  IllegalArgumentException <code>element</code> was a
     *          NullPointer.
     */
    public final boolean contains(final EqualFormula formula) {
        if (formula == null) {
            throw new IllegalArgumentException(NULLPOINTER_AS_WORD_IS_NOT_ALLOWED);
        }
        return this.formulas.contains(formula);
    }

    /**
     * Is set empty?
     *
     * @return  Is this set empty?
     */
    public final boolean isEmpty() {
        return formulas.isEmpty();
    }

    /**
     * Is <code>set</code> a subset of this set?
     *
     * @param   set    set to check for.
     * @return  is <code>set</code> a subset of this set?
     * @throws  IllegalArgumentException <code>set</code> was a
     *          NullPointer
     */
    public final boolean isSubset(final EqualFormulaSet set) {
        if (set == null) {
            throw new IllegalArgumentException(NULLPOINTER_AS_SET_IS_NOT_ALLOWED);
        }
        return this.formulas.containsAll(set.formulas);
    }

    /**
     * Add a formula to set. This object is after the method the
     * union of this set with <code>element</code>
     *
     * @param   formula FormulaOrTerm to put into the set.
     * @return  <code>this</code>.
     * @throws  IllegalArgumentException <code>formula</code> was a
     *          NullPointer.
     */
    public final EqualFormulaSet add(final EqualFormula formula) {
        if (formula == null) {
            throw new IllegalArgumentException(NULLPOINTER_AS_WORD_IS_NOT_ALLOWED);
        }
        formulas.add(formula);
        return this;
    }

    /**
     * After this method this object is the union of the two sets.
     *
     * @param   set    Add all formulas that are here in.
     * @return  <code>this</code>.
     * @throws  IllegalArgumentException    <code>set</code> was a NullPointer.
     */
    public final EqualFormulaSet union(final EqualFormulaSet set) {
        if (set == null) {
            throw new IllegalArgumentException(NULLPOINTER_AS_SET_IS_NOT_ALLOWED);
        }
        formulas.addAll(set.formulas);
        return this;
    }

    /**
     * Remove an element from set.
     *
     * @param   formula FormulaOrTerm to remove from the set
     * @return  <code>this</code>.
     * @throws  IllegalArgumentException    <code>formula</code> was a NullPointer.
     */
    public final EqualFormulaSet remove(final EqualFormula formula) {
        if (formula == null) {
            throw new IllegalArgumentException(NULLPOINTER_AS_WORD_IS_NOT_ALLOWED);
        }
        formulas.remove(formula);
        return this;
    }

    /**
     * Remove elements from another {@link EqualFormulaSet} from this set.
     * After this method this object is the asymmetric set difference of the
     * two sets: <code>this</code> \ <code>set</code>.
     *
     * @param   set    Remove all elements that are in this set from
     *                 <code>this</code>.
     * @return  Was <code>this</code> set changed?
     * @throws  IllegalArgumentException    <code>set</code> was a
     *          NullPointer
     */
    public final EqualFormulaSet minus(final EqualFormulaSet set) {
        if (set == null) {
            throw new IllegalArgumentException(NULLPOINTER_AS_SET_IS_NOT_ALLOWED);
        }
        this.formulas.removeAll(set.formulas);
        return this;
    }

    /**
     * Build the intersection.
     *
     * @param   set    Check for these elements.
     * @return  Has <code>this</code> set changed?
     * @throws  IllegalArgumentException <code>set</code> was a
     *          NullPointer
     */
    public final EqualFormulaSet intersection(final EqualFormulaSet set) {
        if (set == null) {
            throw new IllegalArgumentException(NULLPOINTER_AS_SET_IS_NOT_ALLOWED);
        }
        this.formulas.retainAll(set.formulas);
        return this;
    }

    /**
     * Build a new intersection.
     *
     * @param   set    check for these elements
     * @return  was <code>this</code> set changed?
     * @throws  IllegalArgumentException if the set was a
     *          NullPointer
     */
    public final EqualFormulaSet newIntersection(final EqualFormulaSet set) {
        if (set == null) {
            throw new IllegalArgumentException(
                NULLPOINTER_AS_SET_IS_NOT_ALLOWED);
        }
        final EqualFormulaSet result = new EqualFormulaSet(this);
        result.formulas.retainAll(set.formulas);
        return result;
    }

    public final boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() == EqualFormulaSet.class) {
            return this.formulas.equals(((EqualFormulaSet) obj).formulas);
        }
        return false;
    }

    public final int hashCode() {
        return formulas.hashCode();
    }

    public final String toString() {
        final StringBuffer result = new StringBuffer();
        result.append("{");
        final Iterator iterator = formulas.iterator();
        while (iterator.hasNext()) {
            result.append(iterator.next());
            if (iterator.hasNext()) {
                result.append(", ");
            }
        }
        result.append("}");
        return result.toString();
    }

}
