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

package org.qedeq.gui.se.pane;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JViewport;

import org.qedeq.base.io.ResourceLoaderUtility;
import org.qedeq.base.io.TextInput;
import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.base.utility.YodaUtility;
import org.qedeq.gui.se.element.CPTextArea;
import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.bo.module.InternalKernelServices;
import org.qedeq.kernel.bo.parser.MathParser;
import org.qedeq.kernel.bo.parser.ParserException;
import org.qedeq.kernel.bo.parser.Term;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.xml.handler.parser.LoadXmlOperatorListUtility;


/**
 * View for QEDEQ XML files.
 *
 * @author  Michael Meyling
 */
public class ParserPane extends JFrame {

    /** This class. */
    private static final Class CLASS = ParserPane.class;

    /** Source to parse. */
    private CPTextArea source = new CPTextArea();

    /** Parse result. */
    private CPTextArea resultField = new CPTextArea();

    /** Error messages. */
    private CPTextArea error = new CPTextArea();

    /** Make source scrollable. */
    private JScrollPane sourceScroller = new JScrollPane();

    /** Split between source and result. */
    private JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

    /** Menu for copy and paste. */
    private JMenuBar menu = new JMenuBar();

    /** List of {@link org.qedeq.kernel.parser.Operator}s. */
    private final List operators;

    /** Error position within source file. */
    private int errorPosition = -1;

    /** Split between previous split and error pane. */
    private JSplitPane globalPane;

    /** File that contains operator definitions. */
    private final File resourceFile;

    /** Use this parser. */
    private final MathParser parser;


    public ParserPane(final String name, final MathParser parser, final String title,
            final String resourceName, final String sample) throws SourceFileExceptionList {
        // LATER mime 20080131: hard coded window, change to FormLayout
        super(title);
        this.parser = parser;
        source.setText(sample);
        final String resourceDirectoryName = "config";
        try {
            resourceFile = ResourceLoaderUtility.getResourceFile(
                KernelContext.getInstance().getConfig().getBasisDirectory(), resourceDirectoryName,
                resourceName).getCanonicalFile();
        } catch (final IOException e) {     // should not occur
            throw new RuntimeException(e);
        }
        // LATER mime 20100906: change name and loading mechanism (don't use source directory for file!!!)
        try {
            operators = LoadXmlOperatorListUtility.getOperatorList(
                (InternalKernelServices) YodaUtility.getFieldValue(KernelContext.getInstance(), "services"),
                resourceFile);
        } catch (NoSuchFieldException e) {  // programming error
            throw new RuntimeException(e);
        }
        setupView(name);
        updateView();
    }

    /**
     * Assembles the GUI components of the panel.
     *
     * @param   name    Name of transformation source.
     */
    private final void setupView(final String name) {
        final Container pane = getContentPane();
        source.setDragEnabled(true);
        source.setFont(new Font("monospaced", Font.PLAIN, pane.getFont().getSize()));
        source.setAutoscrolls(true);
        source.setCaretPosition(0);
        source.setEditable(true);
        source.getCaret().setVisible(false);
        source.setLineWrap(true);
        source.setWrapStyleWord(true);
        source.setFocusable(true);

        resultField.setFont(new Font("monospaced", Font.PLAIN, pane.getFont().getSize()));
        resultField.setAutoscrolls(true);
        resultField.setCaretPosition(0);
        resultField.setEditable(false);
        resultField.getCaret().setVisible(false);
        resultField.setFocusable(true);

        error.setFont(new Font("monospaced", Font.PLAIN, pane.getFont().getSize()));
        error.setForeground(Color.RED);
        error.setAutoscrolls(true);
        error.setCaretPosition(0);
        error.setEditable(false);
        error.getCaret().setVisible(false);
        error.setFocusable(true);
        error.addMouseListener(new MouseAdapter() {
            public void mouseClicked(final MouseEvent e) {
                updateView();
            }

        });

        final JViewport qedeqPort = sourceScroller.getViewport();
        qedeqPort.add(source);

        final JScrollPane resultScroller = new JScrollPane();
        final JViewport resultPort = resultScroller.getViewport();
        resultPort.add(resultField);

        final JScrollPane errorScroller = new JScrollPane();
        final JViewport errorPort = errorScroller.getViewport();
        errorPort.add(error);

        splitPane.setTopComponent(sourceScroller);
        splitPane.setBottomComponent(resultScroller);
        splitPane.setResizeWeight(0);
        splitPane.setOneTouchExpandable(true);

        error.setText("");

        globalPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        globalPane.setTopComponent(splitPane);
        globalPane.setBottomComponent(errorScroller);
        globalPane.setResizeWeight(1);
        globalPane.setOneTouchExpandable(true);

        pane.add(globalPane);

        addComponentListener(new ComponentAdapter() {
            public void componentHidden(final ComponentEvent e) {
                Trace.trace(CLASS, this, "componentHidden", e);
            }
            public void componentShown(final ComponentEvent e) {
                Trace.trace(CLASS, this, "componentShown", e);
            }
        });

        menu.removeAll();

/*
        final JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        {
            final JMenuItem transform = new JMenuItem("Load");
            transform.setMnemonic('L');
            transform.addActionListener(new AbstractAction() {
                public final void actionPerformed(final ActionEvent action) {
                }
            });
            fileMenu.add(transform);
        }
        menu.add(fileMenu);
        setJMenuBar(menu);
*/
        final JMenu transformMenu = new JMenu("Transform");
        transformMenu.setMnemonic('T');
        {
            final JMenuItem transform = new JMenuItem(name + " to QEDEQ");
            transform.setMnemonic('Q');
            transform.addActionListener(new AbstractAction() {
                public final void actionPerformed(final ActionEvent action) {
                    resultField.setText(printMath(source.getText()));
                    updateView();
                }
            });
            transformMenu.add(transform);
        }
//** LATER mime 20071230: add conversion from QEDEQ into LaTeX
        /*
        {
            final JMenuItem transform = new JMenuItem("QEDEQ to LaTeX");
            transform.setMnemonic('L');
            transform.addActionListener(new AbstractAction() {
                public final void actionPerformed(final ActionEvent action) {
                    La
                    System.out.println(qedeq.getText());
                    result.setText(printMath(qedeq.getText()));
                    updateView();
                }
            });
            transformMenu.add(transform);
        }
*/
        {
            final JMenuItem doSwitch = new JMenuItem("switch content");
            doSwitch.setMnemonic('S');
            doSwitch.addActionListener(new AbstractAction() {
                public final void actionPerformed(final ActionEvent action) {
                    final String text = source.getText();
                    source.setText(resultField.getText());
                    resultField.setText(text);
                    updateView();
                }
            });
            transformMenu.add(doSwitch);
        }
        menu.add(transformMenu);

        final JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        {
            final JMenuItem about = new JMenuItem("About");
            about.setMnemonic('A');
            about.addActionListener(new AbstractAction() {
                public final void actionPerformed(final ActionEvent action) {
                    JOptionPane.showMessageDialog(ParserPane.this,
                        "This dialog enables to transform textual input into the QEDEQ format.\n"
                        + "The operators and their QEDEQ counterparts are defined int the file:\n"
                        + resourceFile,
                        "About", JOptionPane.INFORMATION_MESSAGE);
                }
            });
            helpMenu.add(about);
        }
        menu.add(helpMenu);

        setJMenuBar(menu);
        setSize(500, 800);
    }

