/* $Id: ParserPane.java,v 1.1 2007/10/07 16:39:59 m31 Exp $
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

package org.qedeq.gui.se.pane;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JViewport;

import org.qedeq.gui.se.element.CPTextArea;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.context.KernelContext;
import org.qedeq.kernel.parser.LatexMathParser;
import org.qedeq.kernel.parser.ParserException;
import org.qedeq.kernel.parser.Term;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.utility.IoUtility;
import org.qedeq.kernel.utility.ResourceLoaderUtility;
import org.qedeq.kernel.utility.TextInput;
import org.qedeq.kernel.xml.handler.parser.LoadXmlOperatorListUtility;


/**
 * View for QEDEQ XML files.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class ParserPane extends JFrame {

    private static final String SAMPLE = "x \\land (y \\lor z) \\leftrightarrow (x \\land y) \\lor "
        + "(x \\land z)\n\n\\{ x | y \\in x \\} = \\{ z | y \\in x \\}";

    private CPTextArea source = new CPTextArea(SAMPLE);

    private CPTextArea resultField = new CPTextArea();

    private CPTextArea error = new CPTextArea();

    private JScrollPane sourceScroller = new JScrollPane();

    private JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

    private JMenuBar menu = new JMenuBar();

    private final List operators;

    private int errorPosition = -1;

    private JSplitPane globalPane;


    public ParserPane() throws SourceFileExceptionList, FileNotFoundException {
        // TODO mime: hard coded window, change to FormLayout
        super("QEDEQ LaTeX Parser Sample");
        final String resourceDirectoryName = "config";
        final String resourceName = "mengenlehreMathOperators.xml";
        operators = LoadXmlOperatorListUtility.getOperatorList(
            ResourceLoaderUtility.getResourceUrl(
                KernelContext.getInstance().getConfig().getBasisDirectory(), resourceDirectoryName,
                resourceName));
        setupView();
        updateView();
    }

    public static final void main(final String[] args) throws Exception {
        final ParserPane parserPane = new ParserPane();
        parserPane.show();
        parserPane.updateView();
    }

    /**
     * Assembles the GUI components of the panel.
     */
    private final void setupView() {
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
        splitPane.setResizeWeight(0.5);
        splitPane.setOneTouchExpandable(true);

//        pane.setLayout(new BorderLayout(1, 1));
//        pane.setLayout(new FlowLayout());
//        pane.setLayout(new BorderLayout(1, 1));

        error.setText("");
//        error.setBorder(LineBorder.createBlackLineBorder());
//        pane.add(splitPane);
//        pane.add(error);

        globalPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        globalPane.setTopComponent(splitPane);
        globalPane.setBottomComponent(errorScroller);
        globalPane.setResizeWeight(1);
        globalPane.setOneTouchExpandable(true);

        pane.add(globalPane);

        addComponentListener(new ComponentAdapter() {
            public void componentHidden(final ComponentEvent e) {
                Trace.trace(this, "componentHidden", e);
            }
            public void componentShown(final ComponentEvent e) {
                Trace.trace(this, "componentShown", e);
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
            final JMenuItem transform = new JMenuItem("LaTeX to QEDEQ");
            transform.setMnemonic('Q');
            transform.addActionListener(new AbstractAction() {
                public final void actionPerformed(final ActionEvent action) {
                    resultField.setText(printMath(source.getText()));
                    updateView();
                }
            });
            transformMenu.add(transform);
        }
//*******************
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
        setJMenuBar(menu);
        setSize(600, 400);
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
        Trace.begin(this, method);

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
        final LatexMathParser parser = new LatexMathParser(input, operators);
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
            final StringBuffer pointer = IoUtility.getSpaces(input.getColumn());
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
            final StringBuffer pointer = IoUtility.getSpaces(input.getColumn());
            pointer.append('^');
            result.append(pointer);
            System.out.println(result.toString());
            error.setText(result.toString());
        }
        return out.toString();
    }



}
