/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.service.heuristic;

import java.util.Map;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.bo.common.PluginExecutor;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.logic.common.Operators;
import org.qedeq.kernel.bo.logic.model.DynamicDirectInterpreter;
import org.qedeq.kernel.bo.logic.model.DynamicModel;
import org.qedeq.kernel.bo.logic.model.FourDynamicModel;
import org.qedeq.kernel.bo.logic.model.FunctionConstant;
import org.qedeq.kernel.bo.logic.model.HeuristicErrorCodes;
import org.qedeq.kernel.bo.logic.model.HeuristicException;
import org.qedeq.kernel.bo.logic.model.PredicateConstant;
import org.qedeq.kernel.bo.module.ControlVisitor;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.PluginBo;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.module.Axiom;
import org.qedeq.kernel.se.base.module.FormalProofLine;
import org.qedeq.kernel.se.base.module.FunctionDefinition;
import org.qedeq.kernel.se.base.module.Latex;
import org.qedeq.kernel.se.base.module.LatexList;
import org.qedeq.kernel.se.base.module.Node;
import org.qedeq.kernel.se.base.module.PredicateDefinition;
import org.qedeq.kernel.se.base.module.Proposition;
import org.qedeq.kernel.se.base.module.Rule;
import org.qedeq.kernel.se.base.module.VariableList;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.dto.list.DefaultAtom;
import org.qedeq.kernel.se.dto.list.DefaultElementList;


/**
 * Check if formulas are valid in our model.
 *
 * @author  Michael Meyling
 */
public final class DynamicHeuristicCheckerExecutor extends ControlVisitor implements PluginExecutor {

    /** This class. */
    private static final Class CLASS = DynamicHeuristicCheckerExecutor.class;

    /** Interpretation for variables. */
    private final DynamicDirectInterpreter interpreter;

    /**
     * Constructor.
     *
     * @param   plugin      This plugin we work for.
     * @param   qedeq       QEDEQ module object.
     * @param   parameters  Execution parameters.
     */
    DynamicHeuristicCheckerExecutor(final PluginBo plugin, final KernelQedeqBo qedeq,
            final Map parameters) {
        super(plugin, qedeq);
        final String method = "DynamicHeuristicChecker(PluginBo, QedeqBo, Map)";
        final String modelClass
            = (parameters != null ? (String) parameters.get("model") : null);
        DynamicModel model = null;
        if (modelClass != null && modelClass.length() > 0) {
            try {
                Class cl = Class.forName(modelClass);
                model = (DynamicModel) cl.newInstance();
            } catch (ClassNotFoundException e) {
                Trace.fatal(CLASS, this, method, "Model class not in class path: "
                    + modelClass, e);
            } catch (InstantiationException e) {
                Trace.fatal(CLASS, this, method, "Model class could not be instanciated: "
                    + modelClass, e);
            } catch (IllegalAccessException e) {
                Trace.fatal(CLASS, this, method,
                    "Programming error, access for instantiation failed for model: "
                    + modelClass, e);
            } catch (RuntimeException e) {
                Trace.fatal(CLASS, this, method,
                    "Programming error, instantiation failed for model: " + modelClass, e);
            }
        }
        // fallback is the default model
        if (model == null) {
            model = new FourDynamicModel();
        }
        this.interpreter = new DynamicDirectInterpreter(qedeq, model);
    }

