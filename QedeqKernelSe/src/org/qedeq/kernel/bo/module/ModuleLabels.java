/* $Id: ModuleLabels.java,v 1.3 2007/04/12 23:50:04 m31 Exp $
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

import java.util.HashMap;
import java.util.Map;

import org.qedeq.kernel.dto.module.NodeVo;


/**
 * Maps labels of an module to their elements.
 *
 * @version $Revision: 1.3 $
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
     * Add node with certain id.
     *
     * @param   node    For this node.
     * @param   context With this context.
     * @throws  IllegalModuleDataException  The <code>id</code> already exists or is
     *          <code>null</code>.
     */
    public final void addNode(final ModuleContext context,  final NodeVo node)
            throws IllegalModuleDataException {
        if (null == node.getId()) {
            throw new IllegalModuleDataException(10001, "An id was not defined.", context, null,
                null);  // LATER mime 20071026: organize exception codes
        }
        if (label2Bo.containsKey(node.getId())) {
            // LATER mime 20071026: organize exception codes
            throw new IllegalModuleDataException(10002, "Id \"" + node.getId()
                + "\" defined more than once.", context,
                (ModuleContext) label2Context.get(node.getId()), null);
        }
        label2Bo.put(node.getId(), node);
        // don't forget to use the copy constructor because the context could change!
        label2Context.put(node.getId(), new ModuleContext(context));
    }

    /**
     * Get node for given id.
     *
     * @param   id   Label to search node for.
     * @return  Node for given label. Maybe <code>null</code>.
     */
    public final NodeVo getNode(final String id) {
        return (NodeVo) label2Bo.get(id);
    }

}
