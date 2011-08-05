/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.base.test.QedeqTestCase;

/**
 * Test {@link Path}.
 *
 * @author Michael Meyling
 */
public class PathTest extends QedeqTestCase {

    /**
     * Test constructor and getters.
     *
     * @throws  Exception   Test failure.
     */
    public void testConstructor1() throws Exception {
        {
            final Path p1 = new Path("a/b/c");
            assertEquals("a/b/", p1.getDirectory());
            assertEquals("c", p1.getFileName());
            assertEquals(false, p1.isAbsolute());
            assertEquals(true, p1.isRelative());
            assertEquals("a/b/c", p1.toString());
            assertEquals(false, p1.isDirectory());
        }
        {
            final Path p1 = new Path("/a/b/c/");
            assertEquals("/a/b/c/", p1.getDirectory());
            assertEquals("", p1.getFileName());
            assertEquals(true, p1.isAbsolute());
            assertEquals(false, p1.isRelative());
            assertEquals("/a/b/c/", p1.toString());
            assertEquals(true, p1.isDirectory());
        }
        {
            final Path p1 = new Path("");
            assertEquals("", p1.getDirectory());
            assertEquals("", p1.getFileName());
            assertEquals(false, p1.isAbsolute());
            assertEquals(true, p1.isRelative());
            assertEquals("", p1.toString());
            assertEquals(true, p1.isDirectory());
        }
        try {
            new Path(null);
            fail("RuntimeException expected");
        } catch (RuntimeException e) {
            // ok
        }
        {
            final Path p1 = new Path("a/b/c/../../d/e");
            assertEquals("a/d/", p1.getDirectory());
            assertEquals("e", p1.getFileName());
            assertEquals(false, p1.isAbsolute());
            assertEquals(true, p1.isRelative());
            assertEquals("a/d/e", p1.toString());
            assertEquals(false, p1.isDirectory());
        }
        {
            final Path p1 = new Path("a/b/c/../../../d/e/");
            assertEquals("d/e/", p1.getDirectory());
            assertEquals("", p1.getFileName());
            assertEquals(false, p1.isAbsolute());
            assertEquals(true, p1.isRelative());
            assertEquals("d/e/", p1.toString());
            assertEquals(true, p1.isDirectory());
        }
    }

    /**
     * Test constructor and getters.
     *
     * @throws  Exception   Test failure.
     */
    public void testConstructor2() throws Exception {
        {
            final Path p1 = new Path("a/b/c/", "");
            assertEquals("a/b/c/", p1.getDirectory());
            assertEquals("", p1.getFileName());
            assertEquals(false, p1.isAbsolute());
            assertEquals(true, p1.isRelative());
            assertEquals("a/b/c/", p1.toString());
            assertEquals(true, p1.isDirectory());
        }
        {
            final Path p1 = new Path("a/b/c", "");
            assertEquals("a/b/c/", p1.getDirectory());
            assertEquals("", p1.getFileName());
            assertEquals(false, p1.isAbsolute());
            assertEquals(true, p1.isRelative());
            assertEquals("a/b/c/", p1.toString());
            assertEquals(true, p1.isDirectory());
        }
        {
            final Path p1 = new Path("a/b/c", null);
            assertEquals("a/b/c/", p1.getDirectory());
            assertEquals("", p1.getFileName());
            assertEquals(false, p1.isAbsolute());
            assertEquals(true, p1.isRelative());
            assertEquals("a/b/c/", p1.toString());
            assertEquals(true, p1.isDirectory());
        }
        {
            final Path p1 = new Path("a/b/c", "d");
            assertEquals("a/b/c/", p1.getDirectory());
            assertEquals("d", p1.getFileName());
            assertEquals(false, p1.isAbsolute());
            assertEquals(true, p1.isRelative());
            assertEquals("a/b/c/d", p1.toString());
            assertEquals(false, p1.isDirectory());
        }
        try {
            new Path((String[]) null, "");
            fail("RuntimeException expected");
        } catch (RuntimeException e) {
            // ok
        }
    }

