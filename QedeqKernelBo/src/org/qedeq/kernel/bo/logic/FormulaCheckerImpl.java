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

package org.qedeq.kernel.bo.logic;

import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.logic.wf.ElementCheckException;
import org.qedeq.kernel.bo.logic.wf.EverythingExists;
import org.qedeq.kernel.bo.logic.wf.ExistenceChecker;
import org.qedeq.kernel.bo.logic.wf.FormulaBasicErrors;
import org.qedeq.kernel.bo.logic.wf.FormulaCheckException;
import org.qedeq.kernel.bo.logic.wf.LogicalCheckExceptionList;
import org.qedeq.kernel.bo.logic.wf.Operators;
import org.qedeq.kernel.bo.logic.wf.TermCheckException;
import org.qedeq.kernel.se.base.list.Atom;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.list.ElementList;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.dto.list.ElementSet;


/**
 * This class deals with {@link org.qedeq.kernel.se.base.list.Element}s which represent a
 * formula. It has methods to check those elements for being well-formed.
 *
 * LATER mime 20070307: here are sometimes error messages that get concatenated with
 * an {@link org.qedeq.kernel.se.base.list.ElementList#getOperator()} string. Perhaps these
 * strings must be translated into the original input format and a mapping must be done.
 *
 * @author  Michael Meyling
 */
public class FormulaCheckerImpl implements Operators, FormulaBasicErrors, FormulaChecker {

    /** This class. */
    private static final Class CLASS = FormulaCheckerImpl.class;

    /** Current context during creation. */
    private ModuleContext currentContext;

    /** Existence checker for operators. */
    private ExistenceChecker existenceChecker;

    /** All exceptions that occurred during checking. */
    private LogicalCheckExceptionList exceptions;


    /**
     * Constructor.
     *
     */
    public FormulaCheckerImpl() {
    }

    public final LogicalCheckExceptionList checkFormula(final Element element,
            final ModuleContext context, final ExistenceChecker existenceChecker) {
        if (existenceChecker.identityOperatorExists()
                && !existenceChecker.predicateExists(existenceChecker.getIdentityOperator(), 2)) {
            throw new IllegalArgumentException("identy predicate should exist, but it doesn't");
        }
        this.existenceChecker = existenceChecker;
        currentContext = new ModuleContext(context);
        exceptions = new LogicalCheckExceptionList();
        checkFormula(element);
        return exceptions;
    }

    public final LogicalCheckExceptionList checkFormula(final Element element,
            final ModuleContext context) {
        return checkFormula(element, context, EverythingExists.getInstance());
    }

    /**
     * Check if {@link Element} is a term. If there are any errors the returned list
     * (which is always not <code>null</code>) has a size greater zero.
     *
     * @param   element             Check this element.
     * @param   context             For location information. Important for locating errors.
     * @param   existenceChecker    Existence checker for operators.
     * @return  Collected errors if there are any. Not <code>null</code>.
     */
    public final LogicalCheckExceptionList checkTerm(final Element element,
            final ModuleContext context, final ExistenceChecker existenceChecker) {
        if (existenceChecker.identityOperatorExists()
                && !existenceChecker.predicateExists(existenceChecker.getIdentityOperator(), 2)) {
            throw new IllegalArgumentException("identy predicate should exist, but it doesn't");
        }
        this.existenceChecker = existenceChecker;
        currentContext = new ModuleContext(context);
        exceptions = new LogicalCheckExceptionList();
        checkTerm(element);
        return exceptions;
    }

    /**
     * Check if {@link Element} is a term. If there are any errors the returned list
     * (which is always not <code>null</code>) has a size greater zero.
     * If the existence context is known you should use
     * {@link #checkTerm(Element, ModuleContext, ExistenceChecker)}.
     *
     * @param   element Check this element.
     * @param   context For location information. Important for locating errors.
     * @return  Collected errors if there are any. Not <code>null</code>.
     */
    public final LogicalCheckExceptionList checkTerm(final Element element,
            final ModuleContext context) {
        return checkTerm(element, context, EverythingExists.getInstance());
    }

