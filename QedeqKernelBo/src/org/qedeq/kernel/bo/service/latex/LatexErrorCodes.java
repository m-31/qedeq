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

package org.qedeq.kernel.bo.service.latex;

import org.qedeq.kernel.common.ErrorCodes;


/**
 * Contains the error codes for LaTeX handling.
 * @author  Michael Meyling
 */
public interface LatexErrorCodes extends ErrorCodes {

    /** Error (or warning) number for: ending "}" for "\qref{" not found within 1024 characters.*/
    public static final int QREF_END_NOT_FOUND_CODE = 80007;

    /** Error (or warning) text for: ending "}" for "\qref{" not found. */
    public static final String QREF_END_NOT_FOUND_MSG = "ending \"}\" for \"\\qref{\" not found within 1024 characters";


    /** Error (or warning) number for: empty reference: "\qref{}". */
    public static final int QREF_EMPTY_CODE = 80008;

    /** Error (or warning) text for: empty reference: "\qref{}". */
    public static final String QREF_EMPTY_MSG = "empty reference: \"\\qref{}\"";


    /** Error (or warning) number for: ending "]" for "\qref{..}[" not found.*/
    public static final int QREF_SUB_END_NOT_FOUND_CODE = 80009;

    /** Error (or warning) text for: ending "]" for "\qref{..}[" not found. */
    public static final String QREF_SUB_END_NOT_FOUND_MSG = "ending \"]\" for \"\\qref{..}[\" not found";


    /** Error (or warning) number for: parsing of "\qref{" failed. */
    public static final int QREF_PARSING_EXCEPTION_CODE = 80010;

    /** Error (or warning) text for: parsing of "\qref{" failed. */
    public static final String QREF_PARSING_EXCEPTION_MSG = "parsing of \"\\qref{\" failed";


    /** Error (or warning) number for: ending "}" for "{" not found. */
    public static final int BRACKET_END_NOT_FOUND_CODE = 80017;

    /** Error (or warning) text for: ending "}" for "{" not found. */
    public static final String BRACKET_END_NOT_FOUND_MSG = "ending \"}\" for \"{\" not found";

    /** Error (or warning) number for: command not supported. */
    public static final int COMMAND_NOT_SUPPORTED_CODE = 80017;

    /** Error (or warning) text for: command not supported. */
    public static final String COMMAND_NOT_SUPPORTED_MSG = "LaTeX command not supported: ";



}
