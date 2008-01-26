/* $Id: DefaultExistenceChecker.java,v 1.1 2008/01/26 12:39:09 m31 Exp $
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

import java.util.HashMap;
import java.util.Map;

import org.qedeq.kernel.base.module.FunctionDefinition;
import org.qedeq.kernel.base.module.PredicateDefinition;
import org.qedeq.kernel.bo.control.HigherLogicalErrors;
import org.qedeq.kernel.trace.Trace;


/**
 * Checks if all predicate and function constants exist already.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class DefaultExistenceChecker implements ExistenceChecker {

    /** This class. */
    private static final Class CLASS = DefaultExistenceChecker.class;

    /** Maps {@link Predicate} identifiers to {@link PredicateDefinition}s. */
    private final Map predicateDefinitions = new HashMap();

    /** Maps {@link Function} identifiers to {@link FunctionDefinition}s. */
    private final Map functionDefinitions = new HashMap();

    /** Is the class operator already defined? */
    private boolean setDefinitionByFormula;

    /** Is the identity operator already defined? */
    private boolean identityOperatorDefined;

    /** Identity operator name. */
    private String identityOperator;


    /**
     * Constructor.
     */
    public DefaultExistenceChecker() {
        clear();
    }

    /**
     * Empty all definitions.
     */
    public void clear() {
        Trace.trace(CLASS, this, "setClassOperatorExists", "clear");
        predicateDefinitions.clear();
        functionDefinitions.clear();
        identityOperatorDefined = false;
        identityOperator = null;
        setDefinitionByFormula = false;
    }

    /**
     * Check if a predicate constant is already defined.
     *
     * @param   predicate   Predicate.
     * @return  Predicate is already defined.
     */
    public boolean predicateExists(final Predicate predicate) {
        final PredicateDefinition definition = (PredicateDefinition) predicateDefinitions
            .get(predicate);
        return null != definition;
    }

    public boolean predicateExists(final String name, final int arguments) {
        final Predicate predicate = new Predicate(name, "" + arguments);
        final PredicateDefinition definition = (PredicateDefinition) predicateDefinitions
            .get(predicate);
        return null != definition;
    }

    /**
     * Add unknown predicate constant definition. If the predicate constant is already known a
     * runtime exception is thrown.
     *
     * @param   definition  Predicate constant definition that is not already known. Must not be
     *                      <code>null</code>.
     * @throws  IllegalArgumentException    Predicate constant is already defined.
     */
    public void add(final PredicateDefinition definition) {
        final Predicate predicate = new Predicate(definition.getName(),
            definition.getArgumentNumber());
        if (predicateDefinitions.get(predicate) != null) {
            throw new IllegalArgumentException(HigherLogicalErrors.PREDICATE_ALREADY_DEFINED_TEXT
                + predicate);
        }
        predicateDefinitions.put(predicate, definition);
    }

    /**
     * Get predicate constant definition.
     *
     * @param   predicate   Get definition of this predicate.
     * @return  Definition.
     */
    public PredicateDefinition get(final Predicate predicate) {
        return (PredicateDefinition) predicateDefinitions.get(predicate);
    }

    /**
     * Get predicate constant definition.
     *
     * @param   name        Name of predicate.
     * @param   arguments   Arguments of predicate.
     * @return  Definition.
     */
    public PredicateDefinition getPredicate(final String name, final int arguments) {
        final Predicate predicate = new Predicate(name, "" + arguments);
        return get(predicate);
    }

    /**
     * Check if a function constant is already defined.
     *
     * @param   function    Function.
     * @return  Function is already defined.
     */
    public boolean functionExists(final Function function) {
        final FunctionDefinition definition = (FunctionDefinition) functionDefinitions
            .get(function);
        return null != definition;
    }

    public boolean functionExists(final String name, final int arguments) {
        final Function function = new Function(name, "" + arguments);
        final FunctionDefinition definition = (FunctionDefinition) functionDefinitions
            .get(function);
        return null != definition;
    }

    /**
     * Add unknown function constant definition. If the function constant is already known a
     * runtime exception is thrown.
     *
     * @param   definition  Function constant definition that is not already known. Must not be
     *                      <code>null</code>.
     * @throws  IllegalArgumentException    Function constant is already defined.
     */
    public void add(final FunctionDefinition definition) {
        final Function function = new Function(definition.getName(),
            definition.getArgumentNumber());
        if (functionDefinitions.get(function) != null) {
            throw new IllegalArgumentException(HigherLogicalErrors.FUNCTION_ALREADY_DEFINED_TEXT
                + function);
        }
        functionDefinitions.put(function, definition);
    }

    /**
     * Get function constant definition.
     *
     * @param   function    Get definition of this predicate.
     * @return  Definition.
     */
    public FunctionDefinition get(final Function function) {
        return (FunctionDefinition) functionDefinitions.get(function);
    }

    /**
     * Get function constant definition.
     *
     * @param   name        Name of function.
     * @param   arguments   Arguments of function.
     * @return  Definition.
     */
    public FunctionDefinition getFunction(final String name, final int arguments) {
        final Function function = new Function(name, "" + arguments);
        return get(function);
    }

    public boolean classOperatorExists() {
        return setDefinitionByFormula;
    }

    /**
     * Set if the class operator is already defined.
     *
     * @param   existence  Class operator is defined.
     */
    public void setClassOperatorExists(final boolean existence) {
        Trace.param(CLASS, this, "setClassOperatorExists", "existence", existence);
        setDefinitionByFormula = existence;
    }

    public boolean equalityOperatorExists() {
        return identityOperatorDefined;
    }

    /**
     * Set the identity operator.
     *
     * @param   defined             Is the operator defined?
     * @param   identityOperator    Operator name.
     */
    public void setIdentityOperatorDefined(final boolean defined, final String identityOperator) {
        Trace.param(CLASS, this, "setIdentityOperatorDefined", "defined", defined);
        this.identityOperatorDefined = defined;
        if (defined) {
            this.identityOperator = identityOperator;
        } else {
            this.identityOperator = null;
        }
    }

    public String getIdentityOperator() {
        if (!equalityOperatorExists()) {
            return null;
        }
        return this.identityOperator;
    }

}
