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
package org.qedeq.kernel.xml.common;

import org.qedeq.base.test.QedeqTestCase;

/**
 * Testing {@link XmlSyntaxException}.
 *
 * @author    Michael Meyling
 */
public class XmlSyntaxExceptionTest extends QedeqTestCase {

    protected void setUp() throws Exception {
        super.setUp();
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
        XmlSyntaxException xse = XmlSyntaxException.createUnexpectedTagException("blueBerry");
        assertNull(xse.getCause());
        assertEquals(XmlSyntaxException.UNEXPECTED_TAG_CODE, xse.getErrorCode());
        RuntimeException re = new NullPointerException();
        xse = XmlSyntaxException.createByRuntimeException(re);
        assertEquals(XmlSyntaxException.PROGRAMMING_ERROR_CODE, xse.getErrorCode());
        assertEquals(re, xse.getCause());
        xse = XmlSyntaxException.createEmptyAttributeException("TAGGG", "Attribute");
        assertEquals(XmlSyntaxException.EMPTY_ATTRIBUTE_CODE, xse.getErrorCode());
        assertNull(xse.getCause());
        xse = XmlSyntaxException.createMissingAttributeException("TAAAG", "attribuTE");
        assertEquals(XmlSyntaxException.MISSING_ATTRIBUTE_CODE, xse.getErrorCode());
        assertNull(xse.getCause());
        xse = XmlSyntaxException.createUnexpectedTextDataException("tag", "ATTRIBUTE");
        assertEquals(XmlSyntaxException.UNEXPECTED_DATA_CODE, xse.getErrorCode());
        assertNull(xse.getCause());
    }
    
    /**
     * Test getter.
     *
     * @throws  Exception   Something bad happened.
     */
    public void testGetter() throws Exception {
        XmlSyntaxException xse = XmlSyntaxException.createUnexpectedTagException("blueBerry");
        assertNull(xse.getCause());
        assertEquals(XmlSyntaxException.UNEXPECTED_TAG_CODE, xse.getErrorCode());
    }

}
