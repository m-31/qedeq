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

package org.qedeq.kernel.bo.logic.model;

import java.util.ArrayList;
import java.util.List;

import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.base.list.Element;
import org.qedeq.kernel.base.list.ElementList;
import org.qedeq.kernel.base.module.FunctionDefinition;
import org.qedeq.kernel.base.module.PredicateDefinition;
import org.qedeq.kernel.bo.logic.wf.Operators;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.service.DefaultKernelQedeqBo;
import org.qedeq.kernel.common.DefaultModuleAddress;
import org.qedeq.kernel.common.ModuleContext;


/**
 * This class calculates a new truth value for a given formula for a given interpretation.
 *
 * @author  Michael Meyling
 */
public final class DynamicDirectInterpreter {

    /** This class. */
    private static final Class CLASS = DynamicDirectInterpreter.class;

    /** Module context. Here were are currently. */
    private ModuleContext moduleContext;

    /** For formatting debug trace output. */
    private final StringBuffer deepness = new StringBuffer();

    /** Model contains entities, functions, predicates. */
    private final DynamicModel model;

    /** Interpret subject variables. */
    private final SubjectVariableInterpreter subjectVariableInterpreter;

    /** Interpret predicate variables. */
    private final PredicateVariableInterpreter predicateVariableInterpreter;

    /** Interpret function variables. */
    private final FunctionVariableInterpreter functionVariableInterpreter;

   /*
    * Constructor.
    *
    * @param   model       We work with this model.
    */
   public DynamicDirectInterpreter(final DynamicModel model, final KernelQedeqBo qedeq) {
       this(model, qedeq, new SubjectVariableInterpreter(model), new PredicateVariableInterpreter(model),
           new FunctionVariableInterpreter(model));
   }

    /**
     * Constructor.
     *
     * @param   model                           We work with this model.
     * @param   qedeq                           We work with this module.
     * @param   subjectVariableInterpreter      Subject variable interpreter.
     * @param   predicateVariableInterpreter    Predicate variable interpreter.
     * @param   functionVariableInterpreter     Function variable interpreter.
     */
    public DynamicDirectInterpreter(final DynamicModel model, final KernelQedeqBo qedeq,
            final SubjectVariableInterpreter subjectVariableInterpreter,
            final PredicateVariableInterpreter predicateVariableInterpreter,
            final FunctionVariableInterpreter functionVariableInterpreter) {
        this.model = model;
        this.subjectVariableInterpreter = subjectVariableInterpreter;
        this.predicateVariableInterpreter = predicateVariableInterpreter;
        this.functionVariableInterpreter = functionVariableInterpreter;
    }

    /**
     * Calculate function value.
     *
     * @param   qedeq           Module we are currently in.
     * @param   constant        This is the function definition.
     * @param   entities        Function arguments.
     * @return  Result of calculation;
     */
    public Entity calculateFunctionValue(final KernelQedeqBo qedeq, final FunctionDefinition constant,
        final Entity[] entities) {
        for (int i = 0; i < entities.length; i++) {
            final SubjectVariable var = new SubjectVariable(constant.getVariableList().get(i).getList()
                .getElement(0).getAtom().getString());
            subjectVariableInterpreter.forceAddSubjectVariable(var, entities[i].getValue());
        }
        Entity result;
        try {
            result = calculateTerm(qedeq, new ModuleContext(new DefaultModuleAddress()),
                constant.getTerm().getElement());
        } catch (HeuristicException e) {
            throw new RuntimeException(e);  // TODO 20101014 m31: improve error handling
        }
        for (int i = entities.length - 1; i >= 0; i--) {
            final SubjectVariable var = new SubjectVariable(constant.getVariableList().get(i).getList()
                .getElement(0).getAtom().getString());
            subjectVariableInterpreter.forceRemoveSubjectVariable(var);
        }
        return result;
    }

