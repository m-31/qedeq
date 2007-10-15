/* $Id: Proof.java,v 1.7 2007/02/25 20:05:36 m31 Exp $
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
 * Contains a proof for a proposition or rule.
 *
 * LATER mime 20050220: add formal proof
 *
 * @version $Revision: 1.7 $
 * @author  Michael Meyling
 */
public interface Proof {

    /**
     * Get type of proof. E.g. "informal".
     *
     * @return  Type of proof.
     */
    public String getKind();

    /**
     * Get level of proof. Higher levels contain additional informations.
     *
     * @return  Returns the level.
     */
    public String getLevel();

    /**
     *  Get proof content.
     *
     * @return  LaTeX proof text.
     */
    public LatexList getNonFormalProof();

}
