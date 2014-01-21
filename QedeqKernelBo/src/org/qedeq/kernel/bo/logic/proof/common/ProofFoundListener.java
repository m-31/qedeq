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

package org.qedeq.kernel.bo.logic.proof.common;

import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.module.FormalProofLineList;

/**
 * A listener for proof findings.
 *
 * @author  Michael Meyling
 */
public interface ProofFoundListener {

    /**
     * Informs about a found proof for a formula.
     *
     * @param   formula     Formula we got a proof for.
     * @param   proof       Created formal proof.
     */
    public void proofFound(Element formula, FormalProofLineList proof);


}
