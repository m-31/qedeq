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

package org.qedeq.kernel.rel.test.text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.qedeq.kernel.bo.module.ModuleDataException;
import org.qedeq.kernel.bo.module.QedeqBo;
import org.qedeq.kernel.common.XmlFileExceptionList;
import org.qedeq.kernel.latex.Qedeq2Xml;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.utility.IoUtility;
import org.qedeq.kernel.utility.TextOutput;
import org.qedeq.kernel.xml.mapper.ModuleDataException2XmlFileException;
import org.qedeq.kernel.xml.parser.DefaultXmlFileExceptionList;


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
     * Main method.
     *
     * @param   args    Various parameters. See implementation of
     *                  {@link #printProgramInformation()}.
     */
    public static void main(final String[] args) {
        String from = null;
        String to = null;

        if (args.length == 0) {
            printProgramInformation();
            return;
        }

        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {  // option
                final String option = args[i].substring(1).toLowerCase();
                if (option.equals("help") || option.equals("h")
                        || option.equals("?")) {
                    printProgramInformation();
                    return;
                }
                if (option.equals("to")) {
                    if (i + 1 >= args.length) {
                        printArgumentError("\"-to\" must be followed by a filename.");
                        return;
                    }
                    to = args[i + 1];
                    i++;
                } else {                    // unknown option
                    printArgumentError("Unknown option: " + option);
                    return;
                }
            } else {                        // no option, must be file name
                if (from != null) {
                    printArgumentError("XML file name must only be specified once.");
                    return;
                }
                from = args[i];
            }
        }
        if (from == null) {
            printArgumentError("XML file must be specified.");
            return;
        }
        System.out.println(IoUtility.getClassName(Xml2Xml.class) + ", running on: "
            + KernelFacade.getKernelContext().getDescriptiveKernelVersion());
        try {
            System.out.println("Successfully generated:\n" + generate(from, to));
        } catch (XmlFileExceptionList e) {
            // TODO mime 20070323: print all single errors
            System.out.println(e);
        } catch (RuntimeException e) {
            System.out.println(e);
        }
    }

    /**
     * Writes calling convention to <code>System.err</code>.
     */
    public static void printProgramInformation() {
        System.err.println("Name");
        System.err.println("----");
        System.err.println(IoUtility.getClassName(Xml2Xml.class) + " - create LaTeX document");
        System.err.println();
        System.err.println("Synopsis");
        System.err.println("-------------------");
        System.err.println("[-h] <xmlFile> [-to <latexFile>]");
        System.err.println();
        System.err.println("Description");
        System.err.println("-----------");
        System.err.println("This program creates a XML file out of *Hilbert II* XML files.");
        System.err.println("If no \"-to\" filename was given, the resulting XML file is at the same");
        System.err.println("place as the original file but has the extension \"_.xml\".");
        System.err.println();
        System.err.println("Options and Parameter");
        System.err.println("---------------------");
        System.err.println("-h         writes this text and returns");
        System.err.println("<xmlFile>  XML file that fulfils the XSD from \"http://www.qedeq.org/"
            + KernelFacade.getKernelContext().getKernelVersionDirectory() + "\"");
        System.err.println("-to <file> write result into this file");
        System.err.println();
        System.err.println("Parameter Examples");
        System.err.println("------------------");
        System.err.println("sample/qedeq_basic_concept.xml");
        System.err.println();
        System.err.println("Further information");
        System.err.println("-------------------");
        System.err.println("For more information about *Hilbert II* look at:");
        System.err.println("\thttp://www.qedeq.org/");
        System.err.println();
    }

    private static void printArgumentError(final String message) {
        System.err.println(">>>ERROR reason:");
        System.err.println(message);
        System.err.println();
        System.err.println(">>>Calling convention:");
        printProgramInformation();
    }
    
    /**
     * Generate LaTeX file out of XML file.
     *
     * @param   from            Read this XML file.
     * @param   to              Write to this file. Could be <code>null</code>.
     * @return  File name of generated LaTeX file.
     */
    public static String generate(final String from, final String to) 
            throws XmlFileExceptionList {
        return generate((from != null ? new File(from) : null), (to != null ? new File(to) : null));
    }

    /**
     * Generate LaTeX file out of XML file. Also initializes trace file.
     *
     * @param   from            Read this XML file.
     * @param   to              Write to this file. Could be <code>null</code>.
     * @return  File name of generated LaTeX file.
     */
    public static String generate(final File from, final File to)
            throws XmlFileExceptionList {
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
            Trace.trace(Xml2Latex.class, method, e);
            throw new DefaultXmlFileExceptionList(e);
        }
        TextOutput printer = null;
        try {
            qedeqBo = KernelFacade.getKernelContext().loadModule(
                IoUtility.toUrl(source.getCanonicalFile()).toExternalForm());

            // System.out.println(simple.getQedeq().toString());
            IoUtility.createNecessaryDirectories(destination);
            final OutputStream outputStream = new FileOutputStream(destination);
            printer = new TextOutput(destination.getName(), outputStream);
            Qedeq2Xml.print(source.getCanonicalPath(), qedeqBo, printer);
            return destination.getCanonicalPath();
        } catch (IOException e) {
            Trace.trace(Xml2Latex.class, method, e);
            throw new DefaultXmlFileExceptionList(e);
        } catch (RuntimeException e) {
            Trace.trace(Xml2Latex.class, method, e);
            throw new DefaultXmlFileExceptionList(e);
        } catch (ModuleDataException e) {
            Trace.trace(Xml2Latex.class, method, e);
            Trace.param(Xml2Latex.class, method, "context", e.getContext());
            throw ModuleDataException2XmlFileException.createXmlFileExceptionList(e, qedeqBo);
        } finally {
            if (printer != null) {
                printer.close();
            }
            Trace.end(Xml2Xml.class, method);
        }
    }

}
