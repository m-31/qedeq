/* $Id: QedeqParserTest.java,v 1.14 2007/12/21 23:35:18 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2007,  Michael Meyling <mime@qedeq.org>.
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
import org.qedeq.kernel.rel.test.text.KernelFacade;
import org.qedeq.kernel.test.QedeqTestCase;

/**
 * Tests correct charset handling by {@link SaxParser}, 
 * {@link org.qedeq.kernel.xml.parser.SaxDefaultHandler} (and higher level classes).
 *
 * @version $Revision: 1.14 $
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
        KernelContext.getInstance().checkModule(address);
    }

}