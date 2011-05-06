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

package org.qedeq.kernel.se.base.module;


/**
 * Usage of conditional proof method. If you can derive the proposition A out of
 * the assumed formulas then the following formula is true:
 * conjunction of the assumed formulas implies A
 * <pre>
 *      H    hypothesis
 *     -------------------
 *      A
 *  ---------------
 *   H -&gt; A
 * </pre>
 *
 * @author  Michael Meyling
 */
public interface ConditionalProof extends Reason {

    /**
     * Get reference to already proven formula.
     *
     * @return  Reference to previously proved formula.
     */
    public Hypothesis getHypothesis();

    /**
     * Get free subject variable that should be replaced.
     *
     * @return  Subject variable.
     */
    public FormalProofLineList getFormalProofLineList();


}
