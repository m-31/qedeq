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

package org.qedeq.kernel.bo.logic.common;

import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.common.ModuleDataException;

/**
 * This is the basis for an exception for logical errors within a QEDEQ module.
 *
 * @author  Michael Meyling
 */
public abstract class LogicalCheckException extends ModuleDataException {

    /**
     * This element causes the error.
     */
    private final Element element;

    /**
     * Constructs an exception.
     *
     * @param   errorCode           ErrorCode of this message.
     * @param   message             What is the problem.
     * @param   element             Problematic line.
     * @param   context             Error location.
     * @param   referenceContext    Reference location.
     */
    public LogicalCheckException(final int errorCode, final String message, final Element element,
            final ModuleContext context, final ModuleContext referenceContext) {
        super(errorCode, message, context, referenceContext);
        this.element = element;
    }

    /**
     * Constructs an exception.
     *
     * @param  errorCode        ErrorCode of this message.
     * @param  message          What is the problem.
     * @param  element          Problematic formula.
     * @param  context          Error location.
     */
    public LogicalCheckException(final int errorCode, final String message,
            final Element element, final ModuleContext context) {
        super(errorCode, message, context);
        this.element = element;
    }

    /**
     * Constructs an exception.
     *
     * @param  errorCode        ErrorCode of this message.
     * @param  message          What is the problem.
     * @param  context          Error location.
     */
    public LogicalCheckException(final int errorCode, final String message,
            final ModuleContext context) {
        super(errorCode, message, context);
        this.element = null;
    }

    /**
     * Get the problematic element. Might be <code>null</code>.
     *
     * @return  Element.
     */
    public final Element getElement() {
        return this.element;
    }

    /**
     * Returns a short description of this throwable.
     * If this <code>Throwable</code> object was created with a non-null detail
     * message string, then the result is the concatenation of five strings:
     * <ul>
     * <li>The name of the actual class of this object
     * <li>": " (a colon and a space)
     * <li>The result of the {@link Throwable#getMessage()} method for this object
     * <li>"\n" (a newline)
     * <li>A string representation of the {@link #getElement()} method for this object
     *     (might be empty).
     * </ul>
     *
     * @return a string representation of this throwable.
     */
    public final String toString() {
        return super.toString() + "\n"
            + (getElement() != null ? getElement().toString() : "");
    }

}
