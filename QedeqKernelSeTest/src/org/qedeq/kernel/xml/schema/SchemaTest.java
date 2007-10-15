/* $Id: SchemaTest.java,v 1.9 2007/10/07 16:43:10 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2007,  Michael Meyling <mime@qedeq.org>.
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
package org.qedeq.kernel.xml.schema;

import org.qedeq.kernel.test.QedeqTestCase;

/**
 * This class tests the XML schema files under <code>org.qedeq.kernel.xml.schema</code>.
 *
 * @version $Revision: 1.9 $
 * @author    Michael Meyling
 */
public class SchemaTest extends QedeqTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test XSLT transformation.
     *
     * @throws  Exception   Something bad happened.
     */
    public void testXml() throws Exception {
        org.apache.xalan.xslt.Process.main(new String[] {"-IN", "data/qedeq.xml", "-XSL",
                "data/qedeq.xsl",
                "-OUT", "../../../qedeq_gen/qedeq.html", "-HTML"});
    }
}
