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

package org.qedeq.kernel.se.dto.module;


/**
 * Test class {@link org.qedeq.kernel.se.dto.module.SubsectionVo}.
 *
 * @author  Michael Meyling
 */
public class SubsectionVoTest extends AbstractVoModuleTestCase {

    /** This class is tested. */
    private Class clazz = SubsectionVo.class;

    protected Class getTestedClass() {
        return clazz;
    }

    private SubsectionVo subsection;
    
    public void setUp() throws Exception {
        super.setUp();
        removeMethodToCheck("getNode");
        removeMethodToCheck("getSubsection");
        this.subsection = new SubsectionVo();
    }

    public void testGetSubsection() {
        assertEquals(subsection, subsection.getSubsection());
    }

    public void testGetNode() {
        assertNull(subsection.getNode());
    }


}
