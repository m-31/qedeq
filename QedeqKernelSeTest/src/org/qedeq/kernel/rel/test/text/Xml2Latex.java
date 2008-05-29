/* $Id: Xml2Latex.java,v 1.4 2008/03/27 05:12:47 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2008,  Michael Meyling <mime@qedeq.org>.
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
import java.io.IOException;

import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.service.latex.Qedeq2Latex;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.test.KernelFacade;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.utility.IoUtility;
import org.qedeq.kernel.utility.StringUtility;
import org.qedeq.kernel.utility.TextOutput;


/**
 * Test application.
 *
 * @version $Revision: 1.4 $
 * @author    Michael Meyling
 */
public final class Xml2Latex  {

    /** This class. */
    private static final Class CLASS = Xml2Latex.class;

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
        KernelFacade.startup();
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
        System.out.println(StringUtility.getClassName(CLASS) + ", running on: "
            + KernelFacade.getKernelContext().getDescriptiveKernelVersion());
        try {
            System.out.println("Successfully generated:\n" + generate(from, to, language, level));
        } catch (SourceFileExceptionList e) {
            System.out.println(e);
        } catch (RuntimeException e) {
            System.out.println(e);
        }
        KernelFacade.shutdown();

    }

    /**
     * Writes calling convention to <code>System.err</code>.
     */
    public static void printProgramInformation() {
        System.err.println("Name");
        System.err.println("----");
        System.err.println(StringUtility.getClassName(CLASS) + " - create LaTeX document");
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
            final String level) throws SourceFileExceptionList {
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
            throws SourceFileExceptionList {
        final String method = "generate(String, String, String, String)";
        File destination = null;
        File source = null;
        KernelQedeqBo prop = null;
        try {
            Trace.begin(CLASS, method);
            Trace.param(CLASS, method, "from", from);
            Trace.param(CLASS, method, "to", to);
            Trace.param(CLASS, method, "language", language);
            Trace.param(CLASS, method, "level", level);
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
            Trace.trace(CLASS, method, e);
            throw new DefaultSourceFileExceptionList(e);
        }
        TextOutput printer = null;
        try {
            final ModuleAddress address = KernelFacade.getKernelContext().getModuleAddress(
                IoUtility.toUrl(source.getCanonicalFile()));
            prop = (KernelQedeqBo) KernelFacade.getKernelContext().loadModule(address);
            IoUtility.createNecessaryDirectories(destination);
            // System.out.println(simple.getQedeq().toString());
            Qedeq2Latex.createLatex(prop, language, level); 
            return destination.getCanonicalPath();
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
