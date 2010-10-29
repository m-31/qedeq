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

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import org.qedeq.base.utility.DateUtility;
import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.bo.context.KernelContext;
import org.qedeq.kernel.common.ServiceProcess;

/**
 * Table model for QEDEQ module specific error pane.
 *
 * @author  Michael Meyling
 */

public class ProcessListModel extends AbstractTableModel {

    /** Error icon. */
    private static ImageIcon errorIcon = new ImageIcon(
        ModuleErrorAndWarningListPane.class.getResource(
            "/images/eclipse/error_tsk.gif"));

    /** Warning icon. */
    private static ImageIcon warningIcon = new ImageIcon(
        ModuleErrorAndWarningListPane.class.getResource(
            "/images/eclipse/warn_tsk.gif"));

    /** We want to show errors and warnings from this module. */
    private QedeqBo qedeq;

    private ServiceProcess[] processes = new ServiceProcess[] {};

    public String getColumnName(final int column) {
        if (column == 1) {
            return "Service";
        } else if (column == 2) {
            return "Start";
        } else if (column == 3) {
            return "Stop";
        } else if (column == 4) {
            return "Duration";
        } else if (column == 5) {
            return "Parameters";
        } else {
            return "";
        }
    }

    public int getRowCount() {
        return processes.length;
    }

    public int getColumnCount() {
        return 7;
    }

    public Object getValueAt(final int row, final int col) {
//        System.out.println("row: " + row + " col: " + col);
        if (qedeq == null) {
            return "";
        }
        final ServiceProcess e = getServiceProcess(row);
        if (e == null) {
            return "";
        }
        switch (col) {
        case 0: if (wasFailure(row)) {
                    return errorIcon;
                } else if (wasSuccess(row)) {
                    return warningIcon;
                } else if (isRunning(row)) {
                    return warningIcon;
                }
                break;
        case 1: return e.getService();
        case 2: return DateUtility.getIsoTimestamp(e.getStart());
        case 3: return DateUtility.getIsoTimestamp(e.getStop());
        case 4: return DateUtility.getDuration(e.getStop() - e.getStart());
        case 5: return e.getParameter();
        default:
                return "";
        }
        return "";
    }

    public boolean isCellEditable(final int row, final int column) {
        return false;
    }

    public void setValueAt(final Object value, final int row, final int col) {
    }

    public Class getColumnClass(final int col) {
        return col == 0 ? Icon.class : Object.class;
   }

    /**
     * Get QEDEQ module.
     *
     * @return  QEDEQ module.
     */
    public QedeqBo getQedeq() {
        return qedeq;
    }

    public void fireTableDataChanged() {
        processes = KernelContext.getInstance().getServiceProcesses();
        super.fireTableDataChanged();
    }

    /**
     * Get service process that is at given row.
     *
     * @param   row     Look into this row.
     * @return  Process of this row. Might be <code>null</code> if row doesn't exist.
     */
    public ServiceProcess getServiceProcess(final int row) {
        if (row < processes.length && row >= 0) {
            return processes[row];
        }
        return null;
    }

    /**
     * Is there an error at given row?
     *
     * @param   row     Look at this row.
     * @return  Is there an error?
     */
    public boolean wasFailure(final int row) {
        if (row >= 0 && row < processes.length) {
            return getServiceProcess(row).wasFailure();
        }
        return false;
    }

    /**
     * Is there an warning at given row?
     *
     * @param   row     Look at this row.
     * @return  Is there an warning?
     */
    public boolean wasSuccess(final int row) {
        if (row >= 0 && row < processes.length) {
            return getServiceProcess(row).wasSuccess();
        }
        return false;
    }

    /**
     * Is there an warning at given row?
     *
     * @param   row     Look at this row.
     * @return  Is there an warning?
     */
    public boolean isRunning(final int row) {
        if (row >= 0 && row < processes.length) {
            return getServiceProcess(row).isRunning();
        }
        return false;
    }

}
