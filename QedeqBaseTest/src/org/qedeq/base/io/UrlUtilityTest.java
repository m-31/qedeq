/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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
import java.net.URI;
import java.net.URL;

import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.base.test.WebServer;

/**
 * Test {@link org.qedeq.kernel.utility.IoUtility}.
 *
 * @author  Michael Meyling
 */
public class UrlUtilityTest extends QedeqTestCase {


    public void testToUrl() throws Exception {
        final URL url = UrlUtility.toUrl(new File("."));
        final URI uri = new URI(url.toString());
        assertEquals(new File(".").getCanonicalPath(),
            new File(uri).getCanonicalPath());
    }

    public void testToFile() throws Exception {
        final File start = new File("empty path");
        assertEquals(UrlUtility.transformURLPathToFilePath(UrlUtility.toUrl(start)).getCanonicalPath(),
            start.getCanonicalPath());
    }

    public void testEasyUrl() throws Exception {
        final String url1 = "http://www.qedeq.org/sample.html#poke";
        assertEquals(url1, UrlUtility.easyUrl(url1));
        final String file = "/user/home/qedeq/sample1.qedeq";
        assertEquals(new File(file.replace('/', File.separatorChar)).getCanonicalPath(),
            UrlUtility.easyUrl("file://" + file));
    }

    public void testSaveUrlToFile() throws Exception {
        final File original = new File(getOutdir(), "testSaveUrlToFile1.txt");
        IoUtility.saveFile(original, "we don't need another hero");
        final File save = new File(getOutdir(), "testSaveUrlToFile2.txt");
        final WebServer server = new WebServer(8082, getOutdir(), 5000, true);
        server.start();
        UrlUtility.saveUrlToFile("http://localhost:8082/" + original.getName(), save, null, null, null, 0, 0,
            new LoadingListener() {
                public void loadingCompletenessChanged(double completeness) {
                    System.out.println(completeness);
                }
            }
        );
        assertTrue(IoUtility.compareFilesBinary(original, save));
        server.stop();
    }

}
