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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
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

import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.gui.se.control.SelectionListenerList;
import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.bo.log.ModuleEventListener;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * Shows QEDEQ module specific error pane.
 *
 * @author  Michael Meyling
 */

public class ModuleErrorAndWarningListPane extends JPanel implements ModuleEventListener {

    /** This class. */
    private static final Class CLASS = ModuleErrorAndWarningListPane.class;

    /** Currently selected error. */
    private int selectedLine = -1;

    /** Table model. */
    private ModuleErrorAndWarningListModel model = new ModuleErrorAndWarningListModel();

    /** This table holds the error descriptions. */
    private JTable list = new JTable(model) {

        /**
         * Just return the error message of the row.
         *
         * @param  e   Mouse event.
         * @return Tool tip text.
         */
        public String getToolTipText(final MouseEvent e) {
            String tip = null;
            java.awt.Point p = e.getPoint();
            int row = rowAtPoint(p);
            try {
                tip = model.getSourceFileException(row).getMessage() + "\n";
            } catch (RuntimeException ex) {
                return super.getToolTipText(e);
            }
            return tip;
        }

    };

    /** Write with this font attributes. */
    private final SimpleAttributeSet errorAttrs = new SimpleAttributeSet();

    /** For this module properties the warnings and errors are shown. */
    private QedeqBo prop;

    /** Our scroll area. */
    private JScrollPane scrollPane;

    /** This listener gets all selection messages from us. */
    private SelectionListenerList listener;

    /**
     * Creates new panel.
     *
     * @param   listener    Send selecting events to this listener.
     */
    public ModuleErrorAndWarningListPane(final SelectionListenerList listener) {
        super(false);
        this.listener = listener;
        setModel(null);
        setupView();
    }

    /**
     * Send selection events.
     */
    private void selectLine() {
        Trace.param(CLASS, this, "selectLine", "selectedLine", selectedLine);
        if (model.isError(selectedLine)) {
            listener.selectError(model.getErrorNumber(selectedLine), model.getSourceFileException(selectedLine));
        } else if (model.isWarning(selectedLine)) {
            listener.selectWarning(model.getWarningNumber(selectedLine), model.getSourceFileException(selectedLine));
        }
    }

    /**
     * Assembles the GUI components of the panel.
     */
    private final void setupView() {
        FormLayout layout = new FormLayout(
            "min:grow",
            "0:grow");
        final DefaultFormBuilder builder = new DefaultFormBuilder(layout, this);
        builder.setBorder(BorderFactory.createEmptyBorder());
        builder.setRowGroupingEnabled(true);

        final CellConstraints cc = new CellConstraints();
        builder.appendRow(new RowSpec("0:grow"));

        list.setDefaultRenderer(Icon.class, new IconCellRenderer());

        final ListSelectionModel rowSM = list.getSelectionModel();
        rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // if selection changes remember the error number (= selected row (starting with 0))
        rowSM.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(final ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                selectedLine = list.getSelectionModel().getLeadSelectionIndex();
                Trace.param(CLASS, this, "setupView$valueChanged", "selectedLine" , selectedLine);
            }
        });

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

        changeHeaderWidth();
    }

    /**
     * Make 2. column smaller.
     */
    private void changeHeaderWidth() {
        TableColumnModel columnModel = list.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(24);
        columnModel.getColumn(0).setMinWidth(24);
        columnModel.getColumn(1).setPreferredWidth(2000);
        columnModel.getColumn(1).setMinWidth(100);
        columnModel.getColumn(2).setPreferredWidth(100);
        columnModel.getColumn(2).setMinWidth(100);
    }

    /**
     * Set new model. To make the new model visible {@link #updateView} must be called.
     *
     * @param   prop
     */
    public synchronized void setModel(final QedeqBo prop) {
        Trace.trace(CLASS, this, "setModel", prop);
        model.setQedeq(prop);
        if (!EqualsUtility.equals(this.prop, prop)) {
            this.prop = prop;
            Runnable setModel = new Runnable() {
                public void run() {
                    updateView();
                }
            };
            SwingUtilities.invokeLater(setModel);
        }
    }

    /**
     * Update from model.
     */
    public synchronized void updateView() {
        final String method = "updateView";
        Trace.begin(CLASS, this, method);
        ((AbstractTableModel) list.getModel()).fireTableDataChanged();
        ((AbstractTableModel) list.getModel()).fireTableStructureChanged();
        changeHeaderWidth();
        list.invalidate();
        list.repaint();
        this.repaint();
    }

    public void addModule(final QedeqBo p) {
        if (prop != null && prop.equals(p)) {
            Runnable addModule = new Runnable() {
                public void run() {
                    updateView();
                }
            };
            SwingUtilities.invokeLater(addModule);
        }
    }

    public void stateChanged(final QedeqBo p) {
        if (prop != null && prop.equals(p)) {
            Runnable stateChanged = new Runnable() {
                public void run() {
                    updateView();
                }
            };
            SwingUtilities.invokeLater(stateChanged);
        }
    }

    public void removeModule(final QedeqBo p) {
        if (prop != null && prop.equals(p)) {
            prop = null;
            Runnable removeModule = new Runnable() {
                public void run() {
                    updateView();
                }
            };
            SwingUtilities.invokeLater(removeModule);
        }
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

}
