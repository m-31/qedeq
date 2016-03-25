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

import org.qedeq.kernel.se.common.ModuleContext;

/**
 * Proof search couldn't start because of invalid arguements.
 *
 * @author  Michael Meyling.
 *
 */
public class ProofFinderArgumentException extends ProofException {

    /**
     * Constructor.
     *
     * @param   errorCode   Error code of this message.
     * @param   message     Error message.
     * @param   context     Error location.
     */
    public ProofFinderArgumentException(final int errorCode, final String message,
                                        final ModuleContext context) {
        super(errorCode, message, null, context);
    }

}
