/* $Id: LoadRequiredModules.java,v 1.2 2008/01/26 12:39:09 m31 Exp $
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

package org.qedeq.kernel.bo.control;

import org.qedeq.kernel.base.module.Node;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.ModuleNodes;
import org.qedeq.kernel.dto.module.NodeVo;
import org.qedeq.kernel.trace.Trace;


/**
 * Create mapping from labels to {@link org.qedeq.kernel.dto.module.NodeVo} for a QEDEQ module.
 *
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */
public final class ModuleNodesCreator extends ControlVisitor {

    /** This class. */
    private static final Class CLASS = ModuleNodesCreator.class;

    /** QEDEQ module labels. */
    private ModuleNodes labels;

    /**
     * Constructor.
     *
     * @param   prop        Internal QedeqBo.
     */
    public ModuleNodesCreator(final DefaultQedeqBo prop) {
        super(prop);
    }

    /**
     * Get QEDEQ module labels.
     *
     * @return  QEDEQ module labeles.
     * @throws  DefaultSourceFileExceptionList  Traverse lead to errors.
     */
    public ModuleNodes createLabels() throws DefaultSourceFileExceptionList {
        if (this.labels == null) {
            this.labels = new ModuleNodes();
            traverse();
        }
        return this.labels;
    }

    public void visitEnter(final Node node) throws ModuleDataException {
        try {
            this.labels.addNode(getCurrentContext(), (NodeVo) node);
        } catch (ModuleDataException me) {
            addModuleDataException(me);
            Trace.trace(CLASS, this, "visitEnter(Node)", me);
        }
        setBlocked(true);   // block further traverse of sub nodes
    }

    public void visitLeave(final Node node) {
        setBlocked(false);  // allow further traverse
    }

}
