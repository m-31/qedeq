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

package org.qedeq.kernel.bo.logic.common;



/**
 * Check if a function is already defined.
 *
 * @author  Michael Meyling
 */
public interface FunctionExistenceChecker {

    /**
     * Check if a function is already defined.
     *
     * @param   name        Function name.
     * @param   arguments   Number of operands for the function.
     * @return  Functions is defined.
     */
    public boolean functionExists(String name, int arguments);

    /**
     * Check if a function is already defined.
     *
     * @param   function    Function.
     * @return  Functions is defined.
     */
    public boolean functionExists(FunctionKey function);

    /**
     * Check if given function key has an initial function definition.
     *
     * @param   function    Function key.
     * @return  Function is defined and is an initial function definition.
     */
    public boolean isInitialFunction(final FunctionKey function);

}
