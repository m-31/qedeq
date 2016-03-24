/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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


/**
 * Usage of Modus Ponens.
 * <pre>
 *  A -&gt; B
 *    A
 *  ------
 *    B
 * </pre>
 *
 * @author  Michael Meyling
 */
public interface ModusPonens extends Reason {

    /**
     * Get this reason.
     *
     * @return  This reason.
     */
    public ModusPonens getModusPonens();

    /**
     * Get reference to formula. Usually this a formula of type A -&gt; B
     *
     * @return  Reference to previously proved formula.
     */
    public String getReference1();

    /**
     * Get reference to formula. Usually this a formula of type A
     *
     * @return  Reference to previously proved formula.
     */
    public String getReference2();

}
