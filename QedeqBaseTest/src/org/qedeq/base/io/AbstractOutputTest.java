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

import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.base.utility.StringUtility;

/**
 * Test {@link StringOutput}.
 *
 * @author  Michael Meyling
 */
public class AbstractOutputTest extends QedeqTestCase {

    private static final String XML_DATA =
    /*  1 */      "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"
    /*  2 */    + "<QEDEQ \n"
    /*  3 */    + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
    /*  4 */    + "    xsi:noNamespaceSchemaLocation=\"http://www.qedeq.org/0_01_06/xml/qedeq.xsd\">\n"
    /*  5 */    + "  <HEADER email=\"mime@qedeq.org\">\n"
    /*  6 */    + "    <SPEC name=\"qedeq_sample1\" ruleVersion=\"1.00.00\">\n"
    /*  7 */    + "      <LOCATIONS>\n"
    /*  8 */    + "      \t\r   <LOCATION value=\"http://qedeq.org/0.01.06/sample1\"/>\n"
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
    /* 28 */    + "      </HEADER>\n"
    /* 29 */    + "</QEDEQ>\n";

    /** We test this object. */
    private AbstractOutput out;

    protected void setUp() throws Exception {
        super.setUp();
        out = new AbstractOutput() {

            private StringBuffer buffer = new StringBuffer();

            public void append(final String text) {
                buffer.append(text);
            }

            public String toString() {
                return buffer.toString();
            }
        };
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test various print methods.
     *
     * @throws  Exception   Test failed.
     */
    public void testComplete() throws Exception {
        out.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        out.println("<QEDEQ ");
        out.pushLevel();
        out.pushLevel();
        out.print("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        out.println();
        out.println("xsi:noNamespaceSchemaLocation=\"http://www.qedeq.org/0_01_06/xml/qedeq.xsd\">");
        out.popLevel();
        out.println("<HEADER email=\"mime@qedeq.org\">");
        out.pushLevel();
        out.print("<SPEC name=\"qedeq_sample1\" ruleVersion=\"1.00.00\">");
        out.print("\n");
        out.pushLevel();
        out.println("<LOCATIONS>");
        out.print((Object) ("\t\r   <LOCATION value=\"http://qedeq.org/0.01.06/sample1\"/>"
                + "\n"));
        out.println("</LOCATIONS>");
        out.popLevel();
        out.println("</SPEC>");
        out.println("<TITLE>");
        out.pushLevel();
        out.println("<LATEX language=\"en\">");
        out.println("   Example1");
        out.println("</LATEX>");
        out.popLevel();
        out.println("</TITLE>");
        out.println("<ABSTRACT>");
        out.pushLevel();
        out.println("<LATEX language=\"en\">");
        out.pushLevel();
        out.println(" 1789.01239In this very first qedeq module the XML specification is demonstrated.");
        out.popLevel();
        out.println("</LATEX>");
        out.popLevel();
        out.println("</ABSTRACT>");
        out.println("<AUTHORS>");
        out.pushLevel();
        out.println("<AUTHOR email=\"michael@meyling.com\">");
        out.pushLevel();
        out.println("<LATEX language=\"de\">");
        out.pushLevel();
        out.println(" <![CDATA[Michael Meyling]]>");
        out.popLevel();
        out.println("</LATEX>");
        out.popLevel();
        out.println("</AUTHOR>");
        out.popLevel();
        out.println("</AUTHORS>");
        out.println((Object) "  </HEADER>");
        out.pushLevel();
        out.clearLevel();
        out.println("</QEDEQ>");

        assertEquals(StringUtility.string2Hex(XML_DATA),
                StringUtility.byte2Hex(out.toString().getBytes()));

        assertEquals(XML_DATA, out.toString());

//        System.out.println(to.toString());
    }

    public void testPushPop() throws Exception {
        out.popLevel();
        out.popLevel();
        out.print("James Bond");
        assertEquals("James Bond", out.toString());
    }

    public void testAdd01() throws Exception {
        out.setColumns(10);
        out.addToken("Hello");
        out.addWs(" ");
        out.addToken("Hello");
        out.flush();
        assertEquals("Hello\nHello", out.toString());
    }

    public void testAdd02() throws Exception {
        out.setColumns(10);
        out.addToken(" Hello");
        out.addWs(" ");
        out.addToken(" Hello");
        out.flush();
        assertEquals(" Hello\n Hello", out.toString());
    }

    public void testAdd03() throws Exception {
        out.setColumns(12);
        out.pushLevel();
        out.addToken("Hello");
        out.addWs(" ");
        out.addToken("Hello");
        out.flush();
        assertEquals("  Hello\n  Hello", out.toString());
    }

    public void testAdd04() throws Exception {
        out.setColumns(13);
        out.pushLevel();
        out.addToken("Hello");
        out.addWs(" ");
        out.addToken("Hello");
        out.flush();
        assertEquals("  Hello Hello", out.toString());
    }

    public void testAdd05() throws Exception {
        out.setColumns(12);
        out.pushLevel();
        out.addToken("Hello");
        out.addWs("  ");
        out.addToken("Hello");
        out.flush();
        assertEquals("  Hello\n  Hello", out.toString());
    }

    public void testAdd06() throws Exception {
        out.setColumns(10);
        out.pushLevel();
        out.addToken("Hel");
        out.addWs(" ");
        out.addWs(" ");
        out.addWs(" ");
        out.addToken("Hel");
        out.flush();
        assertEquals("  Hel\n  Hel", out.toString());
    }

    public void testAdd07() throws Exception {
        out.setColumns(11);
        out.pushLevel();
        out.addToken("Hel");
        out.addWs(" ");
        out.addWs(" ");
        out.addWs(" ");
        out.addToken("Hel");
        out.flush();
        assertEquals("  Hel   Hel", out.toString());
    }

    public void testAdd08() throws Exception {
        out.setColumns(10);
        out.pushLevel();
        out.addToken("H");
        out.addToken("e");
        out.addToken("l");
        out.addToken("l");
        out.addToken("o");
        out.addWs(" ");
        out.addToken("H");
        out.addToken("e");
        out.addToken("l");
        out.addToken("l");
        out.addToken("o");
        out.flush();
        assertEquals("  Hello\n  Hello", out.toString());
    }

    public void testAdd09() throws Exception {
        out.setColumns(80);
        out.pushLevel();
        out.addToken("Hello");
        out.addWs(" ");
        out.addToken("Hello");
        out.flush();
        assertEquals("  Hello Hello", out.toString());
    }

    public void testAdd10() throws Exception {
        out.setColumns(0);
        out.pushLevel();
        out.addToken("Hello");
        out.addWs(" ");
        out.addToken("Hello");
        out.flush();
        assertEquals("  Hello Hello", out.toString());
    }

    public void testAdd11() throws Exception {
        out.setColumns(10);
        out.pushLevel();
        out.addToken("Hello");
        out.addWs(" ");
        out.addToken("Hello");
        out.flush();
        assertEquals("  Hello\n  Hello", out.toString());
    }

    public void testAdd12() throws Exception {
        out.setColumns(7);
        out.pushLevel();
        out.addToken("Hello");
        out.addWs(" ");
        out.addToken("Hello");
        out.flush();
        assertEquals("  Hello\n  Hello", out.toString());
    }

    public void testAdd13() throws Exception {
        out.setColumns(1);
        out.pushLevel();
        out.addToken("Hello");
        out.addWs(" ");
        out.addToken("Hello");
        out.flush();
        assertEquals("  Hello\n  Hello", out.toString());
    }

    public void testAdd14() throws Exception {
        out.setColumns(1);
        out.pushLevel();
        out.addToken("Hello");
        out.pushLevel();
        out.popLevel();
        out.addWs(" ");
        out.addToken("Hello");
        out.flush();
        assertEquals("  Hello\n  Hello", out.toString());
    }

    public void testAdd15() throws Exception {
        out.setColumns(1);
        out.pushLevel();
        out.addToken("Hello");
        out.addWs(" ");
        out.pushLevel();
        out.popLevel();
        out.addToken("Hello");
        out.flush();
        assertEquals("  Hello\n  Hello", out.toString());
    }

    public void testAdd16() throws Exception {
        out.setColumns(7);
        out.pushLevel();
        out.addToken("Hel");
        out.addToken("lo");
        out.addWs(" ");
        out.addToken("Hello");
        out.flush();
        assertEquals("  Hello\n  Hello", out.toString());
    }

    public void testAdd17() throws Exception {
        out.setColumns(5);
        out.pushLevel();
        out.addToken("Hel");
        out.addToken("lo");
        out.addWs(" ");
        out.addToken("Hello");
        out.flush();
        assertEquals("  Hello\n  Hello", out.toString());
    }

    public void testAdd18() throws Exception {
        out.setColumns(5);
        out.pushLevel();
        out.addToken("Hel");
        out.pushLevel();
        out.popLevel();
        out.addToken("lo");
        out.addWs(" ");
        out.addToken("Hello");
        out.flush();
        assertEquals("  Hello\n  Hello", out.toString());
    }

}

