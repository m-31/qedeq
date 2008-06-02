/* $Id: SimpleMathParser.java,v 1.4 2008/03/27 05:16:27 m31 Exp $
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

import org.qedeq.base.io.TextInput;

/**
 * Parse term or formula data into {@link org.qedeq.kernel.parser.Term}s.
 * This parser uses simple ASCII text operators.
 *
 * @version $Revision: 1.4 $
 * @author  Michael Meyling
 */
public class SimpleMathParser extends MathParser {

    /** Characters that are always tokens itself. */
    private static final String SEPARATORS = "()[],{}";

    /**
     * Constructor.
     *
     * @param   input       Parse this input.
     * @param   operators   List of operators.
     */
    public SimpleMathParser(final TextInput input, final List operators) {
        super(new MementoTextInput(input), operators);
/*
        operators.add(new Operator("~",      "NOT",   110, 1, 1, 1));
        operators.add(new Operator("-",      "NOT",   110, 1, 1, 1));
        operators.add(new Operator("&",      "AND",   100, 0, 2));
        operators.add(new Operator("|",      "OR",     90, 0, 2));
//        operators.put("v",      new Operator("v",      "OR",     90, 0, 2));
        operators.add(new Operator("->",     "IMPL",   80, 0, 2, 2));
        operators.add(new Operator("=>",     "IMPL",   80, 0, 2, 2));
        operators.add(new Operator("<->",    "EQUI",   80, 0, 2));
        operators.add(new Operator("<=>",    "EQUI",   80, 0, 2));
        operators.add(new Operator("all",    "ALL",    40, 1, 2, 3));
        operators.add(new Operator("exists", "EXISTS", 40, 1, 2, 3));
        operators.add(new Operator("in",     "IN",    200, 0, 2, 2));
        operators.add(new Operator("=",      "EQUAL", 200, 0, 2));
        operators.add(new Operator("{", ",", "}", "SET", 200, 0));
        operators.add(new Operator("{", ":", "}", "SETPROP", 200, 2, 2));
*/
    }

    protected final String readToken() {
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
            return "" + (char) readChar();
        }
        final StringBuffer token = new StringBuffer();
        while (!eof() && !Character.isWhitespace((char) getChar())
                && SEPARATORS.indexOf(getChar()) < 0) {
            token.append((char) readChar());
            if (null != getOperator(token.toString())) {
                if (getChar() >= 0) {
                    final char c = (char) getChar();
                    if (null != getOperator(token.toString() + c)) {
                        continue;
                    }
                }
                break;
            }
        }
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