    /**
     * Calculate function value.
     *
     * @param   qedeq           Module we are currently in.
     * @param   constant        This is the function definition.
     * @param   entities        Function arguments.
     * @return  Result of calculation;
     */
    public boolean calculatePredicateValue(final KernelQedeqBo qedeq, final PredicateDefinition constant,
        final Entity[] entities) {
        for (int i = 0; i < entities.length; i++) {
            final SubjectVariable var = new SubjectVariable(constant.getVariableList().get(i).getList()
                .getElement(0).getAtom().getString());
            subjectVariableInterpreter.forceAddSubjectVariable(var, entities[i].getValue());
        }
        boolean result;
        try {
            result = calculateValue(qedeq, new ModuleContext(new DefaultModuleAddress()),
                constant.getFormula().getElement());
        } catch (HeuristicException e) {
            throw new RuntimeException(e);  // TODO 20101014 m31: improve error handling
        }
        for (int i = entities.length - 1; i >= 0; i--) {
            final SubjectVariable var = new SubjectVariable(constant.getVariableList().get(i).getList()
                .getElement(0).getAtom().getString());
            subjectVariableInterpreter.forceRemoveSubjectVariable(var);
        }
        return result;
    }

    /**
     * Calculate the truth value of a given formula is a tautology. This is done by checking with
     * a model and certain variable values.
     *
     * @param   qedeq           Module we are currently in.
     * @param   moduleContext   Where we are within an module.
     * @param   formula         Formula.
     * @return  Truth value of formula.
     * @throws  HeuristicException      We couldn't calculate the value.
     */
    public boolean calculateValue(final KernelQedeqBo qedeq, final ModuleContext moduleContext, final Element formula)
            throws  HeuristicException {
//        this.startElement = formula;
        this.moduleContext = moduleContext;
//        this.startContext = new ModuleContext(moduleContext);
        return calculateValue(qedeq, formula);
    }

