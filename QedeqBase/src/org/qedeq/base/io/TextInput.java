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

package org.qedeq.base.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.qedeq.base.utility.StringUtility;


/**
 * This class provides convenient methods for parsing input.
 *
 * @author  Michael Meyling
 */
public class TextInput extends InputStream {

    /** Char marking end of data. */
    public static final int EOF = -1;

    /** Char marking end of input line. */
//    public final static char CR = '\n'; // LATER mime 20050613: delete if running on all platforms
    public static final char CR = '\012';

    /** String for marking current reading position. */
    private static final String MARKER = "#####";

    /** Holds the data. */
    private final StringBuffer source;

    /** Current line number (starting with 0). */
    private int lineNumber = 0;

    /** Current column (starting with 0). */
    private int column = 0;

    /** Current reading position (starting with 0). */
    private int position = 0;

    /**
     * Constructor using <code>Reader</code> source.
     *
     * @param   reader  Data source. This reader will be closed (even if reading fails).
     * @throws  IOException     Reading failed.
     * @throws  NullPointerException    Argument was a null pointer.
     */
    public TextInput(final Reader reader) throws IOException {
        try {
            if (reader == null) {
                throw new NullPointerException(
                    "no null pointer as argument accepted");
            }
            this.source = new StringBuffer();
            // TODO mime 20080307: optimize reading
            int c;
            while (-1 != (c = reader.read())) {
                this.source.append((char) c);
            }
        } finally {
            IoUtility.close(reader);
        }
    }

    /**
     * Constructor using <code>StringBuffer</code> source.
     *
     * @param   source  data source
     * @throws  NullPointerException    Argument was a null pointer.
     */
    public TextInput(final StringBuffer source) {
        if (source == null) {
            throw new NullPointerException(
                "no null pointer as argument accepted");
        }
        this.source = source;
    }

    /**
     * Constructor using <code>String</code> source.
     *
     * @param   source  data source
     * @throws  NullPointerException    Argument was a null pointer.
     */
    public TextInput(final String source) {
        if (source == null) {
            throw new NullPointerException(
                "no null pointer as argument accepted");
        }
        this.source = new StringBuffer(source);
    }


    /**
     * Constructor using <code>FILE</code> source.
     *
     * @param   file            Data source.
     * @param   encoding        Take this encoding for file.
     * @throws  IOException     File reading failed.
     * @throws  NullPointerException    One argument was a null pointer.
     */
    public TextInput(final File file, final String encoding) throws IOException {
        if (file == null) {
            throw new NullPointerException(
                "no null pointer as argument accepted");
        }
        this.source = new StringBuffer();
        IoUtility.loadFile(file, source, encoding);
    }

    /**
     * Reads a single character and increments the reading position
     * by one. If no characters are left, <code>-1</code> is returned.
     * Otherwise a cast to <code>char</code> gives the character read.
     *
     * @return  Character read, if there are no more chars
     *          <code>-1</code> is returned.
     */
    public final int read() {
        if (position >= source.length()) {
            return EOF;
        }
        if (getChar() == CR) {
            lineNumber++;
            column = 0;
        } else {
            column++;
        }
        return source.charAt(position++);
    }

    /**
     * Decrements the reading position by one and reads a single character.
     * If no characters are left, <code>-1</code> is returned.
     * Otherwise a cast to <code>char</code> gives the character read.
     *
     * @return  Character read, if there are no more chars
     *          <code>-1</code> is returned.
     */
    public final int readInverse() {
        if (position <= 0) {
            return -1;
        }
        final char c = source.charAt(--position);
        if (c == CR) {
            lineNumber--;
            int pos =  source.lastIndexOf("" + CR, position - 1);
            if (pos < 0) {
                column = position;
            } else {
                column = position - 1 - pos;
            }
        } else {
            column--;
            if (column < 0) {
                throw new IllegalStateException("column less then 0");
            }
        }
        return c;
    }

    /**
     * Reads a given amount of characters and increments the reading position
     * accordingly.
     *
     * @param   number  amount of characters to read
     * @return  string read
     */
    public final String readString(final int number) {
        final StringBuffer result = new StringBuffer(number);
        for (int i = 0; i < number; i++) {
            final int c = read();
            if (c != -1) {
                result.append((char) c);
            } else {
                break;
            }
        }
        return result.toString();
    }

