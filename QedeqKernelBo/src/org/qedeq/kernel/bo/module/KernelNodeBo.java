/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2013,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.kernel.bo.common.NodeBo;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.module.ConditionalProof;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.base.module.FormalProofList;
import org.qedeq.kernel.se.base.module.NodeType;
import org.qedeq.kernel.se.base.module.Proposition;
import org.qedeq.kernel.se.base.module.Reason;
import org.qedeq.kernel.se.common.CheckLevel;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.dto.module.NodeVo;
import org.qedeq.kernel.se.visitor.QedeqNumbers;

/**
 * Business object for node access.
 *
 * @author  Michael Meyling
 */
public class KernelNodeBo implements NodeBo, CheckLevel {

    /** The plain node data. */
    private final NodeVo node;

    /** The module context the node is within. */
    private final ModuleContext context;

    /** Parent module the node is within. */
    private final KernelQedeqBo qedeq;

    /** Herein are the results of various counters for the node. */
    private final QedeqNumbers data;

    /** Are all formal formulas of this node well formed.
     * See {@link CheckLevel} for value format. */
    private int wellFormedCheck = UNCHECKED;

    /** Is this node been successfully formally proved at least once.
     * See {@link CheckLevel} for value format. */
    private int provedCheck = UNCHECKED;


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
     * Get parent module the node is within.
     *
     * @return  Parent module the node is within.
     */
    public KernelQedeqBo getQedeqBo() {
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

    /**
     * // FIXME 20110316 m31: we have to solve the uniqueness problem if we have several formal proofs
     * Is the given name a proof line label within this node?
     *
     * @param   label   Look if this node is a proposition that contains this label name.
     * @return  Answer.
     */
    public boolean isProofLineLabel(final String label) {
        if (label == null || label.length() == 0) {
            return false;
        }
        final Proposition theorem = getNodeVo().getNodeType().getProposition();
        if (theorem == null) {
            return false;
        }
        final FormalProofList proofs = theorem.getFormalProofList();
        if (proofs == null) {
            return false;
        }
        // iterate through all formal proofs
        for (int i = 0; i < proofs.size(); i++) {
            final FormalProofLineList list = proofs.get(i).getFormalProofLineList();
            if (hasProofLineLabel(label, list)) {
                return true;
            }
        }
        // nowhere found:
        return false;
    }

    private boolean hasProofLineLabel(final String label, final FormalProofLineList list) {
        if (list == null) {
            return false;
        }
        for (int j = 0; j < list.size(); j++) {
            if (label.equals(list.get(j).getLabel())) {
                return true;
            }
            final Reason reason = list.get(j).getReason();
            if (reason instanceof ConditionalProof) {
                final ConditionalProof conditionalProof = (ConditionalProof) reason;
                if (conditionalProof.getHypothesis() != null) {
                    if (label.equals(conditionalProof.getHypothesis().getLabel())) {
                        return true;
                    }
                }
                final FormalProofLineList list2 = conditionalProof.getFormalProofLineList();
                if (hasProofLineLabel(label, list2)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Set proved parameter. See {@link #isWellFormed()}.
     *
     * @param   wellFormedCheck     Node well formed state.
     *          See {@link CheckLevel} for parameter format.
     */
    public void setWellFormed(final int wellFormedCheck) {
        this.wellFormedCheck = wellFormedCheck;
    }

    public boolean isWellFormed() {
        return wellFormedCheck >= SUCCESS;
    }

    public boolean isNotWellFormed() {
        return wellFormedCheck < SUCCESS && wellFormedCheck > UNCHECKED;
    }

    /**
     * Set proved parameter. See {@link #isProved()}.
     *
     * @param   provedCheck     Node proved state.
     *          See {@link CheckLevel} for parameter format.
     */
    public void setProved(final int provedCheck) {
        this.provedCheck = provedCheck;
    }

    public boolean isProved() {
        return provedCheck >= SUCCESS;
    }

    public boolean isNotProved() {
        return provedCheck < SUCCESS && provedCheck > UNCHECKED;
    }

    public boolean hasFormula() {
        return null != getFormula();
    }

    public Element getFormula() {
        if (getNodeVo() == null || getNodeVo().getNodeType() == null) {
            return null;
        }
        final NodeType nodeType = getNodeVo().getNodeType();
        if (nodeType.getProposition() != null) {
            if (nodeType.getProposition().getFormula() != null) {
                return nodeType.getProposition().getFormula().getElement();
            }
            return null;
        } else if (nodeType.getPredicateDefinition() != null) {
            if (nodeType.getPredicateDefinition().getFormula() != null) {
                return nodeType.getPredicateDefinition().getFormula().getElement();
            }
            return null;
        } else if (nodeType.getFunctionDefinition() != null) {
            if (nodeType.getFunctionDefinition().getFormula() != null) {
                return nodeType.getFunctionDefinition().getFormula().getElement();
            }
            return null;
        } else if (nodeType.getAxiom() != null) {
            if (nodeType.getAxiom().getFormula() != null) {
                return nodeType.getAxiom().getFormula().getElement();
            }
            return null;
        }
        return null;
    }
}
