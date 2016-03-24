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

package org.qedeq.kernel.se.dto.module;

import org.qedeq.kernel.se.dto.list.DefaultAtom;


/**
 * Test class {@link org.qedeq.kernel.se.dto.module.TermVo}.
 *
 * @author    Michael Meyling
 */
public class TermVoTest extends AbstractVoModuleTestCase {

    /** This class is tested. */
    private Class clazz = TermVo.class;

    private TermVo v0;

    private TermVo v1;

    protected void setUp() throws Exception {
        super.setUp();
        v0 = new TermVo();
        v1 = new TermVo(new DefaultAtom("atom"));
    }

    protected Class getTestedClass() {
        return clazz;
    }

    public void testGetElement() {
        assertNull(v0.getElement());
        assertEquals(new DefaultAtom("atom"), v1.getElement());
    }

}
