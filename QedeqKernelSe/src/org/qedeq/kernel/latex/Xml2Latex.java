/* $Id: Xml2Latex.java,v 1.11 2007/12/21 23:33:48 m31 Exp $
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

package org.qedeq.kernel.latex;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.qedeq.kernel.bo.module.ModuleDataException;
import org.qedeq.kernel.bo.module.ModuleProperties;
import org.qedeq.kernel.bo.module.QedeqBo;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.context.KernelContext;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.utility.IoUtility;
import org.qedeq.kernel.utility.TextOutput;
import org.qedeq.kernel.xml.mapper.ModuleDataException2XmlFileException;
import org.qedeq.kernel.xml.parser.DefaultSourceFileExceptionList;


/**
 * TODO mime 20070704: this content must be called by the KernelContext!
 *
 * @version $Revision: 1.11 $
 * @author  Michael Meyling
 */
public final class Xml2Latex  {

    /**
     * Constructor.
     */
    private Xml2Latex() {
        // nothing to do
    }

    /**
     * Generate LaTeX file out of XML file.
     *
     * @param   prop            Take this QEDEQ module.
     * @param   to              Write to this file. Could be <code>null</code>.
     * @param   language        Resulting language. Could be <code>null</code>.
     * @param   level           Resulting detail level. Could be <code>null</code>.
     * @return  File name of generated LaTeX file.
     * @throws  SourceFileExceptionList    Something went wrong.
     */
    public static String generate(final ModuleProperties prop, final File to, final String language,
            final String level) throws SourceFileExceptionList {
        final String method = "generate(String, String, String, String)";
        File destination = null;
        QedeqBo qedeqBo = null;
        try {
            Trace.begin(Xml2Latex.class, method);
            Trace.param(Xml2Latex.class, method, "prop", prop);
            Trace.param(Xml2Latex.class, method, "to", to);
            Trace.param(Xml2Latex.class, method, "language", language);
            Trace.param(Xml2Latex.class, method, "level", level);
            if (to == null) {
                String tex = prop.getModuleAddress().localizeInFileSystem(prop.getModuleAddress()
                    .getURL());
                if (tex.toLowerCase().endsWith(".xml")) {
                    tex = tex.substring(0, tex.length() - 4);
                }
                if (language != null && language.length() > 0) {
                    tex = tex + "_" + language;
                }
                // the destination is the configured destination directory plus the (relative)
                // localized file (or path) name
                destination = new File(KernelContext.getInstance().getConfig()
                    .getGenerationDirectory(), tex + ".tex").getCanonicalFile();
            } else {
                destination = to;
            }
        } catch (IOException e) {
            Trace.trace(Xml2Latex.class, method, e);
            throw new DefaultSourceFileExceptionList(e);
        }
        TextOutput printer = null;
        try {
            qedeqBo = prop.getModule();
            IoUtility.createNecessaryDirectories(destination);
            final OutputStream outputStream = new FileOutputStream(destination);
            printer = new TextOutput(destination.getName(), outputStream);

            // System.out.println(simple.getQedeq().toString());
            Qedeq2Latex.print(prop.getUrl(), qedeqBo, printer,
                language, level);
            return destination.getCanonicalPath();
        } catch (IOException e) {
            Trace.trace(Xml2Latex.class, method, e);
            throw new DefaultSourceFileExceptionList(e);
        } catch (RuntimeException e) {
            Trace.trace(Xml2Latex.class, method, e);
            throw new DefaultSourceFileExceptionList(e);
        } catch (ModuleDataException e) {
            Trace.trace(Xml2Latex.class, method, e);
            Trace.param(Xml2Latex.class, method, "context", e.getContext());
            throw ModuleDataException2XmlFileException.createXmlFileExceptionList(e,
                qedeqBo.getQedeq());
        } finally {
            if (printer != null) {
                printer.close();
            }
            Trace.end(Xml2Latex.class, method);
        }
    }

}