    /**
     * Is {@link Element} a formula?
     *
     * @param   element    Check this element.
     */
    private final void checkFormula(final Element element) {
        final String method = "checkFormula";
        Trace.begin(CLASS, this, method);
        Trace.param(CLASS, this, method, "element", element);
        final String context = getCurrentContext().getLocationWithinModule();
        Trace.param(CLASS, this, method, "context", context);
        if (!checkList(element)) {
            return;
        }
        final ElementList list = element.getList();
        final String listContext = context + ".getList()";
        setLocationWithinModule(listContext);
        final String operator = list.getOperator();
        if (operator.equals(CONJUNCTION_OPERATOR)
                || operator.equals(DISJUNCTION_OPERATOR)
                || operator.equals(IMPLICATION_OPERATOR)
                || operator.equals(EQUIVALENCE_OPERATOR)) {
            Trace.trace(CLASS, this, method,
                "one of (and, or, implication, equivalence) operator found");
            if (list.size() <= 1) {
                handleFormulaCheckException(MORE_THAN_ONE_ARGUMENT_EXPECTED,
                    MORE_THAN_ONE_ARGUMENT_EXPECTED_TEXT + "\""
                    + operator + "\"", element, getCurrentContext());
                return;
            }
            if (operator.equals(IMPLICATION_OPERATOR) && list.size() != 2) {
                handleFormulaCheckException(EXACTLY_TWO_ARGUMENTS_EXPECTED,
                    EXACTLY_TWO_ARGUMENTS_EXPECTED_TEXT + "\""
                    + operator + "\"", element, getCurrentContext());
                return;
            }
            for (int i = 0; i < list.size(); i++) {
                setLocationWithinModule(listContext + ".getElement(" + i + ")");
                checkFormula(list.getElement(i));
            }
            setLocationWithinModule(listContext);
            checkFreeAndBoundDisjunct(0, list);
        } else if (operator.equals(NEGATION_OPERATOR)) {
            Trace.trace(CLASS, this, method, "negation operator found");
            setLocationWithinModule(listContext);
            if (list.size() != 1) {
                handleFormulaCheckException(EXACTLY_ONE_ARGUMENT_EXPECTED,
                    EXACTLY_ONE_ARGUMENT_EXPECTED_TEXT + "\"" + operator + "\"",
                    element, getCurrentContext());
                return;
            }
            setLocationWithinModule(listContext + ".getElement(0)");
            checkFormula(list.getElement(0));
        } else if (operator.equals(PREDICATE_VARIABLE)
                || operator.equals(PREDICATE_CONSTANT)) {
            Trace.trace(CLASS, this, method, "predicate operator found");
            setLocationWithinModule(listContext);
            if (list.size() < 1) {
                handleFormulaCheckException(AT_LEAST_ONE_ARGUMENT_EXPECTED,
                    AT_LEAST_ONE_ARGUMENT_EXPECTED_TEXT + "\"" + operator + "\"",
                    element, getCurrentContext());
                return;
            }
            // check if first argument is an atom
            setLocationWithinModule(listContext + ".getElement(0)");
            if (!checkAtomFirst(list.getElement(0))) {
                return;
            }

            // check if rest arguments are terms
            for (int i = 1; i < list.size(); i++) {
                setLocationWithinModule(listContext + ".getElement(" + i + ")");
                checkTerm(list.getElement(i));
            }

            setLocationWithinModule(listContext);
            checkFreeAndBoundDisjunct(1, list);

            // check if predicate is known
            if (PREDICATE_CONSTANT.equals(operator) && !existenceChecker.predicateExists(
                    list.getElement(0).getAtom().getString(), list.size() - 1)) {
                handleFormulaCheckException(UNKNOWN_PREDICATE_CONSTANT,
                    UNKNOWN_PREDICATE_CONSTANT_TEXT + "\""
                    + list.getElement(0).getAtom().getString() + "\" [" + (list.size() - 1) + "]",
                    element, getCurrentContext());
            }

        } else if (operator.equals(EXISTENTIAL_QUANTIFIER_OPERATOR)
                || operator.equals(UNIQUE_EXISTENTIAL_QUANTIFIER_OPERATOR)
                || operator.equals(UNIVERSAL_QUANTIFIER_OPERATOR)) {
            Trace.trace(CLASS, this, method, "quantifier found");
            setLocationWithinModule(context);
            checkQuantifier(element);
        } else {
            setLocationWithinModule(listContext + ".getOperator()");
            handleFormulaCheckException(UNKNOWN_LOGICAL_OPERATOR,
                UNKNOWN_LOGICAL_OPERATOR_TEXT + "\"" + operator + "\"",
                element, getCurrentContext());
        }
        // restore context
        setLocationWithinModule(context);
        Trace.end(CLASS, this, method);
    }

