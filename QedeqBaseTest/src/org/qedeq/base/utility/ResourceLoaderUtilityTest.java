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

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.test.QedeqTestCase;

/**
 * Test {@link ResourceLoaderUtility}.
 *
 * @version $Revision: 1.1 $
 * @author Michael Meyling
 */
public class ResourceLoaderUtilityTest extends QedeqTestCase {

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
     * Test {@link ResourceLoaderUtility#getResourceFile(java.io.File, String, String)}.
     * TODO mime 2009ß614: This test must be run against a jar file!
     *
     * @throws  Exception   Test failure.
     */
    public void testGetResourceFile() throws Exception {
        File file = new File("./ResourceLoaderUtilityTestFile.txt");
        if (file.exists()) {
            assertTrue(file.delete());
        }
        file.createNewFile();
        IoUtility.saveFile(file, "once=the cow jumped over the moon",
            IoUtility.getDefaultEncoding());
        assertEquals("once=the cow jumped over the moon", IoUtility.loadFile(
            ResourceLoaderUtility.getResourceFile(new File("."), ".",
            "ResourceLoaderUtilityTestFile.txt").toString(), IoUtility.getDefaultEncoding()));
        String name = ResourceLoaderUtility.class.getName();
        String shortName = StringUtility.getClassName(ResourceLoaderUtility.class);
        final String directory = name.substring(0,
            name.length() - shortName.length()).replace('.', '/');
        file = ResourceLoaderUtility.getResourceFile(new File("."), directory,
            shortName + ".class");
//        long lastModifiedBefore = file.lastModified();
//        System.out.println(lastModifiedBefore);
        IoUtility.saveFileBinary(file, IoUtility.loadFileBinary(file));
        long lastModifiedAfter = file.lastModified();
//        System.out.println(lastModifiedAfter);
        file = ResourceLoaderUtility.getResourceFile(new File("."), directory,
            shortName + ".class");
        assertEquals(lastModifiedAfter, file.lastModified());
    }

}
