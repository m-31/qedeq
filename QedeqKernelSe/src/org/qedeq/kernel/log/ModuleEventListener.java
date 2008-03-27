/* $Id: ModuleEventListener.java,v 1.3 2008/03/27 05:16:26 m31 Exp $
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

package org.qedeq.kernel.log;

import org.qedeq.kernel.common.QedeqBo;


/**
 * Interface for an QEDEQ module event listener.
 *
 * @version $Revision: 1.3 $
 * @author  Michael Meyling
 */
public interface ModuleEventListener {

    /**
     * Add module.
     *
     * @param   prop    add module with this properties.
     */
    void addModule(final QedeqBo prop);


    /**
     * Module properties (i.e. the status) have changed.
     *
     * @param   prop
     */
    void stateChanged(QedeqBo prop);


    /**
     * Remove module.
     *
     * @param   prop    remove module with this properties.
     */
    void removeModule(QedeqBo prop);



}
