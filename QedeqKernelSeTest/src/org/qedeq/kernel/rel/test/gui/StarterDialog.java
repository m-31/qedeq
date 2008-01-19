/* $Id: StarterDialog.java,v 1.3 2007/12/21 23:35:17 m31 Exp $
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

package org.qedeq.kernel.rel.test.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

import org.qedeq.kernel.bo.save.Xml2Xml;
import org.qedeq.kernel.common.SourceFileException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.rel.test.text.KernelFacade;
import org.qedeq.kernel.rel.test.text.Xml2Latex;
import org.qedeq.kernel.rel.test.text.Xml2Wiki;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.utility.IoUtility;


/**
 * Show and edit preferences of this application.
 *
 * @version $Revision: 1.3 $
 * @author  Michael Meyling
 */
public final class StarterDialog extends JFrame {

    /** This class. */
    private static final Class CLASS = StarterDialog.class;

    /** Width for big components inside this dialog. */
    private static final int CONTENTS_WIDTH = 600;

    /** Height for components inside this dialog. */
    private static final int CONTENT_HEIGHT = 17;

    /** X margin. */
    private static final int MARGIN_X = 33;

    /** Parameter list for this dialog. */
    private final ParameterList parameterList;
    
    private JTextArea result;

    /** Current y height. */
    private int y;

    private Parameter kind;

    private Parameter from;

    private Parameter language;

    private Parameter level;

    private File configFile;

    private SourceFileException errorPosition;

    private JButton edit;

    /**
     * Constructor.
     *
     * @param   title           Dialog title.
     * @param   configLocation  path of config file. 
     */
    public StarterDialog(final String title, final String configLocation) {
        super(title);
        final String method = "StarterDialog(String, ParameterList)";
        parameterList = new ParameterList();
        final List list = new ArrayList();
        list.add("tex");
        list.add("wiki");
        list.add("txt");
        list.add("xml");
        kind = new Parameter("kind", "Result Type",
            "Resulting file(s) shall be of this type.", "tex", "tex", list);
        parameterList.add(kind);
        from = new Parameter("from", "Qedeq XML File", File.class,
            "Convert this XML file into LaTeX.", "sample/qedeq_basic_concept.xml");
        parameterList.add(from);
        language = new Parameter("language", "Language Filter", String.class,
            "Filter for this language. (\"en\" and maybe \"de\" occur in the sample texts.)", "en");
        parameterList.add(language);
        level = new Parameter("level", "Level Filter", String.class,
                    "Filter for this level (not supported yet).", "1");
        configFile = new File(configLocation);
        try {
            parameterList.fill(configFile);
        } catch (IOException e) {
            Trace.trace(CLASS, this, method, e);
        }
        try {
            Trace.begin(CLASS, this, method);
            setupView();
            // the following statements are only here to enable a simple
            //  refresh for the parameter list, to reset default values
            pack();
            setSize(2 * MARGIN_X + CONTENTS_WIDTH, y);
        } catch (Throwable e) {
            Trace.trace(CLASS, this, method, e);
            e.printStackTrace();
        } finally {
            Trace.end(CLASS, this, method);
        }
    }