    public Object executePlugin() {
        final String method = "executePlugin()";
        final String ref = "\"" + IoUtility.easyUrl(getQedeqBo().getUrl()) + "\"";
        try {
            QedeqLog.getInstance().logRequest("Dynamic heuristic test for " + ref);
            // first we try to get more information about required modules and their predicates..
            try {
                KernelContext.getInstance().checkModule(getQedeqBo().getModuleAddress());
            } catch (Exception e) {
                // we continue and ignore external predicates
                Trace.trace(CLASS, method, e);
            }
            traverse();
            QedeqLog.getInstance().logSuccessfulReply(
                "Heuristic test succesfull for " + ref);
        } catch (final SourceFileExceptionList e) {
            final String msg = "Test failed for " + ref;
            Trace.fatal(CLASS, this, method, msg, e);
            QedeqLog.getInstance().logFailureReply(msg, e.getMessage());
        } catch (final RuntimeException e) {
            Trace.fatal(CLASS, this, method, "unexpected problem", e);
            QedeqLog.getInstance().logFailureReply(
                "Test failed for " + ref, "unexpected problem: "
                + (e.getMessage() != null ? e.getMessage() : e.toString()));
        } finally {
            getQedeqBo().addPluginErrorsAndWarnings(getPlugin(), getErrorList(), getWarningList());
        }
        return null;
    }

    /**
     * Check truth value in our model. If it is no tautology an warning is added.
     * This also happens if our model doesn't support an operator found in the formula.
     *
     * @param   test    Test formula.
     */
    private void test(final Element test) {
        try {
            if (!isTautology(getCurrentContext(), test)) {
                addWarning(new HeuristicException(HeuristicErrorCodes.EVALUATED_NOT_TRUE_CODE,
                    HeuristicErrorCodes.EVALUATED_NOT_TRUE_TEXT + " (\""
                        + interpreter.getModel().getName() + "\")", getCurrentContext()));
            }
        } catch (HeuristicException h) {
            // TODO 20101015 m31: better exception handling would be better!
            if (getCurrentContext().getModuleLocation().equals(h.getContext().getModuleLocation())) {
                addWarning(h);
            } else {
                addWarning(new HeuristicException(h.getErrorCode(), h.getMessage(),
                        getCurrentContext()));
            }
        } catch (RuntimeException e) {
            Trace.fatal(CLASS, this, "test(Element)", "unexpected runtime exception", e);
            if (e.getCause() != null && e.getCause() instanceof HeuristicException) {
                // TODO 20101015 m31: better exception handling would be better!
                HeuristicException h = (HeuristicException) e.getCause();
                addWarning(new HeuristicException(h.getErrorCode(), h.getMessage(),
                    getCurrentContext()));
            } else {
                addWarning(new HeuristicException(HeuristicErrorCodes.RUNTIME_EXCEPTION_CODE,
                    HeuristicErrorCodes.RUNTIME_EXCEPTION_TEXT + e, getCurrentContext()));
            }
        }
    }

    /**
     * Test if given formula is a tautology. This is done by checking a model and
     * iterating through variable values.
     *
     * @param   moduleContext   Here we are within a module.
     * @param   formula         Formula.
     * @return  Is this formula a tautology according to our tests.
     * @throws  HeuristicException  Evaluation failed.
     */
    private boolean isTautology(final ModuleContext moduleContext, final Element formula)
            throws HeuristicException {
        boolean result = true;
        ModuleContext context = moduleContext;
        try {
            do {
                result &= interpreter.calculateValue(new ModuleContext(context), formula);
    //            System.out.println(interpreter.toString());
            } while (result && interpreter.next());
//        if (!result) {
//            System.out.println(interpreter);
//        }
//        System.out.println("interpretation finished - and result is = " + result);
        } finally {
            interpreter.clearVariables();
        }
        return result;
    }


