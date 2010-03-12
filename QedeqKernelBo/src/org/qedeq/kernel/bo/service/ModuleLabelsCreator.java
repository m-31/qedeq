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

package org.qedeq.kernel.bo.service;

import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.base.module.Axiom;
import org.qedeq.kernel.base.module.Chapter;
import org.qedeq.kernel.base.module.FunctionDefinition;
import org.qedeq.kernel.base.module.Import;
import org.qedeq.kernel.base.module.Node;
import org.qedeq.kernel.base.module.PredicateDefinition;
import org.qedeq.kernel.base.module.Proposition;
import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.bo.module.ControlVisitor;
import org.qedeq.kernel.bo.module.KernelNodeNumbers;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.ModuleLabels;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.dto.module.NodeVo;


/**
 * Create mapping from labels to {@link org.qedeq.kernel.dto.module.NodeVo} for a QEDEQ module.
 *
 * @author  Michael Meyling
 */
public final class ModuleLabelsCreator extends ControlVisitor {

    /** This class. */
    private static final Class CLASS = ModuleLabelsCreator.class;

    /** QEDEQ module labels. */
    private ModuleLabels labels;

    private KernelNodeNumbers data = new KernelNodeNumbers();

    /**
     * Constructor.
     *
     * @param   prop        Internal QedeqBo.
     */
    public ModuleLabelsCreator(final KernelQedeqBo prop) {
        super(prop);
    }

    public void visitEnter(final Qedeq qedeq) {
        data = new KernelNodeNumbers();
    }

    /**
     * Visit import. Loads referenced QEDEQ module and saves reference.
     *
     * @param   imp                 Begin visit of this element.
     */
    public void visitEnter(final Import imp) {
        try {
            this.labels.addLabel(getCurrentContext(),
                imp.getLabel());
        } catch (ModuleDataException me) {
            addModuleDataException(me);
            Trace.trace(CLASS, this, "visitEnter(Import)", me);
        }
    }

    /**
     * Visit chapter. Increases chapter number, if this chapter doesn't forbid it.
     *
     * @param   chapter             Visit this chapter.
     */
    public void visitEnter(final Chapter chapter) {
        if (Boolean.TRUE.equals(chapter.getNoNumber())) {
            data.setChapterNumbering(false);
        } else {
            data.setChapterNumbering (true);
            data.increaseChapterNumber();
        }
    }

    /**
     * Visit chapter. Increases axiom number.
     *
     * @param   chapter             Visit this chapter.
     */
    public void visitEnter(final Axiom axiom) {
        data.increaseAxiomNumber();
        setBlocked(true);   // block further traverse
    }

    /**
     * Visit chapter. Increases chapter number, if this chapter doesn't forbid it.
     *
     * @param   chapter             Visit this chapter.
     */
    public void visitEnter(final Proposition proposition) {
        data.increasePropositionNumber();
        setBlocked(true);   // block further traverse
    }

    /**
     * Visit import. Loads referenced QEDEQ module and saves reference.
     *
     * @param   funcDef             Begin visit of this element.
     */
    public void visitEnter(final FunctionDefinition funcDef) {
        data.increaseFunctionDefinitionNumber();
        setBlocked(true);   // block further traverse
    }

    /**
     * Visit import. Loads referenced QEDEQ module and saves reference.
     *
     * @param   predDef             Begin visit of this element.
     */
    public void visitEnter(final PredicateDefinition predDef) {
        data.increasePredicateDefinitionNumber();
        setBlocked(true);   // block further traverse
    }

    public void visitLeave(final Node node) throws ModuleDataException {
        try {
            this.labels.addNode(getCurrentContext(), (NodeVo) node, getQedeqBo(), data);
        } catch (ModuleDataException me) {
            addModuleDataException(me);
            Trace.trace(CLASS, this, "visitEnter(Node)", me);
        }
        setBlocked(false);   // allow further traverse
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

}

