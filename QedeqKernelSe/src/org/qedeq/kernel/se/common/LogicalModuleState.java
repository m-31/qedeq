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

package org.qedeq.kernel.se.common;

/**
 * Represents a mathematical module state. Every instance of this class is unique.
 *
 * @author  Michael Meyling
 */
public final class LogicalModuleState implements State {

    /** Unchecked. */
    public static final LogicalModuleState STATE_UNCHECKED
        = new LogicalModuleState(LogicalModuleStateDescriptions.STATE_STRING_UNCHECKED,
            false, LogicalModuleStateDescriptions.STATE_CODE_UNCHECKED);

    /** External checking. */
    public static final LogicalModuleState STATE_EXTERNAL_CHECKING
        = new LogicalModuleState(LogicalModuleStateDescriptions.STATE_STRING_EXTERNAL_CHECKING,
            false, LogicalModuleStateDescriptions.STATE_CODE_EXTERNAL_CHECKING);

    /** External checking failed. */
    public static final LogicalModuleState STATE_EXTERNAL_CHECKING_FAILED
        =  new LogicalModuleState(LogicalModuleStateDescriptions.STATE_STRING_EXTERNAL_CHECKING_FAILED,
            true, LogicalModuleStateDescriptions.STATE_CODE_EXTERNAL_CHECKING_FAILED);

    /** Internal checking phase. */
    public static final LogicalModuleState STATE_INTERNAL_CHECKING
        = new LogicalModuleState(LogicalModuleStateDescriptions.STATE_STRING_INTERNAL_CHECKING,
            false, LogicalModuleStateDescriptions.STATE_CODE_INTERNAL_CHECKING);

    /** Internal check failed. */
    public static final LogicalModuleState STATE_INTERNAL_CHECKING_FAILED
        =  new LogicalModuleState(LogicalModuleStateDescriptions.STATE_STRING_INTERNAL_CHECKING_FAILED,
            true, LogicalModuleStateDescriptions.STATE_CODE_INTERNAL_CHECKING_FAILED);


    /** Successfully completely checked. */
    public static final LogicalModuleState STATE_CHECKED
        = new LogicalModuleState(LogicalModuleStateDescriptions.STATE_STRING_COMPLETELY_CHECKED,
            false, LogicalModuleStateDescriptions.STATE_CODE_COMPLETELY_CHECKED);


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
    private LogicalModuleState(final String text, final boolean failure, final int code) {
        this.text = text;
        if (this.text == null) {
            throw new IllegalArgumentException("text==null");
        }
        this.failure = failure;
        this.code = code;
    }

    public String getText() {
        return this.text;
    }

    public boolean isFailure() {
        return this.failure;
    }

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
