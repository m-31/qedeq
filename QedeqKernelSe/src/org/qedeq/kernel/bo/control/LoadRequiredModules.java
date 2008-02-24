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
import org.qedeq.kernel.bo.module.DefaultModuleReferenceList;
import org.qedeq.kernel.bo.visitor.AbstractModuleVisitor;
import org.qedeq.kernel.bo.visitor.QedeqNotNullTraverser;
import org.qedeq.kernel.common.DependencyState;
import org.qedeq.kernel.common.ModuleContext;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.context.KernelContext;
import org.qedeq.kernel.log.ModuleEventLog;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.xml.mapper.ModuleDataException2XmlFileException;
import org.qedeq.kernel.xml.parser.DefaultSourceFileExceptionList;


/**
 * Load all required QEDEQ modules.
 *
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */
public final class LoadRequiredModules extends AbstractModuleVisitor {

    /** This class. */
    private static final Class CLASS = LoadRequiredModules.class;

    /** Traverse QEDEQ module with this traverser. */
    private final QedeqNotNullTraverser traverser;

    /** QEDEQ module properties object to work on. */
    private final DefaultQedeqBo prop;

    /** Kernel services. */
    private final DefaultKernelServices services;

    /** List of required QEDEQ modules. */
    private final DefaultModuleReferenceList required;

    /**
     * Constructor.
     *
     * @param   prop    QEDEQ module properties object.
     * @param   services    Kernel services.
     */
    private LoadRequiredModules(final DefaultQedeqBo prop,
            final DefaultKernelServices services) {
        this.prop = prop;
        this.services = services;
        this.traverser = new QedeqNotNullTraverser(prop.getModuleAddress(), this);
        required = new DefaultModuleReferenceList();
    }

    /**
     * Load all required QEDEQ modules for a given QEDEQ module.
     *
     * @param   prop        Module properties.
     * @param   services    Kernel services.
     * @throws  SourceFileExceptionList Failure(s).
     */
    public static void loadRequired(final DefaultQedeqBo prop, final DefaultKernelServices
            services)
            throws SourceFileExceptionList {
        final String method = "loadRequired(DefaultQedeqBo)";
        // did we check this already?
        if (prop.getDependencyState().areAllRequiredLoaded()) {
            return; // everything is OK
        }
        KernelContext.getInstance().loadModule(prop.getModuleAddress());
        prop.setDependencyProgressState(DependencyState.STATE_LOADING_REQUIRED_MODULES);
        ModuleEventLog.getInstance().stateChanged(prop);
        final LoadRequiredModules converter = new LoadRequiredModules(prop, services);
        try {
            converter.loadRequired();
            prop.setLoadedRequiredModules(converter.required);
            ModuleEventLog.getInstance().stateChanged(prop);
        } catch (ModuleDataException e) {
            final SourceFileExceptionList sfl =
                ModuleDataException2XmlFileException.createXmlFileExceptionList(e,
                    prop.getQedeq());
            prop.setDependencyFailureState(DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED,
                sfl);
            ModuleEventLog.getInstance().stateChanged(prop);
            throw sfl;
        } catch (final RuntimeException e) {    // last catch
            Trace.fatal(LoadRequiredModules.class, method, "programming error", e);
            ModuleDataException me = new LoadRequiredModuleException(10, e.toString(),
                converter.traverser.getCurrentContext());
            final SourceFileExceptionList sfl =
                new DefaultSourceFileExceptionList(me);
            prop.setDependencyFailureState(
                DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED, sfl);
            ModuleEventLog.getInstance().stateChanged(prop);
            throw sfl;
        } catch (final Throwable e) {           // last catch
            ModuleDataException me = new LoadRequiredModuleException(10, e.toString(),
                converter.traverser.getCurrentContext());
            final SourceFileExceptionList sfl =
                new DefaultSourceFileExceptionList(me);
            prop.setDependencyFailureState(
                DependencyState.STATE_LOADING_REQUIRED_MODULES_FAILED, sfl);
            ModuleEventLog.getInstance().stateChanged(prop);
            throw sfl;
        }
    }

    /**
     * Load all required QEDEQ modules for a given QEDEQ module.
     *
     * @throws  ModuleDataException Exception during traverse.
     */
    private final void loadRequired() throws ModuleDataException {
        traverser.accept(prop.getQedeq());
    }

    public void visitEnter(final Import imp) throws ModuleDataException {
        try {
            final DefaultQedeqBo propNew = services.loadModule(prop,
                imp.getSpecification());
            required.add(new ModuleContext(traverser.getCurrentContext()), imp.getLabel(), propNew);
            Trace.param(CLASS, "visitEnter(Import)", "adding context",
                traverser.getCurrentContext());
            loadRequired(propNew, services);
        } catch (SourceFileExceptionList e) {
            Trace.trace(CLASS, this, "visitEnter(Import)", e);
            throw new LoadRequiredModuleException(e.get(0).getErrorCode(),
                "import of module labeled \"" + imp.getLabel() + "\" failed: "
                + e.get(0).getMessage(), traverser.getCurrentContext());
        }
    }

    public void visitLeave(final ImportList imports) {
        traverser.setBlocked(true);
    }

}
