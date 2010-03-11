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

import org.qedeq.kernel.bo.NodeBo;
import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.common.ModuleContext;
import org.qedeq.kernel.dto.module.NodeVo;

/**
 * Business object for node access.
 * 
 * @author  Michael Meyling
 */
public class KernelNodeBo implements NodeBo {

    /** The plain node date. */
    private final NodeVo node;

    /** The module context the node is within. */
    private final ModuleContext context;

    /** Parent module the node is within. */
    private final KernelQedeqBo qedeq;

    /** Chapter number the node is within. */
    private final int chapterNumber;

    /** Axioms before node (including this one). */
    private final int axiomNumber;

    /** Function definitions before node (including this one). */
    private final int functionDefinitionNumber;

    /** Predicate definitions before node (including this one). */
    private final int predicateDefinitionNumber;

    /** Propositions before node (including this one). */
    private final int propositionNumber;

    /** Rule definitions before node (including this one). */
    private final int ruleNumber;


    public KernelNodeBo(final NodeVo node, final ModuleContext context, final KernelQedeqBo qedeq,
            final int chapterNumber, final int axiomNumber, final int ruleNumber,
            final int functionDefinitionNumber, final int predicateDefinitionNumber,
            final int propositionNumber) {
        this.node = node;
        this.context = new ModuleContext(context);
        this.qedeq = qedeq;
        this.chapterNumber = chapterNumber;
        this.axiomNumber = axiomNumber;
        this.ruleNumber = ruleNumber;
        this.functionDefinitionNumber = functionDefinitionNumber;
        this.predicateDefinitionNumber = predicateDefinitionNumber;
        this.propositionNumber = propositionNumber;
        
    }

    public NodeVo getNodeVo() {
        return node;
    }

    public ModuleContext getModuleContext() {
        return context;
    }

    public QedeqBo getParentQedeqBo() {
        return qedeq;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public int getAxiomNumber() {
        return axiomNumber;
    }

    public int getFunctionDefinitionNumber() {
        return functionDefinitionNumber;
    }

    public int getPredicateDefinitionNumber() {
        return predicateDefinitionNumber;
    }

    public int getPropositionNumber() {
        return propositionNumber;
    }

    public int getRuleNumber() {
        return ruleNumber;
    }

}