    /**
     * Calculate the truth value of a given formula is a tautology. This is done by checking with
     * a model and certain variable values.
     *
     * @param   qedeq           Module we are currently in.
     * @param   formula         Formula.
     * @return  Truth value of formula.
     * @throws  HeuristicException      We couldn't calculate the value.
     */
    private boolean calculateValue(final KernelQedeqBo qedeq, final Element formula) throws  HeuristicException {
        final String method = "calculateValue(Element)";
//        System.out.println(deepness.toString() + Latex2Utf8Parser.transform(null,
//        qedeq.getElement2Latex().getLatex(formula), 0));
//        deepness.append("-");
        if (Trace.isDebugEnabled(CLASS)) {
            Trace.param(CLASS, this, method, deepness.toString() + "formula", formula);
            deepness.append("-");
        }
        if (formula.isAtom()) {
            throw new HeuristicException(HeuristicErrorCodes.WRONG_CALLING_CONVENTION_CODE,
                HeuristicErrorCodes.WRONG_CALLING_CONVENTION_MSG, moduleContext);
        }
        final String context = getLocationWithinModule();
        setLocationWithinModule(context + ".getList()");
        final ElementList list = formula.getList();
        final String op = list.getOperator();
        boolean result;
        if (Operators.CONJUNCTION_OPERATOR.equals(op)) {
            result = true;
            for (int i = 0; i < list.size() && result; i++) {
                setLocationWithinModule(context + ".getList().getElement(" + i + ")");
                result &= calculateValue(qedeq, list.getElement(i));
            }
        } else if (Operators.DISJUNCTION_OPERATOR.equals(op)) {
            result = false;
            for (int i = 0; i < list.size() && !result; i++) {
                setLocationWithinModule(context + ".getList().getElement(" + i + ")");
                result |= calculateValue(qedeq, list.getElement(i));
            }
        } else if (Operators.EQUIVALENCE_OPERATOR.equals(op)) {
            result = true;
            boolean value = false;
            for (int i = 0; i < list.size() && result; i++) {
                if (i > 0) {
                    setLocationWithinModule(context + ".getList().getElement(" + i + ")");
                    if (value != calculateValue(qedeq, list.getElement(i))) {
                        result = false;
                    }
                } else {
                    setLocationWithinModule(context + ".getList().getElement(" + i + ")");
                    value = calculateValue(qedeq, list.getElement(i));
                }
            }
        } else if (Operators.IMPLICATION_OPERATOR.equals(op)) {
            result = false;
            for (int i = 0; i < list.size() && !result; i++) {
                setLocationWithinModule(context + ".getList().getElement(" + i + ")");
                if (i < list.size() - 1) {
                    result |= !calculateValue(qedeq, list.getElement(i));
                } else {
                    result |= calculateValue(qedeq, list.getElement(i));
                }
            }
        } else if (Operators.NEGATION_OPERATOR.equals(op)) {
            result = true;
            for (int i = 0; i < list.size() && result; i++) {
                setLocationWithinModule(context + ".getList().getElement(" + i + ")");
                result &= !calculateValue(qedeq, list.getElement(i));
            }
        } else if (Operators.PREDICATE_VARIABLE.equals(op)) {
            final PredicateVariable var = new PredicateVariable(list.getElement(0).getAtom().getString(),
                    list.size() - 1);
            setLocationWithinModule(context + ".getList()");
            result = predicateVariableInterpreter.getPredicate(var)
                .calculate(getEntities(qedeq, list));
        } else if (Operators.UNIVERSAL_QUANTIFIER_OPERATOR.equals(op)) {
            result = handleUniversalQuantifier(qedeq, list);
        } else if (Operators.EXISTENTIAL_QUANTIFIER_OPERATOR.equals(op)) {
            result = handleExistentialQuantifier(qedeq, list);
        } else if (Operators.UNIQUE_EXISTENTIAL_QUANTIFIER_OPERATOR.equals(op)) {
            result = handleUniqueExistentialQuantifier(qedeq, list);
        } else if (Operators.PREDICATE_CONSTANT.equals(op)) {
            final String label = list.getElement(0).getAtom().getString();
            String name = label;
            KernelQedeqBo newProp = qedeq;
            if (label.indexOf(".") >= 0) {
                name = label.substring(label.indexOf(".") + 1);
                final String external = label.substring(0, label.indexOf("."));
                newProp = null;
                if (qedeq.getKernelRequiredModules() != null) {
                    newProp = (DefaultKernelQedeqBo)
                        qedeq.getKernelRequiredModules().getQedeqBo(external);
                }
                if (newProp == null) {
                    setLocationWithinModule(context + ".getList().getOperator()");
                    throw new HeuristicException(HeuristicErrorCodes.UNKNOWN_PREDICATE_CONSTANT_CODE,
                        HeuristicErrorCodes.UNKNOWN_PREDICATE_CONSTANT_MSG + label, moduleContext);
                }
            }
            PredicateDefinition definition = newProp.getElement2Latex().getPredicate(name, list.size() - 1);
            if (definition != null) {
                // must that have a model implementation?
                if (definition.getFormula() == null) {
                    final PredicateConstant var = new PredicateConstant(name,
                        list.size() - 1);
                    Predicate predicate = model.getPredicateConstant(var);
                    if (predicate == null) {
                        setLocationWithinModule(context + ".getList().getOperator()");
                        throw new HeuristicException(HeuristicErrorCodes.UNKNOWN_PREDICATE_CONSTANT_CODE,
                            HeuristicErrorCodes.UNKNOWN_PREDICATE_CONSTANT_MSG + var, moduleContext);
                    }
                    setLocationWithinModule(context + ".getList()");
                    result = predicate.calculate(getEntities(qedeq, list));
                } else {
                    setLocationWithinModule(context + ".getList()");
                    result = calculatePredicateValue(newProp, definition, getEntities(qedeq, list));
                }
            } else {
                setLocationWithinModule(context + ".getList().getOperator()");
                throw new HeuristicException(HeuristicErrorCodes.UNKNOWN_PREDICATE_CONSTANT_CODE,
                    HeuristicErrorCodes.UNKNOWN_PREDICATE_CONSTANT_MSG + label, moduleContext);
            }

//            final String text = stripReference(list.getElement(0).getAtom().getString());
//            final PredicateConstant var = new PredicateConstant(text,
//                list.size() - 1);
//            Predicate predicate = model.getPredicateConstant(var);
//            if (predicate == null) {
//                setLocationWithinModule(context + ".getList().getOperator()");
//                throw new HeuristicException(HeuristicErrorCodes.UNKNOWN_PREDICATE_CONSTANT_CODE,
//                    HeuristicErrorCodes.UNKNOWN_PREDICATE_CONSTANT_MSG + var, moduleContext);
//            }
//            setLocationWithinModule(context + ".getList()");
//            result = predicate.calculate(getEntities(qedeq, list));
        } else {
            setLocationWithinModule(context + ".getList().getOperator()");
            throw new HeuristicException(HeuristicErrorCodes.UNKNOWN_OPERATOR_CODE,
                HeuristicErrorCodes.UNKNOWN_OPERATOR_MSG + op, moduleContext);
        }
        setLocationWithinModule(context);
        if (Trace.isDebugEnabled(CLASS)) {
            deepness.setLength(deepness.length() > 0 ? deepness.length() - 1 : 0);
            Trace.param(CLASS, this, method, deepness.toString() + "result ", result);
        }
//        deepness.setLength(deepness.length() > 0 ? deepness.length() - 1 : 0);
//        System.out.print(deepness.toString() + Latex2Utf8Parser.transform(null,
//         qedeq.getElement2Latex().getLatex(formula), 0));
//        System.out.println("=" + result);
        return result;
    }

