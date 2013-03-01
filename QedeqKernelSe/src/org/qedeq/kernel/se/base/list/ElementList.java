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

package org.qedeq.kernel.se.base.list;

import java.util.List;

/**
 * Every Operator must implement this interface. Each operator deals with arguments. These arguments
 * form an ordered list. So there is the number of arguments, which is told by {@link #size} and the
 * <code>i</code>-th argument, accessible by {@link #getElement(int)}.
 *
 * @author Michael Meyling
 */
public interface ElementList extends Element {

    /**
     * Get the number of arguments.
     *
     * @return Number of arguments.
     */
    public int size();

    /**
     * Get the operator.
     *
     * @return Operator.
     */
    public String getOperator();

    /**
     * Get the requested argument.
     *
     * @param i Number of argument (starting with <code>0</code>).
     * @return <code>i</code>-th part formula.
     * @throws IllegalArgumentException <code>i</code> is not between <code>0</code> and
     *             <code>{@link #size()} - 1</code>
     */
    public Element getElement(int i);

    /**
     * Get all arguments as an list.
     *
     * @return All elements.
     */
    public List getElements();

    /**
     * TODO 20110327 m31: do we have to use modifying methods like this or {@link ElementList#remove(int)}
     * and so on? If we need them we have to make a deep copy. Otherwise we can save lots of space!
     * Adds an element to end of list.
     *
     * @param element Element to add.
     * @throws IllegalArgumentException The given element was a NullPointer.
     */
    public void add(final Element element);

    /**
     * Inserts an element to specified position.
     *
     * @param position Position of element to add.
     * @param element Element to add.
     * @throws IllegalArgumentException The given element was a NullPointer or the position was not
     *             valid.
     */
    public void insert(int position, final Element element);

    /**
     * Replaces an element at specified position.
     *
     * @param position Position of element to replace.
     * @param element Replacement element.
     * @throws IllegalArgumentException The given element was a NullPointer or the position was not
     *             valid.
     */
    public void replace(int position, final Element element);

    /**
     * Deletes an element of element list.
     *
     * @param i Position of element to remove.
     * @throws IllegalArgumentException The given position was not valid.
     */
    public void remove(final int i);

}
