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

package org.qedeq.kernel.se.dto.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.qedeq.kernel.se.base.list.Atom;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.list.ElementList;


/**
 * Every Operator must inherit from this class. Its main function is to
 * provide the standard implementation of {@link #equals}
 * and {@link #hashCode}.
 *
 * @author  Michael Meyling
 */
public final class DefaultElementList implements ElementList {

    /** Operator string, e.g. "AND". */
    private final String operator;

    /** Here is are the elements stored. */
    private final List elements;


    /**
     * Constructs an element list with no elements.
     *
     * @param   operator    Operator name.
     * @throws  IllegalArgumentException Element or operator was a NullPointer.
     */
    public DefaultElementList(final String operator) {
        if (operator == null) {
            throw new IllegalArgumentException(
                "NullPointer as operator is not allowed");
        }
        this.operator = operator;
        this.elements = new ArrayList();
    }

    /**
     * Constructs an element list.
     *
     * @param   operator    Operator name.
     * @param   elements the elements to make a list of
     * @throws  IllegalArgumentException Element or operator was a NullPointer.
     */
    public DefaultElementList(final String operator, final Element[] elements) {
        if (operator == null) {
            throw new IllegalArgumentException(
                "NullPointer as operator is not allowed");
        }
        if (elements == null) {
            throw new IllegalArgumentException(
                "NullPointer as element array is not allowed");
        }
        this.operator = operator;
        this.elements = new ArrayList(Arrays.asList(elements));
    }

    public final boolean isAtom() {
        return false;
    }

    public final Atom getAtom() {
        throw new ClassCastException("this is no " + Atom.class.getName()
            + ", but a " + this.getClass().getName());
    }

    public final boolean isList() {
        return true;
    }

    public final ElementList getList() {
        return this;
    }

    public final String getOperator() {
        return this.operator;
    }

    public final int size() {
        return this.elements.size();
    }

    public final Element getElement(final int i) {
        if (i >= 0 && i < elements.size()) {
            return (Element) elements.get(i);
        }
        if (size() == 0)  {
            throw new IllegalArgumentException(
                "there are no elements, therefore no element number "
                + i);
        }
        throw new IllegalArgumentException(
            "there is no element number " + i
            + " the maximum element number is " + size());
    }

    public final List getElements() {
        return this.elements;
    }

    public final boolean equals(final Object object) {
        if (object == null) {
            return false;
        }
        if (object.getClass() == this.getClass()) {
            final ElementList element = (ElementList) object;
            if (getOperator().equals(element.getOperator())
                    && size() == element.size()) {
                for (int i = 0; i < size(); i++) {
                    if (!getElement(i).equals(element.getElement(i))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public final Element copy() {
        final Element[] copied = new Element[size()];
        for (int i = 0; i < size(); i++) {
            copied[i] = getElement(i).copy();
        }
        return new DefaultElementList(getOperator(), copied);
    }

    public final Element replace(final Element search,
            final Element replacement) {
        if (this.equals(search)) {
            return replacement.copy();
        }
        final Element[] replaced = new Element[size()];
        for (int i = 0; i < size(); i++) {
            replaced[i] = getElement(i).replace(search, replacement);
        }
        return new DefaultElementList(getOperator(), replaced);
    }

    public final void add(final Element element) {
        if (element == null) {
            throw new IllegalArgumentException(
                "NullPointer couldn't be added");
        }
        this.elements.add(element);
    }

    public final void insert(final int position, final Element element) {
        if (element == null) {
            throw new IllegalArgumentException(
                "NullPointer couldn't be inserted");
        }
        if (position >= 0 && position <= this.elements.size()) {
            this.elements.add(position, element);
        } else {
            throw new IllegalArgumentException(
                "allowed set is {0"
                + (this.elements.size() > 0 ? ", .. "
                + this.elements.size() : "")
                + "}, and " + position + " is not in this set");
        }
    }

    public final void replace(final int position, final Element element) {
        if (element == null) {
            throw new IllegalArgumentException(
                "NullPointer couldn't be set");
        }
        if (position >= 0 && position < this.elements.size()) {
            this.elements.set(position, element);
        }
        if (size() == 0)  {
            throw new IllegalArgumentException(
                "there are no elements, therefore no element number "
                + position + " could be replaced");
        }
        throw new IllegalArgumentException(
            "there is no element number " + position
            + " the maximum element number is " + size());
    }

    public final void remove(final int i) {
        if (i >= 0 && i < elements.size()) {
            elements.remove(i);
            return;
        }
        if (size() == 0)  {
            throw new IllegalArgumentException(
                "there are no elements, therefore no element number "
                + i + " could be removed");
        }
        throw new IllegalArgumentException(
            "there is no element number " + i
            + " the maximum element number is " + size());
    }

    public final int hashCode() {
        return toString().hashCode();
    }

    public final String toString() {
        if (size() > 0) {
            final StringBuffer buffer = new StringBuffer(getOperator() + " ( ");
            for (int i = 0; i < size(); i++) {
                if (i != 0) {
                    buffer.append(", ");
                }
                buffer.append(getElement(i));
            }
            buffer.append(")");
            return buffer.toString();
        }
        return getOperator();
    }

}
