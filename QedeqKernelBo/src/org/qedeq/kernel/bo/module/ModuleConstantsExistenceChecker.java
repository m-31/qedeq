/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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

import java.util.Map;

import org.qedeq.kernel.bo.logic.common.ClassOperatorAlreadyExistsException;
import org.qedeq.kernel.bo.logic.common.ExistenceChecker;
import org.qedeq.kernel.bo.logic.common.FunctionConstant;
import org.qedeq.kernel.bo.logic.common.FunctionKey;
import org.qedeq.kernel.bo.logic.common.IdentityOperatorAlreadyExistsException;
import org.qedeq.kernel.bo.logic.common.PredicateConstant;
import org.qedeq.kernel.bo.logic.common.PredicateKey;
import org.qedeq.kernel.se.base.module.Rule;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.common.RuleKey;

/**
 * Contains methods for existence checking of various operands.
 *
 * @author  Michael Meyling
 */
public interface ModuleConstantsExistenceChecker extends ExistenceChecker {

    public boolean predicateExists(final PredicateKey predicate);

    public boolean functionExists(final FunctionKey function);

    /**
     * Get QEDEQ module where given function constant is defined.
     *
     * @param   function    Function we look for.
     * @return  QEDEQ module where function constant is defined.
     */
    public KernelQedeqBo getQedeq(final FunctionKey function);

    /**
     * Get QEDEQ module where given predicate constant is defined.
     *
     * @param   predicate   Predicate we look for.
     * @return  QEDEQ module where predicate constant is defined.x
     */
    public KernelQedeqBo getQedeq(final PredicateKey predicate);

    /**
     * Get QEDEQ module where given rule is defined.
     *
     * @param   ruleKey   Rule we look for.
     * @return  QEDEQ module where rule is defined.x
     */
    public KernelQedeqBo getQedeq(final RuleKey ruleKey);

    /**
     * Get maximum rule version that is defined in this or an imported module.
     *
     * @param   ruleName   Rule we look for.
     * @return  Rule key with maximum version.x
     */
    public RuleKey getRuleKey(final String ruleName);

    /**
     * Get maximum rule version that is defined in an imported module.
     *
     * @param   ruleName   Rule we look for.
     * @return  Rule key with maximum version.
     */
    public RuleKey getParentRuleKey(final String ruleName);

    /**
     * Get map of all {@link RuleKey}s defined in this module or in one of the imported ones.
     *
     * @return  Map of all defined rule keys mapping from {@link RuleKey} to {@link KernelQedeqBo}.
     */
    public Map getRules();

    public boolean classOperatorExists();

    /**
     * Set the identity operator.
     *
     * @param   identityOperator        Operator name. Might be <code>null</code>.
     * @param   identityOperatorModule  In this module the identity operator is defined.
     * @param   context                 Here we are within the module.
     * @throws  IdentityOperatorAlreadyExistsException  Already defined.
     */
    public void setIdentityOperatorDefined(final String identityOperator,
            final KernelQedeqBo identityOperatorModule,
            final ModuleContext context)
            throws IdentityOperatorAlreadyExistsException;

    /**
     * Set if the class operator is already defined.
     *
     * @param   classOperatorModule  Module where class operator is defined.
     * @param   context              Context where we try to set new class operator.
     * @throws  ClassOperatorAlreadyExistsException Operator already defined.
     */
    public void setClassOperatorModule(
            final KernelQedeqBo classOperatorModule,
            final ModuleContext context)
            throws ClassOperatorAlreadyExistsException;

    /**
     * Get QEDEQ module where the class operator is defined within.
     *
     * @return  Class operator defining module.
     */
    public KernelQedeqBo getClassOperatorModule();

    /**
     * Get QEDEQ module where the identity operator is defined within.
     *
     * @return  Identity operator defining module.
     */
    public KernelQedeqBo getIdentityOperatorModule();

    /**
     * Get predicate constant definition.
     *
     * @param   predicate   Get definition of this predicate.
     * @return  Definition.
     */
    public PredicateConstant get(final PredicateKey predicate);

    /**
     * Get predicate constant definition.
     *
     * @param   name        Name of predicate.
     * @param   arguments   Arguments of predicate.
     * @return  Definition. Might be <code>null</code>.
     */
    public PredicateConstant getPredicate(final String name, final int arguments);

    /**
     * Get function constant definition.
     *
     * @param   function    Get definition of this predicate.
     * @return  Definition. Might be <code>null</code>.
     */
    public FunctionConstant get(final FunctionKey function);

    /**
     * Get function constant definition.
     *
     * @param   name        Name of function.
     * @param   arguments   Arguments of function.
     * @return  Definition. Might be <code>null</code>.
     */
    public FunctionConstant getFunction(final String name, final int arguments);

    /**
     * Get rule declaration.
     *
     * @param   ruleKey     Get definition of this rule.
     * @return  Rule. Might be <code>null</code>.
     */
    public Rule get(final RuleKey ruleKey);

}
