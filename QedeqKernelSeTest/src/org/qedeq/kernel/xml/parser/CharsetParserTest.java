/* $Id: CharsetParserTest.java,v 1.1 2008/03/27 05:12:46 m31 Exp $
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

import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.context.KernelContext;
import org.qedeq.kernel.test.KernelFacade;
import org.qedeq.kernel.test.QedeqTestCase;

/**
 * Tests correct charset handling by {@link SaxParser}, 
 * {@link org.qedeq.kernel.xml.parser.SaxDefaultHandler} (and higher level classes).
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class CharsetParserTest extends QedeqTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        KernelFacade.startup();
    }

    protected void tearDown() throws Exception {
        KernelFacade.shutdown();
        super.tearDown();
    }

    /**
     * Test parsing with default SAX parser.
     *
     * @throws  Exception   Something bad happened.
     */
    public void testParse1() throws Exception {
        final ModuleAddress address = KernelContext.getInstance()
            .getModuleAddress("data/charset/qedeq_utf8_with_errors_01.xml ");
        KernelContext.getInstance().loadModule(address);
        assertFalse(KernelContext.getInstance().checkModule(address));
        final String[] errors = KernelContext.getInstance().getSourceFileExceptionList(address);
        assertEquals(1, errors.length);
        String[] lines = errors[0].split("\n");
        assertTrue(lines[0].endsWith("\"\u00e4\u00f6\u00fc\u00c4\u00d6\u00dc\u00df\u00e8\u00e9"
            + "\u00ea\u00eb\u00c8\u00c9\u00ca\u00cb\u20ac\" [2]"));
        assertTrue(lines[1].endsWith(":105:19"));
    }

    /**
     * Test parsing with default SAX parser.
     *
     * @throws  Exception   Something bad happened.
     */
    public void testParse2() throws Exception {
        final ModuleAddress address = KernelContext.getInstance()
            .getModuleAddress("data/charset/qedeq_utf16_with_errors_01.xml ");
        KernelContext.getInstance().loadModule(address);
        assertFalse(KernelContext.getInstance().checkModule(address));
        final String[] errors = KernelContext.getInstance().getSourceFileExceptionList(address);
        assertEquals(1, errors.length);
        String[] lines = errors[0].split("\n");
        assertTrue(lines[0].endsWith("\"\u00e4\u00f6\u00fc\u00c4\u00d6\u00dc\u00df\u00e8\u00e9"
            + "\u00ea\u00eb\u00c8\u00c9\u00ca\u00cb\u20ac\" [2]"));
        assertTrue(lines[1].endsWith(":105:19"));
    }

}
