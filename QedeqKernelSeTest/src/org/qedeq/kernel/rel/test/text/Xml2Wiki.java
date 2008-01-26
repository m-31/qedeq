/* $Id: Xml2Wiki.java,v 1.2 2007/12/21 23:35:18 m31 Exp $
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
import java.io.IOException;

import org.qedeq.kernel.bo.module.ModuleAddress;
import org.qedeq.kernel.bo.module.ModuleProperties;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.latex.Qedeq2Wiki;
import org.qedeq.kernel.rel.test.gui.Xml2OtherGui;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.utility.IoUtility;
import org.qedeq.kernel.xml.parser.DefaultSourceFileExceptionList;


/**
 * Test application.
 *
 * @version $Revision: 1.2 $
 * @author    Michael Meyling
 */
public final class Xml2Wiki  {

    /** This class. */
    private static final Class CLASS = Xml2OtherGui.class;

    /**
     * Constructor.
     */
    private Xml2Wiki() {
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
        System.out.println(IoUtility.getClassName(CLASS) + ", running on: "
            + KernelFacade.getKernelContext().getDescriptiveKernelVersion());
        try {
            generate(from, to, language, level);
            System.out.println("Successfully generated files.");
        } catch (SourceFileExceptionList e) {
            // LATER mime 20070323: print all errors in list
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
        System.err.println(IoUtility.getClassName(CLASS) + " - create LaTeX document");
        System.err.println();
        System.err.println("Synopsis");
        System.err.println("-------------------");
        System.err.println("[-h] [-language <language>] [-level <level>] <xmlFile> [-to <directory>]");
        System.err.println();
        System.err.println("Description");
        System.err.println("-----------");
        System.err.println("This program creates wiki file(s) out of *Hilbert II* XML files.");
        System.err.println("If no \"-to\" directory was given, the resulting wiki file(s) are placed ");
        System.err.println("at the same directory as the original file.");
        System.err.println();
        System.err.println("Options and Parameter");
        System.err.println("---------------------");
        System.err.println("-h         writes this text and returns");
        System.err.println("-language  set the language filter (default: \"en\")");
        System.err.println("-level     the level filter (default: \"1\")");
        System.err.println("<xmlFile>  XML file that fulfils the XSD from \"http://www.qedeq.org/"
            + KernelFacade.getKernelContext().getKernelVersionDirectory() + "\"");
        System.err.println("-to <directory> write result(s) into this directory");
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
     * Generate wiki file(s) out of XML file. Also initializes trace file.
     *
     * @param   from            Read this XML file.
     * @param   to              Write to this file. Could be <code>null</code>.
     * @param   language        Resulting language. Could be <code>null</code>.
     * @param   level           Resulting detail level. Could be <code>null</code>.
     * @throws  XmlFilePositionException 
     */
    public static void generate(final String from, final String to, final String language, 
            final String level) throws SourceFileExceptionList {
        generate((from != null ? new File(from) : null), (to != null ? new File(to) : null), 
            language, level);
    }

    /**
     * Generate LaTeX file out of XML file.
     *
     * @param   from            Read this XML file.
     * @param   to              Write to this directory. Could be <code>null</code>.
     * @param   language        Resulting language. Could be <code>null</code>.
     * @param   level           Resulting detail level. Could be <code>null</code>.
     */
    public static void generate(final File from, final File to, final String language, final String level)
            throws SourceFileExceptionList {
        final String method = "generate(String, String, String, String)";
        File source = null;
        File destination = null;
        ModuleProperties prop = null;
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
                destination = source.getParentFile().getCanonicalFile();
            }
        } catch (IOException e) {
            Trace.trace(CLASS, method, e);
            throw new DefaultSourceFileExceptionList(e);
        }
        try {
            final ModuleAddress address = KernelFacade.getKernelContext().getModuleAddress(
                IoUtility.toUrl(source.getCanonicalFile()));
            prop = KernelFacade.getKernelContext().loadModule(address);

            // System.out.println(simple.getQedeq().toString());
            final Qedeq2Wiki converter;
            converter = new Qedeq2Wiki(prop);
            IoUtility.createNecessaryDirectories(destination);
            converter.printWiki(language, level, destination);
        } catch (IOException e) {
            Trace.trace(CLASS, method, e);
            throw new DefaultSourceFileExceptionList(e);
        } catch (RuntimeException e) {
            Trace.trace(CLASS, method, e);
            throw new DefaultSourceFileExceptionList(e);
        } finally {
            Trace.end(CLASS, method);
        }
    }

}
