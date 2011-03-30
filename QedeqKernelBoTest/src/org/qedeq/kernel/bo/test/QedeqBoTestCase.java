/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
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

import java.io.File;
import java.io.IOException;

import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.base.utility.YodaUtility;
import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.bo.module.InternalKernelServices;

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
        genDir = new File("../../../qedeq_gen");
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

    public InternalKernelServices getServices() {
        final KernelContext c = KernelFacade.getKernelContext();
        try {
            return (InternalKernelServices) YodaUtility.getFieldValue(c, "services");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