    /**
     * Skips a given amount of characters and increments the reading position
     * accordingly.
     *
     * @param   number  Amount of characters to read
     */
    public final void forward(final int number) {
        for (int i = 0; i < number; i++) {
            final int c = read();
            if (c == -1) {
                break;
            }
        }
    }

    /**
     * Skips until a given keyword is reached. The position afterwards is at the start
     * of the keyword or at the end of the text (if the keyword is not found).
     *
     * @param   search  Look for this keyword.
     * @return  Was the keyword found?
     */
    public final boolean forward(final String search) {
        final int pos = source.indexOf(search, position);
        if (pos < 0) {
            setPosition(getMaximumPosition());
            return false;
        }
        setPosition(pos);
        return true;
    }

    /**
     * Reads a single character and does not change the reading
     * position. If no characters are left, <code>-1</code> is returned.
     * Otherwise a cast to <code>char</code> gives the character read.
     *
     * @return  Character read at current position, if there are no more chars
     *          <code>-1</code> is returned
     */
    public final int getChar() {
        if (position >= source.length()) {
            return -1;
        }
        return source.charAt(position);
    }

    /**
     * Reads a single character and does not change the reading
     * position. If offset addition leads out of the source,
     * <code>-1</code> is returned. Otherwise a cast to <code>char</code>
     * gives the character read.
     *
     * @param   skip   Offset from current reading position. Maybe negative.
     * @return  Character read, if position is out of scope
     *          <code>-1</code> is returned.
     */
    public final int getChar(final int skip) {
        if (position + skip < 0 || position + skip >= source.length()) {
            return -1;
        }
        return source.charAt(position + skip);
    }


   /**
    * Reads a substring. Doesn't change reading position.
    *
    * @param   from Absolute reading position.
    * @param   to   Read to this position.
    * @return  Resulting string.
    * @throws  StringIndexOutOfBoundsException  If from > to.
    */
   public final String getSubstring(final int from, final int to) {
       final int l = source.length();
       final int f = (from < 0 ? 0 : (from > l ? l : from));
       final int t = (to < 0 ? 0 : (to > l ? l : to));
       return source.substring(f, t);
   }

   /**
    * Get complete input source. Doesn't change reading position.
    *
    * @return  Complete input string.
    */
   public final String asString() {
       return source.toString();
   }

   /**
    * Replace given interval with given string.
    * If the current reading position is in the interval it is set
    * to the end of the interval.
    *
    * @param   from         Absolute reading position.
    * @param   to           Read to this position.
    * @param   replacement  Replacement for interval.
    */
   public final void replace(final int from, final int to, final String replacement) {
       source.replace(from, to, replacement);
       if (position > from && position < to) {
           setPosition(from + replacement.length());
       } else if (position > from) {    // correct row (and column) information
           setPosition(position - to + from + replacement.length());
       }
   }

    /**
     * Skips white space, beginning from reading position.
     * Changes reading position to next non white space
     * character.
     */
    public final void skipWhiteSpace() {
        while (!isEmpty() && Character.isWhitespace((char) getChar())) {
            read();
        }
    }

    /**
     * Skips white space, beginning from reading position.
     * Changes reading position to next non white space
     * character.
     */
    public final void skipWhiteSpaceInverse() {
        while (getPosition() > 0 && Character.isWhitespace((char) getChar(-1))) {
            readInverse();
        }
    }

    /**
     * Skip current position back to beginning of an XML tag.
     * This is mainly something like <code>&lt;tagName</code>.
     *
     * @throws  IllegalArgumentException    No begin of XML tag found.
     */
    public final void skipBackToBeginOfXmlTag() {
        if ('<' == getChar()) {
            return;
        }
        boolean quoted = false;
        do {
            if (-1 == readInverse()) {
                throw new IllegalArgumentException("begin of xml tag not found");
            }
            if ('\"' == getChar()) {
                quoted = !quoted;
            }
        } while (quoted || '<' != getChar());
    }

