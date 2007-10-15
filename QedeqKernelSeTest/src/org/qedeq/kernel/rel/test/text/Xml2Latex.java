/* $Id: Xml2Latex.java,v 1.1 2007/10/07 16:43:10 m31 Exp $
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
import org.qedeq.kernel.latex.Qedeq2Latex;
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
public final class Xml2Latex  {

    /**
     * Constructor.
     */
    private Xml2Latex() {
        // nothing to do
    }

    /**
     * Main method.
     *
     * @param   args    Various parameters. See implementation of {@link #printProgramInformation()}.
     */
    public static void main(final String[] args) {
        String language = null;
        String level = null;
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
                if (option.equals("language")) {
                    if (i + 1 >= args.length) {
                        printArgumentError("\"-language\" must be followed by a language.");
                        return;
                    }
                    language = args[i + 1];
                    i++;
                } else if (option.equals("level")) {
                    if (i + 1 >= args.length) {
                        printArgumentError("\"-level\" must be followed by a level.");
                        return;
                    }
                    level = args[i + 1];
                    i++;
                } else if (option.equals("to")) {
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
        System.out.println(IoUtility.getClassName(Xml2Latex.class) + ", running on: "
            + KernelFacade.getKernelContext().getDescriptiveKernelVersion());
        try {
            System.out.println("Successfully generated:\n" + generate(from, to, language, level));
        } catch (XmlFileExceptionList e) {
            System.out.println(e);  // TODO mime 20070323: better: iterate through errors
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
        System.err.println(IoUtility.getClassName(Xml2Latex.class) + " - create LaTeX document");
        System.err.println();
        System.err.println("Synopsis");
        System.err.println("-------------------");
        System.err.println("[-h] [-language <language>] [-level <level>] <xmlFile> [-to <latexFile>]");
        System.err.println();
        System.err.println("Description");
        System.err.println("-----------");
        System.err.println("This program creates a LaTeX file out of *Hilbert II* XML files.");
        System.err.println("If no \"-to\" filename was given, the resulting LaTeX file is at the same");
        System.err.println("place as the original file but has the extension \".tex\".");
        System.err.println();
        System.err.println("Options and Parameter");
        System.err.println("---------------------");
        System.err.println("-h         writes this text and returns");
        System.err.println("-language  set the language filter (default: \"en\")");
        System.err.println("-level     the level filter (default: \"1\")");
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
     * Generate LaTeX file out of XML file. Also initializes trace file.
     *
     * @param   from            Read this XML file.
     * @param   to              Write to this file. Could be <code>null</code>.
     * @param   language        Resulting language. Could be <code>null</code>.
     * @param   level           Resulting detail level. Could be <code>null</code>.
     * @return  File name of generated LaTeX file.
     * @throws  XmlFilePositionException 
     */
    public static String generate(final String from, final String to, final String language, 
            final String level) throws XmlFileExceptionList {
        return generate((from != null ? new File(from) : null), (to != null ? new File(to) : null), 
            language, level);
    }

    /**
     * Generate LaTeX file out of XML file.
     *
     * @param   from            Read this XML file.
     * @param   to              Write to this file. Could be <code>null</code>.
     * @param   language        Resulting language. Could be <code>null</code>.
     * @param   level           Resulting detail level. Could be <code>null</code>.
     * @return  File name of generated LaTeX file.
     * @throws  XmlFilePositionException 
     */
    public static String generate(final File from, final File to, final String language, final String level)
            throws XmlFileExceptionList {
        final String method = "generate(String, String, String, String)";
        File destination = null;
        File source = null;
        QedeqBo qedeqBo = null;
        try {
            Trace.begin(Xml2Latex.class, method);
            Trace.param(Xml2Latex.class, method, "from", from);
            Trace.param(Xml2Latex.class, method, "to", to);
            Trace.param(Xml2Latex.class, method, "language", language);
            Trace.param(Xml2Latex.class, method, "level", level);
            source = from.getCanonicalFile();
            if (to != null) {
                destination = to.getCanonicalFile();
            } else {
                String tex = source.getName();
                if (tex.toLowerCase().endsWith(".xml")) {
                    tex = tex.substring(0, tex.length() - 4);
                }
                if (language != null && language.length() > 0) {
                    tex = tex + "_" + language;
                }
                destination = new File(source.getParentFile(), tex + ".tex").getCanonicalFile();
            }
        } catch (IOException e) {
            Trace.trace(Xml2Latex.class, method, e);
            throw new DefaultXmlFileExceptionList(e);
        }
        TextOutput printer = null;
        try {
            qedeqBo = KernelFacade.getKernelContext().loadModule(
                IoUtility.toUrl(source.getCanonicalFile()).toExternalForm());
            IoUtility.createNecessaryDirectories(destination);
            final OutputStream outputStream = new FileOutputStream(destination);
            printer = new TextOutput(destination.getName(), outputStream);
            
            // System.out.println(simple.getQedeq().toString());
            Qedeq2Latex.print(source.getAbsolutePath(), qedeqBo, printer, 
                language, level); 
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
            Trace.end(Xml2Latex.class, method);
        }
    }

}
