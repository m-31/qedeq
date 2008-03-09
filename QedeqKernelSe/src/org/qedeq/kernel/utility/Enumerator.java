/* $Id: Enumerator.java,v 1.2 2007/02/25 20:05:37 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2007,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.utility;

/**
 * An object of this class represents a number, that could be
 * compared and increased.
 *
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */
public final class Enumerator {

    /** The plain number. */
    private int number;

    /**
     * Constructs an object.
     */
    public Enumerator() {
        number = 0;
    }

    /**
     * Constructs an object with given start number.
     *
     * @param    number  Start value.
     */
    public Enumerator(final int number) {
        this.number = number;
    }


    /**
     * Gets current number.
     *
     * @return  Current number.
     */
    public final int getNumber() {
        return number;
    }


    /**
     * Increases current number by one.
     */
    public final void increaseNumber() {
        number++;
    }

    /**
     * Return number in <code>String</code> format.
     *
     * @return  Number as <code>String</code>.
     */
    public final String toString() {
        return Integer.toString(number);
    }

}
