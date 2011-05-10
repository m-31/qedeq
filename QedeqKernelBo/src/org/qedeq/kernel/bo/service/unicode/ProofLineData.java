/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.service.unicode;

/**
 * Contains printing data for a formal proof line.
 *
 * @author  Michael Meyling
 */
public class ProofLineData {

    /** Current Label of formal proof line. */
    private String lineLabel;

    /** String representation of formal proof line formula. */
    private String[] formula;

    /** String representation of formal proof line reason. */
    private String[] reason;

    /**
     * Constructs empty container.
     */
    public ProofLineData() {
        init();
    }

    /**
     * Construct new proof line data.
     *
     * @param   lineLabel   Proof line label.
     * @param   formula     Formula strings.
     * @param   reason      Reason strings.
     */
    public ProofLineData(final String lineLabel, final String[] formula, final String[] reason) {
        this.lineLabel = lineLabel;
        this.formula = formula;
        this.reason = reason;
    }

    /**
     * Empty data.
     */
    public void init() {
        this.lineLabel = "";
        this.formula = new String[0];
        this.reason = new String[0];
    }

    /**
     * Exists formula data or reason data?
     *
     * @return  Is there any reason or formula data?
     */
    public boolean containsData() {
        return (0 != lines());
    }

    /**
     * How many lines do we have? This is maximum of formula lines and reason lines.
     *
     * @return  Number of lines.
     */
    public int lines() {
        return Math.max(getFormula().length, getReason().length);
    }

    public String getLineLabel() {
        return lineLabel;
    }

    public void setLineLabel(final String lineLabel) {
        this.lineLabel = lineLabel;
    }

    public String[] getFormula() {
        return formula;
    }

    public void setFormula(final String[] formula) {
        this.formula = formula;
    }

    public String[] getReason() {
        return reason;
    }

    public void setReason(final String[] reason) {
        this.reason = reason;
    }
}