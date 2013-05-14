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

package org.qedeq.kernel.bo.service.common;

import org.apache.commons.lang.StringUtils;


/**
 * Execution result of a {@link ServiceCall}.
 *
 * @author  Michael Meyling
 */
public class ServiceResultImpl implements ServiceResult {

    public static ServiceResult INTERRUPTED = new ServiceResultImpl(true);

    public static ServiceResult SUCCESSFUL = new ServiceResultImpl(false);

    private final boolean interrupted;
    private final boolean ok;
    private final boolean hasWarnings;
    private final boolean hasErrors;
    private final String message;
    private Object result;
    
    public ServiceResultImpl(final boolean interrupted, final boolean ok, final boolean hasWarnings,
                final boolean hasErrors, final String message, final Object result) {
        this.interrupted = interrupted;
        this.ok = ok;
        this.hasWarnings = hasWarnings;
        this.hasErrors = hasErrors;
        this.message = message;
        this.result = result;
    }

    public ServiceResultImpl(final String errorMessage) {
        this.interrupted = false;
        this.ok = false;
        this.hasErrors = true;
        this.hasWarnings = false;
        this.message = errorMessage;
        this.result = null;
    }

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
