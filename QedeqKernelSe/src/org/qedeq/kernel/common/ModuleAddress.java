/* $Id: ModuleAddress.java,v 1.7 2008/01/26 12:39:09 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2007,  Michael Meyling <mime@qedeq.org>.
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

import java.net.URL;

/**
 * An address for a QEDEQ module.
 *
 * @version $Revision: 1.7 $
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
    public URL getURL();

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

}
