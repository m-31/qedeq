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

package org.qedeq.kernel.bo.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.qedeq.base.io.Parameters;
import org.qedeq.base.io.TextOutput;
import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.YodaUtility;
import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.bo.job.ServiceCallImpl;
import org.qedeq.kernel.bo.module.InternalKernelServices;
import org.qedeq.kernel.bo.module.InternalModuleServiceCall;
import org.qedeq.kernel.bo.module.InternalServiceJob;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.service.internal.DefaultInternalKernelServices;
import org.qedeq.kernel.bo.service.internal.DefaultKernelQedeqBo;
import org.qedeq.kernel.bo.service.internal.ServiceProcessManager;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.visitor.InterruptException;
import org.qedeq.kernel.xml.dao.Qedeq2Xml;

/**
 * Test generating LaTeX files for all known samples.
 *
 * @author  Michael Meyling
 */
public abstract class QedeqBoTestCase extends QedeqTestCase {

    /** Here should the result get into. */
    private File genDir;

    /** Here are the documents within. */
    private File docDir;

    public QedeqBoTestCase() {
        super();
    }

    public QedeqBoTestCase(final String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        docDir = new File("../QedeqDoc");
        genDir = new File("../../qedeq_gen");
        // test if we are in the normal development environment, where a project with name
        // "../QedeqDoc" exists, otherwise we assume to run within the release directory
        // structure where the docs are in the directory ../doc
        if (!docDir.exists()) {
            docDir = getFile("doc");
            // or are we testing automatically?
            if (!docDir.exists()) {
                throw new IOException("unknown source directory for QEDEQ modules");
            }
            genDir = new File(getOutdir(), "doc");
        }
        KernelFacade.startup();
        Trace.setTraceOn(false);    // disable trace because it leads to java heap space errors
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        KernelFacade.shutdown();
    }

    public File getGenDir() {
        return genDir;
    }

    public File getDocDir() {
        return docDir;
    }

    /**
     * Get test input data file. Get file relative to {@link #getDocDir()}.
     *
     * @param   fileName    Relative file path.
     * @return  Test data file.
     */
    public File getDocFile(final String fileName) {
        return new File(getDocDir(), fileName);
    }

    public InternalKernelServices getInternalServices() {
        final KernelContext c = KernelFacade.getKernelContext();
        try {
            return (InternalKernelServices) YodaUtility.getFieldValue(c, "services");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public DefaultInternalKernelServices getServices() {
        final KernelContext c = KernelFacade.getKernelContext();
        try {
            return (DefaultInternalKernelServices) YodaUtility.getFieldValue(c, "services");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public InternalModuleServiceCall createServiceCall(final String name, final KernelQedeqBo prop)
            throws InterruptException {
        final InternalServiceJob process = getInternalServices().createServiceProcess(name);
        final ServiceCallImpl call = new ServiceCallImpl(DummyPlugin.getInstance(), prop, Parameters.EMPTY, Parameters.EMPTY, process, null);
        return call;
    }

    public void endServiceCall(final InternalModuleServiceCall call) {
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

    public void printlnXml(final Element element) throws ModuleDataException {
        final KernelQedeqBo prop = new DefaultKernelQedeqBo(null, DefaultModuleAddress.MEMORY);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final TextOutput output = new TextOutput("out", outputStream, "UTF-8");
        final Qedeq2Xml visitor = new Qedeq2Xml(null, prop, output);
        visitor.getTraverser().accept(element);
        try {
            System.out.println(outputStream.toString("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // should never happen
            throw new RuntimeException(e);
        }
    }


}
