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
package com.sun.syndication.io;

import java.io.IOException;
import java.io.InputStream;

import org.qedeq.base.test.QedeqTestCase;

/**
 * Testing {@link XmlReaderException}.
 *
 * @author  Michael Meyling
 */
public class XmlReaderExceptionTest extends QedeqTestCase {

    /** Test instance 1. */
    private XmlReaderException object1;
    private InputStream in1;

    private XmlReaderException object2;

    protected void setUp() throws Exception {
        super.setUp();
        in1 = new InputStream(){
            public int read() throws IOException {
                return 0;
            }
        };
        object1 = new XmlReaderException("XML reading failure", "UTF8", "UTF16", "UTF32", in1);
        object2 = new XmlReaderException("XML reading failure", "mime", "ISO", "UTF8", "UTF16", "UTF32", in1);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test creator.
     *
     * @throws  Exception   Something bad happened.
     */
    public void testCreator() throws Exception {
        new XmlReaderException("XML reading failure", "UTF8", "UTF16", "UTF32",
            new InputStream(){
                public int read() throws IOException {
                    return 0;
                }
            });
    }

    /**
     * Test getter.
     *
     * @throws  Exception   Something bad happened.
     */
    public void testGetter() throws Exception {
        assertEquals("XML reading failure", object1.getMessage());
        assertEquals("UTF8", object1.getBomEncoding());
        assertEquals("UTF16", object1.getXmlGuessEncoding());
        assertEquals("UTF32", object1.getXmlEncoding());
        assertSame(in1, object1.getInputStream());
        assertNull(object1.getContentTypeEncoding());
        assertNull(object1.getContentTypeMime());
    }

    /**
     * Test getter.
     *
     * @throws  Exception   Something bad happened.
     */
    public void testGetter2() throws Exception {
        assertEquals("XML reading failure", object2.getMessage());
        assertEquals("UTF8", object2.getBomEncoding());
        assertEquals("UTF16", object2.getXmlGuessEncoding());
        assertEquals("UTF32", object2.getXmlEncoding());
        assertSame(in1, object2.getInputStream());
        assertEquals("ISO", object2.getContentTypeEncoding());
        assertEquals("mime", object2.getContentTypeMime());
    }

}
