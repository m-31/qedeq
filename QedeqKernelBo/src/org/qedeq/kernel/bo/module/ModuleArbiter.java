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

package org.qedeq.kernel.bo.module;

import org.qedeq.kernel.bo.common.QedeqBoSet;
import org.qedeq.kernel.bo.common.ServiceJob;
import org.qedeq.kernel.se.visitor.InterruptException;

/**
 * Get locks for modules.
 *
 * @author  Michael Meyling
 */
public interface ModuleArbiter {

    /**
     * Lock QEDEQ module for exclusive read and write access.
     *
     * @param   call    This service call acquires the lock.
     * @return  The call locked this module newly. Before this call the module was not locked.
     * @throws  InterruptException  Lock acquirement interrupted.
     */
    public boolean lockRequiredModule(InternalModuleServiceCall call) throws InterruptException;

    /**
     * Unlock module again.
     *
     * @param   call    This call must have acquired the lock.
     * @return  Was this module even locked?
     */
    public boolean unlockRequiredModule(InternalModuleServiceCall call);

    /**
     * Get all blocked modules from given process.
     *
     * @param   process Get all modules blocked by this job.
     *
     * @return  Set of blocked modules.
     */
    public QedeqBoSet getBlockedModules(ServiceJob process);

}
