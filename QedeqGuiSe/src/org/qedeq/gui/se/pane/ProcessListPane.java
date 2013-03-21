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

package org.qedeq.gui.se.pane;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.DateUtility;
import org.qedeq.base.utility.YodaUtility;
import org.qedeq.gui.se.util.GuiHelper;
import org.qedeq.kernel.bo.common.PluginCall;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.bo.common.QedeqBoSet;
import org.qedeq.kernel.bo.common.ServiceProcess;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * Shows QEDEQ module specific error pane.
 *
 * @author  Michael Meyling
 */

public class ProcessListPane extends JPanel  {

    /** This class. */
    private static final Class CLASS = ProcessListPane.class;

    /** Currently selected process. */
    private final Set selectedProcesses;

    /** Start automatic refresh thread? */
    private boolean automaticRefresh = true;

    /** Table model. */
    private ProcessListModel model = new ProcessListModel();

    /** This table holds the error descriptions. */
    private final JTable list = new JTable(model) {

        /**
         * Just return the service process of the row.
         *
         * @param  e   Mouse event.
         * @return Tool tip text.
         */
        public String getToolTipText(final MouseEvent e) {
            String tip = null;
            java.awt.Point p = e.getPoint();
            final int row = rowAtPoint(p);
            final int col = columnAtPoint(p);
            try {
                final ServiceProcess process = model.getServiceProcess(row);
                switch (col) {
                case 0:
                     if (process.isBlocked()) {
                         tip = "Process is waiting";
                     } else if (process.isRunning()) {
                         tip = "Process is running";
                     } else if (process.wasFailure()) {
                         tip = "Process was stopped.";
                     } else if (process.wasSuccess()) {
                         tip = "Process has finished";
                     }
                     break;
                case 1: tip = (process.getPluginCall() != null
                     ? process.getPluginCall().getPlugin().getPluginDescription()
                     : process.getExecutionActionDescription());
                     break;
                case 2:
                case 3:
                case 4:
                case 5: tip = process.getQedeq().getUrl();
                     break;
                case 7: tip = GuiHelper.getToolTipText(process.getExecutionActionDescription());
                     break;
                case 8: tip = GuiHelper.getToolTipText(process.getPluginCall() != null
                     ? process.getPluginCall().getParameterString() : "");
                     break;
                default: tip = "";
                }
            } catch (RuntimeException ex) {
                return super.getToolTipText(e);
            }
            return tip;
        }

    };

    /** Write with this font attributes. */
    private final SimpleAttributeSet errorAttrs = new SimpleAttributeSet();

    /** Our scroll area. */
    private JScrollPane scrollPane;

    /**
     * Creates new panel.
     */
    public ProcessListPane() {
        super(false);
        selectedProcesses = new TreeSet();
        setupView();
    }

    /**
     * Send selection events.
     */
//    private void selectLine() {
//        Trace.param(CLASS, this, "selectLine", "selectedLine", selectedLine);
//    }

    /**
     * Assembles the GUI components of the panel.
     */
    private final void setupView() {
        list.setFont(new Font("Lucida Sans Unicode", Font.PLAIN, list.getFont().getSize()));
        list.addComponentListener(new ComponentAdapter() {
            public void componentResized(final ComponentEvent e) {
                list.scrollRectToVisible(list.getCellRect(list.getRowCount() - 1, 0, true));
            }
        });

        final FormLayout layout = new FormLayout(
            "min:grow",
            "0:grow");
        final DefaultFormBuilder builder = new DefaultFormBuilder(layout, this);
        builder.setBorder(BorderFactory.createEmptyBorder());
        builder.setRowGroupingEnabled(true);

        final CellConstraints cc = new CellConstraints();
        builder.appendRow(new RowSpec("0:grow"));

        list.setDefaultRenderer(Icon.class, new IconCellRenderer());

        final ListSelectionModel rowSM = list.getSelectionModel();
        rowSM.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // if selection changes remember the process number (= selected row (starting with 0))
        rowSM.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(final ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                selectedProcesses.clear();
                if (!rowSM.isSelectionEmpty()) {
                    int minIndex = rowSM.getMinSelectionIndex();
                    int maxIndex = rowSM.getMaxSelectionIndex();
                    for (int i = minIndex; i <= maxIndex; i++) {
                        if (rowSM.isSelectedIndex(i)) {
                            final ServiceProcess process = model.getServiceProcess(i);
                            selectedProcesses.add(process);
                        }
                    }
                }
            }
        });
