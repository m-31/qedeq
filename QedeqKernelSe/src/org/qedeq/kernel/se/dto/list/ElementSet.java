/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2013,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.se.dto.list;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.list.ElementList;


/**
 * This class represents a set of {@link org.qedeq.kernel.se.base.list.Element}s.
 *
 * @author  Michael Meyling
 */
public class ElementSet {


    /** Here are the elements stored. */
    private final Set elements;


    /**
     * Constructs an empty element set.
     */
    public ElementSet() {
        this.elements = new HashSet();
    }


    /**
     * Constructs an element set.
     *
     * @param  elements the elements to put into the set
     * @throws IllegalArgumentException if <code>elements</code> was a NullPointer
     */
    public ElementSet(final Element[] elements) {
        if (elements == null) {
            throw new IllegalArgumentException(
                "NullPointer as element array is not allowed");
        }
        this.elements = new HashSet(Arrays.asList(elements));
    }


    /**
     * Constructs an element set.
     *
     * @param  set  contains the elements to put into the set
     * @throws IllegalArgumentException if <code>set</code> was a
     *         NullPointer
     */
    public ElementSet(final ElementSet set) {
        if (set == null) {
            throw new IllegalArgumentException(
                "NullPointer as set is not allowed");
        }
        this.elements = new HashSet(set.elements);
    }


    /**
     * Constructs an element set from all operands of an element.
     * The element must not be a symbol.
     *
     * @param  element  contains the elements to put into the set
     * (without the operator)
     * @throws IllegalArgumentException if <code>element</code> was a
     *         NullPointer or was an atom.
     */
    public ElementSet(final ElementList element) {
        if (element == null) {
            throw new IllegalArgumentException(
                "NullPointer as element is not allowed");
        }
        if (element.isAtom()) {
            throw new IllegalArgumentException(
                "text as element is not allowed");
        }
        this.elements = new HashSet(element.getElements());
    }


    /**
     * Is element in set?
     *
     * @param   element    Element to check for.
     * @return  Is <code>element</code> in this set?
     * @throws  IllegalArgumentException if the element was a
     *          NullPointer
     */
    public final boolean contains(final Element element) {
        if (element == null) {
            throw new IllegalArgumentException("NullPointer as element is not allowed");
        }
        return this.elements.contains(element);
    }

    /**
     * Is this set empty?
     *
     * @return  Is this set empty?
     */
    public final boolean isEmpty() {
        return elements.isEmpty();
    }


    /**
     * Is <code>set</code> a superset of this set?
     *
     * @param   set    Set to check for.
     * @return  Is this set a subset of <code>set</code>?
     * @throws  IllegalArgumentException if the set was a NullPointer
     */
    public final boolean isSubset(final ElementSet set) {
        if (set == null) {
            throw new IllegalArgumentException("NullPointer as set is not allowed");
        }
        return set.elements.containsAll(this.elements);
    }


    /**
     * Add an element to set. This object is after the method the
     * union of this set with {<code>element</code>}
     *
     * @param   element    element to put into the set
     * @return  Possibly changed <code>this</code>.
     * @throws  IllegalArgumentException if the element was a
     *          NullPointer
     */
    public final ElementSet add(final Element element) {
        if (element == null) {
            throw new IllegalArgumentException("NullPointer as element is not allowed");
        }
        elements.add(element);
        return this;
    }


    /**
     * Add elements from another {@link ElementSet} to this set.
     * After this method this object is the union of the two sets.
     *
     * @param   set    add all elements that are here
     * @return  Possibly changed <code>this</code>.
     * @throws  IllegalArgumentException if the set was a
     *          NullPointer
     */
    public final ElementSet union(final ElementSet set) {
        if (set == null) {
            throw new IllegalArgumentException(
                "NullPointer as set is not allowed");
        }
        elements.addAll(set.elements);
        return this;
    }


    /**
     * Remove an element from this set.
     *
     * @param   element    Element to remove from the set. Must not be <code>null</code>.
     * @return  Possibly changed <code>this</code>.
     * @throws  IllegalArgumentException if the element was a
     *          NullPointer
     */
    public final ElementSet remove(final Element element) {
        if (element == null) {
            throw new IllegalArgumentException(
                "NullPointer as element is not allowed");
        }
        elements.remove(element);
        return this;
    }


    /**
     * Remove elements from another {@link ElementSet} from this set.
     * After this method this object is the asymmetric set difference of the
     * two sets: <code>this</code> \ <code>set</code>.
     *
     * @param   set    Remove all elements that are in this set from
     *                 <code>this</code>.
     * @return  Possibly changed <code>this</code>.
     * @throws  IllegalArgumentException if the set was a
     *          NullPointer
     */
    public final ElementSet minus(final ElementSet set) {
        if (set == null) {
            throw new IllegalArgumentException(
                "NullPointer as set is not allowed");
        }
        this.elements.removeAll(set.elements);
        return this;
    }


    /**
     * Build the intersection.
     *
     * @param   set    Check for these elements.
     * @return  Possibly changed <code>this</code>.
     * @throws  IllegalArgumentException if the set was a
     *          NullPointer
     */
    public final ElementSet intersection(final ElementSet set) {
        if (set == null) {
            throw new IllegalArgumentException(
                "NullPointer as set is not allowed");
        }
        this.elements.retainAll(set.elements);
        return this;
    }


    /**
     * Build a new intersection.
     *
     * @param   set    check for these elements
     * @return  New instance that contains all elements that were in <code>this</code>
     *          and <code>set</code>.
     * @throws  IllegalArgumentException if the set was a
     *          NullPointer
     */
    public final ElementSet newIntersection(final ElementSet set) {
        if (set == null) {
            throw new IllegalArgumentException(
                "NullPointer as set is not allowed");
        }
        final ElementSet result = new ElementSet(this);
        result.elements.retainAll(set.elements);
        return result;
    }


    /**
     * Return all elements that are only in one of both sets.
     * This method returns a new instance that holds the symmetric set difference of the
     * two sets. The original set is not modified.
     *
     * @param   set    Build the symmetric difference with this set.
     *                 <code>this</code>
     * @return  Symmetric difference.
     * @throws  IllegalArgumentException if the set was a
     *          NullPointer
     */
    public final ElementSet newDelta(final ElementSet set) {
        if (set == null) {
            throw new IllegalArgumentException(
                "NullPointer as set is not allowed");
        }
        final ElementSet union = new ElementSet(this);
        union.union(set);
        final ElementSet intersection = new ElementSet(this);
        intersection.intersection(set);
        union.minus(intersection);
        return union;
    }

    /**
     * Get number of elements.
     *
     * @return  Number of elements in this set.
     */
    public final int size() {
        return this.elements.size();
    }

    /**
     * Returns an iterator over the elements in this set.  The elements are
     * returned in no particular order (unless this set is an instance of some
     * class that provides a guarantee).
     *
     * @return  Iterator over the elements in this set.
     */
    public Iterator iterator() {
        return this.elements.iterator();
    }

    public final boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() == ElementSet.class) {
            return this.elements.equals(((ElementSet) obj).elements);
        }
        return false;
    }

    public final int hashCode() {
        return elements.hashCode();
    }

    public final String toString() {
        final StringBuffer result = new StringBuffer();
        result.append("{");
        final Iterator iterator = elements.iterator();
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