    public void visitEnter(final Axiom axiom) throws ModuleDataException {
        if (axiom == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        System.out.println("\ttesting axiom");
        if (axiom.getFormula() != null) {
            setLocationWithinModule(context + ".getFormula().getElement()");
            final Element test = axiom.getFormula().getElement();
            test(test);
        }
        setLocationWithinModule(context);
        setBlocked(true);
    }

    public void visitLeave(final Axiom axiom) {
        setBlocked(false);
    }

    public void visitEnter(final PredicateDefinition definition)
            throws ModuleDataException {
        final String method = "visitEnter(PredicateDefinition)";
        if (definition == null) {
            return;
        }
        System.out.println("\ttesting predicate definition");
        final String context = getCurrentContext().getLocationWithinModule();
        try {
            PredicateConstant predicate = new PredicateConstant(definition.getName(),
                Integer.parseInt(definition.getArgumentNumber()));
            if (definition.getFormula() != null) {

                // add new predicate constant
                setLocationWithinModule(context + ".getFormula().getElement()");
                final VariableList variableList = definition.getVariableList();
                final int size = (variableList == null ? 0 : variableList.size());
// LATER 20101209 m31: uncomment again, when using DynamicInterpreter
//                interpreter.addPredicateConstant(predicate, variableList, definition.getFormula()
//                    .getElement().getList());

                // test new predicate constant: must always be successful otherwise there
                // must be a programming error or the predicate definition is not formal correct
                final Element[] elements = new Element[size + 1];
                elements[0] = new DefaultAtom(definition.getName());
                for (int i = 0; i < size; i++) {
                    elements[i + 1]  = variableList.get(i);
                }
                final Element left = new DefaultElementList(Operators.PREDICATE_CONSTANT,
                    elements);
                final Element[] compare = new Element[2];
                compare[0] = left;
                compare[1] = definition.getFormula().getElement();
                setLocationWithinModule(context);
                // TODO 20101211 m31: the context is wrong, because we constructed the element list
                // it is tolerable because we expect no error in a definition (should be always true)
                // that is tested against itself
                test(new DefaultElementList(Operators.EQUIVALENCE_OPERATOR, compare));
            } else if (!interpreter.hasPredicateConstant(predicate)) {
                // check if model contains predicate
                setLocationWithinModule(context + ".getName()");
                addWarning(new HeuristicException(
                    HeuristicErrorCodes.UNKNOWN_PREDICATE_CONSTANT_CODE,
                    HeuristicErrorCodes.UNKNOWN_PREDICATE_CONSTANT_TEXT + predicate,
                    getCurrentContext()));
            }
        } catch (NumberFormatException e) {
            Trace.fatal(CLASS, this, method, "not suported argument number: "
                + definition.getArgumentNumber(), e);
            setLocationWithinModule(context + ".getArgumentNumber()");
            addWarning(new HeuristicException(HeuristicErrorCodes.UNKNOWN_ARGUMENT_FORMAT_CODE,
                HeuristicErrorCodes.UNKNOWN_ARGUMENT_FORMAT_TEXT + definition.getArgumentNumber(),
                getCurrentContext()));
        }
        setLocationWithinModule(context);
        setBlocked(true);
    }

    public void visitLeave(final PredicateDefinition definition) {
        setBlocked(false);
    }


    public void visitEnter(final FunctionDefinition definition)
            throws ModuleDataException {
        final String method = "visitEnter(FunctionDefinition)";
        if (definition == null) {
            return;
        }
        System.out.println("\ttesting function definition");
        final String context = getCurrentContext().getLocationWithinModule();
        try {
            FunctionConstant function = new FunctionConstant(definition.getName(),
                Integer.parseInt(definition.getArgumentNumber()));
            if (definition.getTerm() != null) {

                // add new predicate constant
                setLocationWithinModule(context + ".getTerm().getElement()");
                final VariableList variableList = definition.getVariableList();
                final int size = (variableList == null ? 0 : variableList.size());
                // LATER 20101209 m31: uncomment again, when using DynamicInterpreter
//                interpreter.addFunctionConstant(function, variableList, definition.getTerm()
//                        .getElement().getList());

                // test new predicate constant: must always be successful otherwise there
                // must be a programming error or the predicate definition is not formal correct
                final Element[] elements = new Element[size + 1];
                elements[0] = new DefaultAtom(definition.getName());
                for (int i = 0; i < size; i++) {
                    elements[i + 1]  = variableList.get(i);
                }
                final Element left = new DefaultElementList(Operators.FUNCTION_CONSTANT,
                   elements);
                final Element[] equal = new Element[3];
                equal[0] = new DefaultAtom("equal");
                if (getQedeqBo().getExistenceChecker() != null && getQedeqBo().getExistenceChecker()
                        .identityOperatorExists()) {
                    equal[0] = new DefaultAtom(getQedeqBo().getExistenceChecker().getIdentityOperator());
                }
                equal[1] = left;
                equal[2] = definition.getTerm().getElement();
                setLocationWithinModule(context);
                test(new DefaultElementList(Operators.PREDICATE_CONSTANT, equal));
            } else if (!interpreter.hasFunctionConstant(function)) {
                // check if model contains predicate
                setLocationWithinModule(context + ".getName()");
                addWarning(new HeuristicException(
                    HeuristicErrorCodes.UNKNOWN_FUNCTION_CONSTANT_CODE,
                    HeuristicErrorCodes.UNKNOWN_FUNCTION_CONSTANT_TEXT + function,
                    getCurrentContext()));
            }
        } catch (NumberFormatException e) {
            Trace.fatal(CLASS, this, method, "not suported argument number: "
                + definition.getArgumentNumber(), e);
            setLocationWithinModule(context + ".getArgumentNumber()");
            addWarning(new HeuristicException(HeuristicErrorCodes.UNKNOWN_ARGUMENT_FORMAT_CODE,
                HeuristicErrorCodes.UNKNOWN_ARGUMENT_FORMAT_TEXT + definition.getArgumentNumber(),
                getCurrentContext()));
        }
        setLocationWithinModule(context);
        setBlocked(true);
    }

    public void visitLeave(final FunctionDefinition definition) {
        setBlocked(false);
    }


    public void visitEnter(final Node node) {
        System.out.println(getLatexListEntry(node.getTitle()) + " " + node.getId());
    }

    public void visitEnter(final Proposition proposition)
            throws ModuleDataException {
        if (proposition == null) {
            return;
        }
        System.out.println("\ttesting proposition");
        final String context = getCurrentContext().getLocationWithinModule();
        if (proposition.getFormula() != null) {
            setLocationWithinModule(context + ".getFormula().getElement()");
            final Element test = proposition.getFormula().getElement();
            test(test);
        }
        setLocationWithinModule(context);
    }

    public void visitLeave(final Proposition definition) {
    }

    public void visitEnter(final FormalProofLine line)
            throws ModuleDataException {
        if (line == null) {
            return;
        }
        System.out.println("\t\ttesting line " + line.getLabel());
        final String context = getCurrentContext().getLocationWithinModule();
        if (line.getFormula() != null) {
            setLocationWithinModule(context + ".getFormula().getElement()");
            final Element test = line.getFormula().getElement();
            test(test);
        }
        setLocationWithinModule(context);
        setBlocked(true);
    }

    public void visitLeave(final FormalProofLine line) {
        setBlocked(false);
    }

    public void visitEnter(final Rule rule) throws ModuleDataException {
        setBlocked(true);
    }

    public void visitLeave(final Rule rule) {
        setBlocked(false);
    }

    public String getExecutionActionDescription() {
        return super.getExecutionActionDescription() + "\n" + interpreter.toString();
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
     * Filters correct entry out of LaTeX list. Filter criterion is for example the correct
     * language.
     *
     * @param   list    List of LaTeX entries.
     * @return  Filtered text.
     */
    private String getLatexListEntry(final LatexList list) {
        if (list == null) {
            return "";
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != null && "en".equals(list.get(i).getLanguage())) {
                return getLatex(list.get(i));
            }
        }
        // assume entry with missing language as default
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != null && list.get(i).getLanguage() == null) {
                return getLatex(list.get(i));
            }
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != null) {
                return getLatex(list.get(i));
            }
        }
        return "";
    }

    private String getLatex(final Latex latex) {
        String result = latex.getLatex();
        if (result == null) {
            result = "";
        }
        result = result.trim();
        result = result.replaceAll("\\\\index\\{.*\\}", "");
        return result.trim();
    }

}
