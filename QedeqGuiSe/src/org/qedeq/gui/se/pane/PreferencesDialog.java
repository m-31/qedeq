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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileFilter;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.gui.se.util.GuiHelper;
import org.qedeq.kernel.bo.context.KernelContext;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Configures the application.
 *
 * @author  Michael Meyling
 */

public class PreferencesDialog extends JDialog {

    /** This class. */
    private static final Class CLASS = PreferencesDialog.class;

    /** Timeout for making a TCP/IP connection. */
    private JTextField connectionTimeoutTextField;

    /** Timeout for reading from a TCP/IP connection. */
    private JTextField readTimeoutTextField;

    /** Automatic scroll of log pane. */
    private JCheckBox automaticLogScrollCB;

    /** Automatic reload of all modules that were successfully checked in last session. */
    private JCheckBox autoReloadLastSessionCheckedCB;

    /** List of icon sizes. */
    private JComboBox iconSizeCB;

    /** Look and feel of the application. */
    private JComboBox lookAndFeelCB;

//    /** Response with a message box. */
//    private JCheckBox directResponseCB;

    /** Automatic start of default HTML browser after HTML generation. */
    private JCheckBox autoStartHtmlBrowserCB;

    /** Enable detail trace. Detail level are configurated within the logging environment.
     * (E.g. within log4j.xml.)*/
    private JCheckBox traceOnCB;

    /** QEDEQ module buffer directory. */
    private JTextArea moduleBufferTextArea;

    /** Generation directory. */
    private JTextArea generationPathTextArea;

    /** Directory for new local modules. */
    private JTextArea localModulesPathTextArea;

    /** HTTP proxy host.*/
    private JTextField httpProxyHostTextField;

    /** HTTP proxy port. */
    private JTextField httpProxyPortTextField;

    /** HTTP non proxy hosts. */
    private JTextField httpNonProxyHostsTextField;

    /**
     * Creates new Panel.
     *
     * @param   parent  Parent frame.
     */
    public PreferencesDialog(final JFrame parent) {
        super(parent, "Preferences");
        final String method = "Constructor";
        Trace.begin(CLASS, this, method);
        try {
            setModal(true);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setupView();
            updateView();
        } catch (Throwable e) {
            Trace.fatal(CLASS, this, "Initalization of PreferencesDialog failed.", method, e);
        } finally {
            Trace.end(CLASS, this, method);
        }
    }

    /**
     * Assembles Timeout settings panel.
     *
     * @return  Timeout settings panel.
     */
    private JComponent buildTimeoutPanel() {
        FormLayout layout = new FormLayout(
        "right:pref, 5dlu, fill:50dlu");    // columns

        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        builder.getPanel().setOpaque(false);

        builder.append("Connection Timeout");
        connectionTimeoutTextField = createTextField("" + QedeqGuiConfig.getInstance().getConnectTimeout(), true);
        connectionTimeoutTextField.setToolTipText("Sets a specified timeout value, in milliseconds, to be used when"
            + " opening a communications link a remote QEDEQ module. If the timeout expires before the connection can"
            + " be established, an error occurs. A timeout of zero is interpreted as an infinite timeout.");
        builder.append(connectionTimeoutTextField);

        builder.append("Read Timeout");
        readTimeoutTextField = createTextField("" + QedeqGuiConfig.getInstance().getReadTimeout() , true);
        readTimeoutTextField.setToolTipText("Sets the read timeout to a specified timeout, in milliseconds. A"
           + " non-zero value specifies the timeout when reading from Input stream when a connection is established"
           + " to a resource. If the timeout expires before there is data available for read, an error occurs. A"
           + " timeout of zero is interpreted as an infinite timeout.");
        builder.append(readTimeoutTextField);
        return GuiHelper.addSpaceAndTitle(builder.getPanel(), "Timeouts");
    }

