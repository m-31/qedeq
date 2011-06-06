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

import org.qedeq.kernel.bo.log.LogListener;
import org.qedeq.kernel.bo.module.Element2Utf8;
import org.qedeq.kernel.se.base.list.ElementList;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.visitor.InterruptException;

/**
 * A proof finder can create formal proofs for propositions.
 *
 * @author  Michael Meyling
 */
public interface MultiProofFinder {

    /**
     * Finds a multiple formal proofs.
     *
     * @param   formulas    Formulas we want to proof.
     * @param   proof       Initial proof lines containing only "Add" lines.
     * @param   listener    Proof found listener.
     * @param   context             We are in this context.
     * @param   log                 Log progress.
     * @param   transform           Transformer for getting UTF-8 out of elements.
     * @throws  InterruptException  Proof finding was interrupted.
     * @return  Did we found any proof at all?
     */
    public boolean findProof(ElementList formulas, FormalProofLineList proof,
        ProofFoundListener listener, ModuleContext context, LogListener log,
        Element2Utf8 trans) throws InterruptException;

    /**
     * Get a description which action is currently taken.
     *
     * @return  Description.
     */
    public String getExecutionActionDescription();


}
