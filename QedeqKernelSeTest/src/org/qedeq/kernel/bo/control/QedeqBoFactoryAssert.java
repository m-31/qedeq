/* $Id: QedeqBoFactoryAssert.java,v 1.12 2007/12/21 23:35:17 m31 Exp $
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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.bo.load.DefaultModuleAddress;
import org.qedeq.kernel.bo.load.DefaultQedeqBo;
import org.qedeq.kernel.bo.load.QedeqBoFactory;
import org.qedeq.kernel.bo.module.ModuleDataException;
import org.qedeq.kernel.rel.test.text.KernelFacade;
import org.qedeq.kernel.test.DynamicGetter;
import org.qedeq.kernel.xml.mapper.Context2SimpleXPath;
import org.qedeq.kernel.xml.tracker.SimpleXPath;
import org.qedeq.kernel.xml.tracker.XPathLocationFinder;
import org.xml.sax.SAXException;

/**
 * For testing QEDEQ BO generation.
 *
 * @version $Revision: 1.12 $
 * @author Michael Meyling
 */
public class QedeqBoFactoryAssert extends QedeqBoFactory {

    /**
     * Constructor.
     * 
     * @param   globalContext     Module location information.
     */
    public QedeqBoFactoryAssert(final URL globalContext) {
        super(globalContext);
    }

    /**
     * Create {@link QedeqBo} out of an {@link Qedeq} instance.
     * During that procedure some basic checking is done. E.g. the uniqueness of entries
     * is tested. The resulting business object has no references to the original
     * {@link Qedeq} instance.
     * <p>
     * During the creation process the caller must assert that no modifications are made
     * to the {@link Qedeq} instance including its referenced objects.
     *
     * @param   globalContext   Module location information.
     * @param   original        Basic qedeq module object.
     * @return  Filled QEDEQ business object. Is equal to the parameter <code>qedeq</code>.
     * @throws  ModuleDataException  Semantic or syntactic error occurred.
     */
    public static DefaultQedeqBo createQedeq(final URL globalContext, final Qedeq original)
            throws ModuleDataException {
        final QedeqBoFactoryAssert creator = new QedeqBoFactoryAssert(globalContext);
        final DefaultQedeqBo bo = creator.create(original);
        try {
            bo.setModuleAddress(new DefaultModuleAddress(globalContext));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        QedeqBoFormalLogicChecker.check(globalContext, bo); // TODO mime 20061105: just for testing
        QedeqBoDuplicateLanguageChecker.check(globalContext, bo); // TODO mime 20070301: just for testing
        return bo;
    }

    /**
     * Set location information where are we within the module.
     *
     * @param   locationWithinModule    Location within module.
     */
    protected void setLocationWithinModule(final String locationWithinModule) {
        getCurrentContext().setLocationWithinModule(locationWithinModule);

        try {
            System.out.println("###> "  + getCurrentContext().getLocationWithinModule());
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
        System.out.println("###< " + xpath);
        try {
            final SimpleXPath find = XPathLocationFinder.getXPathLocation(
                new File(KernelFacade.getKernelContext().getLocalFilePath(
                new DefaultModuleAddress(getCurrentContext().getModuleLocation()))), xpath,
                getCurrentContext().getModuleLocation());
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
