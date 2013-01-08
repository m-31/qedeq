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

import org.qedeq.kernel.bo.logic.common.LogicalCheckExceptionList;
import org.qedeq.kernel.bo.logic.common.ReferenceResolver;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.base.module.Rule;
import org.qedeq.kernel.se.common.ModuleContext;

/**
 * A proof checker can check if a formal proof is correct.
 *
 * @author  Michael Meyling
 */
public interface ProofChecker {

    /**
     * Checks if a formal proof is ok. If there are any errors the returned list
     * (which is always not <code>null</code>) has a size greater zero.
     * Precondition is a well formed environment. So every formula must
     * be well formed, every necessary operator and predicate and function must
     * exist.
     *
     * @param   formula             Formula we want to proof.
     * @param   proof               Check this formal proof.
     * @param   checker             Gets defined rule information.
     * @param   context             Location information of formal proof.
     *                              Important for locating errors.
     * @param   resolver            Resolver for references.
     * @return  Collected errors if there are any. Not <code>null</code>.
     */
    public LogicalCheckExceptionList checkProof(final Element formula,
            final FormalProofLineList proof, final RuleChecker checker,
            final ModuleContext context, final ReferenceResolver resolver);


    /**
     * Checks if a rule declaration is ok for the proof checker.
     *
     * @param   rule                Rule we want to use later on.
     * @param   context             Location information rule declaration.
     *                              Important for locating errors.
     * @param   checker             Gets defined rule information.
     * @param   resolver            Resolver for references.
     * @return  Collected errors if there are any. Not <code>null</code>.
     */
    public LogicalCheckExceptionList checkRule(final Rule rule,
        final ModuleContext context, final RuleChecker checker,
        final ReferenceResolver resolver);

}
