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
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.ModuleLabels;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.ModuleContext;
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

    /** Chapter numbering currently on? */
    private boolean chapterNumbering;

    /** Chapter number. */
    private int chapterNumber;

    /** Rule number. */
    private int ruleNumber;

    /** Axiom number. */
    private int axiomNumber;

    /** Proposition number. */
    private int propositionNumber;

    /** Function definition number. */
    private int functionDefinitionNumber;

    /** Predicate definition number. */
    private int predicateDefinitionNumber;

    /**
     * Constructor.
     *
     * @param   prop        Internal QedeqBo.
     */
    public ModuleLabelsCreator(final KernelQedeqBo prop) {
        super(prop);
    }

    public void visitEnter(final Qedeq qedeq) {
        chapterNumber = 0;
        ruleNumber = 0;
        axiomNumber = 0;
        predicateDefinitionNumber = 0;
        functionDefinitionNumber = 0;
        propositionNumber = 0;
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
     * Visit chapter. Increases chapter number, if this chapter doesn't forbid it.
     *
     * @param   chapter             Visit this chapter.
     */
    public void visitEnter(final Chapter chapter) {
        if (Boolean.TRUE.equals(chapter.getNoNumber())) {
            chapterNumbering = true;
            chapterNumber++;
        } else {
            chapterNumbering = false;
        }
    }

    /**
     * Visit chapter. Increases axiom number.
     *
     * @param   chapter             Visit this chapter.
     */
    public void visitEnter(final Axiom axiom) {
        axiomNumber++;
        setBlocked(true);   // block further traverse
    }

    /**
     * Visit chapter. Increases chapter number, if this chapter doesn't forbid it.
     *
     * @param   chapter             Visit this chapter.
     */
    public void visitEnter(final Proposition proposition) {
        propositionNumber++;
        setBlocked(true);   // block further traverse
    }

    /**
     * Visit import. Loads referenced QEDEQ module and saves reference.
     *
     * @param   funcDef             Begin visit of this element.
     */
    public void visitEnter(final FunctionDefinition funcDef) {
        functionDefinitionNumber++;
        setBlocked(true);   // block further traverse
    }

    /**
     * Visit import. Loads referenced QEDEQ module and saves reference.
     *
     * @param   predDef             Begin visit of this element.
     */
    public void visitEnter(final PredicateDefinition predDef) {
        predicateDefinitionNumber++;
        setBlocked(true);   // block further traverse
    }

    public void visitLeave(final Node node) throws ModuleDataException {
        try {
            this.labels.addNode(getCurrentContext(), (NodeVo) node,
                (chapterNumbering ? chapterNumber : -1), ruleNumber,
                propositionNumber, axiomNumber, predicateDefinitionNumber,
                functionDefinitionNumber);
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

