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

package org.qedeq.kernel.xml.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Locale;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.io.Parameters;
import org.qedeq.base.io.TextOutput;
import org.qedeq.base.io.UrlUtility;
import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.YodaUtility;
import org.qedeq.kernel.bo.common.KernelServices;
import org.qedeq.kernel.bo.job.InternalModuleServiceCallImpl;
import org.qedeq.kernel.bo.module.InternalKernelServices;
import org.qedeq.kernel.bo.module.InternalServiceJob;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.service.internal.ServiceProcessManager;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.ModuleService;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.visitor.InterruptException;


/**
 * Test application.
 *
 * @author  Michael Meyling
 */
public final class Xml2Xml implements ModuleService {

    /** This class. */
    private static final Class CLASS = Xml2Xml.class;

    /**
     * Constructor.
     */
    private Xml2Xml() {
        // nothing to do
    }

    /**
     * Generate XML file out of XML file.
     *
     * @param   services        Use this kernel services.
     * @param   internal        Use this internal kernel services.
     * @param   from            Read this XML file.
     * @param   to              Write to this file. Could be <code>null</code>.
     * @throws  SourceFileExceptionList     Module could not be successfully loaded.
     * @throws  InterruptException          User canceled process.
     * @return  File name of generated LaTeX file.
     */
    public static String generate(final KernelServices services, final InternalKernelServices internal,
            final File from, final File to) throws SourceFileExceptionList, InterruptException {
        final String method = "generate(File, File)";
        File destination = null;
        try {
            if (to != null) {
                destination = to.getCanonicalFile();
            } else {
                String xml = from.getName();
                if (xml.toLowerCase(Locale.US).endsWith(".xml")) {
                    xml = xml.substring(0, xml.length() - 4);
                }
                destination = new File(from.getParentFile(), xml + "_.xml").getCanonicalFile();
            }
            return generate(services, internal, UrlUtility.toUrl(from), destination);
        } catch (IOException e) {
            Trace.fatal(CLASS, "Writing failed destionation", method, e);
            throw internal.createSourceFileExceptionList(
                DaoErrors.WRITING_MODULE_FILE_FAILED_CODE,
                DaoErrors.WRITING_MODULE_FILE_FAILED_TEXT + destination,
                to + "", e);
        }
    }

    /**
     * Generate XML file out of XML file.
     *
     * @param   services        Here we get our kernel services.
     * @param   internal        Our internal kernel services.
     * @param   from            Read this XML file.
     * @param   to              Write to this file. Could not be <code>null</code>.
     * @throws  SourceFileExceptionList     Module could not be successfully loaded.
     * @throws  IOException                 Writing (or reading) failed.
     * @throws  InterruptException          Generation canceled by user.
     * @return  File name of generated LaTeX file.
     */
    private static String generate(final KernelServices services, final InternalKernelServices internal,
            final URL from, final File to)
            throws SourceFileExceptionList, IOException, InterruptException {
        final String method = "generate(URL, File)";
        Trace.begin(CLASS, method);
        Trace.param(CLASS, method, "from", from);
        Trace.param(CLASS, method, "to", to);
        TextOutput printer = null;
        try {
            final ModuleAddress address = services.getModuleAddress(from);
            // if this class returns into production code you must get rid of the following:
            final InternalServiceJob process;
            try {
                process = ((ServiceProcessManager) YodaUtility.getFieldValue(internal, "processManager"))
                    .createServiceProcess("generate XML");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            final ModuleService plugin = new Xml2Xml();
            final KernelQedeqBo prop = internal.loadKernelModule(process, address);
            if (!prop.isLoaded()) {
                throw prop.getErrors();
            }
            IoUtility.createNecessaryDirectories(to);
            final OutputStream outputStream = new FileOutputStream(to);
            printer = new TextOutput(to.getName(), outputStream, "UTF-8");
            final InternalModuleServiceCallImpl call = new InternalModuleServiceCallImpl(plugin, prop, Parameters.EMPTY, Parameters.EMPTY, process, null);
            Qedeq2Xml.print(call.getInternalServiceProcess(), plugin, prop, printer);
            return to.getCanonicalPath();
        } finally {
            if (printer != null) {
                printer.close();
            }
            Trace.end(CLASS, method);
        }
    }

    public String getServiceId() {
        return CLASS.getName();
    }
    public String getServiceAction() {
        return "Xml2Xml";
    }
    public String getServiceDescription() {
        return "transform XML QEDEQ module in a new XML file";
    }

}