    /**
     * Handle universal quantifier operator.
     *
     * @param   qedeq           Module we are currently in.
     * @param   list    Work on this formula.
     * @return  result  Calculated truth value.
     * @throws  HeuristicException  Calculation not possible.
     */
    private boolean handleUniversalQuantifier(final KernelQedeqBo qedeq, final ElementList list)
            throws HeuristicException {
        final String context = getLocationWithinModule();
        boolean result = true;
        final ElementList variable = list.getElement(0).getList();
        final SubjectVariable var = new SubjectVariable(variable.getElement(0).getAtom().getString());
        subjectVariableInterpreter.addSubjectVariable(var);
        for (int i = 0; i < model.getEntitiesSize(); i++) {
//            System.out.print(deepness.toString() + Latex2Utf8Parser.transform(null,
//            qedeq.getElement2Latex().getLatex(variable), 0));
//            System.out.println("=" + model.getEntity(i));

            if (list.size() == 2) {
                setLocationWithinModule(context + ".getList().getElement(1)");
                result &= calculateValue(qedeq, list.getElement(1));
            } else {  // must be 3
                setLocationWithinModule(context + ".getList().getElement(1)");
                final boolean result1 = calculateValue(qedeq, list.getElement(1));
                setLocationWithinModule(context + ".getList().getElement(2)");
                final boolean result2 = calculateValue(qedeq, list.getElement(2));
                result &= !result1 || result2;
            }
            if (!result) {
                break;
            }
            subjectVariableInterpreter.increaseSubjectVariableSelection(var);
        }
        subjectVariableInterpreter.removeSubjectVariable(var);
        return result;
    }

    /**
     * Handle existential quantifier operator.
     *
     * @param   qedeq           Module we are currently in.
     * @param   list    Work on this formula.
     * @return  result  Calculated truth value.
     * @throws  HeuristicException  Calculation not possible.
     */
    private boolean handleExistentialQuantifier(final KernelQedeqBo qedeq, final ElementList list)
            throws HeuristicException {
        final String context = getLocationWithinModule();
        boolean result = false;
        final ElementList variable = list.getElement(0).getList();
        final SubjectVariable var = new SubjectVariable(variable.getElement(0).getAtom().getString());
        subjectVariableInterpreter.addSubjectVariable(var);
        for (int i = 0; i < model.getEntitiesSize(); i++) {
//            System.out.print(deepness.toString() + Latex2Utf8Parser.transform(null,
//            qedeq.getElement2Latex().getLatex(variable), 0));
//            System.out.println("=" + model.getEntity(i));
            if (list.size() == 2) {
                setLocationWithinModule(context + ".getList().getElement(1)");
                result |= calculateValue(qedeq, list.getElement(1));
            } else {  // must be 3
                setLocationWithinModule(context + ".getList().getElement(1)");
                final boolean result1 = calculateValue(qedeq, list.getElement(1));
                setLocationWithinModule(context + ".getList().getElement(2)");
                final boolean result2 = calculateValue(qedeq, list.getElement(2));
                result |= result1 && result2;
            }
            if (result) {
                break;
            }
            subjectVariableInterpreter.increaseSubjectVariableSelection(var);
        }
        subjectVariableInterpreter.removeSubjectVariable(var);
        return result;
    }

