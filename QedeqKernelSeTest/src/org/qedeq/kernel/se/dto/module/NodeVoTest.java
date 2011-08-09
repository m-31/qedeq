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

package org.qedeq.kernel.se.dto.module;


/**
 * Test class {@link org.qedeq.kernel.se.dto.module.NodeVo}.
 *
 * @author    Michael Meyling
 */
public class NodeVoTest extends AbstractVoModuleTestCase {

    /** This object is tested. */
    private NodeVo node;

    protected void setUp() throws Exception {
        super.setUp();
        node = new NodeVo();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        node = null;
    }

    protected Class getTestedClass() {
        return NodeVo.class;
    }

    /**
     * Test {@link NodeVo#getId()} and {@link NodeVo#setId(String)}.
     */
    public void testSetGetLabel() {
        assertNull(node.getId());
        final String value = "mulumis";
        node.setId(value);
        assertEquals(value, node.getId());
    }

    /**
     * Test {@link NodeVo#getLevel()} and {@link NodeVo#setLevel(String)}.
     */
    public void testSetGetLevel() {
        assertNull(node.getLevel());
        final String value = "muasdfj7";
        node.setLevel(value);
        assertEquals(value, node.getLevel());
    }

    /**
     * Test {@link NodeVo#getName()} and {@link NodeVo#setName(LatexListVo)}.
     */
    public void testSetGetName() {
        assertNull(node.getName());
        final LatexListVo value = new LatexListVo();
        value.add(new LatexVo("dsfj", "8asdf67"));
        value.add(new LatexVo("dusi", "jasjfdsjf"));
        node.setName(value);
        assertEquals(value, node.getName());
    }

    /**
     * Test {@link NodeVo#getPrecedingText()} and
     * {@link NodeVo#setPrecedingText(LatexListVo)}.
     */
    public void testSetGetPrecedingText() {
        assertNull(node.getPrecedingText());
        final LatexListVo value = new LatexListVo();
        value.add(new LatexVo("tdsfj", "a8asdf67"));
        value.add(new LatexVo("tdusi", "ajasjfdsjf"));
        node.setPrecedingText(value);
        assertEquals(value, node.getPrecedingText());
    }

    /**
     * Test {@link NodeVo#getNodeType()} and {@link NodeVo#setNodeType(NodeType)}.
     */
    public void testSetGetNodeType() {
        assertNull(node.getNodeType());
        final PredicateDefinitionVo value = new PredicateDefinitionVo();
        node.setNodeType(value);
        assertEquals(value, node.getNodeType());
    }

    /**
     * Test {@link NodeVo#getSucceedingText()} and
     * {@link NodeVo#setSucceedingText(LatexListVo)}.
     */
    public void testSetGetSucceedingText() {
        assertNull(node.getSucceedingText());
        final LatexListVo value = new LatexListVo();
        value.add(new LatexVo("ftdsfj", "a8asddf67"));
        value.add(new LatexVo("ftdusi", "adjasjfdsjf"));
        node.setSucceedingText(value);
        assertEquals(value, node.getSucceedingText());
    }

    /**
     * Test {@link NodeVo#equals(Object)}.
     */
    public void testEquals() {
        final NodeVo node2 = new NodeVo();
        assertTrue(node.equals(node2));
        assertTrue(node2.equals(node));
        node.setId("Hei");
        assertFalse(node.equals(node2));
        assertFalse(node2.equals(node));
    }

    /**
     * Test {@link NodeVo#toString()}.
     *
     */
    public void testToString() {
        assertNotNull(node.toString());
        final NodeVo node2 = new NodeVo();
        assertEquals(node.toString(), node2.toString());
        node.setId("Hei");
    }

}
