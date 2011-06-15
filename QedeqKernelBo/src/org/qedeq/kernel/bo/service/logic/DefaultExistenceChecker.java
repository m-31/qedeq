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
import org.qedeq.kernel.bo.logic.common.FunctionConstant;
import org.qedeq.kernel.bo.logic.common.FunctionKey;
import org.qedeq.kernel.bo.logic.common.PredicateConstant;
import org.qedeq.kernel.bo.logic.common.PredicateKey;
import org.qedeq.kernel.bo.logic.common.RuleKey;
import org.qedeq.kernel.se.base.module.InitialFunctionDefinition;
import org.qedeq.kernel.se.base.module.InitialPredicateDefinition;
import org.qedeq.kernel.se.base.module.Rule;


/**
 * Checks if all predicate and function constants exist already.
 *
 * @author  Michael Meyling
 */
public class DefaultExistenceChecker implements ExistenceChecker {

    /** This class. */
    private static final Class CLASS = DefaultExistenceChecker.class;

    /** Maps {@link PredicateKey} identifiers to {@link InitialPredicateDefinitions}s. */
    private final Map initialPredicateDefinitions = new HashMap();

    /** Maps {@link PredicateKey} identifiers to {@link PredicateConstant}s. */
    private final Map predicateDefinitions = new HashMap();

    /** Maps {@link FunctionKey} identifiers to {@link InitialFunctionDefinition}s. */
    private final Map initialFunctionDefinitions = new HashMap();

    /** Maps {@link FunctionKey} identifiers to {@link FunctionConstant}s. */
    private final Map functionDefinitions = new HashMap();

