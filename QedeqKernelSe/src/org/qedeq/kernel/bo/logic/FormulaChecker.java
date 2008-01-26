/* $Id: FormulaChecker.java,v 1.9 2008/01/26 12:39:09 m31 Exp $
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

package org.qedeq.kernel.bo.logic;

import org.qedeq.kernel.base.list.Atom;
import org.qedeq.kernel.base.list.Element;
import org.qedeq.kernel.base.list.ElementList;
import org.qedeq.kernel.bo.module.ModuleContext;
import org.qedeq.kernel.dto.list.ElementSet;
import org.qedeq.kernel.trace.Trace;


/**
 * This class deals with {@link org.qedeq.kernel.base.list.Element}s which represent a
 * formula. It has methods to check those elements for certain criteria.
 *
 * LATER mime 20070307: here are sometimes error messages that get concatenated with
 * an {@link org.qedeq.kernel.base.list.ElementList#getOperator()} string. Perhaps these
 * strings must be translated into the original input format and a mapping must be done.
 *
 * @version $Revision: 1.9 $
 * @author  Michael Meyling
 */
public final class FormulaChecker implements Operators, FormulaBasicErrors {

    // If you want to check if the context information within this class is correct you have to
    // do the following:
    // 1. add "private Qedeq qedeq;" as a new field of this class
    // 2. add DynamicGetter from test project into this package
    // 3. modify {@link #setLocationWithinModule(String) by calling
    //             DynamicGetter.get(qedeq, getCurrentContext().getLocationWithinModule());
    // 4. modify the {@link #checkFormula(Element, ModuleContext, ExistenceChecker} by adding
    //    a Qedeq parameter;
    // 5. modify {@link #checkTerm(Element, ModuleContext, ExistenceChecker) analogous

    /** This class. */
    private static final Class CLASS = FormulaChecker.class;

    /** Current context during creation. */
    private final ModuleContext currentContext;

    /** Existence checker for operators. */
    private final ExistenceChecker existenceChecker;

    /**
     * Constructor.
     *
     * @param   context             For location information. Important for locating errors.
     * @param   existenceChecker    Existence checker for operators.
     * @throws  IllegalArgumentException    The <code>existenceChecker</code> says the equality
     *          operator exists but the predicate is not found. This should be a programming
     *          error.
     */
    private FormulaChecker(final ModuleContext context,
            final ExistenceChecker existenceChecker) {
        this.existenceChecker = existenceChecker;
        if (existenceChecker.equalityOperatorExists()
                && !existenceChecker.predicateExists(existenceChecker.getIdentityOperator(), 2)) {
            throw new IllegalArgumentException("equality predicate should exist, but it doesn't");
        }
        currentContext = new ModuleContext(context);
    }

    /**
     * Is {@link Element} a formula?
     *
     * @param   element             Check this element.
     * @param   context             For location information. Important for locating errors.
     * @param   existenceChecker    Existence checker for operators.
     * @throws  LogicalCheckException  It is no formula.
     */
    public static final void checkFormula(final Element element, final ModuleContext context,
            final ExistenceChecker existenceChecker) throws LogicalCheckException {
        final FormulaChecker checker = new FormulaChecker(context, existenceChecker);
        checker.checkFormula(element);
    }

    /**
     * Is {@link Element} a formula? All predicates and functions are assumed to exit.
     * If the existence context is known you should use
     * {@link #checkFormula(Element, ModuleContext, ExistenceChecker)}.
     *
     * @param   element             Check this element.
     * @param   context             For location information. Important for locating errors.
     * @throws  LogicalCheckException  It is no formula.
     */
    public static final void checkFormula(final Element element, final ModuleContext context)
            throws LogicalCheckException {
        checkFormula(element, context, EverythingExists.getInstance());
    }

    /**
     * Is {@link Element} a term?
     *
     * @param   element             Check this element.
     * @param   context             For location information. Important for locating errors.
     * @param   existenceChecker    Existence checker for operators.
     * @throws  LogicalCheckException      It is no term.
     */
    public static final void checkTerm(final Element element, final ModuleContext context,
            final ExistenceChecker existenceChecker) throws LogicalCheckException {
        final FormulaChecker checker = new FormulaChecker(context, existenceChecker);
        checker.checkTerm(element);
    }

