/* $Id: QedeqBoFormalLogicChecker.java,v 1.6 2008/01/26 12:39:09 m31 Exp $
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

import org.qedeq.kernel.base.module.Axiom;
import org.qedeq.kernel.base.module.Formula;
import org.qedeq.kernel.base.module.FunctionDefinition;
import org.qedeq.kernel.base.module.PredicateDefinition;
import org.qedeq.kernel.base.module.Proposition;
import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.base.module.Rule;
import org.qedeq.kernel.base.module.Term;
import org.qedeq.kernel.bo.logic.ExistenceChecker;
import org.qedeq.kernel.bo.logic.FormulaChecker;
import org.qedeq.kernel.bo.logic.Function;
import org.qedeq.kernel.bo.logic.Predicate;
import org.qedeq.kernel.bo.module.DefaultModuleReferenceList;
import org.qedeq.kernel.bo.visitor.AbstractModuleVisitor;
import org.qedeq.kernel.bo.visitor.QedeqNotNullTraverser;
import org.qedeq.kernel.common.IllegalModuleDataException;
import org.qedeq.kernel.common.LogicalState;
import org.qedeq.kernel.common.ModuleContext;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.log.ModuleEventLog;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.xml.mapper.ModuleDataException2XmlFileException;


/**
 * Checks if all formulas of a QEDEQ module are well formed.
 *
 * @version $Revision: 1.6 $
 * @author  Michael Meyling
 */
public final class QedeqBoFormalLogicChecker extends AbstractModuleVisitor {

    /** This class. */
    private static final Class CLASS = QedeqBoFormalLogicChecker.class;

    /** QEDEQ module properties. */
    private final DefaultModuleProperties prop;

    /** Current context during creation. */
    private final QedeqNotNullTraverser traverser;

    /** Existence checker for predicate and function constants. */
    private ModuleConstantsExistenceChecker existence;

    /**
     * Constructor.
     *
     * @param   prop              QEDEQ module properties object.
     */
    private QedeqBoFormalLogicChecker(final DefaultModuleProperties prop) {
        this.traverser = new QedeqNotNullTraverser(prop.getModuleAddress(), this);
        this.prop = prop;
    }

    /**
     * Checks if all formulas of a QEDEQ module are well formed.
     *
     * @param   prop                QEDEQ module properties object.
     * @throws  SourceFileExceptionList      Major problem occurred.
     */
    public static void check(final DefaultModuleProperties prop)
            throws SourceFileExceptionList {
        if (prop.isChecked()) {
            return;
        }
        if (!prop.hasLoadedRequiredModules()) {
            throw new IllegalStateException("QEDEQ module has not loaded with required files: "
                + prop);
        }
        prop.setLogicalProgressState(LogicalState.STATE_EXTERNAL_CHECKING);
        ModuleEventLog.getInstance().stateChanged(prop);
        DefaultModuleReferenceList list = (DefaultModuleReferenceList) prop.getRequiredModules();
        for (int i = 0; i < list.size(); i++) {
            try {
                Trace.trace(CLASS, "check(ModuleProperties)", "checking label",
                    list.getLabel(i));
                check((DefaultModuleProperties) list.getModuleProperties(i));
            } catch (SourceFileExceptionList e) {   // TODO mime 20080114: hard coded codes
                ModuleDataException md = new CheckRequiredModuleException(11231,
                    "import check failed: " + list.getModuleProperties(i).getModuleAddress(),
                    list.getModuleContext(i));
                final SourceFileExceptionList sfl =
                    ModuleDataException2XmlFileException.createXmlFileExceptionList(md,
                    prop.getQedeq());
                prop.setLogicalFailureState(LogicalState.STATE_EXTERNAL_CHECKING_FAILED, sfl);
                ModuleEventLog.getInstance().stateChanged(prop);
                throw e;
            }
        }
        prop.setLogicalProgressState(LogicalState.STATE_INTERNAL_CHECKING);
        ModuleEventLog.getInstance().stateChanged(prop);
        final QedeqBoFormalLogicChecker checker = new QedeqBoFormalLogicChecker(prop);
        try {
            checker.check();
        } catch (ModuleDataException e) {
            final SourceFileExceptionList sfl =
                ModuleDataException2XmlFileException.createXmlFileExceptionList(e,
                prop.getQedeq());
            prop.setLogicalFailureState(LogicalState.STATE_INTERNAL_CHECKING_FAILED, sfl);
            ModuleEventLog.getInstance().stateChanged(prop);
            throw sfl;
        }
        prop.setChecked(checker.existence);
        ModuleEventLog.getInstance().stateChanged(prop);
    }

