/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2013,  Michael Meyling <mime@qedeq.org>.
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

import java.lang.reflect.InvocationTargetException;

import org.qedeq.base.io.Parameters;
import org.qedeq.base.io.SourceArea;
import org.qedeq.base.test.DynamicGetter;
import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.YodaUtility;
import org.qedeq.kernel.bo.module.InternalKernelServices;
import org.qedeq.kernel.bo.module.InternalServiceProcess;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.QedeqFileDao;
import org.qedeq.kernel.bo.service.common.InternalServiceCall;
import org.qedeq.kernel.bo.service.latex.QedeqBoDuplicateLanguageChecker;
import org.qedeq.kernel.bo.test.DummyPlugin;
import org.qedeq.kernel.bo.test.KernelFacade;
import org.qedeq.kernel.se.base.module.Qedeq;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.dto.module.QedeqVo;
import org.qedeq.kernel.se.state.LoadingState;
import org.qedeq.kernel.se.visitor.InterruptException;
import org.qedeq.kernel.xml.dao.XmlQedeqFileDao;
import org.qedeq.kernel.xml.mapper.Context2SimpleXPath;
import org.qedeq.kernel.xml.tracker.SimpleXPath;
import org.qedeq.kernel.xml.tracker.XPathLocationParser;

/**
 * For testing QEDEQ BO generation.
 *
 * @author Michael Meyling
 */
public class QedeqBoFactoryAssert extends QedeqVoBuilder {

    /** This class. */
    private static final Class CLASS = QedeqBoFactoryAssert.class;

    /**
     * Constructor.
     *
     * @param   address     QEDEQ module address.
     */
    public QedeqBoFactoryAssert(final ModuleAddress address) {
        super(address);
    }

    public static DefaultInternalKernelServices getInternalServices() {
        try {
            return (DefaultInternalKernelServices) YodaUtility.getFieldValue(
                KernelFacade.getKernelContext(), "services");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static InternalServiceCall createServiceCall(final String name, final KernelQedeqBo prop)
            throws InterruptException {
        InternalServiceProcess process = getInternalServices().createServiceProcess(name);
        InternalServiceCall call = getInternalServices().createServiceCall(DummyPlugin.getInstance(), prop,
            Parameters.EMPTY, Parameters.EMPTY, process, null);
        return call;
    }
 
    public static void endServiceCall(final InternalServiceCall call) {
        if (call == null) {
            return;
        }
        try {
            ((ServiceProcessManager) YodaUtility.getFieldValue(getInternalServices(), "processManager"))
                .endServiceCall(call);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Create {@link QedeqBo} out of an {@link Qedeq} instance.
     * During that procedure some basic checking is done. E.g. the uniqueness of entries
     * is tested.
     * The resulting business object has no references to the original
     * {@link Qedeq} instance.
     * <p>
     * During the creation process the caller must assert that no modifications are made
     * to the {@link Qedeq} instance including its referenced objects.
     *
     * @param   prop            Module informations.
     * @param   original        Basic qedeq module object.
     */
    public static void createQedeq(final DefaultKernelQedeqBo prop,
            final Qedeq original) throws SourceFileExceptionList, InterruptException {
        final QedeqBoFactoryAssert creator = new QedeqBoFactoryAssert(prop.getModuleAddress());
        final QedeqVo vo;
        try {
            vo = creator.create(original);
        } catch (ModuleDataException e) {
            final SourceFileExceptionList xl
                = prop.createSourceFileExceptionList(DummyPlugin.getInstance(), e, original);
            prop.setLoadingFailureState(LoadingState.STATE_LOADING_INTO_MEMORY_FAILED, xl);
            throw xl;
        }
        final QedeqFileDao loader = new XmlQedeqFileDao();
        loader.setServices(getInternalServices());
        prop.setQedeqFileDao(loader);
        prop.setQedeqVo(vo);
        final ModuleLabelsCreator mc = new ModuleLabelsCreator(DummyPlugin.getInstance(),
            prop);
        InternalServiceCall call = null;
        try {
            call = createServiceCall("createQedeq", prop);
            mc.createLabels(call);
        } finally {
            endServiceCall(call);
        }
        prop.setLoaded(vo, mc.getLabels(), mc.getConverter(), mc.getTextConverter());
        KernelFacade.getKernelContext().loadRequiredModules(prop.getModuleAddress());
        KernelFacade.getKernelContext().checkWellFormedness(prop.getModuleAddress());
        if (!prop.isWellFormed()) {
            throw prop.getErrors();
        }
        QedeqBoDuplicateLanguageChecker.check(call);
    }

    /**
     * Set location information where we are within the module.
     *
     * @param   locationWithinModule    Location within module.
     */
    protected void setLocationWithinModule(final String locationWithinModule) {
        Trace.param(CLASS, "setLocationWithinModule(String)",
            "locationWithinModule > ", locationWithinModule);
        getCurrentContext().setLocationWithinModule(locationWithinModule);

        try {
            DynamicGetter.get(getQedeqOriginal(), getCurrentContext().getLocationWithinModule());
        } catch (RuntimeException e) {
            System.err.println(getCurrentContext().getLocationWithinModule());
            throw e;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        SimpleXPath xpath;
        try {
            xpath = Context2SimpleXPath.getXPath(getCurrentContext(), getQedeqOriginal());
        } catch (ModuleDataException e) {
            throw new RuntimeException(e);
        }
        Trace.param(CLASS, "setLocationWithinModule(String)",
            "xpath                < ", xpath);
        InternalKernelServices services;
        try {
            services = (InternalKernelServices) YodaUtility
                .getFieldValue(KernelFacade.getKernelContext(), "services");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        final SourceArea find = XPathLocationParser.findSourceArea(
            services.getLocalFilePath(
            getCurrentContext().getModuleLocation()), xpath);
        if (find.getStartPosition() == null) {
            System.out.println(getCurrentContext());
            throw new RuntimeException("start not found: " + find + "\ncontext: "
                + getCurrentContext().getLocationWithinModule());
        }
        if (find.getEndPosition() == null) {
            System.out.println(getCurrentContext());
            throw new RuntimeException("end not found: " + find + "\ncontext: "
                + getCurrentContext().getLocationWithinModule());
        }
    }
}