    /**
     * Test constructor and getters.
     *
     * @throws  Exception   Test failure.
     */
    public void testConstructor3() throws Exception {
        {
            final Path p1 = new Path(new String[] {"a", "b", "c"}, "");
            assertEquals("a/b/c/", p1.getDirectory());
            assertEquals("", p1.getFileName());
            assertEquals(false, p1.isAbsolute());
            assertEquals(true, p1.isRelative());
            assertEquals("a/b/c/", p1.toString());
            assertEquals(true, p1.isDirectory());
        }
        {
            final Path p1 = new Path(new String[] {"", "a", "b", "c"}, "");
            assertEquals("/a/b/c/", p1.getDirectory());
            assertEquals("", p1.getFileName());
            assertEquals(true, p1.isAbsolute());
            assertEquals(false, p1.isRelative());
            assertEquals("/a/b/c/", p1.toString());
            assertEquals(true, p1.isDirectory());
        }
        {
            final Path p1 = new Path(new String[] {"a", "b", "c"}, null);
            assertEquals("a/b/c/", p1.getDirectory());
            assertEquals("", p1.getFileName());
            assertEquals(false, p1.isAbsolute());
            assertEquals(true, p1.isRelative());
            assertEquals("a/b/c/", p1.toString());
            assertEquals(true, p1.isDirectory());
        }
        {
            final Path p1 = new Path(new String[] {"a", "b", "c"}, "d");
            assertEquals("a/b/c/", p1.getDirectory());
            assertEquals("d", p1.getFileName());
            assertEquals(false, p1.isAbsolute());
            assertEquals(true, p1.isRelative());
            assertEquals("a/b/c/d", p1.toString());
            assertEquals(false, p1.isDirectory());
        }
    }

    /**
     * Test createRelative.
     * 
     */
    public void testCreateRelative() throws Exception {
        {
            final Path p1 = new Path("/a/b/c");
            assertEquals("d", p1.createRelative("/a/b/d").toString());
        }
        {
            final Path p1 = new Path("/a/b/c/");
            assertEquals("d/e", p1.createRelative("d/e").toString());
        }
        {
            final Path p1 = new Path("/a/b/c/");
            assertEquals("d/", p1.createRelative("d/").toString());
        }
        {
            final Path p1 = new Path("/a/b/c/");
            assertEquals("../../../d/", p1.createRelative("/d/").toString());
        }
        {
            final Path p1 = new Path("a/b/c/");
            assertEquals("/d", p1.createRelative("/d").toString());
        }
        {
            final Path p1 = new Path("a/b/c/");
            assertEquals("/d/e", p1.createRelative("/d/e").toString());
        }
        {
            final Path p1 = new Path("a/b/c/");
            assertEquals("a/b/d", p1.createRelative("a/b/d").toString());
        }
        {
            final Path p1 = new Path("/a/b/c/");
            assertEquals("", p1.createRelative("/a/b/c/").toString());
        }
        {
            final Path p1 = new Path("/a/b/c/");
            assertEquals("../d/e", p1.createRelative("/a/b/d/e").toString());
        }
    }

    /**
     * Test equals.
     *
     * @throws  Exception   Test failure.
     */
    public void testEquals1() throws Exception {
        {
            final Path p1 = new Path("a/b/c");
            final Path p2 = new Path("a/b/c");
            final Path p3 = new Path("a/d/c");
            assertEquals(p1, p2);
            assertFalse(p1.equals(null));
            assertFalse(p1.equals(p3));
            assertFalse(p3.equals(p1));
        }
        {
            final Path p1 = new Path("a/b/c/");
            final Path p2 = new Path("a/b/c/");
            final Path p3 = new Path("a/d/c/");
            assertEquals(p1, p2);
            assertFalse(p1.equals(null));
            assertFalse(p1.equals(p3));
            assertFalse(p3.equals(p1));
        }
        {
            final Path p1 = new Path("/a/b/c/");
            final Path p2 = new Path("/a/b/c/");
            final Path p3 = new Path("/a/d/c/");
            assertEquals(p1, p2);
            assertFalse(p1.equals(null));
            assertFalse(p1.equals(p3));
            assertFalse(p3.equals(p1));
        }
    }

    /**
     * Test hashCode.
     *
     * @throws  Exception   Test failure.
     */
    public void testHashCode() throws Exception {
        final Path p1 = new Path("a/b/c");
        final Path p2 = new Path("a/b/c");
        final Path p3 = new Path("a/d/c");
        assertEquals(p1.hashCode(), p2.hashCode());
        assertFalse(p1.hashCode() == p3.hashCode());
    }


}