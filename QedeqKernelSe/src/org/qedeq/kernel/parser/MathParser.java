/* $Id: MathParser.java,v 1.5 2007/05/10 00:37:51 m31 Exp $
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

package org.qedeq.kernel.parser;

import java.util.ArrayList;
import java.util.List;

import org.qedeq.kernel.trace.Trace;

/**
 * Parse term or formula data into {@link org.qedeq.kernel.parser.Term}s.
 *
 * @version $Revision: 1.5 $
 * @author  Michael Meyling
 */
public abstract class MathParser {

    /** Input source to parse. */
    private final MementoTextInput input;

    /** List of operators. */
    private List operators;

    /**
     * Constructor.
     *
     * @param   input       Input source to parse.
     * @param   operators   Operator list.
     */
    public MathParser(final MementoTextInput input, final List operators) {
        this.input = input;
        this.operators = operators;
    }

    protected final List getOperators() {
        return operators;
    }

    /**
     * Reads (maximal possible) Term from input.
     *
     * @return  Read term.
     * @throws  ParserException Parsing failed.
     */
    public final Term readTerm() throws ParserException {
        final String method = "Term readTerm()";
        Trace.begin(this, method);
        try {
            final Term term = readMaximalTerm(0);
            if (eot(getToken())) {
                readToken();
            }
            return term;
        } finally {
            Trace.end(this, method);
        }
    }

    /**
     * Reads next "maximal" term from input. The following input doesn't make the the term more
     * complete. Respects the priority of coming operators by comparing it
     * to the given value.
     *
     * @param priority  next formula will be the last, if
     *                  connecting operator has higher priority
     * @return  Read term.
     * @throws  ParserException Parsing failed.
     */
    private final Term readMaximalTerm(final int priority) throws ParserException {
        final String method = "readMaximalTerm(int)";
        Trace.begin(this, method);
        Term term = null;
        try {
            if (eot(getToken())) {
                Trace.param(this, method, "return term", "null");
                return null;
            }
            term = readPrefixTerm();
            term = addNextInfixTerms(priority, term);
            Trace.param(this, method, "return term",
                (term != null ? term.getQedeq() : "null"));
            return term;
        } finally {
            Trace.end(this, method);
        }
    }

    private Term addNextInfixTerms(final int priority, final Term initialTerm)
            throws ParserException {
        final String method = "Term addNextInfixTerms(int, Term)";
        Trace.begin(this, method);
        Term term = initialTerm;
        try {
            Operator newOperator = null;
            Operator oldOperator = null;

            do {
                markPosition();
                newOperator = readOperator();  // we expect an unique infix operator
                Trace.param(this, method, "newOperator",
                    (newOperator != null ? newOperator.getQedeq() : "null"));
                if (newOperator == null || newOperator.getPriority() <= priority) {
                    Trace.trace(this, method, "newOperator is null or of less priority");
                    rewindPosition();
                    Trace.param(this, method, "read term",
                        (term != null ? term.getQedeq() : "null"));
                    return term;
                }
                if (newOperator.isPrefix()) {
                    Trace.trace(this, method, "newOperator is prefix");
                    // TODO mime 20060313: try to read further arguments
                    rewindPosition();
                    Trace.param(this, method, "read term",
                        (term != null ? term.getQedeq() : "null"));
                    return term;
                }
                if (newOperator.isPostfix()) {
                    rewindPosition();
                    throw new IllegalArgumentException("Postfix Operators not yet supported");
                }
                clearMark();
                if (oldOperator == null || oldOperator.getPriority() >= newOperator.getPriority()) {
                    Trace.trace(this, method, "oldOperator is null or has more priority than new");
                    Term term2 = readMaximalTerm(newOperator.getPriority());
                    if (term2 == null) {
    //                      TODO mime 20060313: 2 could be false if newOperator == oldOperator
                        throw new TooFewArgumentsException(getPosition(), 2);
                    }
                    if (oldOperator == newOperator) {
                        if (oldOperator.getMax() != -1 && term.size() + 1 >= oldOperator.getMax()) {
                            throw new TooMuchArgumentsException(getPosition(), oldOperator,
                                oldOperator.getMax());
                        }
                        Trace.trace(this, method, "new term is added to old term");
                        term.addArgument(term2);
                    } else {
                        // old term is first argument of new operator
                        Trace.trace(this, method, "old term is first argument of new operator");
                        term = new Term(newOperator, term);
                        term.addArgument(term2);
                    }
                } else {
                    Trace.trace(this, method,
                        "oldOperator is not null or has less priority than new");
                    Term term2 = readMaximalTerm(newOperator.getPriority());
                    if (term2 == null) {
    //                      TODO mime 20060313: 2 could be false if newOperator == oldOperator
                        throw new TooFewArgumentsException(getPosition(), 2);
                    }
                    term = new Term(newOperator, term);
                    term.addArgument(term2);
                }
                oldOperator = newOperator;
            } while (true);
        } finally {
            Trace.end(this, method);
        }
    }

