/* $Id: ModuleFileNotFoundException.java,v 1.4 2007/12/21 23:33:46 m31 Exp $
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

package org.qedeq.kernel.bo.control;

import java.io.File;

import org.qedeq.kernel.common.SourceFileExceptionList;

/**
 * An instance of this interface can load a local file.
 *
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */
public interface ModuleLoader {

    /**
     * Load a local QEDEQ module.
     *
     * @param   prop        Module properties.
     * @param   localFile   Load XML file from tbis location.
     * @throws  ModuleFileNotFoundException    Local file was not found.
     * @throws  SourceFileExceptionList    Module could not be successfully loaded.
     */
    public void loadLocalModule(final DefaultQedeqBo prop, final File localFile)
            throws ModuleFileNotFoundException, SourceFileExceptionList;

}
