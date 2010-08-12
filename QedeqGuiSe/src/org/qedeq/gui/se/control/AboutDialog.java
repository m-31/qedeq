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

package org.qedeq.gui.se.control;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.qedeq.base.io.IoUtility;
import org.qedeq.gui.se.util.GuiHelper;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.looks.Options;

/**
 * Shows the application's "About" dialog box.
 * 
 * @author  Michael Meyling
 */

public class AboutDialog extends JDialog {

    /**
     * Constructor.
     *
     * @param   parent  Parent frame.
     */
    public AboutDialog(Frame parent) {
        super(parent, "About", true);
        final JPanel content = new JPanel(new BorderLayout());
        getContentPane().add(content);

        final ImageIcon icon = GuiHelper.readImageIcon("qedeq/32x32/qedeq.png");
        final JLabel logo = new JLabel(icon, JLabel.RIGHT);
        logo.setText("GUI for Hilbert II ");
        logo.setHorizontalTextPosition(JLabel.LEFT);
        logo.setFont(UIManager.getFont("TitledBorder.font"));
//        logo.setOpaque(true);
        logo.setBorder(new CompoundBorder(new EmptyBorder(5, 5, 0, 10),
                logo.getBorder()));

        final String url = "http://www.qedeq.org/";

        final JPanel logoPanel = new JPanel();
//        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.X_AXIS));
        logoPanel.setLayout(new GridLayout(1, 1));
        logoPanel.add(logo);

//      logoPanel.setOpaque(true);
        logoPanel.setToolTipText(url);
        logoPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                try {
                } catch(Exception ex) { // ignore, just not open web page
                }
            }
        });

        
        
//        logoPanel.setBorder(new CompoundBorder(new EmptyBorder(5, 5, 5, 5),
//                new EtchedBorder()));
        content.add(logoPanel, BorderLayout.NORTH);

        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);
//        tabbedPane.putClientProperty("jgoodies.noContentBorder", Boolean.TRUE);
//        tabbedPane.putClientProperty(Options.EMBEDDED_TABS_KEY, Boolean.TRUE);
        tabbedPane.addTab("About", createAboutTab());
        tabbedPane.addTab("System Properties", createSystemPropertiesTab());
//        tabbedPane.setBorder(GuiHelper.getEmptyBorder());
        tabbedPane.setBorder(new CompoundBorder(new EmptyBorder(0, 10, 10, 10),
                tabbedPane.getBorder()));
        content.add(tabbedPane, BorderLayout.CENTER);

        content.add(createBottomBar(), BorderLayout.SOUTH);


        
        // let the container calculate the ideal size
        this.pack();

        int width = 540;
        int height = 320;

        this.setSize(width, height);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                AboutDialog.this.dispose();
            }
            
        });

    }

    private JComponent createBottomBar() {
        
        ButtonBarBuilder bbuilder = ButtonBarBuilder.createLeftToRightBuilder();

        JButton ok = new JButton("Close");
        ok.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                String command = actionEvent.getActionCommand();

                if (command.compareTo("Close") == 0) {
                    AboutDialog.this.dispose();
                }
            }
        });

        JButton copyButton = new JButton("Copy System Properties");
        copyButton.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            }
        });
        copyButton.setToolTipText("Copy Java system information into "
            + "the clipboard.");

        bbuilder.addGriddedButtons(new JButton[]{copyButton, ok});

        
        return GuiHelper.addSpaceAndAlignRight(bbuilder.getPanel());

    }

    private JComponent createSystemPropertiesTab() {
        String[][] rowData = IoUtility.getSortedSystemProperties();
        String[] nvStrings = new String[] { "Property", "Value" };
        DefaultTableModel model = new DefaultTableModel(rowData, nvStrings) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable table = new JTable(model) {
        };
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        GuiHelper.calcColumnWidths(table);
        JScrollPane scroller = new JScrollPane(table);
//        scroller.setBorder(new LineBorder(UIManager.getColor("controlHighlight"),1));
        scroller.setBorder(GuiHelper.getEmptyBorder());
        scroller.setBackground(UIManager.getColor("controlShadow"));
//        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//        scroller.setBorder(new CompoundBorder(new EmptyBorder(5, 0, 0, 0), scroller.getBorder()));
        return scroller;
//        JPanel propTab = new JPanel(new BorderLayout());
//        propTab.add(new JLabel("Current system properties:"),
//                BorderLayout.NORTH);
//        propTab.add(scroller, BorderLayout.CENTER);
//        return propTab;
    }

    private JComponent createAboutTab() {
        // TODO Auto-generated method stub
        return new JPanel();
    }

    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();

        if (command.compareTo("Close") == 0) {
            this.dispose();
        }
    }

}