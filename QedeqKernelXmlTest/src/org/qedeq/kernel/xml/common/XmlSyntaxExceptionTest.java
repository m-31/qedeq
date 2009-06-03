/* $Id: SchemaTest.java,v 1.1 2008/07/26 08:01:09 m31 Exp $
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
     * Test constructors.
     *
     * @throws  Exception   Something bad happened.
     */
    public void testConstructor() throws Exception {
        XmlSyntaxException xse = XmlSyntaxException.createUnexpectedTagException("blueBerry");
        assertNull(xse.getCause());
        assertEquals(XmlSyntaxException.UNEXPECTED_TAG_CODE, xse.getErrorCode());
    }
}