    private void check() throws ModuleDataException {
        this.existence = new ModuleConstantsExistenceChecker(prop);
        traverser.accept(prop.getQedeq());
    }

    public void visitEnter(final Axiom axiom) throws ModuleDataException {
        if (axiom == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        if (axiom.getFormula() != null) {
            setLocationWithinModule(context + ".getFormula().getElement()");
            final Formula formula = axiom.getFormula();
            FormulaChecker.checkFormula(formula.getElement(), getCurrentContext(), existence);
        }
        setLocationWithinModule(context);
        traverser.setBlocked(true);
    }

    public void visitLeave(final Axiom axiom) {
        traverser.setBlocked(false);
    }

    public void visitEnter(final PredicateDefinition definition)
            throws ModuleDataException {
        if (definition == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        final Predicate predicate = new Predicate(definition.getName(),
            definition.getArgumentNumber());
        if (existence.predicateExists(predicate)) {
            throw new IllegalModuleDataException(HigherLogicalErrors.PREDICATE_ALREADY_DEFINED,
                HigherLogicalErrors.PREDICATE_ALREADY_DEFINED_TEXT + predicate,
                getCurrentContext());
        }
        if ("2".equals(predicate.getArguments())
                && ExistenceChecker.NAME_EQUAL.equals(predicate.getName())) {
            existence.setIdentityOperatorDefined(true, predicate.getName());
        }
        if (definition.getFormula() != null) {
            setLocationWithinModule(context + ".getFormula().getElement()");
            final Formula formula = definition.getFormula();
            FormulaChecker.checkFormula(formula.getElement(), getCurrentContext(), existence);
        }
        existence.add(definition);
        setLocationWithinModule(context);
        traverser.setBlocked(true);
    }

    public void visitLeave(final PredicateDefinition definition) {
        traverser.setBlocked(false);
    }

    public void visitEnter(final FunctionDefinition definition)
            throws ModuleDataException {
        if (definition == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        final Function function = new Function(definition.getName(),
            definition.getArgumentNumber());
        if (existence.functionExists(function)) {
            throw new IllegalModuleDataException(HigherLogicalErrors.FUNCTION_ALREADY_DEFINED,
                HigherLogicalErrors.FUNCTION_ALREADY_DEFINED_TEXT + function,
                getCurrentContext());
        }
        if (definition.getTerm() != null) {
            setLocationWithinModule(context + ".getTerm().getElement()");
            final Term term = definition.getTerm();
            FormulaChecker.checkTerm(term.getElement(), getCurrentContext(), existence);
        }
        existence.add(definition);
        setLocationWithinModule(context);
        traverser.setBlocked(true);
    }

    public void visitLeave(final FunctionDefinition definition) {
        traverser.setBlocked(false);
    }

    public void visitEnter(final Proposition proposition)
            throws ModuleDataException {
        if (proposition == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        if (proposition.getFormula() != null) {
            setLocationWithinModule(context + ".getFormula().getElement()");
            final Formula formula = proposition.getFormula();
            FormulaChecker.checkFormula(formula.getElement(), getCurrentContext(), existence);
        }
        setLocationWithinModule(context);
        traverser.setBlocked(true);
    }

    public void visitLeave(final Proposition definition) {
        traverser.setBlocked(false);
    }

    public void visitEnter(final Rule rule) throws ModuleDataException {
        if (rule == null) {
            return;
        }
        if (rule.getName() != null) {
            if ("SET_DEFINION_BY_FORMULA".equals(rule.getName())) {
                // LATER mime 20080114: check if this rule can be proposed
                existence.setClassOperatorExists(true);
            }
        }
        traverser.setBlocked(true);
    }

    public void visitLeave(final Rule rule) {
        traverser.setBlocked(false);
    }

    /**
     * Set location information where are we within the original module.
     *
     * @param   locationWithinModule    Location within module.
     */
    public void setLocationWithinModule(final String locationWithinModule) {
        getCurrentContext().setLocationWithinModule(locationWithinModule);
    }

    /**
     * Get current context within original.
     *
     * @return  Current context.
     */
    public final ModuleContext getCurrentContext() {
        return traverser.getCurrentContext();
    }

    /**
     * Get original QEDEQ module.
     *
     * @return  Original QEDEQ module.
     */
    protected final Qedeq getQedeqOriginal() {
        return prop.getQedeq();
    }

}
