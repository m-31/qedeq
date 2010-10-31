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

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
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
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.utility.DateUtility;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.gui.se.element.CPTextArea;
import org.qedeq.gui.se.util.BareBonesBrowserLaunch;
import org.qedeq.gui.se.util.GuiHelper;
import org.qedeq.kernel.bo.context.KernelContext;

import com.jgoodies.forms.builder.ButtonBarBuilder;

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
    public AboutDialog(final Frame parent) {
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
            public void mouseClicked(final MouseEvent e) {
                try {
                    BareBonesBrowserLaunch.openURL(url);
                } catch (Exception ex) { // ignore, just not open web page
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

//        int width = 540;
//        int height = 400;
//        this.setSize(width, height);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent e) {
                AboutDialog.this.dispose();
            }
        });

    }

    private JComponent createBottomBar() {
        final ButtonBarBuilder bbuilder = ButtonBarBuilder.createLeftToRightBuilder();
        final JButton ok = new JButton("Close");
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
                final StringBuffer sb = new StringBuffer(1000);
                final String[][] prop = IoUtility.getSortedSystemProperties();
                sb.append("# Copied Java system properties during run of ");
                sb.append(KernelContext.getInstance().getDescriptiveKernelVersion());
                sb.append("\n");
                sb.append("# GMT: ");
                sb.append(DateUtility.getGmtTimestamp());
                sb.append("\n#");
                sb.append("\n# The following list contains excaped characters as defined for"
                    + " \"properties\" files");
                sb.append("\n#\n");
                for (int i = 0; i < prop.length; i++) {
                    sb.append(StringUtility.escapeProperty(prop[i][0]));
                    sb.append("=");
                    sb.append(StringUtility.escapeProperty(prop[i][1]));
                    sb.append("\n");
                }
                Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection sel = new StringSelection(sb.toString());
                cb.setContents(sel, null);
            }
        });
        copyButton.setToolTipText("Copy Java system information into "
            + "the clipboard.");

        bbuilder.addGriddedButtons(new JButton[]{copyButton, ok});

        return GuiHelper.addSpaceAndAlignRight(bbuilder.getPanel());

    }

    private JComponent createSystemPropertiesTab() {
        final String[][] rowData = IoUtility.getSortedSystemProperties();
        for (int i = 0; i < rowData.length; i++) {
            // escape following property also for view
            if ("line.separator".equals(rowData[i][0])) {
                rowData[i][1] = StringUtility.escapeProperty(rowData[i][1]);
                break;  // no further search
            }
        }
        String[] nvStrings = new String[] {"Property", "Value"};
        final DefaultTableModel model = new DefaultTableModel(rowData, nvStrings) {
            public boolean isCellEditable(final int row, final int col) {
                return false;
            }
        };
        JTable table = new JTable(model);
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
        final JPanel panel = new JPanel(new BorderLayout());

        final StringBuffer sb = new StringBuffer();
        sb.append("This is Hilbert II - a project to create a formal correct (checkable by a proof"
            + " verifier) but readable (like an ordinary LaTeX textbook) mathematical knowledge base.");
        sb.append("\n\nWritten by Michael Meyling <mime@qedeq.org>");
        sb.append("\nCopyright \u00a9 2010 Michael Meyling. All Rights Reserved.");
        sb.append("\n\nHilbert II comes with ABSOLUTELY NO WARRANTY. This is free software, and you"
            + " are welcome to redistribute it under certain conditions.");
        sb.append(" Please take a look at the license argreements section in the online help.");
        sb.append("\n\nKernel Version: " + KernelContext.getInstance().getKernelVersion());
        sb.append("\nCode Name: " + KernelContext.getInstance().getKernelCodeName());
        sb.append("\nBuild: " + KernelContext.getInstance().getBuildId());
        sb.append("\n" + KernelContext.getInstance().getDedication());
        sb.append("\n\n");
        sb.append("\nUsed memory: ");
        sb.append(Runtime.getRuntime().totalMemory()
            - Runtime.getRuntime().freeMemory());
        sb.append("\nFree memory: ");
        sb.append(Runtime.getRuntime().freeMemory());
        sb.append("\nTotal memory: ");
        sb.append(Runtime.getRuntime().totalMemory());
        sb.append("\nMaximum memory: ");
        sb.append(Runtime.getRuntime().maxMemory());
        sb.append("\n\nNumber of processors/cores: ");
        sb.append(Runtime.getRuntime().availableProcessors());
        final JComponent copy = createTextScroller(sb.toString());
        copy.setBorder(GuiHelper.getEmptyBorder());
        // add to panel
        panel.add(copy, BorderLayout.CENTER);
        return panel;
    }

    public static JScrollPane createTextScroller(final String text) {
        final JScrollPane scroller = new JScrollPane();
        final JViewport vp = scroller.getViewport();
        vp.add(createTextArea(text));
        scroller.setBackground(UIManager.getColor("controlShadow"));
        return scroller;
    }

    public static JTextArea createTextArea(final String text) {
        JTextArea textArea = new CPTextArea(text, false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setCaretPosition(0);
        textArea.setBackground(UIManager.getColor("controlHighlight"));
        return textArea;
    }

}
