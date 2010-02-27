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

package org.qedeq.gui.se.pane;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.trace.Trace;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * View for {@link QedeqBo}s.
 *
 * @author  Michael Meyling
 */

public class PreferencesDialog extends JDialog {

    /** This class. */
    private static final Class CLASS = PreferencesDialog.class;

    /** Timeout for making a TCP/IP connection.*/
    private JTextField connectionTimeoutTextField;

    /** Timeout for reading from a TCP/IP connection.*/
    private JTextField readTimeoutTextField;

    /** Automatic scroll of log pane.*/
    private JCheckBox automaticLogScrollCB;

    /** Automatic reload of all modules that were successfully checked in last session.*/
    private JCheckBox autoReloadLastSessionCheckedCB;

    /** Response with a message box.*/
    private JCheckBox directResponseCB;

    /** Automatic start of default HTML browser after HTML generation.*/
    private JCheckBox autoStartHtmlBrowserCB;

    /** QEDEQ module buffer directory.*/
    private JTextArea moduleBufferTextArea;

    /** Generation directory. */
    private JTextArea generationPathTextArea;

    /** Directory for new local modules.*/
    private JTextArea localModulesPathTextArea;

    /** HTTP proxy host.*/
    private JTextField httpProxyHostTextField;

    /** HTTP proxy port.*/
    private JTextField httpProxyPortTextField;

    /** HTTP non proxy hosts.*/
    private JTextField httpNonProxyHostsTextField;

    /** Local QEDEQ module buffer directory.*/
    private File bufferDirectory;

    /** Generation directory.*/
    private File generationDirectory;

    /** Directory for new local modules.*/
    private File localModulesDirectory;

    /** Flag for automatic scroll of log window.*/
    private boolean automaticLogScroll;

    /** Flag for automatic reload of all QEDEQ modules that were successfully loaded in the last
     * session. */
    private boolean autoReloadLastSessionChecked;

    /** Flag for automatic start of the default HTML browser after HTML generation. */
    private boolean autoStartHtmlBrowser;

    /** Flag for direct message box response mode.*/
    private boolean directResponse;

    /** Timeout for creating a TCP/IP connection.*/
    private int connectionTimeout;

    /** Timeout for reading from a TCP/IP connection.*/
    private int readTimeout;

    /** HTTP proxy host.*/
    private String httpProxyHost;

    /** HTTP proxy port.*/
    private String httpProxyPort;

    /** HTTP non proxy hosts.*/
    private String httpNonProxyHosts;

    /** Internal flag for remembering if any value changed.*/
    private boolean changed;


    /**
     * Creates new Panel.
     */
    public PreferencesDialog(final JFrame parent) {
        super(parent, "Preferences");
        final String method = "Constructor";
        Trace.begin(CLASS, this, method);
        try {
            setupView();
            updateView();
        } catch (Throwable e) {
            Trace.trace(CLASS, this, method, e);
        } finally {
            Trace.end(CLASS, this, method);
        }
    }

    /**
     * Assembles Timeout settings panel.
     */
    private JComponent buildTimeoutPanel() {
        FormLayout layout = new FormLayout(
        "right:pref, 5dlu, fill:50dlu:grow");    // columns
//            "right:pref, 5dlu, fill:50dlu:grow");    // columns
//            + "pref, 3dlu, pref");                  // rows

        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        builder.getPanel().setOpaque(false);

        builder.append("Connection Timeout");
        connectionTimeout = QedeqGuiConfig.getInstance().getConnectTimeout();
        connectionTimeoutTextField = createTextField("" + connectionTimeout, true);
        builder.append(connectionTimeoutTextField);

        readTimeout = QedeqGuiConfig.getInstance().getReadTimeout();
        builder.append("Read Timeout");
        readTimeoutTextField = createTextField("" + readTimeout , true);
        builder.append(readTimeoutTextField);
        return addSpaceAndTitle(builder.getPanel(), "Timeouts");
    }

