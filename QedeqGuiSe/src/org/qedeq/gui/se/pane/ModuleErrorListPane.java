/* $Id: ErrorListPane.java,v 1.6 2008/03/27 05:14:03 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2008,  Michael Meyling <mime@qedeq.org>.
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.qedeq.kernel.common.QedeqBo;
import org.qedeq.kernel.log.ModuleEventListener;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.utility.EqualsUtility;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * Shows QEDEQ module specific error pane.
 *
 * @version $Revision: 1.6 $
 * @author  Michael Meyling
 */

public class ModuleErrorListPane extends JPanel implements ModuleEventListener {

    /** This class. */
    private static final Class CLASS = ModuleErrorListPane.class;

    /** This text field holds the error descriptions. */
    private JTable error = new JTable( new AbstractTableModel() {
            public String getColumnName(int column) {
                return "error";
            }
            public int getRowCount() { 
                return (prop != null && prop.getException() != null ? prop.getException().size()
                    : 0);
            }
            public int getColumnCount() { 
                return 1;
            }

            public Object getValueAt(int row, int col) {
                return (prop != null && prop.getException() != null ?
                    prop.getException().get(row).getMessage()
                    : "");
            }
            
            public boolean isCellEditable(int row, int column) { return false; }
            public void setValueAt(Object value, int row, int col) {
            }
        });

    /** Write with this font attributes. */
    private final SimpleAttributeSet errorAttrs = new SimpleAttributeSet();

    /** For this module properties the errors are shown. */
    private QedeqBo prop;

    private ErrorSelectionListener errorListener;

    private JScrollPane scrollPane;

    /**
     * Creates new panel.
     */
    public ModuleErrorListPane(final ErrorSelectionListener listener) {
        super(false);
        setModel(null);
        setupView();
        this.errorListener = listener;

    }

    /**
     * Assembles the GUI components of the panel.
     */
    private final void setupView() {
        FormLayout layout = new FormLayout(
            "min:grow",
            "0:grow");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout, this);
//        builder.setDefaultDialogBorder();
        builder.setBorder(BorderFactory.createEmptyBorder());
        builder.setRowGroupingEnabled(true);

        CellConstraints cc = new CellConstraints();
        builder.appendRow(new RowSpec("0:grow"));

        ListSelectionModel rowSM = error.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int errNo = e.getFirstIndex();
//                if (!e.getValueIsAdjusting()) {
                    errorListener.selectError(errNo);
//                }
            }
        });

        error.addMouseListener(new MouseAdapter() {
            public void mouseClicked(final MouseEvent e) {

            }
        });

        scrollPane = new JScrollPane(error);
        builder.add(scrollPane,
            cc.xywh(builder.getColumn(), builder.getRow(), 1, 2, "fill, fill"));

        StyleConstants.setForeground(this.errorAttrs, Color.red);
        // because we override getScrollableTracksViewportWidth:
        scrollPane.getViewport().setBackground(Color.white);
        StyleConstants.setBackground(this.errorAttrs, Color.white);  // TODO mime 20080124: testing


    }

    /**
     * Set new model. To make the new model visible {@link #updateView} must be called.
     *
     * @param   prop
     */
    public void setModel(final QedeqBo prop) {
        Trace.trace(CLASS, this, "setModel", prop);
        if (!EqualsUtility.equals(this.prop, prop)) {
            this.prop = prop;
            updateView();
        }
    }


    public QedeqBo getModel() {
        return this.prop;
    }

    /**
     * Update from model.
     */
    public synchronized void updateView() {
        final String method = "updateView";
        Trace.begin(CLASS, this, method);
        ((AbstractTableModel) error.getModel()).fireTableDataChanged();
        error.invalidate();
        error.repaint();
        this.repaint();
    }

    public void addModule(final QedeqBo prop) {
        if (this.prop != null && this.prop.equals(prop)) {
            updateView();
        }
    }

    public void stateChanged(final QedeqBo prop) {
        if (this.prop != null && this.prop.equals(prop)) {
            updateView();
        }
    }

    public void removeModule(final QedeqBo prop) {
        if (this.prop != null && this.prop.equals(prop)) {
            this.prop = null;
            updateView();
        }
    }


}
