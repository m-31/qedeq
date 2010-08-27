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

package org.qedeq.kernel.common;

import org.qedeq.base.trace.Trace;


/**
 * Define context for an instance of {@link org.qedeq.kernel.base.module.Qedeq}.
 * It consists of a location information: where is this module located.
 * Also the location within the {@link org.qedeq.kernel.base.module.Qedeq} object
 * should be described in an XPath like manner.
 * <p>
 * The idea behind this context is a caller perspective. The caller sets the
 * context (at least the module location information) and if the called method
 * throws an exception a try/catch block can retrieve the context information.
 *
 * @author  Michael Meyling
 */
public class ModuleContext {

    /** This class. */
    private static final Class CLASS = ModuleContext.class;

    /** Module location. */
    private ModuleAddress moduleLocation;

    /** Location within the module. */
    private String locationWithinModule;

    /** Skip this number of rows (beginning from location start). */
    private final int startRow;

    /** Go to this column. */
    private final int startColumn;

    /** Mark until this row. */
    private final int endRow;

    /** Mark until this column. */
    private final int endColumn;

    /**
     * Constructor.
     *
     * @param   moduleLocation          Module location information. Must not be <code>null</code>.
     * @param   locationWithinModule    Location within module. Must not be <code>null</code>.
     * @param   startRow                Skip this number of rows. Must not be below minus one.
     *                                  Minus one means no further location precision.
     * @param   startColumn             Starting column. Must not be below one.
     * @param   endRow                  Also relative to location begin. Must not be below minus
     *                                  one.
     *                                  Minus one means no further location precision.
     * @param   endColumn               Ending column. Must not be below one.
     * @throws  NullPointerException    At least one parameter is null.
     * @throws  IllegalArgumentException    One parameter is below its allowed minimum.
     */
    public ModuleContext(final ModuleAddress moduleLocation, final String locationWithinModule,
            final int startRow, final int startColumn, final int endRow, final int endColumn) {
        if (moduleLocation == null) {
            throw new NullPointerException("module adress should not be null");
        }
        if (locationWithinModule == null) {
            throw new NullPointerException("location within module should not be null");
        }
        if (startRow < -1) {
            throw new IllegalArgumentException("start row must be not below minus one");
        }
        if (startColumn < 1) {
            throw new IllegalArgumentException("start column must be not below one");
        }
        if (endRow < -1) {
            throw new IllegalArgumentException("end row must be not below minus one");
        }
        if (endColumn < 1) {
            throw new IllegalArgumentException("end column must be not below one");
        }
        this.moduleLocation = moduleLocation;
        this.locationWithinModule = locationWithinModule;
        this.startRow = startRow;
        this.startColumn = startColumn;
        this.endRow = endRow;
        this.endColumn = endColumn;
    }

    /**
     * Constructor.
     *
     * @param   moduleLocation  Module location information. Must not be <code>null</code>.
     * @param   locationWithinModule    Location within module. Must not be <code>null</code>.
     * @throws  NullPointerException At least one parameter is null.
     */
    public ModuleContext(final ModuleAddress moduleLocation, final String locationWithinModule) {
        this(moduleLocation, locationWithinModule, -1, 1, -1, 1);
    }

    /**
     * Constructor.
     *
     * @param   moduleLocation  Module location information.
     */
    public ModuleContext(final ModuleAddress moduleLocation) {
        this(moduleLocation, "");
    }

    /**
     * Copy constructor.
     *
     * @param   original    Original context.
     */
    public ModuleContext(final ModuleContext original) {
        this(original.getModuleLocation(), original.getLocationWithinModule(),
            original.getStartRow(), original.getStartColumn(), original.getEndRow(),
            original.getEndColumn());
    }

    /**
     * Constructor.
     *
     * @param   main            Main context. Must not be <code>null</code>.
     * @param   moduleLocation  Module location information. Must not be <code>null</code>.
     */
    public ModuleContext(final ModuleContext main, final String moduleLocation) {
        this(main.getModuleLocation(), moduleLocation);
    }

    /**
     * Get location information about module.
     *
     * @return  Module location information.
     */
    public final ModuleAddress getModuleLocation() {
        return moduleLocation;
    }

    /**
     * Get location information where are we within the module.
     *
     * @return  Location within module.
     */
    public final String getLocationWithinModule() {
        return locationWithinModule;
    }

    /**
     * Set location information where are we within the module.
     *
     * @param   locationWithinModule    Location within module.
     */
    public final void setLocationWithinModule(final String locationWithinModule) {
        final String method = "setLocationWithinModule(String)";
        this.locationWithinModule = locationWithinModule;
        Trace.param(CLASS, this, method, "locationWithinModule", locationWithinModule);
    }

    /**
     * Location start plus this number of rows is the real starting position.
     *
     * @return  Skip this number of rows.
     */
    public final int getStartRow() {
        return startRow;
    }

    /**
     * Column of precise location start. If {@link #getStartColumn()} == 0 this is added to
     * the column value of the location beginning.
     *
     * @return  Start column.
     */
    public final int getStartColumn() {
        return startColumn;
    }

    /**
     * Location start plus this number of rows is the real end position.
     *
     * @return  Skip this number of rows.
     */
    public final int getEndRow() {
        return endRow;
    }

    /**
     * Column of precise location end. If {@link #getEndColumn()} == 0 this is added to
     * the column value of the location beginning.
     *
     * @return  Start column.
     */
    public final int getEndColumn() {
        return endColumn;
    }


    public final int hashCode() {
        return getModuleLocation().hashCode() ^ getLocationWithinModule().hashCode()
         ^ startRow ^ startColumn ^ endRow ^ endColumn;
    }

    public final boolean equals(final Object obj) {
        if (!(obj instanceof ModuleContext)) {
            return false;
        }
        final ModuleContext other = (ModuleContext) obj;
        return getModuleLocation().equals(other.getModuleLocation())
            && getLocationWithinModule().equals(other.getLocationWithinModule())
            && startRow == other.startRow && startColumn == other.startColumn
            && endRow == other.endRow && endColumn == other.endColumn;
    }

    public final String toString() {
        return getModuleLocation() + ":" + getLocationWithinModule() + ":" + startRow + ":"
            + startColumn + ":" + endRow + ":" + endColumn;
    }

}
