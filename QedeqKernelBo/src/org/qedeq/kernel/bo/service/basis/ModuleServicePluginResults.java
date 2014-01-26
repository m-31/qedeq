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

package org.qedeq.kernel.bo.service.basis;

import org.qedeq.kernel.se.common.SourceFileExceptionList;

/**
 * Holds the results from a plugin execution.
 *
 * @author  Michael Meyling
 */
public class ModuleServicePluginResults {

    /** Errors that occurred. */
    private SourceFileExceptionList errors;

    /** Warnings that occurred. */
    private SourceFileExceptionList warnings;

    /**
     * Creates a new result container.
     */
    public ModuleServicePluginResults() {
        errors = new SourceFileExceptionList();
        warnings = new SourceFileExceptionList();
    }

    /**
     * Clear all warnings and errors.
     */
    public void clear() {
        errors.clear();
        warnings.clear();
    }

    /**
     * Get list of all errors.
     *
     * @return  Error list. Is never <code>null</code>.
     */
    public SourceFileExceptionList getErrors() {
        return errors;
    }

    /**
     * Add errors.
     *
     * @param   errors  Add these errors.
     */
    public void addErrors(final SourceFileExceptionList errors) {
        this.errors.add(errors);
    }

    /**
     * Get list of all warnings.
     *
     * @return  Warnings list. Is never <code>null</code>.
     */
    public SourceFileExceptionList getWarnings() {
        return warnings;
    }

    /**
     * Add warnings.
     *
     * @param   warnings    Add these warnings.
     */
    public void addWarnings(final SourceFileExceptionList warnings) {
        this.warnings.add(warnings);
    }

    /**
     * Are there any errors?
     *
     * @return  Errors exist.
     */
    public boolean hasErrors() {
        return errors.size() > 0;
    }

    /**
     * Are there any warnings.
     *
     * @return  Warnings exist.
     */
    public boolean hasWarnings() {
        return warnings.size() > 0;
    }

}
