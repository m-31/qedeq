/* $Id: ModuleAddress.java,v 1.2 2008/07/26 07:59:40 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2009,  Michael Meyling <mime@qedeq.org>.
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

import java.io.IOException;

import org.qedeq.kernel.base.module.Specification;

/**
 * An address for a QEDEQ module.
 *
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */
public interface ModuleAddress {

    /**
     * Get module address as {@link ModuleContext}. Creates a new object.
     *
     * @return  Module address as {@link ModuleContext}.
     */
    public ModuleContext createModuleContext();

    /**
     * Get address header (including protocol, host, port, user)
     * but without file path.
     *
     * @return address header
     */
    public String getHeader();

    /**
     * Get address path (without protocol, host, port and file name).
     *
     * @return module path
     */
    public String getPath();

    /**
     * Get module file name.
     *
     * @return  Module file name.
     */
    public String getFileName();

    /**
     * Get name of module (file name without <code>.xml</code>).
     *
     * @return  Module name.
     */
    public String getName();

    /**
     * Get fully qualified URL of module.
     *
     * @return  URL for QEDEQ module.
     */
    public String getUrl();

    /**
     * Was this module address created relatively?
     *
     * @return  Relatively created?
     */
    public boolean isRelativeAddress();

    /**
     * Is this a local QEDEQ file. That means the address starts with <code>file:</code>.
     *
     * @return  Is the QEDEQ module a local file?
     */
    public boolean isFileAddress();


    /**
     * Get all potential module addresses from a module specification.
     *
     * TODO mime 20070326: add context information (for error case)
     *
     * @param   spec    Here are the (perhaps relative) addresses to
     *                  another module.
     * @return  Array of absolute address strings.
     * @throws  IOException One address is not correctly formed.
     */
    public ModuleAddress[] getModulePaths(final Specification spec) throws IOException;

}