    /**
     * Handle unique existential quantifier operator.
     *
     * @param   qedeq           Module we are currently in.
     * @param   list            Work on this formula.
     * @return  result          Calculated truth value.
     * @throws  HeuristicException  Calculation not possible.
     */
    private boolean handleUniqueExistentialQuantifier(final KernelQedeqBo qedeq, final ElementList list)
            throws HeuristicException {
        final String context = getLocationWithinModule();
        boolean result = false;
        final ElementList variable = list.getElement(0).getList();
        final SubjectVariable var = new SubjectVariable(variable.getElement(0).getAtom().getString());
        subjectVariableInterpreter.addSubjectVariable(var);
        for (int i = 0; i < model.getEntitiesSize(); i++) {
            boolean val;
            if (list.size() == 2) {
                setLocationWithinModule(context + ".getList().getElement(1)");
                val = calculateValue(qedeq, list.getElement(1));
            } else {  // must be 3
                setLocationWithinModule(context + ".getList().getElement(1)");
                final boolean result1 = calculateValue(qedeq, list.getElement(1));
                setLocationWithinModule(context + ".getList().getElement(2)");
                final boolean result2 = calculateValue(qedeq, list.getElement(2));
                val = result1 && result2;
            }
            if (val) {
                if (result) {
                    result = false;
                    break;
                } else {
                    result = true;
                }
            }
            subjectVariableInterpreter.increaseSubjectVariableSelection(var);
        }
        subjectVariableInterpreter.removeSubjectVariable(var);
        return result;
    }

    /**
     * Interpret terms.
     *
     * @param   qedeq           Module we are currently in.
     * @param   terms           Interpret these terms. The first entry is stripped.
     * @return  Values.
     * @throws  HeuristicException evaluation failed.
     */
    private Entity[] getEntities(final KernelQedeqBo qedeq, final ElementList terms)
            throws HeuristicException {
        final String context = getLocationWithinModule();
        final Entity[] result =  new Entity[terms.size() - 1];    // strip first argument
        for (int i = 0; i < result.length; i++) {
            setLocationWithinModule(context + ".getElement(" + (i + 1) + ")");
            result[i] = calculateTerm(qedeq, terms.getElement(i + 1));
        }
        setLocationWithinModule(context);
        return result;
    }

    /**
     * Calculate the term value of a given term. This is done by checking with
     * a model and certain variable values.
     *
     * @param   qedeq           Module we are currently in.
     * @param   moduleContext   Where we are within an module.
     * @param   term            Term.
     * @return  Entity of model.
     * @throws  HeuristicException      We couldn't calculate the value.
     */
    public Entity calculateTerm(final KernelQedeqBo qedeq, final ModuleContext moduleContext, final Element term)
            throws  HeuristicException {
//        this.startElement = formula;
        this.moduleContext = moduleContext;
//        this.startContext = new ModuleContext(moduleContext);
        return calculateTerm(qedeq, term);
    }

