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
import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.bo.module.DefaultModuleReferenceList;
import org.qedeq.kernel.bo.visitor.AbstractModuleVisitor;
import org.qedeq.kernel.bo.visitor.QedeqNotNullTraverser;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.ModuleContext;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.SourceFileException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.xml.mapper.ModuleDataException2SourceFileException;
import org.qedeq.kernel.xml.parser.DefaultSourceFileExceptionList;


/**
 * Load all required QEDEQ modules.
 *
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */
public final class LoadDirectlyRequiredModules extends AbstractModuleVisitor {

    /** Address of QEDEQ module to work on. */
    private final ModuleAddress address;

    /** QEDEQ module properties object to work on. */
    private final Qedeq qedeq;

    /** This class. */
    private static final Class CLASS = LoadDirectlyRequiredModules.class;

    /** Traverse QEDEQ module with this traverser. */
    private final QedeqNotNullTraverser traverser;

    /** Kernel services. */
    private final DefaultKernelServices services;

    /** List of required QEDEQ modules. */
    private final DefaultModuleReferenceList required;

    /** List of Exceptions during Module load. */
    private DefaultSourceFileExceptionList visitorList;

    /**
     * Constructor.
     *
     * @param   address     Module address
     * @param   qedeq       Qedeq.
     * @param   services    Kernel services.
     */
    LoadDirectlyRequiredModules(final ModuleAddress address, final Qedeq qedeq,
            final DefaultKernelServices services) {
        this.address = address;
        this.qedeq = qedeq;
        this.services = services;
        this.traverser = new QedeqNotNullTraverser(address, this);
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
        final String method = "load(DefaultQedeqBo, DefaultKernelServices)";
        try {
            traverser.accept(qedeq);
        } catch (ModuleDataException e) {   // should not happen
            Trace.fatal(CLASS, method, "unexpected exception", e);
            final SourceFileExceptionList sfl =
                ModuleDataException2SourceFileException.createSourceFileExceptionList(e,
                    qedeq);
            throw sfl;
        }
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
     * Get list of exceptions that occurred during loading referenced modules.
     *
     * @return  Exception list.
     */
    DefaultSourceFileExceptionList getSourceFileExceptionList() {
        return visitorList;
    }

    /**
     * Visit import. Loads referenced QEDEQ module and saves reference.
     *
     * @param   imp                 Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(final Import imp) throws ModuleDataException {
        try {
            final DefaultQedeqBo propNew = services.loadModule(address, imp.getSpecification());
            getRequired().addLabelUnique(new ModuleContext(traverser.getCurrentContext()),
                imp.getLabel(), propNew);
            Trace.param(CLASS, "visitEnter(Import)", "adding context",
                traverser.getCurrentContext());
        } catch (SourceFileExceptionList e) {
            final ModuleDataException me = new LoadRequiredModuleException(e.get(0).getErrorCode(),
                "import of module labeled \"" + imp.getLabel() + "\" failed: "
                + e.get(0).getMessage(), traverser.getCurrentContext());
            // TODO mime 20080227: also include reference area in sf creation?
            final SourceFileException sf = ModuleDataException2SourceFileException
                .createSourceFileException(me, this.qedeq);
            if (getSourceFileExceptionList() == null) {
                setSourceFileExceptionList(new DefaultSourceFileExceptionList(sf));
            } else {
                getSourceFileExceptionList().add(sf);
            }
            Trace.trace(CLASS, this, "visitEnter(Import)", e);
        }
    }

    /**
     * End of visit of import list. Blocks further visits.
     *
     * @param   imports             This visit has just ended.
     */
    public void visitLeave(final ImportList imports) {
        traverser.setBlocked(true); // TODO mime 20080226: is this enough? isn't blocking per
                                    // node and sub notes? if so this is not enough!
    }

    private void setSourceFileExceptionList(final DefaultSourceFileExceptionList visitorList) {
        this.visitorList = visitorList;
    }

}
