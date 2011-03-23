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

package org.qedeq.kernel.bo.logic.common;

import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.module.FormalProofLineList;

/**
 * A proof finder can create formal proofs for propositions.
 *
 * @author  Michael Meyling
 */
public interface ProofFinder {

    /**
     * Finds a formal proof.
     *
     * @param   formula             Formula we want to proof.
     * @param   proof               Initial proof lines containing only Add lines.
     * @return  Created formal proof. <code>null</code> if we did not find one.
     */
    public FormalProofLineList findProof(final Element formula,
            final FormalProofLineList proof);


}
