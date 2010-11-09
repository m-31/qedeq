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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.YodaUtility;
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

    /** Here is our process list. */
    private ProcessListPane processList;

    /**
     * Creates new Panel.
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


    /**
     * Assembles the GUI components of the panel.
     */
    public final void setupView() {
        setIconImage(GuiHelper.readImageIcon("tango/16x16/categories/applications-system.png")
            .getImage());
        final Container content = getContentPane();
        content.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        JPanel allOptions = new JPanel();
        allOptions.setBorder(GuiHelper.getEmptyBorder());
        allOptions.setLayout(new BoxLayout(allOptions, BoxLayout.Y_AXIS));
        processList = new ProcessListPane();
        allOptions.add(processList);
        content.add(allOptions);

        ButtonBarBuilder bbuilder = ButtonBarBuilder.createLeftToRightBuilder();

        JButton stackTrace = null;

        if (YodaUtility.existsMethod(Thread.class, "getStackTrace", new Class[] {})) {
            stackTrace = new JButton("Stacktrace");
            stackTrace.addActionListener(new  ActionListener() {
                public void actionPerformed(final ActionEvent actionEvent) {
                    (new TextPaneWindow("Stacktrace",
                        GuiHelper.readImageIcon("tango/16x16/devices/video-display.png"),
                        ProcessWindow.this.processList
                        .stackTraceSelected())).setVisible(true);
                }
            });
        }

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

        if (stackTrace != null) {
            bbuilder.addGriddedButtons(new JButton[]{stackTrace, stop, refresh, cancel, ok});
        } else {
            bbuilder.addGriddedButtons(new JButton[]{stop, refresh, cancel, ok});
        }

        final JPanel buttons = bbuilder.getPanel();
        content.add(GuiHelper.addSpaceAndAlignRight(buttons));

        // let the container calculate the ideal size
        pack();

        final Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2,
            1000, 400);
    }

    /**
     * Update from model.
     */
    public void updateView() {
        invalidate();
        repaint();
    }


}

