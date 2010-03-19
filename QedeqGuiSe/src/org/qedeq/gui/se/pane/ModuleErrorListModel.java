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

import javax.swing.table.AbstractTableModel;

import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.common.SourceFileExceptionList;

/**
 * Table model for QEDEQ module specific error pane.
 *
 * @author  Michael Meyling
 */

public class ModuleErrorListModel extends AbstractTableModel {

    /** We want to show errors and warnings from this module. */
    private QedeqBo qedeq;

    public String getColumnName(final int column) {
        if (qedeq == null) {
            return "";
        }
        if (column == 1) {
            return (qedeq != null ? qedeq.getName() + " ("
                + qedeq.getModuleAddress() + ")" : "");
        } else if (column == 2) {
            return "Location";
        } else {
            return "";
        }
    }

    public int getRowCount() {
        if (qedeq == null) {
            return 0;
        }
        final SourceFileExceptionList errors = qedeq.getErrors();
        final SourceFileExceptionList warnings = qedeq.getWarnings();
        int size = 0;
        if (errors != null) {
            size += errors.size();
        }
        if (warnings != null) {
            size += warnings.size();
        }
        return size;
    }

    public int getColumnCount() {
        return 3;
    }

    public Object getValueAt(final int row, final int col) {
        if (qedeq == null) {
            return "";
        }
        final SourceFileExceptionList errors = qedeq.getErrors();
        final SourceFileExceptionList warnings = qedeq.getWarnings();
        int maxErrors = 0;
        if (errors != null) {
            maxErrors += errors.size();
        }
        if (row < maxErrors) {
            if (col == 0) {
                return "E";
            } else if (col == 1) {
                return errors.get(row).getMessage();
            } else if (col == 2 && errors.get(row).getSourceArea() != null) {
                return "line " + errors.get(row).getSourceArea().getStartPosition().getLine();
            }
        } else if (row >= maxErrors && warnings != null && row < maxErrors + warnings.size()) {
            if (col == 0) {
                return "W";
            } else if (col == 1) {
                return warnings.get(row - maxErrors).getMessage();
            } else if (col == 2 && warnings.get(row - maxErrors).getSourceArea() != null) {
                return "line " + warnings.get(row - maxErrors).getSourceArea().getStartPosition().getLine();
            }
        }
        return "";
    }

    public boolean isCellEditable(final int row, final int column) {
        return false;
    }

    public void setValueAt(final Object value, final int row, final int col) {
    }

    /**
     * Set QEDEQ module.
     *
     * @param   qedeq   QEDEQ module.
     */
    public void setQedeq(final QedeqBo qedeq) {
        this.qedeq = qedeq;
    }

}
