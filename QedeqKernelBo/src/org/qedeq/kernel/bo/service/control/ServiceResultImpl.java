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

package org.qedeq.kernel.bo.service.control;

import org.apache.commons.lang.StringUtils;
import org.qedeq.kernel.bo.common.ServiceResult;


/**
 * Execution result of a {@link ServiceCall}.
 *
 * @author  Michael Meyling
 */
public class ServiceResultImpl implements ServiceResult {

    /** A simple service result that has a user interrupt. */
    public static final ServiceResult INTERRUPTED = new ServiceResultImpl(true);

    /** A simple successful (=ok) service result. */
    public static final ServiceResult SUCCESSFUL = new ServiceResultImpl(false);

    /** Did a user interrupt occur and ended the service call? */
    private final boolean interrupted;

    /** Was the call fully successful (finished ok without errors and warnings)? */
    private final boolean ok;

    /** Did warnings occur? */
    private final boolean hasWarnings;

    /** Did errors occur? */
    private final boolean hasErrors;

    /** Error message. Might be empty. */
    private final String message;

    /** Result of service call. Might be null even for a successful call. */
    private Object result;

    /**
     * Constructor.
     *
     * @param   interrupted Did a user interrupt occur and ended the service call?
     * @param   ok          Was the call fully successful (finished ok without errors and warnings)?
     * @param   hasWarnings Did warnings occur?
     * @param   hasErrors   Did errors occur?
     * @param   message     Error message. Might be empty.
     * @param   result      Result of service call. Might be null even for a successful call.
     */
    public ServiceResultImpl(final boolean interrupted, final boolean ok, final boolean hasWarnings,
            final boolean hasErrors, final String message, final Object result) {
        this.interrupted = interrupted;
        this.ok = ok;
        this.hasWarnings = hasWarnings;
        this.hasErrors = hasErrors;
        this.message = message;
        this.result = result;
    }

    /**
     * Constructor for a failure call.
     *
     * @param   errorMessage     Error message. Should not be empty.
     */
    public ServiceResultImpl(final String errorMessage) {
        this.interrupted = false;
        this.ok = false;
        this.hasErrors = true;
        this.hasWarnings = false;
        this.message = errorMessage;
        this.result = null;
    }

    /**
     * Constructor for simple successful or user interrupted call.
     *
     * @param   interrupted Did a user interrupt occur and ended the service call?
     */
    private ServiceResultImpl(final boolean interrupted) {
        if (interrupted) {
            this.interrupted = true;
            this.ok = false;
            this.hasErrors = true;
            this.message = "User interrupted service call.";
        } else {
            this.interrupted = false;
            this.ok = true;
            this.hasErrors = true;
            this.message = StringUtils.EMPTY;
        }
        this.hasWarnings = false;
        this.result = null;
    }

    public boolean wasInterrupted() {
        return interrupted;
    }

    public boolean isOk() {
        return ok;
    }

    public boolean hasWarnings() {
        return hasWarnings;
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    public String getErrorMessage() {
        return message;
    }

    public Object getExecutionResult() {
        return result;
    }

    /**
     * Remove execution result. This might be necessary because it is really big.
     */
    public void deleteExecutionResult() {
        result = null;
    }
}
