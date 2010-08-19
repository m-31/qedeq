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

package org.qedeq.kernel.bo.service;

import org.qedeq.kernel.bo.ModuleReferenceList;
import org.qedeq.kernel.bo.logic.wf.Function;
import org.qedeq.kernel.bo.logic.wf.Predicate;
import org.qedeq.kernel.bo.module.DefaultExistenceChecker;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.common.ModuleDataException;


/**
 * Checks if all formulas of a QEDEQ module are well formed.
 *
 * @author  Michael Meyling
 */
public class ModuleConstantsExistenceChecker extends DefaultExistenceChecker {

    /** QEDEQ module properties. */
    private final KernelQedeqBo prop;

    /**
     * Constructor.
     *
     * @param   prop                QEDEQ module properties object.
     * @throws  ModuleDataException Referenced QEDEQ modules are already inconsistent: they doesn't
     *          mix.
     */
    public ModuleConstantsExistenceChecker(final KernelQedeqBo prop) throws  ModuleDataException {
        super();
        this.prop = prop;
        init();
    }

    /**
     * Check if required QEDEQ modules mix without problems. If for example the identity operator
     * is defined in two different modules in two different ways we have got a problem.
     * Also the basic properties (for example
     * {@link DefaultExistenceChecker#setIdentityOperatorDefined(String)} and
     * {@link DefaultExistenceChecker#setClassOperatorExists(boolean)}) are set accordingly.
     *
     * @throws ModuleDataException  Required modules doesn't mix.
     */
    public final void init() throws ModuleDataException {
        clear();
        boolean classOperatorExists = false;
        final ModuleReferenceList list = prop.getRequiredModules();
        String identityOperator = null;
        for (int i = 0; i < list.size(); i++) {
            final DefaultKernelQedeqBo bo = (DefaultKernelQedeqBo) list
                .getQedeqBo(i);
            if (bo.getExistenceChecker().identityOperatorExists()) {
                if (identityOperator != null
                        && !getQedeq(new Predicate(identityOperator, "" + 2)).equals(
                            bo.getExistenceChecker().getQedeq(new Predicate(bo.getExistenceChecker()
                                .getIdentityOperator(), "" + 2)))) {
                    throw new IdentityOperatorAlreadyExistsException(123476,
                        "identity operator already defined with " + identityOperator,
                        list.getModuleContext(i));
                }
                identityOperator = list.getLabel(i) + "."
                    + bo.getExistenceChecker().getIdentityOperator();
            }
            if (bo.getExistenceChecker().classOperatorExists()) {
                if (classOperatorExists) {
                    // FIXME mime 20089116: check if both definitions are the same (Module URL ==)
                    throw new ClassOperatorAlreadyExistsException(123478,
                        "class operator already defined", list.getModuleContext(i));
                }
                classOperatorExists = true;
            }
        }
        setIdentityOperatorDefined(identityOperator);
        setClassOperatorExists(classOperatorExists);
    }

    public boolean predicateExists(final Predicate predicate) {
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
        return newProp.getExistenceChecker().predicateExists(new Predicate(shortName, arguments));
    }

    public boolean functionExists(final Function function) {
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
        final String shortName = name.substring(external + 1);
        return newProp.getExistenceChecker().functionExists(new Function(shortName, arguments));
    }

    /**
     * Get QEDEQ module where given function constant is defined.
     *
     * @param   function    Function we look for.
     * @return  QEDEQ module where function constant is defined.x
     */
    public KernelQedeqBo getQedeq(final Function function) {
        final String name = function.getName();
        final String arguments = function.getArguments();
        final int external = name.indexOf(".");
        if (external < 0) {
            return prop;
        }
        final String label = name.substring(0, external);
        final ModuleReferenceList ref = prop.getRequiredModules();
        final KernelQedeqBo newProp = (KernelQedeqBo) ref.getQedeqBo(label);
        final String shortName = name.substring(external + 1);
        return newProp.getExistenceChecker().getQedeq(new Function(shortName, arguments));
    }

    /**
     * Get QEDEQ module where given predicate constant is defined.
     *
     * @param   predicate   Predicate we look for.
     * @return  QEDEQ module where predicate constant is defined.x
     */
    public KernelQedeqBo getQedeq(final Predicate predicate) {
        final String name = predicate.getName();
        final String arguments = predicate.getArguments();
        final int external = name.indexOf(".");
        if (external < 0) {
            return prop;
        }
        final String label = name.substring(0, external);
        final ModuleReferenceList ref = prop.getRequiredModules();
        final KernelQedeqBo newProp = (KernelQedeqBo) ref.getQedeqBo(label);
        final String shortName = name.substring(external + 1);
        return newProp.getExistenceChecker().getQedeq(new Predicate(shortName, arguments));
    }

}
