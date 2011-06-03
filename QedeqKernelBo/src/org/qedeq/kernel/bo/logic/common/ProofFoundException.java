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

import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.common.ModuleContext;

/**
 * Indicates we found a proof.
 *
 * @author  Michael Meyling.
 *
 */
public class ProofFoundException extends ProofException {

    /**
     * Constructor.
     *
     * @param   errorCode   Error code of this message.
     * @param   message     Error message.
     * @param   lines       Current proof lines.
     * @param   context     Error location.
     */
    public ProofFoundException(final int errorCode, final String message,
            final FormalProofLineList lines, final ModuleContext context) {
        super(errorCode, message, lines, context);
    }

}
