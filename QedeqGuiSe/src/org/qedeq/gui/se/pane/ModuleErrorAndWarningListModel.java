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

import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.common.SourceFileException;
import org.qedeq.kernel.common.SourceFileExceptionList;

/**
 * Table model for QEDEQ module specific error pane.
 *
 * @author  Michael Meyling
 */

public class ModuleErrorAndWarningListModel extends AbstractTableModel {

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

    /** Errors of current module. Might be <code>null</code>. */
    private SourceFileExceptionList errors;

    /** Warnings of current module. Might be <code>null</code>. */
    private SourceFileExceptionList warnings;

    /** Number of errors of current module. */
    private int maxErrors;

    /** Number of errors plus number of warnings of current module. */
    private int maxEntries;

    public String getColumnName(final int column) {
        if (qedeq == null) {
            return "";
        }
        if (column == 1) {
            return qedeq.getModuleAddress().getUrl();
        } else if (column == 2) {
            return "Location";
        } else {
            return "";
        }
    }

    public int getRowCount() {
        return maxEntries;
    }

    public int getColumnCount() {
        return 3;
    }

    public Object getValueAt(final int row, final int col) {
//        System.out.println("row: " + row + " col: " + col);
        if (qedeq == null) {
            return "";
        }
        final SourceFileException e = getSourceFileException(row);
        if (e == null) {
            return "";
        }
        switch (col) {
        case 0: if (isError(row)) {
                    return errorIcon;
                } else if (isWarning(row)) {
                    return warningIcon;
                }
                break;
        case 1: if (getSourceFileException(row) != null) {
                    return getSourceFileException(row).getMessage();
                }
                break;
        case 2: if (getSourceFileException(row) != null && getSourceFileException(row).getSourceArea() != null) {
                    return "line " + getSourceFileException(row).getSourceArea().getStartPosition().getRow();
                }
                break;
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
     * Set QEDEQ module.
     *
     * @param   qedeq   QEDEQ module.
     */
    public void setQedeq(final QedeqBo qedeq) {
        this.qedeq = qedeq;
    }

    public void fireTableDataChanged() {
        if (qedeq != null) {
            this.errors = qedeq.getErrors();
            this.warnings = qedeq.getWarnings();
        } else {
            this.errors = null;
            this.warnings = null;
        }
        maxErrors = 0;
        if (errors != null) {
            maxErrors += errors.size();
        }
        maxEntries = maxErrors;
        if (warnings != null) {
            maxEntries += warnings.size();
        }
        super.fireTableDataChanged();
    }

    /**
     * Get exception that is at given row.
     *
     * @param   row     Look into this row.
     * @return  Exception of this row. Might be <code>null</code> if row doesn't exist.
     */
    public SourceFileException getSourceFileException(final int row) {
        if (isError(row)) {
            return errors.get(row);
        } else if (isWarning(row)) {
            return warnings.get(row - maxErrors);
        }
        return null;
    }

    /**
     * Get number of error (that is position in error list) that is in the given row.
     * If at the given row is no error an IllegalArgumentException is thrown.
     *
     * @param   row     Look at this row.
     * @return  Error list number of error at this row.
     * @throws  IllegalArgumentException    At given row is no error.
     */
    public int getErrorNumber(final int row) {
        if (!isError(row)) {
            throw new IllegalArgumentException("This is not an error row: " + row);
        }
        return row;
    }

    /**
     * Get number of warning (that is position in warning list) that is in the given row.
     * If at the given row is no warning an IllegalArgumentException is thrown.
     *
     * @param   row     Look at this row.
     * @return  Warning list number of warning at this row.
     * @throws  IllegalArgumentException    At given row is no warning.
     */
    public int getWarningNumber(final int row) {
        if (!isWarning(row)) {
            throw new IllegalArgumentException("This is not an warning row: " + row);
        }
        return row - maxErrors;
    }

    /**
     * Is there an error at given row?
     *
     * @param   row     Look at this row.
     * @return  Is there an error?
     */
    public boolean isError(final int row) {
        if (row >= 0 && row < maxErrors) {
            return true;
        }
        return false;
    }

    /**
     * Is there an warning at given row?
     *
     * @param   row     Look at this row.
     * @return  Is there an warning?
     */
    public boolean isWarning(final int row) {
        if (row >= maxErrors && row < maxEntries) {
            return true;
        }
        return false;
    }

}