    /**
     * Assembles proxy settings panel.
     */
    private JComponent buildProxyPanel() {

        if (IoUtility.isWebStarted()) {
            JPanel panel = new JPanel();
            JTextArea label = new JTextArea("This application is webstarted. For changing the"
                + " proxy settings see for example \"Sun Java Plugin Control Panel / General /"
                + " Network Settings\".");
            label.setWrapStyleWord(true);
            label.setLineWrap(true);
            label.setEditable(false);
//            JTextArea label = new JTextArea("For webstart");
//            label.setMinimumSize(new Dimension(400, 30));
//            label.setPreferredSize(new Dimension(400, 30));
            panel.add(label);
//            panel.setMinimumSize(new Dimension(400, 30));
//            panel.setPreferredSize(new Dimension(400, 30));
//            panel.setLayout(new FlowLayout(FlowLayout.LEFT));
            panel.setLayout(new GridLayout(0, 1));
//            return panel;
            JPanel withSpace = new JPanel();
            //        withSpace.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                    withSpace.add(panel);
                    withSpace.setLayout(new GridLayout(0, 1));
                    JPanel withTitle = new JPanel();
                    withTitle.setBorder(BorderFactory.createTitledBorder("Proxy Settings"));
                    withTitle.add(withSpace);
                    withTitle.setLayout(new GridLayout(0, 1));
                    return withTitle;
        } else {
            FormLayout layout = new FormLayout(
            "right:pref, 5dlu, fill:50dlu:grow");    // columns
//                "right:pref, 5dlu, fill:50dlu:grow");    // columns
//                + "pref, 3dlu, pref");                  // rows

            DefaultFormBuilder builder = new DefaultFormBuilder(layout);
            builder.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            builder.getPanel().setOpaque(false);
            httpProxyHost = QedeqGuiConfig.getInstance().getHttpProxyHost();
            builder.append("HTTP proxy host");
            httpProxyHostTextField = createTextField(httpProxyHost, true);
            builder.append(httpProxyHostTextField);

            httpProxyPort = QedeqGuiConfig.getInstance().getHttpProxyPort();
            builder.append("HTTP proxy port");
            httpProxyPortTextField = createTextField(httpProxyPort, true);
            builder.append(httpProxyPortTextField);

            httpNonProxyHosts = QedeqGuiConfig.getInstance().getHttpNonProxyHosts();
            builder.append("HTTP non proxy hosts");
            httpNonProxyHostsTextField = createTextField(httpNonProxyHosts, true);
            builder.append(httpNonProxyHostsTextField);
            return addSpaceAndTitle(builder.getPanel(), "Proxy Settings");
        }

    }

    /**
     * Assembles Timeout settings panel.
     */
    private JComponent buildPathsPanel() {
        FormLayout layout = new FormLayout(
            "fill:50dlu:grow");    // columns

        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        builder.getPanel().setOpaque(false);

        builder.append("Path for newly created module files");
        bufferDirectory = QedeqGuiConfig.getInstance().getBufferDirectory();
        moduleBufferTextArea = new JTextArea(bufferDirectory.getAbsolutePath());
        moduleBufferTextArea.setEditable(false);
        moduleBufferTextArea.setLineWrap(false);
        builder.append(wrapWithScrollPane(moduleBufferTextArea));

        builder.append("Path for generated files");
        generationDirectory = QedeqGuiConfig.getInstance().getGenerationDirectory();
        generationPathTextArea = new JTextArea(generationDirectory.getAbsolutePath());
        generationPathTextArea.setEditable(false);
        builder.append(wrapWithScrollPane(generationPathTextArea));

        builder.append("Path for newly created module files");
        localModulesDirectory = QedeqGuiConfig.getInstance().getLocalModulesDirectory();
        localModulesPathTextArea = new JTextArea(localModulesDirectory.getAbsolutePath());
        localModulesPathTextArea.setEditable(false);
        builder.append(wrapWithScrollPane(localModulesPathTextArea));

        JPanel withSpace = new JPanel();
        withSpace.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // FIXME dynamize
        withSpace.add(builder.getPanel());
        withSpace.setLayout(new GridLayout(0, 1));
        JPanel withTitle = new JPanel();
        withTitle.setBorder(BorderFactory.createTitledBorder("Paths"));
        withTitle.add(withSpace);
        withTitle.setLayout(new GridLayout(0, 1));
        return withTitle;

//        return addSpaceAndTitle(builder.getPanel(), "Paths");
    }

