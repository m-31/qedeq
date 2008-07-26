/* $Id: ModuleConstantsExistenceChecker.java,v 1.1 2008/07/26 07:58:29 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2008,  Michael Meyling <mime@qedeq.org>.
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
import org.qedeq.kernel.bo.module.DefaultExistenceChecker;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.common.ModuleDataException;


/**
 * Checks if all formulas of a QEDEQ module are well formed.
 *
 * @version $Revision: 1.1 $
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
     * {@link DefaultExistenceChecker#setIdentityOperatorDefined(boolean, String)} and
     * {@link DefaultExistenceChecker#setClassOperatorExists(boolean)}) are set accordingly.
     *
     * @throws ModuleDataException  Required modules doesn't mix.
     */
    public final void init() throws ModuleDataException {
        clear();
        boolean identityOperatorExists = false;
        boolean classOperatorExists = false;
        final ModuleReferenceList list = prop.getRequiredModules();
        String identityOperator = null;
        for (int i = 0; i < list.size(); i++) {
            final KernelQedeqBo bo = (KernelQedeqBo) list
                .getQedeqBo(i);
            if (bo.getExistenceChecker().equalityOperatorExists()) {
                if (identityOperatorExists) {
                    // FIXME mime 20089116: check if both definitions are the same (Module URL ==)
                    throw new IdentityOperatorAlreadyExistsException(123476,
                        "identity operator already defined", list.getModuleContext(i));
                }
                identityOperatorExists = true;
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
        setIdentityOperatorDefined(identityOperatorExists, identityOperator);
        setClassOperatorExists(classOperatorExists);
    }

    public boolean predicateExists(final String name, final int arguments) {
        final int external = name.indexOf('.');
        if (external < 0) {
            return super.predicateExists(name, arguments);
        }
        final String label = name.substring(0, external);
        final ModuleReferenceList ref = prop.getRequiredModules();

        final KernelQedeqBo newProp = (KernelQedeqBo) ref
            .getQedeqBo(label);
        if (newProp == null) {
            return false;
        }
        final String shortName = name.substring(external + 1);
        return newProp.getExistenceChecker().predicateExists(shortName, arguments);
    }

    public boolean functionExists(final String name, final int arguments) {
        final int external = name.indexOf(".");
        if (external < 0) {
            return super.functionExists(name, arguments);
        }
        final String label = name.substring(0, external);
        final ModuleReferenceList ref = prop.getRequiredModules();
        final KernelQedeqBo newProp = (KernelQedeqBo) ref
            .getQedeqBo(label);
        final String shortName = name.substring(external + 1);
        return newProp.getExistenceChecker().functionExists(shortName, arguments);
    }

}
