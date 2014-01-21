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


/**
 * Test class {@link org.qedeq.kernel.se.dto.module.AuthorVo}.
 *
 * @author    Michael Meyling
 */
public class AuthorVoTest extends AbstractVoModuleTestCase {

    /** This class is tested. */
    private Class clazz = AuthorVo.class;

    private AuthorVo vo;

    protected Class getTestedClass() {
        return clazz;
    }

    public void testConstructor() {
        vo = new AuthorVo(null, null);
        assertNull(vo.getEmail());
        assertNull(vo.getName());
        vo = new AuthorVo(new LatexVo("en", "name"), "mail@mail.org");
        assertEquals("mail@mail.org", vo.getEmail());
        assertEquals(new LatexVo("en", "name"), vo.getName());
    }

}
