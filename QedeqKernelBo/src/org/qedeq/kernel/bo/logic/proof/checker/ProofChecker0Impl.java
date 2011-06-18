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

package org.qedeq.kernel.bo.logic.proof.checker;

import org.qedeq.kernel.bo.logic.common.LogicalCheckExceptionList;
import org.qedeq.kernel.bo.logic.common.ReferenceResolver;
import org.qedeq.kernel.bo.logic.proof.common.ProofChecker;
import org.qedeq.kernel.bo.logic.proof.common.RuleChecker;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.base.module.Rule;
import org.qedeq.kernel.se.common.ModuleContext;


/**
 * Formal proof checker that don't allow any proof method.
 *
 * @author  Michael Meyling
 */
public class ProofChecker0Impl implements ProofChecker {

    /** Rule version we can check. */
    private final String ruleVersion;

    /**
     * Constructor.
     *
     * @param   ruleVersion Rule version we check.
     *
     */
    public ProofChecker0Impl(final String ruleVersion) {
        this.ruleVersion = ruleVersion;
    }

    public LogicalCheckExceptionList checkProof(final Element formula,
            final FormalProofLineList proof,
            final RuleChecker checker,
            final ModuleContext moduleContext,
            final ReferenceResolver resolver) {
        final ProofCheckException ex = new ProofCheckException(
            BasicProofErrors.NO_FORMAL_PROOFS_SUPORTED_CODE,
            BasicProofErrors.NO_FORMAL_PROOFS_SUPORTED_TEXT + ruleVersion,
            null, moduleContext, null);
            final LogicalCheckExceptionList exceptions = new LogicalCheckExceptionList();
            exceptions.add(ex);
        return exceptions;
    }

    public LogicalCheckExceptionList checkRule(final Rule rule,
            final ModuleContext context, final RuleChecker checker,
            final ReferenceResolver resolver) {
        // we just ignore rule definitions
        final LogicalCheckExceptionList exceptions = new LogicalCheckExceptionList();
        return exceptions;
    }

}
