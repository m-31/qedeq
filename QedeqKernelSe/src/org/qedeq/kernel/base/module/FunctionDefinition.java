/* $Id: FunctionDefinition.java,v 1.3 2007/02/25 20:05:35 m31 Exp $
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

package org.qedeq.kernel.base.module;


/**
 * Definition of function operator. This is a function constant. For example the function
 * "x union y" or constants like the empty set.
 *
 * @version $Revision: 1.3 $
 * @author  Michael Meyling
 */
public interface FunctionDefinition extends NodeType {

    /**
     * Get number of arguments for the defined object. Carries information about the argument
     * number the defined object needs.
     *
     * @return  Argument number.
     */
    public String getArgumentNumber();

    /**
     * This name together with {@link #getArgumentNumber()} identifies a function.
     *
     * @return  Name of defined function.
     */
    public String getName();

    /**
     * Get LaTeX output for definition. The replaceable arguments must are marked as "#1",
     * "#2" and so on. For example "\mathfrak{M}(#1)"
     *
     * @return  LaTeX pattern for definition type setting.
     */
    public String getLatexPattern();

    /**
     * Get variable list of definition arguments.
     *
     * @return  List of formulas or subject variables to be replaced in the LaTeX pattern.
     *          Could be <code>null</code>.
     */
    public VariableList getVariableList();

    /**
     * Get term that defines the object. Could be <code>null</code>.
     *
     * @return  Defining term.
     */
    public Term getTerm();

    /**
     * Get description. Only necessary if formula is not self-explanatory.
     *
     * @return  Description.
     */
    public LatexList getDescription();

}
