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

import org.qedeq.kernel.se.base.list.ElementList;
import org.qedeq.kernel.se.base.module.FormalProof;
import org.qedeq.kernel.se.common.ModuleContext;

/**
 * A proof checker can check if a formal proof is correct.
 *
 * @author  Michael Meyling
 */
public interface ProofChecker {

    /**
     * Checks if an formal proof is ok. If there are any errors the returned list
     * (which is always not <code>null</code>) has a size greater zero.
     *
     * @param   formula             Formula we want to proof.
     * @param   proof               Check this formal proof.
     * @param   context             For location information. Important for locating errors.
     * @param   resolver            Resolver for references.
     * @param   existence           Existence checker for operators.
     * @return  Collected errors if there are any. Not <code>null</code>.
     */
    public LogicalCheckExceptionList checkProof(final ElementList formula,
            final FormalProof proof,
            final ModuleContext context, final ReferenceResolver resolver,
            final ExistenceChecker existence);


}