    /**
     * Is {@link Element} a term?  If the existence context is known you should use
     * {@link #checkTerm(Element, ModuleContext, ExistenceChecker)}.
     *
     * @param   element Check this element.
     * @param   context For location information. Important for locating errors.
     * @throws  LogicalCheckException  It is no term.
     */
    public static final void checkTerm(final Element element, final ModuleContext context)
            throws LogicalCheckException {
        checkTerm(element, context, EverythingExists.getInstance());
    }

    /**
     * Is {@link Element} a formula?
     *
     * @param   element    Check this element.
     * @throws  LogicalCheckException  It is no formula.
     */
    private final void checkFormula(final Element element)
            throws LogicalCheckException {
        final String method = "checkFormula";
        Trace.begin(CLASS, this, method);
        Trace.param(CLASS, this, method, "element", element);
        final String context = getCurrentContext().getLocationWithinModule();
        Trace.param(CLASS, this, method, "context", context);
        checkList(element);
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
                throw new FormulaCheckException(MORE_THAN_ONE_ARGUMENT_EXPECTED,
                    MORE_THAN_ONE_ARGUMENT_EXPECTED_TEXT + "\""
                    + operator + "\"", element, getCurrentContext());
            }
            if (operator.equals(IMPLICATION_OPERATOR) && list.size() != 2) {
                throw new FormulaCheckException(EXACTLY_TWO_ARGUMENTS_EXPECTED,
                    EXACTLY_TWO_ARGUMENTS_EXPECTED_TEXT + "\""
                    + operator + "\"", element, getCurrentContext());
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
                throw new FormulaCheckException(EXACTLY_ONE_ARGUMENT_EXPECTED,
                    EXACTLY_ONE_ARGUMENT_EXPECTED_TEXT + "\"" + operator + "\"",
                    element, getCurrentContext());
            }
            setLocationWithinModule(listContext + ".getElement(0)");
            checkFormula(list.getElement(0));
        } else if (operator.equals(PREDICATE_VARIABLE)
                || operator.equals(PREDICATE_CONSTANT)) {
            Trace.trace(CLASS, this, method, "predicate operator found");
            setLocationWithinModule(listContext);
            if (list.size() < 1) {
                throw new FormulaCheckException(AT_LEAST_ONE_ARGUMENT_EXPECTED,
                    AT_LEAST_ONE_ARGUMENT_EXPECTED_TEXT + "\"" + operator + "\"",
                    element, getCurrentContext());
            }
            // check if first argument is an atom
            setLocationWithinModule(listContext + ".getElement(0)");
            checkAtomFirst(list.getElement(0));

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
                throw new FormulaCheckException(UNKNOWN_PREDICATE_CONSTANT,
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
            throw new FormulaCheckException(UNKNOWN_LOGICAL_OPERATOR,
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
     * @throws  LogicalCheckException  Check failed.
     * @throws  IllegalArgumentException    <code>element.getList().getOperator()</code> is no
     *          quantifier operator.
     */
    private void checkQuantifier(final Element element) throws LogicalCheckException {
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
            throw new FormulaCheckException(EXACTLY_TWO_OR_THREE_ARGUMENTS_EXPECTED,
                EXACTLY_TWO_OR_THREE_ARGUMENTS_EXPECTED_TEXT, element, getCurrentContext());
        }

        // check if unique existential operator could be used
        if (operator.equals(UNIQUE_EXISTENTIAL_QUANTIFIER_OPERATOR)
                && !existenceChecker.equalityOperatorExists()) {
            setLocationWithinModule(listContext + ".getOperator()");
            throw new FormulaCheckException(EQUALITY_PREDICATE_NOT_YET_DEFINED,
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
        if (FormulaChecker.getBoundSubjectVariables(list.getElement(1)).contains(
                list.getElement(0))) {
            throw new FormulaCheckException(SUBJECT_VARIABLE_ALREADY_BOUND_IN_FORMULA,
                SUBJECT_VARIABLE_ALREADY_BOUND_IN_FORMULA_TEXT, list.getElement(1),
                getCurrentContext());
        }
        if (list.size() > 3) {
            throw new FormulaCheckException(EXACTLY_TWO_OR_THREE_ARGUMENTS_EXPECTED,
                EXACTLY_TWO_OR_THREE_ARGUMENTS_EXPECTED_TEXT, list,
                getCurrentContext());
        }
        if (list.size() > 2) {
            // check if third argument is a formula
            setLocationWithinModule(listContext + ".getElement(" + 2 + ")");
            checkFormula(list.getElement(2));

            // check if subject variable is not bound in formula
            setLocationWithinModule(listContext);
            if (FormulaChecker.getBoundSubjectVariables(list.getElement(2)).contains(
                    list.getElement(0))) {
                throw new FormulaCheckException(SUBJECT_VARIABLE_ALREADY_BOUND_IN_FORMULA,
                    SUBJECT_VARIABLE_ALREADY_BOUND_IN_FORMULA_TEXT, list.getElement(2),
                    getCurrentContext());
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
     * @throws  LogicalCheckException  It is no term.
     */
    private void checkTerm(final Element element) throws LogicalCheckException {
        final String method = "checkTerm";
        Trace.begin(CLASS, this, method);
        Trace.param(CLASS, this, method, "element", element);
        // save current context
        final String context = getCurrentContext().getLocationWithinModule();
        Trace.param(CLASS, this, method, "context", context);
        checkList(element);
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
                throw new TermCheckException(AT_LEAST_ONE_ARGUMENT_EXPECTED,
                    AT_LEAST_ONE_ARGUMENT_EXPECTED_TEXT, element, getCurrentContext());
            }

            // function variables must have at least a function name and at least one argument
            if (operator.equals(FUNCTION_VARIABLE) && list.size() < 2) {
                throw new TermCheckException(MORE_THAN_ONE_ARGUMENT_EXPECTED,
                    MORE_THAN_ONE_ARGUMENT_EXPECTED_TEXT, element, getCurrentContext());
            }

            // check if first argument is an atom
            setLocationWithinModule(listContext + ".getElement(0)");
            checkAtomFirst(list.getElement(0));

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
                throw new FormulaCheckException(UNKNOWN_FUNCTION_CONSTANT,
                    UNKNOWN_FUNCTION_CONSTANT_TEXT + "\""
                    + list.getElement(0).getAtom().getString() + "\"", element,
                    getCurrentContext());
            }

        } else if (operator.equals(CLASS_OP)) {

            if (list.size() != 2) {
                throw new TermCheckException(EXACTLY_TWO_ARGUMENTS_EXPECTED,
                    EXACTLY_TWO_ARGUMENTS_EXPECTED_TEXT, element, getCurrentContext());
            }
            // check if first argument is a subject variable
            setLocationWithinModule(listContext + ".getElement(" + 0 + ")");
            if (!isSubjectVariable(list.getElement(0))) {
                throw new TermCheckException(SUBJECT_VARIABLE_EXPECTED,
                    SUBJECT_VARIABLE_EXPECTED_TEXT, element, getCurrentContext());
            }

            // check if the second argument is a formula
            setLocationWithinModule(listContext + ".getElement(" + 1 + ")");
            checkFormula(list.getElement(1));

            // check if class operator is allowed
            setLocationWithinModule(listContext);
            if (!existenceChecker.classOperatorExists()) {
                throw new FormulaCheckException(CLASS_OPERATOR_STILL_UNKNOWN,
                    CLASS_OPERATOR_STILL_UNKNOWN_TEXT, element, getCurrentContext());
            }

            // check if subject variable is not bound in formula
            setLocationWithinModule(listContext + ".getElement(" + 0 + ")");
            if (FormulaChecker.getBoundSubjectVariables(list.getElement(1)).contains(
                    list.getElement(0))) {
                throw new TermCheckException(SUBJECT_VARIABLE_ALREADY_BOUND_IN_FORMULA,
                    SUBJECT_VARIABLE_ALREADY_BOUND_IN_FORMULA_TEXT, list.getElement(0),
                    getCurrentContext());
            }

        } else {
            setLocationWithinModule(listContext + ".getOperator()");
            throw new TermCheckException(UNKNOWN_TERM_OPERATOR,
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
     * @throws  LogicalCheckException  At least one variable occurs free and bound in different
     *          sub-elements.
     */
    private void checkFreeAndBoundDisjunct(final int start,
            final ElementList list) throws LogicalCheckException {
        // save current context
        final String context = getCurrentContext().getLocationWithinModule();
        final ElementSet free = new ElementSet();
        final ElementSet bound = new ElementSet();
        for (int i = start; i < list.size(); i++) {
            setLocationWithinModule(context + ".getElement(" + i + ")");
            final ElementSet newFree
                = getFreeSubjectVariables(list.getElement(i));
            final ElementSet newBound
                = FormulaChecker.getBoundSubjectVariables(list.getElement(i));
            final ElementSet interBound = newFree.newIntersection(bound);
            if (!interBound.isEmpty()) {
                throw new FormulaCheckException(FREE_VARIABLE_ALREADY_BOUND,
                    FREE_VARIABLE_ALREADY_BOUND_TEXT
                    + interBound, list.getElement(i), getCurrentContext());
            }
            final ElementSet interFree = newBound.newIntersection(free);
            if (!interFree.isEmpty()) {
                throw new FormulaCheckException(BOUND_VARIABLE_ALREADY_FREE,
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
     * Is {@link Element} a subject variable?
     *
     * @param   element    Element to look onto.
     * @return  Is it a subject variable?
     */
    private final boolean isSubjectVariable(final Element element) {
        if (element == null || !element.isList() || element.getList() == null) {
            return false;
        }
        final ElementList list = element.getList();
        if (list.getOperator().equals(SUBJECT_VARIABLE)) {
            if (list.size() != 1) {
                return false;
            }
            final Element first = element.getList().getElement(0);
            if (first == null || !first.isAtom() || first.getAtom() == null) {
                return false;
            }
            final Atom atom = first.getAtom();
            if (atom.getString() == null || atom.getAtom().getString() == null
                    || atom.getString().length() == 0) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }


    /**
     * Check if {@link Element} is a subject variable.
     *
     * @param   element    Check this element.
     * @throws  LogicalCheckException  It is no subject variable.
     */
    private final void checkSubjectVariable(final Element element)
            throws LogicalCheckException {
        // save current context
        final String context = getCurrentContext().getLocationWithinModule();
        checkList(element);
        setLocationWithinModule(context + ".getList()");
        if (element.getList().getOperator().equals(SUBJECT_VARIABLE)) {
            if (element.getList().size() != 1) {
                throw new FormulaCheckException(EXACTLY_ONE_ARGUMENT_EXPECTED,
                    EXACTLY_ONE_ARGUMENT_EXPECTED_TEXT, element.getList(), getCurrentContext());
            }
            // check if first argument is an atom
            setLocationWithinModule(context + ".getList().getElement(0)");
            checkAtomFirst(element.getList().getElement(0));
        } else {
            setLocationWithinModule(context + ".getList().getOperator()");
            throw new FormulaCheckException(SUBJECT_VARIABLE_EXPECTED,
                SUBJECT_VARIABLE_EXPECTED_TEXT, element, getCurrentContext());
        }
        // restore context
        setLocationWithinModule(context);
    }


    /**
     * Return all free subject variables of an element.
     *
     * @param   element    Work on this element.
     * @return  All free subject variables.
     */
    private final ElementSet getFreeSubjectVariables(final Element element) {
        final ElementSet free = new ElementSet();
        if (isSubjectVariable(element)) {
            free.add(element);
        } else if (element.isList()) {
            final ElementList list = element.getList();
            final String operator = list.getOperator();
            if (operator.equals(EXISTENTIAL_QUANTIFIER_OPERATOR)
                || operator.equals(UNIQUE_EXISTENTIAL_QUANTIFIER_OPERATOR)
                || operator.equals(UNIVERSAL_QUANTIFIER_OPERATOR)
                || operator.equals(CLASS_OP)) {
                for (int i = 1; i < list.size(); i++) {
                    free.union(getFreeSubjectVariables(list.getElement(i)));
                }
                free.remove(list.getElement(0));
            } else {
                for (int i = 0; i < list.size(); i++) {
                    free.union(getFreeSubjectVariables(list.getElement(i)));
                }
            }
        }
        return free;
    }

    /**
     * Return all bound subject variables of an element.
     *
     * @param   element    Work on this element.
     * @return  All bound subject variables.
     */
    public static final ElementSet getBoundSubjectVariables(final Element element) {
        final ElementSet bound = new ElementSet();
        if (element.isList()) {
            ElementList list = element.getList();
            final String operator = list.getOperator();
            // if operator is quantifier or class term
            if (operator.equals(EXISTENTIAL_QUANTIFIER_OPERATOR)
                    || operator.equals(UNIQUE_EXISTENTIAL_QUANTIFIER_OPERATOR)
                    || operator.equals(UNIVERSAL_QUANTIFIER_OPERATOR)
                    || operator.equals(CLASS_OP)) {
                // add subject variable to bound list
                bound.add(list.getElement(0));
                // add all bound variables of sub-elements
                for (int i = 1; i < list.size(); i++) {
                    bound.union(FormulaChecker.getBoundSubjectVariables(
                        list.getElement(i)));
                }
            } else {
                // add all bound variables of sub-elements
                for (int i = 0; i < list.size(); i++) {
                    bound.union(FormulaChecker.getBoundSubjectVariables(list.getElement(i)));
                }
            }
        }
        return bound;
    }

    /**
     * Check common requirements for list elements that are checked for being a term or formula.
     *
     * @param   element     List element.
     * @throws  ElementCheckException   Requirements not fulfilled.
     */
    private void checkList(final Element element) throws ElementCheckException {
        // save current context
        final String context = getCurrentContext().getLocationWithinModule();
        if (element == null) {
            throw new ElementCheckException(ELEMENT_MUST_NOT_BE_NULL,
                ELEMENT_MUST_NOT_BE_NULL_TEXT, null, getCurrentContext());
        }
        if (!element.isList()) {
            throw new ElementCheckException(LIST_EXPECTED,
                LIST_EXPECTED_TEXT, element, getCurrentContext());
        }
        final ElementList list = element.getList();
        setLocationWithinModule(context + ".getList()");
        if (list == null) {
            throw new ElementCheckException(LIST_MUST_NOT_BE_NULL,
                LIST_MUST_NOT_BE_NULL_TEXT, element, getCurrentContext());
        }
        final String operator = list.getOperator();
        setLocationWithinModule(context + ".getList().getOperator()");
        if (operator == null) {
            throw new ElementCheckException(OPERATOR_CONTENT_MUST_NOT_BE_NULL,
                OPERATOR_CONTENT_MUST_NOT_BE_NULL_TEXT + "\""
                + operator + "\"", element, getCurrentContext());
        }
        if (operator.length() == 0) {
            throw new ElementCheckException(OPERATOR_CONTENT_MUST_NOT_BE_EMPTY,
                OPERATOR_CONTENT_MUST_NOT_BE_EMPTY_TEXT + "\""
                + operator + "\"", element, getCurrentContext());
        }
        // restore context
        setLocationWithinModule(context);
    }

    /**
     * Check if element is an atom and has valid content. It is assumed, that this element is the
     * first of a list.
     *
     * @param   element Check this for an atom.
     * @throws  ElementCheckException   No valid content.
     */
    private void checkAtomFirst(final Element element) throws ElementCheckException {
        // save current context
        final String context = getCurrentContext().getLocationWithinModule();
        if (element == null) {
            throw new ElementCheckException(ELEMENT_MUST_NOT_BE_NULL,
                ELEMENT_MUST_NOT_BE_NULL_TEXT, null, getCurrentContext());
        }
        if (!element.isAtom()) {    // TODO mime 20061126: this is special?
            throw new ElementCheckException(FIRST_ARGUMENT_MUST_BE_AN_ATOM,
                FIRST_ARGUMENT_MUST_BE_AN_ATOM_TEXT, element, getCurrentContext());
        }
        final Atom atom = element.getAtom();
        setLocationWithinModule(context + ".getAtom()");
        if (atom == null) {
            throw new ElementCheckException(ATOM_MUST_NOT_BE_NULL,
                ATOM_MUST_NOT_BE_NULL_TEXT, element, getCurrentContext());
        }
        if (atom.getString() == null) {
            throw new ElementCheckException(ATOM_CONTENT_MUST_NOT_BE_NULL,
                ATOM_CONTENT_MUST_NOT_BE_NULL_TEXT, element, getCurrentContext());
        }
        if (atom.getString().length() == 0) {
            throw new ElementCheckException(ATOM_CONTENT_MUST_NOT_BE_EMPTY,
                ATOM_CONTENT_MUST_NOT_BE_EMPTY_TEXT, element, getCurrentContext());
        }
        // restore context
        setLocationWithinModule(context);
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
