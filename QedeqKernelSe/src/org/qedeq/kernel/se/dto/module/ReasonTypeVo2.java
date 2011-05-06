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

package org.qedeq.kernel.se.dto.module;

import org.qedeq.kernel.se.base.module.ConditionalProof;
import org.qedeq.kernel.se.base.module.Reason;
import org.qedeq.kernel.se.base.module.ReasonType2;


/**
 * Reason type for getting a new proof line. Rule version 1.01.
 *
 * @author  Michael Meyling
 */
public class ReasonTypeVo2 extends ReasonTypeVo implements ReasonType2 {

    /**
     * Constructs an proof line.
     *
     * @param   reason      Rule that was used to derive the formula.
     */
    public ReasonTypeVo2(final Reason reason) {
        super(reason);
    }

    /**
     * Default constructor.
     */
    public ReasonTypeVo2() {
        // nothing to do
    }

    public ConditionalProof getConditionalProof() {
        if (getReason() instanceof ConditionalProof) {
            return (ConditionalProof) getReason();
        }
        return null;
    }

}