    /**
     * Assembles proxy settings panel.
     *
     * @return  Proxy settings panel.
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
            panel.add(label);
            panel.setLayout(new GridLayout(0, 1));
            JPanel withSpace = new JPanel();
                withSpace.add(panel);
                withSpace.setLayout(new GridLayout(0, 1));
                JPanel withTitle = new JPanel();
                withTitle.setBorder(BorderFactory.createTitledBorder("Proxy Settings"));
                withTitle.add(withSpace);
                withTitle.setLayout(new GridLayout(0, 1));
                return withTitle;
        } else {
            FormLayout layout = new FormLayout(
                "left:pref, 5dlu, fill:pref:grow");    // columns

            DefaultFormBuilder builder = new DefaultFormBuilder(layout);
            builder.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            builder.getPanel().setOpaque(false);

            builder.append("HTTP proxy host");
            httpProxyHostTextField = createTextField(QedeqGuiConfig.getInstance().getHttpProxyHost(), true);
            httpProxyHostTextField.setToolTipText("Proxy server for the http protocol.");
            builder.append(httpProxyHostTextField);

            builder.append("HTTP proxy port");
            httpProxyPortTextField = createTextField(QedeqGuiConfig.getInstance().getHttpProxyPort(), true);
            httpProxyPortTextField.setToolTipText("Proxy server port for the http protocol.");
            builder.append(httpProxyPortTextField);

            builder.append("HTTP non proxy hosts");
            httpNonProxyHostsTextField = createTextField(StringUtility.replace(QedeqGuiConfig.getInstance()
                .getHttpNonProxyHosts(), "|", ","), true);
            builder.append(httpNonProxyHostsTextField);
            httpNonProxyHostsTextField.setToolTipText("Lists the hosts which should be connected to directly and"
                + " not through the proxy server. The value can be a comma separated list of hosts, and in addition"
                + " a wildcard character (*) can be used for matching. For example: *.foo.com,localhost");

            return GuiHelper.addSpaceAndTitle(builder.getPanel(), "Proxy Settings");
        }
    }

    /**
     * Assembles path settings panel.
     *
     * @return  Created panel.
     */
    private JComponent buildPathsPanel() {
        FormLayout layout = new FormLayout(
//            "fill:200dlu:grow, 5dlu, right:pref");    // columns
            "left:pref, fill:5dlu:grow, right:pref");    // columns

        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        builder.getPanel().setOpaque(false);

        builder.append("Path for QEDEQ module file buffer");
        moduleBufferTextArea = new JTextArea(QedeqGuiConfig.getInstance().getBufferDirectory().getPath());
        moduleBufferTextArea.setEditable(false);
        moduleBufferTextArea.setLineWrap(false);
        final JButton chooseBufferLocation = new JButton("Choose");
        builder.append(chooseBufferLocation);
        builder.append(wrapWithScrollPane(moduleBufferTextArea), 3);
        chooseBufferLocation.setEnabled(true);
//        chooseBufferLocation.setBounds(33 + 600 - 90, 180 + y, 90, 21);
        chooseBufferLocation.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                try {
                    QedeqGuiConfig.getInstance().getBufferDirectory().mkdirs();
                    JFileChooser chooser = new JFileChooser(QedeqGuiConfig.getInstance().getBufferDirectory());
                    FileFilter filter = new FileFilter() {
                        public boolean accept(final File f) {
                            if (f.isDirectory()) {
                                return true;
                            }
                            return false;
                        }

                        // description of this filter
                        public String getDescription() {
                            return "Directory";
                        }
                    };

                    chooser.setFileFilter(filter);
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    final int returnVal = chooser.showOpenDialog(PreferencesDialog.this);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        moduleBufferTextArea.setText(chooser.getSelectedFile().getPath());
                    }
                } catch (Exception e) {
                     JOptionPane.showMessageDialog(PreferencesDialog.this, e.getMessage(), "Alert",
                     JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        builder.append("Path for generated files");
        generationPathTextArea = new JTextArea(QedeqGuiConfig.getInstance().getGenerationDirectory().getPath());
        generationPathTextArea.setEditable(false);
        final JButton chooseGenerationLocation = new JButton("Choose");
        builder.append(chooseGenerationLocation);
        builder.append(wrapWithScrollPane(generationPathTextArea), 3);
        chooseGenerationLocation.setEnabled(true);
//        chooseGenerationLocation.setBounds(33 + 600 - 90, 250 + y, 90, 21);
        chooseGenerationLocation.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                try {
                    QedeqGuiConfig.getInstance().getGenerationDirectory().mkdirs();
                    JFileChooser chooser = new JFileChooser(QedeqGuiConfig.getInstance().getGenerationDirectory());
                    FileFilter filter = new FileFilter() {
                        public boolean accept(final File f) {
                            if (f.isDirectory()) {
                                return true;
                            }
                            return false;
                        }

                        // description of this filter
                        public String getDescription() {
                            return "Directory";
                        }
                    };
                    chooser.setFileFilter(filter);
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    final int returnVal = chooser.showOpenDialog(PreferencesDialog.this);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        generationPathTextArea.setText(chooser.getSelectedFile().getPath());
                    }
                } catch (Exception e) {
                     JOptionPane.showMessageDialog(PreferencesDialog.this, e.getMessage(), "Alert",
                     JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        builder.append("Path for newly created module files");
        localModulesPathTextArea = new JTextArea(QedeqGuiConfig.getInstance().getLocalModulesDirectory().getPath());
        localModulesPathTextArea.setEditable(false);
        final JButton chooselocalModulesLocation = new JButton("Choose");
        builder.append(chooselocalModulesLocation);
        builder.append(wrapWithScrollPane(localModulesPathTextArea), 3);
        chooselocalModulesLocation.setEnabled(true);
        chooselocalModulesLocation.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                try {
                    QedeqGuiConfig.getInstance().getLocalModulesDirectory().mkdirs();
                    JFileChooser chooser = new JFileChooser(QedeqGuiConfig.getInstance().getLocalModulesDirectory());
                    FileFilter filter = new FileFilter() {
                        public boolean accept(final File f) {
                            if (f.isDirectory()) {
                                return true;
                            }
                            return false;
                        }
                        // description of this filter
                        public String getDescription() {
                            return "Directory";
                        }
                    };
                    chooser.setFileFilter(filter);
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    final int returnVal = chooser.showOpenDialog(PreferencesDialog.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        localModulesPathTextArea.setText(chooser.getSelectedFile().getPath());
                    }
                } catch (Exception e) {
                     JOptionPane.showMessageDialog(PreferencesDialog.this, e.getMessage(), "Alert",
                     JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return GuiHelper.addSpaceAndTitle(builder.getPanel(), "Paths");
    }

    /**
     * Assembles checkbox settings panel.
     *
     * @return  Created panel.
     */
    private JComponent buildBinaryOptionPanel() {
        FormLayout layout = new FormLayout(
        "left:pref");    // columns

        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        builder.getPanel().setOpaque(false);

        autoReloadLastSessionCheckedCB = new JCheckBox(" Auto loading of in last session successfully checked modules",
            QedeqGuiConfig.getInstance().isAutoReloadLastSessionChecked());
        builder.append(autoReloadLastSessionCheckedCB);

//        directResponseCB = new JCheckBox(" Direct message response for actions",
//            QedeqGuiConfig.getInstance().isDirectResponse());
//        builder.append(directResponseCB);

        automaticLogScrollCB = new JCheckBox(" Automatic Scroll of Log Window",
            QedeqGuiConfig.getInstance().isAutomaticLogScroll());
        builder.append(automaticLogScrollCB);

        autoStartHtmlBrowserCB = new JCheckBox(" Auto start web browser after HTML generation",
            QedeqGuiConfig.getInstance().isAutoStartHtmlBrowser());
        builder.append(autoStartHtmlBrowserCB);

        traceOnCB = new JCheckBox(" Enable trace output generation. Must be on to configure detailed log.",
                QedeqGuiConfig.getInstance().isTraceOn());
            builder.append(traceOnCB);

        return GuiHelper.addSpaceAndTitle(builder.getPanel(), "Miscellaneous Switches");
    }

    /**
     * Assembles GUI size settings panel.
     *
     * @return  Created panel.
     */
    private JComponent buildSizeOptionPanel() {
        FormLayout layout = new FormLayout(
        "right:pref, 5dlu, fill:50dlu");    // columns

        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        builder.getPanel().setOpaque(false);

        iconSizeCB = new JComboBox(new String[]  {"16x16", "22x22", "32x32"});
        iconSizeCB.setSelectedItem(QedeqGuiConfig.getInstance().getIconSize());
        builder.append("Icon size", iconSizeCB);
        return GuiHelper.addSpaceAndTitle(builder.getPanel(), "Sizes");
    }

    /**
     * Assembles Look and Feel settings panel.
     *
     * @return  Created panel.
     */
    private JComponent buildLookAndFeelPanel() {
        FormLayout layout = new FormLayout(
        "right:pref, 5dlu, fill:150dlu");    // columns

        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        builder.getPanel().setOpaque(false);

        lookAndFeelCB = new JComboBox(new String[] {"PlasticXP", "Plastic",
            "Plastic3D", "Windows", "Metal"});
        lookAndFeelCB.setEditable(true);
        lookAndFeelCB.setSelectedItem(QedeqGuiConfig.getInstance().getLookAndFeel());
        builder.append("Look & Feel", lookAndFeelCB);
        return GuiHelper.addSpaceAndTitle(builder.getPanel(), "Look and Feel");
    }

    /**
     * Assembles the GUI components of the panel.
     */
    public final void setupView() {
        final Container content = getContentPane();
        content.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        final JPanel allOptions = new JPanel();
        allOptions.setBorder(GuiHelper.getEmptyBorder());
        allOptions.setLayout(new BoxLayout(allOptions, BoxLayout.Y_AXIS));
        allOptions.add(buildBinaryOptionPanel());
        allOptions.add(Box.createVerticalStrut(GuiHelper.getEmptyBorderPixelsY()));
        allOptions.add(buildPathsPanel());
        allOptions.add(Box.createVerticalStrut(GuiHelper.getEmptyBorderPixelsY()));
        allOptions.add(buildTimeoutPanel());
        final JComponent proxyPanel = buildProxyPanel();
        allOptions.add(Box.createVerticalStrut(GuiHelper.getEmptyBorderPixelsY()));
        allOptions.add(buildProxyPanel());

        final JPanel graphicOptions = new JPanel();
        graphicOptions.setBorder(GuiHelper.getEmptyBorder());
        graphicOptions.setLayout(new BoxLayout(graphicOptions, BoxLayout.Y_AXIS));
        graphicOptions.add(new JLabel("Most options require a restart of the application!"));
        graphicOptions.add(buildLookAndFeelPanel());
        graphicOptions.add(Box.createVerticalStrut(GuiHelper.getEmptyBorderPixelsY()));
        graphicOptions.add(buildSizeOptionPanel());
        graphicOptions.add(Box.createVerticalGlue());

        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);
        tabbedPane.addTab("Main Options", allOptions);
        tabbedPane.addTab("Gui Options", graphicOptions);

        content.add(tabbedPane);

        content.add(GuiHelper.addSpaceAndAlignRight(createButtonPanel()));

        // let the container calculate the ideal size
        pack();

        final Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2,
            getWidth(), getHeight() + (IoUtility.isWebStarted() ? proxyPanel.getHeight()
            + 20    // TODO 20101228 m31: hard coded pixel size
            : 0));
    }

    /**
     * Create button panel.
     *
     * @return  Main buttons.
     */
    private JPanel createButtonPanel() {
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
        return buttons;
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

    boolean changed() {
        boolean result = false;
        result = result || (EqualsUtility.equals(moduleBufferTextArea.getText(),
            QedeqGuiConfig.getInstance().getBufferDirectory()));
        result = result || (EqualsUtility.equals(generationPathTextArea.getText(),
                QedeqGuiConfig.getInstance().getGenerationDirectory()));
        result = result || (EqualsUtility.equals(localModulesPathTextArea.getText(),
                QedeqGuiConfig.getInstance().getLocalModulesDirectory()));
        result = result || (automaticLogScrollCB.isSelected()
                == QedeqGuiConfig.getInstance().isAutomaticLogScroll());
//        result = result || (directResponseCB.isSelected()
//                == QedeqGuiConfig.getInstance().isDirectResponse());
        result = result || (autoReloadLastSessionCheckedCB.isSelected()
                == QedeqGuiConfig.getInstance().isAutoReloadLastSessionChecked());
        result = result || (autoStartHtmlBrowserCB.isSelected()
                == QedeqGuiConfig.getInstance().isAutoStartHtmlBrowser());
        result = result || (traceOnCB.isSelected()
                == QedeqGuiConfig.getInstance().isTraceOn());
        result = result || (EqualsUtility.equals(iconSizeCB.getSelectedItem(),
                QedeqGuiConfig.getInstance().getIconSize()));
        result = result || (EqualsUtility.equals(lookAndFeelCB.getSelectedItem(),
                QedeqGuiConfig.getInstance().getLookAndFeel()));
        if (KernelContext.getInstance().isSetConnectionTimeOutSupported()) {
            result = result || EqualsUtility.equals(connectionTimeoutTextField.getText(),
                    "" + QedeqGuiConfig.getInstance().getConnectTimeout());
        }
        if (KernelContext.getInstance().isSetReadTimeoutSupported()) {
            result = result || EqualsUtility.equals(readTimeoutTextField.getText(),
                    "" + QedeqGuiConfig.getInstance().getReadTimeout());
        }
        if (!IoUtility.isWebStarted()) {
            result = result || EqualsUtility.equals(httpProxyHostTextField.getText(),
                    QedeqGuiConfig.getInstance().getHttpProxyHost());
            result = result || EqualsUtility.equals(httpProxyPortTextField.getText(),
                    QedeqGuiConfig.getInstance().getHttpProxyPort());
            result = result || EqualsUtility.equals(httpNonProxyHostsTextField.getText(),
                    QedeqGuiConfig.getInstance().getHttpNonProxyHosts());
        }
        return result;
    }

    void save() {
        if (changed()) {
            try {
                QedeqGuiConfig.getInstance().setBufferDirectory(new File(moduleBufferTextArea.getText()));
                QedeqGuiConfig.getInstance().setGenerationDirectory(new File(generationPathTextArea.getText()));
                QedeqGuiConfig.getInstance().setLocalModulesDirectory(new File(localModulesPathTextArea.getText()));
                QedeqGuiConfig.getInstance().setAutomaticLogScroll(automaticLogScrollCB.isSelected());
//                QedeqGuiConfig.getInstance().setDirectResponse(directResponseCB.isSelected());
                QedeqGuiConfig.getInstance().setAutoReloadLastSessionChecked(
                    autoReloadLastSessionCheckedCB.isSelected());
                QedeqGuiConfig.getInstance().setAutoStartHtmlBrowser(autoStartHtmlBrowserCB.isSelected());
                QedeqGuiConfig.getInstance().setTraceOn(traceOnCB.isSelected());
                Trace.setTraceOn(traceOnCB.isSelected());   // TODO 20101122 m31: is this the correct position
                                                            // to set this flag
                if (KernelContext.getInstance().isSetConnectionTimeOutSupported()) {
                    QedeqGuiConfig.getInstance().setConnectionTimeout(Integer.parseInt(
                        connectionTimeoutTextField.getText()));
                }
                if (KernelContext.getInstance().isSetReadTimeoutSupported()) {
                    QedeqGuiConfig.getInstance().setReadTimeout(Integer.parseInt(readTimeoutTextField.getText()));
                }
                if (!IoUtility.isWebStarted()) {
                    QedeqGuiConfig.getInstance().setHttpProxyHost(httpProxyHostTextField.getText().trim());
                    QedeqGuiConfig.getInstance().setHttpProxyPort(httpProxyPortTextField.getText().trim());
                    StringBuffer httpNonProxyHosts = new StringBuffer(httpNonProxyHostsTextField.getText().trim());
                    StringUtility.replace(httpNonProxyHosts, " ", "|");
                    StringUtility.replace(httpNonProxyHosts, "\t", "|");
                    StringUtility.replace(httpNonProxyHosts, "\r", "|");
                    StringUtility.replace(httpNonProxyHosts, ",", "|");
                    StringUtility.replace(httpNonProxyHosts, ";", "|");
                    StringUtility.replace(httpNonProxyHosts, "||", "|");
                    QedeqGuiConfig.getInstance().setHttpNonProxyHosts(httpNonProxyHosts.toString());
                }
                QedeqGuiConfig.getInstance().setIconSize(String.valueOf(iconSizeCB.getSelectedItem()));
                QedeqGuiConfig.getInstance().setLookAndFeel(String.valueOf(lookAndFeelCB.getSelectedItem()));
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            }

            try {
                QedeqGuiConfig.getInstance().store();
            } catch (IOException e) {
                Trace.fatal(CLASS, this, "save", "couldn't save preferences", e);
                JOptionPane.showMessageDialog(this, "Couldn't save preferences", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}

