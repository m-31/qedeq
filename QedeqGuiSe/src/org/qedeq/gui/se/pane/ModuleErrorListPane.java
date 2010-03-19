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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.gui.se.control.ErrorSelectionListenerList;
import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.bo.log.ModuleEventListener;
import org.qedeq.kernel.common.SourceFileExceptionList;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * Shows QEDEQ module specific error pane.
 *
 * @author  Michael Meyling
 */

public class ModuleErrorListPane extends JPanel implements ModuleEventListener {


    /** This class. */
    private static final Class CLASS = ModuleErrorListPane.class;

    /** Currently selected error. */
    private int errNo = -1;

    /** Table model. */
    ModuleErrorListModel model = new ModuleErrorListModel();

    /** This table holds the error descriptions. */
    private JTable error = new JTable(model) {

        /**
         * Just return the error message of the row.
         *
         * @param  e   Mouse event.
         * @return Tool tip text.
         */
        public String getToolTipText(final MouseEvent e) {
            String tip = null;
            java.awt.Point p = e.getPoint();
            int rowIndex = rowAtPoint(p);
            try {
                tip = sfl.get(rowIndex).getMessage() + "\n";
            } catch (RuntimeException ex) {
                return super.getToolTipText(e);
            }
            return tip;
        }

    };

    /** Write with this font attributes. */
    private final SimpleAttributeSet errorAttrs = new SimpleAttributeSet();

    /** For this module properties the errors are shown. */
    private QedeqBo prop;

    /** These errors are shown. */
    private SourceFileExceptionList sfl;

    /** Our scroll area. */
    private JScrollPane scrollPane;

    /**
     * Creates new panel.
     */
    public ModuleErrorListPane() {
        super(false);
        setModel(null);
        setupView();
    }

    private void selectError() {
        Trace.param(CLASS, this, "selectError", "sfl", sfl);
        Trace.param(CLASS, this, "selectError", "errNo", errNo);
        if (sfl != null && errNo >= 0 && errNo < sfl.size()) {
            ErrorSelectionListenerList.getInstance().selectError(errNo, sfl.get(errNo));
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

        final ListSelectionModel rowSM = error.getSelectionModel();
        rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // if selection changes remember the error number (= selected row (starting with 0))
        rowSM.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(final ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                errNo = error.getSelectionModel().getLeadSelectionIndex();
                Trace.param(CLASS, this, "setupView$valueChanged", "errNo" , errNo);
            }
        });

        // doing a click shall open the edit window
        error.addMouseListener(new MouseAdapter()  {
            public void mouseClicked(final MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Trace.trace(CLASS, this, "setupView$vmouseClicked", "doubleClick");
                }
                selectError();
            }
        });

        // pressing the enter key shall open the edit window
        error.getActionMap().put("selectNextRowCell", new AbstractAction() {
            public void actionPerformed(final ActionEvent event) {
                Trace.param(CLASS, this, "setupView$actionPerformed", "event" , event);
                selectError();
            }
        });

        scrollPane = new JScrollPane(error);
        builder.add(scrollPane,
            cc.xywh(builder.getColumn(), builder.getRow(), 1, 2, "fill, fill"));

        StyleConstants.setForeground(this.errorAttrs, Color.red);
        // because we override getScrollableTracksViewportWidth:
        scrollPane.getViewport().setBackground(Color.white);
        StyleConstants.setBackground(this.errorAttrs, Color.white);  // TODO mime 20080124: testing


        // the default table cell renderer uses a JLabel to render the heading and we want to
        // left align the header columns
        // TODO mime 20080415: left align with small spaces would be better
        final JTableHeader header = error.getTableHeader();
        ((JLabel) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        changeHeaderWidth();
    }

    /**
     * Make 2. column smaller.
     */
    private void changeHeaderWidth() {
        TableColumnModel model = error.getColumnModel();
        model.getColumn(0).setPreferredWidth(10);
        model.getColumn(1).setPreferredWidth(300);
        model.getColumn(2).setPreferredWidth(30);
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
        this.sfl = (prop != null ? prop.getErrors() : null);
        ((AbstractTableModel) error.getModel()).fireTableDataChanged();
        ((AbstractTableModel) error.getModel()).fireTableStructureChanged();
        changeHeaderWidth();
        error.invalidate();
        error.repaint();
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

}

