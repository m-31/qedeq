/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.xml.common;

import java.io.IOException;

import org.qedeq.kernel.se.common.ErrorCodes;
import org.qedeq.kernel.se.common.QedeqException;
import org.xml.sax.SAXException;


/**
 * Exception that occurs during XML parsing. It specifies an syntactical error.
 * It can also mark a lack of inner consistence of something.
 * Also a programming error can lead to this exception.
 *
 * @author  Michael Meyling
 */
public final class XmlSyntaxException extends QedeqException implements ErrorCodes {

    /** Error code for Exceptions thrown by the SAXParser. */
    public static final int SAX_PARSER_EXCEPTION = 9001;

    /** Error code for unexpected tag. */
    public static final int UNEXPECTED_TAG_CODE = 9002;

    /** Unexpected tag message text. */
    public static final String UNEXPECTED_TAG_TEXT = "XML structure problem. Unexpected tag: ";

    /** Error code for unexpected character data. */
    public static final int UNEXPECTED_DATA_CODE = 9003;

    /** Unexpected tag message text, part one. */
    public static final String UNEXPECTED_DATA_TEXT = "XML structure problem. Unexpected character data in tag: ";

    /** Error code for missing attribute. */
    public static final int MISSING_ATTRIBUTE_CODE = 9004;

    /** Missing attribute text. */
    public static final String MISSING_ATTRIBUTE_TEXT_1 = "XML structure problem. Missing neccessary attribute: ";

    /** Missing attribute, part two. */
    public static final String MISSING_ATTRIBUTE_TEXT_2 = " in tag: ";

    /** Error code for empty attribute. */
    public static final int EMPTY_ATTRIBUTE_CODE = 9004;

    /** Missing attribute text. */
    public static final String EMPTY_ATTRIBUTE_TEXT_1 = "Missing attribute: ";

    /** Missing attribute, part two. */
    public static final String EMPTY_ATTRIBUTE_TEXT_2 = " in tag: ";

    /** Error code for a programming error. */
    public static final int IO_ERROR_CODE = 9900;

    /** Unexpected tag message text, part one. */
    public static final String IO_ERROR_TEXT = "An IO error occurred.";

    /** Error code for a sax parser error. */
    public static final int SAX_ERROR_CODE = 9910;

    /** Unexpected tag message text, part one. */
    public static final String SAX_ERROR_TEXT = "A XML syntax error occurred.";

    /** Error code for a programming error. */
    public static final int PROGRAMMING_ERROR_CODE = 9999;

    /** Unexpected tag message text, part one. */
    public static final String PROGRAMMING_ERROR_TEXT = "A programming error occurred.";

    /**
     * Constructor.
     *
     * @param   code    Error code.
     * @param   message Error message.
     */
    private XmlSyntaxException(final int code, final String message) {
        super(code, message);
    }

    /**
     * Constructor.
     *
     * @param   code    Error code.
     * @param   message Error message.
     * @param   e       Error cause.
     */
    private XmlSyntaxException(final int code, final String message, final Exception e) {
        super(code, message, e);
    }

    /**
     * Create exception for unexpected tag.
     *
     * @param   name    Tag name.
     * @return  Exception.
     */
    public static final XmlSyntaxException createUnexpectedTagException(
            final String name) {
        return new XmlSyntaxException(UNEXPECTED_TAG_CODE, UNEXPECTED_TAG_TEXT + name);
    }

    /**
     * Create exception for unexpected text data within a tag.
     *
     * @param   name    Tag name.
     * @param   value   Data found.
     * @return  Exception.
     */
    public static final XmlSyntaxException createUnexpectedTextDataException(
            final String name, final String value) {
        return new XmlSyntaxException(UNEXPECTED_DATA_CODE, UNEXPECTED_DATA_TEXT + name);
    }

    /**
     * Create exception for missing attribute within a tag.
     *
     * @param   name        Tag name.
     * @param   attribute   Attribute name.
     * @return  Exception.
     */
    public static final XmlSyntaxException createMissingAttributeException(
            final String name,
            final String attribute) {
        return new XmlSyntaxException(MISSING_ATTRIBUTE_CODE, MISSING_ATTRIBUTE_TEXT_1 + attribute
            + MISSING_ATTRIBUTE_TEXT_2 + name);
    }

    /**
     * Create exception for empty attribute within a tag.
     *
     * @param   name        Tag name.
     * @param   attribute   Attribute name.
     * @return  Exception.
     */
    public static final XmlSyntaxException createEmptyAttributeException(
            final String name, final String attribute) {
        return new XmlSyntaxException(EMPTY_ATTRIBUTE_CODE, EMPTY_ATTRIBUTE_TEXT_1 + attribute
            + EMPTY_ATTRIBUTE_TEXT_2 + name);
    }

    /**
     * Create exception for a IO error.
     *
     * @param   e       Exception.
     * @return  Created exception.
     */
    public static final XmlSyntaxException createByIOException(
            final IOException e) {
        return new XmlSyntaxException(IO_ERROR_CODE, IO_ERROR_TEXT, e);
    }

    /**
     * Create exception for a SAX parsing error.
     *
     * @param   e       Exception.
     * @return  Created exception.
     */
    public static final XmlSyntaxException createBySAXException(
            final SAXException e) {
        return new XmlSyntaxException(SAX_ERROR_CODE, SAX_ERROR_TEXT, e);
    }

    /**
     * Create exception for a programming error.
     *
     * @param   e       Exception.
     * @return  Created exception.
     */
    public static final XmlSyntaxException createByRuntimeException(
            final RuntimeException e) {
        return new XmlSyntaxException(PROGRAMMING_ERROR_CODE, PROGRAMMING_ERROR_TEXT, e);
    }

}