    /**
     * Check quantifier element.
     *
     * @param   element     Check this element. Must be a quantifier element.
     * @throws  IllegalArgumentException    <code>element.getList().getOperator()</code> is no
     *          quantifier operator.
     */
    private void checkQuantifier(final Element element) {
        final String method = "checkQuantifier";
        Trace.begin(CLASS, this, method);
        Trace.param(CLASS, this, method, "element", element);
        // save context
        final String context = getCurrentContext().getLocationWithinModule();
        Trace.param(CLASS, this, method, "context", context);
        checkList(element);
        final ElementList list = element.getList();
        final String listContext = context + ".getList()";
        setLocationWithinModule(listContext);
        final String operator = list.getOperator();
        if (!operator.equals(EXISTENTIAL_QUANTIFIER_OPERATOR)
                && operator.equals(UNIQUE_EXISTENTIAL_QUANTIFIER_OPERATOR)
                && operator.equals(UNIVERSAL_QUANTIFIER_OPERATOR)) {
            throw new IllegalArgumentException("quantifier element expected but found: "
                    + element.toString());
        }
        if (list.size() < 2 || list.size() > 3) {
            handleFormulaCheckException(EXACTLY_TWO_OR_THREE_ARGUMENTS_EXPECTED,
                EXACTLY_TWO_OR_THREE_ARGUMENTS_EXPECTED_TEXT, element, getCurrentContext());
            return;
        }

        // check if unique existential operator could be used
        if (operator.equals(UNIQUE_EXISTENTIAL_QUANTIFIER_OPERATOR)
                && !existenceChecker.identityOperatorExists()) {
            setLocationWithinModule(listContext + ".getOperator()");
            handleFormulaCheckException(EQUALITY_PREDICATE_NOT_YET_DEFINED,
                EQUALITY_PREDICATE_NOT_YET_DEFINED_TEXT, element, getCurrentContext());
        }

        // check if first argument is subject variable
        setLocationWithinModule(listContext + ".getElement(" + 0 + ")");
        checkSubjectVariable(list.getElement(0));

        // check if second argument is a formula
        setLocationWithinModule(listContext + ".getElement(" + 1 + ")");
        checkFormula(list.getElement(1));

        setLocationWithinModule(listContext);
        // check if subject variable is not already bound in formula
        if (FormulaUtility.getBoundSubjectVariables(list.getElement(1)).contains(
                list.getElement(0))) {
            handleFormulaCheckException(SUBJECT_VARIABLE_ALREADY_BOUND_IN_FORMULA,
                SUBJECT_VARIABLE_ALREADY_BOUND_IN_FORMULA_TEXT, list.getElement(1),
                getCurrentContext());
        }
        if (list.size() > 3) {
            handleFormulaCheckException(EXACTLY_TWO_OR_THREE_ARGUMENTS_EXPECTED,
                EXACTLY_TWO_OR_THREE_ARGUMENTS_EXPECTED_TEXT, list,
                getCurrentContext());
            return;
        }
        if (list.size() > 2) {
            // check if third argument is a formula
            setLocationWithinModule(listContext + ".getElement(" + 2 + ")");
            checkFormula(list.getElement(2));

            // check if subject variable is not bound in formula
            setLocationWithinModule(listContext);
            if (FormulaUtility.getBoundSubjectVariables(list.getElement(2)).contains(
                    list.getElement(0))) {
                handleFormulaCheckException(SUBJECT_VARIABLE_ALREADY_BOUND_IN_FORMULA,
                    SUBJECT_VARIABLE_ALREADY_BOUND_IN_FORMULA_TEXT, list.getElement(2),
                    getCurrentContext());
                return;
            }
            setLocationWithinModule(listContext);
            checkFreeAndBoundDisjunct(1, list);
        }
        // restore context
        setLocationWithinModule(context);
        Trace.end(CLASS, this, method);
    }

