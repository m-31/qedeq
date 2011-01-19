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

package org.qedeq.kernel.xml.handler.parser;

import org.qedeq.kernel.se.common.ErrorCodes;

/**
 * Error codes and messages for parser package.
 *
 * @author  Michael Meyling
 */
public interface ParserErrors extends ErrorCodes {

    /** Error code. */
    public static final int PARSER_PROGRAMMING_ERROR_CODE = 754123000;

    /** Error message. */
    public static final String PARSER_PROGRAMMING_ERROR_TEXT
        = "Parsing programming error.";


    /** Error code. */
    public static final int PARSER_CONFIGURATION_ERROR_CODE = 754123010;

    /** Error message. */
    public static final String PARSER_CONFIGURATION_ERROR_TEXT
        = "Parser configuration error.";


    /** Error code. */
    public static final int XML_FILE_PARSING_FAILED_CODE = 754123020;

    /** Error message. */
    public static final String XML_FILE_PARSING_FAILED_TEXT
        = "XML file parsing failed.";


    /** Error code. */
    public static final int PARSER_FACTORY_CONFIGURATION_CODE = 754123030;

    /** Error message. */
    public static final String PARSER_FACTORY_CONFIGURATION_TEXT
        = "Probably SAX Parser not in classpath, "
            + "add for example \"xercesImpl.jar\" and \"xml-apis.jar\".";


}
