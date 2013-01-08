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

package org.qedeq.kernel.bo.logic.proof.checker;

import org.qedeq.kernel.bo.logic.common.LogicalCheckException;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.common.ModuleContext;

/**
 * This is an exception for formal proof errors within a QEDEQ module.
 *
 * A checked proof is not correct.
 *
 * @author  Michael Meyling
 */
public class ProofCheckException extends LogicalCheckException {

    /**
     * Constructs an exception.
     *
     * @param   errorCode           ErrorCode of this message.
     * @param   message             What is the problem.
     * @param   element             Problematic formula.
     * @param   context             Error location. Not necessarily pointing to
     *                                  <code>element</code>.
     * @param   referenceContext    Reference location.
     */
    public ProofCheckException(final int errorCode, final String message, final Element element,
            final ModuleContext context, final ModuleContext referenceContext) {
        super(errorCode, message, element, context, referenceContext);
    }

    /**
     * Constructs an exception.
     *
     * @param  errorCode        ErrorCode of this message.
     * @param  message          What is the problem.
     * @param  element          Problematic formula.
     * @param  context          Error location. Not necessarily pointing to
     *                              <code>element</code>.
     */
    public ProofCheckException(final int errorCode, final String message,
            final Element element, final ModuleContext context) {
        super(errorCode, message, element, context);
    }

    /**
     * Constructs an exception.
     *
     * @param  errorCode        ErrorCode of this message.
     * @param  message          What is the problem.
     * @param  context          Error location. Not necessarily pointing to
     *                                  <code>element</code>.
     */
    public ProofCheckException(final int errorCode, final String message,
            final ModuleContext context) {
        super(errorCode, message, context);
    }

}
