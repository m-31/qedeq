/* $Id: Xml2Xml.java,v 1.1 2007/10/07 16:43:10 m31 Exp $
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

import org.qedeq.kernel.bo.module.ModuleDataException;
import org.qedeq.kernel.bo.module.QedeqBo;
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
    public static String generate(final String from, final String to)
            throws SourceFileExceptionList {
        return generate((from != null ? new File(from) : null), (to != null ? new File(to) : null));
    }

    /**
     * Generate LaTeX file out of XML file. Also initializes trace file.
     *
     * @param   from            Read this XML file.
     * @param   to              Write to this file. Could be <code>null</code>.
     * @throws  SourceFileExceptionList    Module could not be successfully loaded.
     * @return  File name of generated LaTeX file.
     */
    public static String generate(final File from, final File to)
            throws SourceFileExceptionList {
        final String method = "generate(String, String, String, String)";
        File destination = null;
        File source = null;
        QedeqBo qedeqBo = null;
        try {
            Trace.begin(Xml2Xml.class, method);
            Trace.param(Xml2Xml.class, method, "from", from);
            Trace.param(Xml2Xml.class, method, "to", to);
            source = from;
            if (to != null) {
                destination = to.getCanonicalFile();
            } else {
                String xml = source.getName();
                if (xml.toLowerCase().endsWith(".xml")) {
                    xml = xml.substring(0, xml.length() - 4);
                }
                destination = new File(source.getParentFile(), xml + "_.xml").getCanonicalFile();
            }
        } catch (IOException e) {
            Trace.trace(Xml2Xml.class, method, e);
            throw new DefaultSourceFileExceptionList(e);
        }
        TextOutput printer = null;
        try {
            qedeqBo = KernelContext.getInstance().loadModule(
                IoUtility.toUrl(source.getCanonicalFile()));

            IoUtility.createNecessaryDirectories(destination);
            final OutputStream outputStream = new FileOutputStream(destination);
            printer = new TextOutput(destination.getName(), outputStream);
            Qedeq2Xml.print(source.getCanonicalPath(), qedeqBo, printer);
            return destination.getCanonicalPath();
        } catch (IOException e) {
            Trace.trace(Xml2Xml.class, method, e);
            throw new DefaultSourceFileExceptionList(e);
        } catch (RuntimeException e) {
            Trace.trace(Xml2Xml.class, method, e);
            throw new DefaultSourceFileExceptionList(e);
        } catch (ModuleDataException e) {
            Trace.trace(Xml2Xml.class, method, e);
            Trace.param(Xml2Xml.class, method, "context", e.getContext());
            throw ModuleDataException2XmlFileException.createXmlFileExceptionList(e,
                qedeqBo.getQedeq());
        } finally {
            if (printer != null) {
                printer.close();
            }
            Trace.end(Xml2Xml.class, method);
        }
    }

}
