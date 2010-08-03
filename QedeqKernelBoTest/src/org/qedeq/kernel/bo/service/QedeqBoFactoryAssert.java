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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.xml.parsers.ParserConfigurationException;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.test.DynamicGetter;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.bo.module.InternalKernelServices;
import org.qedeq.kernel.bo.module.QedeqFileDao;
import org.qedeq.kernel.bo.service.latex.QedeqBoDuplicateLanguageChecker;
import org.qedeq.kernel.bo.test.KernelFacade;
import org.qedeq.kernel.common.DummyPlugin;
import org.qedeq.kernel.common.LoadingState;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.Plugin;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.dto.module.QedeqVo;
import org.qedeq.kernel.xml.dao.XmlQedeqFileDao;
import org.qedeq.kernel.xml.mapper.Context2SimpleXPath;
import org.qedeq.kernel.xml.tracker.SimpleXPath;
import org.qedeq.kernel.xml.tracker.XPathLocationParser;
import org.xml.sax.SAXException;

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
     * @throws  ModuleDataException  Semantic or syntactic error occurred.
     */
    public static void createQedeq(final DefaultKernelQedeqBo prop,
            final Qedeq original) throws SourceFileExceptionList {
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
        final InternalKernelServices services = (InternalKernelServices) IoUtility
            .getFieldContent(KernelFacade.getKernelContext(), "services");
        final QedeqFileDao loader = new XmlQedeqFileDao();
        loader.setServices(services);
        prop.setQedeqFileDao(loader);
        prop.setQedeqVo(vo);
        prop.setLoaded(vo, new ModuleLabelsCreator(DummyPlugin.getInstance(), prop).createLabels());
        KernelFacade.getKernelContext().loadRequiredModules(prop.getModuleAddress());
        KernelFacade.getKernelContext().checkModule(prop.getModuleAddress());
        if (!prop.isChecked()) {
            throw prop.getErrors();
        }
        QedeqBoDuplicateLanguageChecker.check(new Plugin() {
                public String getPluginId() {
                    return QedeqBoDuplicateLanguageChecker.class.getName();
                }
    
                public String getPluginName() {
                    return "duplicate language checker";
                }
    
                public String getPluginDescription() {
                    return "Test for duplicate language entries within LaTeX sections";
                }
    
            }, prop);
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
        try {
            final InternalKernelServices services = (InternalKernelServices) IoUtility
                .getFieldContent(KernelFacade.getKernelContext(), "services");
            final SimpleXPath find = XPathLocationParser.getXPathLocation(
                services.getLocalFilePath(
                    getCurrentContext().getModuleLocation()), xpath);
            if (find.getStartLocation() == null) {
                System.out.println(getCurrentContext());
                throw new RuntimeException("start not found: " + find + "\ncontext: "
                    + getCurrentContext().getLocationWithinModule());
            }
            if (find.getEndLocation() == null) {
                System.out.println(getCurrentContext());
                throw new RuntimeException("end not found: " + find + "\ncontext: "
                    + getCurrentContext().getLocationWithinModule());
            }

        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