    /**
     * Assembles checkbox settings panel.
     */
    private JComponent buildBinaryOptionPanel() {
        FormLayout layout = new FormLayout(
        "left:pref");    // columns
//            "right:pref, 5dlu, fill:50dlu:grow");    // columns
//            + "pref, 3dlu, pref");                   // rows

        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        builder.getPanel().setOpaque(false);

        automaticLogScroll = QedeqGuiConfig.getInstance().isAutoReloadLastSessionChecked();
        autoReloadLastSessionCheckedCB = new JCheckBox(" Auto loading of in last session successfully checked modules",
            automaticLogScroll);
        autoReloadLastSessionCheckedCB.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                PreferencesDialog.this.autoReloadLastSessionChecked
                    = PreferencesDialog.this.autoReloadLastSessionCheckedCB.isSelected();
                PreferencesDialog.this.changed = true;
            }
        });
        builder.append(autoReloadLastSessionCheckedCB);

        directResponse = QedeqGuiConfig.getInstance().isDirectResponse();
        directResponseCB = new JCheckBox(" Direct message response for actions",
            directResponse);
        directResponseCB.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                PreferencesDialog.this.directResponse = PreferencesDialog.this.directResponseCB.isSelected();
                PreferencesDialog.this.changed = true;
            }
        });
        builder.append(directResponseCB);

        automaticLogScroll = QedeqGuiConfig.getInstance().isAutomaticLogScroll();
        automaticLogScrollCB = new JCheckBox(" Automatic Scroll of Log Window",
            automaticLogScroll);
        automaticLogScrollCB.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                PreferencesDialog.this.automaticLogScroll
                    = PreferencesDialog.this.automaticLogScrollCB.isSelected();
                PreferencesDialog.this.changed = true;
            }
        });
        builder.append(automaticLogScrollCB);

        autoStartHtmlBrowser = QedeqGuiConfig.getInstance().isAutoStartHtmlBrowser();
        autoStartHtmlBrowserCB = new JCheckBox(" Auto start web browser after HTML generation",
            autoStartHtmlBrowser);
        autoStartHtmlBrowserCB.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                PreferencesDialog.this.autoStartHtmlBrowser
                    = PreferencesDialog.this.autoStartHtmlBrowserCB.isSelected();
                PreferencesDialog.this.changed = true;
            }
        });
        builder.append(autoStartHtmlBrowserCB);
        return addSpaceAndTitle(builder.getPanel(), "Miscellaneous Switches");
    }

    /**
     * Adds border space to a panel and surrounds it with a title border.
     *
     * @param   panel   Panel to decorate.
     * @param   title   Title to use.
     * @return  Panel with more decorations.
     */
    private JComponent addSpaceAndTitle(final JPanel panel, final String title) {
        JPanel withSpace = new JPanel();
//        withSpace.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        withSpace.add(panel);
//        withSpace.setLayout(new GridLayout(0, 1));
        JPanel withTitle = new JPanel();
        withTitle.setBorder(BorderFactory.createTitledBorder(title));
        withTitle.add(withSpace);
        withTitle.setLayout(new FlowLayout(FlowLayout.LEFT));
        return withTitle;
    }

    /**
     * Adds boarder space and floats panel to the right.
     *
     * @param   panel   Panel to decorate.
     * @return  Panel with more decorations.
     */
    private JComponent addSpaceAndAlignRight(final JPanel panel) {
        JPanel withSpace = new JPanel();
//        withSpace.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // FIXME dynamize
        withSpace.add(panel);
        JPanel alignRight = new JPanel();
        alignRight.setLayout(new FlowLayout(FlowLayout.RIGHT));
        alignRight.add(withSpace);
        return alignRight;
    }

    /**
     * Assembles the GUI components of the panel.
     */
    public final void setupView() {
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        //A border that puts 10 extra pixels at the sides and
        //bottom of each pane.
        Border paneEdge = BorderFactory.createEmptyBorder(10, 10, 10, 10); // FIXME dynamize
        JPanel allOptions = new JPanel();
        allOptions.setBorder(paneEdge);
        allOptions.setLayout(new BoxLayout(allOptions, BoxLayout.Y_AXIS));
        allOptions.add(buildBinaryOptionPanel());
        allOptions.add(buildPathsPanel());
        allOptions.add(buildTimeoutPanel());
        JComponent proxyPanel = buildProxyPanel();
        allOptions.add(proxyPanel);
        add(allOptions);

        ButtonBarBuilder bbuilder = ButtonBarBuilder.createLeftToRightBuilder();

        JButton ok = new JButton("OK");
        ok.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                PreferencesDialog.this.save();
                PreferencesDialog.this.dispose();
            }
        });

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                PreferencesDialog.this.dispose();
            }
        });

        bbuilder.addGriddedButtons(new JButton[]{cancel, ok});

        final JPanel buttons = bbuilder.getPanel();
        add(addSpaceAndAlignRight(buttons));

        // let the container calculate the ideal size
        pack();

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2,
            getWidth(), getHeight() + (IoUtility.isWebStarted() ? proxyPanel.getHeight() : 0));
    }

    /**
     * Update from model.
     */
    public void updateView() {
        invalidate();
        repaint();
    }

    private JTextField createTextField(final String selectedText, final boolean editable) {
        JTextField combo = new JTextField(selectedText);
        combo.setEditable(editable);
        return combo;
    }

    private Component wrapWithScrollPane(final Component c) {
        return new JScrollPane(c,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }

    final void save() {
        if (changed) {
// TODO mime 20070903: setting still unsupported
            QedeqGuiConfig.getInstance().setBufferDirectory(bufferDirectory);
            QedeqGuiConfig.getInstance().setGenerationDirectory(generationDirectory);
            QedeqGuiConfig.getInstance().setLocalModulesDirectory(localModulesDirectory);
            QedeqGuiConfig.getInstance().setAutomaticLogScroll(automaticLogScroll);
            QedeqGuiConfig.getInstance().setDirectResponse(directResponse);
            QedeqGuiConfig.getInstance().setAutoReloadLastSessionChecked(
                autoReloadLastSessionChecked);
            QedeqGuiConfig.getInstance().setAutoStartHtmlBrowser(autoStartHtmlBrowser);
            QedeqGuiConfig.getInstance().setConnectionTimeout(connectionTimeout);
            QedeqGuiConfig.getInstance().setReadTimeout(readTimeout);
            if (!IoUtility.isWebStarted()) {
                QedeqGuiConfig.getInstance().setHttpProxyHost(httpProxyHost);
                QedeqGuiConfig.getInstance().setHttpProxyHost(httpProxyPort);
                QedeqGuiConfig.getInstance().setHttpProxyHost(httpNonProxyHosts);
            }
            try {
                QedeqGuiConfig.getInstance().store();
            } catch (IOException e) {
                Trace.fatal(CLASS, this, "save", "couldn't save preferences", e);
            }
        }
    }

}
