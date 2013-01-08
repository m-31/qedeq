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

package org.qedeq.kernel.bo.logic.proof.common;

import org.qedeq.base.io.Parameters;
import org.qedeq.kernel.bo.common.Element2Utf8;
import org.qedeq.kernel.bo.log.ModuleLogListener;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.visitor.InterruptException;

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
     * @param   proof               Initial proof lines containing only "Add" lines.
     * @param   context             We are in this context.
     * @param   parameters          Further parameters to tune search process.
     * @param   log                 Log progress.
     * @param   transform           Transformer for getting UTF-8 out of elements.
     * @throws  InterruptException  Proof finding was interrupted.
     * @throws  ProofException      Finding result.
     */
    public void findProof(Element formula, FormalProofLineList proof,
            ModuleContext context, Parameters parameters, ModuleLogListener log,
            Element2Utf8 transform)
            throws InterruptException, ProofException;

    /**
     * Get a description which action is currently taken.
     *
     * @return  Description.
     */
    public String getExecutionActionDescription();

}
