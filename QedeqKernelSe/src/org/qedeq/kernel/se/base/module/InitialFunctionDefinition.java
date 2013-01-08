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
 * Definition of initial function operator. This is a function constant that can not defined further.
 * For example the function "emptySet" might be an undefinable function of a certain set theory. So every
 * model for this set theory must mark an object of its domain as "emptySet".
 *
 * @author  Michael Meyling
 */
public interface InitialFunctionDefinition extends NodeType {

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
     * Get function constant that we define. The function constant must
     * match {@link #getName()} and {@link #getArgumentNumber()}.
     *
     * @return  Function constant with free subject variables as arguments.
     */
    public Element getFunCon();

    /**
     * Get description. Only necessary if formula is not self-explanatory.
     *
     * @return  Description.
     */
    public LatexList getDescription();

}
