/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
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
import java.io.Reader;

import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.base.utility.StringUtility;

/**
 * Test {@link TextInput}.
 *
 * @author  Michael Meyling
 */
public class TextInputTest extends QedeqTestCase {

    private TextInput qedeqInput;

    private TextInput emptyInput;

    private static final String XML_DATA =
    /*  1 */      "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"
    /*  2 */    + "<QEDEQ \n"
    /*  3 */    + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
    /*  4 */    + "    xsi:noNamespaceSchemaLocation=\"http://www.qedeq.org/0_01_06/xml/qedeq.xsd\">\n"
    /*  5 */    + "  <HEADER email=\"mime@qedeq.org\">\n"
    /*  6 */    + "    <SPEC name=\"qedeq_sample1\" ruleVersion=\"1.00.00\">\n"
    /*  7 */    + "      <LOCATIONS>\n"
    /*  8 */    + "   \t\r   <LOCATION value=\"http://qedeq.org/0.01.06/sample1\"/>\n"
    /*  9 */    + "      </LOCATIONS>\n"
    /* 10 */    + "    </SPEC>\n"
    /* 11 */    + "    <TITLE>\n"
    /* 12 */    + "      <LATEX language=\"en\">\n"
    /* 13 */    + "         Example1\n"
    /* 14 */    + "      </LATEX>\n"
    /* 15 */    + "    </TITLE>\n"
    /* 16 */    + "    <ABSTRACT>\n"
    /* 17 */    + "      <LATEX language=\"en\">\n"
    /* 18 */    + "         1789.01239In this very first qedeq module the XML specification is demonstrated.\n"
    /* 19 */    + "      </LATEX>\n"
    /* 20 */    + "    </ABSTRACT>\n"
    /* 21 */    + "    <AUTHORS>\n"
    /* 22 */    + "      <AUTHOR email=\"michael@meyling.com\">\n"
    /* 23 */    + "        <LATEX language=\"de\">\n"
    /* 24 */    + "           <![CDATA[Michael Meyling]]>\n"
    /* 25 */    + "        </LATEX>\n"
    /* 26 */    + "      </AUTHOR>\n"
    /* 27 */    + "    </AUTHORS>\n"
    /* 28 */    + "  </HEADER>\n"
    /* 29 */    + "</QEDEQ>";


    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        qedeqInput = new TextInput(XML_DATA);
        emptyInput = new TextInput("");
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        qedeqInput = null;
        super.tearDown();
    }

    /**
     * Test constructor {@link TextInput#TextInput(File)}.
     * @throws  Exception   Test failed.
     */
    public void testTextInputFileString() throws Exception {
        final File file = new File(getOutdir(), this.getClass().getName() + ".testTexFileInput.impl");
        IoUtility.saveFile(file, XML_DATA, IoUtility.getDefaultEncoding());
        TextInput ti = new TextInput(file, IoUtility.getDefaultEncoding());
        while (!qedeqInput.isEmpty()) {
            assertEquals(qedeqInput.read(), ti.read());
        }
        try {
            new TextInput((File) null, "UTF-8");
            fail("Exception expected");
        } catch (RuntimeException e) {
            // OK
        }
        assertTrue(file.delete());
    }

    /**
     * Test constructor {@link TextInput#TextInput(String)}.
     */
    public void testTextInputStringStringString() {
        int i = 0;
        while (!qedeqInput.isEmpty()) {
            assertEquals(qedeqInput.read(), XML_DATA.charAt(i++));
        }
        try {
            new TextInput((String) null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * Test constructor {@link TextInput#TextInput(StringBuffer)}.
     */
    public void testTextInputStringBufferStringString() {
        final StringBuffer buffer = new StringBuffer(XML_DATA);
        final TextInput ti = new TextInput(buffer);
        int i = 0;
        while (!ti.isEmpty()) {
            assertEquals(ti.read(), XML_DATA.charAt(i++));
        }
        try {
            new TextInput((StringBuffer) null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * Test constructor {@link TextInput#TextInput(Reader)}.
     */
    public void testTextInputReader() throws Exception {
        final TextInput ti = new TextInput(new Reader() {
            int pos = 0;

            public void close() throws IOException {
            }

            public int read(char[] cbuf, int off, int len) throws IOException {
                if (pos >= XML_DATA.length()) {
                    return -1;
                }
                final String result = StringUtility.substring(XML_DATA, pos, len);
                for (int i = 0; i < result.length(); i++) {
                    cbuf[off + i] = result.charAt(i);
                }
                pos += result.length();
                return result.length();
            }
            
        });
        int i = 0;
        while (!ti.isEmpty()) {
            assertEquals(ti.read(), XML_DATA.charAt(i++));
        }
        try {
            new TextInput((Reader) null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * Test {@link TextInput#read()}.
     */
    public void testRead() {
        final String first = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
        for (int i = 0; i < first.length(); i++) {
            assertEquals(first.charAt(i), qedeqInput.read());
        }
        qedeqInput.setPosition(qedeqInput.getMaximumPosition());
        for (int i = 0; i < 10; i++) {
            assertEquals(-1, qedeqInput.read());
        }
        qedeqInput.setPosition(0);
        for (int i = 0; i < first.length(); i++) {
            assertEquals(first.charAt(i), qedeqInput.read());
        }
        for (int i = 0; i < 10; i++) {
            assertEquals(-1, emptyInput.read());
        }
    }

    /**
     * Test {@link TextInput#read()}.
     */
    public void testReadString() {
        qedeqInput.read();
        qedeqInput.read();
        assertEquals("xml", qedeqInput.readString(3));
    }

    /**
     * Test {@link TextInput#getChar()}.
     */
    public void testGetChar() {
        final String first = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
        for (int i = 0; i < first.length(); i++) {
            assertEquals(first.charAt(i), qedeqInput.getChar());
            assertEquals(first.charAt(i), qedeqInput.read());
        }
        qedeqInput.setPosition(qedeqInput.getMaximumPosition());
        for (int i = 0; i < 10; i++) {
            assertEquals(-1, qedeqInput.getChar());
            assertEquals(-1, qedeqInput.read());
        }
        qedeqInput.setPosition(0);
        for (int i = 0; i < first.length(); i++) {
            assertEquals(first.charAt(i), qedeqInput.getChar());
            assertEquals(first.charAt(i), qedeqInput.read());
        }
    }

    public void testForwardInt() {
        qedeqInput.forward(-1);
        assertEquals(0, qedeqInput.getPosition());
        qedeqInput.forward(0);
        assertEquals(0, qedeqInput.getPosition());
        qedeqInput.forward(1);
        assertEquals(1, qedeqInput.getPosition());
        qedeqInput.forward(6);
        assertEquals(7, qedeqInput.getPosition());
        qedeqInput.forward(9);
        assertEquals(16, qedeqInput.getPosition());
        qedeqInput.forward(99999);
        assertEquals(XML_DATA.length(), qedeqInput.getPosition());
    }

    public void testForwardString() {
        qedeqInput.forward("zafsduizasif");
        assertEquals(832, qedeqInput.getPosition());
        qedeqInput.setPosition(0);
        qedeqInput.forward("<?xml");
        assertEquals(0, qedeqInput.getPosition());
    }

    /**
     * Test {@link TextInput#getChar(int)}.
     */
    public void testGetCharInt() {
        final String first = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
        for (int i = 0; i < first.length(); i++) {
            assertEquals(first.charAt(i), qedeqInput.getChar(0));
            assertEquals(first.charAt(i), qedeqInput.read());
        }
        qedeqInput.setPosition(qedeqInput.getMaximumPosition());
        for (int i = 0; i < 10; i++) {
            assertEquals(-1, qedeqInput.getChar(0));
        }
        for (int i = 0; i < 10; i++) {
            assertEquals(-1, qedeqInput.getChar(i));
        }
        assertEquals('>', qedeqInput.getChar(-1));
        assertEquals('Q', qedeqInput.getChar(-2));
        assertEquals('E', qedeqInput.getChar(-3));
        assertEquals('D', qedeqInput.getChar(-4));
        assertEquals('E', qedeqInput.getChar(-5));
        assertEquals('Q', qedeqInput.getChar(-6));
        assertEquals('/', qedeqInput.getChar(-7));
        assertEquals('<', qedeqInput.getChar(-8));
        qedeqInput.setPosition(0);
        for (int i = 0; i < first.length(); i++) {
            assertEquals(first.charAt(i), qedeqInput.getChar(0));
            assertEquals(first.charAt(i), qedeqInput.read());
        }
        qedeqInput.setPosition(0);
        for (int i = 0; i < first.length(); i++) {
            assertEquals(first.charAt(i), qedeqInput.getChar(i));
        }
    }

    public void testGetSubstring() {
        final String text = "one two three";
        final TextInput ti = new TextInput(text);
        assertEquals("", ti.getSubstring(text.length(), text.length()));
        assertEquals("", ti.getSubstring(-1, -1));
        assertEquals("", ti.getSubstring(0, 0));
        assertEquals("", ti.getSubstring(text.length(), 0));
        assertEquals("", ti.getSubstring(text.length(), text.length() + 1));
        assertEquals("e", ti.getSubstring(text.length() - 1, text.length() + 1));
        assertEquals("e", ti.getSubstring(text.length() - 1, text.length()));
        assertEquals("", ti.getSubstring(text.length() + 1, 0));
        assertEquals("", ti.getSubstring(text.length() + 1, text.length()));
        assertEquals("", ti.getSubstring(text.length() + 1, -1));
        assertEquals(text, ti.getSubstring(-10, 999));
        assertEquals(text, ti.getSubstring(-10, text.length()));
        assertEquals(text, ti.getSubstring(0, 997));
        assertEquals("one", ti.getSubstring(-1, 3));
        assertEquals("thre", ti.getSubstring(8, text.length() - 1));
        assertEquals("three", ti.getSubstring(8, text.length()));
        assertEquals("three", ti.getSubstring(8, text.length()+ 1));
        assertEquals("two", ti.getSubstring(4, 7));
    }

    public void testAsString() {
        final String text = "one two three";
        final TextInput ti = new TextInput(text);
        assertEquals(text, ti.asString());
    }

    public void testReplace() {
        final String text = "one two three";
        final TextInput ti = new TextInput(text);
        ti.replace(4, 7, "2");
        assertEquals("one 2 three", ti.asString());
        ti.skipToEndOfLine();
        ti.replace(0, 3, "1");
        ti.setPosition(2);
        ti.replace(0, 2, "");
    }

    /**
     * Test {@link TextInput#skipWhiteSpace()}.
     */
    public void testSkipWhiteSpace() {
        final String first = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
        qedeqInput.skipWhiteSpace();
        assertEquals(0, qedeqInput.getPosition());
        qedeqInput.setPosition(first.length());
        qedeqInput.skipWhiteSpace();
        assertEquals(first.length() + 1, qedeqInput.getPosition());
        qedeqInput.setRow(8);
        assertEquals("   \t\r   <LOCATION value=\"http://qedeq.org/0.01.06/sample1\"/>", qedeqInput.getLine());
        qedeqInput.skipWhiteSpace();
        assertEquals(9, qedeqInput.getColumn());
        assertEquals('<', qedeqInput.getChar());
        qedeqInput.setRow(8);
        qedeqInput.setPosition(qedeqInput.getPosition() - 1);
        qedeqInput.skipWhiteSpace();
        assertEquals(9, qedeqInput.getColumn());
        assertEquals('<', qedeqInput.getChar());
    }

    /**
     * Test {@link TextInput#skipWhiteSpace()}.
     */
    public void testSkipWhiteSpaceInverse() {
        final String first = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
        qedeqInput.skipWhiteSpaceInverse();
        assertEquals(0, qedeqInput.getPosition());
        qedeqInput.setPosition(first.length() + 1);
        qedeqInput.skipWhiteSpaceInverse();
        assertEquals('\n', qedeqInput.getChar());
        assertEquals(first.length(), qedeqInput.getPosition());
        qedeqInput.setRow(8);
        assertEquals("   \t\r   <LOCATION value=\"http://qedeq.org/0.01.06/sample1\"/>", qedeqInput.getLine());
        qedeqInput.setColumn(9);
        qedeqInput.skipWhiteSpaceInverse();
        assertEquals(7, qedeqInput.getRow());
        assertEquals('>', qedeqInput.getChar(-1));
        qedeqInput.read();
        assertEquals(8, qedeqInput.getRow());
    }

    /**
     * Tests {@link TextInput#skipBackToBeginOfXmlTag()}.
     */
    public void testSkipBackToBeginOfXmlTag() {
        final TextInput ti = new TextInput("hiso \"<\"hiso >goto blub > &");
        ti.setPosition(300);
        try {
            ti.skipBackToBeginOfXmlTag();
            fail("Exception expected");
        } catch (RuntimeException e) {
            // OK
        }
        final String first = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
        qedeqInput.setPosition(first.length());
        qedeqInput.skipBackToBeginOfXmlTag();
        assertEquals(0, qedeqInput.getPosition());
        qedeqInput.setPosition(first.length() + 1);
        qedeqInput.skipWhiteSpace();
        // now we are at "<"
        final int pos = qedeqInput.getPosition();
        qedeqInput.skipBackToBeginOfXmlTag();
        assertEquals(pos, qedeqInput.getPosition());
        qedeqInput.readInverse();
        // now we are just before "<"
        qedeqInput.skipBackToBeginOfXmlTag();
        assertEquals(0, qedeqInput.getPosition());
        final TextInput ti2 = new TextInput("we will test if this is working");
        ti2.setPosition(300);
        try {
            ti2.skipBackToBeginOfXmlTag();
            fail("Exception expected");
        } catch (RuntimeException e) {
            // OK
        }
    }

    public void testSkipToEndOfLine() {
        qedeqInput.skipToEndOfLine();
        assertEquals('\n', qedeqInput.readInverse());
        final TextInput ti = new TextInput("\n\n\n\n");
        ti.skipToEndOfLine();
        assertEquals(1, ti.getPosition());
        ti.skipToEndOfLine();
        assertEquals(2, ti.getPosition());
        ti.skipToEndOfLine();
        assertEquals(3, ti.getPosition());
        ti.skipToEndOfLine();
        assertEquals(4, ti.getPosition());
        ti.skipToEndOfLine();
        assertEquals(4, ti.getPosition());
    }

    public void testSkipForwardToEndOfXmlTag() {
        qedeqInput.skipForwardToEndOfXmlTag();
        assertEquals('\n', qedeqInput.getChar());
        qedeqInput.skipForwardToEndOfXmlTag();
        assertEquals('\n', qedeqInput.read());
        assertEquals("  <HEADER", qedeqInput.readString("  <HEADER".length()));
        final TextInput ti = new TextInput("<Head this=\">is not the end\">zulu");
        ti.skipForwardToEndOfXmlTag();
        assertEquals("zulu", ti.readString(4));
        final TextInput ti2 = new TextInput("<Head this=\"one\" that=\"two\">zulu");
        ti2.skipForwardToEndOfXmlTag();
        assertEquals("zulu", ti2.readString(4));
        final TextInput ti3 = new TextInput("<Head this=\"one\" that=\"two>zulu");
        try {
            ti3.skipForwardToEndOfXmlTag();
            fail("Exception expected"); // the quotation is not ended!
        } catch (RuntimeException e) {
            // OK
        }
        final TextInput ti4 = new TextInput("<Head this=\"one\" that=\"two\" this= that");
        try {
            ti4.skipForwardToEndOfXmlTag();
            fail("Exception expected"); // the tag is not ended with >
        } catch (RuntimeException e) {
            // OK
        }
    }

    /**
     * Test {@link TextInput#isEmpty()}.
     */
    public void testIsEmpty() {
        assertFalse(qedeqInput.isEmpty());
        final String first = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
        for (int i = 0; i < first.length(); i++) {
            qedeqInput.read();
            assertFalse(qedeqInput.isEmpty());
        }
        qedeqInput.setPosition(qedeqInput.getMaximumPosition());
        assertTrue(qedeqInput.isEmpty());
    }

    /**
     * Test {@link TextInput#isEmpty()}.
     */
    public void testIsEmptyInt() {
        assertFalse(qedeqInput.isEmpty(0));
        assertFalse(qedeqInput.isEmpty(-1));
        assertFalse(qedeqInput.isEmpty(1));
        final String first = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
        assertFalse(qedeqInput.isEmpty(first.length()));
        for (int i = 0; i < first.length(); i++) {
            qedeqInput.read();
        }
        assertFalse(qedeqInput.isEmpty(0));
        qedeqInput.setPosition(qedeqInput.getMaximumPosition());
        assertTrue(qedeqInput.isEmpty(0));
        assertFalse(qedeqInput.isEmpty(-1));
        assertTrue(qedeqInput.isEmpty(1));
    }

    /**
     * Test {@link TextInput#readLetterDigitString()}.
     */
    public void testReadLetterDigitString() {
        qedeqInput.read();
        qedeqInput.read();
        assertEquals("xml", qedeqInput.readLetterDigitString());
        qedeqInput.setRow(13);
        assertEquals("Example1", qedeqInput.readLetterDigitString());
        qedeqInput.setRow(13);
        qedeqInput.setColumn(1);
        qedeqInput.skipWhiteSpace();
        assertEquals("Example1", qedeqInput.readLetterDigitString());
        try {
            qedeqInput.readLetterDigitString();
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            emptyInput.readLetterDigitString();
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * Tests {@link TextInput#readCounter()}.
     */
    public void testReadCounter() {
        qedeqInput.setRow(18);
        assertEquals("1789", qedeqInput.readCounter());
        try {
            qedeqInput.readCounter();
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        qedeqInput.read();
        try {
            qedeqInput.readCounter();
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        qedeqInput.read();
        assertEquals("1239", qedeqInput.readCounter());
        try {
            qedeqInput.readCounter();
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            emptyInput.readCounter();
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * Test {@link TextInput#readQuoted()}.
     */
    public void testReadQuoted() {
        final TextInput first = new TextInput("Hell=\"one\" Water=\"two\"");
        first.setPosition(5);
        assertEquals("one", first.readQuoted());
        first.readString(7);
        assertEquals("two", first.readQuoted());
        final TextInput second = new TextInput("\"\"\"one\" Water=\"two\"");
        assertEquals("\"one", second.readQuoted());
        second.setPosition(1);
        assertEquals("", second.readQuoted());
        final int position = second.getPosition();
        try {
            second.readQuoted();
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertTrue(position < second.getPosition());
    }

    /**
     * Test {@link TextInput#readInverse()}.
     */
    public void testReadInverse() {
        assertEquals(-1, qedeqInput.readInverse());
        assertEquals('<', qedeqInput.read());
        assertEquals('<', qedeqInput.readInverse());
    }

    /**
     * Test {@link TextInput#readNextAttributeValue()}.
     */
    public void pestReadNextAttributeValue() {
        // qedeqInput.readNextAttributeValue();
        fail("not implemented");
    }

    /**
     * Test {@link TextInput#readNextXmlName()}.
     */
    public void pestReadNextXmlName() {
        qedeqInput.readNextXmlName();
        fail("not implemented");
    }

    /**
     * Test {@link TextInput#getRow()}.
     */
    public void pestGetRow() {
        fail("not implemented");
    }

    /**
     * Test {@link TextInput#getColumn()}.
     */
    public void pestGetColumn() {
        fail("not implemented");
    }

    /**
     * Test {@link TextInput#getLine()}.
     */
    public void pestGetLine() {
        fail("not implemented");
    }

    /**
     * Test {@link TextInput#getPosition()} and {@link TextInput#setPosition(int)}.
     */
    public void pestGetSetPosition() {
        fail("not implemented");
    }

    /**
     * Test {@link TextInput#getMaximumPosition()}.
     */
    public void pestGetMaximumPosition() {
        fail("not implemented");
    }

    /**
     * Test {@link TextInput#setRow(int)}.
     */
    public void pestSetRow() {
        fail("not implemented");
    }

    /**
     * Test {@link TextInput#setColumn(int)}.
     */
    public void pestSetColumn() {
        fail("not implemented");
    }

    /**
     * Test {@link TextInput#getAddress()}.
     */
    public void pestGetAddress() {
        fail("not implemented");
    }

    /**
     * Test {@link TextInput#getLocalAddress()}.
     */
    public void pestGetLocalAddress() {
        fail("not implemented");
    }

    /**
     * Test {@link TextInput#showLinePosition()}.
     */
    public void pestShowLinePosition() {
        fail("not implemented");
    }

    /**
     * Test {@link TextInput#hashCode()}.
     */
    public void pestHashCode() {
        fail("not implemented");
    }

    /**
     * Test {@link TextInput#equals(Object)}.
     */
    public void testEquals() {
        assertTrue(qedeqInput.equals(qedeqInput));
        assertFalse(emptyInput.equals(qedeqInput));
        assertFalse(emptyInput.equals(null));
        assertFalse(qedeqInput.equals(null));
        assertFalse(qedeqInput.equals(emptyInput));
        assertTrue(emptyInput.equals(emptyInput));
    }

    /**
     * Test {@link TextInput#toString()}.
     */
    public void pestToString() {
        fail("not implemented");
    }

}
