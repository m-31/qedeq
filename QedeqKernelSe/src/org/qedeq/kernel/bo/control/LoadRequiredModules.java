/* $Id: Qedeq2Latex.java,v 1.47 2007/05/10 00:37:53 m31 Exp $
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
import org.qedeq.kernel.bo.module.LoadingState;
import org.qedeq.kernel.bo.module.ModuleDataException;
import org.qedeq.kernel.bo.module.ModuleProperties;
import org.qedeq.kernel.bo.module.ModuleReferenceList;
import org.qedeq.kernel.bo.module.QedeqBo;
import org.qedeq.kernel.bo.visitor.AbstractModuleVisitor;
import org.qedeq.kernel.bo.visitor.QedeqNotNullTransverser;
import org.qedeq.kernel.common.XmlFileExceptionList;
import org.qedeq.kernel.context.KernelContext;
import org.qedeq.kernel.log.ModuleEventLog;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.xml.mapper.ModuleDataException2XmlFileException;
import org.qedeq.kernel.xml.parser.DefaultXmlFileExceptionList;


/**
 * Load all required QEDEQ modules.
 *
 * @version $Revision: 1.47 $
 * @author  Michael Meyling
 */
public final class LoadRequiredModules extends AbstractModuleVisitor {

    /** Transverse QEDEQ module with this transverser. */
    private final QedeqNotNullTransverser transverser;

    /** QEDEQ BO object to work on. */
    private final QedeqBo qedeq;

    /** List of required QEDEQ modules. */
    private final ModuleReferenceList required;

    /**
     * Constructor.
     *
     * @param   qedeq           QEDEQ BO object.
     */
    private LoadRequiredModules(final QedeqBo qedeq) {
        this.qedeq = qedeq;
        this.transverser = new QedeqNotNullTransverser(
            KernelContext.getInstance().getLocalFilePath(qedeq.getModuleAddress()), this);
        required = new ModuleReferenceList();
    }

    /**
     * Load all required QEDEQ modules for a given QEDEQ module.
     *
     * @param   qedeqBo     Basic QEDEQ module object.
     * @throws  ModuleDataException Major problem occurred.
     */
    public static void loadRequired(final QedeqBo qedeqBo)
            throws ModuleDataException {
        final String method = "loadRequired(QedeqBo)";
        final ModuleProperties prop = KernelContext.getInstance().getModuleProperties(
            qedeqBo.getModuleAddress().getAddress());   // TODO mime 20071026: this is no good code!
        prop.setLoadingProgressState(LoadingState.STATE_LOADING_REQUIRED_MODULES);
        final LoadRequiredModules converter = new LoadRequiredModules(qedeqBo);
        try {
            converter.loadRequired();
            prop.setLoadedRequiredModules(converter.required);
        } catch (ModuleDataException e) {
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_REQUIRED_MODULES_FAILED,
                ModuleDataException2XmlFileException.createXmlFileExceptionList(e,
                    qedeqBo.getQedeq()));
            throw e;
        } catch (final RuntimeException e) {
            Trace.fatal(LoadRequiredModules.class, method, "programming error", e);
            ModuleDataException me = new LoadRequiredModuleException(10, e.toString(),
                converter.transverser.getCurrentContext());
            final XmlFileExceptionList xl =
                new DefaultXmlFileExceptionList(e);
            prop.setLoadingFailureState(
                LoadingState.STATE_LOADING_REQUIRED_MODULES_FAILED, xl);
            ModuleEventLog.getInstance().stateChanged(prop);
            throw me;
        } catch (final Throwable e) {
            ModuleDataException me = new LoadRequiredModuleException(10, e.toString(),
                converter.transverser.getCurrentContext());
            final XmlFileExceptionList xl =
                new DefaultXmlFileExceptionList(e);
            prop.setLoadingFailureState(
                LoadingState.STATE_LOADING_REQUIRED_MODULES_FAILED, xl);
            ModuleEventLog.getInstance().stateChanged(prop);
            throw me;
        }
    }

    /**
     * Load all required QEDEQ modules for a given QEDEQ module.
     *
     * @throws  ModuleDataException Exception during transversion.
     */
    private final void loadRequired() throws ModuleDataException {
        transverser.accept(qedeq.getQedeq());
    }

    public void visitEnter(final Import imp) throws ModuleDataException {
        try {
            final QedeqBo other = KernelContext.getInstance().loadModule(qedeq,
                imp.getSpecification());
            required.add(transverser.getCurrentContext(), imp.getLabel(),
                other.getModuleAddress().getURL());
            loadRequired(other);
        } catch (XmlFileExceptionList e) {
            throw new LoadRequiredModuleException(e.get(0).getErrorCode(), e.get(0).getMessage(),
                transverser.getCurrentContext());
        }
    }

    public void visitLeave(final ImportList imports) {
        transverser.setBlocked(true);
    }

}
