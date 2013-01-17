/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2013,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.gui.se.element;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Segment;

import org.qedeq.base.trace.Trace;
import org.qedeq.gui.se.util.GuiHelper;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Configures the application.
 *
 * @author  Michael Meyling
 */

public class FindDialog extends JDialog {

    /** This class. */
    private static final Class CLASS = FindDialog.class;

    public final static List history = new ArrayList();

    /** Search in this text field. */
    private JTextComponent text;

    /** Case sensitive search?*/
    private JCheckBox caseSensitive;

    /** Search for this text. */
    private JComboBox searchText;

    /** Search for this text. */
    private JLabel status;

    /**
     * Creates new Panel.
     *
     * @param   text    Text to search within.
     */
    public FindDialog(final JTextComponent text) {
        super((JFrame) text.getTopLevelAncestor(), "Find");
        final String method = "Constructor";
        Trace.begin(CLASS, this, method);
        try {
//            setModal(true);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setupView(text);
            updateView();
        } catch (Throwable e) {
            Trace.fatal(CLASS, this, "Initalization of FindDialog failed.", method, e);
        } finally {
            Trace.end(CLASS, this, method);
        }
    }

    /**
     * Assembles checkbox settings panel.
     *
     * @return  Created panel.
     */
    private JComponent searchPanel() {
        FormLayout layout = new FormLayout(
        "left:pref");    // columns

        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        builder.getPanel().setOpaque(false);

        caseSensitive = new JCheckBox(" Case sensitive", false);
        builder.append(caseSensitive);

        searchText = new JComboBox();
        for (int i = 0; i < history.size(); i++) {
            searchText.addItem(history.get(i));
        }
        searchText.setEditable(true);

        builder.append("Search text", searchText);
        return GuiHelper.addSpaceAndTitle(builder.getPanel(), "Find");

    }

    /**
     * Assembles the GUI components of the panel.
     */
    public final void setupView(final JTextComponent text) {
        this.text = text;
        final Container content = getContentPane();
        content.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        final JPanel allOptions = new JPanel();
        allOptions.setBorder(GuiHelper.getEmptyBorder());
        allOptions.setLayout(new FlowLayout(FlowLayout.LEFT));
        allOptions.add(searchPanel());
        allOptions.add(Box.createVerticalStrut(GuiHelper.getEmptyBorderPixelsY()));


        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);
        tabbedPane.addTab("Main Options", allOptions);

        content.add(tabbedPane);

        content.add(GuiHelper.addSpaceAndAlignRight(createButtonPanel()));
        status = new JLabel("");
        final JPanel panel = new JPanel();
        panel.add(status);
        content.add(GuiHelper.alignLeft(panel));

        // let the container calculate the ideal size
        pack();

        final Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2,
            getWidth(), getHeight() + 20);
    }

    /**
     * Create button panel.
     *
     * @return  Main buttons.
     */
    private JPanel createButtonPanel() {
        ButtonBarBuilder bbuilder = ButtonBarBuilder.createLeftToRightBuilder();
        JButton findB = new JButton("Find");
        findB.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                try {
//                    text.getCaret().setVisible(true);
                    int pos1 = findCaretPosition(text.getCaretPosition());
                    if (pos1 < 0) {
                        pos1 = findCaretPosition(0);
                        if (pos1 < 0) {
                            status.setText("String Not Found");
                            return;
                        }
                    }
                    status.setText("");
                    final int pos2 = pos1 + searchText.getSelectedItem().toString().length();
//                    JTextPane pane = (JTextPane) text;
//                    pane.getDocument().
                    text.setCaretPosition(pos1);
                    text.moveCaretPosition(pos2);
                    text.requestFocus();
//                    text.setCaretPosition(pos2);
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            }
        });

        JButton close = new JButton("Close");
        close.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                FindDialog.this.save();
                FindDialog.this.dispose();
            }
        });

        bbuilder.addGriddedButtons(new JButton[]{findB, close});

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

    void save() {
    }

    private final int findCaretPosition(final int start) {
        final Document doc = text.getDocument();
        int nleft = doc.getLength();
        String data = "";
        Segment s = new Segment();
        int offs = 0;
        s.setPartialReturn(false);
        try {
            doc.getText(offs, nleft, s);
            data = s.toString();
        } catch (BadLocationException e) {
            data = text.getText();
        }
        final String m = searchText.getSelectedItem().toString();
        System.out.println("searching for " + m + " from " + start);
        final int result =  data.indexOf(m, start);
        System.out.println("result = " + result);
        return result;
    }
}

