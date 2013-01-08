/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2013,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.kernel.bo.test.DummyPlugin;
import org.qedeq.kernel.xml.handler.common.AbstractSimpleHandler;
import org.qedeq.kernel.xml.handler.common.SaxDefaultHandler;
import org.qedeq.kernel.xml.handler.common.SimpleAttributes;
import org.qedeq.kernel.xml.handler.module.QedeqHandler;

/**
 * Tests the class {@link SaxParser}.
 *
 * @version $Revision: 1.1 $
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
            SaxDefaultHandler handler = new SaxDefaultHandler(new DummyPlugin());
            AbstractSimpleHandler simple = new AbstractSimpleHandler(handler, null) {

                public void startElement(final String name, final SimpleAttributes attributes) {
//                    System.out.println("<" + name + " " + attributes + ">");
                }

                public void endElement(final String name) {
//                    System.out.println("</" + name + ">");
                    // mime 20050205: for testing: throw new NullPointerException("");
                }

                public void characters(final String name, final String value) {
//                    System.out.println(value);
                }

                public void init() {
                    // do nothing
                }

            };
            handler.setBasisDocumentHandler(simple);
            parser1 = new SaxParser(DummyPlugin.getInstance(), handler);
        }
        {
            SaxDefaultHandler handler = new SaxDefaultHandler(new DummyPlugin());
            AbstractSimpleHandler simple = new QedeqHandler(handler);
            handler.setBasisDocumentHandler(simple);
            parser2 = new SaxParser(DummyPlugin.getInstance(), handler);
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
            parser1.parse(getFile("qedeq.xml"), getFile("qedeq.xml").toURL().toString());
        } catch (Exception e) {
            e.printStackTrace(System.out);
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
            parser2.parse(getFile("qedeq.xml"), getFile("qedeq.xml").toURL().toString());
        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw e;
        }
    }
}