    /**
     * Is {@link Element} a term?
     *
     * @param   element    Check this element.
     */
    private void checkTerm(final Element element) {
        final String method = "checkTerm";
        Trace.begin(CLASS, this, method);
        Trace.param(CLASS, this, method, "element", element);
        // save current context
        final String context = getCurrentContext().getLocationWithinModule();
        Trace.param(CLASS, this, method, "context", context);
        if (!checkList(element)) {
            return;
        }
        final ElementList list = element.getList();
        final String listContext = context + ".getList()";
        setLocationWithinModule(listContext);
        final String operator = list.getOperator();
        if (operator.equals(SUBJECT_VARIABLE)) {
            checkSubjectVariable(element);
        } else if (operator.equals(FUNCTION_CONSTANT)
                || operator.equals(FUNCTION_VARIABLE)) {

            // function constants must have at least a function name
            if (operator.equals(FUNCTION_CONSTANT) && list.size() < 1) {
                handleTermCheckException(AT_LEAST_ONE_ARGUMENT_EXPECTED,
                    AT_LEAST_ONE_ARGUMENT_EXPECTED_TEXT, element, getCurrentContext());
                return;
            }

            // function variables must have at least a function name and at least one argument
            if (operator.equals(FUNCTION_VARIABLE) && list.size() < 2) {
                handleTermCheckException(MORE_THAN_ONE_ARGUMENT_EXPECTED,
                    MORE_THAN_ONE_ARGUMENT_EXPECTED_TEXT, element, getCurrentContext());
                return;
            }

            // check if first argument is an atom
            setLocationWithinModule(listContext + ".getElement(0)");
            if (!checkAtomFirst(list.getElement(0))) {
                return;
            }

            // check if all arguments are terms
            setLocationWithinModule(listContext);
            for (int i = 1; i < list.size(); i++) {
                setLocationWithinModule(listContext + ".getElement(" + i + ")");
                checkTerm(list.getElement(i));
            }

            setLocationWithinModule(listContext);
            checkFreeAndBoundDisjunct(1, list);

            // check if function is known
            setLocationWithinModule(listContext);
            if (FUNCTION_CONSTANT.equals(operator) && !existenceChecker.functionExists(
                    list.getElement(0).getAtom().getString(), list.size() - 1)) {
                handleFormulaCheckException(UNKNOWN_FUNCTION_CONSTANT,
                    UNKNOWN_FUNCTION_CONSTANT_TEXT + "\""
                    + list.getElement(0).getAtom().getString() + "\"", element,
                    getCurrentContext());
            }

        } else if (operator.equals(CLASS_OP)) {

            if (list.size() != 2) {
                handleTermCheckException(EXACTLY_TWO_ARGUMENTS_EXPECTED,
                    EXACTLY_TWO_ARGUMENTS_EXPECTED_TEXT, element, getCurrentContext());
                return;
            }
            // check if first argument is a subject variable
            setLocationWithinModule(listContext + ".getElement(" + 0 + ")");
            if (!FormulaUtility.isSubjectVariable(list.getElement(0))) {
                handleTermCheckException(SUBJECT_VARIABLE_EXPECTED,
                    SUBJECT_VARIABLE_EXPECTED_TEXT, element, getCurrentContext());
            }

            // check if the second argument is a formula
            setLocationWithinModule(listContext + ".getElement(" + 1 + ")");
            checkFormula(list.getElement(1));

            // check if class operator is allowed
            setLocationWithinModule(listContext);
            if (!existenceChecker.classOperatorExists()) {
                handleFormulaCheckException(CLASS_OPERATOR_STILL_UNKNOWN,
                    CLASS_OPERATOR_STILL_UNKNOWN_TEXT, element, getCurrentContext());
            }

            // check if subject variable is not bound in formula
            setLocationWithinModule(listContext + ".getElement(" + 0 + ")");
            if (FormulaUtility.getBoundSubjectVariables(list.getElement(1)).contains(
                    list.getElement(0))) {
                handleTermCheckException(SUBJECT_VARIABLE_ALREADY_BOUND_IN_FORMULA,
                    SUBJECT_VARIABLE_ALREADY_BOUND_IN_FORMULA_TEXT, list.getElement(0),
                    getCurrentContext());
            }

        } else {
            setLocationWithinModule(listContext + ".getOperator()");
            handleTermCheckException(UNKNOWN_TERM_OPERATOR,
                UNKNOWN_TERM_OPERATOR_TEXT + "\"" + operator + "\"", element, getCurrentContext());
        }
        // restore context
        setLocationWithinModule(context);
        Trace.end(CLASS, this, method);
    }

