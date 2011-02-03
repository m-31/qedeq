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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.qedeq.base.trace.Trace;
import org.qedeq.gui.se.util.GuiHelper;

import com.jgoodies.forms.builder.ButtonBarBuilder;

/**
 * Global log. All events are displayed here.
 *
 * @author  Michael Meyling
 */

public class TextPaneWindow extends JFrame {

    /** This class. */
    private static final Class CLASS = TextPaneWindow.class;

    /** Common text attributes. */
    private final SimpleAttributeSet attrs = new SimpleAttributeSet();

    /** Here is the text. */
    private JTextPane textPane;

    /** Word wrap on?. */
    private boolean wordWrap;


    /**
     * Creates new panel.
     *
     * @param   title   Window title.
     * @param   icon    Window icon.
     * @param   text    Display this text.
     */
    public TextPaneWindow(final String title, final ImageIcon icon, final String text) {
        super(title);
        setIconImage(icon.getImage());
        final String method = "Constructor";
        Trace.begin(CLASS, this, method);
        try {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setupView(text);
        } catch (Throwable e) {
            Trace.fatal(CLASS, this, "Initalization of TextPaneWindow failed.", method, e);
        } finally {
            Trace.end(CLASS, this, method);
        }
    }

    /**
     * Assembles the GUI components of the panel.
     *
     * @param   text    Display this text.
     */
    public final void setupView(final String text) {
        final Container content = getContentPane();
        content.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        JPanel allOptions = new JPanel();
        allOptions.setBorder(GuiHelper.getEmptyBorder());
        allOptions.setLayout(new BoxLayout(allOptions, BoxLayout.Y_AXIS));
        allOptions.add(createTextPanel(text));
        content.add(allOptions);

        ButtonBarBuilder bbuilder = ButtonBarBuilder.createLeftToRightBuilder();

        JButton ok = new JButton("OK");
        ok.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                TextPaneWindow.this.dispose();
            }
        });

        JButton increaseFontSize = new JButton("+1");
        increaseFontSize.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                final MutableAttributeSet attr = TextPaneWindow.this.textPane.getInputAttributes();
                final int size = StyleConstants.getFontSize(attr);
                StyleConstants.setFontSize(attr, size + 1);
                final StyledDocument doc = TextPaneWindow.this.textPane.getStyledDocument();
                doc.setCharacterAttributes(0, doc.getLength(), attr, true);
            }
        });

        JButton decreaseFontSize = new JButton("-1");
        decreaseFontSize.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                final MutableAttributeSet attr = TextPaneWindow.this.textPane.getInputAttributes();
                final int size = StyleConstants.getFontSize(attr);
                if (size > 1) {
                    StyleConstants.setFontSize(attr, size - 1);
                    final StyledDocument doc = TextPaneWindow.this.textPane.getStyledDocument();
                    doc.setCharacterAttributes(0, doc.getLength(), attr, true);
                }
            }
        });

        final JButton wrap = new JButton("wrap on");
        wrap.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                TextPaneWindow.this.wordWrap = !TextPaneWindow.this.wordWrap;
                if (TextPaneWindow.this.wordWrap) {
                    wrap.setText("wrap on");
                } else {
                    wrap.setText("wrap off");
                }
                Dimension d = TextPaneWindow.this.textPane.getSize();
                // TODO 20101116 m31: Quick and Dirty hack to force a new layout and refresh
                d.setSize(d.height, d.width - 1);
                TextPaneWindow.this.textPane.setSize(d);
                d.setSize(d.height, d.width + 1);
                TextPaneWindow.this.textPane.setSize(d);
            }
        });

        bbuilder.addGriddedButtons(new JButton[] {wrap, decreaseFontSize, increaseFontSize, ok});

        final JPanel buttons = bbuilder.getPanel();
        content.add(GuiHelper.addSpaceAndAlignRight(buttons));

        // let the container calculate the ideal size
        pack();

        final Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2);

//        final Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
//            setBounds((screenSize.width - getWidth()) / 3, (screenSize.height - getHeight()) / 3,
//            800, 600);
    }

    /**
     * Creates panel with text pane.
     *
     * @param   text    Display this text.
     * @return  Created panel.
     */
    private final JPanel createTextPanel(final String text) {
        textPane = new JTextPane() {
            public boolean getScrollableTracksViewportWidth() {
                return TextPaneWindow.this.wordWrap;
            }
        };
//        textPane.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, textPane.getFont().getSize()));
//        textPane.setFont(new Font("Lucida Console", Font.PLAIN, textPane.getFont().getSize()));
//        textPane.setFont(new Font("Lucida Sans Typewriter", Font.PLAIN, textPane.getFont().getSize()));
        textPane.setFont(new Font("Monospaced", Font.PLAIN, textPane.getFont().getSize()));
        final StyleContext sc = new StyleContext();
        final DefaultStyledDocument doc = new DefaultStyledDocument(sc);
        try {
            doc.insertString(0, text, attrs);
        } catch (BadLocationException e) {
            // should not occur
            throw new RuntimeException(e);
        }
        textPane.setDocument(doc);
        final JPanel panel = new JPanel();
        textPane.setDragEnabled(true);
        textPane.setAutoscrolls(true);
        textPane.setCaretPosition(0);
        textPane.setEditable(false);
        textPane.getCaret().setVisible(false);
//      final DefaultCaret caret = (DefaultCaret) text.getCaret();
//      caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);  // LATER mime JRE 1.5
        textPane.setFocusable(true);
        final JScrollPane scroller = new JScrollPane();
        final JViewport vp = scroller.getViewport();
        vp.add(textPane);
        panel.setLayout(new BorderLayout(1, 1));
        panel.add(scroller);
        return panel;
    }

}

