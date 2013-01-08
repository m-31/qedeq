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

package org.qedeq.base.utility;

import java.util.Arrays;


/**
 * A collection of useful static methods for equality.
 *
 * @author  Michael Meyling
 */
public final class EqualsUtility {

    /**
     * Constructor, should never be called.
     */
    private EqualsUtility() {
        // don't call me
    }

    /**
     * Compare two objects, each of them could be <code>null</code>.
     *
     * @param   a   First parameter.
     * @param   b   Second parameter.
     * @return  Are <code>a</code> and <code>b</code> equal?
     */
    public static boolean equals(final Object a, final Object b) {
        if (a == null) {
            if (b == null) {
                return true;
            }
            return false;
        }
        return a.equals(b);
    }

    /**
     * Compare two objects, each of them could be <code>null</code>.
     *
     * @param   a   First parameter.
     * @param   b   Second parameter.
     * @return  Are <code>a</code> and <code>b</code> equal?
     */
    public static boolean equals(final Object[] a, final Object[] b) {
        if (a == null) {
            if (b == null) {
                return true;
            }
            return false;
        }
        return Arrays.equals(a, b);
    }

    /**
     * Compare two objects, each of them could be <code>null</code>.
     *
     * @param   a   First parameter.
     * @param   b   Second parameter.
     * @return  Are <code>a</code> and <code>b</code> equal?
     */
    public static boolean equals(final byte[] a, final byte[] b) {
        if (a == null) {
            if (b == null) {
                return true;
            }
            return false;
        }
        return Arrays.equals(a, b);
    }

    /**
     * Compare two objects, each of them could be <code>null</code>.
     *
     * @param   a   First parameter.
     * @param   b   Second parameter.
     * @return  Are <code>a</code> and <code>b</code> equal?
     */
    public static boolean equals(final int[] a, final int[] b) {
        if (a == null) {
            if (b == null) {
                return true;
            }
            return false;
        }
        return Arrays.equals(a, b);
    }

}
