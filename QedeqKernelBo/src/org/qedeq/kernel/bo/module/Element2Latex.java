/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.module;

import org.qedeq.kernel.bo.common.ModuleReferenceList;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.module.FunctionDefinition;
import org.qedeq.kernel.se.base.module.PredicateDefinition;
import org.qedeq.kernel.se.common.ModuleContext;

/**
 * Transfer a QEDEQ formulas into LaTeX text.
 *
 * @author  Michael Meyling
 */
public interface Element2Latex {

    /**
     * Get predicate definition.
     *
     * @param   name            Predicate name.
     * @param   argumentNumber  Number of predicate arguments.
     * @return  Definition. Might be <code>null</code>.
     */
    public abstract PredicateDefinition getPredicate(final String name,
            final int argumentNumber);

    /**
     * Get predicate context. This is only a copy.
     *
     * @param   name            Predicate name.
     * @param   argumentNumber  Number of predicate arguments.
     * @return  Module context. Might be <code>null</code>.
     */
    public abstract ModuleContext getPredicateContext(final String name,
            final int argumentNumber);

    /**
     * Get function definition.
     *
     * @param   name            Function name.
     * @param   argumentNumber  Number of function arguments.
     * @return  Definition. Might be <code>null</code>.
     */
    public abstract FunctionDefinition getFunction(final String name,
            final int argumentNumber);

    /**
     * Get function context. This is only a copy.
     *
     * @param   name            Function name.
     * @param   argumentNumber  Number of function arguments.
     * @return  Module context. Might be <code>null</code>.
     */
    public abstract ModuleContext getFunctionContext(final String name,
            final int argumentNumber);

    /**
     * Get LaTeX element presentation.
     *
     * @param   element    Print this element.
     * @return  LaTeX form of element.
     */
    public abstract String getLatex(final Element element);

    /**
     * Set list of external QEDEQ module references.
     *
     * @param   references  External QEDEQ module references.
     */
    public abstract void setModuleReferences(
            final ModuleReferenceList references);

}
