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

package org.qedeq.kernel.bo.service.logic;

import java.util.HashMap;
import java.util.Map;

import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.logic.common.ExistenceChecker;
import org.qedeq.kernel.bo.logic.common.Function;
import org.qedeq.kernel.bo.logic.common.Predicate;
import org.qedeq.kernel.bo.logic.wf.HigherLogicalErrors;
import org.qedeq.kernel.se.base.module.FunctionDefinition;
import org.qedeq.kernel.se.base.module.PredicateDefinition;


/**
 * Checks if all predicate and function constants exist already.
 *
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

    /** Identity operator. */
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
        identityOperator = null;
        setDefinitionByFormula = false;
    }

    public boolean predicateExists(final Predicate predicate) {
        final PredicateDefinition definition = (PredicateDefinition) predicateDefinitions
            .get(predicate);
        return null != definition;
    }

    public boolean predicateExists(final String name, final int arguments) {
        final Predicate predicate = new Predicate(name, "" + arguments);
        return predicateExists(predicate);
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
     * @return  Definition. Might be <code>null</code>.
     */
    public PredicateDefinition getPredicate(final String name, final int arguments) {
        final Predicate predicate = new Predicate(name, "" + arguments);
        return get(predicate);
    }

    public boolean functionExists(final Function function) {
        final FunctionDefinition definition = (FunctionDefinition) functionDefinitions
            .get(function);
        return null != definition;
    }

    public boolean functionExists(final String name, final int arguments) {
        final Function function = new Function(name, "" + arguments);
        return functionExists(function);
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
     * @return  Definition. Might be <code>null</code>.
     */
    public FunctionDefinition get(final Function function) {
        return (FunctionDefinition) functionDefinitions.get(function);
    }

    /**
     * Get function constant definition.
     *
     * @param   name        Name of function.
     * @param   arguments   Arguments of function.
     * @return  Definition. Might be <code>null</code>.
     */
    public FunctionDefinition getFunction(final String name, final int arguments) {
        final Function function = new Function(name, "" + arguments);
        return get(function);
    }

    public boolean classOperatorExists() {
        return setDefinitionByFormula;
    }

//    /**
//     * Set if the class operator is already defined.
//     *
//     * @param   existence  Class operator is defined.
//     */
// TODO m31 20100820: write some tests that use this feature
//    public void setClassOperatorExists(final boolean existence) {
//        Trace.param(CLASS, this, "setClassOperatorExists", "existence", existence);
//        setDefinitionByFormula = existence;
//    }

    public boolean identityOperatorExists() {
        return this.identityOperator != null;
    }

    public String getIdentityOperator() {
        return this.identityOperator;
    }

    /**
     * Set the identity operator.
     *
     * @param   identityOperator    Operator name. Might be <code>null</code>.
     */
    public void setIdentityOperatorDefined(final String identityOperator) {
        Trace.param(CLASS, this, "setIdentityOperatorDefined", "identityOperator", identityOperator);
        this.identityOperator = identityOperator;
    }

}