    /**
     * Check if no bound variables are free and vice versa.
     * The current context must be at the list element.
     *
     * @param   start   Start check with this list position. Beginning with 0.
     * @param   list    List element to check.
     */
    private void checkFreeAndBoundDisjunct(final int start,
            final ElementList list) {
        // save current context
        final String context = getCurrentContext().getLocationWithinModule();
        final ElementSet free = new ElementSet();
        final ElementSet bound = new ElementSet();
        for (int i = start; i < list.size(); i++) {
            setLocationWithinModule(context + ".getElement(" + i + ")");
            final ElementSet newFree
                = FormulaUtility.getFreeSubjectVariables(list.getElement(i));
            final ElementSet newBound
                = FormulaUtility.getBoundSubjectVariables(list.getElement(i));
            final ElementSet interBound = newFree.newIntersection(bound);
            if (!interBound.isEmpty()) {
                handleFormulaCheckException(FREE_VARIABLE_ALREADY_BOUND,
                    FREE_VARIABLE_ALREADY_BOUND_TEXT
                    + interBound, list.getElement(i), getCurrentContext());
            }
            final ElementSet interFree = newBound.newIntersection(free);
            if (!interFree.isEmpty()) {
                handleFormulaCheckException(BOUND_VARIABLE_ALREADY_FREE,
                    BOUND_VARIABLE_ALREADY_FREE_TEXT
                    + interFree, list.getElement(i), getCurrentContext());
            }
            bound.union(newBound);
            free.union(newFree);
        }
        // restore context
        setLocationWithinModule(context);
    }

    /**
     * Check if {@link Element} is a subject variable.
     *
     * @param   element    Check this element.
     * @return  Is it a subject variable?
     */
    private boolean checkSubjectVariable(final Element element) {
           // throws LogicalCheckException {
        // save current context
        final String context = getCurrentContext().getLocationWithinModule();
        if (!checkList(element)) {
            return false;
        }
        setLocationWithinModule(context + ".getList()");
        if (element.getList().getOperator().equals(SUBJECT_VARIABLE)) {
            if (element.getList().size() != 1) {
                handleFormulaCheckException(EXACTLY_ONE_ARGUMENT_EXPECTED,
                    EXACTLY_ONE_ARGUMENT_EXPECTED_TEXT, element.getList(), getCurrentContext());
                return false;
            }
            // check if first argument is an atom
            setLocationWithinModule(context + ".getList().getElement(0)");
            if (checkAtomFirst(element.getList().getElement(0))) {
                return false;
            }
        } else {
            setLocationWithinModule(context + ".getList().getOperator()");
            handleFormulaCheckException(SUBJECT_VARIABLE_EXPECTED,
                SUBJECT_VARIABLE_EXPECTED_TEXT, element, getCurrentContext());
            return false;
        }
        // restore context
        setLocationWithinModule(context);
        return true;
    }

    /**
     * Check common requirements for list elements that are checked for being a term or formula.
     * That includes: is the element a true list, has the operator a non zero size.
     *
     * @param   element     List element.
     * @return  Are the requirements fulfilled?
     */
    private boolean checkList(final Element element) {
        // save current context
        final String context = getCurrentContext().getLocationWithinModule();
        if (element == null) {
            handleElementCheckException(ELEMENT_MUST_NOT_BE_NULL,
                ELEMENT_MUST_NOT_BE_NULL_TEXT, null, getCurrentContext());
            return false;
        }
        if (!element.isList()) {
            handleElementCheckException(LIST_EXPECTED,
                LIST_EXPECTED_TEXT, element, getCurrentContext());
            return false;
        }
        final ElementList list = element.getList();
        setLocationWithinModule(context + ".getList()");
        if (list == null) {
            handleElementCheckException(LIST_MUST_NOT_BE_NULL,
                LIST_MUST_NOT_BE_NULL_TEXT, element, getCurrentContext());
            return false;
        }
        final String operator = list.getOperator();
        setLocationWithinModule(context + ".getList().getOperator()");
        if (operator == null) {
            handleElementCheckException(OPERATOR_CONTENT_MUST_NOT_BE_NULL,
                OPERATOR_CONTENT_MUST_NOT_BE_NULL_TEXT , element,
                getCurrentContext());
            return false;
        }
        if (operator.length() == 0) {
            handleElementCheckException(OPERATOR_CONTENT_MUST_NOT_BE_EMPTY,
                OPERATOR_CONTENT_MUST_NOT_BE_EMPTY_TEXT + "\""
                + operator + "\"", element, getCurrentContext());
            return false;
        }
        // restore context
        setLocationWithinModule(context);
        return true;
    }