    /**
     * Interpret term.
     *
     * @param   qedeq           Module we are currently in.
     * @param   term    Interpret this term.
     * @return  Value.
     * @throws  HeuristicException evaluation failed.
     */
    private Entity calculateTerm(final KernelQedeqBo qedeq, final Element term)
            throws  HeuristicException {
        final String method = "calculateTerm(Element) ";
        if (Trace.isDebugEnabled(CLASS)) {
            Trace.param(CLASS, this, method, deepness.toString() + "term   ", term);
            deepness.append("-");
        }
        if (!term.isList()) {
            throw new RuntimeException("a term should be a list: " + term);
        }
        final String context = getLocationWithinModule();
        Entity result = null;
        try {
            final ElementList termList = term.getList();
            final String op = termList.getOperator();
            if (Operators.SUBJECT_VARIABLE.equals(op)) {
                final String text = termList.getElement(0).getAtom().getString();
                final SubjectVariable var = new SubjectVariable(text);
                result = subjectVariableInterpreter.getEntity(var);
            } else if (Operators.FUNCTION_VARIABLE.equals(op)) {
                final FunctionVariable var = new FunctionVariable(termList.getElement(0).getAtom().getString(),
                    termList.size() - 1);
                Function function = functionVariableInterpreter.getFunction(var);
                setLocationWithinModule(context + ".getList()");
                result = function.map(getEntities(qedeq, termList));
            } else if (Operators.FUNCTION_CONSTANT.equals(op)) {
                final String label = termList.getElement(0).getAtom().getString();
                String name = label;
                KernelQedeqBo newProp = qedeq;
                if (label.indexOf(".") >= 0) {
                    name = label.substring(label.indexOf(".") + 1);
                    final String external = label.substring(0, label.indexOf("."));
                    newProp = null;
                    if (qedeq.getKernelRequiredModules() != null) {
                        newProp = (DefaultKernelQedeqBo)
                            qedeq.getKernelRequiredModules().getQedeqBo(external);
                    }
                    if (newProp == null) {
                        setLocationWithinModule(context + ".getList().getOperator()");
                        throw new HeuristicException(HeuristicErrorCodes.UNKNOWN_FUNCTION_CONSTANT_CODE,
                            HeuristicErrorCodes.UNKNOWN_FUNCTION_CONSTANT_MSG + label, moduleContext);
                    }
                }
                FunctionDefinition definition = newProp.getElement2Latex().getFunction(name, termList.size() - 1);
                if (definition != null) {
                    // must that have a model implementation?
                    if (definition.getTerm() == null) {
                        final FunctionConstant var = new FunctionConstant(name,
                            termList.size() - 1);
                        Function function = model.getFunctionConstant(var);
                        if (function == null) {
                            setLocationWithinModule(context + ".getList().getOperator()");
                            throw new HeuristicException(HeuristicErrorCodes.UNKNOWN_FUNCTION_CONSTANT_CODE,
                                HeuristicErrorCodes.UNKNOWN_FUNCTION_CONSTANT_MSG + var, moduleContext);
                        }
                        setLocationWithinModule(context + ".getList()");
                        result = function.map(getEntities(newProp, termList));
                    } else {
                        setLocationWithinModule(context + ".getList()");
                        result = calculateFunctionValue(newProp, definition, getEntities(qedeq, termList));
                    }
                } else {
                    setLocationWithinModule(context + ".getList().getOperator()");
                    throw new HeuristicException(HeuristicErrorCodes.UNKNOWN_FUNCTION_CONSTANT_CODE,
                        HeuristicErrorCodes.UNKNOWN_FUNCTION_CONSTANT_MSG + label, moduleContext);
                }
            } else if (Operators.CLASS_OP.equals(op)) {
                List fullfillers = new ArrayList();
                ElementList variable = termList.getElement(0).getList();
                final SubjectVariable var = new SubjectVariable(variable.getElement(0).getAtom().getString());
                subjectVariableInterpreter.addSubjectVariable(var);
                final PredicateDefinition isSet = qedeq.getElement2Latex().getPredicate("isSet", 1);
                if (isSet == null) {
                    throw new HeuristicException(HeuristicErrorCodes.UNKNOWN_TERM_OPERATOR_CODE,
                            HeuristicErrorCodes.UUNKNOWN_TERM_OPERATOR_MSG + "isSet(*)", moduleContext);
                }
                for (int i = 0; i < model.getEntitiesSize(); i++) {
                    setLocationWithinModule(context + ".getList().getElement(1)");
                    if (calculateValue(qedeq, termList.getElement(1))
                            && calculatePredicateValue(qedeq, isSet, new Entity[] {model.getEntity(i)})) {
                        fullfillers.add(model.getEntity(i));
                    }
                    subjectVariableInterpreter.increaseSubjectVariableSelection(var);
                }
                result = model.comprehension((Entity[]) fullfillers.toArray(new Entity[] {}));
                subjectVariableInterpreter.removeSubjectVariable(var);
            } else {
                setLocationWithinModule(context + ".getList().getOperator()");
                throw new HeuristicException(HeuristicErrorCodes.UNKNOWN_TERM_OPERATOR_CODE,
                    HeuristicErrorCodes.UUNKNOWN_TERM_OPERATOR_MSG + op, moduleContext);
            }
        } finally {
            setLocationWithinModule(context);
        }
        if (Trace.isDebugEnabled(CLASS)) {
            deepness.setLength(deepness.length() > 0 ? deepness.length() - 1 : 0);
            Trace.param(CLASS, this, method, deepness.toString() + "result ", result);
        }
        return result;
    }

