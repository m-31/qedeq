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

package org.qedeq.text.se.main;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.qedeq.base.io.IoUtility;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.gui.se.pane.QedeqGuiConfig;
import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.bo.log.LogListenerImpl;
import org.qedeq.kernel.bo.log.ModuleEventListenerLog;
import org.qedeq.kernel.bo.log.ModuleEventLog;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.log.TraceListener;
import org.qedeq.kernel.bo.service.DefaultInternalKernelServices;
import org.qedeq.kernel.bo.service.logic.SimpleProofFinderPlugin;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.xml.dao.XmlQedeqFileDao;

/**
 * This is the main console for a standalone program version of <b>Hilbert II</b>.
 *
 * @author  Michael Meyling
 */
public final class Main {

    /**
     * Don't call me.
     *
     */
    private Main()  {

    }

    private static void checkDirectoryExistenceAndOptionallyCreate(final QedeqGuiConfig config)
            throws IOException {
        // application log file directory
        {
            final File file = new File(config.getBasisDirectory(), config.getLogFile());
            final File dir = file.getParentFile();
            if (!dir.exists() &&  !dir.mkdirs()) {
                throw new IOException("can't create directory: " + dir.getAbsolutePath());
            }
        }
    }

    /**
     * Make local copy of Log4J properties if we don't find the Log4J property file in application
     * config directory. This is necessary especially if the application was launched by
     * Webstart.
     * <p>
     * If the copy action fails, error messages are written to <code>System.err</code> but the
     * application continues.
     *
     * @param   config  Configuration file.
     */
    private static void initLog4J(final QedeqGuiConfig config) {
        final String resourceName = "log4j.xml";
        // LATER mime 20070927: hard coded entry "config":
        String resourceDirectoryName = "config";
        final File resourceDir = new File(config.getBasisDirectory(), resourceDirectoryName);
        final File resource = new File(resourceDir, resourceName);
        String res = "/" + resourceDirectoryName + "/" + resourceName;
        if (!resource.exists()) {
            final URL url = Main.class.getResource(res);
            if (url == null) {
                errorPrintln("Resource not found: " + res);
            } else {
                try {
                    if (!resourceDir.exists()) {
                        if (!resourceDir.mkdirs()) {
                            errorPrintln("Creation of directory failed: "
                                + resourceDir.getAbsolutePath());
                        }
                    }
                    final StringBuffer buffer = new StringBuffer();
                    IoUtility.loadFile(url, buffer, "ISO-8859-1");
                    File traceFile = config.createAbsolutePath("log/trace.txt");
                    StringUtility.replace(buffer, "@trace_file_path@", traceFile.toString()
                        .replace('\\', '/'));
// for a properties file:
//                        IoUtility.escapeProperty(traceFile.toString().replace('\\', '/')));
                    IoUtility.saveFile(resource, buffer, "ISO-8859-1");
                    res = IoUtility.toUrl(resource).toString();
                } catch (IOException e1) {
                    errorPrintln("Resource can not be saved: " + resource.getAbsolutePath());
                    e1.printStackTrace();
                }
            }
        } else {
            res = IoUtility.toUrl(resource).toString();
        }
        System.setProperty("log4j.configDebug", "true");
        System.setProperty("log4j.configuration", res);

        // init Log4J watchdog
        try {
            // set properties and watch file every 5 seconds
            if (res.endsWith(".xml")) {
                DOMConfigurator.configureAndWatch(resource.getCanonicalPath(), 5000);
            } else {
                PropertyConfigurator.configureAndWatch(resource.getCanonicalPath(), 5000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Print error message. Writes to <code>System.err</code>.
     *
     * @param   message Message to print.
     */
    private static void errorPrintln(final String message) {
        System.err.println("ERROR>>> " + message);
    }

    /**
     * Main method.
     *
     * @param   args    Various parameters. See implementation of {@link #printProgramInformation()}.
     */
    public static void main(final String[] args) {
        String qedeq = null;

        if (args.length == 0) {
            printProgramInformation();
            return;
        }

        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {  // option
                String option = args[i].substring(1).toLowerCase();
                if (option.length() <= 0 && i + 1 < args.length) {
                    option = args[i + 1];
                }
                if (option.equals("help") || option.equals("h")
                        || option.equals("?")) {
                    printProgramInformation();
                    return;
                } else {                    // unknown option
                    printArgumentError("Unknown option: " + option);
                    return;
                }
            } else {                        // no option, must be directory name
                if (qedeq != null) {
                    printArgumentError("only one QEDEQ module can be searched at once.");
                    return;
                }
                qedeq = args[i];
            }
        }

        // check parameters
        if (qedeq == null) {
            printArgumentError("QEDEQ module must be specified.");
            return;
        }

        findProofs(qedeq);
    }

    /**
     * Writes calling convention to <code>System.err</code>.
     */
    public static void printProgramInformation() {
        System.err.println("Name");
        System.err.println("----");
        System.err.println(Main.class.getName() + " - find simple propositional calculus proofs");
        System.err.println();
        System.err.println("Synopsis");
        System.err.println("--------");
        System.err.println("[-h] <module url>");
        System.err.println();
        System.err.println("Description");
        System.err.println("-----------");
        System.err.println("This program finds formal proofs for propositional calculus propositions. You give it");
        System.err.println("an QEDEQ module URL and the missing formal proofs will be added. When a proof was found");
        System.err.println("the buffered file (or original if the protocol is \"file\") will be altered.");
        System.err.println();
        System.err.println("Options and Parameter");
        System.err.println("---------------------");
        System.err.println("-h           writes this text and returns");
        System.err.println("<module url> URL for QEDEQ module to work on");
        System.err.println();
        System.err.println("Example");
        System.err.println("-------");
        System.err.println("http://www.qedeq.org/0_04_02/sample/qedeq_sample4.xml");
        System.err.println();
    }

    /**
     * Print argument error to System.err.
     *
     * @param   message Message to print.
     */
    private static void printArgumentError(final String message) {
        System.err.println(">>>ERROR reason:");
        System.err.println(message);
        System.err.println();
        System.err.println(">>>Calling convention:");
        printProgramInformation();
    }

    public static void findProofs(final String qedeq) {
        // load configuration file
        try {
            QedeqGuiConfig.init(new File(IoUtility.getStartDirectory("qedeq"),
                "config/org.qedeq.properties"), IoUtility.getStartDirectory("qedeq"));
        } catch (Throwable e) {
            e.printStackTrace();
            System.err.println("Configuration file not found!");
            System.exit(-1);
            return;
        }

        try {
            // we make a local file copy of the log4j.properties if it dosen't exist already
            initLog4J(QedeqGuiConfig.getInstance());
        } catch (Throwable e) {
            e.printStackTrace();
            System.err.println("Initialization of Log4J failed!");
            System.exit(-2);
            return;
        }

        try {
            try {
                checkDirectoryExistenceAndOptionallyCreate(QedeqGuiConfig.getInstance());

                // add various loggers
                QedeqLog.getInstance().addLog(new LogListenerImpl());   // System.out
                QedeqLog.getInstance().addLog(new TraceListener());     // trace file
                ModuleEventLog.getInstance().addLog(new ModuleEventListenerLog());  // all loggers

                // initialize the kernel, this may create already some logging events
                KernelContext.getInstance().init(
                    new DefaultInternalKernelServices(KernelContext.getInstance(), new XmlQedeqFileDao()),
                    QedeqGuiConfig.getInstance());
                KernelContext.getInstance().startup();
                final ModuleAddress address = KernelContext.getInstance().getModuleAddress(qedeq);
                KernelContext.getInstance().loadModule(address);
                KernelContext.getInstance().executePlugin(SimpleProofFinderPlugin.class.getName(),
                    address, null);
                KernelContext.getInstance().shutdown();
            } catch (IOException e) {
                System.err.println("Application start failed!");
                System.exit(-3);
                return;
            }

        } catch (Throwable e) {
            e.printStackTrace();
            System.err.println("Unexpected major failure!");
            System.exit(-4);
            return;
        }
    }

}
