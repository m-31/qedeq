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
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import javax.xml.parsers.ParserConfigurationException;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.io.ResourceLoaderUtility;
import org.qedeq.base.io.StringOutput;
import org.qedeq.base.io.TextInput;
import org.qedeq.base.io.TextOutput;
import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.base.utility.YodaUtility;
import org.qedeq.gui.se.element.CPTextArea;
import org.qedeq.gui.se.util.GuiHelper;
import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.bo.module.InternalKernelServices;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.ModuleLabels;
import org.qedeq.kernel.bo.parser.MathParser;
import org.qedeq.kernel.bo.parser.ParserException;
import org.qedeq.kernel.bo.parser.Term;
import org.qedeq.kernel.bo.service.DefaultKernelQedeqBo;
import org.qedeq.kernel.bo.service.Element2LatexImpl;
import org.qedeq.kernel.bo.service.Element2Utf8Impl;
import org.qedeq.kernel.bo.service.unicode.Qedeq2UnicodeVisitor;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.module.Proposition;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.dto.module.FormalProofLineListVo;
import org.qedeq.kernel.se.dto.module.FormalProofLineVo;
import org.qedeq.kernel.se.dto.module.FormalProofVo;
import org.qedeq.kernel.se.dto.module.FormulaVo;
import org.qedeq.kernel.se.dto.module.NodeVo;
import org.qedeq.kernel.se.dto.module.PropositionVo;
import org.qedeq.kernel.xml.dao.Qedeq2Xml;
import org.qedeq.kernel.xml.handler.parser.LoadXmlOperatorListUtility;
import org.qedeq.kernel.xml.parser.BasicParser;
import org.xml.sax.SAXException;


/**
 * Transform text proof into QEDEQ proof.
 *
 * @author  Michael Meyling
 */
public class ProofParserPane extends JFrame {

    /** This class. */
    private static final Class CLASS = ProofParserPane.class;

    /** Source to parse. */
    private CPTextArea source = new CPTextArea(true);

    /** Parse QEDEQ result. */
    private CPTextArea resultQedeqField = new CPTextArea(false);

    /** Parse text result. */
    private CPTextArea resultTextField = new CPTextArea(false);

    /** Error messages. */
    private CPTextArea error = new CPTextArea(false);

    /** Make source scrollable. */
    private JScrollPane sourceScroller = new JScrollPane();

    /** Split between source and result. */
    private JSplitPane splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

    /** Split between source and result right. */
    private JSplitPane splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

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

    private PropositionVo proposition;


