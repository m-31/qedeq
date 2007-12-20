// $Id: Preferences.java,v 1.1 2007/10/07 16:39:59 m31 Exp $
//
// This file is part of the program suite "Principia Mathematica II"
// which is a working prototype for the main project:
// "Hilbert II" - http://www.qedeq.org
//
// Copyright 2001-2004  Michael Meyling <mime@qedeq.org>.
//
// "Principia Mathematica II" is free software; you can redistribute
// it and/or modify it under the terms of the GNU General Public
// License as published by the Free Software Foundation; either
// version 2 of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.

package org.qedeq.gui.se.pane;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import org.qedeq.kernel.trace.Trace;


/**
 * Show and edit preferences of this application.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public final class Preferences extends JFrame {

    /** Automatic scroll of log pane. */
    private JCheckBox automaticLogScrollCB;

    /** Automatic reload of all modules that were successfully checked in last session. */
    private JCheckBox autoReloadLastSessionCheckedCB;

    /** Response with a message box. */
    private JCheckBox directResponseCB;

    /** Automatic start of default HTML browser after HTML generation. */
    private JCheckBox autoStartHtmlBrowserCB;

    /** Generate old HTML code. */
    private JCheckBox oldHtmlCodeCB;

    /** Label for QEDEQ module buffer directory. */
    private JLabel moduleBufferLabel;

    /** QEDEQ module buffer directory. */
    private JTextField moduleBufferTextField;

    /** Label for generation directory. */
    private JLabel generationPathLabel;

    /** Generation directory. */
    private JTextField generationPathTextField;

    /** Label for directory for new local modules. */
    private JLabel localModulesPathLabel;

    /** Directory for new local modules. */
    private JTextField localModulesPathTextField;

    /** Local QEDEQ module buffer directory. */
    private File bufferDirectory;

    /** Generation directory. */
    private File generationDirectory;

    /** Directory for new local modules. */
    private File localModulesDirectory;

    /** Flag for automatic scroll of log window. */
    private boolean automaticLogScroll;

    /** Flag for automatic reload of all QEDEQ modules that were successfully loaded in the last
     * session. */
    private boolean autoReloadLastSessionChecked;

    /** Flag for automatic start of the default HTML browser after HTML generation. */
    private boolean autoStartHtmlBrowser;

    /** Flag for direct message box response mode. */
    private boolean directResponse;

    /** Flag for generating old HTML code. */
    private boolean oldHtmlCode;

    /** Internal flag for remembering if any value changed. */
    private boolean changed;


    /**
     * Constructor.
     */
    public Preferences(final String title) {
        super(title);
        final String method = "Constructor";
        Trace.begin(this, method);
        try {
            changed = false;
            setupView();
            pack();
            setSize(660, 490);
        } catch (Throwable e) {
            Trace.trace(this, method, e);
        } finally {
            Trace.end(this, method);
        }
    }


    /**
     * Assembles the GUI components of the panel.
     */
    public final void setupView() {
        final String method = "setupView";
        final Container contents = getContentPane();

        contents.setLayout(null);

        int y = 0;

        automaticLogScroll
            = QedeqGuiConfig.getInstance().isAutomaticLogScroll();
        automaticLogScrollCB
            = new JCheckBox(" Automatic Scroll of Log Window",
            automaticLogScroll);
        contents.add(automaticLogScrollCB);
        automaticLogScrollCB.setBounds(33, 20 + y, 400, 17);
        automaticLogScrollCB.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                Preferences.this.automaticLogScroll
                    = Preferences.this.automaticLogScrollCB.isSelected();
                Preferences.this.changed = true;
            }
        });

        y += 40;

        autoReloadLastSessionChecked
            = QedeqGuiConfig.getInstance().isAutoReloadLastSessionChecked();
        autoReloadLastSessionCheckedCB
            = new JCheckBox(" Auto loading of in last session successfully checked modules",
            autoReloadLastSessionChecked);
        contents.add(autoReloadLastSessionCheckedCB);
        autoReloadLastSessionCheckedCB.setBounds(33, 20 + y, 400, 17);
        autoReloadLastSessionCheckedCB.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                Preferences.this.autoReloadLastSessionChecked
                    = Preferences.this.autoReloadLastSessionCheckedCB.isSelected();
                Preferences.this.changed = true;
            }
        });

        directResponse = QedeqGuiConfig.getInstance().isDirectResponse();
        directResponseCB = new JCheckBox(" Direct message response for actions",
            directResponse);
        contents.add(directResponseCB);
        directResponseCB.setBounds(33, 60 + y, 400, 17);
        directResponseCB.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                Preferences.this.directResponse = Preferences.this.directResponseCB.isSelected();
                Preferences.this.changed = true;
            }
        });

        autoStartHtmlBrowser = QedeqGuiConfig.getInstance().isAutoStartHtmlBrowser();
        autoStartHtmlBrowserCB = new JCheckBox(" Auto start web browser after HTML generation",
            autoStartHtmlBrowser);
        contents.add(autoStartHtmlBrowserCB);
        autoStartHtmlBrowserCB.setBounds(33, 100 + y, 400, 17);
        autoStartHtmlBrowserCB.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                Preferences.this.autoStartHtmlBrowser
                    = Preferences.this.autoStartHtmlBrowserCB.isSelected();
                Preferences.this.changed = true;
            }
        });

        oldHtmlCode = QedeqGuiConfig.getInstance().isOldHtml();
        oldHtmlCodeCB = new JCheckBox(" Use System font HTML at web browser start",
            oldHtmlCode);
        contents.add(oldHtmlCodeCB);
        oldHtmlCodeCB.setBounds(33, 140 + y, 400, 17);
        oldHtmlCodeCB.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                Preferences.this.oldHtmlCode = Preferences.this.oldHtmlCodeCB.isSelected();
                Preferences.this.changed = true;
            }
        });

        moduleBufferLabel = new JLabel("Local file path for QEDEQ module buffer");
        contents.add(moduleBufferLabel);
        moduleBufferLabel.setBounds(33, 180 + y, 400, 17);

        bufferDirectory = new File(QedeqGuiConfig.getInstance().getBufferDirectory());
        moduleBufferTextField = new JTextField(bufferDirectory.getAbsolutePath());
        moduleBufferTextField.setEditable(false);
        contents.add(moduleBufferTextField);
        moduleBufferTextField.setBounds(33, 210 + y, 600, 21);

        final JButton chooseBufferLocation = new JButton("Choose");
        chooseBufferLocation.setEnabled(false); // TODO mime 200700903: setting still unsupported
        contents.add(chooseBufferLocation);
        chooseBufferLocation.setBounds(33 + 600 - 90, 180 + y, 90, 21);

        chooseBufferLocation.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                try {
                    Trace.trace(this, method, "mkdirs="
                        + new Boolean(Preferences.this.bufferDirectory.mkdirs()));
                    JFileChooser chooser = new JFileChooser(Preferences.this.bufferDirectory);
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
                    final int returnVal = chooser.showOpenDialog(Preferences.this);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        moduleBufferTextField.setText(chooser.getSelectedFile().getPath());
                        Preferences.this.bufferDirectory = chooser.getSelectedFile();
                        Preferences.this.changed = true;
                    }
                } catch (Exception e) {
                     JOptionPane.showMessageDialog(Preferences.this, e.getMessage(), "Alert",
                     JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        generationPathLabel = new JLabel("Path for generated files");
        contents.add(generationPathLabel);
        generationPathLabel.setBounds(33, 250 + y, 400, 17);

        generationDirectory = new File(QedeqGuiConfig.getInstance().getGenerationDirectory());
        generationPathTextField = new JTextField(generationDirectory.getAbsolutePath());
        generationPathTextField.setEditable(false);
        contents.add(generationPathTextField);
        generationPathTextField.setBounds(33, 280 + y, 600, 21);

        final JButton chooseGenerationLocation = new JButton("Choose");
        chooseGenerationLocation.setEnabled(false); //TODO mime 200700903: setting still unsupported
        contents.add(chooseGenerationLocation);
        chooseGenerationLocation.setBounds(33 + 600 - 90, 250 + y, 90, 21);

        chooseGenerationLocation.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                try {
                    Trace.trace(this, method, "mkdirs="
                        + new Boolean(Preferences.this.generationDirectory.mkdirs()));
                    JFileChooser chooser = new JFileChooser(Preferences.this.generationDirectory);
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
                    final int returnVal = chooser.showOpenDialog(Preferences.this);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        generationPathTextField.setText(chooser.getSelectedFile().getPath());
                        Preferences.this.generationDirectory = chooser.getSelectedFile();
                        Preferences.this.changed = true;
                    }
                } catch (Exception e) {
                     JOptionPane.showMessageDialog(Preferences.this, e.getMessage(), "Alert",
                     JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        localModulesPathLabel = new JLabel("Path for newly created module files");
        contents.add(localModulesPathLabel);
        localModulesPathLabel.setBounds(33, 310 + y, 400, 17);

        localModulesDirectory = new File(QedeqGuiConfig.getInstance().getLocalModulesDirectory());
        localModulesPathTextField = new JTextField(localModulesDirectory.getAbsolutePath());
        localModulesPathTextField.setEditable(false);
        contents.add(localModulesPathTextField);
        localModulesPathTextField.setBounds(33, 340 + y, 600, 21);

        final JButton chooselocalModulesLocation = new JButton("Choose");

//      TODO mime 200700903: setting still unsupported
        chooselocalModulesLocation.setEnabled(false);
        contents.add(chooselocalModulesLocation);
        chooselocalModulesLocation.setBounds(33 + 600 - 90, 310 + y, 90, 21);

        chooselocalModulesLocation.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                try {
                    Trace.trace(this, method, "mkdirs="
                        + new Boolean(Preferences.this.localModulesDirectory.mkdirs()));
                    JFileChooser chooser = new JFileChooser(Preferences.this.localModulesDirectory);
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
                    final int returnVal = chooser.showOpenDialog(Preferences.this);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        localModulesPathTextField.setText(chooser.getSelectedFile().getPath());
                        Preferences.this.localModulesDirectory = chooser.getSelectedFile();
                        Preferences.this.changed = true;
                    }
                } catch (Exception e) {
                     JOptionPane.showMessageDialog(Preferences.this, e.getMessage(), "Alert",
                     JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        final JButton cancel = new JButton("Cancel");
        contents.add(cancel);
        cancel.setBounds(33 + 600 - 190, 390 + y, 90, 21);
        cancel.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                Preferences.this.dispose();
            }
        });

        final JButton ok = new JButton("OK");
        contents.add(ok);
        ok.setBounds(33 + 600 - 90, 390  + y, 90, 21);
        ok.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                Preferences.this.save();
                Preferences.this.dispose();
            }
        });

    }


    final void save() {
        if (changed) {
// TODO mime 20070903: setting still unsupported
//            QedeqGuiConfig.getInstance().setBufferDirectory(bufferDirectory);
//            QedeqGuiConfig.getInstance().setGenerationDirectory(generationDirectory);
//            QedeqGuiConfig.getInstance().setLocalModulesDirectory(localModulesDirectory);
            QedeqGuiConfig.getInstance().setAutomaticLogScroll(automaticLogScroll);
            QedeqGuiConfig.getInstance().setDirectResponse(directResponse);
            QedeqGuiConfig.getInstance().setAutoReloadLastSessionChecked(
                autoReloadLastSessionChecked);
            QedeqGuiConfig.getInstance().setAutoStartHtmlBrowser(autoStartHtmlBrowser);
            QedeqGuiConfig.getInstance().setOldHtml(oldHtmlCode);
        }
    }


}
