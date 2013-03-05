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

package org.qedeq.kernel.se.visitor;

import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.dto.module.HeaderVo;
import org.qedeq.kernel.se.dto.module.QedeqVo;

/**
 * Test class.
 *
 * @author Michael Meyling
 */
public class DefaultContextCheckerTest extends QedeqTestCase {

    private DefaultContextChecker def;
    
    private QedeqVo qedeq;

    public void setUp() throws Exception {
        super.setUp();
        this.qedeq = new QedeqVo();
        final HeaderVo header = new HeaderVo();
        header.setEmail("test@me");
        this.qedeq.setHeader(header);
        this.def = new DefaultContextChecker();
    }
 
    /**
     * Test hash code generation.
     */
    public void testCheckHashCode() throws Exception {
        def.checkContext(qedeq, new ModuleContext(DefaultModuleAddress.MEMORY));
        def.checkContext(qedeq, new ModuleContext(DefaultModuleAddress.MEMORY, ".getHeader()"));
        def.checkContext(qedeq, new ModuleContext(DefaultModuleAddress.MEMORY, ".getHeader().getEmail()"));
        def.checkContext(qedeq, new ModuleContext(DefaultModuleAddress.MEMORY, ".getUnknown()"));
        def.checkContext(qedeq, new ModuleContext(DefaultModuleAddress.MEMORY, ".getHeader("));
    }

}
