/* This file is part of the project "Hilbert II" - http://www.qedeq.org
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

package org.qedeq.kernel.bo.latex;

import java.io.File;

import org.qedeq.kernel.bo.control.QedeqBoFactoryTest;

/**
 * Test generating LaTeX files for all known samples.
 *
 * @version $Revision: 1.1 $
 * @author Michael Meyling
 */
public final class ExtendedGenerateLatexTest extends GenerateLatexTest {

    /** This class. */
    private static final Class CLASS = ExtendedGenerateLatexTest.class;

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Start main process.
     *
     * @throws Exception
     */
    public void testGeneration() throws Exception {
        super.testGeneration();
    }

    /**
     * Call the generation of one LaTeX file and copy XML source to same destination directory for
     * all supported languages.
     * <p>
     * Tests also if the parsed context can be found by
     * {@link org.qedeq.kernel.xml.mapper.Context2XPath#getXPath(ModuleContext)}.
     *
     * @param   dir         Start directory.
     * @param   xml         Relative path to XML file. Must not be <code>null</code>.
     * @param   destinationDirectory Directory path for LaTeX file. Must not be <code>null</code>.
     * @param   onlyEn      Generate only for language "en".
     * @throws  Exception   Failure.
     */
    public void generate(final File dir, final String xml,
            final File destinationDirectory, final boolean onlyEn) throws Exception {
        generate(dir, xml, "en", destinationDirectory);
        if (!onlyEn) {
            generate(dir, xml, "de", destinationDirectory);
        }
        QedeqBoFactoryTest.loadQedeqAndAssertContext(new File(dir, xml));
    }

}