    /**
     * Skip current position forward to end of an XML tag.
     * This is mainly something like <code>&gt;</code>. Quoted data is skipped.
     *
     * @throws  IllegalArgumentException    No end of XML tag found.
     */
    public final void skipForwardToEndOfXmlTag() {
        if ('>' == getChar()) {
            return;
        }
        boolean quoted = false;
        while ('>' != getChar()) {
            if ('\"' == getChar()) {
                quoted = !quoted;
            }
            if (!quoted) {
                if (-1 == read()) {
                    throw new IllegalArgumentException("end of xml tag not found");
                }
            }
        }
        read(); // skip '>'
    }

    /**
     * Reads tag or attribute name out of XML stream. Whitespace is skipped and
     * characters are read till &quot;=&quot; or &quot;&gt;&quot; or whitespace is found.
     *
     * @return  Name of tag or attribute.
     * @throws  IllegalArgumentException    Next non white space character is &quot;=&quot;
     *                                      or &quot;&gt;&quot;.
     */
    public final String readNextXmlName() {
        skipWhiteSpace();
        if (isEmpty() || '=' == getChar() || '>' == getChar()) {
            throw new IllegalArgumentException(
                "begin of attribute expected");
        }
        StringBuffer buffer = new StringBuffer();
        while (!isEmpty() && '=' != getChar() && '>' != getChar()
                && !Character.isWhitespace((char) getChar())) {
            buffer.append((char) read());
        }
        return buffer.toString();
    }

    /**
     * Reads attribute value out of XML stream. Whitespace is skipped and an &quot;=&quot;
     * is expected to follow. Again whitespace is skipped. If no quotation mark follows
     * characters are read till whitespace or &quot;&gt;&quot; occurs. Otherwise data is
     * read till an ending quotation mark comes.
     *
     * @return  Value read.
     * @throws  IllegalArgumentException    Following had not one of the following forms:
     * <pre>
     *   = "value"
     * </pre>
     * <pre>
     *   = value
     * </pre>
     */
    public final String readNextAttributeValue() {
        skipWhiteSpace();
        if (isEmpty() || '=' != getChar()) {
            throw new IllegalArgumentException(
                "\"=\" expected");
        }
        read();         // read =
        skipWhiteSpace();
        if (isEmpty() || '>' == getChar()) {
            throw new IllegalArgumentException(
                "attribute value expected");
        }
        StringBuffer buffer = new StringBuffer();
        if ('\"' == getChar()) {
            read();     // read "
            while (!isEmpty() && '\"' != getChar()) {
                buffer.append((char) read());
            }
            if ('\"' != getChar()) {
                throw new IllegalArgumentException("\" expected");
            }
            read();     // read "
        } else {
            while (!isEmpty() && '>' != getChar()
                    && !Character.isWhitespace((char) getChar())) {
                buffer.append((char) read());
            }
        }
        return StringUtility.decodeXmlMarkup(buffer);
    }

    /**
     * Is there no data left for reading?
     *
     * @return  is all data read?
     */
    public final boolean isEmpty() {
        return position >= source.length();
    }

    /**
     * Is there no data left for reading after skipping?
     *
     * @param   skip    Add this number to current position.
     * @return  Is data empty at that new position?
     */
    public final boolean isEmpty(final int skip) {
        return position + skip >= source.length();
    }

    /**
     * Reads the next string containing only letters or digits,
     * leading whitespace is skipped.
     * Changes reading position.
     *
     * @return  read string
     * @throws  IllegalArgumentException if no such characters could
     *          be found
     */
    public final String readLetterDigitString() {
        skipWhiteSpace();
        if (isEmpty() || !Character.isLetterOrDigit((char) getChar())) {
            read();     // for showing correct position
            throw new IllegalArgumentException(
                "letter or digit expected");
        }
        StringBuffer buffer = new StringBuffer();
        while (!isEmpty() && Character.isLetterOrDigit((char) getChar())) {
            buffer.append((char) read());
        }
        return buffer.toString();
    }

