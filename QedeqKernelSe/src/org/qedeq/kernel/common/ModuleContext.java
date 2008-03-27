/* $Id: ModuleContext.java,v 1.1 2008/03/27 05:16:25 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2008,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.kernel.trace.Trace;


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
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class ModuleContext {

    /** This class. */
    private static final Class CLASS = ModuleContext.class;

    /** Module location. */
    private ModuleAddress moduleLocation;

    /** Location within the module. */
    private String locationWithinModule;

    /**
     * Constructor.
     *
     * @param   moduleLocation  Module location information.
     * @param   locationWithinModule    Location within module.
     */
    public ModuleContext(final ModuleAddress moduleLocation, final String locationWithinModule) {
        this.moduleLocation = moduleLocation;
        this.locationWithinModule = locationWithinModule;
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
        this(original.getModuleLocation(), original.getLocationWithinModule());
    }

    /**
     * Constructor.
     *
     * @param   main            Main context.
     * @param   moduleLocation  Module location information.
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
     * Set location information about module.
     *
     * @param   moduleLocation  Module location information.
     */
    public final void setModuleLocation(final ModuleAddress moduleLocation) {
        this.moduleLocation = moduleLocation;
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public final String toString() {
        return getModuleLocation() + ":" + getLocationWithinModule();
    }

}
