/* $Id: Xml2Xml.java,v 1.1 2007/12/21 23:33:48 m31 Exp $
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

package org.qedeq.kernel.bo.save;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import org.qedeq.kernel.bo.module.ModuleAddress;
import org.qedeq.kernel.bo.module.ModuleDataException;
import org.qedeq.kernel.bo.module.ModuleProperties;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.context.KernelContext;
import org.qedeq.kernel.latex.Qedeq2Xml;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.utility.IoUtility;
import org.qedeq.kernel.utility.TextOutput;
import org.qedeq.kernel.xml.mapper.ModuleDataException2XmlFileException;
import org.qedeq.kernel.xml.parser.DefaultSourceFileExceptionList;


/**
 * Test application.
 *
 * @version $Revision: 1.1 $
 * @author    Michael Meyling
 */
public final class Xml2Xml  {

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
                if (xml.toLowerCase().endsWith(".xml")) {
                    xml = xml.substring(0, xml.length() - 4);
                }
                destination = new File(from.getParentFile(), xml + "_.xml").getCanonicalFile();
            }
        } catch (IOException e) {
            Trace.trace(CLASS, method, e);
            throw new DefaultSourceFileExceptionList(e);
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
            final ModuleProperties prop = KernelContext.getInstance().loadModule(address);
            IoUtility.createNecessaryDirectories(to);
            final OutputStream outputStream = new FileOutputStream(to);
            printer = new TextOutput(to.getName(), outputStream);
            try {
                Qedeq2Xml.print(address, prop.getModule(), printer);
            } catch (ModuleDataException e) {
                Trace.trace(CLASS, method, e);
                Trace.param(CLASS, method, "context", e.getContext());
                throw ModuleDataException2XmlFileException.createXmlFileExceptionList(e,
                    prop.getModule().getQedeq());
            }
            return to.getCanonicalPath();
        } catch (IOException e) {
            Trace.trace(CLASS, method, e);
            throw new DefaultSourceFileExceptionList(e);
        } catch (RuntimeException e) {
            Trace.trace(CLASS, method, e);
            throw new DefaultSourceFileExceptionList(e);
        } finally {
            if (printer != null) {
                printer.close();
            }
            Trace.end(CLASS, method);
        }
    }

}
