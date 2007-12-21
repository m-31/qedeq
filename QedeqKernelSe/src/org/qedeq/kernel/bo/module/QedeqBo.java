/* $Id: QedeqBo.java,v 1.14 2007/12/21 23:33:46 m31 Exp $
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

package org.qedeq.kernel.bo.module;

import org.qedeq.kernel.base.module.Qedeq;

/**
 * A complete QEDEQ module. This describes the root business object.
 *
 * @version $Revision: 1.14 $
 * @author  Michael Meyling
 */
public interface QedeqBo {


    /**
     * Set data transfer object of this BO.
     *
     * @param   qedeq   QEDEQ module data.
     */
    public void setQedeq(final Qedeq qedeq);

    /**
     * Get data transfer object of this BO.
     *
     * @return  QEDEQ module data.
     */
    public Qedeq getQedeq();

    /**
     * Get module label associations for this module.
     *
     * @return  Label associations.
     */
    public ModuleLabels getModuleLabels();

    /**
     * Get physical address of this module.
     *
     * @return  Module address..
     */
    public ModuleAddress getModuleAddress();

    /**
     * Get physical addresses of this module.
     *
     * @param   address Module address.
     */
    public void setModuleAddress(final ModuleAddress address);

}
