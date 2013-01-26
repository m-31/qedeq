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

package org.qedeq.kernel.se.visitor;

import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.kernel.se.dto.module.LatexListVo;
import org.qedeq.kernel.se.dto.module.LatexVo;

/**
 * Test class.
 *
 * @author Michael Meyling
 */
public class LatexList2TextTest extends QedeqTestCase {

    private LatexList2Text converter;

    public void setUp() throws Exception {
        super.setUp();
        this.converter = new LatexList2Text();
    }
 
    public void testGetLatex() {
        assertEquals("", converter.getLatex(null));
        assertEquals("", converter.getLatex(new LatexVo()));
        assertEquals("", converter.getLatex(new LatexVo(null, null)));
        assertEquals("", converter.getLatex(new LatexVo(null, "")));
        assertEquals("one", converter.getLatex(new LatexVo(null, "one")));
        assertEquals("one", converter.getLatex(new LatexVo("en", "one")));
        assertEquals("one", converter.getLatex(new LatexVo("en", "one\\index{two}")));
        assertEquals("sch\"on", converter.getLatex(new LatexVo("de", "\\bf{sch\"{o}n}")));
        assertEquals("\\pi^2", converter.getLatex(new LatexVo("en", "\\pi^2")));
        assertEquals("x^2", converter.getLatex(new LatexVo("en", "\\underline{x}^2")));
        assertEquals("B \\rightarrow A", converter.getLatex(new LatexVo("en", "B \\rightarrow A")));
    }

    public void testTransform() {
        LatexListVo list = null;
        assertEquals("", converter.transform(list));
        list = new LatexListVo();
        assertEquals("", converter.transform(list));
        list.add(new LatexVo("de", "zwei"));
        list.add(new LatexVo("it", "tre"));
        list.add(new LatexVo("en", "one"));
        assertEquals("one", converter.transform(list));
        assertEquals("one", converter.transform(list, null));
        assertEquals("one", converter.transform(list, "es"));
        assertEquals("one", converter.transform(list, "en"));
        assertEquals("tre", converter.transform(list, "it"));
        assertEquals("zwei", converter.transform(list, "de"));
        list = new LatexListVo();
        list.add(new LatexVo(null, "zwei"));
        list.add(new LatexVo("it", "tre"));
        assertEquals("zwei", converter.transform(list));
        assertEquals("tre", converter.transform(list, "it"));
        assertEquals("zwei", converter.transform(list, "en"));
        list.add(new LatexVo("en", "one"));
        assertEquals("one", converter.transform(list));
        list = new LatexListVo();
        list.add(new LatexVo("de", "zwei"));
        list.add(new LatexVo("it", "tre"));
        assertEquals("zwei", converter.transform(list));
        assertEquals("zwei", converter.transform(list, "en"));
    }

}
