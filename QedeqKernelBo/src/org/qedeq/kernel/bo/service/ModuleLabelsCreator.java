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
import org.qedeq.kernel.base.module.FunctionDefinition;
import org.qedeq.kernel.base.module.Import;
import org.qedeq.kernel.base.module.Node;
import org.qedeq.kernel.base.module.PredicateDefinition;
import org.qedeq.kernel.base.module.Proposition;
import org.qedeq.kernel.base.module.Rule;
import org.qedeq.kernel.bo.module.ControlVisitor;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.ModuleLabels;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.Plugin;
import org.qedeq.kernel.common.SourceFileExceptionList;
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

    /**
     * Constructor.
     *
     * @param   plugin      This plugin we work for.
     * @param   prop        Internal QedeqBo.
     */
    public ModuleLabelsCreator(final Plugin plugin, final KernelQedeqBo prop) {
        super(plugin, prop);
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
            addError(me);
            Trace.trace(CLASS, this, "visitEnter(Import)", me);
        }
    }

    /**
     * Increase axiom counter.
     *
     * @param   axiom               Visit this element.
     */
    public void visitEnter(final Axiom axiom) {
        setBlocked(true);   // block further traverse
    }

    /**
     * Increase proposition counter.
     *
     * @param   proposition         Begin visit of this element.
     */
    public void visitEnter(final Proposition proposition) {
        setBlocked(true);   // block further traverse
    }

    /**
     * Increase function definition counter.
     *
     * @param   funcDef             Begin visit of this element.
     */
    public void visitEnter(final FunctionDefinition funcDef) {
        setBlocked(true);   // block further traverse
    }

    /**
     * Increase predicate definition counter.
     *
     * @param   predDef             Begin visit of this element.
     */
    public void visitEnter(final PredicateDefinition predDef) {
        setBlocked(true);   // block further traverse
    }

    /**
     * Increase rule counter.
     *
     * @param   rule                Begin visit of this element.
     */
    public void visitEnter(final Rule rule) {
        setBlocked(true);   // block further traverse
    }

    /**
     * Add node and counter informations.
     *
     * @param  node                 End visit of this element.
     */
    public void visitLeave(final Node node) {
        try {
            this.labels.addNode(getCurrentContext(), (NodeVo) node, getQedeqBo(),
                getCurrentNumbers());
        } catch (ModuleDataException me) {
            addError(me);
            Trace.trace(CLASS, this, "visitEnter(Node)", me);
        }
        setBlocked(false);   // allow further traverse
    }

    /**
     * Get QEDEQ module labels.
     *
     * @return  QEDEQ module labels.
     * @throws  SourceFileExceptionList Traverse lead to errors.
     */
    public ModuleLabels createLabels() throws SourceFileExceptionList {
        if (this.labels == null) {
            this.labels = new ModuleLabels();
            traverse();
        }
        return this.labels;
    }

}