    public ProofParserPane(final String name, final MathParser parser, final String title,
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

        resultTextField.setFont(new Font("monospaced", Font.PLAIN, pane.getFont().getSize()));
        resultTextField.setAutoscrolls(true);
        resultTextField.setCaretPosition(0);
        resultTextField.setEditable(false);
        resultTextField.getCaret().setVisible(false);
        resultTextField.setFocusable(true);

        resultQedeqField.setFont(new Font("monospaced", Font.PLAIN, pane.getFont().getSize()));
        resultQedeqField.setAutoscrolls(true);
        resultQedeqField.setCaretPosition(0);
        resultQedeqField.setEditable(false);
        resultQedeqField.getCaret().setVisible(false);
        resultQedeqField.setFocusable(true);

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

        final JScrollPane resultTextScroller = new JScrollPane();
        final JViewport resultTextPort = resultTextScroller.getViewport();
        resultTextPort.add(resultTextField);

        final JScrollPane resultQedeqScroller = new JScrollPane();
        final JViewport resultQedeqPort = resultQedeqScroller.getViewport();
        resultQedeqPort.add(resultQedeqField);

        final JScrollPane errorScroller = new JScrollPane();
        final JViewport errorPort = errorScroller.getViewport();
        errorPort.add(error);

        splitPane2.setLeftComponent(sourceScroller);
        splitPane2.setRightComponent(resultTextScroller);
        splitPane2.setResizeWeight(0.5);
        splitPane2.setOneTouchExpandable(true);

        splitPane1.setTopComponent(splitPane2);
        splitPane1.setBottomComponent(resultQedeqScroller);
        splitPane1.setResizeWeight(0);
        splitPane1.setOneTouchExpandable(true);

        error.setText("");

        globalPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        globalPane.setTopComponent(splitPane1);
        globalPane.setBottomComponent(errorScroller);
        globalPane.setResizeWeight(1);
        globalPane.setOneTouchExpandable(true);

        pane.add(globalPane);

        final JMenuItem item = new JMenuItem("Show as Text");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final TextPaneWindow window = new TextPaneWindow("QEDEQ formulas as unicode text",
                    GuiHelper.readImageIcon("oil/" + QedeqGuiConfig.getInstance().getIconSize()
                    + "/apps/education-mathematics.png"), getTextResult());
                window.setVisible(true);
            }

        });
        resultQedeqField.addMenuItem(item);
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
                    try {
                        setProposition(source.getText());
                        resultQedeqField.setText(getQedeqXml(proposition));
                        resultTextField.setText(getUtf8(proposition));
                    } catch (ModuleDataException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
//                    resultQedeqField.setText(getQedeq(source.getText()));
//                    resultTextField.setText(getTextResult());
                    updateView();
                }
            });
            transformMenu.add(transform);
        }
        menu.add(transformMenu);

        final JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        {
            final JMenuItem about = new JMenuItem("About");
            about.setMnemonic('A');
            about.addActionListener(new AbstractAction() {
                public final void actionPerformed(final ActionEvent action) {
                    JOptionPane.showMessageDialog(ProofParserPane.this,
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
        setSize(900, 800);
    }

    private String getTextResult() {
        final String text = resultQedeqField.getText().trim();
        if (text.length() == 0) {
            return "";
        }
        ModuleLabels labels = new ModuleLabels();
        Element2LatexImpl converter = new Element2LatexImpl(labels);
        Element2Utf8Impl textConverter = new Element2Utf8Impl(converter);
        Element[] elements = new Element[0];
        try {
            elements = BasicParser.createElements(text);
        } catch (final ParserConfigurationException e1) {
            Trace.fatal(CLASS, "setupView$actionPerformed",
                    "Parser configuration error", e1);
            return "";
        } catch (final SAXException e1) {
            // ignore
            return "";
        }
        final StringBuffer result = new StringBuffer();
        for (int i = 0; i < elements.length; i++) {
            final String[] parsed = textConverter.getUtf8(elements[i], 120);
            for (int j = 0; j < parsed.length; j++) {
                result.append(parsed[j] + "\n");
            }
            result.append("\n");
        }
        return result.toString();
    }

    private Element getElement(final String text) {
        if (text == null || text.trim().length() == 0) {
            return null;
        }
        ModuleLabels labels = new ModuleLabels();
        Element2LatexImpl converter = new Element2LatexImpl(labels);
        Element2Utf8Impl textConverter = new Element2Utf8Impl(converter);
        Element[] elements = new Element[0];
        try {
            elements = BasicParser.createElements(text);
        } catch (final ParserConfigurationException e1) {
            Trace.fatal(CLASS, "setupView$actionPerformed",
                    "Parser configuration error", e1);
            return null;
        } catch (final SAXException e1) {
            // ignore
        }
        if (elements.length == 0) {
            return null;
        }
        return elements[0];
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

        splitPane2.setDividerLocation(0.5);
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

    private void setProposition(final String text) {
        proposition = new PropositionVo();
        error.setText("");
        final StringBuffer buffer = new StringBuffer(text);
        final TextInput input = new TextInput(buffer);
        parser.setParameters(input, operators);
        errorPosition = -1;
        final StringBuffer out = new StringBuffer();
        try {
            proposition = new PropositionVo();
            final FormalProofVo fp = new FormalProofVo();
            proposition.addFormalProof(fp);
            final FormalProofLineListVo proof = new FormalProofLineListVo();
            fp.setFormalProofLineList(proof);
            Term term = null;
            do {
                final FormulaVo formula = new FormulaVo();
                final String line = input.getLine().trim();
                if (line.startsWith("(") && Character.isDigit(line.charAt(1))) {
                    // we assume a proof line
                    input.skipWhiteSpace();
                    input.read();
                    final String label = input.readLetterDigitString();
                    input.skipWhiteSpace();
                    input.readString(1);  // should be ")"
                    final FormalProofLineVo l = new FormalProofLineVo();
                    term = parser.readTerm();
                    if (term != null) {
                        formula.setElement(getElement(term.getQedeqXml()));
                    }
                    l.setFormula(formula);
                    l.setLabel(label);
                    proof.add(l);
                    input.skipToEndOfLine();
                } else {
                    term = parser.readTerm();
                    if (term != null) {
                        formula.setElement(getElement(term.getQedeqXml()));
                        proposition.setFormula(formula);
                    }
                    input.skipToEndOfLine();
                }
//                term = parser.readTerm();
//                if (term != null) {
//                    out.append(term.getQedeqXml()).append("\n");
//                    System.out.println(term.getQedeqXml());
//                }
            } while (!input.isEmpty() && !parser.eof());
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
        IoUtility.close(input);  // to satisfy checkstyle
    }

    private String getQedeqXml(final Proposition proposition) throws ModuleDataException {
        final KernelQedeqBo prop = new DefaultKernelQedeqBo(null, DefaultModuleAddress.MEMORY);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final TextOutput output = new TextOutput("out", outputStream, "UTF-8");
        final Qedeq2Xml visitor = new Qedeq2Xml(null, prop, output);
        final NodeVo node = new NodeVo();
        node.setNodeType(proposition);
        visitor.getTraverser().accept(node);
        try {
            return outputStream.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private String getUtf8(final Proposition proposition) throws ModuleDataException {
        final KernelQedeqBo prop = new DefaultKernelQedeqBo(null, DefaultModuleAddress.MEMORY);
        final StringOutput output = new StringOutput();
        final Qedeq2UnicodeVisitor visitor = new Qedeq2UnicodeVisitor(null, prop, true, 0, false,
              false) {
            protected String getUtf8(final Element element) {
                if (element == null) {
                    return "";
                }
                ModuleLabels labels = new ModuleLabels();
                Element2LatexImpl converter = new Element2LatexImpl(labels);
                Element2Utf8Impl textConverter = new Element2Utf8Impl(converter);
                return textConverter.getUtf8(element);
            }

            protected String[] getUtf8(final Element element, final int max) {
                return new String[] {getUtf8(element)};
            }

        };
        visitor.setParameters(output, "en");
        final NodeVo node = new NodeVo();
        node.setNodeType(proposition);
        visitor.getTraverser().accept(node);
        return output.toString();
    }

    private String getQedeqFormula(final String text)  {
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
            error.insert(result.toString() + "\n", error.getText().length());
        }
        IoUtility.close(input);  // to satisfy checkstyle
        return out.toString();
    }


}