    /**
     * Check if element is an atom and has valid content. It is assumed, that this element is the
     * first of a list.
     *
     * @param   element Check this for an atom.
     * @return  Is the content valid?
     */
    private boolean checkAtomFirst(final Element element) {
        // save current context
        final String context = getCurrentContext().getLocationWithinModule();
        if (element == null) {
            handleElementCheckException(ELEMENT_MUST_NOT_BE_NULL,
                ELEMENT_MUST_NOT_BE_NULL_TEXT, null, getCurrentContext());
            return false;
        }
        if (!element.isAtom()) {    // TODO mime 20061126: this is special?
            handleElementCheckException(FIRST_ARGUMENT_MUST_BE_AN_ATOM,
                FIRST_ARGUMENT_MUST_BE_AN_ATOM_TEXT, element, getCurrentContext());
            return false;
        }
        final Atom atom = element.getAtom();
        setLocationWithinModule(context + ".getAtom()");
        if (atom == null) {
            handleElementCheckException(ATOM_MUST_NOT_BE_NULL,
                ATOM_MUST_NOT_BE_NULL_TEXT, element, getCurrentContext());
            return false;
        }
        if (atom.getString() == null) {
            handleElementCheckException(ATOM_CONTENT_MUST_NOT_BE_NULL,
                ATOM_CONTENT_MUST_NOT_BE_NULL_TEXT, element, getCurrentContext());
            return false;
        }
        if (atom.getString().length() == 0) {
            handleElementCheckException(ATOM_CONTENT_MUST_NOT_BE_EMPTY,
                ATOM_CONTENT_MUST_NOT_BE_EMPTY_TEXT, element, getCurrentContext());
            return false;
        }
        // restore context
        setLocationWithinModule(context);
        return true;
    }

    /**
     * Add new {@link FormulaCheckException} to exception list.
     *
     * @param code      Error code.
     * @param msg       Error message.
     * @param element   Element with error.
     * @param context   Error context.
     */
    private void handleFormulaCheckException(final int code, final String msg,
            final Element element, final ModuleContext context) {
        final FormulaCheckException ex = new FormulaCheckException(code, msg, element, context);
        exceptions.add(ex);
    }

    /**
     * Add new {@link TermCheckException} to exception list.
     *
     * @param code      Error code.
     * @param msg       Error message.
     * @param element   Element with error.
     * @param context   Error context.
     */
    private void handleTermCheckException(final int code, final String msg,
            final Element element, final ModuleContext context) {
        final TermCheckException ex = new TermCheckException(code, msg, element, context);
        exceptions.add(ex);
    }

    /**
     * Add new {@link ElementCheckException} to exception list.
     *
     * @param code      Error code.
     * @param msg       Error message.
     * @param element   Element with error.
     * @param context   Error context.
     */
    private void handleElementCheckException(final int code, final String msg,
            final Element element, final ModuleContext context) {
        final ElementCheckException ex = new ElementCheckException(code, msg, element, context);
        exceptions.add(ex);
    }

    /**
     * Set location information where are we within the original module.
     *
     * @param   locationWithinModule    Location within module.
     */
    protected void setLocationWithinModule(final String locationWithinModule) {
        getCurrentContext().setLocationWithinModule(locationWithinModule);
    }

    /**
     * Get current context within original.
     *
     * @return  Current context.
     */
    protected final ModuleContext getCurrentContext() {
        return currentContext;
    }

}
