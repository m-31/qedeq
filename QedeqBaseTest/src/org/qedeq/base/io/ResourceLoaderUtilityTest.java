/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2010,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.base.io;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.io.ResourceLoaderUtility;
import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.base.utility.StringUtility;

/**
 * Test {@link ResourceLoaderUtility}.
 *
 * @author Michael Meyling
 */
public class ResourceLoaderUtilityTest extends QedeqTestCase {

    private static final String ONCE_THE_COW_JUMPED_OVER_THE_MOON = "once=the cow jumped over the moon";

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
     * TODO mime 20090614: This test must be run against a jar file!
     *
     * @throws  Exception   Test failure.
     */
    public void testGetResourceFile() throws Exception {
        File file = new File("./ResourceLoaderUtilityTestFile.txt");
        if (file.exists()) {
            assertTrue(file.delete());
        }
        file.createNewFile();
        IoUtility.saveFile(file, ONCE_THE_COW_JUMPED_OVER_THE_MOON,
            IoUtility.getDefaultEncoding());
        assertEquals(ONCE_THE_COW_JUMPED_OVER_THE_MOON, IoUtility.loadFile(
            ResourceLoaderUtility.getResourceFile(new File("."), ".",
            "ResourceLoaderUtilityTestFile.txt").toString(), IoUtility.getDefaultEncoding()));
        String name = ResourceLoaderUtility.class.getName();
        String shortName = StringUtility.getClassName(ResourceLoaderUtility.class);
        final String directory = name.substring(0,
            name.length() - shortName.length()).replace('.', '/');
        file.delete();
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
        file.delete();
    }

    /**
     * Test {@link ResourceLoaderUtility#getResourceUrl(String)}.
     *
     * @throws  Exception   Test failure.
     */
    public void testGetResourceUrl() throws Exception {
        String name = ResourceLoaderUtilityTest.class.getName().replace('.', '/') + ".class";
//        System.out.println(name);
        final URL url = ResourceLoaderUtility.getResourceUrl(name);
//        System.out.println(url);
        final StringBuffer buffer = new StringBuffer();
        IoUtility.loadFile(url, buffer, IoUtility.getDefaultEncoding());
        assertTrue(buffer.toString().indexOf(ONCE_THE_COW_JUMPED_OVER_THE_MOON) >= 0);
    }

    /**
     * Test {@link ResourceLoaderUtility#getResourceAsStream(String)}.
     *
     * @throws  Exception   Test failure.
     */
    public void testGetResourceAsStream() throws Exception {
        String name = ResourceLoaderUtilityTest.class.getName().replace('.', '/') + ".class";
//        System.out.println(name);
        final InputStream stream = ResourceLoaderUtility.getResourceAsStream(name);
        assertTrue(IoUtility.loadStreamWithoutException(stream, 30000).indexOf(
            ONCE_THE_COW_JUMPED_OVER_THE_MOON) >= 0);
    }

}
