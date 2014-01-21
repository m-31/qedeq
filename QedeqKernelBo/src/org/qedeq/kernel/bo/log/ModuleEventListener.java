/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.log;

import org.qedeq.kernel.bo.common.QedeqBo;


/**
 * Interface for an QEDEQ module event listener.
 *
 * @author  Michael Meyling
 */
public interface ModuleEventListener {

    /**
     * Add module.
     *
     * @param   prop    add module with this properties.
     */
    public void addModule(final QedeqBo prop);


    /**
     * Module properties (i.e. the status) have changed.
     *
     * @param   prop    The state of this module changed.
     */
    public void stateChanged(final QedeqBo prop);


    /**
     * Remove module.
     *
     * @param   prop    This module was removed.
     */
    public void removeModule(final QedeqBo prop);

}
