/* $Id: LoadingStateDescriptions.java,v 1.1 2008/03/27 05:16:25 m31 Exp $
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
 * This interface provides constants of this package.
 *
 * @version $Revision: 1.1 $
 * @author Michael Meyling
 */
public interface LoadingStateDescriptions {

    /** Undefined state code. */
    public static final int STATE_CODE_UNDEFINED = 0;

    /** Undefined state description. */
    public static final String STATE_STRING_UNDEFINED = "undefined";

    /** Trying to access web address code. */
    public static final int STATE_CODE_LOCATING_WITHIN_WEB = 1;

    /** Trying to access web address description. */
    public static final String STATE_STRING_LOCATING_WITHIN_WEB = "locating within web";

    /** Trying to access web address failure code. */
    public static final int STATE_CODE_LOCATING_WITHIN_WEB_FAILED = 2;

    /** Trying to access web address failure description. */
    public static final String STATE_STRING_LOCATING_WITHIN_WEB_FAILED
        = "locating within web failed";

    /** Loading from web address code. */
    public static final int STATE_CODE_LOADING_FROM_WEB = 3;

    /** Loading from web address description. */
    public static final String STATE_STRING_LOADING_FROM_WEB = "loading from web";

    /** Loading from web address failure code. */
    public static final int STATE_CODE_LOADING_FROM_WEB_FAILED = 4;

    /** Loading from web address failure description. */
    public static final String STATE_STRING_LOADING_FROM_WEB_FAILED = "loading from web failed";

    /** Loading from local file buffer code. */
    public static final int STATE_CODE_LOADING_FROM_BUFFER = 5;

    /** Loading from local file buffer description. */
    public static final String STATE_STRING_LOADING_FROM_BUFFER = "loading from local buffer";

    /** Loading from local file buffer failed code. */
    public static final int STATE_CODE_LOADING_FROM_BUFFER_FAILED = 6;

    /** Loading from local file buffer failed description. */
    public static final String STATE_STRING_LOADING_FROM_BUFFER_FAILED
        = "loading from local buffer failed";

    /** Loading into memory code. */
    public static final int STATE_CODE_LOADING_INTO_MEMORY = 7;

    /** Loading into memory description. */
    public static final String STATE_STRING_LOADING_INTO_MEMORY = "loading into memory";

    /** Loading into memory failed code. */
    public static final int STATE_CODE_LOADING_INTO_MEMORY_FAILED = 8;

    /** Loading into memory failed description. */
    public static final String STATE_STRING_LOADING_INTO_MEMORY_FAILED
        = "loading into memory failed";

    /** Completely loaded code. */
    public static final int STATE_CODE_LOADED = 10;

    /** Completely loaded description. */
    public static final String STATE_STRING_LOADED = "loaded";

    /** Completely loaded code. */
    public static final int STATE_CODE_DELETED = -1;

    /** Completely loaded description. */
    public static final String STATE_STRING_DELETED = "deleted";

}
