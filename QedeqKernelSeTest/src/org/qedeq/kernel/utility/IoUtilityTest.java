/* $Id: IoUtilityTest.java,v 1.2 2008/03/27 05:12:44 m31 Exp $
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

package org.qedeq.kernel.utility;

import java.io.File;

import org.qedeq.kernel.test.QedeqTestCase;

/**
 * Test {@link org.qedeq.kernel.utility.TextInput}.
 *
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */
public class IoUtilityTest extends QedeqTestCase {


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
     * Test createRelativePath(final File orgin, final File next).
     * 
     * @throws Exception
     */
    public void testCreateRelativePath() throws Exception {
        assertEquals("local", IoUtility.createRelativePath(new File("."), new File ("local")));
        assertEquals("text", IoUtility.createRelativePath(new File("/local/data"), 
            new File ("/local/data/text")));
        assertEquals("../../green", IoUtility.createRelativePath(new File("/local/data"), 
            new File ("/green")));
        assertEquals("../green", IoUtility.createRelativePath(new File("/local/data"), 
            new File ("/local/green")));
    }
    
}
