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

package org.qedeq.gui.se.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.qedeq.base.io.IoUtility;
import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.gui.se.control.QedeqController;
import org.qedeq.gui.se.pane.QedeqGuiConfig;
import org.qedeq.gui.se.util.GuiHelper;
import org.qedeq.kernel.bo.context.KernelContext;
import org.qedeq.kernel.bo.log.LogListenerImpl;
import org.qedeq.kernel.bo.log.ModuleEventListenerLog;
import org.qedeq.kernel.bo.log.ModuleEventLog;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.service.DefaultInternalKernelServices;
import org.qedeq.kernel.xml.dao.XmlQedeqFileDao;

import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.Options;

/**
 * This is the main frame of the GUI frontend for a standalone program
 * version of <b>Hilbert II</b>.
 *
 * @author  Michael Meyling
 * @version $Revision: 1.6 $
 */
public class QedeqMainFrame extends JFrame {

    /** Initial frame resolution. */
    protected static final Dimension PREFERRED_SIZE = (LookUtils.IS_LOW_RESOLUTION
        ? new Dimension(650, 510) : new Dimension(730, 560));

    /**
     * Constructor, configures the UI, and builds the content. Also some indirectly some GUI
     * loggers are added.
     *
     * @param   settings    GUI options.
     * @throws  IOException Initialization failed for IO reasons.
     */
    public QedeqMainFrame(final GuiOptions settings) throws IOException {
        GuiHelper.configureUI(settings);

        checkDirectoryExistenceAndOptionallyCreate(QedeqGuiConfig.getInstance());

        // add various loggers
        QedeqLog.getInstance().addLog(new LogListenerImpl());   // System.out
        QedeqLog.getInstance()                                  // log file
            .addLog(new LogListenerImpl(new PrintStream(
            new FileOutputStream(new File(QedeqGuiConfig.getInstance().getBasisDirectory(),
                QedeqGuiConfig.getInstance().getLogFile()), true), true)));
        ModuleEventLog.getInstance().addLog(new ModuleEventListenerLog());  // all loggers

        // initialize the kernel, this may create already some logging events
        KernelContext.getInstance().init(
            new DefaultInternalKernelServices(KernelContext.getInstance(), new XmlQedeqFileDao()),
            QedeqGuiConfig.getInstance());

        // create new controller for all possible actions
        final QedeqController controller = new QedeqController(this);

        // assemble main GUI window
        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(new QedeqMainPane(controller), BorderLayout.CENTER);
        setContentPane(panel);
        setTitle(" " + KernelContext.getInstance().getDescriptiveKernelVersion());
        final JMenuBar menuBar =  new QedeqMenuBar(controller, settings);
        setJMenuBar(menuBar);
        setIconImage(GuiHelper.readImageIcon("qedeq/16x16/qedeq.png").getImage());
        addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent e) {
                controller.getExitAction().actionPerformed(null);
            }
        });

    }

    private void checkDirectoryExistenceAndOptionallyCreate(final QedeqGuiConfig config)
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
        // TODO mime 20070927: hard coded entry "config":
        String resourceDirectoryName = "config";
        final File resourceDir = new File(config.getBasisDirectory(), resourceDirectoryName);
        final File resource = new File(resourceDir, resourceName);
        String res = "/" + resourceDirectoryName + "/" + resourceName;
        if (!resource.exists()) {
            final URL url = QedeqMainFrame.class.getResource(res);
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
     * @param   Message to print.
     */
    private static void errorPrintln(final String message) {
        System.err.println("ERROR>>> " + message);
    }

    public static void main(final String[] args) {
        // load configuration file
        try {
            QedeqGuiConfig.init(new File(IoUtility.getStartDirectory("qedeq"),
                "config/org.qedeq.properties"), IoUtility.getStartDirectory("qedeq"));
        } catch (Throwable e) {
            e.printStackTrace();
            JOptionPane.showInternalMessageDialog(null, "Configuration file not found!\n\n"
                + e, "Hilbert II - Error", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
            return;
        }

        try {
            // we make a local file copy of the log4j.properties if it dosen't exist already
            initLog4J(QedeqGuiConfig.getInstance());
        } catch (Throwable e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Initialization of Log4J failed!\n\n"
                + e, "Hilbert II - Error", JOptionPane.ERROR_MESSAGE);
            System.exit(-2);
            return;
        }

        try {
            final GuiOptions options = new GuiOptions();
            if (args.length > 0) {
                String lafShortName = args[0];
                String lafClassName;
                if ("Windows".equalsIgnoreCase(lafShortName)) {
                    lafClassName = Options.JGOODIES_WINDOWS_NAME;
                } else if ("Plastic".equalsIgnoreCase(lafShortName)) {
                    lafClassName = Options.PLASTIC_NAME;
                } else if ("Plastic3D".equalsIgnoreCase(lafShortName)) {
                    lafClassName = Options.PLASTIC3D_NAME;
                } else if ("PlasticXP".equalsIgnoreCase(lafShortName)) {
                    lafClassName = Options.PLASTICXP_NAME;
                } else {
                    lafClassName = lafShortName;
                }
                options.setSelectedLookAndFeel(lafClassName);
            }
            final QedeqMainFrame instance;
            try {
                instance = new QedeqMainFrame(options);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Application start failed!\n\n"
                    + e, "Hilbert II - Error", JOptionPane.ERROR_MESSAGE);
                KernelContext.getInstance().shutdown();
                System.exit(-3);
                return;
            }
            instance.setSize(PREFERRED_SIZE);
            Dimension paneSize = instance.getSize();
            Dimension screenSize = instance.getToolkit().getScreenSize();
            instance.setLocation(
                (screenSize.width  - paneSize.width)  / 2,
                (screenSize.height - paneSize.height) / 2);
            instance.setVisible(true);

            // wait till GUI is ready
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    // now we are ready to fire up the kernel
                    KernelContext.getInstance().startup();
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
            Trace.fatal(QedeqMainFrame.class, "main(String[])", null, e);
            JOptionPane.showMessageDialog(null, "Unexpected major failure!\n\n"
                + e, "Hilbert II - Error", JOptionPane.ERROR_MESSAGE);
            System.exit(-4);
            return;
        }
    }

}
