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
     * Add node with certain id.
     *
     * @param   node    For this node.
     * @param   context With this context.
     * @param   chapterNumber
     * @param   functionDefinitionNumber 
     * @param   predicateDefinitionNumber 
     * @param   axiomNumber 
     * @param   propositionNumber 
     * @param   ruleNumber 
     * @throws  IllegalModuleDataException  The <code>id</code> already exists (perhaps as a label)
     *          or is <code>null</code>.
     */
    public final void addNode(final ModuleContext context, final NodeVo node, final int chapterNumber,
            final int ruleNumber, final int propositionNumber, final int axiomNumber,
            final int predicateDefinitionNumber, final int functionDefinitionNumber)
            throws IllegalModuleDataException {
        // FIXME m31 20100309: use these parameters, put them into new NodeBo and make them usable for LaTeX generation
        
        // don't forget to use the copy constructor because the context could change!
        final ModuleContext con = new ModuleContext(context);
        if (null == node.getId()) {
            throw new IllegalModuleDataException(10001, "An id was not defined.", con, null,
                null);  // LATER mime 20071026: organize exception codes
        }
        checkLabelIntern(con, node.getId());
        label2Context.put(node.getId(), con);
        label2Bo.put(node.getId(), node);
    }

    /**
     * Add unique label for module.
     *
     * @param   label   Add this label.
     * @param   context With this context.
     * @throws  IllegalModuleDataException  The <code>id</code> already exists or is
     *          <code>null</code>.
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
     * @param   context With this context.
     * @throws  IllegalModuleDataException  The <code>id</code> already exists or is
     *          <code>null</code>.
     */
    public final void checkLabel(final ModuleContext context,  final String label)
            throws IllegalModuleDataException {
        // don't forget to use the copy constructor because the context could change!
        final ModuleContext con = new ModuleContext(context);
        checkLabelIntern(con, label);
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
    public final NodeVo getNode(final String id) {
        return (NodeVo) label2Bo.get(id);
    }

}
