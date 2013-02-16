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

package org.qedeq.kernel.se.state;

import org.qedeq.kernel.se.common.State;

/**
 * Represents a mathematical module state. Every instance of this class is unique.
 *
 * @author  Michael Meyling
 */
public final class WellFormedState extends AbstractState implements State {

    /** Unchecked. */
    public static final WellFormedState STATE_UNCHECKED
        = new WellFormedState(WellFormedStateDescriptions.STATE_STRING_UNCHECKED,
            false, WellFormedStateDescriptions.STATE_CODE_UNCHECKED);

    /** External checking. */
    public static final WellFormedState STATE_EXTERNAL_CHECKING
        = new WellFormedState(WellFormedStateDescriptions.STATE_STRING_EXTERNAL_CHECKING,
            false, WellFormedStateDescriptions.STATE_CODE_EXTERNAL_CHECKING);

    /** External checking failed. */
    public static final WellFormedState STATE_EXTERNAL_CHECKING_FAILED
        =  new WellFormedState(WellFormedStateDescriptions.STATE_STRING_EXTERNAL_CHECKING_FAILED,
            true, WellFormedStateDescriptions.STATE_CODE_EXTERNAL_CHECKING_FAILED);

    /** Internal checking phase. */
    public static final WellFormedState STATE_INTERNAL_CHECKING
        = new WellFormedState(WellFormedStateDescriptions.STATE_STRING_INTERNAL_CHECKING,
            false, WellFormedStateDescriptions.STATE_CODE_INTERNAL_CHECKING);

    /** Internal check failed. */
    public static final WellFormedState STATE_INTERNAL_CHECKING_FAILED
        =  new WellFormedState(WellFormedStateDescriptions.STATE_STRING_INTERNAL_CHECKING_FAILED,
            true, WellFormedStateDescriptions.STATE_CODE_INTERNAL_CHECKING_FAILED);


    /** Successfully completely checked. */
    public static final WellFormedState STATE_CHECKED
        = new WellFormedState(WellFormedStateDescriptions.STATE_STRING_COMPLETELY_CHECKED,
            false, WellFormedStateDescriptions.STATE_CODE_COMPLETELY_CHECKED);


    /**
     * Creates new module state.
     *
     * @param   text    meaning of this state, <code>null</code> is not permitted.
     * @param   failure is this a failure state?
     * @param   code    code of this state.
     * @throws  IllegalArgumentException    text == <code>null</code>
     */
    private WellFormedState(final String text, final boolean failure, final int code) {
        super(text, failure, code);
    }

}
