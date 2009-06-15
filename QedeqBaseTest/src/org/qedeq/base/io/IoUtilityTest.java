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

package org.qedeq.base.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.base.utility.StringUtility;

/**
 * Test {@link org.qedeq.kernel.utility.TextInput}.
 *
 * @version $Revision: 1.1 $
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
     * Test {@link IoUtility#getDefaultEncoding()}.
     *
     * @throws Exception
     */
    public void testGetDefaultEncoding() throws Exception {
        // UTF-8 and UTF8 are the same, so we remove all "-" ...
        assertEquals(StringUtility.replace(System.getProperty("file.encoding"), "-", ""),
            StringUtility.replace(IoUtility.getDefaultEncoding(), "-", ""));
    }

    /**
     * Test {@link IoUtility#loadFile(String fileName, String encoding)}.
     *
     * @throws Exception
     */
    public void testLoadFileStringString() throws Exception {
        final File file = new File("IoUtilityTestLoadStringString.txt");
        if (file.exists()) {
            assertTrue(file.delete());
        }
        IoUtility.saveFileBinary(file, StringUtility.hex2byte(
            "FF FE 49 00 20 00 61 00 6D 00 20 00 64 00 72 00"
                + "65 00 61 00 6D 00 69 00 6E 00 67 00 20 00 6F 00"
                + "66 00 20 00 61 00 20 00 77 00 68 00 69 00 74 00"
                + "65 00 20 00 63 00 68 00 72 00 69 00 73 00 74 00"
                + "6D 00 61 00 73 00 73 00 2E 00 2E 00 2E 00"));
        assertEquals("I am dreaming of a white christmass...", IoUtility.loadFile(file.toString(),
            "UTF16"));
    }

    /**
     * Test {@link IoUtility#getWorkingEncoding(String)}.
     *
     * @throws Exception
     */
    public void testGetWorkingEncoding() throws Exception {
        assertEquals("ISO-8859-1", IoUtility.getWorkingEncoding("unknown"));
        System.err.println("^ above text is ok, this was intended test behaviour");
        assertEquals("UTF-8", IoUtility.getWorkingEncoding("UTF-8"));
        assertEquals("UTF-16", IoUtility.getWorkingEncoding("UTF-16"));
        assertEquals("UTF16", IoUtility.getWorkingEncoding("UTF16"));
        assertEquals("UTF8", IoUtility.getWorkingEncoding("UTF8"));
    }

    /**
     * Test {@link IoUtility#createRelativePath(final File origin, final File next)}.
     *
     * @throws Exception
     */
    public void testCreateRelativePath() throws Exception {
        assertEquals("local", IoUtility.createRelativePath(new File("."), new File("local")));
        assertEquals("text", IoUtility.createRelativePath(new File("/local/data"),
            new File("/local/data/text")));
        assertEquals("../../green", IoUtility.createRelativePath(new File("/local/data"),
            new File("/green")));
        assertEquals("../green", IoUtility.createRelativePath(new File("/local/data"),
            new File("/local/green")));
    }

    /**
     * Test {@link IoUtility#loadFileBinary(File)}.
     *
     * @throws Exception
     */
    public void testLoadFileBinary() throws Exception {
        final File file = new File("IoUtilityTestLoadBinary.bin");
        if (file.exists()) {
            assertTrue(file.delete());
        }
        final OutputStream out = new FileOutputStream(file);
        try {
            for (int i = 0; i < 256; i++) {
                out.write(i);
            }
            for (int i = 0; i < 256; i++) {
                out.write(i);
            }
        } finally {
            out.close();
        }
        final byte [] loaded = IoUtility.loadFileBinary(file);
        assertEquals(512, loaded.length);
        for (int i = 0; i < 256; i++) {
            assertEquals((byte) i, loaded[i]);
        }
        for (int i = 0; i < 256; i++) {
            assertEquals((byte) i, loaded[i]);
        }
        file.delete();
    }

    /**
     * Test {@link IoUtility#saveFileBinary(File, byte[])}.
     *
     * @throws Exception
     */
    public void testSaveFileBinary() throws Exception {
        final File file = new File("IoUtilityTestLoadBinary.bin");
        if (file.exists()) {
            assertTrue(file.delete());
        }
        final byte data[] = new byte[512];
        for (int i = 0; i < 512; i++) {
            data[i] = (byte) i;
        }
        IoUtility.saveFileBinary(file, data);
        final InputStream in = new FileInputStream(file);
        try {
            for (int i = 0; i < 256; i++) {
                assertEquals(i, in.read());
            }
            for (int i = 0; i < 256; i++) {
                assertEquals(i, in.read());
            }
            assertEquals(-1, in.read());
        } finally {
            in.close();
        }
        file.delete();
    }

    /**
     * Test {@link IoUtility#saveFileBinary(File, byte[])}.
     *
     * @throws Exception
     */
    public void testLoadAndSaveFileBinary() throws Exception {
        final File file = new File("IoUtilityTestLoadBinary.bin");
        if (file.exists()) {
            assertTrue(file.delete());
        }
        Random random = new Random(1001);
        final byte data[] = new byte[100000];
        for (int i = 0; i < 512; i++) {
            data[i] = (byte) i;
        }
        for (int i = 512; i < data.length; i++) {
            data[i] = (byte) random.nextInt();
        }
        IoUtility.saveFileBinary(file, data);
        EqualsUtility.equals(data, IoUtility.loadFileBinary(file));
        file.delete();
    }

    /**
     * Test {@link IoUtility#compareFilesBinary(File, File)}.
     *
     * @throws Exception
     */
    public void testCompareFile() throws Exception {
        final File file1 = new File("IoUtilityTestLoadBinary1.bin");
        if (file1.exists()) {
            assertTrue(file1.delete());
        }
        final File file2 = new File("IoUtilityTestLoadBinary2.bin");
        if (file2.exists()) {
            assertTrue(file2.delete());
        }
        Random random = new Random(1001);
        final byte data[] = new byte[1025];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) random.nextInt();
        }
        IoUtility.saveFileBinary(file1, data);
        IoUtility.saveFileBinary(file2, data);
        assertTrue(IoUtility.compareFilesBinary(file1, file2));
        data[1000] += 1;
        IoUtility.saveFileBinary(file2, data);
        assertFalse(IoUtility.compareFilesBinary(file1, file2));
        data[999] -= 1;
        IoUtility.saveFileBinary(file2, data);
        assertFalse(IoUtility.compareFilesBinary(file1, file2));
        file1.delete();
        file2.delete();
    }

}