    /** Maps {@link Rule} identifiers to {@link Rules}s. */
    private final Map ruleDefinitions = new HashMap();

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
        initialPredicateDefinitions.clear();
        predicateDefinitions.clear();
        initialFunctionDefinitions.clear();
        ruleDefinitions.clear();
        functionDefinitions.clear();
        identityOperator = null;
        setDefinitionByFormula = false;
    }

    public boolean predicateExists(final PredicateKey predicate) {
        final InitialPredicateDefinition initialDefinition
            = (InitialPredicateDefinition) initialPredicateDefinitions.get(predicate);
        if (initialDefinition != null) {
            return true;
        }
        return null != predicateDefinitions.get(predicate);
    }

    public boolean predicateExists(final String name, final int arguments) {
        final PredicateKey predicate = new PredicateKey(name, "" + arguments);
        return predicateExists(predicate);
    }

    public boolean isInitialPredicate(final PredicateKey predicate) {
        final InitialPredicateDefinition initialDefinition
            = (InitialPredicateDefinition) initialPredicateDefinitions.get(predicate);
        return initialDefinition != null;
    }

    /**
     * Add unknown predicate constant definition. If the predicate constant is already known a
     * runtime exception is thrown.
     *
     * @param   initialDefinition   Predicate constant definition that is not already known. Must not be
     *                              <code>null</code>.
     * @throws  IllegalArgumentException    Predicate constant is already defined.
     */
    public void add(final InitialPredicateDefinition initialDefinition) {
        final PredicateKey predicate = new PredicateKey(initialDefinition.getName(),
            initialDefinition.getArgumentNumber());
        if (predicateExists(predicate)) {
            throw new IllegalArgumentException(LogicErrors.PREDICATE_ALREADY_DEFINED_TEXT
                + predicate);
        }
        initialPredicateDefinitions.put(predicate, initialDefinition);
    }

    /**
     * Add unknown predicate constant definition. If the predicate constant is already known a
     * runtime exception is thrown.
     *
     * @param   constant    Predicate constant definition that is not already known. Must not be
     *                      <code>null</code>.
     * @throws  IllegalArgumentException    Predicate constant is already defined.
     */
    public void add(final PredicateConstant constant) {
        final PredicateKey predicate = constant.getKey();
        if (predicateExists(predicate)) {
            throw new IllegalArgumentException(LogicErrors.PREDICATE_ALREADY_DEFINED_TEXT
                + predicate);
        }
        predicateDefinitions.put(predicate, constant);
    }

    /**
     * Get predicate constant definition.
     *
     * @param   predicate   Get definition of this predicate.
     * @return  Definition.
     */
    public PredicateConstant get(final PredicateKey predicate) {
        return (PredicateConstant) predicateDefinitions.get(predicate);
    }

    /**
     * Get predicate constant definition.
     *
     * @param   name        Name of predicate.
     * @param   arguments   Arguments of predicate.
     * @return  Definition. Might be <code>null</code>.
     */
    public PredicateConstant getPredicate(final String name, final int arguments) {
        final PredicateKey predicate = new PredicateKey(name, "" + arguments);
        return get(predicate);
    }

    public boolean functionExists(final FunctionKey function) {
        final InitialFunctionDefinition initialDefinition
            = (InitialFunctionDefinition) initialFunctionDefinitions.get(function);
        if (initialDefinition != null) {
            return true;
        }
        return null != functionDefinitions.get(function);
    }

    public boolean functionExists(final String name, final int arguments) {
        final FunctionKey function = new FunctionKey(name, "" + arguments);
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
    public void add(final FunctionConstant definition) {
        final FunctionKey function = definition.getKey();
        if (functionDefinitions.get(function) != null) {
            throw new IllegalArgumentException(LogicErrors.FUNCTION_ALREADY_DEFINED_TEXT
                + function);
        }
        functionDefinitions.put(function, definition);
    }

    /**
     * Add unknown function constant definition. If the function constant is already known a
     * runtime exception is thrown.
     *
     * @param   initialDefinition   Function constant definition that is not already known. Must not be
     *                              <code>null</code>.
     * @throws  IllegalArgumentException    Function constant is already defined.
     */
    public void add(final InitialFunctionDefinition initialDefinition) {
        final FunctionKey predicate = new FunctionKey(initialDefinition.getName(),
            initialDefinition.getArgumentNumber());
        if (functionExists(predicate)) {
            throw new IllegalArgumentException(LogicErrors.FUNCTION_ALREADY_DEFINED_TEXT
                + predicate);
        }
        initialFunctionDefinitions.put(predicate, initialDefinition);
    }

    /**
     * Get function constant definition.
     *
     * @param   function    Get definition of this predicate.
     * @return  Definition. Might be <code>null</code>.
     */
    public FunctionConstant get(final FunctionKey function) {
        return (FunctionConstant) functionDefinitions.get(function);
    }

    /**
     * Get function constant definition.
     *
     * @param   name        Name of function.
     * @param   arguments   Arguments of function.
     * @return  Definition. Might be <code>null</code>.
     */
    public FunctionConstant getFunction(final String name, final int arguments) {
        final FunctionKey function = new FunctionKey(name, "" + arguments);
        return get(function);
    }

    public boolean isInitialFunction(final FunctionKey function) {
        final InitialFunctionDefinition initialDefinition
            = (InitialFunctionDefinition) initialFunctionDefinitions.get(function);
        return initialDefinition != null;
    }


    public boolean ruleExists(final RuleKey ruleKey) {
        final Rule ruleDefinition
            = (Rule) ruleDefinitions.get(ruleKey);
        if (ruleDefinition != null) {
            return true;
        }
        return null != ruleDefinitions.get(ruleKey);
    }

    public boolean ruleExists(final String name, final String version) {
        final RuleKey ruleKey = new RuleKey(name, version);
        return ruleExists(ruleKey);
    }

    /**
     * Add unknown rule definition. If the rule is already known a runtime exception is thrown.
     *
     * @param   definition  Rule definition that is not already known. Must not be
     *                      <code>null</code>.
     * @throws  IllegalArgumentException    Rule is already defined (for given version).
     */
    public void add(final Rule definition) {
        final RuleKey ruleKey = new RuleKey(definition.getName(), definition.getVersion());
        if (ruleDefinitions.get(ruleKey) != null) {
            throw new IllegalArgumentException(LogicErrors.RULE_ALREADY_DEFINED_TEXT
                + ruleKey);
        }
        ruleDefinitions.put(ruleKey, definition);
    }

    /**
     * Get rule definition.
     *
     * @param   ruleKey Get definition of this key.
     * @return  Definition. Might be <code>null</code>.
     */
    public Rule get(final RuleKey ruleKey) {
        return (Rule) ruleDefinitions.get(ruleKey);
    }

    /**
     * Get rule definition.
     *
     * @param   name        Name of function.
     * @param   version   Arguments of function.
     * @return  Rule. Might be <code>null</code>.
     */
    public Rule getRule(final String name, final String version) {
        final RuleKey ruleKey = new RuleKey(name, version);
        return get(ruleKey);
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
