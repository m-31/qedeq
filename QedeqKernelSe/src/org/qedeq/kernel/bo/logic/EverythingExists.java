/* $Id: EverythingExists.java,v 1.3 2008/01/26 12:39:09 m31 Exp $
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

package org.qedeq.kernel.bo.logic;


/**
 * This implementation gives always the answer <code>true</code> to the question
 * <em>exists this predicate?</em>.
 *
 * @version $Revision: 1.3 $
 * @author  Michael Meyling
 */
public final class EverythingExists implements ExistenceChecker {

    /** One and only instance. */
    private static final ExistenceChecker ALWAYS = new EverythingExists();

    /**
     * Hidden constructor.
     */
    private EverythingExists() {
        // nothing to do
    }

    public boolean predicateExists(final String name, final int arguments) {
        return true;
    }

    public boolean predicateExists(final Predicate predicate) {
        return true;
    }

    public boolean functionExists(final String name, final int arguments) {
        return true;
    }

    public boolean functionExists(final Function function) {
        return true;
    }

    public boolean classOperatorExists() {
        return true;
    }

    public boolean equalityOperatorExists() {
        return true;
    }

    public String getIdentityOperator() {
        return NAME_EQUAL;
    }

    /**
     * Get one instance of this class.
     *
     * @return  Class instance.
     */
    public static final ExistenceChecker getInstance() {
        return ALWAYS;
    }

}