    /**
     * Assembles the GUI components of the panel.
     */
    public final void setupView() {
        int startY = 21;
        y = startY;
        int deltay = 40;

        final Container contents = getContentPane();
        contents.removeAll();
        contents.setLayout(null);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent e) {
                shutdown();
            }
        });

        for (int i = 0; i < parameterList.size(); i++) {
            final Parameter parameter = parameterList.get(i);
            if (Boolean.class.equals(parameter.getType())) {
                addCheckBox(parameter);
                y += deltay;
            } else if (String.class.equals(parameter.getType())) {
                addTextField(parameter);
                y += deltay;
            } else if (File.class.equals(parameter.getType())) {
                addFileSelector(parameter);
                y += deltay;
            } else if (List.class.equals(parameter.getType())) {
                addListField(parameter);
                y += deltay;
            }
        }

        result = new JTextArea();
        result.setToolTipText("Shows transformation result. If here is any error described just a "
            + "click on this field opens an viewer with the error location.");
        result.setFont(new Font("monospaced", Font.PLAIN, result.getFont().getSize()));
        result.setAutoscrolls(true);
        result.setCaretPosition(0);
        result.setEditable(false);
        result.getCaret().setVisible(false);
        result.setFocusable(true);
        result.setForeground(Color.RED);
        result.addMouseListener( new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                openTextFileAtPosition();
            }

        });
        
        final JScrollPane scroller = new JScrollPane(result);
        scroller.setBounds(MARGIN_X, y, CONTENTS_WIDTH, CONTENT_HEIGHT * 5);
        contents.add(scroller);

        y += CONTENT_HEIGHT * 5 + deltay;
        
        y += deltay;

        final JButton dflt = new JButton("Default");
        contents.add(dflt);
        dflt.setBounds(MARGIN_X, y, 90, 21);
        dflt.setToolTipText("Resets all parameters to default values.");
        dflt.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                parameterList.resetToDefaultValues();
                StarterDialog.this.setupView();
            }
        });

        final JButton parser = new JButton("Formula Parser");
        contents.add(parser);
        parser.setBounds(MARGIN_X + CONTENTS_WIDTH - 290 - 8 * 21, y, 130, 21);
        parser.setToolTipText("Parser formula into QEDEQ format. "
            + "Converts LaTeX formulas into QEDEQ XML.");
        parser.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                final ParserPane parserPane;
                try {
                    parserPane = new ParserPane();
                } catch (SourceFileExceptionList e) {
                    Trace.trace(CLASS, this, "actionPerformed", e);
                    return;
                } catch (FileNotFoundException e) {
                    Trace.trace(CLASS, this, "actionPerformed", e);
                    return;
                }
                parserPane.show();
                parserPane.updateView();
            }
        });

        edit = new JButton("Edit");
        contents.add(edit);
        edit.setEnabled(false);
        edit.setBounds(MARGIN_X + CONTENTS_WIDTH - 290 - 21, y, 90, 21);
        edit.setToolTipText("Edit file. Only enabled if an transformation failed.");
        edit.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                openTextFileAtPosition();
            }
        });

        final JButton ok = new JButton("Start");
        contents.add(ok);
        ok.setBounds(MARGIN_X + CONTENTS_WIDTH - 190, y, 90, 21);
        ok.setToolTipText("Starts the application.");
        ok.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                final String method ="actionPerformed";
                try {
                    setResultMessage(true, "generating");
                    // LATER 20070415: this line is necessary because we still don't
                    // know if a file (or web) QEDEQ module has changed its content
                    KernelFacade.getKernelContext().removeAllModules();
                    if ("tex".equals(kind.getStringValue())) {
                        final String generated = Xml2Latex.generate(from.getFileValue(), null, 
                            language.getStringValue(),
                            level.getStringValue());
                        Trace.trace(CLASS, this, method, "successfully generated: " + generated);
                        setResultMessage(true, "successfully generated:\n\t" + generated);
                    } else if ("wiki".equals(kind.getStringValue())) {
                        Xml2Wiki.generate(from.getFileValue(), null, 
                            language.getStringValue(),
                            level.getStringValue());
                        Trace.trace(CLASS, this, method, "successfully generated files");
                        setResultMessage(true, "successfully generated files");
                    } else if ("xml".equals(kind.getStringValue())) {
                        Xml2Xml.generate(from.getFileValue(), null);
                        Trace.trace(CLASS, this, method, "successfully generated files");
                        setResultMessage(true, "successfully generated files");
                    } else {
                        Trace.trace(CLASS, this, method, "format not yet supported: " 
                            + kind.getCurrentStringValue());
                        setResultMessage(false, "format not yet supported:\n\t" 
                            + kind.getCurrentStringValue());
                    }
                } catch (final SourceFileExceptionList e) {
                    Trace.trace(CLASS, this, method, e);
                    if (e.size() > 0) {
                        errorPosition = e.get(0);   // TODO mime 20070323: handle other positions too
                        setResultMessage(false, errorPosition.getDescription(
                            from.getFileValue()));
                    } else {
                        errorPosition = new SourceFileException(from.getFileValue(), e);
                        setResultMessage(false, errorPosition.getDescription(
                            from.getFileValue()));
                    }
                } catch (final Exception e) {
                    Trace.trace(CLASS, this, method, e);
                    errorPosition = new SourceFileException(from.getFileValue(), e);
                    setResultMessage(false, errorPosition.getDescription(
                        from.getFileValue()));
                } catch (final Error e) {
                    Trace.trace(CLASS, this, method, e);
                    shutdown();
                }
    
                // save parameters
                Trace.trace(Xml2OtherGui.class, method, "saving parameters");
                IoUtility.createNecessaryDirectories(configFile);
                try {
                    parameterList.save(configFile);
                } catch (IOException e) {
                    Trace.trace(Xml2OtherGui.class, method, e);
                }
            }
        });

        final JButton cancel = new JButton("Exit");
        contents.add(cancel);
        cancel.setBounds(MARGIN_X + CONTENTS_WIDTH - 90, y, 90, 21);
        cancel.setToolTipText("Exits the application.");
        cancel.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                shutdown();
            }
        });
        y += deltay;
        
        y += 21;
    }

    private void setResultMessage(final boolean ok, final String message) {
        if (ok) {
            result.setForeground(Color.GREEN);
        } else {
            result.setForeground(Color.RED);
        }
        result.setText(message);
        result.setCaretPosition(0);
        edit.setEnabled(!ok);
    }
    
    private void setEmptyResultMessage() {
        result.setForeground(Color.BLACK);
        result.setText("");
        result.setCaretPosition(0);
        errorPosition = null;
    }    
    
    /**
     * Add file selector for file parameter.
     *
     * @param   parameter   Add file selector for this parameter.
     */
    private void addFileSelector(final Parameter parameter) {
        final Container contents = getContentPane();
        final JLabel label = new JLabel(parameter.getLabel());
        contents.add(label);
        label.setBounds(MARGIN_X, y, CONTENTS_WIDTH, CONTENT_HEIGHT);
        y += CONTENT_HEIGHT;

        final CPTextField textField = new CPTextField();
        if (parameter.getFileValue() != null) {
            try {
                textField.setText(parameter.getFileValue().getCanonicalPath());
            } catch (IOException e) {
                textField.setText(parameter.getStringValue());
            }
        }
        textField.setEditable(false);
        textField.setBounds(MARGIN_X, y, CONTENTS_WIDTH - 100, CONTENT_HEIGHT);
        textField.setToolTipText(parameter.getComment());
        contents.add(textField);

        final JButton choose = new JButton("Choose");
        contents.add(choose);
        choose.setBounds(MARGIN_X + CONTENTS_WIDTH - 90, y, 90, CONTENT_HEIGHT);
        choose.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                try {
                    setEmptyResultMessage();
                    File file = parameter.getFileValue();
                    if (!file.exists()) {
                        file = new File(".");
                    }
                    JFileChooser chooser = new JFileChooser(new File(file.getCanonicalPath()));

                    // TODO mime 20050205: because this makes this starter dialog special
                    FileFilter filter = new FileFilter() {
                        public boolean accept(final File f) {
                            if (f.isDirectory()) {
                                return true;
                            } else if (f.getName().toLowerCase().endsWith(".xml")) {
                                return true;
                            }
                            return false;
                        }

                        // description of this filter
                        public String getDescription() {
                            return "XML files";
                        }
                    };
                    chooser.setFileFilter(filter);
                    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    // TODO mime 20050205: end of Q & D
                    final int returnVal = chooser.showOpenDialog(StarterDialog.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        textField.setText(chooser.getSelectedFile().getAbsolutePath());
                        parameter.setStringValue(IoUtility.createRelativePath(new File("."),
                            chooser.getSelectedFile()));
                    }
                } catch (Exception e) {
                     JOptionPane.showMessageDialog(StarterDialog.this, e.getMessage(), "Alert",
                     JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * Add text field for string parameter.
     *
     * @param   parameter   Add text field selector for this parameter.
     */
    private void addTextField(final Parameter parameter) {
        final Container contents = getContentPane();
        final JLabel label = new JLabel(parameter.getLabel());
        contents.add(label);
        label.setBounds(MARGIN_X, y, CONTENTS_WIDTH, CONTENT_HEIGHT);
        y += CONTENT_HEIGHT;
        final CPTextField textField = new CPTextField();
        if (parameter.getStringValue() != null) {
            textField.setText(parameter.getStringValue());
        }
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                setEmptyResultMessage();
                parameter.setValue(textField.getText());
            }
        });
        textField.getDocument().addDocumentListener(new DocumentListener() {

            public void changedUpdate(final DocumentEvent e) {
                setEmptyResultMessage();
                parameter.setValue(textField.getText());
            }

            public void insertUpdate(final DocumentEvent e) {
                setEmptyResultMessage();
                parameter.setValue(textField.getText());
            }

            public void removeUpdate(final DocumentEvent e) {
                setEmptyResultMessage();
                parameter.setValue(textField.getText());
            }

        });
        contents.add(textField);
//      TODO mime 20050205: just Q & D:
        textField.setBounds(MARGIN_X, y, CONTENTS_WIDTH / 6, CONTENT_HEIGHT);
        textField.setToolTipText(parameter.getComment());
    }

    /**
     * Add combo box field for list parameter.
     *
     * @param   parameter   Add combo box for this parameter.
     */
    private void addListField(final Parameter parameter) {
        final Container contents = getContentPane();
        final JLabel label = new JLabel(parameter.getLabel());
        contents.add(label);
        label.setBounds(MARGIN_X, y, CONTENTS_WIDTH, CONTENT_HEIGHT);
        y += CONTENT_HEIGHT;
        final Vector vector = new Vector(parameter.getList());
        final JComboBox comboBox = new JComboBox(vector);
        if (parameter.getStringValue() != null) {
            comboBox.setSelectedItem(parameter.getStringValue());
        }
        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                setEmptyResultMessage();
                System.out.println("selected: " + comboBox.getSelectedItem().toString());
                parameter.setValue(comboBox.getSelectedItem().toString());
            }
        });
        contents.add(comboBox);
        // TODO mime 20050205: just Q & D
        comboBox.setBounds(MARGIN_X, y, CONTENTS_WIDTH / 6, CONTENT_HEIGHT);
        comboBox.setToolTipText(parameter.getComment());
    }

    /**
     * Add check box for boolean parameter.
     *
     * @param   parameter   Add check box for this parameter.
     */
    private void addCheckBox(final Parameter parameter) {
        final Container contents = getContentPane();
        final JCheckBox checkBox = new JCheckBox(" " + parameter.getLabel());
        checkBox.setSelected(Boolean.TRUE.equals(parameter.getValue()));
        checkBox.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                setEmptyResultMessage();
                parameter.setValue(Boolean.valueOf(checkBox.isSelected()));
            }
        });
        contents.add(checkBox);
        checkBox.setBounds(MARGIN_X, y, CONTENTS_WIDTH, CONTENT_HEIGHT);
        checkBox.setToolTipText(parameter.getComment());
    }

    private void shutdown() {
        final String method = "shutdown()";
        dispose();
        Trace.trace(CLASS, this, method, "calling System.exit");
        System.exit(0);
    }
    
    private void openTextFileAtPosition() {
        if (errorPosition != null && errorPosition.getSourceArea() != null) {
            final QedeqPane qedeq = new QedeqPane(errorPosition);
            qedeq.show();
            qedeq.updateView();
        }
    }

}
