/* $Id: FormatUtility.java,v 1.1 2007/10/07 16:43:10 m31 Exp $
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

package org.qedeq.kernel.rel.test.gui;

import java.math.BigDecimal;



/**
 * Contains static methods for formatting strings.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class FormatUtility  {

    /**
     * Hidden constructor.
     */
    private FormatUtility() {
        // nothing to do
    }


    public static final String toString(final Double value) {
        if (value == null) {
            return "";
        }
        return (new BigDecimal(Double.toString(value.doubleValue()))).toString();
    }

    public static final String toString(final double value) {
        return (new BigDecimal("" + value)).toString();
    }


}


