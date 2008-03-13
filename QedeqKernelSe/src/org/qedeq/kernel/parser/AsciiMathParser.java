/* $Id: AsciiMathParser.java,v 1.5 2008/01/26 12:39:09 m31 Exp $
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

package org.qedeq.kernel.parser;

import java.util.ArrayList;
import java.util.List;

import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.utility.TextInput;

/*
 * LATER mime 20080131: refactor
 *
 */

/**
 * Parse term or formula data into {@link org.qedeq.kernel.parser.Term}s.
 * This parser uses simple ASCII text operators.
 *
 * @version $Revision: 1.5 $
 * @author  Michael Meyling
 */
public final class AsciiMathParser extends MathParser {

    /** This class. */
    private static final Class CLASS = AsciiMathParser.class;

    /** Separators for tokens. */
    private static final String SEPARATORS = "()[],{}";

    /**
     * Constructor.
     *
     * @param   input       Parse this input.
     * @param   operators   Operator definitions.
     */
    public AsciiMathParser(final TextInput input, final List operators) {
        super(new MementoTextInput(input), operators);
    }

    protected final String readToken() {
        final String method = "readToken()";
        int lines = 0;
        while (getChar() != -1 && Character.isWhitespace((char) getChar())) {
            if ('\n' == (char) getChar()) {
                lines++;
            }
            readChar();
        }
        if (lines > 1) {
            return "";
        }
        if (eof()) {
            return null;
        }
        if (SEPARATORS.indexOf(getChar()) >= 0) {
            Trace.param(CLASS, this, method, "Read token", "" + (char) getChar());
            return "" + (char) readChar();
        }
        final StringBuffer token = new StringBuffer();
        String operator = null;
        markPosition();
        while (!eof() && !Character.isWhitespace((char) getChar())
                && SEPARATORS.indexOf(getChar()) < 0) {
            token.append((char) readChar());
            if (null != getOperator(token.toString())) {
                operator = token.toString();
                clearMark();
                markPosition();
            }
        }
        if (operator != null) {
            rewindPosition();
            token.setLength(0);
            token.append(operator);
        } else {
            clearMark();
        }
        Trace.param(CLASS, this, method, "Read token", token);
        return token.toString();
    }

    protected final Operator getOperator(final String token) {
        Operator result = null;
        if (token == null) {
            return result;
        }
        for (int i = 0; i < getOperators().size(); i++) {
            if (token.equals(((Operator) getOperators().get(i)).getStartSymbol())) {
                result = (Operator) getOperators().get(i);
                break;
            }
        }
        return result;
    }

    protected final List getOperators(final String token) {
        final List result = new ArrayList();
        if (token == null) {
            return result;
        }
        for (int i = 0; i < getOperators().size(); i++) {
            if (token.equals(((Operator) getOperators().get(i)).getStartSymbol())) {
                result.add(getOperators().get(i));
            }
        }
        return result;
    }

    protected boolean eot(final String token) {
        return token == null || token.trim().length() == 0;
    }

}
