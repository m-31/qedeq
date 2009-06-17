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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
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
     * @throws Exception Test failed.
     */
    public void testGetDefaultEncoding() throws Exception {
        // UTF-8 and UTF8 are the same, so we remove all "-" ...
        assertEquals(StringUtility.replace(System.getProperty("file.encoding"), "-", ""),
            StringUtility.replace(IoUtility.getDefaultEncoding(), "-", ""));
    }

    /**
     * Test {@link IoUtility#loadFile(String fileName, String encoding)}.
     *
     * @throws Exception Test failed.
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
     * @throws Exception Test failed.
     */
    public void testGetWorkingEncoding() throws Exception {
        assertEquals("ISO-8859-1", IoUtility.getWorkingEncoding("unknown"));
        System.err.println("^ above text is ok, this was intended test behaviour");
        assertEquals("UTF-8", IoUtility.getWorkingEncoding("UTF-8"));
        assertEquals("UTF-16", IoUtility.getWorkingEncoding("UTF-16"));
        assertEquals("UTF16", IoUtility.getWorkingEncoding("UTF16"));
        assertEquals("UTF8", IoUtility.getWorkingEncoding("UTF8"));
        assertEquals("ISO-8859-1", IoUtility.getWorkingEncoding(null));
        System.err.println("^ above text is ok, this was intended test behaviour");
    }

    /**
     * Test {@link IoUtility#createRelativePath(final File origin, final File next)}.
     *
     * @throws Exception Test failed.
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
     * Test {@link IoUtility#loadStream(InputStream, StringBuffer)}.
     *
     * @throws Exception Test failed.
     */
    public void testLoadStreamInputStreamStringBuffer() throws Exception {
        final File file = new File("testLoadStreamInputStreamStringBuffer.txt");
        try {
            if (file.exists()) {
                assertTrue(file.delete());
            }
            final OutputStream out = new FileOutputStream(file);
            final String expected = "We all live in a yellow submarine ...";
            out.write(expected.getBytes());
            out.close();
            final StringBuffer buffer = new StringBuffer();
            final InputStream in = new FileInputStream(file);
            IoUtility.loadStream(in, buffer);
            assertEquals(expected, buffer.toString());
        } finally {
            file.delete();
        }
    }

    /**
     * Test {@link IoUtility#loadStreamWithoutException(InputStream, int)}.
     *
     * @throws Exception Test failed.
     */
    public void testLoadStreamWithoutException() throws Exception {
        final String expected = "We all live in a yellow submarine ...";
        assertEquals("", IoUtility.loadStreamWithoutException(null, 100));
        assertEquals("", IoUtility.loadStreamWithoutException(null, -100));
        assertEquals("", IoUtility.loadStreamWithoutException(new InputStream() {
            public int read() throws IOException {
                throw new IOException("i am an error input stream!");
            }}, 0));
        assertEquals("", IoUtility.loadStreamWithoutException(new InputStream() {
            public int read() throws IOException {
                throw new IOException("i am an error input stream!");
            }}, -99));
        assertEquals(expected, streamLoad(expected, expected.length()));
        assertEquals(expected, streamLoad(expected, expected.length() * 100));
        assertEquals(expected.substring(0, expected.length() - 1),
            streamLoad(expected, expected.length() - 1));
        assertEquals(expected.substring(0, 1),
            streamLoad(expected, 1));
        assertEquals("", streamLoad(expected, -2));

    }

    private String streamLoad(final String expected, final int length) throws FileNotFoundException,
            IOException {
        final File file = new File("testLoadStreamWithoutException.txt");
        if (file.exists()) {
            assertTrue(file.delete());
        }
        try {
            final OutputStream out = new FileOutputStream(file);
            try {
                out.write(expected.getBytes());
            } finally {
                if (out != null) {
                    out.close();
                }
            }
            final InputStream in = new FileInputStream(file);
            try {
                return IoUtility.loadStreamWithoutException(in, length);
            } finally {
                if (in != null) {
                    // assert that stream is not closed yet
                    in.close();
                }
            }
        } finally {
            file.delete();
        }
    }

    /**
     * Test {@link IoUtility#loadReader(Reader, StringBuffer)}.
     *
     * @throws Exception Test failed.
     */
    public void testLoadReader() throws Exception {
        final File file = new File("testLoadReaderStringBuffer.txt");
        try {
            if (file.exists()) {
                assertTrue(file.delete());
            }
            final Writer out = new FileWriter(file);
            final String expected = "We all live in a yellow submarine ...";
            out.write(expected);
            out.close();
            final StringBuffer buffer = new StringBuffer();
            final Reader in = new FileReader(file);
            IoUtility.loadReader(in, buffer);
            // test if reader can be closed
            in.close();
            assertEquals(expected, buffer.toString());
        } finally {
            file.delete();
        }
    }

    /**
     * Test {@link IoUtility#loadFile(File, StringBuffer)}.
     *
     * @throws Exception Test failed.
     */
    public void testLoadFileFileStringBuffer() throws Exception {
        final File file = new File("testLoadFileFileStringBuffer.txt");
        try {
            if (file.exists()) {
                assertTrue(file.delete());
            }
            final Writer out = new FileWriter(file);
            final String expected = "We all live in a yellow submarine ...";
            out.write(expected);
            out.close();
            final StringBuffer buffer = new StringBuffer();
            IoUtility.loadFile(file, buffer);
            assertEquals(expected, buffer.toString());
        } finally {
            file.delete();
        }
    }

    /**
     * Test {@link IoUtility#loadFile(File, StringBuffer, String)}.
     *
     * @throws Exception Test failed.
     */
    public void testLoadFileFileStringBufferString() throws Exception {
        final File file = new File("testLoadFileFileStringBufferString.txt");
        try {
            if (file.exists()) {
                assertTrue(file.delete());
            }
            final String expected1 = "We all live in a yellow submarine ...";
            final String encoding1 = "UTF-16";
            assertEquals(expected1, loadFile(file, expected1, encoding1));
            final String expected2 = "We all live in a yellow submarine\n"
                + "Yellow submarine, yellow submarine\n"
                + "We all live in a yellow submarine\n"
                + "Yellow submarine, yellow submarine\n"
                + "\n"
                + "As we live a life of ease\n"
                + "Everyone of us has all we need\n"
                + "Sky of blue and sea of green\n"
                + "In our yellow submarine ";
            final String encoding2 = "UnicodeBigUnmarked";
            assertEquals(expected2, loadFile(file, expected2, encoding2));
            final String encoding3 = "ASCII";
            assertEquals(expected2, loadFile(file, expected2, encoding3));
        } finally {
            file.delete();
        }
    }

    private String loadFile(final File file, final String expected, final String encoding)
            throws FileNotFoundException, IOException, UnsupportedEncodingException {
        final OutputStream out = new FileOutputStream(file);
        try {
            out.write(expected.getBytes(encoding));
        } finally {
            if (out != null) {
                out.close();
            }
        }
        final StringBuffer buffer = new StringBuffer();
        IoUtility.loadFile(file, buffer, encoding);
        return buffer.toString();
    }

    /**
     * Test {@link IoUtility#loadFileBinary(File)}.
     *
     * @throws Exception Test failed.
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
     * @throws Exception Test failed.
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
     * @throws Exception Test failed.
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
     * @throws Exception Test failed.
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

    /**
     * Test {@link IoUtility#loadFile(URL, StringBuffer)}.
     *
     * @throws Exception Test failed.
     */
    public void testLoadFileURLStringBuffer() throws Exception {
        final File file = new File("testLoadFileURLStringBuffer.txt");
        try {
            if (file.exists()) {
                assertTrue(file.delete());
            }
            final Writer out = new FileWriter(file);
            final String expected = "We all live in a yellow submarine ...";
            out.write(expected);
            out.close();
            final StringBuffer buffer = new StringBuffer();
            IoUtility.loadFile(file.toURL(), buffer);
            assertEquals(expected, buffer.toString());
        } finally {
            file.delete();
        }
    }

    /**
     * Test {@link IoUtility#loadFile(URL, StringBuffer, String)}.
     *
     * @throws Exception Test failed.
     */
    public void testLoadFileURLStringBufferString() throws Exception {
        final File file = new File("testLoadFileURLStringBufferString.txt");
        try {
            if (file.exists()) {
                assertTrue(file.delete());
            }
            final Writer out = new FileWriter(file);
            final String expected = "We all live in a yellow submarine ...\n";
            final String encoding = "UTF-8";
            out.write(expected);
            out.close();
            final StringBuffer buffer = new StringBuffer();
            IoUtility.loadFile(file.toURL(), buffer, encoding);
            assertEquals(expected, buffer.toString());
            IoUtility.saveFileBinary(file, StringUtility.hex2byte(
                "FF FE 49 00 20 00 61 00 6D 00 20 00 64 00 72 00"
                    + "65 00 61 00 6D 00 69 00 6E 00 67 00 20 00 6F 00"
                    + "66 00 20 00 61 00 20 00 77 00 68 00 69 00 74 00"
                    + "65 00 20 00 63 00 68 00 72 00 69 00 73 00 74 00"
                    + "6D 00 61 00 73 00 73 00 2E 00 2E 00 2E 00"));
            IoUtility.loadFile(file.toURL(), buffer, "UTF16");
            assertEquals(expected + "I am dreaming of a white christmass...", buffer.toString());
        } finally {
            file.delete();
        }
    }

    /**
     * Test {@link IoUtility#saveFile(URL, File))}.
     *
     * @throws Exception Test failed.
     */
    public void testSaveFileURLFile() throws Exception {
        final File file1 = new File("testSaveFileURLFile1.txt");
        final File file2 = new File("testSaveFileURLFile2.txt");
        try {
            if (file1.exists()) {
                assertTrue(file1.delete());
            }
            if (file2.exists()) {
                assertTrue(file2.delete());
            }
            IoUtility.saveFileBinary(file1, StringUtility.hex2byte(
                "FF FE 49 00 20 00 61 00 6D 00 20 00 64 00 72 00"
                    + "65 00 61 00 6D 00 69 00 6E 00 67 00 20 00 6F 00"
                    + "66 00 20 00 61 00 20 00 77 00 68 00 69 00 74 00"
                    + "65 00 20 00 63 00 68 00 72 00 69 00 73 00 74 00"
                    + "6D 00 61 00 73 00 73 00 2E 00 2E 00 2E 00"));
            IoUtility.saveFile(file1.toURL(), file2);
            assertTrue(IoUtility.compareFilesBinary(file1, file2));
        } finally {
            file1.delete();
            file2.delete();
        }
    }

    /**
     * Test {@link IoUtility#saveFile(URL, File))}.
     *
     * @throws Exception Test failed.
     */
    public void testSaveFileInputStreamFile() throws Exception {
        final File file1 = new File("testSaveFileInputStreamFile1.txt");
        final File file2 = new File("testSaveFileInputStreamFile2.txt");
        InputStream in = null;
        try {
            if (file1.exists()) {
                assertTrue(file1.delete());
            }
            if (file2.exists()) {
                assertTrue(file2.delete());
            }
            IoUtility.saveFileBinary(file1, StringUtility.hex2byte(
                "FF FE 49 00 20 00 61 00 6D 00 20 00 64 00 72 00"
                    + "65 00 61 00 6D 00 69 00 6E 00 67 00 20 00 6F 00"
                    + "66 00 20 00 61 00 20 00 77 00 68 00 69 00 74 00"
                    + "65 00 20 00 63 00 68 00 72 00 69 00 73 00 74 00"
                    + "6D 00 61 00 73 00 73 00 2E 00 2E 00 2E 00"));
            in = new FileInputStream(file1);
            IoUtility.saveFile(in, file2);
            assertTrue(IoUtility.compareFilesBinary(file1, file2));
        } finally {
            if (in != null) {
                in.close();
            }
            file1.delete();
            file2.delete();
        }
    }

    /**
     * Test {@link IoUtility#stringToReader(String))}.
     *
     * @throws Exception Test failed.
     */
    public void testStringToReader() throws Exception {
        final String expected = "We all live in a yellow submarine\n"
            + "Yellow submarine, yellow submarine\n"
            + "We all live in a yellow submarine\n"
            + "Yellow submarine, yellow submarine\n"
            + "\n"
            + "As we live a life of ease\n"
            + "Everyone of us has all we need\n"
            + "Sky of blue and sea of green\n"
            + "In our yellow submarine ";
        final Reader reader = IoUtility.stringToReader(expected);
        final StringBuffer buffer = new StringBuffer();
        int c;
        while (0 <= (c = (reader.read()))) {
            buffer.append((char) c);
        }
        assertEquals(expected, buffer.toString());
    }

}
