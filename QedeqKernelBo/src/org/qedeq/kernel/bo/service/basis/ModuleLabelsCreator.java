/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.service.basis;

import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.common.Element2Latex;
import org.qedeq.kernel.bo.common.Element2Utf8;
import org.qedeq.kernel.bo.module.InternalServiceJob;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.ModuleLabels;
import org.qedeq.kernel.se.base.module.Axiom;
import org.qedeq.kernel.se.base.module.ChangedRule;
import org.qedeq.kernel.se.base.module.ChangedRuleList;
import org.qedeq.kernel.se.base.module.FunctionDefinition;
import org.qedeq.kernel.se.base.module.Import;
import org.qedeq.kernel.se.base.module.Node;
import org.qedeq.kernel.se.base.module.PredicateDefinition;
import org.qedeq.kernel.se.base.module.Proposition;
import org.qedeq.kernel.se.base.module.Rule;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.Service;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.dto.module.NodeVo;


/**
 * Create mapping from labels to {@link org.qedeq.kernel.se.dto.module.NodeVo} for a QEDEQ module.
 *
 * @author  Michael Meyling
 */
public final class ModuleLabelsCreator extends ControlVisitor {

    /** This class. */
    private static final Class CLASS = ModuleLabelsCreator.class;

    /** QEDEQ module labels, definitions, references, etc. */
    private ModuleLabels labels;

    /** Current node id. */
    private String nodeId = "";

    /**
     * Constructor.
     *
     * @param   service     This service we work for.
     * @param   prop        Internal QedeqBo.
     */
    public ModuleLabelsCreator(final Service service, final KernelQedeqBo prop) {
        super(service, prop);
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

    public void visitEnter(final Axiom axiom) {
        setBlocked(true);   // block further traverse
    }

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
        // we always save the definition, even if there already exists an entry
        labels.addFunction(funcDef, getCurrentContext());
    }

    public void visitEnter(final PredicateDefinition predDef) {
        setBlocked(true);   // block further traverse
        // we always save the definition, even if there already exists an entry
        labels.addPredicate(predDef, getCurrentContext());
    }

    public void visitEnter(final Rule rule) {
        setBlocked(true);   // block further traverse
        // we always save the definition, even if there already exists an entry
        labels.addRule(nodeId, rule, getCurrentContext());
        if (rule.getChangedRuleList() != null) {
            final ChangedRuleList list = rule.getChangedRuleList();
            for (int i = 0; i < list.size() && list.get(i) != null; i++) {
                final ChangedRule r = list.get(i);
                labels.addChangedRule(nodeId, rule, r, getCurrentContext());
            }
        }
    }

    public void visitEnter(final Node node) {
        nodeId = node.getId();
    }

    public void visitLeave(final Node node) {
        nodeId = "";
        try {
            labels.addNode(getCurrentContext(), (NodeVo) node, getKernelQedeqBo(),
                getCurrentNumbers());
        } catch (ModuleDataException me) {
            addError(me);
            Trace.trace(CLASS, this, "visitEnter(Node)", me);
        }
        setBlocked(false);   // allow further traverse
    }

    /**
     * Fill QEDEQ module labels and Element2Latex and Element2Utf8 converter.
     *
     * @param   process         We work for this process.
     * @param   labels          Herein the labels are filled.
     * @throws  SourceFileExceptionList Traverse lead to errors.
     */
    public void createLabels(final InternalServiceJob process, final ModuleLabels labels)
            throws SourceFileExceptionList {
        if (this.labels == null) {
            this.labels = labels;
            traverse(process);
        }
    }

    /**
     * Get QEDEQ module labels.
     *
     * @return  QEDEQ module labels. */
    public ModuleLabels getLabels() {
        return labels;
    }

}

