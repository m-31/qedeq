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

package org.qedeq.kernel.bo.parser;

import java.util.Stack;

import org.qedeq.base.io.TextInput;

/**
 * Remember TextInput positions.
 *
 * @author  Michael Meyling
 */
public class MementoTextInput {

    /** For remembering input positions. */
    private final Stack stack = new Stack();

    /** Input source to parse. */
    private final TextInput input;

    /**
     * Constructor.
     *
     * @param   input   Input source to parse.
     */
    public MementoTextInput(final TextInput input) {
        this.input = input;
    }

    /**
     * Remember current position.
     */
    public void markPosition() {
        stack.push(new Integer(input.getPosition()));
    }

    /**
     * Rewind to previous marked position. Also clears the mark.
     *
     * @return  Current position before pop.
     */
    public long rewindPosition() {
        final long oldPosition = getPosition();
        input.setPosition(((Integer) stack.pop()).intValue());
        return oldPosition;
    }

    /**
     * Forget last remembered position.
     */
    public void clearMark() {
        stack.pop();
    }

    /**
     * Get byte position.
     *
     * @return  Position.
     */
    public long getPosition() {
        return input.getPosition();
    }

    /**
     * Reads a single character and does not change the reading
     * position.
     *
     * @return  character read, if there are no more chars
     *          <code>Character.MAX_VALUE</code> is returned
     */
    public int getChar() {
        return input.getChar();
    }

    /**
     * Reads a single character and increments the reading position
     * by one.
     *
     * @return  character read, if there are no more chars
     *          <code>-1</code> is returned
     */
    public int readChar() {
        return input.read();
    }

    /**
     * Are there still any characters to read?
     *
     * @return  Anything left for reading further?
     */
    public final boolean eof() {
        return input.isEmpty();
    }

    /**
     * Get rewind stack size.
     *
     * @return  Rewind stack size.
     */
    public int getRewindStackSize() {
        return stack.size();
    }

    /**
     * Returns the current column number.
     *
     * @return  Current column number (starting with line 1).
     */
    public int getColumn() {
        return input.getColumn();
    }

    /**
     * Returns the current line number.
     *
     * @return  Current line number (starting with line 1).
     */
    public int getRow() {
        return input.getRow();
    }

    /**
     * Returns the current line.
     *
     * @return  Current line.
     */
    public String getLine() {
        return input.getLine();
    }

    /**
     * Decrements the reading position by one and reads a single character.
     * If no characters are left, <code>-1</code> is returned.
     * Otherwise a cast to <code>char</code> gives the character read.
     *
     * @return  Character read, if there are no more chars
     *          <code>-1</code> is returned.
     */
    public int readInverse() {
        return input.readInverse();
    }

}