/*
        // doing a click shall open the edit window
        list.addMouseListener(new MouseAdapter()  {
            public void mouseClicked(final MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Trace.trace(CLASS, this, "setupView$vmouseClicked", "doubleClick");
                }
                selectLine();
            }
        });

        // pressing the enter key shall open the edit window
        list.getActionMap().put("selectNextRowCell", new AbstractAction() {
            public void actionPerformed(final ActionEvent event) {
                Trace.param(CLASS, this, "setupView$actionPerformed", "event" , event);
                selectLine();
            }
        });
*/
        scrollPane = new JScrollPane(list);
        builder.add(scrollPane,
            cc.xywh(builder.getColumn(), builder.getRow(), 1, 2, "fill, fill"));

        StyleConstants.setForeground(this.errorAttrs, Color.red);
        // because we override getScrollableTracksViewportWidth:
        scrollPane.getViewport().setBackground(Color.white);
        StyleConstants.setBackground(this.errorAttrs, Color.white);  // TODO mime 20080124: testing


        // the default table cell renderer uses a JLabel to render the heading and we want to
        // left align the header columns
        // TODO mime 20080415: left align with small spaces would be better
        final JTableHeader header = list.getTableHeader();
        ((JLabel) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
        //set height of header correctly
        header.setPreferredSize(new Dimension(list.getTableHeader().getWidth(),
            (int) (1.1 * this.getFontMetrics(getFont()).getHeight())));

        changeHeaderWidth();
        final Thread refresh = new Thread() {
            public void run() {
                while (automaticRefresh) {
                    updateView();
                    IoUtility.sleep(5000);  // refresh every 5 seconds
                }
            }
        };
        refresh.setDaemon(true);
        refresh.start();
    }

    /**
     * Make 2. column smaller.
     */
    private void changeHeaderWidth() {
        TableColumnModel columnModel = list.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(24);
        columnModel.getColumn(0).setMinWidth(24);
        columnModel.getColumn(1).setPreferredWidth(200);
        columnModel.getColumn(1).setMinWidth(150);
        columnModel.getColumn(2).setPreferredWidth(200);
        columnModel.getColumn(2).setMinWidth(150);
        columnModel.getColumn(3).setPreferredWidth(100);
        columnModel.getColumn(3).setMinWidth(100);
        columnModel.getColumn(4).setPreferredWidth(100);
        columnModel.getColumn(4).setMinWidth(100);
        columnModel.getColumn(5).setPreferredWidth(100);
        columnModel.getColumn(5).setMinWidth(100);
        columnModel.getColumn(6).setPreferredWidth(60);
        columnModel.getColumn(6).setMinWidth(60);
        columnModel.getColumn(7).setPreferredWidth(2000);
        columnModel.getColumn(7).setMinWidth(100);
        columnModel.getColumn(8).setPreferredWidth(200);
        columnModel.getColumn(8).setMinWidth(100);
    }

    /**
     * Update from model.
     */
    public synchronized void updateView() {
        final String method = "updateView";
        Trace.begin(CLASS, this, method);
        ((AbstractTableModel) list.getModel()).fireTableDataChanged();
        list.invalidate();
        this.revalidate();
// TODO 20130228 remove me if refresh works
//        list.repaint();
//        this.repaint();
    }

    public void refreshStates() {
        Runnable stateChanged = new Runnable() {
            public void run() {
                updateView();
            }
        };
        SwingUtilities.invokeLater(stateChanged);
    }

    /**
     * We render our cells with this class.
     *
     * @author  Michael Meyling
     */
    private static class IconCellRenderer extends DefaultTableCellRenderer {

        protected void setValue(final Object value) {
            if (value instanceof Icon) {
                setIcon((Icon) value);
                super.setValue(null);
            } else {
                setIcon(null);
                super.setValue(value);
            }
        }

    }

    public void setRunningOnly(final boolean runningOnly) {
        model.setOnlyRunning(runningOnly);
    }

    public void stopSelected() {
        Iterator iterator = selectedProcesses.iterator();
        while (iterator.hasNext()) {
            final ServiceProcess process = (ServiceProcess) iterator.next();
            if (process != null && process.isRunning()) {
                process.interrupt();
            }
        }
    }

    /**
     * Print stack trace of selected service process thread to <code>System.out</code> if the
     * method <code>Thread.getStackTrace()</code> is supported form the currently running java
     * version.
     */
    public void stackTraceSelected() {
        if (!selectedProcesses.isEmpty()) {
            StringBuffer result = new StringBuffer();
            Iterator iterator = selectedProcesses.iterator();
            while (iterator.hasNext()) {
                final ServiceProcess process = (ServiceProcess) iterator.next();
                if (process != null && process.isRunning()
                            && YodaUtility.existsMethod(Thread.class, "getStackTrace", new Class[] {})) {
                    StackTraceElement[] trace = new StackTraceElement[] {};
                    result.append("id ").append(process.getId());
                    try {
                        trace = (StackTraceElement[]) YodaUtility.executeMethod(
                            process.getThread(), "getStackTrace", new Class[] {}, new Object[] {});
                    } catch (NoSuchMethodException e) {
                        // ignore
                    } catch (InvocationTargetException e) {
                        // ignore
                    }
                    for (int i = 0; i < trace.length; i++)  {
                        result.append(trace[i]);
                        result.append("\n");
                    }
                    result.append("\n\n");
                }
            }
            (new TextPaneWindow("Stacktrace",
                GuiHelper.readImageIcon("tango/16x16/devices/video-display.png"),
                result.toString())).setVisible(true);
        }
    }


    /**
     * Print stack trace of selected service process thread to <code>System.out</code> if the
     * method <code>Thread.getStackTrace()</code> is supported form the currently running java
     * version.
     */
    public void detailsSelected() {
        if (!selectedProcesses.isEmpty()) {
            StringBuffer result = new StringBuffer();
            Iterator iterator = selectedProcesses.iterator();
            while (iterator.hasNext()) {
                final ServiceProcess process = (ServiceProcess) iterator.next();
                if (process != null) {
                    result.append("id ").append(process.getId());
                    String tip = "";
                    if (process.isBlocked()) {
                        tip = "Process is waiting";
                    } else if (process.isRunning()) {
                        tip = "Process is running";
                    } else if (process.wasFailure()) {
                        tip = "Process was stopped.";
                    } else if (process.wasSuccess()) {
                        tip = "Process has finished";
                    }
                    result.append("\n\tStatus:     ").append(tip);
                    result.append("\n\tAction:     ").append(process.getActionName());
                    result.append("\n\tModule:     ").append((process.getPluginCall() != null
                        ? process.getQedeq().getName() : ""));
                    result.append("\n\tURL:        ").append((process.getPluginCall() != null
                        ? process.getQedeq().getModuleAddress().getUrl() : ""));
                    result.append("\n\tStart:      ").append(DateUtility.getIsoTime(process.getStart()));
                    result.append("\n\tStop:       ").append((process.getStop() != 0
                        ? DateUtility.getIsoTime(process.getStop()) : ""));
                    result.append("\n\tPercentage: ").append(process.getExecutionPercentage());
                    result.append("\n\tDescription:").append(process.getExecutionActionDescription());
                    result.append("\n\tParameter:  ").append((process.getPluginCall() != null
                        ? process.getPluginCall().getParameterString() : ""));
                    result.append("\n\tBlocked:   ");
                    final QedeqBoSet blocked = process.getBlockedModules();
                    Iterator bIterator = blocked.iterator();
                    while (bIterator.hasNext()) {
                        final QedeqBo qedeq = (QedeqBo) bIterator.next();
                        result.append(" ").append(qedeq.getName());
                    }
                    result.append("\n\tParent:   ");
                    PluginCall parent = process.getPluginCall();
                    while (parent != null) {
                        parent = parent.getParentPluginCall();
                        if (parent != null) {
                            result.append(" ").append(parent.getId());
                        }
                    }
                    result.append("\n");
                }
            }
            (new TextPaneWindow("Service Process Details",
                GuiHelper.readImageIcon("tango/16x16/devices/video-display.png"),
                result.toString())).setVisible(true);
        }
    }


}
