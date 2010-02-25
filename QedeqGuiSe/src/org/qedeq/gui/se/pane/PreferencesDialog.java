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
import java.awt.Dimension;
import java.awt.FlowLayout;
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
    private JTextField connectionTimeout;

    /** Timeout for reading from a TCP/IP connection.*/
    private JTextField readTimeout;

    /** Automatic scroll of log pane.*/
    private JCheckBox automaticLogScrollCB;

    /** Automatic reload of all modules that were successfully checked in last session.*/
    private JCheckBox autoReloadLastSessionCheckedCB;

    /** Response with a message box.*/
    private JCheckBox directResponseCB;

    /** Automatic start of default HTML browser after HTML generation.*/
    private JCheckBox autoStartHtmlBrowserCB;

    /** QEDEQ module buffer directory.*/
    private JTextField moduleBufferTextField;

    /** Generation directory. */
    private JTextField generationPathTextField;

    /** Directory for new local modules.*/
    private JTextArea localModulesPathTextField;

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

    /** Flag for generating old HTML code.*/
    private boolean oldHtmlCode;

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
        connectionTimeout = createTextField(""
            + QedeqGuiConfig.getInstance().getConnectTimeout(), true);
        builder.append(connectionTimeout);

        builder.append("Read Timeout");
        readTimeout = createTextField(""
            + QedeqGuiConfig.getInstance().getReadTimeout(), true);
        builder.append(readTimeout);
        return addSpaceAndTitle(builder.getPanel(), "Timeouts");
    }

    /**
     * Assembles Timeout settings panel.
     */
    private JComponent buildPathsPanel() {
        FormLayout layout = new FormLayout(
            "left:pref");    // columns

        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        builder.getPanel().setOpaque(false);

        builder.append("Path for newly created module files");
        localModulesDirectory = QedeqGuiConfig.getInstance().getLocalModulesDirectory();
        localModulesPathTextField = new JTextArea(localModulesDirectory.getAbsolutePath());
        localModulesPathTextField.setEditable(false);
        builder.append(wrapWithScrollPane(localModulesPathTextField));

        builder.append("Path for newly created module files");
        localModulesDirectory = QedeqGuiConfig.getInstance().getLocalModulesDirectory();
        localModulesPathTextField = new JTextArea(localModulesDirectory.getAbsolutePath());
        localModulesPathTextField.setEditable(false);
        builder.append(wrapWithScrollPane(localModulesPathTextField));

        return addSpaceAndTitle(builder.getPanel(), "Paths");
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

        autoReloadLastSessionCheckedCB = new JCheckBox(" Auto loading of in last session successfully checked modules",
            QedeqGuiConfig.getInstance().isAutoReloadLastSessionChecked());
        autoReloadLastSessionCheckedCB.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                PreferencesDialog.this.autoReloadLastSessionChecked
                    = PreferencesDialog.this.autoReloadLastSessionCheckedCB.isSelected();
                PreferencesDialog.this.changed = true;
            }
        });
        builder.append(autoReloadLastSessionCheckedCB);

        directResponseCB = new JCheckBox(" Direct message response for actions",
            QedeqGuiConfig.getInstance().isDirectResponse());
        directResponseCB.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                PreferencesDialog.this.directResponse = PreferencesDialog.this.directResponseCB.isSelected();
                PreferencesDialog.this.changed = true;
            }
        });
        builder.append(directResponseCB);

        automaticLogScrollCB = new JCheckBox(" Automatic Scroll of Log Window",
            QedeqGuiConfig.getInstance().isAutomaticLogScroll());
        automaticLogScrollCB.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                PreferencesDialog.this.automaticLogScroll
                    = PreferencesDialog.this.automaticLogScrollCB.isSelected();
                PreferencesDialog.this.changed = true;
            }
        });
        builder.append(automaticLogScrollCB);

        autoStartHtmlBrowserCB = new JCheckBox(" Auto start web browser after HTML generation",
            QedeqGuiConfig.getInstance().isAutoStartHtmlBrowser());
        builder.append(autoStartHtmlBrowserCB);
/*
        builder.append("Rule Version");
        ruleVersion = createTextField("", false);
        builder.append(ruleVersion);

        builder.append("URL");
//        url = createTextField("", false);
        url = new JTextArea();
        url.setEditable(false);
        url.setLineWrap(false);
        builder.append(wrapWithScrollPane(url));
//        builder.append(url);
*/
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
        JPanel withSpace= new JPanel();
        withSpace.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // FIXME dynamize
        withSpace.add(panel);
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
        JPanel withSpace= new JPanel();
        withSpace.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // FIXME dynamize
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
        Border paneEdge = BorderFactory.createEmptyBorder(0,10,10,10); // FIXME dynamize
        JPanel allOptions = new JPanel();
        allOptions.setBorder(paneEdge);
        allOptions.setLayout(new BoxLayout(allOptions, BoxLayout.Y_AXIS));
        allOptions.add(buildBinaryOptionPanel());
        allOptions.add(buildPathsPanel());
        allOptions.add(buildTimeoutPanel());
        add(allOptions);

        ButtonBarBuilder builder = ButtonBarBuilder.createLeftToRightBuilder();

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

        builder.addGriddedButtons( new JButton[] {cancel, ok});

        final JPanel buttons = builder.getPanel();
        add(addSpaceAndAlignRight(buttons));

        setPreferredSize(new Dimension(400, 400));
        pack();
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize ();
        setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2);
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
        return new JScrollPane(
            c,
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
            QedeqGuiConfig.getInstance().setOldHtml(oldHtmlCode);
            try {
                QedeqGuiConfig.getInstance().store();
            } catch (IOException e) {
                Trace.fatal(CLASS, this, "save", "couldn't save preferences", e);
            }
        }
    }

}
