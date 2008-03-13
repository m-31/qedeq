/* $Id: LogicalState.java,v 1.4 2008/01/26 12:39:09 m31 Exp $
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

package org.qedeq.kernel.common;

/**
 * Represents a mathematical module state. Every instance of this class is unique.
 *
 * @version $Revision: 1.4 $
 * @author  Michael Meyling
 */
public final class LogicalState {

    /** Unchecked. */
    public static final LogicalState STATE_UNCHECKED
        = new LogicalState(LogicalStateDescriptions.STATE_STRING_UNCHECKED,
            false, LogicalStateDescriptions.STATE_CODE_UNCHECKED);

    /** External checking. */
    public static final LogicalState STATE_EXTERNAL_CHECKING
        = new LogicalState(LogicalStateDescriptions.STATE_STRING_EXTERNAL_CHECKING,
            false, LogicalStateDescriptions.STATE_CODE_EXTERNAL_CHECKING);

    /** Successfully created. */
    public static final LogicalState STATE_EXTERNAL_CHECKING_FAILED
        =  new LogicalState(LogicalStateDescriptions.STATE_STRING_EXTERNAL_CHECKING_FAILED,
            true, LogicalStateDescriptions.STATE_CODE_EXTERNAL_CHECKING_FAILED);

    /** Internal checking phase. */
    public static final LogicalState STATE_INTERNAL_CHECKING
        = new LogicalState(LogicalStateDescriptions.STATE_STRING_INTERNAL_CHECKING,
            false, LogicalStateDescriptions.STATE_CODE_INTERNAL_CHECKING);

    /** Internal check failed. */
    public static final LogicalState STATE_INTERNAL_CHECKING_FAILED
        =  new LogicalState(LogicalStateDescriptions.STATE_STRING_INTERNAL_CHECKING_FAILED,
            true, LogicalStateDescriptions.STATE_CODE_INTERNAL_CHECKING_FAILED);


    /** Successfully completely checked. */
    public static final LogicalState STATE_CHECKED
        = new LogicalState(LogicalStateDescriptions.STATE_STRING_COMPLETELY_CHECKED,
            false, LogicalStateDescriptions.STATE_CODE_COMPLETELY_CHECKED);


    /** meaning of this state. */
    private final String text;

    /** is this state a failure? */
    private final boolean failure;

    /** Code for state. */
    private final int code;

    /**
     * Creates new module state.
     *
     * @param   text    meaning of this state, <code>null</code> is not permitted.
     * @param   failure is this a failure state?
     * @param   code    code of this state.
     * @throws  IllegalArgumentException    text == <code>null</code>
     */
    private LogicalState(final String text, final boolean failure, final int code) {
        this.text = text;
        if (this.text == null) {
            throw new IllegalArgumentException("text==null");
        }
        this.failure = failure;
        this.code = code;
    }

    /**
     * Get meaning of module state.
     *
     * @return  meaning of module state.
     */
    public String getText() {
        return this.text;
    }

    /**
     * Is this a failure state?
     *
     * @return  is this a failure state?
     */
    public boolean isFailure() {
        return this.failure;
    }

    /**
     * Get module state code.
     *
     * @return  Module state.
     */
    public int getCode() {
        return this.code;
    }

    public String toString() {
        return this.text;
    }

    public int hashCode() {
        return this.text.hashCode();
    }

    public boolean equals(final Object obj) {
        // every instance is unique
        return (this == obj);
    }

}
