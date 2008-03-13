/* $Id: QedeqParserTest.java,v 1.14 2007/12/21 23:35:18 m31 Exp $
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
package org.qedeq.kernel.xml.parser;

import org.qedeq.kernel.test.QedeqTestCase;
import org.qedeq.kernel.xml.handler.module.QedeqHandler;

/**
 * Tests the class {@link SaxParser}.
 *
 * @version $Revision: 1.14 $
 * @author  Michael Meyling
 */
public class QedeqParserTest extends QedeqTestCase {

    /** Parser with default SAX document handler. */
    private SaxParser parser1;

    /** Parser with application document handlers. */
    private SaxParser parser2;

    protected void setUp() throws Exception {
        super.setUp();
        {
            SaxDefaultHandler handler = new SaxDefaultHandler();
            AbstractSimpleHandler simple = new AbstractSimpleHandler(handler, null) {

                public void startElement(final String name, final SimpleAttributes attributes) {
                    System.out.println("<" + name + " " + attributes + ">");
                }

                public void endElement(final String name) {
                    System.out.println("</" + name + ">");
                    // mime 20050205: for testing: throw new NullPointerException("");
                }

                public void characters(final String name, final String value) {
                    System.out.println(value);
                }

                public void init() {
                    // do nothing
                }

            };
            handler.setBasisDocumentHandler(simple);
            parser1 = new SaxParser(handler);
        }
        {
            SaxDefaultHandler handler = new SaxDefaultHandler();
            AbstractSimpleHandler simple = new QedeqHandler(handler);
            handler.setBasisDocumentHandler(simple);
            parser2 = new SaxParser(handler);
        }
    }

    protected void tearDown() throws Exception {
        parser1 = null;
        parser2 = null;
        super.tearDown();
    }

    /**
     * Test parsing with default SAX parser.
     *
     * @throws  Exception   Something bad happened.
     */
    public void testParse1() throws Exception {
        try {
            parser1.parse("data/qedeq.xml", null);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }

    /**
     * Test parsing with application parser.
     *
     * @throws  Exception   Something bad happened.
     */
    public void testParse2() throws Exception {
        try {
            parser2.parse("data/qedeq.xml", null);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }
}