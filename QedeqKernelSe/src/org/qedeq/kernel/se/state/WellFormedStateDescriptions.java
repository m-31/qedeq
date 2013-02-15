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

/**
 * This interface provides primitive constants for the {@link WellFormedState}.
 *
 * @author  Michael Meyling
 */
public interface WellFormedStateDescriptions {

    /** Code for unchecked state. */
    public static final int STATE_CODE_UNCHECKED = 0;

    /** Description for unchecked state. */
    public static final String STATE_STRING_UNCHECKED = "unchecked";

    /** Code for external checking phase. */
    public static final int STATE_CODE_EXTERNAL_CHECKING = 16;

    /** Description for external internal checking phase. */
    public static final String STATE_STRING_EXTERNAL_CHECKING = "checking imports";

    /** Code for external check failure. */
    public static final int STATE_CODE_EXTERNAL_CHECKING_FAILED = 17;

    /** Description for external check failure. */
    public static final String STATE_STRING_EXTERNAL_CHECKING_FAILED = "checking imports failed";

    /** Code for internal checking phase. */
    public static final int STATE_CODE_INTERNAL_CHECKING = 18;

    /** Description for internal checking phase. */
    public static final String STATE_STRING_INTERNAL_CHECKING = "wf checking";

    /** Code for check failure. */
    public static final int STATE_CODE_INTERNAL_CHECKING_FAILED = 19;

    /** Description for check failure. */
    public static final String STATE_STRING_INTERNAL_CHECKING_FAILED = "wf checking failed";

    /** Code for successfully completely checked state. */
    public static final int STATE_CODE_COMPLETELY_CHECKED = 20;

    /** Description for successfully completely checked state. */
    public static final String STATE_STRING_COMPLETELY_CHECKED = "well formed";

}
