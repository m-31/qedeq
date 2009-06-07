/* $Id: Author.java,v 1.6 2008/03/27 05:16:26 m31 Exp $
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
 * Describes a QEDEQ module author.
 *
 * @version $Revision: 1.6 $
 * @author  Michael Meyling
 */
public interface Author {

    /**
     * Get name of author.
     *
     * @return  Author name.
     */
    public Latex getName();

    /**
     * Get email address of author.
     *
     * @return  Author's email address.
     */
    public String getEmail();

}
