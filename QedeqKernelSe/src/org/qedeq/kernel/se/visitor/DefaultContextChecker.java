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
 * Basis implementation.
 */
public class DefaultContextChecker implements ContextChecker {

    /**
     * We don't check anything.
     *
     * @param   qedeq   QEDEQ module.
     * @param   context Current context.
     */
    public void checkContext(final Qedeq qedeq, final ModuleContext context) {
        // we don't check anything
    }

}

