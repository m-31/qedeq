/* $Id: IoUtilityTest.java,v 1.1 2008/07/26 07:56:13 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2009,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.base.utility;

import java.io.File;

import org.qedeq.base.test.QedeqTestCase;

/**
 * Test {@link StringUtility}.
 * 
 * @version $Revision: 1.1 $
 * @author Michael Meyling
 */
public class StringUtilityTest extends QedeqTestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test replace(String, String, String).
     * 
     * @throws Exception
     */
    public void testReplace() throws Exception {
        assertEquals("13", StringUtility.replace("12", "2", "3"));
    }
    
}