    /**
     * Set line wrap.
     *
     * @param   wrap    Line wrap?
     */
    public void setLineWrap(final boolean wrap) {
        source.setLineWrap(wrap);
    }

    /**
     * Get line wrap.
     *
     * @return  Line wrap?
     */
    public boolean getLineWrap() {
        return source.getLineWrap();
    }

    /**
     * Update from model.
     */
    public synchronized void updateView() {
        final String method = "updateView()";
        Trace.begin(CLASS, this, method);

        if (errorPosition >= 0) {
            // reserve 3 text lines for error description
            globalPane.setDividerLocation(globalPane.getHeight()
                - globalPane.getDividerSize()
                - error.getFontMetrics(error.getFont()).getHeight() * 3 - 4);
        } else {
            error.setText("");
            globalPane.setDividerLocation(this.getHeight());
        }
        this.repaint();
    }



// not used any longer
/*
    private final int findCaretPosition(final int i) {
        if (i == 1) {
            return 0;
        }
        final String s = qedeq.getText();
        int j = 0;
        int k = 0;
        for (; j < s.length(); j++) {
            if (s.charAt(j) == '\n') {
                k++;
            }
            if (k == i - 1) {
                return j + 1;
            }
        }
        return 0;
    }
*/

    private String printMath(final String text)  {
        final StringBuffer buffer = new StringBuffer(text);
        final TextInput input = new TextInput(buffer);
        parser.setParameters(text, operators);
        final StringBuffer out = new StringBuffer();
        errorPosition = -1;
        try {
            Term term = null;
            do {
                term = parser.readTerm();
                if (term != null) {
                    out.append(term.getQedeqXml()).append("\n");
                    System.out.println(term.getQedeqXml());
                }
            } while (term != null || !parser.eof());
        } catch (ParserException e) {
            e.printStackTrace(System.out);
            final StringBuffer result = new StringBuffer();
            errorPosition = input.getPosition();
            result.append(input.getRow() + ":" + input.getColumn() + ":" + "\n");
            result.append(e.getMessage() + "\n");
            result.append(input.getLine().replace('\t', ' ').replace('\015', ' ') + "\n");
            final StringBuffer pointer = StringUtility.getSpaces(input.getColumn());
            pointer.append('^');
            result.append(pointer);
            System.out.println(result.toString());
            error.setText(result.toString());
        } catch (Exception e) {
            e.printStackTrace(System.out);
            final StringBuffer result = new StringBuffer();
            errorPosition = input.getPosition();
            result.append(input.getRow() + ":" + input.getColumn() + ":" + "\n");
            result.append(e.getMessage() + "\n");
            result.append(input.getLine().replace('\t', ' ').replace('\015', ' ') + "\n");
            final StringBuffer pointer = StringUtility.getSpaces(input.getColumn());
            pointer.append('^');
            result.append(pointer);
            System.out.println(result.toString());
            error.setText(result.toString());
        }
        return out.toString();
    }



}
