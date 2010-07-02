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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang.SystemUtils;
import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.base.utility.StringUtility;

/**
 * Test {@link TextOutput}.
 *
 * @author  Michael Meyling
 */
public class TextOutputTest extends QedeqTestCase {

    private static final String XML_DATA =
    /*  1 */      "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\012"
    /*  2 */    + "<QEDEQ \012"
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
    /* 29 */    + "</QEDEQ>\n";

    protected void setUp() throws Exception {
        super.setUp();
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
        final ByteArrayOutputStream to = new ByteArrayOutputStream();
        final TextOutput out = new TextOutput("flying toasters", to);
        out.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        out.println("<QEDEQ ");
        out.pushLevel();
        out.pushLevel();
        out.levelPrint("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        out.println();
        out.levelPrintln("xsi:noNamespaceSchemaLocation=\"http://www.qedeq.org/0_01_06/xml/qedeq.xsd\">");
        out.popLevel();
        out.levelPrintln("<HEADER email=\"mime@qedeq.org\">");
        out.pushLevel();
        out.levelPrint("<SPEC name=\"qedeq_sample1\" ruleVersion=\"1.00.00\">");
        out.print(SystemUtils.LINE_SEPARATOR);
        out.pushLevel();
        out.levelPrintln("<LOCATIONS>");
        out.print((Object) ("   \t\r   <LOCATION value=\"http://qedeq.org/0.01.06/sample1\"/>"
                + SystemUtils.LINE_SEPARATOR));
        out.levelPrintln("</LOCATIONS>");
        out.popLevel();
        out.levelPrintln("</SPEC>");
        out.levelPrintln("<TITLE>");
        out.pushLevel();
        out.levelPrintln("<LATEX language=\"en\">");
        out.levelPrintln("   Example1");
        out.levelPrintln("</LATEX>");
        out.popLevel();
        out.levelPrintln("</TITLE>");
        out.levelPrintln("<ABSTRACT>");
        out.pushLevel();
        out.levelPrintln("<LATEX language=\"en\">");
        out.pushLevel();
        out.levelPrintln(" 1789.01239In this very first qedeq module the XML specification is demonstrated.");
        out.popLevel();
        out.levelPrintln("</LATEX>");
        out.popLevel();
        out.levelPrintln("</ABSTRACT>");
        out.levelPrintln("<AUTHORS>");
        out.pushLevel();
        out.levelPrintln("<AUTHOR email=\"michael@meyling.com\">");
        out.pushLevel();
        out.levelPrintln("<LATEX language=\"de\">");
        out.pushLevel();
        out.levelPrintln(" <![CDATA[Michael Meyling]]>");
        out.popLevel();
        out.levelPrintln("</LATEX>");
        out.popLevel();
        out.levelPrintln("</AUTHOR>");
        out.popLevel();
        out.levelPrintln("</AUTHORS>");
        out.println((Object) "  </HEADER>");
        out.pushLevel();
        out.clearLevel();
        out.levelPrintln("</QEDEQ>");
        out.close();
        
        System.out.println(to.toString());
        assertEquals(StringUtility.string2Hex(XML_DATA.replace("\n", SystemUtils.LINE_SEPARATOR),
            "ISO-8859-1"), StringUtility.byte2Hex(to.toByteArray()));
    }

    public void testPushPop() throws Exception {
        final ByteArrayOutputStream to = new ByteArrayOutputStream();
        final TextOutput out = new TextOutput("jumper", to);
        out.popLevel();
        out.popLevel();
        out.levelPrint("James Bond");
        assertEquals("James Bond", to.toString("UTF-8"));
    }
    
    /**
     * Test IO error handling.
     * 
     * @throws  Exception   Test failed.
     */
    public void testErrorStream() throws Exception {
        final OutputStream to = new OutputStream() {
            public void write(int i) throws IOException {
                throw new IOException("i have got you");
            }
        };
        TextOutput out = new TextOutput("flying toasters", to);
        assertFalse(out.checkError());
        assertNull(out.getError());
        out.flush();
        assertFalse(out.checkError());
        out.println("i am not written");
        out.flush();
        assertTrue(out.checkError());
        out.getError().printStackTrace(System.out);
        // the message is still hard coded in TextOutput
        assertEquals("Writing failed.", out.getError().getMessage());

    }
}

