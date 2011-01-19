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
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.dto.module.NodeVo;
import org.qedeq.kernel.se.visitor.QedeqNumbers;

/**
 * Business object for node access.
 *
 * @author  Michael Meyling
 */
public class KernelNodeBo {

    /** The plain node data. */
    private final NodeVo node;

    /** The module context the node is within. */
    private final ModuleContext context;

    /** Parent module the node is within. */
    private final KernelQedeqBo qedeq;

    /** Herein are the results of various counters for the node. */
    private final QedeqNumbers data;


    /**
     * Constructor.
     *
     * @param   node    The plain node data.
     * @param   context The module context the node is within.
     * @param   qedeq   Parent module the node is within.
     * @param   data    Herein are the results of various counters for the node.
     */
    public KernelNodeBo(final NodeVo node, final ModuleContext context, final KernelQedeqBo qedeq,
            final QedeqNumbers data) {
        this.node = node;
        this.context = new ModuleContext(context);
        this.qedeq = qedeq;
        this.data = new QedeqNumbers(data);
    }

    /**
     * Get plain node data.
     *
     * @return  The plain node data.
     */
    public NodeVo getNodeVo() {
        return node;
    }

    /**
     * Get module context the node is within.
     *
     * @return  The module context the node is within.
     */
    public ModuleContext getModuleContext() {
        return context;
    }

    /**
     * Get parent module the node is within.
     *
     * @return  Parent module the node is within.
     */
    public QedeqBo getParentQedeqBo() {
        return qedeq;
    }

    /**
     * Get the results of various counters for the node.
     *
     * @return  Herein are the results of various counters for the node.
     */
    public QedeqNumbers getNumbers() {
        return data;
    }

}