    /**
     * Reads the next (big) integer, leading whitespace is skipped.
     * The first character might be a minus sign, the rest must be
     * digits. Leading zero digits are not allowed, also "-0" is not
     * accepted. <p>
     * Changes reading position.
     *
     * @return  read integer
     * @throws  IllegalArgumentException if no digits where found or
     *          the number was to big for an <code>int</code>
     */
    public final String readCounter() {
        skipWhiteSpace();
        if (isEmpty()) {
            throw new IllegalArgumentException("integer expected");
        }
        StringBuffer buffer = new StringBuffer();
        if (getChar() == '-') {
            buffer.append(read());
        }
        final int begin = getPosition();
        if (!Character.isDigit((char) getChar())) {
            throw new IllegalArgumentException("digit expected");
        }
        while (!isEmpty() && Character.isDigit((char) getChar())) {
            buffer.append((char) read());
        }
        if (buffer.length() >= 2 && ('0' == buffer.charAt(0)
                || '-' == buffer.charAt(0) && '0' == buffer.charAt(1))) {
            setPosition(begin);     // for showing correct position
            throw new IllegalArgumentException("no leading zeros allowed");
        }
        return buffer.toString();
    }

    /**
     * Reads the next quoted string, leading whitespace is skipped.
     * A correctly quoted string could be created by adding a leading and
     * a trailing quote character and doubling each other quote character.
     * The resulting string is dequoted.
     * Changes reading position.
     *
     * @return  Dequoted string read.
     * @throws  IllegalArgumentException    No correctly quoted string was found.
     */
    public final String readQuoted() {
        skipWhiteSpace();
        if (isEmpty() || read() != '\"') {
            throw new IllegalArgumentException(
                "\" expected");
        }
        StringBuffer unquoted = new StringBuffer();
        char c;
        do {
            if (isEmpty()) {
                throw new IllegalArgumentException(
                    "ending \" expected");
            }
            c = (char) read();
            if (c != '\"') {
                unquoted.append(c);
            } else {        // c == '\"'
                if (isEmpty() || getChar() != '\"') {
                    break;  // success
                }
                unquoted.append((char) read());
            }
        } while (true);
        return unquoted.toString();
    }

    /**
     * Returns the current line number.
     *
     * @return  Current line number (starting with line 1).
     */
    public final int getRow() {
        return lineNumber + 1;
    }

    /**
     * Returns the current column number.
     *
     * @return  Current column number (starting with line 1).
     */
    public final int getColumn() {
        return column + 1;
    }

    /**
     * Returns the current line.
     *
     * @return  Current line.
     */
    public final String getLine() {
        int min =  position - 1;
        while (min >= 0 && source.charAt(min) != CR) {
            min--;
        }
        int max = position;
        while (max < source.length()
                && source.charAt(max) != CR) {
            max++;
        }
        if (min + 1 >= max) {
            return "";
        }
        return source.substring(min + 1, max);
    }

    /**
     * Returns the current position. Starting with 0. This is the number of characters
     * from the beginning.
     *
     * @return  Current position.
     */
    public final int getPosition() {
        return position;
    }

    /**
     * Returns the current position.
     *
     * @return  Current position.
     */
    public final SourcePosition getSourcePosition() {
        return new SourcePosition(getRow(), getColumn());
    }

    /**
     * Returns the highest position number possible. This is equal
     * to the length of the source.
     *
     * @return  Maximum position.
     */
    public final int getMaximumPosition() {
        return source.length();
    }

    /**
     * Sets the current position (and indirectly the row and column number).
     *
     * @param  position Set current position to this value.
     */
    public final void setPosition(final int position) {
        if (position >= source.length()) {
            this.position = source.length();
        } else if (this.position != position) {
            if (position < this.position) {
                this.position = 0;
                this.lineNumber = 0;
                this.column = 0;
                for (int i = 0; i < position; i++) {    // Q & D
                    read();
                }
            } else {
                for (int i = this.position; i < position; i++) {
                    read();
                }
            }
        }
    }

    /**
     * Sets the current position (and indirectly the row and column number).
     *
     * @param  position Set current position to this value.
     */
    public final void setPosition(final SourcePosition position) {
        setRow(position.getRow());
        setColumn(position.getColumn());
    }

    /**
     * Adds a given position to the current one and changes the row and column number accordingly.
     * A delta position with one row and one column doesn't change the current position.
     *
     * @param  delta Add this position to current one.
     */
    public final void addPosition(final SourcePosition delta) {
        addRow(delta.getRow() - 1);
        addColumn(delta.getColumn() - 1);
    }

