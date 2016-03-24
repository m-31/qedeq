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

package org.qedeq.base.utility;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.qedeq.base.test.QedeqTestCase;

/**
 * Test {@link StringUtility}.
 *
 * @version $Revision: 1.1 $
 * @author Michael Meyling
 */
public class StringUtilityTest extends QedeqTestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test replace(String, String, String).
     *
     * @throws Exception
     */
    public void testReplace() throws Exception {
        assertEquals("", StringUtility.replace("", "12345", "89"));
        assertEquals("", StringUtility.replace((String) null, "12345", "89"));
        assertEquals("", StringUtility.replace((String) null, null, null));
        assertEquals("", StringUtility.replace("", null, "89"));
        assertEquals("", StringUtility.replace("", "2", null));
        assertEquals("", StringUtility.replace("", "", null));
        assertEquals("", StringUtility.replace("", "", ""));
        assertEquals("", StringUtility.replace("", null, ""));
        assertEquals("", StringUtility.replace((String) null, "", ""));
        assertEquals("", StringUtility.replace("", "1", "1"));
        assertEquals("", StringUtility.replace("", "12", "12"));
        assertEquals("1", StringUtility.replace("1", "1", "1"));
        assertEquals("12", StringUtility.replace("12", "12", "12"));
        assertEquals("", StringUtility.replace("1", "1", ""));
        assertEquals("", StringUtility.replace("12", "12", ""));
        assertEquals("", StringUtility.replace("1", "1", null));
        assertEquals("", StringUtility.replace("12", "12", null));
        assertEquals("13", StringUtility.replace("12", "2", "3"));
        assertEquals("1", StringUtility.replace("12", "2", null));
        assertEquals("12", StringUtility.replace("12", "", "7"));
        assertEquals("12", StringUtility.replace("12", null, "7"));
        assertEquals("145", StringUtility.replace("12345", "23", null));
        assertEquals("12345", StringUtility.replace("12345", "23", "23"));
        assertEquals("12AA12AA12", StringUtility.replace("12012012", "0", "AA"));
        assertEquals("AA12AA12AA12AA", StringUtility.replace("0120120120", "0", "AA"));
        assertEquals("AA12ABA12AA12AA", StringUtility.replace("012ABA120120", "0", "AA"));
        assertEquals("012ABA120120", StringUtility.replace("012ABA120120", "", "AA"));
        assertEquals("012ABA120120", StringUtility.replace("012ABA120120", null, "AA"));
        assertEquals("012ABA120120", StringUtility.replace("012ABA120120", null, null));
        assertEquals("012ABA120120", StringUtility.replace("012ABA120120", "", "AA"));
        assertEquals("12ABA1212", StringUtility.replace("012ABA120120", "0", null));
        assertEquals("12ABA1212", StringUtility.replace("012ABA120120", "0", ""));
        assertEquals("012ABA120120", StringUtility.replace("012ABA120120", "", ""));
        assertEquals("A3A2A3A2A3A20", StringUtility.replace("0120120120", "01", "A3A"));
        assertEquals("0ABA1200", StringUtility.replace("012ABA120120", "012", "0"));
        assertEquals("012ABA120120", StringUtility.replace("012ABA120120", "", "012"));
        assertEquals("012ABA120120", StringUtility.replace("012ABA120120", null, "012"));
        assertEquals("", StringUtility.replace("012ABA120120", "012ABA120120", ""));
        assertEquals("", StringUtility.replace("012ABA120120", "012ABA120120", null));
        assertEquals("012ABA120120", StringUtility.replace("012ABA120120", "012ABA120120",
            "012ABA120120"));
    }

    /**
     * Test replace(String, String, String).
     *
     * @throws Exception
     */
    public void testReplaceStringBuffer() throws Exception {
        StringBuffer buffer = new StringBuffer();
        doReplace(buffer, "", "", "12345", "89");
        doReplace(buffer, "", "", null, null);
        doReplace(buffer, "", "", null, "89");
        doReplace(buffer, "", "", "2", null);
        doReplace(buffer, "", "", "", null);
        doReplace(buffer, "", "", "", "");
        doReplace(buffer, "", "", "1", "1");
        doReplace(buffer, "", "", "12", "12");
        doReplace(buffer, "1", "1", "1", "1");
        doReplace(buffer, "12", "12", "12", "12");
        doReplace(buffer, "", "1", "1", "");
        doReplace(buffer, "", "12", "12", "");
        doReplace(buffer, "", "1", "1", null);
        doReplace(buffer, "", "12", "12", null);
        doReplace(buffer, "13", "12", "2", "3");
        doReplace(buffer, "1", "12", "2", null);
        doReplace(buffer, "12", "12", "", "7");
        doReplace(buffer, "12", "12", null, "7");
        doReplace(buffer, "145", "12345", "23", null);
        doReplace(buffer, "12345", "12345", "23", "23");
        doReplace(buffer, "12AA12AA12", "12012012", "0", "AA");
        doReplace(buffer, "AA12AA12AA12AA", "0120120120", "0", "AA");
        doReplace(buffer, "AA12ABA12AA12AA", "012ABA120120", "0", "AA");
        doReplace(buffer, "012ABA120120", "012ABA120120", "", "AA");
        doReplace(buffer, "012ABA120120", "012ABA120120", null, "AA");
        doReplace(buffer, "012ABA120120", "012ABA120120", null, null);
        doReplace(buffer, "012ABA120120", "012ABA120120", "", "AA");
        doReplace(buffer, "12ABA1212", "012ABA120120", "0", null);
        doReplace(buffer, "12ABA1212", "012ABA120120", "0", "");
        doReplace(buffer, "012ABA120120", "012ABA120120", "", "");
        doReplace(buffer, "A3A2A3A2A3A20", "0120120120", "01", "A3A");
        doReplace(buffer, "0ABA1200", "012ABA120120", "012", "0");
        doReplace(buffer, "012ABA120120", "012ABA120120", "", "012");
        doReplace(buffer, "012ABA120120", "012ABA120120", null, "012");
        doReplace(buffer, "", "012ABA120120", "012ABA120120", "");
        doReplace(buffer, "", "012ABA120120", "012ABA120120", null);
        doReplace(buffer, "012ABA120120", "012ABA120120", "012ABA120120",
            "012ABA120120");
        try {
            doReplace(null, "AA12ABA12AA12AA", "012ABA120120", "0", "AA");
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected;
        }
    }

    private void doReplace(final StringBuffer buffer, final String expected, final String text,
            final String search, final String replacement) {
        buffer.setLength(0);
        buffer.append(text);
        StringUtility.replace(buffer, search, replacement);
        assertEquals(expected, buffer.toString());
    }

    public void testToStringObjectArray() throws Exception {
        assertEquals("(\"a\", 1, null, \"gu\")", StringUtility.toString(new Object[] {
            "a", new Integer(1), null, "gu"}));
        assertEquals("()", StringUtility.toString((Object[]) null));
    }

    public void testToStringSet() throws Exception {
        final TreeSet set = new TreeSet();
        set.add("a");
        set.add("1");
        set.add("");
        set.add("gu");
        assertEquals("{\"\", \"1\", \"a\", \"gu\"}", StringUtility.toString(set));
        assertEquals("{}", StringUtility.toString((Set) null));
        final HashSet set2 = new HashSet();
        set2.add("a");
        set2.add(new Integer(1));
        set2.add(null);
        set2.add("gu");
        final String result = StringUtility.toString(set2);
        assertTrue(result.indexOf("null") >= 0);
        assertTrue(result.indexOf("1") >= 0);
        assertTrue(result.indexOf("\"a\"") >= 0);
        assertTrue(result.indexOf("\"gu\"") >= 0);
    }

    public void testToStringMap() throws Exception {
        final TreeMap map = new TreeMap();
        map.put("v1", "a");
        map.put("v2", new Integer(1));
        map.put("v4", null);
        map.put("v5", "gu");
        assertEquals("{v1=\"a\", v2=1, v4=null, v5=\"gu\"}", StringUtility.toString(map));
        assertEquals("{}", StringUtility.toString((Map) null));
    }

    public void testAsLines() throws Exception {
        final HashSet set = new HashSet();
        set.add("a");
        set.add(new Integer(1));
        set.add(null);
        set.add("gu");
        final String[] array = StringUtility.split(StringUtility.asLines(set), "\n");
        assertTrue(StringUtility.isIn("null", array));
        assertTrue(StringUtility.isIn("1", array));
        assertTrue(StringUtility.isIn("a", array));
        assertTrue(StringUtility.isIn("gu", array));
        assertEquals("", StringUtility.asLines((Set) null));
    }

    /**
     * Test {@link StringUtility#quote(String)}.
     *
     * @throws  Exception   Test failed.
     */
    public void testQuote() throws Exception {
        assertEquals("\"\"", StringUtility.quote(""));
        assertEquals("\"\"\"\"", StringUtility.quote("\""));
        assertEquals("\"a\"", StringUtility.quote("a"));
        assertEquals("\"\"\"a\"\"\"", StringUtility.quote("\"a\""));
        assertEquals("\"b\"\"a\"\"c\"", StringUtility.quote("b\"a\"c"));
        try {
            StringUtility.quote(null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * Test {@link StringUtility#isLetterDigitString(String)}.
     *
     * @throws  Exception   Test failed.
     */
    public void testIsLetterDigitString() throws Exception {
        assertFalse(StringUtility.isLetterDigitString(""));
        assertTrue(StringUtility.isLetterDigitString("a"));
        assertFalse(StringUtility.isLetterDigitString("1a"));
        assertTrue(StringUtility.isLetterDigitString("a1"));
        assertFalse(StringUtility.isLetterDigitString(" 1"));
        assertFalse(StringUtility.isLetterDigitString("a 1"));
        assertTrue(StringUtility.isLetterDigitString("AllOneTwo3"));
        assertTrue(StringUtility.isLetterDigitString("Z111111999999999"));
        assertFalse(StringUtility.isLetterDigitString("$A111111999999999"));
        try {
            StringUtility.isLetterDigitString(null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * Test {@link StringUtility#getSpaces(int))}.
     *
     * @throws  Exception   Test failed.
     */
    public void testGetSpaces() throws Exception {
        eq("", StringUtility.getSpaces(0));
        eq(" ", StringUtility.getSpaces(1));
        eq("  ", StringUtility.getSpaces(2));
        eq("   ", StringUtility.getSpaces(3));
        eq("    ", StringUtility.getSpaces(4));
        eq("     ", StringUtility.getSpaces(5));
        eq("      ", StringUtility.getSpaces(6));
        eq("       ", StringUtility.getSpaces(7));
        eq("        ", StringUtility.getSpaces(8));
        eq("         ", StringUtility.getSpaces(9));
        eq("                                                                                ",
            StringUtility.getSpaces(80));
        eq("", StringUtility.getSpaces(-1));
        eq("", StringUtility.getSpaces(-999999));
    }

    public void eq(final String expected, final StringBuffer spaces) throws Exception {
        assertEquals(expected.length(), spaces.length());
        assertEquals(expected, spaces.toString());
    }

    /**
     * Test {@link StringUtility#getClassName(Class)}.
     *
     * @throws  Exception   Test failed.
     */
    public void testGetClassName() throws Exception {
        assertEquals("IOException", StringUtility.getClassName(IOException.class));
    }

    /**
     * Test {@link StringUtility#deleteLineLeadingWhitespace(StringBuffer)}.
     *
     * @throws  Exception   Test failed.
     */
    public void testDeleteLineLeadingWhitespace() throws Exception {
        eq("", "");
        eq("A", "A");
        eq(" A", "A");
        eq("\tA", "A");
        String muffin1 = "       Do you know the muffin man,\n"
            + "       The muffin man, the muffin man,\n"
            + "       Do you know the muffin man,\n"
            + "       Who lives on Drury Lane?\n";
        String muffin2 = "Do you know the muffin man,\n"
            + "The muffin man, the muffin man,\n"
            + "Do you know the muffin man,\n"
            + "Who lives on Drury Lane?\n";
        String muffin3 = "Do you know the muffin man,\n"
            + "       The muffin man, the muffin man,\n"
            + "       Do you know the muffin man,\n"
            + "       Who lives on Drury Lane?\n";
        String muffin4 = "Do you know the muffin man,\n"
            + " The muffin man, the muffin man,\n"
            + " Do you know the muffin man,\n"
            + " Who lives on Drury Lane?\n";
        eq(muffin1, muffin2);
        eq("\n" + muffin1, "\n" + muffin2);
        eq("\n" + muffin1 + "\n", "\n" + muffin2 + "\n");
        eq("\n " + muffin1 + "\n", "\n" + muffin3 + "\n");

        eq(muffin1.substring(1), muffin4);
        eq("\n\t\n" + muffin1 + "\n", "\n\t\n" + muffin2 + "\n");
        eq("\015\012" + muffin1, "\015\012" + muffin2);
        eq("\015\012" + "      Hello Again" + "\015\012" + "      Said the Knight" + "\015\012",
            "\015\012" + "Hello Again" + "\015\012" + "Said the Knight" + "\015\012");
    }

    public void eq(final String input, final String expected) throws Exception {
        final StringBuffer buffer = new StringBuffer(input);
        StringUtility.deleteLineLeadingWhitespace(buffer);
        assertEquals(expected, buffer.toString());
    }

    /**
     * Test {@link StringUtility#escapeProperty(String)}.
     *
     * @throws  Exception   Test failed.
     */
    public void testEscapeProperty() throws Exception {
        assertEquals("\\u00E4\\u00FC\\u00F6\\u00DF\\u20AC\\u00C4\\u00DC\\u00D6\\u00B3",
            StringUtility.escapeProperty("\u00E4\u00FC\u00F6\u00DF\u20AC\u00C4\u00DC\u00D6\u00B3"));
    }

    public void testAlignRightLongInt() throws Exception {
        try {
            StringUtility.alignRight(13l, 0);
            fail("Exception expected");
        } catch (RuntimeException e) {
            // expected
        }
        try {
            StringUtility.alignRight(13l, -10);
            fail("Exception expected");
        } catch (RuntimeException e) {
            // expected
        }
        assertEquals("13", StringUtility.alignRight(13l, 2));
        assertEquals("13", StringUtility.alignRight(13l, 1));
        assertEquals("                  13", StringUtility.alignRight(13l, 20));
        try {
            StringUtility.alignRight(13l, 21);
            fail("Exception expected");
        } catch (RuntimeException e) {
            // expected
        }
    }

    public void testAlignRightStringInt() throws Exception {
        try {
            StringUtility.alignRight("13", 0);
            fail("Exception expected");
        } catch (RuntimeException e) {
            // expected
        }
        try {
            StringUtility.alignRight("13", -10);
            fail("Exception expected");
        } catch (RuntimeException e) {
            // expected
        }
        assertEquals("13", StringUtility.alignRight("13", 2));
        assertEquals("13", StringUtility.alignRight("13", 1));
        assertEquals("                  13", StringUtility.alignRight("13", 20));
        try {
            StringUtility.alignRight("13", 21);
            fail("Exception expected");
        } catch (RuntimeException e) {
            // expected
        }
    }

    /**
     * Test {@link StringUtility#decodeXmlMarkup(StringBuffer)}.
     *
     * @throws  Exception   Test failed.
     */
    public void testDecodeXmlMarkup() throws Exception {
        final String test1 = "&amp;&lt;&gt;";
        final StringBuffer buffer = new StringBuffer(test1);
        assertEquals("&<>", StringUtility.unescapeXml(buffer.toString()));
        final String test2 = "&#x3C;";
        final StringBuffer buffer2 = new StringBuffer(test2);
        assertEquals("<", StringUtility.unescapeXml(buffer2.toString()));
    }

    /**
     * Test {@link StringUtility#hex2String(String).
     *
     * @throws  Exception   Test failed.
     */
    public void testHex2String() throws Exception {
        assertEquals("A", StringUtility.hex2String("41"));
        assertEquals(" ", StringUtility.hex2String("20"));
        assertEquals("0", StringUtility.hex2String("30"));
        assertEquals("\015\012", StringUtility.hex2String("0D 0A"));
        assertEquals("A", StringUtility.hex2String("41 "));
        assertEquals(" ", StringUtility.hex2String("20 "));
        assertEquals("0", StringUtility.hex2String("30 "));
        assertEquals("\015\012", StringUtility.hex2String("0D 0A "));
        assertEquals("A", StringUtility.hex2String(" 41"));
        assertEquals(" ", StringUtility.hex2String(" 20"));
        assertEquals("0", StringUtility.hex2String(" 30"));
        assertEquals("\015\012", StringUtility.hex2String(" 0D 0A"));
        assertEquals("A", StringUtility.hex2String("4 1"));
        assertEquals(" ", StringUtility.hex2String("2 0"));
        assertEquals("0", StringUtility.hex2String("3 0"));
        assertEquals("\015\012", StringUtility.hex2String("0 D 0 A"));
        assertEquals("A", StringUtility.hex2String("4\n1"));
        assertEquals(" ", StringUtility.hex2String("\t2  \r0\n"));
        assertEquals("0", StringUtility.hex2String("3                   0"));
        assertEquals("\015\012", StringUtility.hex2String("         0       D0A"));
        assertEquals("\352", StringUtility.hex2String("EA", "ISO-8859-1"));
        assertEquals("\352", StringUtility.hex2String("ea", "ISO-8859-1"));
        assertEquals("\352", StringUtility.hex2String("e A ", "ISO-8859-1"));
        assertEquals("\000", StringUtility.hex2String("00"));

        // wrong padding
        try {
            StringUtility.hex2String("0");
            fail("wrong number format exception expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            StringUtility.hex2String("A01");
            fail("wrong number format exception expected");
        } catch (IllegalArgumentException e) {
            // expected
        }

        // wrong character
        try {
            StringUtility.hex2String("a+");
            fail("wrong number format exception expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            StringUtility.hex2String("!+");
            fail("wrong number format exception expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            StringUtility.hex2String("ZZ");
            fail("wrong number format exception expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * Test {@link StringUtility#string2Hex(String).
     *
     * @throws  Exception   Test failed.
     */
    public void testString2Hex() throws Exception {
        assertEquals("41", StringUtility.string2Hex("A"));
        assertEquals("20", StringUtility.string2Hex(" "));
        assertEquals("0D 0A", StringUtility.string2Hex("\015\012"));
        assertEquals("EA", StringUtility.string2Hex("\352", "ISO-8859-1"));
        assertEquals("0D 0A 0D 0A 0D 0A 0D 0A 0D 0A",
            StringUtility.string2Hex("\015\012\015\012\015\012\015\012\015\012"));
        assertEquals("0D 0A 0D 0A 0D 0A 0D 0A 0D 0A 20 20 20 20 20 20",
            StringUtility.string2Hex(
            "\015\012\015\012\015\012\015\012\015\012\040\040\040\040\040\040"));
        assertEquals("0D 0A 0D 0A 0D 0A 0D 0A 0D 0A 20 20 20 20 20 20\n41",
            StringUtility.string2Hex(
            "\015\012\015\012\015\012\015\012\015\012\040\040\040\040\040\040A"));
    }

    /**
     * Test {@link StringUtility#string2Hex(String) and {@link StringUtility#hex2String(String)}.
     *
     * @throws  Exception   Test failed.
     */
    public void testString2HexAndBack() throws Exception {
        final String first = "All my Ducks in a Row!!!$%&<>/\\?";
        assertEquals(first, StringUtility.hex2String(StringUtility.string2Hex(first)));
        final String second = "41 42 43";
        assertEquals(second, StringUtility.string2Hex(StringUtility.hex2String(second)));
        StringBuffer third = new StringBuffer(256);
        for (int i = 0; i < 256; i++) {
            third.append((char) i);
        }
        assertEquals(third.toString(),
            StringUtility.hex2String(StringUtility.string2Hex(third.toString())));
        assertEquals(StringUtility.string2Hex(third.toString()),
            StringUtility.string2Hex(StringUtility.hex2String(StringUtility.string2Hex(
                third.toString()))));
    }

    /**
     * Test {@link StringUtility#format(long, int)}.
     *
     * @throws  Exception   Test failed.
     */
    public void testFormat() throws Exception {
        assertEquals("0", StringUtility.format(0, 1));
        try {
            assertEquals("0", StringUtility.format(0, 0));
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        assertEquals("00123456", StringUtility.format(123456, 8));
        assertEquals("123456", StringUtility.format(123456, 6));
        assertEquals("123456", StringUtility.format(123456, 5));
        assertEquals("00000000000000123456", StringUtility.format(123456, 20));
        try {
            assertEquals("00000000000000000000000123456", StringUtility.format(123456, 30));
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            assertEquals("000000000000000123456", StringUtility.format(123456, 21));
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * Test {@link StringUtility#getSystemLineSeparator()}.
     *
     * @throws  Exception   Test failed.
     */
    public void testGetSystemLineSeparator() throws Exception {
        assertEquals(System.getProperty("line.separator"),
            StringUtility.getSystemLineSeparator());
    }

    /**
     * Test {@link StringUtility#useSystemLineSeparator(String)}.
     *
     * @throws  Exception   Test failed.
     */
    public void testAddSystemLineSeparator() throws Exception {
        assertEquals(null, StringUtility.useSystemLineSeparator(null));
        assertEquals("" + System.getProperty("line.separator"),
            StringUtility.useSystemLineSeparator("\015\012"));
        assertEquals("asdfghj" + System.getProperty("line.separator"),
            StringUtility.useSystemLineSeparator("asdfghj\015"));
    }

    /**
     * Test {@link StringUtility#substring(String, int, int)}.
     *
     * @throws  Exception   Test failed.
     */
    public void testSubstring() throws Exception {
        assertEquals("", StringUtility.substring("1234", 0, 0));
        assertEquals("", StringUtility.substring("1234", -3, 3));
        assertEquals("1", StringUtility.substring("1234", -3, 4));
        assertEquals("", StringUtility.substring("1234", 4, 1));
        assertEquals("4", StringUtility.substring("1234", 3, 1));
        assertEquals("4", StringUtility.substring("1234", 3, 100));
        assertEquals("1234", StringUtility.substring("1234", 0, 100));
        assertEquals("1234", StringUtility.substring("1234", -50, 100));
        assertEquals("", StringUtility.substring("1234", -500, 100));
        assertEquals("23", StringUtility.substring("1234", 1, 2));
        assertEquals("34", StringUtility.substring("1234", 2, 2));
        assertEquals("34", StringUtility.substring("1234", 2, 3));
    }

    public void testEscapeXml() throws Exception {
        assertEquals("&quot;bread&quot; &amp; &quot;butter&quot;",
            StringUtility.escapeXml("\"bread\" & \"butter\""));
    }

    public void testUnEscapeXml() throws Exception {
        assertEquals("\"bread\" & \"butter\"",
            StringUtility.unescapeXml("&quot;bread&quot; &amp; &quot;butter&quot;"));
    }

    public void testIsNotIn() throws Exception {
        assertTrue(StringUtility.isNotIn("12", new String[] { "11", "14", "13"}));
        assertTrue(StringUtility.isNotIn("12", new String[] { "11", "14", null}));
        assertFalse(StringUtility.isNotIn("12", new String[] { "12", "14", null}));
        assertFalse(StringUtility.isNotIn("12", new String[] { null, "14", "12"}));
        assertFalse(StringUtility.isNotIn("", new String[] { null, "14", "12"}));
        assertFalse(StringUtility.isNotIn("", new String[] { "12", "14", "12"}));
        assertFalse(StringUtility.isNotIn("", new String[] { "12", "", "12"}));
        assertFalse(StringUtility.isNotIn(null, new String[] { null, "14", "12"}));
        assertFalse(StringUtility.isNotIn(null, new String[] { "12", "14", "12"}));
    }

    public void testIsIn() throws Exception {
        assertFalse(StringUtility.isIn("12", new String[] { "11", "14", "13"}));
        assertFalse(StringUtility.isIn("12", new String[] { "11", "14", null}));
        assertTrue(StringUtility.isIn("12", new String[] { "12", "14", null}));
        assertTrue(StringUtility.isIn("12", new String[] { null, "14", "12"}));
        assertTrue(StringUtility.isIn("", new String[] { null, "14", "12"}));
        assertTrue(StringUtility.isIn("", new String[] { "12", "14", "12"}));
        assertTrue(StringUtility.isIn("", new String[] { "12", "", "12"}));
        assertTrue(StringUtility.isIn(null, new String[] { null, "14", "12"}));
        assertTrue(StringUtility.isIn(null, new String[] { "12", "14", "12"}));
    }

}
