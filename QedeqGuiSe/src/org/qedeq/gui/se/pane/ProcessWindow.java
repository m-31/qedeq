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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.qedeq.base.trace.Trace;
import org.qedeq.gui.se.util.GuiHelper;

import com.jgoodies.forms.builder.ButtonBarBuilder;

/**
 * Configures the application.
 *
 * @author  Michael Meyling
 */

public class ProcessWindow extends JFrame {

    /** This class. */
    private static final Class CLASS = ProcessWindow.class;
    private ProcessListPane processList;

    /**
     * Creates new Panel.
     *
     * @param   parent  Parent frame.
     */
    public ProcessWindow() {
        super("Processes");
        final String method = "Constructor";
        Trace.begin(CLASS, this, method);
        try {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setupView();
            updateView();
        } catch (Throwable e) {
            Trace.fatal(CLASS, this, "Initalization of PreferencesDialog failed.", method, e);
        } finally {
            Trace.end(CLASS, this, method);
        }
    }

    private JComponent addSpaceAndTitle(final JPanel panel, final String title) {
        JPanel withSpace = new JPanel();
        withSpace.setBorder(GuiHelper.getEmptyBorderStackable());
        withSpace.add(panel);
        withSpace.setLayout(new GridLayout(0, 1));
        JPanel withTitle = new JPanel();
        withTitle.setBorder(BorderFactory.createTitledBorder(title));
        withTitle.add(withSpace);
        withTitle.setLayout(new GridLayout(0, 1));
        return withTitle;
    }

    /**
     * Assembles the GUI components of the panel.
     */
    public final void setupView() {
        final Container content = getContentPane();
        content.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        JPanel allOptions = new JPanel();
        allOptions.setBorder(GuiHelper.getEmptyBorder());
        allOptions.setLayout(new BoxLayout(allOptions, BoxLayout.Y_AXIS));
        processList = new ProcessListPane();
        allOptions.add(processList);
        content.add(allOptions);

        ButtonBarBuilder bbuilder = ButtonBarBuilder.createLeftToRightBuilder();

        JButton stackTrace = new JButton("Stacktrace");
        stackTrace.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                ProcessWindow.this.processList.stackTraceSelected();
            }
        });

        JButton stop = new JButton("Stop");
        stop.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                ProcessWindow.this.processList.stopSelected();
            }
        });


        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                ProcessWindow.this.processList.updateView();
            }
        });


        JButton ok = new JButton("OK");
        ok.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                ProcessWindow.this.dispose();
            }
        });

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                ProcessWindow.this.dispose();
            }
        });

        bbuilder.addGriddedButtons(new JButton[]{stackTrace, stop, refresh, cancel, ok});

        final JPanel buttons = bbuilder.getPanel();
        content.add(GuiHelper.addSpaceAndAlignRight(buttons));

        // let the container calculate the ideal size
        pack();

        final Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2,
            900, 400);
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


}