    /**
     * Read next following term. This is a complete term but some infix operator
     * or some terms for an infix operator might follow.
     *
     * @return  Read term.
     * @throws  ParserException Parsing failed.
     */
    private final Term readPrefixTerm() throws ParserException {
        final String method = "readPrefixTerm()";
        Trace.begin(this, method);
        Term term = null;
        try {
            final List readOperators = readOperators();   // there might be several prefix operators
            if (readOperators != null && readOperators.size() > 0) {
                Trace.trace(this, method, "operators found");
                term = readPrefixOperator(readOperators);
            } else { // no operator found
                Trace.trace(this, method, "no operators found");
                final String token;
                token = getToken();
                if (token == null) {
                    Trace.param(this, method, "read term", "null");
                    return null;
                }
                if ("(".equals(token)) {
                    readToken();
                    Trace.trace(this, method, "start bracket found: " + token);
                    term = readMaximalTerm(0);
                    final String lastToken = readToken();
                    if (!")".equals(lastToken)) {
                        throw new ClosingBracketMissingException(getPosition(), "(", lastToken);
                    }

                } else if ("[".equals(token)) {
                    readToken();
                    Trace.trace(this, method, "start bracket found: " + token);
                    term = readMaximalTerm(0);
                    final String lastToken = readToken();
                    if (!"]".equals(lastToken)) {
                        throw new ClosingBracketMissingException(getPosition(), "[", lastToken);
                    }
                } else {
                    readToken();
                    Trace.param(this, method, "atom", token);
                    term = new Term(new TermAtom(token));
                }
            }
            Trace.param(this, method, "read term",
                (term != null ? term.getQedeq() : "null"));
            return term;
        } finally {
            Trace.end(this, method);
        }
    }

