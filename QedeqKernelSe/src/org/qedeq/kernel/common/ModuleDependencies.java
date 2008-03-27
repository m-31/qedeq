/* $Id: ModuleDependencies.java,v 1.1 2008/03/27 05:16:25 m31 Exp $
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

import java.net.URL;

/**
 * A QEDEQ module URL and the URLs of the modules it requires and its dependents.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public interface ModuleDependencies {

    /**
     * Get URL of this QEDEQ module.
     *
     * @return  URL of this QEDEQ module.
     */
    public URL getUrl();


    /**
     * Add another required QEDEQ module.
     *
     * @param   url     URL of required QEDEQ module.
     * @param   label   Label for that module.
     */// FIXME  perhaps add (QedeqBo, Label)??
    public void addRequired(URL url, String label);

    /**
     * Get number of required QEDEQ modules.
     *
     * @return  Number of required modules.
     */
    public int sizeRequired();

    /**
     * Get certain required module URL.
     *
     * @param   number  Position number. Must be between <code>0</code> and
     *                  {@link #sizeRequired()}.
     *
     * @return  URL of required QEDEQ module.
     */
    public URL getRequiredUrl(final int number);

    /**
     * Get certain required label.
     *
     * @param   number  Position number. Must be between <code>0</code> and
     *                  {@link #sizeRequired()}.
     *
     * @return  Label of required QEDEQ module.
     */
    public String getRequiredLabel(final int number);


    /**
     * Add another QEDEQ module which uses this one.
     *
     * @param   url     URL of QEDEQ module that uses this one.
     */
    public void addDependent(URL url);

    /**
     * Get number of QEDEQ modules which use this one.
     *
     * @return  Number of modules that depend on this one.
     */
    public int sizeDependent();

    /**
     * Get certain dependent module URL.
     *
     * @param   number  Position number. Must be between <code>0</code> and
     *                  {@link #sizeRequired()}.
     *
     * @return  URL of QEDEQ module which uses this one.
     */
    public URL getDependent(final int number);

}
