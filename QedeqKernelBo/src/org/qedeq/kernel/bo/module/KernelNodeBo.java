/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2010,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.common.ModuleContext;
import org.qedeq.kernel.dto.module.NodeVo;

/**
 * Business object for node access.
 *
 * @author  Michael Meyling
 */
public class KernelNodeBo {

    /** The plain node date. */
    private final NodeVo node;

    /** The module context the node is within. */
    private final ModuleContext context;

    /** Parent module the node is within. */
    private final KernelQedeqBo qedeq;

    /** Herein are the results of various counters for the node. */
    private KernelNodeNumbers data = new KernelNodeNumbers();


    public KernelNodeBo(final NodeVo node, final ModuleContext context, final KernelQedeqBo qedeq,
            final KernelNodeNumbers data) {
        this.node = node;
        this.context = new ModuleContext(context);
        this.qedeq = qedeq;
        this.data = new KernelNodeNumbers(data);
    }

    public NodeVo getNodeVo() {
        return node;
    }

    public ModuleContext getModuleContext() {
        return context;
    }

    public QedeqBo getParentQedeqBo() {
        return qedeq;
    }

    public KernelNodeNumbers getNumbers() {
        return data;
    }
}

