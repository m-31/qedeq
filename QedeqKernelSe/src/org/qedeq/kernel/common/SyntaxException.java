/* $Id: SyntaxException.java,v 1.1 2007/04/12 23:50:10 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2007,  Michael Meyling <mime@qedeq.org>.
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

import java.net.URL;

import org.xml.sax.SAXParseException;


/**
 * Exception that occurs during XML parsing. It specifies an syntactical error.
 * It can also mark a lack of inner consistence of something.
 * Also a programming error can lead to this exception.
 *
 * @version $Revision: 1.1 $
 * @author    Michael Meyling
 */
public final class SyntaxException extends QedeqException {

    /** Error code for Exceptions thrown by the SAXParser. */
    public static final int SAX_PARSER_EXCEPTION = 9001;

    /** Error code for unexpected tag. */
    public static final int UNEXPECTED_TAG_CODE = 9002;

    /** Unexpected tag message text. */
    public static final String UNEXPECTED_TAG_TEXT = "Unexpected tag: ";

    /** Error code for unexpected character data. */
    public static final int UNEXPECTED_DATA_CODE = 9003;

    /** Unexpected tag message text, part one. */
    public static final String UNEXPECTED_DATA_TEXT = "Unexpected character data in tag: ";

    /** Error code for missing attribute. */
    public static final int MISSING_ATTRIBUTE_CODE = 9004;

    /** Missing attribute text. */
    public static final String MISSING_ATTRIBUTE_TEXT_1 = "Missing attribute: ";

    /** Missing attribute, part two. */
    public static final String MISSING_ATTRIBUTE_TEXT_2 = " in tag: ";

    /** Error code for empty attribute. */
    public static final int EMPTY_ATTRIBUTE_CODE = 9004;

    /** Missing attribute text. */
    public static final String EMPTY_ATTRIBUTE_TEXT_1 = "Missing attribute: ";

    /** Missing attribute, part two. */
    public static final String EMPTY_ATTRIBUTE_TEXT_2 = " in tag: ";

    /** Error code for a programming error. */
    public static final int PROGRAMMING_ERROR_CODE = 9999;

    /** Unexpected tag message text, part one. */
    public static final String PROGRAMMING_ERROR_TEXT = "A programming error occurred.";

    /** Error location. */
    private SourcePosition position;



    /**
     * Constructor.
     *
     * @param   code    Error code.
     * @param   message Error message.
     */
    private SyntaxException(final int code, final String message) {
        super(code, message);
    }

    /**
     * Constructor.
     *
     * @param   code    Error code.
     * @param   message Error message.
     * @param   e       Error cause.
     */
    private SyntaxException(final int code, final String message, final RuntimeException e) {
        super(code, message, e);
    }

    /**
     * Constructor.
     *
     * @param   e       Exception thrown by the {@link javax.xml.parsers.SAXParser}.
     * @param   url     Parsed file.
     */
    private SyntaxException(final SAXParseException e, final URL url) {
        super(SAX_PARSER_EXCEPTION, e.getMessage(), e);
        position = new SourcePosition(url, e.getLineNumber(),
            e.getColumnNumber());
    }

    /**
     * Get error position.
     *
     * @return  Error position.
     */
    public final SourcePosition getErrorPosition() {
        return position;
    }


    /**
     * Set error position.
     *
     * @param   position    Error position.
     */
    public final void setErrorPosition(final SourcePosition position) {
        this.position = position;
    }

    /**
     * Create exception for unexpected tag.
     *
     * @param   name    Tag name.
     * @return  Exception.
     */
    public static final SyntaxException createUnexpectedTagException(final String name) {
        return new SyntaxException(UNEXPECTED_TAG_CODE, UNEXPECTED_TAG_TEXT + name);
    }

    /**
     * Create exception for unexpected text data within a tag.
     *
     * @param   name    Tag name.
     * @param   value   Data found.
     * @return  Exception.
     */
    public static final SyntaxException createUnexpectedTextDataException(final String name,
            final String value) {
        return new SyntaxException(UNEXPECTED_DATA_CODE, UNEXPECTED_DATA_TEXT + name);
    }

    /**
     * Create exception for missing attribute within a tag.
     *
     * @param   name        Tag name.
     * @param   attribute   Attribute name.
     * @return  Exception.
     */
    public static final SyntaxException createMissingAttributeException(final String name,
            final String attribute) {
        return new SyntaxException(MISSING_ATTRIBUTE_CODE, MISSING_ATTRIBUTE_TEXT_1 + attribute
            + MISSING_ATTRIBUTE_TEXT_2 + name);
    }

    /**
     * Create exception for empty attribute within a tag.
     *
     * @param   name        Tag name.
     * @param   attribute   Attribute name.
     * @return  Exception.
     */
    public static final SyntaxException createEmptyAttributeException(final String name,
            final String attribute) {
        return new SyntaxException(EMPTY_ATTRIBUTE_CODE, EMPTY_ATTRIBUTE_TEXT_1 + attribute
            + EMPTY_ATTRIBUTE_TEXT_2 + name);
    }

    /**
     * Create exception for {@link org.xml.sax.SAXException}.
     *
     * @param   e       Exception thrown by the {@link javax.xml.parsers.SAXParser}.
     * @param   url     Parsed file.
     * @return  Created exception.
     */
    public static final SyntaxException createBySAXParseException(final SAXParseException e,
            final URL url) {
        return new SyntaxException(e, url);
    }

    /**
     * Create exception for a programming error.
     *
     * @param   e       Exception.
     * @return  Created exception.
     */
    public static final SyntaxException createByRuntimeException(final RuntimeException e) {
        return new SyntaxException(PROGRAMMING_ERROR_CODE, PROGRAMMING_ERROR_TEXT, e);
    }

}
