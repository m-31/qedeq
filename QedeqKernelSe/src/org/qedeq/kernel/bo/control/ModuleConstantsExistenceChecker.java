/* $Id: QedeqBoFormalLogicChecker.java,v 1.5 2007/12/21 23:33:46 m31 Exp $
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

package org.qedeq.kernel.bo.control;

import org.qedeq.kernel.bo.logic.DefaultExistenceChecker;
import org.qedeq.kernel.bo.module.ModuleDataException;
import org.qedeq.kernel.bo.module.ModuleProperties;
import org.qedeq.kernel.bo.module.ModuleReferenceList;


/**
 * Checks if all formulas of a QEDEQ module are well formed.
 *
 * @version $Revision: 1.5 $
 * @author  Michael Meyling
 */
public class ModuleConstantsExistenceChecker extends DefaultExistenceChecker {

    /** QEDEQ module properties. */
    private final ModuleProperties prop;

    /**
     * Constructor.
     *
     * @param   prop                QEDEQ module properties object.
     * @throws  ModuleDataException Referenced QEDEQ modules are already inconsistent: they doesn't
     *          mix.
     */
    public ModuleConstantsExistenceChecker(final ModuleProperties prop)
            throws  ModuleDataException {
        super();
        this.prop = prop;
        initialCheck();
    }

    public void initialCheck() throws ModuleDataException {
        clear();
        boolean identityOperatorExists = false;
        boolean classOperatorExists = false;
        final ModuleReferenceList list = prop.getRequiredModules();
        String identityOperator = null;
        for (int i = 0; i < list.size(); i++) {
            if (list.getModuleProperties(i).getExistenceChecker().equalityOperatorExists()) {
                if (identityOperatorExists) {
                    // FIXME mime 20089116: check if both definitions are the same (Module URL ==)
                    throw new IdentityOperatorAlreadyExistsException(123476,
                        "identity operator already defined", list.getModuleContext(i));
                }
                identityOperatorExists = true;
                identityOperator = list.getLabel(i) + "."
                    + list.getModuleProperties(i).getExistenceChecker().getIdentityOperator();
            }
            if (list.getModuleProperties(i).getExistenceChecker().classOperatorExists()) {
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
        final int external = name.indexOf(".");
        if (external < 0) {
            return super.predicateExists(name, arguments);
        }
        final String label = name.substring(0, external);
        final ModuleReferenceList ref = prop.getRequiredModules();
        final ModuleProperties newProp = ref.getModuleProperties(label);
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
        final ModuleProperties newProp = ref.getModuleProperties(label);
        final String shortName = name.substring(external + 1);
        return newProp.getExistenceChecker().functionExists(shortName, arguments);
    }

}
