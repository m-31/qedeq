/* $Id: HigherLogicalErrors.java,v 1.3 2008/03/27 05:16:24 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2008,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.logic.wf;

/**
 * Error codes and messages for formula checker.
 *
 * @version $Revision: 1.3 $
 * @author  Michael Meyling
 */
public interface HigherLogicalErrors {

    /** Error code. */
    public static final int PREDICATE_ALREADY_DEFINED = 40400;

    /** Error message. */
    public static final String PREDICATE_ALREADY_DEFINED_TEXT
        = "predicate was already defined for this argument number: ";


    /** Error code. */
    public static final int FUNCTION_ALREADY_DEFINED = 40400;

    /** Error message. */
    public static final String FUNCTION_ALREADY_DEFINED_TEXT
        = "function was already defined for this argument number: ";



}
