/* $Id: QedeqBoFactoryAssert.java,v 1.13 2008/01/26 12:39:50 m31 Exp $
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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.xml.parsers.ParserConfigurationException;

import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.bo.load.QedeqVoBuilder;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.dto.module.QedeqVo;
import org.qedeq.kernel.rel.test.text.KernelFacade;
import org.qedeq.kernel.test.DynamicGetter;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.xml.mapper.Context2SimpleXPath;
import org.qedeq.kernel.xml.tracker.SimpleXPath;
import org.qedeq.kernel.xml.tracker.XPathLocationParser;
import org.xml.sax.SAXException;

/**
 * For testing QEDEQ BO generation.
 *
 * @version $Revision: 1.13 $
 * @author Michael Meyling
 */
public class QedeqBoFactoryAssert extends QedeqVoBuilder {

    /** This class. */
    private static final Class CLASS = QedeqBoFactoryAssert.class;

    /**
     * Constructor.
     * 
     * @param   prop     QEDEDQ BO.
     */
    public QedeqBoFactoryAssert(final DefaultModuleProperties prop) {
        super(prop);
    }

    /**
     * Create {@link QedeqBo} out of an {@link Qedeq} instance.
     * During that procedure some basic checking is done. E.g. the uniqueness of entries
     * is tested. Also the logical correctness is checked. 
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
    public static void createQedeq(final DefaultModuleProperties prop,
            final Qedeq original) throws ModuleDataException {
        final QedeqBoFactoryAssert creator = new QedeqBoFactoryAssert(prop);
        final QedeqVo qedeq = creator.create(original);
        prop.setLoaded(qedeq, creator.getLabels());
        try {
            KernelFacade.getKernelContext().loadRequiredModules(prop.getModuleAddress());
        } catch (SourceFileExceptionList sfel) {
            throw (ModuleDataException) prop.getException().get(0).getCause(); // TODO mime 20080217 ok???
        }
        KernelFacade.getKernelContext().checkModule(prop.getModuleAddress());
        if (!prop.isChecked()) {
            throw (ModuleDataException) prop.getException().get(0).getCause();
        }
        QedeqBoDuplicateLanguageChecker.check(prop);
    }

    /**
     * Set location information where are we within the module.
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
            final SimpleXPath find = XPathLocationParser.getXPathLocation(
                KernelFacade.getKernelContext().getLocalFilePath(
                    getCurrentContext().getModuleLocation()), xpath,
                getCurrentContext().getModuleLocation().getURL());
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
