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

package org.qedeq.kernel.bo.module;

import org.qedeq.kernel.base.module.FunctionDefinition;
import org.qedeq.kernel.base.module.PredicateDefinition;
import org.qedeq.kernel.bo.logic.wf.ExistenceChecker;
import org.qedeq.kernel.bo.logic.wf.Function;
import org.qedeq.kernel.bo.logic.wf.Predicate;
import org.qedeq.kernel.bo.service.ClassOperatorAlreadyExistsException;
import org.qedeq.kernel.bo.service.IdentityOperatorAlreadyExistsException;
import org.qedeq.kernel.common.ModuleContext;

/**
 * Contains methods for existence checking of various operands.
 *
 * @author  Michael Meyling
 */
public interface ModuleConstantsExistenceCheckerInterface extends ExistenceChecker {

    public boolean predicateExists(final Predicate predicate);

    public boolean functionExists(final Function function);

    /**
     * Get QEDEQ module where given function constant is defined.
     *
     * @param   function    Function we look for.
     * @return  QEDEQ module where function constant is defined.
     */
    public KernelQedeqBo getQedeq(final Function function);

    /**
     * Get QEDEQ module where given predicate constant is defined.
     *
     * @param   predicate   Predicate we look for.
     * @return  QEDEQ module where predicate constant is defined.x
     */
    public KernelQedeqBo getQedeq(final Predicate predicate);

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

    public KernelQedeqBo getClassOperatorModule();

    public KernelQedeqBo getIdentityOperatorModule();

    /**
     * Get predicate constant definition.
     *
     * @param   predicate   Get definition of this predicate.
     * @return  Definition.
     */
    public PredicateDefinition get(final Predicate predicate);

    /**
     * Get predicate constant definition.
     *
     * @param   name        Name of predicate.
     * @param   arguments   Arguments of predicate.
     * @return  Definition. Might be <code>null</code>.
     */
    public PredicateDefinition getPredicate(final String name, final int arguments);

    /**
     * Get function constant definition.
     *
     * @param   function    Get definition of this predicate.
     * @return  Definition. Might be <code>null</code>.
     */
    public FunctionDefinition get(final Function function);

    /**
     * Get function constant definition.
     *
     * @param   name        Name of function.
     * @param   arguments   Arguments of function.
     * @return  Definition. Might be <code>null</code>.
     */
    public FunctionDefinition getFunction(final String name, final int arguments);


}