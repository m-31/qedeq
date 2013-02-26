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

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import org.qedeq.base.utility.DateUtility;
import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.bo.common.ServiceProcess;

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
    private static ImageIcon runningIcon = new ImageIcon(
        ModuleErrorAndWarningListPane.class.getResource(
            "/images/eclipse/progress_ok.gif"));

    /** Waiting icon. */
    private static ImageIcon waitingIcon = new ImageIcon(
        ModuleErrorAndWarningListPane.class.getResource(
            "/images/eclipse/waiting.gif"));

    /** Complete icon. */
    private static ImageIcon successIcon = new ImageIcon(
        ModuleErrorAndWarningListPane.class.getResource(
            "/images/eclipse/complete_task.gif"));

    /** Last process info. */
    private ServiceProcess[] process = new ServiceProcess[] {};

    /** Should only running tasks be chosen? */
    private boolean onlyRunning = true;

    public String getColumnName(final int column) {
        if (column == 1) {
            return "Service";
        } else if (column == 2) {
            return "Module";
        } else if (column == 3) {
            return "Start";
        } else if (column == 4) {
            return "Stop";
        } else if (column == 5) {
            return "Duration";
        } else if (column == 6) {
            return "%";
        } else if (column == 7) {
            return "Position";
        } else if (column == 8) {
            return "Parameters";
        } else {
            return "";
        }
    }

    public int getRowCount() {
        return process.length;
    }

    public int getColumnCount() {
        return 9;
    }

    public Object getValueAt(final int row, final int col) {
//        System.out.println("row: " + row + " col: " + col);
        final ServiceProcess sp = getServiceProcess(row);
        if (sp == null) {
            return "";
        }
        long current = System.currentTimeMillis();
        if (sp.getStop() != 0) {
            current = sp.getStop();
        }
        switch (col) {
        case 0: if (wasFailure(row)) {
                    return errorIcon;
                } else if (wasSuccess(row)) {
                    return successIcon;
                } else if (isWaiting(row)) {
                    return waitingIcon;
                } else if (isRunning(row)) {
                    return runningIcon;
                }
                break;
        case 1: return sp.getService().getPluginActionName();
        case 2: return sp.getQedeq().getName();
        case 3: return DateUtility.getIsoTime(sp.getStart());
        case 4: return sp.getStop() != 0 ? DateUtility.getIsoTime(sp.getStop()) : "";
        case 5: return DateUtility.getDuration(current - sp.getStart());
        case 6: return "" + sp.getExecutionPercentage();
        case 7: return sp.getExecutionActionDescription().replace('\n', ' ');
        case 8: return sp.getParameterString();
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

    public void fireTableDataChanged() {
        final ServiceProcess[] changed;
        if (getOnlyRunning()) {
            changed = KernelContext.getInstance().getRunningServiceProcesses();
        } else {
            changed = KernelContext.getInstance().getServiceProcesses();
        }
        if (process.length > 0) {
            super.fireTableRowsUpdated(0, process.length - 1);
        }
        if (changed.length > process.length) {
            super.fireTableRowsInserted(process.length, changed.length - 1);
        }
        process = changed;
    }

    public boolean getOnlyRunning() {
        return onlyRunning;
    }

    public void setOnlyRunning(final boolean onlyRunning) {
        this.onlyRunning = onlyRunning;
    }

    /**
     * Get service process that is at given row.
     *
     * @param   row     Look into this row.
     * @return  Process of this row. Might be <code>null</code> if row doesn't exist.
     */
    public ServiceProcess getServiceProcess(final int row) {
        if (row < process.length && row >= 0) {
            return process[row];
        }
        return null;
    }

    /**
     * Was the process stopped by the user?
     *
     * @param   row     Look at this row.
     * @return  Was the process stopped?
     */
    public boolean wasFailure(final int row) {
        if (row >= 0 && row < process.length) {
            return getServiceProcess(row).wasFailure();
        }
        return false;
    }

    /**
     * Was the process finished normally?
     *
     * @param   row     Look at this row.
     * @return  Was the process finished normally?
     */
    public boolean wasSuccess(final int row) {
        if (row >= 0 && row < process.length) {
            return getServiceProcess(row).wasSuccess();
        }
        return false;
    }

    /**
     * Is this process currently running?
     *
     * @param   row     Look at this row.
     * @return  Is the process running?
     */
    public boolean isRunning(final int row) {
        if (row >= 0 && row < process.length) {
            return getServiceProcess(row).isRunning();
        }
        return false;
    }

    /**
     * Is the selected process waiting?
     *
     * @param   row     Look at this row.
     * @return  Is the process waiting?
     */
    public boolean isWaiting(final int row) {
        if (row >= 0 && row < process.length) {
            return getServiceProcess(row).isBlocked();
        }
        return false;
    }

}
