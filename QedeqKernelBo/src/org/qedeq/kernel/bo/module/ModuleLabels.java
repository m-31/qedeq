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

import java.util.HashMap;
import java.util.Map;

import org.qedeq.kernel.common.IllegalModuleDataException;
import org.qedeq.kernel.common.ModuleContext;
import org.qedeq.kernel.dto.module.NodeVo;

/**
 * Maps labels of an QEDEQ module to their nodes. Knows all label names.
 *
 * @author  Michael Meyling
 */
public final class ModuleLabels {

    /** Maps labels to business objects. */
    private final Map label2Bo;

    /** Maps labels to context of business objects. */
    private final Map label2Context;

    /**
     * Constructs a new empty module label list.
     */
    public ModuleLabels() {
        label2Bo = new HashMap();
        label2Context = new HashMap();
    }

    /**
     * Add node with certain id. All numbers should start with 1.
     *
     * @param   node        Add this node.
     * @param   context     The node has this context.
     * @param   qedeq       Parent module the node is within.
     * @param   data        Various number counters.
     * @throws  IllegalModuleDataException  The <code>id</code> already exists (perhaps as a label)
     *          or is <code>null</code>.
     */
    public final void addNode(final ModuleContext context, final NodeVo node, final KernelQedeqBo qedeq,
            final KernelNodeNumbers data) throws IllegalModuleDataException {
        // don't forget to use the copy constructor because the context could change!
        final ModuleContext con = new ModuleContext(context);
        if (null == node.getId()) {
            throw new IllegalModuleDataException(10001, "An id was not defined.", con, null,
                null);  // LATER mime 20071026: organize exception codes
        }
        checkLabelIntern(con, node.getId());
        label2Context.put(node.getId(), con);
        final KernelNodeBo nodeBo = new KernelNodeBo(node, context, qedeq, data);
        label2Bo.put(node.getId(), nodeBo);
    }

    /**
     * Add unique label for module.
     *
     * @param   label   Add this label.
     * @param   context With this context.
     * @throws  IllegalModuleDataException  The <code>id</code> already exists or is <code>null</code>.
     */
    public final void addLabel(final ModuleContext context,  final String label)
            throws IllegalModuleDataException {
        // don't forget to use the copy constructor because the context could change!
        final ModuleContext con = new ModuleContext(context);
        checkLabelIntern(con, label);
        label2Context.put(label, con);
    }

    /**
     * Check that label doesn't exist.
     *
     * @param   label   Check this label.
     * @param   context With this context (already copied).
     * @throws  IllegalModuleDataException  The <code>id</code> already exists or is
     *          <code>null</code>.
     */
    private final void checkLabelIntern(final ModuleContext context,  final String label)
            throws IllegalModuleDataException {
        if (label2Context.containsKey(label)) {
            // LATER mime 20071026: organize exception codes
            throw new IllegalModuleDataException(10002, "Id or label \"" + label
                + "\" defined more than once.", context,
                (ModuleContext) label2Context.get(label), null);
        }
    }

    /**
     * Get node for given id.
     *
     * @param   id   Label to search node for.
     * @return  Node for given label. Maybe <code>null</code>.
     */
    public final KernelNodeBo getNode(final String id) {
        return (KernelNodeBo) label2Bo.get(id);
    }

    /**
     * Is the given label id a node?
     *
     * @param   id   Label to search node for.
     * @return  Is this an node of this module?
     */
    public final boolean isNode(final String id) {
        return label2Bo.get(id) != null;
    }

    /**
     * Is the given label id a module?
     *
     * @param   id   Label to search module reference for.
     * @return  Is this an module reference id?
     */
    public final boolean isModule(final String id) {
        return label2Bo.get(id) == null && label2Context.get(id) != null;
    }

}
