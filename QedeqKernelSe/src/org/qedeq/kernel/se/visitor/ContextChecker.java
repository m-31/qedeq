/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2013,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.se.visitor;

import org.qedeq.kernel.se.base.module.Qedeq;
import org.qedeq.kernel.se.common.ModuleContext;


/**
 * Check of current context is ok.
 */
public interface ContextChecker {

    /**
     * Check it this context is valid. Might throw a <code>RuntimeException</code> if context is
     * not valid.
     *
     * @param   qedeq   QEDEQ module.
     * @param   context Current context.
     */
    void checkContext(Qedeq qedeq, ModuleContext context);

}

