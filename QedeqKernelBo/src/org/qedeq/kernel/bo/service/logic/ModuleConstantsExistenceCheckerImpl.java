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

import org.qedeq.kernel.bo.common.ModuleReferenceList;
import org.qedeq.kernel.bo.logic.common.ClassOperatorAlreadyExistsException;
import org.qedeq.kernel.bo.logic.common.FunctionConstant;
import org.qedeq.kernel.bo.logic.common.FunctionKey;
import org.qedeq.kernel.bo.logic.common.IdentityOperatorAlreadyExistsException;
import org.qedeq.kernel.bo.logic.common.ModuleConstantsExistenceChecker;
import org.qedeq.kernel.bo.logic.common.PredicateConstant;
import org.qedeq.kernel.bo.logic.common.PredicateKey;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.common.ModuleDataException;


/**
 * Checks if a predicate or function constant is defined.
 *
 * @author  Michael Meyling
 */
public class ModuleConstantsExistenceCheckerImpl extends DefaultExistenceChecker
        implements ModuleConstantsExistenceChecker {

    /** QEDEQ module properties. */
    private final KernelQedeqBo prop;

    /** In this module the class operator is defined. */
    private KernelQedeqBo classOperatorModule;

    /** In this class the identityOperator is defined. */
    private KernelQedeqBo identityOperatorModule;

    /**
     * Constructor.
     *
     * @param   prop                QEDEQ module properties object.
     * @throws  ModuleDataException Referenced QEDEQ modules are already inconsistent: they doesn't
     *          mix.
     */
    public ModuleConstantsExistenceCheckerImpl(final KernelQedeqBo prop) throws  ModuleDataException {
        super();
        this.prop = prop;
        init();
    }

    /**
     * Check if required QEDEQ modules mix without problems. If for example the identity operator
     * is defined in two different modules in two different ways we have got a problem.
     * Also the basic properties (for example
     * {@link ModuleConstantsExistenceCheckerImpl#setIdentityOperatorDefined(String,
     * KernelQedeqBo, ModuleContext)} and
     * {@link ModuleConstantsExistenceCheckerImpl#setClassOperatorModule(KernelQedeqBo)}) are set accordingly.
     *
     * @throws ModuleDataException  Required modules doesn't mix.
     */
    public final void init() throws ModuleDataException {
        clear();
        final ModuleReferenceList list = prop.getRequiredModules();
        for (int i = 0; i < list.size(); i++) {
            final KernelQedeqBo bo = (KernelQedeqBo) list
                .getQedeqBo(i);
            if (bo.getExistenceChecker().identityOperatorExists()) {
                final String identityOperator = list.getLabel(i) + "."
                    + bo.getExistenceChecker().getIdentityOperator();
                setIdentityOperatorDefined(identityOperator, bo.getExistenceChecker().getIdentityOperatorModule(),
                    list.getModuleContext(i));
            }
            if (bo.getExistenceChecker().classOperatorExists()) {
                setClassOperatorModule(bo.getExistenceChecker().getClassOperatorModule(),
                    list.getModuleContext(i));
            }
        }
    }

    public boolean predicateExists(final PredicateKey predicate) {
        final String name = predicate.getName();
        final String arguments = predicate.getArguments();
        final int external = name.indexOf('.');
        if (external < 0) {
            return super.predicateExists(predicate);
        }
        final String label = name.substring(0, external);
        final ModuleReferenceList ref = prop.getRequiredModules();

        final KernelQedeqBo newProp = (KernelQedeqBo) ref
            .getQedeqBo(label);
        if (newProp == null) {
            return false;
        }
        final String shortName = name.substring(external + 1);
        return newProp.getExistenceChecker().predicateExists(new PredicateKey(shortName, arguments));
    }

    public boolean functionExists(final FunctionKey function) {
        final String name = function.getName();
        final String arguments = function.getArguments();
        final int external = name.indexOf(".");
        if (external < 0) {
            return super.functionExists(function);
        }
        final String label = name.substring(0, external);
        final ModuleReferenceList ref = prop.getRequiredModules();
        final KernelQedeqBo newProp = (KernelQedeqBo) ref
            .getQedeqBo(label);
        if (newProp == null) {
            return false;
        }
        final String shortName = name.substring(external + 1);
        return newProp.getExistenceChecker().functionExists(new FunctionKey(shortName, arguments));
    }

    public boolean isInitialPredicate(final PredicateKey predicate) {
        final String name = predicate.getName();
        final String arguments = predicate.getArguments();
        final int external = name.indexOf('.');
        if (external < 0) {
            return super.isInitialPredicate(predicate);
        }
        final String label = name.substring(0, external);
        final ModuleReferenceList ref = prop.getRequiredModules();

        final KernelQedeqBo newProp = (KernelQedeqBo) ref
            .getQedeqBo(label);
        if (newProp == null) {
            return false;
        }
        final String shortName = name.substring(external + 1);
        return newProp.getExistenceChecker().isInitialPredicate(new PredicateKey(shortName, arguments));
    }

    public boolean isInitialFunction(final FunctionKey function) {
        final String name = function.getName();
        final String arguments = function.getArguments();
        final int external = name.indexOf('.');
        if (external < 0) {
            return super.isInitialFunction(function);
        }
        final String label = name.substring(0, external);
        final ModuleReferenceList ref = prop.getRequiredModules();

        final KernelQedeqBo newProp = (KernelQedeqBo) ref
            .getQedeqBo(label);
        if (newProp == null) {
            return false;
        }
        final String shortName = name.substring(external + 1);
        return newProp.getExistenceChecker().isInitialFunction(new FunctionKey(shortName, arguments));
    }

    public PredicateConstant get(final PredicateKey predicate) {
        final String name = predicate.getName();
        final String arguments = predicate.getArguments();
        final int external = name.indexOf('.');
        if (external < 0) {
            return super.get(predicate);
        }
        final String label = name.substring(0, external);
        final ModuleReferenceList ref = prop.getRequiredModules();

        final KernelQedeqBo newProp = (KernelQedeqBo) ref
            .getQedeqBo(label);
        if (newProp == null) {
            return null;
        }
        final String shortName = name.substring(external + 1);
        return newProp.getExistenceChecker().get(new PredicateKey(shortName, arguments));
    }

    public FunctionConstant get(final FunctionKey function) {
        final String name = function.getName();
        final String arguments = function.getArguments();
        final int external = name.indexOf(".");
        if (external < 0) {
            return super.get(function);
        }
        final String label = name.substring(0, external);
        final ModuleReferenceList ref = prop.getRequiredModules();
        final KernelQedeqBo newProp = (KernelQedeqBo) ref
            .getQedeqBo(label);
        if (newProp == null) {
            return null;
        }
        final String shortName = name.substring(external + 1);
        return newProp.getExistenceChecker().get(new FunctionKey(shortName, arguments));
    }

    /**
     * Get QEDEQ module where given function constant is defined.
     *
     * @param   function    Function we look for.
     * @return  QEDEQ module where function constant is defined.
     */
    public KernelQedeqBo getQedeq(final FunctionKey function) {
        final String name = function.getName();
        final String arguments = function.getArguments();
        final int external = name.indexOf(".");
        if (external < 0) {
            if (functionExists(function)) {
                return prop;
            } else {
                return null;
            }
        }
        final String label = name.substring(0, external);
        final ModuleReferenceList ref = prop.getRequiredModules();
        final KernelQedeqBo newProp = (KernelQedeqBo) ref.getQedeqBo(label);
        if (newProp == null) {
            return newProp;
        }
        final String shortName = name.substring(external + 1);
        return newProp.getExistenceChecker().getQedeq(new FunctionKey(shortName, arguments));
    }

    /**
     * Get QEDEQ module where given predicate constant is defined.
     *
     * @param   predicate   Predicate we look for.
     * @return  QEDEQ module where predicate constant is defined.x
     */
    public KernelQedeqBo getQedeq(final PredicateKey predicate) {
        final String name = predicate.getName();
        final String arguments = predicate.getArguments();
        final int external = name.indexOf(".");
        if (external < 0) {
            if (predicateExists(predicate)) {
                return prop;
            } else {
                return null;
            }
        }
        final String label = name.substring(0, external);
        final ModuleReferenceList ref = prop.getRequiredModules();
        final KernelQedeqBo newProp = (KernelQedeqBo) ref.getQedeqBo(label);
        if (newProp == null) {
            return newProp;
        }
        final String shortName = name.substring(external + 1);
        return newProp.getExistenceChecker().getQedeq(new PredicateKey(shortName, arguments));
    }

    public boolean classOperatorExists() {
        return classOperatorModule != null;
    }

    /**
     * Set the identity operator.
     *
     * @param   identityOperator        Operator name. Might be <code>null</code>.
     * @param   identityOperatorModule  In this module the identity operator is defined.
     * @param   context                 Here we are within the module.
     * @throws  IdentityOperatorAlreadyExistsException  Already defined.
     */
    public void setIdentityOperatorDefined(final String identityOperator,
            final KernelQedeqBo identityOperatorModule, final ModuleContext context)
            throws IdentityOperatorAlreadyExistsException {
        if (this.identityOperatorModule != null && identityOperatorModule != null) {
            if (!this.identityOperatorModule.equals(identityOperatorModule)) {
                throw new IdentityOperatorAlreadyExistsException(LogicErrors.IDENTITY_OPERATOR_ALREADY_EXISTS_CODE,
                    LogicErrors.IDENTITY_OPERATOR_ALREADY_EXISTS_TEXT + " " + getIdentityOperator(), context);
            }
        } else {
            this.identityOperatorModule = identityOperatorModule;
            super.setIdentityOperatorDefined(identityOperator);
        }
    }

    public KernelQedeqBo getClassOperatorModule() {
        return classOperatorModule;
    }

    public KernelQedeqBo getIdentityOperatorModule() {
        return identityOperatorModule;
    }

    /**
     * Set if the class operator is already defined.
     *
     * @param   classOperatorModule  Module where class operator is defined.
     * @param   context              Context where we try to set new class operator.
     * @throws  ClassOperatorAlreadyExistsException Operator already defined.
     */
    public void setClassOperatorModule(final KernelQedeqBo classOperatorModule,
            final ModuleContext context) throws  ClassOperatorAlreadyExistsException {
        if (this.classOperatorModule != null && classOperatorModule != null) {
            if (!this.classOperatorModule.equals(classOperatorModule)) {
                throw new ClassOperatorAlreadyExistsException(123478,
                    "class operator already defined within " + this.classOperatorModule.getUrl(),
                    context);
            }
        } else {
            this.classOperatorModule = classOperatorModule;
        }
    }

}
