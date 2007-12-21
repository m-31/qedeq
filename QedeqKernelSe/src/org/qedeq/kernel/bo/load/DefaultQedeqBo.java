/* $Id: DefaultQedeqBo.java,v 1.2 2007/12/21 23:33:46 m31 Exp $
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

package org.qedeq.kernel.bo.load;

import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.bo.module.ModuleAddress;
import org.qedeq.kernel.bo.module.ModuleLabels;
import org.qedeq.kernel.bo.module.QedeqBo;
import org.qedeq.kernel.utility.EqualsUtility;


/**
 * A complete QEDEQ module. This is the root business object.
 *
 * TODO mime 20070131: shouldn't be a globalContext also an attribute of this class?
 *
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */
public class DefaultQedeqBo implements QedeqBo {

    /** QEDEQ data transfer object. */
    private Qedeq qedeq;

    /** All module labels and their business objects. */
    private final ModuleLabels moduleLabels;

    /** Module address information. */
    private ModuleAddress moduleAddress;

    /**
     * Constructs a new empty QEDEQ module.
     */
    public DefaultQedeqBo() {
        moduleLabels = new ModuleLabels();
    }

    public void setQedeq(final Qedeq qedeq) {
        this.qedeq = qedeq;
    }

    public Qedeq getQedeq() {
        return qedeq;
    }

    /**
     * Get module label associations for this module.
     *
     * @return  Label associations.
     */
    public final ModuleLabels getModuleLabels() {
        return moduleLabels;
    }

    /**
     * Get physical addresses of this module.
     *
     * @return  Module address..
     */
    public final ModuleAddress getModuleAddress() {
        return moduleAddress;
    }

    /**
     * Get physical addresses of this module.
     *
     * @param   address Module address.
     */
    public final void setModuleAddress(final ModuleAddress address) {
        this.moduleAddress = address;
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof DefaultQedeqBo)) {
            return false;
        }
        final DefaultQedeqBo other = (DefaultQedeqBo) obj;
        return  EqualsUtility.equals(getQedeq(), other.getQedeq())
            &&  EqualsUtility.equals(getModuleAddress(), other.getModuleAddress());
    }

    public int hashCode() {
        return (getQedeq() != null ? getQedeq().hashCode() : 0)
            ^ (getModuleAddress() != null ? 1 ^ getModuleAddress().hashCode() : 0);
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(getModuleAddress() + "\n");
        buffer.append(getQedeq() + "\n\n");
        return buffer.toString();
    }

}
