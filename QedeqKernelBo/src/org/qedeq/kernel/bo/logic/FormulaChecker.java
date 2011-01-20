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

package org.qedeq.kernel.bo.logic;

import org.qedeq.kernel.bo.logic.wf.ExistenceChecker;
import org.qedeq.kernel.bo.logic.wf.LogicalCheckExceptionList;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.common.ModuleContext;

/**
 * A formula checker can check logical correctness of a formula or term.
 *
 * @author  Michael Meyling
 */
public interface FormulaChecker {

    /**
     * Checks if an {@link Element} is a formula. If there are any errors the returned list
     * (which is always not <code>null</code>) has a size greater zero.
     *
     * @param   element             Check this element.
     * @param   context             For location information. Important for locating errors.
     * @param   existenceChecker    Existence checker for operators.
     * @return  Collected errors if there are any. Not <code>null</code>.
     */
    public LogicalCheckExceptionList checkFormula(final Element element,
            final ModuleContext context, final ExistenceChecker existenceChecker);

    /**
     * Checks if an {@link Element} is a formula. All predicates and functions are assumed to exist.
     * If there are any errors the returned list (which is always not <code>null</code>) has a size
     * greater zero.
     * If the existence context is known you should use
     * {@link #checkFormula(Element, ModuleContext, ExistenceChecker)}.
     *
     * @param   element             Check this element.
     * @param   context             For location information. Important for locating errors.
     * @return  Collected errors if there are any. Not <code>null</code>.
     */
    public LogicalCheckExceptionList checkFormula(final Element element,
            final ModuleContext context);

    /**
     * Check if {@link Element} is a term. If there are any errors the returned list
     * (which is always not <code>null</code>) has a size greater zero.
     *
     * @param   element             Check this element.
     * @param   context             For location information. Important for locating errors.
     * @param   existenceChecker    Existence checker for operators.
     * @return  Collected errors if there are any. Not <code>null</code>.
     */
    public LogicalCheckExceptionList checkTerm(final Element element,
            final ModuleContext context, final ExistenceChecker existenceChecker);

    /**
     * Check if {@link Element} is a term. If there are any errors the returned list
     * (which is always not <code>null</code>) has a size greater zero.
     * If the existence context is known you should use
     * {@link #checkTerm(Element, ModuleContext, ExistenceChecker)}.
     *
     * @param   element Check this element.
     * @param   context For location information. Important for locating errors.
     * @return  Collected errors if there are any. Not <code>null</code>.
     */
    public LogicalCheckExceptionList checkTerm(final Element element,
            final ModuleContext context);

}
