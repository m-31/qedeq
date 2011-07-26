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

package org.qedeq.kernel.bo.logic.proof.common;

import org.qedeq.base.io.Version;



/**
 * Can create a {@link ProofChecker}.
 *
 * @author  Michael Meyling
 */
public interface ProofCheckerFactory {

    /**
     * Check if we have a proof checker that supports the given rule version.
     *
     * @param   ruleVersion Rule version the module claims to use.
     * @return  Do we have a proof checker for the given rule version.
     */
    public boolean isRuleVersionSupported(final String ruleVersion);

    /**
     * Create a {@link ProofChecker}.
     *
     * @param   ruleVersion Rule version the module claims to use.
     * @return  Instance that can check formulas for correctness.
     */
    public ProofChecker createProofChecker(final Version ruleVersion);

}
