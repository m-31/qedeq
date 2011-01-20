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

package org.qedeq.base.utility;

import org.qedeq.base.test.QedeqTestCase;

/**
 * Test {@link Splitter}.
 *
 * @author Michael Meyling
 */
public class SplitterTest extends QedeqTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test "".
     *
     * @throws Exception
     */
    public void testSplitter00() throws Exception {
        final Splitter splitter = new Splitter("");
        assertFalse(splitter.hasNext());
    }

    /**
     * Test "a b c".
     *
     * @throws Exception
     */
    public void testSplitter01() throws Exception {
        final Splitter splitter = new Splitter("a b c");
        assertTrue(splitter.hasNext());
        assertEquals("a", splitter.nextToken());
        assertTrue(splitter.hasNext());
        assertEquals(" ", splitter.nextToken());
        assertTrue(splitter.hasNext());
        assertEquals("b", splitter.nextToken());
        assertTrue(splitter.hasNext());
        assertEquals(" ", splitter.nextToken());
        assertTrue(splitter.hasNext());
        assertEquals("c", splitter.nextToken());
        assertFalse(splitter.hasNext());
    }

    /**
     * Test "a b c ".
     *
     * @throws Exception
     */
    public void testSplitter02() throws Exception {
        final Splitter splitter = new Splitter("a b c ");
        assertTrue(splitter.hasNext());
        assertEquals("a", splitter.nextToken());
        assertTrue(splitter.hasNext());
        assertEquals(" ", splitter.nextToken());
        assertTrue(splitter.hasNext());
        assertEquals("b", splitter.nextToken());
        assertTrue(splitter.hasNext());
        assertEquals(" ", splitter.nextToken());
        assertTrue(splitter.hasNext());
        assertEquals("c", splitter.nextToken());
        assertTrue(splitter.hasNext());
        assertEquals(" ", splitter.nextToken());
        assertFalse(splitter.hasNext());
    }

    /**
     * Test " a b c ".
     *
     * @throws Exception
     */
    public void testSplitter03() throws Exception {
        final Splitter splitter = new Splitter(" a b c ");
        assertTrue(splitter.hasNext());
        assertEquals(" ", splitter.nextToken());
        assertTrue(splitter.hasNext());
        assertEquals("a", splitter.nextToken());
        assertTrue(splitter.hasNext());
        assertEquals(" ", splitter.nextToken());
        assertTrue(splitter.hasNext());
        assertEquals("b", splitter.nextToken());
        assertTrue(splitter.hasNext());
        assertEquals(" ", splitter.nextToken());
        assertTrue(splitter.hasNext());
        assertEquals("c", splitter.nextToken());
        assertTrue(splitter.hasNext());
        assertEquals(" ", splitter.nextToken());
        assertFalse(splitter.hasNext());
    }

    /**
     * Test " ".
     *
     * @throws Exception
     */
    public void testSplitter04() throws Exception {
        final Splitter splitter = new Splitter(" ");
        assertTrue(splitter.hasNext());
        assertEquals(" ", splitter.nextToken());
        assertFalse(splitter.hasNext());
    }

    /**
     * Test "  ".
     *
     * @throws Exception
     */
    public void testSplitter05() throws Exception {
        final Splitter splitter = new Splitter("  ");
        assertTrue(splitter.hasNext());
        assertEquals("  ", splitter.nextToken());
        assertFalse(splitter.hasNext());
    }

    /**
     * Test "       ".
     *
     * @throws Exception
     */
    public void testSplitter06() throws Exception {
        final Splitter splitter = new Splitter("       ");
        assertTrue(splitter.hasNext());
        assertEquals("       ", splitter.nextToken());
        assertFalse(splitter.hasNext());
    }

    /**
     * Test "       a".
     *
     * @throws Exception
     */
    public void testSplitter07() throws Exception {
        final Splitter splitter = new Splitter("       a");
        assertTrue(splitter.hasNext());
        assertEquals("       ", splitter.nextToken());
        assertTrue(splitter.hasNext());
        assertEquals("a", splitter.nextToken());
        assertFalse(splitter.hasNext());
    }

    /**
     * Test "b       ".
     *
     * @throws Exception
     */
    public void testSplitter08() throws Exception {
        final Splitter splitter = new Splitter("b       ");
        assertTrue(splitter.hasNext());
        assertEquals("b", splitter.nextToken());
        assertTrue(splitter.hasNext());
        assertEquals("       ", splitter.nextToken());
        assertFalse(splitter.hasNext());
    }

    /**
     * Test "bubart       ".
     *
     * @throws Exception
     */
    public void testSplitter09() throws Exception {
        final Splitter splitter = new Splitter("bubart       ");
        assertTrue(splitter.hasNext());
        assertEquals("bubart", splitter.nextToken());
        assertTrue(splitter.hasNext());
        assertEquals("       ", splitter.nextToken());
        assertFalse(splitter.hasNext());
    }

    /**
     * Test "hbubart       zulu".
     *
     * @throws Exception
     */
    public void testSplitter10() throws Exception {
        final Splitter splitter = new Splitter("hbubart       zulu");
        assertTrue(splitter.hasNext());
        assertEquals("hbubart", splitter.nextToken());
        assertTrue(splitter.hasNext());
        assertEquals("       ", splitter.nextToken());
        assertTrue(splitter.hasNext());
        assertEquals("zulu", splitter.nextToken());
        assertFalse(splitter.hasNext());
    }

    /**
     * Test " hbubart       zulu ".
     *
     * @throws Exception
     */
    public void testSplitter11() throws Exception {
        final Splitter splitter = new Splitter(" hbubart       zulu ");
        assertTrue(splitter.hasNext());
        assertEquals(" ", splitter.nextToken());
        assertTrue(splitter.hasNext());
        assertEquals("hbubart", splitter.nextToken());
        assertTrue(splitter.hasNext());
        assertEquals("       ", splitter.nextToken());
        assertTrue(splitter.hasNext());
        assertEquals("zulu", splitter.nextToken());
        assertTrue(splitter.hasNext());
        assertEquals(" ", splitter.nextToken());
        assertFalse(splitter.hasNext());
    }

}
