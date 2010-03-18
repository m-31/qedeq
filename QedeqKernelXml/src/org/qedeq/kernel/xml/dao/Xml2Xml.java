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

package org.qedeq.kernel.xml.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Locale;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.io.TextOutput;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.context.KernelContext;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.Plugin;
import org.qedeq.kernel.common.SourceFileExceptionList;


/**
 * Test application.
 *
 * @author  Michael Meyling
 */
public final class Xml2Xml implements Plugin {

    /** This class. */
    private static final Class CLASS = Xml2Xml.class;

    /** This instance. */
    private static final Xml2Xml INSTANCE = new Xml2Xml();

    /**
     * Constructor.
     */
    private Xml2Xml() {
        // nothing to do
    }

    /**
     * Generate XML file out of XML file.
     *
     * @param   from            Read this XML file.
     * @param   to              Write to this file. Could be <code>null</code>.
     * @throws  SourceFileExceptionList    Module could not be successfully loaded.
     * @return  File name of generated LaTeX file.
     */
    public static String generate(final File from, final File to)
            throws SourceFileExceptionList {
        final String method = "generate(File, File)";
        File destination;
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
        } catch (IOException e) {
            Trace.trace(CLASS, method, e);
            throw new DefaultSourceFileExceptionList(INSTANCE, e);
        }
        return generate(IoUtility.toUrl(from), destination);
    }

    /**
     * Generate LaTeX file out of XML file.
     *
     * @param   from            Read this XML file.
     * @param   to              Write to this file. Could not be <code>null</code>.
     * @throws  SourceFileExceptionList    Module could not be successfully loaded.
     * @return  File name of generated LaTeX file.
     */
    public static String generate(final URL from, final File to)
            throws SourceFileExceptionList {
        final String method = "generate(URL, File)";
        Trace.begin(CLASS, method);
        Trace.param(CLASS, method, "from", from);
        Trace.param(CLASS, method, "to", to);
        TextOutput printer = null;
        try {
            final ModuleAddress address = KernelContext.getInstance().getModuleAddress(from);
            // TODO mime 20080303: find a solution without casting!
            final KernelQedeqBo prop = (KernelQedeqBo) KernelContext.getInstance()
                .loadModule(address);
            if (prop.getLoadingState().isFailure()) {
                throw prop.getErrors();
            }
            IoUtility.createNecessaryDirectories(to);
            final OutputStream outputStream = new FileOutputStream(to);
            printer = new TextOutput(to.getName(), outputStream);
            Qedeq2Xml.print(prop, printer);
            return to.getCanonicalPath();
        } catch (IOException e) {
            Trace.trace(CLASS, method, e);
            throw new DefaultSourceFileExceptionList(INSTANCE, e);
        } catch (RuntimeException e) {
            Trace.trace(CLASS, method, e);
            throw new DefaultSourceFileExceptionList(INSTANCE, e);
        } finally {
            if (printer != null) {
                printer.close();
            }
            Trace.end(CLASS, method);
        }
    }

    public String getPluginDescription() {
        return "transform XML QEDEQ module in a new XML file";
    }

    public String getPluginName() {
        return "Xml2Xml";
    }

}
