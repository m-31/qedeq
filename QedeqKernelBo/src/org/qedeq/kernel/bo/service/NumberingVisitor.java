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

import org.qedeq.kernel.base.module.Axiom;
import org.qedeq.kernel.base.module.Chapter;
import org.qedeq.kernel.base.module.FunctionDefinition;
import org.qedeq.kernel.base.module.Node;
import org.qedeq.kernel.base.module.PredicateDefinition;
import org.qedeq.kernel.base.module.Proposition;
import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.base.module.Rule;
import org.qedeq.kernel.base.module.Section;
import org.qedeq.kernel.base.module.Subsection;
import org.qedeq.kernel.bo.module.ControlVisitor;
import org.qedeq.kernel.bo.module.KernelNodeNumbers;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.common.Plugin;

/**
 * Create mapping from labels to {@link org.qedeq.kernel.dto.module.NodeVo} for a QEDEQ module.
 * FIXME 20101104 m31: transfer this functionality into {@link org.qedeq.kernel.visitor.QedeqNotNullTraverser}.
 * @author  Michael Meyling
 */
public class NumberingVisitor extends ControlVisitor {

    /** This class. */
    private static final Class CLASS = NumberingVisitor.class;

    /** Herein are various counters for the current node. */
    private KernelNodeNumbers data = new KernelNodeNumbers();

    /**
     * Constructor.
     *
     * @param   plugin      This plugin we work for.
     * @param   prop        Internal QedeqBo.
     */
    public NumberingVisitor(final Plugin plugin, final KernelQedeqBo prop) {
        super(plugin, prop);
    }

    public void visitEnter(final Qedeq qedeq) {
        data = new KernelNodeNumbers();
    }

    /**
     * Visit chapter. Increases chapter number, if this chapter doesn't forbid it.
     * Also increases absolute chapter number.
     *
     * @param   chapter             Visit this chapter.
     */
    public void visitEnter(final Chapter chapter) {
        data.increaseAbsoluteChapterNumber();
        if (Boolean.TRUE.equals(chapter.getNoNumber())) {
            data.setChapterNumbering(false);
        } else {
            data.setChapterNumbering(true);
            data.increaseChapterNumber();
        }
    }

    /**
     * Visit section. Increases section number, if this section doesn't forbid it.
     * Also increases absolute section number.
     *
     * @param   section             Visit this section.
     */
    public void visitEnter(final Section section) {
        data.increaseAbsoluteSectionNumber();
        if (Boolean.TRUE.equals(section.getNoNumber())) {
            data.setSectionNumbering(false);
        } else {
            data.setSectionNumbering(true);
            data.increaseSectionNumber();
        }
    }

    /**
     * Visit subsection. Increases subsection number.
     *
     * @param   subsection          Visit this subsection.
     */
    public void visitEnter(final Subsection subsection) {
        data.increaseSubsectionNumber();
    }

    /**
     * Visit node subsection. Increases subsection number.
     *
     * @param   node            Visit this subsection.
     */
    public void visitEnter(final Node node) {
        data.increaseSubsectionNumber();
    }

    /**
     * Increase axiom counter.
     *
     * @param   axiom               Visit this element.
     */
    public void visitEnter(final Axiom axiom) {
        data.increaseAxiomNumber();
        setBlocked(true);   // block further traverse
    }

    /**
     * Increase proposition counter.
     *
     * @param   proposition         Begin visit of this element.
     */
    public void visitEnter(final Proposition proposition) {
        data.increasePropositionNumber();
    }

    /**
     * Increase function definition counter.
     *
     * @param   funcDef             Begin visit of this element.
     */
    public void visitEnter(final FunctionDefinition funcDef) {
        data.increaseFunctionDefinitionNumber();
    }

    /**
     * Increase predicate definition counter.
     *
     * @param   predDef             Begin visit of this element.
     */
    public void visitEnter(final PredicateDefinition predDef) {
        data.increasePredicateDefinitionNumber();
    }

    /**
     * Increase rule counter.
     *
     * @param   rule                Begin visit of this element.
     */
    public void visitEnter(final Rule rule) {
        data.increaseRuleNumber();
    }

    public KernelNodeNumbers getCurrentNumbers() {
        return new KernelNodeNumbers(data);
    }

}