    private String getLocationWithinModule() {
        return moduleContext.getLocationWithinModule();
    }

    private void setLocationWithinModule(final String localContext) {
        moduleContext.setLocationWithinModule(localContext);
// FIXME 20101215 m31: for local testing, but into JUnit test
//        if (localContext.equals(startContext.getLocationWithinModule())) {
//            return;
//        }
//        String position
//            = moduleContext.getLocationWithinModule().substring(startContext.getLocationWithinModule().length());
//        if (position.startsWith(".")) {
//            position = position.substring(1);
//        }
//        try {
//            DynamicGetter.get(startElement, position);
//        } catch (IllegalAccessException e) {
//            System.out.println(position);
//            e.printStackTrace(System.out);
//        } catch (InvocationTargetException e) {
//            System.out.println(position);
//            e.printStackTrace(System.out);
//        } catch (RuntimeException e) {
//            System.out.println(position);
//            e.printStackTrace(System.out);
//            throw e;
//        }
    }

    /**
     * Change to next valuation.
     *
     * @return  Is there a next new valuation?
     */
    public boolean next() {
        return subjectVariableInterpreter.next() || predicateVariableInterpreter.next()
            || functionVariableInterpreter.next();
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("Current interpretation:\n");
        buffer.append("\t" + predicateVariableInterpreter + "\n");
        buffer.append("\t" + subjectVariableInterpreter + "\n");
        buffer.append("\t" + functionVariableInterpreter);
        return buffer.toString();
    }

    /**
     * Strips the reference to external modules.
     *
     * @param   operator    Might have reference to an external module.
     * @return  Operator as local reference.
     */
    public String stripReference(final String operator) {
        final int index = operator.lastIndexOf(".");
        if (index >= 0 && index + 1 < operator.length()) {
            return operator.substring(index + 1);
        }
        return operator;
    }


    /**
     * Is the given predicate constant already defined?
     *
     * @param   constant    Predicate constant to check for.
     * @return  Is the given predicate constant already defined?
     */
    public boolean hasPredicateConstant(final PredicateConstant constant) {
        return null != model.getPredicateConstant(constant);
    }

    /**
     * Is the given function constant already defined?
     *
     * @param   constant    Function constant to check for.
     * @return  Is the given function constant already defined?
     */
    public boolean hasFunctionConstant(final FunctionConstant constant) {
        return null != model.getFunctionConstant(constant);
    }

    /**
     * Clear all variable settings.
     */
    public void clearVariables() {
        subjectVariableInterpreter.clear();
        predicateVariableInterpreter.clear();
        functionVariableInterpreter.clear();
    }

    /**
     * Get model.
     *
     * @return  Model we work with.
     */
    public DynamicModel getModel() {
        return model;
    }


}
