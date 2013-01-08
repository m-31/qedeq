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

package org.qedeq.kernel.se.base.module;

import org.qedeq.kernel.se.base.list.Element;


/**
 * Rename bound subject variable.
 * This replacement can take place for a certain bound occurrence of the subject variable.
 * For example in this case we replace x by y in at the first occurrence.
 * <pre>
 *   forall x A(x)
 *  ---------------
 *   forall y A(y)
 * </pre>
 *
 * @author  Michael Meyling
 */
public interface Rename extends Reason {

    /**
     * Get this reason.
     *
     * @return  This reason.
     */
    public Rename getRename();

    /**
     * Get reference to formula. Usually this a formula of type A(x)
     *
     * @return  Reference to previously proved formula.
     */
    public String getReference();

    /**
     * Get original subject variable. Something like x.
     *
     * @return  Subject variable.
     */
    public Element getOriginalSubjectVariable();

    /**
     * Get replacement subject variable. Something like y.
     *
     * @return  Subject variable.
     */
    public Element getReplacementSubjectVariable();

    /**
     * Get bound occurrence to replace. Starts with 1. A value of 0 means no specification.
     *
     * @return  Number of occurrence.
     */
    public int getOccurrence();

}
