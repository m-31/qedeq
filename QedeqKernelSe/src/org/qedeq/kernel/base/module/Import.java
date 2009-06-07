/* $Id: Import.java,v 1.6 2008/03/27 05:16:26 m31 Exp $
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

package org.qedeq.kernel.base.module;


/**
 * Module import. Every needed module must be referenced as an module import.
 *
 * @version $Revision: 1.6 $
 * @author  Michael Meyling
 */
public interface Import {

    /**
     * Get label for the imported module. All references to that module must have this prefix.
     * @return  Label.
     */
    public String getLabel();

    /**
     * Get import specification. This includes location information.
     *
     * @return  Import specification.
     */
    public Specification getSpecification();

}
