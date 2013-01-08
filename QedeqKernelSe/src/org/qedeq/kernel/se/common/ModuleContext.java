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

package org.qedeq.kernel.se.common;

import org.qedeq.base.io.SourcePosition;
import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.EqualsUtility;


/**
 * Define context for an instance of {@link org.qedeq.kernel.se.base.module.Qedeq}.
 * It consists of a location information: where is this module located.
 * Also the location within the {@link org.qedeq.kernel.se.base.module.Qedeq} object
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

    /** Skip position (relative to location start). Could be <code>null</code>. */
    private final SourcePosition startDelta;

    /** Mark until this column (relative to location start). Could be <code>null</code>. */
    private final SourcePosition endDelta;

    /**
     * Constructor.
     *
     * @param   moduleLocation          Module location information. Must not be <code>null</code>.
     * @param   locationWithinModule    Location within module. Must not be <code>null</code>.
     * @param   startDelta              Skip position (relative to location start). Could be
     *                                  <code>null</code>.
     * @param   endDelta                Mark until this column (relative to location start). Could
     *                                  be <code>null</code>.
     * @throws  NullPointerException    At least one parameter is null.
     * @throws  IllegalArgumentException    One parameter is below its allowed minimum.
     */
    public ModuleContext(final ModuleAddress moduleLocation, final String locationWithinModule,
            final SourcePosition startDelta, final SourcePosition endDelta) {
        if (moduleLocation == null) {
            throw new NullPointerException("module adress should not be null");
        }
        if (locationWithinModule == null) {
            throw new NullPointerException("location within module should not be null");
        }
        this.moduleLocation = moduleLocation;
        this.locationWithinModule = locationWithinModule;
        this.startDelta = startDelta;
        this.endDelta = endDelta;
    }

    /**
     * Constructor.
     *
     * @param   moduleLocation  Module location information. Must not be <code>null</code>.
     * @param   locationWithinModule    Location within module. Must not be <code>null</code>.
     * @throws  NullPointerException At least one parameter is null.
     */
    public ModuleContext(final ModuleAddress moduleLocation, final String locationWithinModule) {
        this(moduleLocation, locationWithinModule, null, null);
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
            original.getStartDelta(), original.getEndDelta());
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
     * Get delta position (relative to location start). This describes the precise
     * location start.
     * Could be <code>null</code>.
     *
     * @return  Delta for precise location start.
     */
    public final SourcePosition getStartDelta() {
        return startDelta;
    }

    /**
     * Get delta position (relative to location start). This describes the precise
     * location end.
     * Could be <code>null</code>.
     *
     * @return  Delta for precise location end.
     */
    public final SourcePosition getEndDelta() {
        return endDelta;
    }

    public final int hashCode() {
        return getModuleLocation().hashCode() ^ getLocationWithinModule().hashCode()
         ^ (startDelta != null ? startDelta.hashCode() : 7)
         ^ (endDelta != null ? endDelta.hashCode() : 11);
    }

    public final boolean equals(final Object obj) {
        if (!(obj instanceof ModuleContext)) {
            return false;
        }
        final ModuleContext other = (ModuleContext) obj;
        return getModuleLocation().equals(other.getModuleLocation())
            && getLocationWithinModule().equals(other.getLocationWithinModule())
            && EqualsUtility.equals(startDelta, other.startDelta)
            && EqualsUtility.equals(endDelta, other.endDelta);
    }

    public final String toString() {
        return getModuleLocation() + ":" + getLocationWithinModule()
            + ":" + startDelta + ":" + endDelta;
    }

}
