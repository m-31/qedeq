/* $Id: LoadRequiredModules.java,v 1.2 2008/01/26 12:39:09 m31 Exp $
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

import org.qedeq.kernel.base.module.Import;
import org.qedeq.kernel.base.module.ImportList;
import org.qedeq.kernel.common.ModuleContext;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.trace.Trace;


/**
 * Load all required QEDEQ modules.
 *
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */
public final class LoadDirectlyRequiredModules extends ControlVisitor {

    /** This class. */
    private static final Class CLASS = LoadDirectlyRequiredModules.class;

    /** Kernel services. */
    private final DefaultKernelServices services;

    /** List of required QEDEQ modules. */
    private final DefaultModuleReferenceList required;

    /**
     * Constructor.
     *
     * @param   prop        Internal QedeqBo.
     * @param   services    Internal kernel services.
     */
    LoadDirectlyRequiredModules(final DefaultQedeqBo prop,
            final DefaultKernelServices services) {
        super(prop);
        this.services = services;
        this.required = new DefaultModuleReferenceList();
    }

    /**
     * Load all directly imported QEDEQ modules for a given QEDEQ module.
     *
     * @return  List of all directly imported QEDEQ modules.
     * @throws  SourceFileExceptionList Failure(s).
     */
    DefaultModuleReferenceList load()
            throws SourceFileExceptionList {
        final String method = "load()";
        traverse();
        return required;
    }

    /**
     * Get list of directly referenced modules.
     *
     * @return  List of directly required modules.
     */
    DefaultModuleReferenceList getRequired() {
        return required;
    }

    /**
     * Visit import. Loads referenced QEDEQ module and saves reference.
     *
     * @param   imp                 Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(final Import imp) throws ModuleDataException {
        try {
            final DefaultQedeqBo propNew = services.loadModule(getQedeqBo().getModuleAddress(),
                imp.getSpecification());
            getRequired().addLabelUnique(new ModuleContext(getCurrentContext()),
                imp.getLabel(), propNew);
            Trace.param(CLASS, "visitEnter(Import)", "adding context", getCurrentContext());
        } catch (SourceFileExceptionList e) {
            final ModuleDataException me = new LoadRequiredModuleException(e.get(0).getErrorCode(),
                "import of module labeled \"" + imp.getLabel() + "\" failed: "
                + e.get(0).getMessage(), getCurrentContext());
            // TODO mime 20080227: also include reference area in sf creation?
            addModuleDataException(me);
            Trace.trace(CLASS, this, "visitEnter(Import)", e);
        }
    }

    /**
     * End of visit of import list. Blocks further visits.
     *
     * @param   imports             This visit has just ended.
     */
    public void visitLeave(final ImportList imports) {
        setBlocked(true); // block further traverse
    }

}
