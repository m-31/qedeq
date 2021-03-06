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

package org.qedeq.kernel.bo.service.dependency;

import org.qedeq.base.io.Parameters;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.module.InternalModuleServiceCall;
import org.qedeq.kernel.bo.module.KernelModuleReferenceList;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.service.basis.ControlVisitor;
import org.qedeq.kernel.bo.service.basis.ModuleServicePluginExecutor;
import org.qedeq.kernel.se.base.module.Import;
import org.qedeq.kernel.se.base.module.ImportList;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.ModuleService;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.state.LoadingImportsState;


/**
 * Load all directly imported QEDEQ modules.
 *
 * @author  Michael Meyling
 */
public final class LoadDirectlyRequiredModulesExecutor extends ControlVisitor
        implements ModuleServicePluginExecutor {

    /** This class. */
    private static final Class CLASS = LoadDirectlyRequiredModulesExecutor.class;

    /** List of required QEDEQ modules. */
    private KernelModuleReferenceList required;

    /**
     * Constructor.
     *
     * @param   plugin      Plugin we work for.
     * @param   prop        Internal QedeqBo.
     * @param   parameter   Currently ignored.
     */
    public LoadDirectlyRequiredModulesExecutor(final ModuleService plugin, final KernelQedeqBo prop,
            final Parameters parameter) {
        super(plugin, prop);
    }

    public Object executePlugin(final InternalModuleServiceCall call, final Object data) {
        if (getKernelQedeqBo().hasLoadedImports()) {
            return getKernelQedeqBo().getRequiredModules();
        }
        this.required = new KernelModuleReferenceList();
        try {
            getKernelQedeqBo().setLoadingImportsProgressState(LoadingImportsState.STATE_LOADING_IMPORTS);
            super.traverse(call.getInternalServiceProcess());
            getKernelQedeqBo().setLoadedImports(required);
        } catch (final SourceFileExceptionList sfl) {
            getKernelQedeqBo().setLoadingImportsFailureState(
                LoadingImportsState.STATE_LOADING_IMPORTS_FAILED, sfl);
        }
        return required;
    }

    /**
     * Get list of directly referenced modules.
     *
     * @return  List of directly required modules.
     */
    public KernelModuleReferenceList getRequired() {
        return required;
    }

    /**
     * Visit import. Loads referenced QEDEQ module and saves reference.
     *
     * @param   imp                 Begin visit of this element.
     * @throws  ModuleDataException Major problem occurred.
     */
    public void visitEnter(final Import imp) throws ModuleDataException {
        final ModuleContext context = getCurrentContext();
        context.setLocationWithinModule(context.getLocationWithinModule() + ".getLabel()");
        try {
            final KernelQedeqBo propNew = getKernelQedeqBo().getKernelServices().loadKernelModule(
                getInternalServiceCall().getInternalServiceProcess(),
                getKernelQedeqBo().getModuleAddress(), imp.getSpecification());
            getRequired().addLabelUnique(context, imp.getLabel(), propNew);
            Trace.param(CLASS, "visitEnter(Import)", "adding context", getCurrentContext());
        } catch (SourceFileExceptionList e) {
            final ModuleDataException me = new LoadRequiredModuleException(e.get(0).getErrorCode(),
                "import of module with label \"" + imp.getLabel() + "\" failed: "
                + e.get(0).getMessage(), context);
            // TODO mime 20080227: also include reference area in sf creation?
            addError(me);
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
