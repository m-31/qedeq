/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2010,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.module;

import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.SourceFileExceptionList;

/**
 * Holds the results from a plugin execution.
 *
 * @author  Michael Meyling
 */
public class PluginResults {

    /** Errors that occurred. */
    private DefaultSourceFileExceptionList errors;

    /** Warnings that occurred. */
    private DefaultSourceFileExceptionList warnings;

    /**
     * Creates a new result container.
     */
    public PluginResults() {
        errors = new DefaultSourceFileExceptionList();
        warnings = new DefaultSourceFileExceptionList();
    }

    public void clear() {
        errors.clear();
        warnings.clear();
    }

    public SourceFileExceptionList getErrors() {
        return errors;
    }

    public void addErrors(final SourceFileExceptionList errors) {
        this.errors.add(errors);
    }

    public SourceFileExceptionList getWarnings() {
        return warnings;
    }

    public void addWarnings(final SourceFileExceptionList warnings) {
        this.warnings.add(warnings);
    }

    public boolean hasErrors() {
        return errors.size() > 0;
    }

    public boolean hasWarnings() {
        return warnings.size() > 0;
    }

}