    /**
     * Try to parse an prefix operator and its operands from the input. Tries first operator,
     * second operator and so on. If the last one fails an appropriate exception is thrown.
     *
     * @param   operators   Prefix operator list.
     * @return  Resulting term.
     * @throws  ParserException Parsing failed.
     */
    private Term readPrefixOperator(final List operators) throws ParserException {
        Term term = null;
        markPosition();
        for (int number = 0; number < operators.size(); number++) {
            rewindPosition();
            markPosition();
            Operator operator = (Operator) operators.get(number);

            if (!operator.isPrefix()) {
                clearMark();
                throw new UnexpectedOperatorException(getPosition(), operator);
            }

            term = new Term(operator);

            if (operator.isFunction()) {
                // constants should have no argument list
                if (operator.getMax() == 0) {
                    break;
                }
                List list = readTupel();
                if (list == null) {     // here we don't distinguish between "a()" and "a"
                    list = new ArrayList();
                }
                // doesn't have enough arguments
                if (list.size() < operator.getMin()) {
                    if (number + 1 < operators.size()) {
                        continue;   // try again
                    }
                    clearMark();
                    throw new TooFewArgumentsException(getPosition(),
                        operator.getMin());
                }
                // has to much arguments
                if (operator.getMax() != -1 && list.size() > operator.getMax()) {
                    if (number + 1 < operators.size()) {
                        continue;   // try again
                    }
                    clearMark();
                    throw new TooMuchArgumentsException(getPosition(), operator,
                        operator.getMax());
                }
                for (int i = 0; i < list.size(); i++) {
                    term.addArgument((Term) list.get(i));
                }
                break;
            }

            int i = 0;
            while (i < operator.getMin()) {
                if (i > 0 && operator.getSeparatorSymbol() != null) {
                    final String separator = getToken();
                    if (!operator.getSeparatorSymbol().equals(separator)) {
                        if (number + 1 < operators.size()) {
                            continue;
                        }
                        clearMark();
                        throw new SeparatorNotFoundException(getPosition(),
                            operator.getSeparatorSymbol());
                    }
                    readToken();
                }
                final Term add = readMaximalTerm(operator.getPriority());
                if (add == null) {
                    if (number + 1 < operators.size()) {
                        continue;
                    }
                    clearMark();
                    throw new TooFewArgumentsException(getPosition(), operator.getMin());
                }
                term.addArgument(add);
                i++;
            }
            while (operator.getMax() == -1 || i < operator.getMax()) {
                if (i > 0 && operator.getSeparatorSymbol() != null) {
                    final String separator = getToken();
                    if (!operator.getSeparatorSymbol().equals(separator)) {
                        break;
                    }
                    readToken();
                }
                if (operator.getEndSymbol() != null) {
                    final String end = getToken();
                    if (operator.getEndSymbol().equals(end)) {
                        break;
                    }
                }
                Term add = null;
                markPosition();
                try {
                    add = readMaximalTerm(operator.getPriority());
                    clearMark();
                } catch (Exception e) {
                    rewindPosition();
                }
                if (add == null) {
                    break;
                }
                term.addArgument(add);
                i++;
            }
            if (operator.getEndSymbol() != null) {
                final String end = getToken();
                if (!operator.getEndSymbol().equals(end)) {
                    if (number + 1 < operators.size()) {
                        continue;
                    }
                    clearMark();
                    throw new EndSymbolNotFoundException(getPosition(),
                        operator.getEndSymbol());
                }
                readToken();
            }
            break;
        }
        clearMark();
        return term;
    }

    /**
     * Read n-tupel. This is a list of terms encapsulated by "(" and ")" and the terms are separated
     * by ",".
     *
     * @return  List of terms.  <code>null</code> if no bracket followed.
     * @throws  ParserException Parsing failed.
     */
    private final List readTupel() throws ParserException {
        final String method = "List readTupel()";
        Trace.begin(this, method);
        try {
            final String firstToken;
            firstToken = getToken();
            if (!"(".equals(firstToken)) {
                Trace.trace(this, method, "no start bracket found");
                return null;
            }
            readToken();    // read "("
            List list = new ArrayList();
            Term term = null;
            while (null != (term = readMaximalTerm(0))) {
                list.add(term);
                final String separatorToken = getToken();
                if (!",".equals(separatorToken)) {
                    break;
                }
                readToken();    // read ","
            }
            final String lastToken = readToken();
            if (!")".equals(lastToken)) {
                throw new ClosingBracketMissingException(getPosition(), ")", lastToken);
            }
            return list;
        } finally {
            Trace.end(this, method);
        }
    }

    /**
     * Read next operator from input.
     *
     * @return  Found operator, maybe <code>null</code>.
     */
    private final Operator readOperator() {
        final String method = "Operator readOperator()";
        Trace.begin(this, method);

        try {
            markPosition();
            final String token;
            token = readToken();
            if (token == null) {
                rewindPosition();
                Trace.trace(this, method, "no operator found");
                return null;
            }
            final Operator operator = getOperator(token);
            if (operator == null) {
                rewindPosition();
                Trace.trace(this, method, "no operator found");
                return null;
            }
            clearMark();
            Trace.param(this, method, "operator", operator.getQedeq());
            return operator;
        } finally {
            Trace.end(this, method);
        }
    }

