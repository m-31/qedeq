/* $Id: ModuleLabelsCreator.java,v 1.1 2008/07/26 07:58:28 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2008,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.service;

import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.base.module.FunctionDefinition;
import org.qedeq.kernel.base.module.Import;
import org.qedeq.kernel.base.module.Node;
import org.qedeq.kernel.base.module.PredicateDefinition;
import org.qedeq.kernel.bo.module.ControlVisitor;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.ModuleContext;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.ModuleLabels;
import org.qedeq.kernel.dto.module.NodeVo;


/**
 * Create mapping from labels to {@link org.qedeq.kernel.dto.module.NodeVo} for a QEDEQ module.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public final class ModuleLabelsCreator extends ControlVisitor {

    /** This class. */
    private static final Class CLASS = ModuleLabelsCreator.class;

    /** QEDEQ module labels. */
    private ModuleLabels labels;

    /**
     * Constructor.
     *
     * @param   prop        Internal QedeqBo.
     */
    public ModuleLabelsCreator(final KernelQedeqBo prop) {
        super(prop);
    }

    /**
     * Visit import. Loads referenced QEDEQ module and saves reference.
     *
     * @param   imp                 Begin visit of this element.
     */
    public void visitEnter(final Import imp) {
        try {
            this.labels.addLabel(new ModuleContext(getCurrentContext()),
                imp.getLabel());
            Trace.param(CLASS, "visitEnter(Import)", "adding context", getCurrentContext());
        } catch (ModuleDataException me) {
            addModuleDataException(me);
            Trace.trace(CLASS, this, "visitEnter(Import)", me);
        }
    }

    /**
     * Visit import. Loads referenced QEDEQ module and saves reference.
     *
     * @param   funcDef             Begin visit of this element.
     */
    public void visitEnter(final FunctionDefinition funcDef) {
        try {
            this.labels.checkLabel(new ModuleContext(getCurrentContext()),
                funcDef.getName());
            Trace.param(CLASS, "visitEnter(FunctionDefinition)", "adding context",
                getCurrentContext());
        } catch (ModuleDataException me) {
            addModuleDataException(me);
            Trace.trace(CLASS, this, "visitEnter(FunctionDefinition)", me);
        }
    }

    /**
     * Visit import. Loads referenced QEDEQ module and saves reference.
     *
     * @param   predDef             Begin visit of this element.
     */
    public void visitEnter(final PredicateDefinition predDef) {
        try {
            this.labels.checkLabel(new ModuleContext(getCurrentContext()),
                predDef.getName());
            Trace.param(CLASS, "visitEnter(PredicateDefinition)", "adding context",
                getCurrentContext());
        } catch (ModuleDataException me) {
            addModuleDataException(me);
            Trace.trace(CLASS, this, "visitEnter(PredicateDefinition)", me);
        }
    }

    /**
     * Get QEDEQ module labels.
     *
     * @return  QEDEQ module labels.
     * @throws  DefaultSourceFileExceptionList  Traverse lead to errors.
     */
    public ModuleLabels createLabels() throws DefaultSourceFileExceptionList {
        if (this.labels == null) {
            this.labels = new ModuleLabels();
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
