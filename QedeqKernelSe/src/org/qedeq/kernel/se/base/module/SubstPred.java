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
 * Usage of substitute predicate variable by formula.
 * <pre>
 *   A(p(x1, x2, .., xn)
 *  --------------------------
 *   A(formula(x1, x2, .., xn)
 * </pre>
 *
 * @author  Michael Meyling
 */
public interface SubstPred extends Reason {

    /**
     * Get this reason.
     *
     * @return  This reason.
     */
    public SubstPred getSubstPred();

    /**
     * Get reference to already proven formula.
     *
     * @return  Reference to previously proved formula.
     */
    public String getReference();

    /**
     * Get predicate variable (with subject variables as parameters) that should be replaced.
     *
     * @return  Reference to previously proved formula.
     */
    public Element getPredicateVariable();

    /**
     * Get substitute formula. Must contain the subject variables from
     * {@link #getPredicateVariable()}.
     *
     * @return  Replacement term.
     */
    public Element getSubstituteFormula();

}