    /**
     * Read next operators from input. Because the token that identifies an operator might be not
     * unique we could get multiple operators that start with that token. So this method reads
     * the operator token (if any) and returns a list of operators that start with that token.
     *
     * @return  Found operators, maybe <code>null</code> if no matching found..
     */
    private final List readOperators() {
        final String method = "List readOperators()";
        Trace.begin(this, method);

        try {
            markPosition();
            final String token;
            token = readToken();
            if (token == null) {
                rewindPosition();
                Trace.trace(this, method, "no operators found");
                return null;
            }
            final List ops = getOperators(token);
            if (ops == null || ops.size() == 0) {
                rewindPosition();
                Trace.trace(this, method, "no operators found");
                return null;
            }
            clearMark();
            for (int i = 0; i < ops.size(); i++) {
                Trace.param(this, method, "operator[" + i + "]", ops.get(i));
            }
            return ops;
        } finally {
            Trace.end(this, method);
        }
    }

    /**
     * Get an operator for that token. If there are more than one possibilities the first matching
     * is returned.
     *
     * @param token  Get an operator for this token.
     * @return  Operator. Might be <code>null</code>.
     */
    protected abstract Operator getOperator(String token);

    /**
     * Get operators for that token. If there are more than one possibilities all matching are
     * returned.
     *
     * @param token  Get operators for this token.
     * @return  Operators. Might be <code>null</code>.
     */
    protected abstract List getOperators(String token);

    /**
     * Read next token from input and move reading position.
     * A token is a recognized character sequence. A token is no
     * elementary whitespace. Also a dosn't start or end with
     * elementary whitespace.
     *
     * @return  Token read, is <code>null</code> if end of data reached.
     */
    protected abstract String readToken();

    /**
     * Read next token from input but don't move reading position.
     * A token is a recognised character sequence. A token is no
     * elementary whitespace. Also a dosn't start or end with
     * elementary whitespace.
     *
     * @return  Token read, is <code>null</code> if end of data reached.
     */
    public final String getToken() {
        markPosition();
        final String result = readToken();
        rewindPosition();
        return result;
    }

    /**
     * Remember current position.
     */
    protected final void markPosition() {
        input.markPosition();
    }

    /**
     * Rewind to previous marked position. Also clears the mark.
     *
     * @return  Current position before pop.
     */
    protected final long rewindPosition() {
        return input.rewindPosition();
    }

    /**
     * Forget last remembered position.
     */
    protected final void clearMark() {
        input.clearMark();
    }

    /**
     * Get byte position.
     *
     * @return  Position.
     */
    private long getPosition() {
        return input.getPosition();
    }

    /**
     * Reads a single character and does not change the reading
     * position.
     *
     * @return  character read, if there are no more chars
     *          <code>Character.MAX_VALUE</code> is returned
     */
    protected final int getChar() {
        return input.getChar();
    }

    /**
     * Reads a single character and increments the reading position
     * by one.
     *
     * @return  character read, if there are no more chars
     *          <code>Character.MAX_VALUE</code> is returned
     */
    protected final int readChar() {
        return input.readChar();
    }

    /**
     * Is this an end of term token?
     *
     * @param   token   Check this token.
     * @return  Is this an end of term token.
     */
    protected abstract boolean eot(String token);

    /**
     * Are there still any characters to read?
     *
     * @return  Anything left for reading further?
     */
    public final boolean eof() {
        return input.eof();
    }

    /**
     * Get rewind stack size.
     *
     * @return  Rewind stack size.
     */
    public final int getRewindStackSize() {
        return input.getRewindStackSize();
    }

}