    /**
     * Sets the current line number (and indirectly the position).
     *
     * @param  row  Move to this line number.
     */
    public final void setRow(final int row) {
        int r = row;
        // check if row is under lower bound
        if (r <= 0) {
            r = 1;
        }
        // check if already at wanted position
        if (getRow() == r) {
            return;
        }
        // check if already at end of file
        if (getPosition() >= source.length() && getRow() >= r) {
            return;
        }
        if (getRow() > r) {
            // reset to begin of file
            this.position = 0;
            this.lineNumber = 0;
            this.column = 0;
        }
        for (int i = 0; getRow() < r; i++) {
            if (EOF == read()) {
                return;
            }
        }
    }

    /**
     * Get given byte position as {@link SourcePosition}.
     *
     * @param   find    Get row and column information for this byte position.
     * @return  Row and column information.
     */
    public final SourcePosition getPosition(final int find) {
        int r = 0;
        int c = 0;
        int i = 0;
        while (i < source.length() && i < find) {
            if (CR == source.charAt(i)) {
                r++;
                c = 0;
            } else {
                c++;
            }
            i++;
        }
        return new SourcePosition(r + 1, c + 1);
    }

    /**
     * Get given byte position as {@link SourcePosition}.
     *
     * @param   position    Get row and column information for this byte position.
     * @return  Row and column information.
     */
    public final int getPosition(final SourcePosition position) {
        int find = 0;
        int r = 0;
        while (++r < position.getRow() && -1 < (find = source.indexOf("" + CR, find))) {
            // nothing to do
        }
        if (find < 0) {
            find = source.length();
        }
        find += position.getColumn();
        if (find > source.length()) {
            find = source.length();
        }
        return find;
    }

    /**
     * Get source area as string.
     *
     * @param   area    Get this area as string.
     * @return  Area itself.
     */
    public final String getSourceArea(final SourceArea area) {
        return source.substring(getPosition(area.getStartPosition()),
                getPosition(area.getEndPosition()));
    }

    /**
     * Add the following rows and reset column (if <code>number == 0</code>).
     *
     * @param  number   Add this number of rows.
     */
    public final void addRow(final int number) {
        setRow(getRow() + number);
    }

    /**
     * Sets the current column position (and indirectly the position).
     * If <code>column</code> is out of range the minimum value (1) or the maximum possible column
     * value is taken.
     *
     * @param  column  Move to this column. First column has the number one.
     */
    public final void setColumn(final int column) {
        int c = column;
        // check if column is out of lower bound
        if (c <= 0) {
            c = 1;
        }
        // check if already at wanted position
        if (getColumn() == c) {
            return;
        }
        if (getColumn() > c) {
            do {
                this.position--;
                this.column--;
            } while (getColumn() > c);
            return;
        }
        while (getChar() != CR && getChar() != EOF && getColumn() < c) {
            read();
        }
    }

    /**
     * Add the following columns.
     *
     * @param  number   Add this number of columns.
     */
    public final void addColumn(final int number) {
        setColumn(getColumn() + number);
    }

    /**
     * Show reading position.
     *
     * @return  current line with mark at current reading position
     */
    public final String showLinePosition() {
        final String line = getLine();
        final StringBuffer buffer = new StringBuffer();
        final int col = getColumn() - 1;
        if (col > 0) {
            if (col < line.length()) {
                buffer.append(line.substring(0, col));
            } else {
                buffer.append(line);
            }
        }
        buffer.append(MARKER);
        if (col < line.length()) {
            buffer.append(line.substring(col));
        }
        return buffer.toString();
    }

// LATER mime 20050608: remove if no use
/*
    public final int findCaretPosition(final int line, final int column, final String source) {
        if (line == 1) {
            return 0;
        }
        int k = 1;
        for (int j = 0; j < source.length(); j++) {
            if (source.charAt(j) == '\n') {
                k++;
            }
            if (k == line) {
                j += column - 1;
                if (j > source.length()) {
                    j = source.length();
                }
                return j;
            }
        }
        return 0;
    }
*/

}
