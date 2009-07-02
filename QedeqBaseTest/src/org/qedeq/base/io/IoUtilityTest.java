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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
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
     * Test {@link IoUtility#getDefaultEncoding()}. TODO mime 20090630 throws no exception no more
     *
     * @throws Exception Test failed.
     */
    public void testGetDefaultEncoding() throws Exception {
        assertEquals(System.getProperty("file.encoding"), IoUtility.getDefaultEncoding());
        final String encoding = new InputStreamReader(new ByteArrayInputStream(
            new byte[0])).getEncoding();
        // UTF-8 and UTF8 are the same, so we remove all "-" ...
        if (!StringUtility.replace(System.getProperty("file.encoding"), "-", "").equals(encoding)) {
            System.out.println("This system showed the java property \"file.encoding\" "
                + System.getProperty("file.encoding") + " but the detected writing default is: "
                + "\"" + IoUtility.getDefaultEncoding()
                + "\"\nThis might be ok, but you should check it");
        }
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
        assertTrue(file.delete());
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
        in.close();
        assertEquals(expected, buffer.toString());
        assertTrue(file.delete());
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
            assertTrue(file.delete());
        }
    }

    /**
     * Test {@link IoUtility#loadReader(Reader, StringBuffer)}.
     *
     * @throws Exception Test failed.
     */
    public void testLoadReader() throws Exception {
        final File file = new File("testLoadReaderStringBuffer.txt");
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
        assertTrue(file.delete());
    }

    /**
     * Test {@link IoUtility#loadFile(File, StringBuffer)}.
     *
     * @throws Exception Test failed.
     */
    public void testLoadFileFileStringBuffer() throws Exception {
        final File file = new File("testLoadFileFileStringBuffer.txt");
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
        assertTrue(file.delete());
    }

    /**
     * Test {@link IoUtility#loadFile(File, StringBuffer, String)}.
     *
     * @throws Exception Test failed.
     */
    public void testLoadFileFileStringBufferString() throws Exception {
        final File file = new File("testLoadFileFileStringBufferString.txt");
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
        assertTrue(file.delete());
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
    public void testLoadFileBinary1() throws Exception {
        final File file = new File("IoUtilityTestLoadBinary1.bin");
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
        assertTrue(file.delete());
    }

    /**
     * Test {@link IoUtility#loadFileBinary(File)}.
     *
     * @throws Exception Test failed.
     */
    public void testLoadFileBinary2() throws Exception {
        final File file = new File("IoUtilityTestLoadBinary2.bin");
        if (file.exists()) {
            assertTrue(file.delete());
        }
        final OutputStream out = new FileOutputStream(file);
        try {
            for (int j = 0; j < 512; j++) {
                for (int i = 0; i < 256; i++) {
                    out.write(i);
                }
                for (int i = 0; i < 256; i++) {
                    out.write(255 - i);
                }
            }
        } finally {
            out.close();
        }
        final byte [] loaded = IoUtility.loadFileBinary(file);
        assertEquals(512 * 2 * 256, loaded.length);
        for (int j = 0; j < 512; j++) {
            for (int i = 0; i < 256; i++) {
                assertEquals((byte) i, loaded[i + 512 * j]);
            }
            for (int i = 0; i < 256; i++) {
                assertEquals((byte) (255 - i), loaded[i + 256 + 512 * j]);
            }
        }
        assertTrue(file.delete());
    }

    /**
     * Test {@link IoUtility#saveFileBinary(File, byte[])}.
     *
     * @throws Exception Test failed.
     */
    public void testSaveFileBinary() throws Exception {
        final File file = new File("IoUtilityTestSaveFileBinary.bin");
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
        assertTrue(file.delete());
    }

    /**
     * Test {@link IoUtility#saveFileBinary(File, byte[])}.
     *
     * @throws Exception Test failed.
     */
    public void testLoadAndSaveFileBinary() throws Exception {
        final File file = new File("testLoadAndSaveFileBinary.bin");
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
        assertTrue(EqualsUtility.equals(data, IoUtility.loadFileBinary(file)));
        assertTrue(file.delete());
    }

    /**
     * Test {@link IoUtility#compareFilesBinary(File, File)}.
     *
     * @throws Exception Test failed.
     */
    public void testCompareFileBinary() throws Exception {
        final File file1 = new File("IoUtilityTestLoadBinary1.bin");
        if (file1.exists()) {
            assertTrue(file1.delete());
        }
        final File file2 = new File("IoUtilityTestLoadBinary2.bin");
        if (file2.exists()) {
            assertTrue(file2.delete());
        }
        assertFalse(IoUtility.compareFilesBinary(null, file1));
        assertFalse(IoUtility.compareFilesBinary(file2, null));
        assertTrue(IoUtility.compareFilesBinary(null, null));
        try {
            assertTrue(IoUtility.compareFilesBinary(file1, file2));
            fail("FileNotFoundException expected");
        } catch (FileNotFoundException e) {
            // expected;
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
        IoUtility.saveFileBinary(file2, new byte[] {});
        assertEquals(0, file2.length());
        assertFalse(IoUtility.compareFilesBinary(file1, file2));
        assertFalse(IoUtility.compareFilesBinary(file2, file1));
        assertTrue(IoUtility.compareFilesBinary(file1, file1));
        assertTrue(IoUtility.compareFilesBinary(file2, file2));
        IoUtility.saveFileBinary(file2, new byte[] {'A', 'B', 'C'});
        assertFalse(IoUtility.compareFilesBinary(file1, file2));
        assertFalse(IoUtility.compareFilesBinary(file2, file1));
        assertTrue(file1.delete());
        assertTrue(file2.delete());
    }

    /**
     * Test {@link IoUtility#loadFile(URL, StringBuffer)}.
     *
     * @throws Exception Test failed.
     */
    public void testLoadFileURLStringBuffer() throws Exception {
        final File file = new File("testLoadFileURLStringBuffer.txt");
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
        assertTrue(file.delete());
    }

    /**
     * Test {@link IoUtility#loadFile(URL, StringBuffer, String)}.
     *
     * @throws Exception Test failed.
     */
    public void testLoadFileURLStringBufferString() throws Exception {
        final File file = new File("testLoadFileURLStringBufferString.txt");
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
        assertTrue(file.delete());
    }

    /**
     * Test {@link IoUtility#saveFile(URL, File))}.
     *
     * @throws Exception Test failed.
     */
    public void testSaveFileURLFile() throws Exception {
        final File file1 = new File("testSaveFileURLFile1.txt");
        final File file2 = new File("testSaveFileURLFile2.txt");
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
        assertTrue(file1.delete());
        assertTrue(file2.delete());
    }

    /**
     * Test {@link IoUtility#saveFile(InputStream, File))}.
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
        }
        assertTrue(file1.delete());
        assertTrue(file2.delete());
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

    /**
     * Test {@link IoUtility#saveFile(File, String))}.
     *
     * @throws Exception Test failed.
     */
    public void testSaveFileFileString() throws Exception {
        final File file1 = new File("testSaveFileFileString1.txt");
        final File file2 = new File("testSaveFileFileString2.txt");
        InputStream in = null;
        try {
            if (file1.exists()) {
                assertTrue(file1.delete());
            }
            if (file2.exists()) {
                assertTrue(file2.delete());
            }
            IoUtility.saveFile(file1, StringUtility.hex2String(
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
        }
        assertTrue(file1.delete());
        assertTrue(file2.delete());
    }

    /**
     * Test {@link IoUtility#saveFile(File, StringBuffer))}.
     *
     * @throws Exception Test failed.
     */
    public void testSaveFileFileStringBuffer() throws Exception {
        final File file1 = new File("testSaveFileFileStringBuffer1.txt");
        final File file2 = new File("testSaveFileFileStringBuffer2.txt");
        InputStream in = null;
        try {
            if (file1.exists()) {
                assertTrue(file1.delete());
            }
            if (file2.exists()) {
                assertTrue(file2.delete());
            }
            final StringBuffer buffer = new StringBuffer(StringUtility.hex2String(
                "FF FE 49 00 20 00 61 00 6D 00 20 00 64 00 72 00"
                    + "65 00 61 00 6D 00 69 00 6E 00 67 00 20 00 6F 00"
                    + "66 00 20 00 61 00 20 00 77 00 68 00 69 00 74 00"
                    + "65 00 20 00 63 00 68 00 72 00 69 00 73 00 74 00"
                    + "6D 00 61 00 73 00 73 00 2E 00 2E 00 2E 00"));
            IoUtility.saveFile(file1, buffer);
            in = new FileInputStream(file1);
            IoUtility.saveFile(in, file2);
            assertTrue(IoUtility.compareFilesBinary(file1, file2));
        } finally {
            if (in != null) {
                in.close();
            }
        }
        assertTrue(file1.delete());
        assertTrue(file2.delete());
    }

    /**
     * Test {@link IoUtility#saveFile(File, String, String)))}.
     *
     * @throws Exception Test failed.
     */
    public void testSaveFileFileStringString() throws Exception {
        final File file1 = new File("testSaveFileFileStringString1.txt");
        final File file2 = new File("testSaveFileFileStringString2.txt");
        final File file3 = new File("testSaveFileFileStringString3.txt");
        InputStream in = null;
        try {
            if (file1.exists()) {
                assertTrue(file1.delete());
            }
            if (file2.exists()) {
                assertTrue(file2.delete());
            }
            if (file3.exists()) {
                assertTrue(file3.delete());
            }
            final String data = StringUtility.hex2String(
                "FF FE 49 00 20 00 61 00 6D 00 20 00 64 00 72 00"
                    + "65 00 61 00 6D 00 69 00 6E 00 67 00 20 00 6F 00"
                    + "66 00 20 00 61 00 20 00 77 00 68 00 69 00 74 00"
                    + "65 00 20 00 63 00 68 00 72 00 69 00 73 00 74 00"
                    + "6D 00 61 00 73 00 73 00 2E 00 2E 00 2E 00");
            IoUtility.saveFile(file1, data, "ISO-8859-1");
            in = new FileInputStream(file1);
            IoUtility.saveFile(in, file2);
            assertTrue(IoUtility.compareFilesBinary(file1, file2));
            IoUtility.saveFile(file3, "I am dreaming of a white christmass...", "UTF-16");
            assertEquals("I am dreaming of a white christmass...", IoUtility.loadFile(
                file3.toString(), "UTF-16"));
        } finally {
            if (in != null) {
                in.close();
            }
        }
        assertTrue(file1.delete());
        assertTrue(file2.delete());
        assertTrue(file3.delete());
    }

    /**
     * Test {@link IoUtility#saveFile(String, String)}.
     *
     * @throws Exception Test failed.
     */
    public void testSaveFileStringString() throws Exception {
        final File file1 = new File("testSaveFileStringString1.txt");
        final File file2 = new File("testSaveFileStringString2.txt");
        InputStream in = null;
        try {
            if (file1.exists()) {
                assertTrue(file1.delete());
            }
            if (file2.exists()) {
                assertTrue(file2.delete());
            }
            final StringBuffer buffer = new StringBuffer(StringUtility.hex2String(
                "FF FE 49 00 20 00 61 00 6D 00 20 00 64 00 72 00"
                    + "65 00 61 00 6D 00 69 00 6E 00 67 00 20 00 6F 00"
                    + "66 00 20 00 61 00 20 00 77 00 68 00 69 00 74 00"
                    + "65 00 20 00 63 00 68 00 72 00 69 00 73 00 74 00"
                    + "6D 00 61 00 73 00 73 00 2E 00 2E 00 2E 00"));
            IoUtility.saveFile(file1.toString(), buffer.toString());
            in = new FileInputStream(file1);
            IoUtility.saveFile(in, file2);
            assertTrue(IoUtility.compareFilesBinary(file1, file2));
        } finally {
            if (in != null) {
                in.close();
            }
        }
        assertTrue(file1.delete());
        assertTrue(file2.delete());
    }

    /**
     * Test {@link IoUtility#saveFile(String, StringBuffer)}.
     *
     * @throws Exception Test failed.
     */
    public void testSaveFileStringStringBuffer() throws Exception {
        final File file1 = new File("testSaveFileStringString1.txt");
        final File file2 = new File("testSaveFileStringString2.txt");
        InputStream in = null;
        try {
            if (file1.exists()) {
                assertTrue(file1.delete());
            }
            if (file2.exists()) {
                assertTrue(file2.delete());
            }
            final StringBuffer buffer = new StringBuffer(StringUtility.hex2String(
                "FF FE 49 00 20 00 61 00 6D 00 20 00 64 00 72 00"
                    + "65 00 61 00 6D 00 69 00 6E 00 67 00 20 00 6F 00"
                    + "66 00 20 00 61 00 20 00 77 00 68 00 69 00 74 00"
                    + "65 00 20 00 63 00 68 00 72 00 69 00 73 00 74 00"
                    + "6D 00 61 00 73 00 73 00 2E 00 2E 00 2E 00"));
            IoUtility.saveFile(file1.toString(), buffer);
            in = new FileInputStream(file1);
            IoUtility.saveFile(in, file2);
            assertTrue(IoUtility.compareFilesBinary(file1, file2));
        } finally {
            if (in != null) {
                in.close();
            }
        }
        assertTrue(file1.delete());
        assertTrue(file2.delete());
    }

    /**
     * Test {@link IoUtility#saveFile(File, StringBuffer, String)}.
     *
     * @throws Exception Test failed.
     */
    public void testSaveFileFileStringBufferString() throws Exception {
        final File file1 = new File("testSaveFileFileStringBufferString1.txt");
        final File file2 = new File("testSaveFileFileStringBufferString2.txt");
        final File file3 = new File("testSaveFileFileStringBufferString3.txt");
        InputStream in = null;
        try {
            if (file1.exists()) {
                assertTrue(file1.delete());
            }
            if (file2.exists()) {
                assertTrue(file2.delete());
            }
            if (file3.exists()) {
                assertTrue(file3.delete());
            }
            final String data = StringUtility.hex2String(
                "FF FE 49 00 20 00 61 00 6D 00 20 00 64 00 72 00"
                    + "65 00 61 00 6D 00 69 00 6E 00 67 00 20 00 6F 00"
                    + "66 00 20 00 61 00 20 00 77 00 68 00 69 00 74 00"
                    + "65 00 20 00 63 00 68 00 72 00 69 00 73 00 74 00"
                    + "6D 00 61 00 73 00 73 00 2E 00 2E 00 2E 00");
            final StringBuffer buffer = new StringBuffer(data);
            IoUtility.saveFile(file1, buffer, "ISO-8859-1");
            in = new FileInputStream(file1);
            IoUtility.saveFile(in, file2);
            assertTrue(IoUtility.compareFilesBinary(file1, file2));
            buffer.setLength(0);
            buffer.append("I am dreaming of a white christmass...");
            IoUtility.saveFile(file3, buffer, "UTF-16");
            assertEquals("I am dreaming of a white christmass...", IoUtility.loadFile(
                file3.toString(), "UTF-16"));
        } finally {
            if (in != null) {
                in.close();
            }
        }
        assertTrue(file1.delete());
        assertTrue(file2.delete());
        assertTrue(file3.delete());
    }

    /**
     * Test {@link IoUtility#copyFile(File, File)}.
     *
     * @throws Exception Test failed.
     */
    public void testCopyFile() throws Exception {
        final File file1 = new File("IoUtilityTestCopyFile1.bin");
        if (file1.exists()) {
            assertTrue(file1.delete());
        }
        final File file2 = new File("IoUtilityTestCopyFile2.bin");
        if (file2.exists()) {
            assertTrue(file2.delete());
        }
        Random random = new Random(1007);
        final byte data[] = new byte[1025];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) random.nextInt();
        }
        IoUtility.saveFileBinary(file1, data);
        IoUtility.copyFile(file1, file1);
        IoUtility.copyFile(file1, file2);
        assertTrue(IoUtility.compareFilesBinary(file1, file2));
        file1.delete();
        file2.delete();
    }


    /**
     * Test {@link IoUtility#compareTextFiles(File, File, String)}.
     *
     * @throws Exception Test failed.
     */
    public void testCompareTextFiles1() throws Exception {
        final File file1 = new File("testCompareTextFiles1.txt");
        if (file1.exists()) {
            assertTrue(file1.delete());
        }
        final File file2 = new File("testCompareTextFiles2.txt");
        if (file2.exists()) {
            assertTrue(file2.delete());
        }
        assertFalse(IoUtility.compareTextFiles(null, file1, "ISO-8859-1"));
        assertFalse(IoUtility.compareTextFiles(file2, null, "ISO-8859-1"));
        try {
            assertTrue(IoUtility.compareTextFiles(file1, file2, "ISO-8859-1"));
            fail("FileNotFoundException expected");
        } catch (FileNotFoundException e) {
            // expected;
        }
        Random random = new Random(1001);
        final byte data[] = new byte[1025];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) random.nextInt();
        }
        IoUtility.saveFileBinary(file1, data);
        IoUtility.saveFileBinary(file2, data);
        try {
            IoUtility.compareTextFiles(file1, file2, "iso");
            fail("UnsupportedEncodingException expected");
        } catch (UnsupportedEncodingException e) {
            // expected;
        }
        try {
            IoUtility.compareTextFiles(file1, file2, null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected;
        }
        assertTrue(IoUtility.compareTextFiles(file1, file2, "ISO-8859-1"));
        data[1000] += 1;
        IoUtility.saveFileBinary(file2, data);
        assertFalse(IoUtility.compareTextFiles(file1, file2, "ISO-8859-1"));
        data[999] -= 1;
        IoUtility.saveFileBinary(file2, data);
        assertFalse(IoUtility.compareTextFiles(file1, file2, "ISO-8859-1"));
        IoUtility.saveFileBinary(file2, new byte[] {});
        assertEquals(0, file2.length());
        assertFalse(IoUtility.compareTextFiles(file1, file2, "ISO-8859-1"));
        assertTrue(IoUtility.compareTextFiles(file1, file1, "ISO-8859-1"));
        assertTrue(IoUtility.compareTextFiles(file2, file2, "ISO-8859-1"));
        assertTrue(file1.delete());
        assertTrue(file2.delete());
    }

    /**
     * Test {@link IoUtility#compareTextFiles(File, File, String)}.
     *
     * @throws Exception Test failed.
     */
    public void testCompareTextFiles2() throws Exception {
        final String text = "When she goes, shes gone.@" + "If she stays, she stays here.@"
            + "The girl does what she wants to do.@" + "She knows what she wants to do.@"
            + "And I know Im fakin it,@" + "Im not really makin it.@" + "@"
            + "Im such a dubious soul,@" + "And a walk in the garden@" + "Wears me down.@"
            + "Tangled in the fallen vines,@" + "Pickin up the punch lines,@"
            + "Ive just been fakin it,@" + "Not really makin it.@" + "@" + "Is there any danger?@"
            + "No, no, not really.@" + "Just lean on me.@" + "Takin time to treat@"
            + "Your friendly neighbors honestly.@" + "Ive just been fakin it,@"
            + "Im not really makin it.@" + "This feeling of fakin it--@"
            + "I still havent shaken it.@" + "@" + "Prior to this lifetime@"
            + "I surely was a tailor.@" + "(good morning, mr. leitch.@"
            + "Have you had a busy day? )@" + "I own the tailors face and hands.@"
            + "I am the tailors face and hands and@" + "I know Im fakin it,@"
            + "Im not really makin it.@" + "This feeling of fakin it--@"
            + "I still havent shaken it.";
        final File file1 = new File("testCompareTextFiles1.txt");
        if (file1.exists()) {
            assertTrue(file1.delete());
        }
        final File file2 = new File("testCompareTextFiles2.txt");
        if (file2.exists()) {
            assertTrue(file2.delete());
        }
        IoUtility.saveFile(file1, "Line1", "ISO-8859-1");
        IoUtility.saveFile(file2, "Line1", "ISO-8859-1");

        assertFalse(IoUtility.compareTextFiles(null, file1, "ISO-8859-1"));
        assertFalse(IoUtility.compareTextFiles(file2, null, "ISO-8859-1"));
        assertTrue(IoUtility.compareTextFiles(null, null, "ISO-8859-1"));
        try {
            IoUtility.compareTextFiles(file1, file2, "iso");
            fail("UnsupportedEncodingException expected");
        } catch (UnsupportedEncodingException e) {
            // expected;
        }
        IoUtility.saveFile(file2, "line1", "ISO-8859-1");
        assertFalse(IoUtility.compareTextFiles(file1, file2, "ISO-8859-1"));
        assertFalse(IoUtility.compareTextFiles(file2, file1, "ISO-8859-1"));

        IoUtility.saveFile(file1, StringUtility.replace(text, "@", "\n"), "ISO-8859-1");
        IoUtility.saveFile(file2, StringUtility.replace(text, "@", "\n"), "ISO-8859-1");
        assertTrue(IoUtility.compareTextFiles(file1, file2, "ISO-8859-1"));
        assertTrue(IoUtility.compareTextFiles(file2, file1, "ISO-8859-1"));

        IoUtility.saveFile(file1, StringUtility.replace(text, "@", "\015\012"), "ISO-8859-1");
        IoUtility.saveFile(file2, StringUtility.replace(text, "@", "\015\012"), "ISO-8859-1");
        assertTrue(IoUtility.compareTextFiles(file1, file2, "ISO-8859-1"));
        assertTrue(IoUtility.compareTextFiles(file2, file1, "ISO-8859-1"));

        IoUtility.saveFile(file1, StringUtility.replace(text, "@", "\012"), "ISO-8859-1");
        IoUtility.saveFile(file2, StringUtility.replace(text, "@", "\015\012"), "ISO-8859-1");
        assertTrue(IoUtility.compareTextFiles(file1, file2, "ISO-8859-1"));
        assertTrue(IoUtility.compareTextFiles(file2, file1, "ISO-8859-1"));

        IoUtility.saveFile(file1, StringUtility.replace(text + "@", "@", "\012"), "ISO-8859-1");
        IoUtility.saveFile(file2, StringUtility.replace(text, "@", "\015\012"), "ISO-8859-1");
        assertTrue(IoUtility.compareTextFiles(file1, file2, "ISO-8859-1"));
        assertTrue(IoUtility.compareTextFiles(file2, file1, "ISO-8859-1"));

        IoUtility.saveFile(file1, StringUtility.replace(text, "@", "\012"), "ISO-8859-1");
        IoUtility.saveFile(file2, StringUtility.replace(text + "@", "@", "\015\012"), "ISO-8859-1");
        assertTrue(IoUtility.compareTextFiles(file1, file2, "ISO-8859-1"));
        assertTrue(IoUtility.compareTextFiles(file2, file1, "ISO-8859-1"));

        IoUtility.saveFile(file1, StringUtility.replace(text + "@", "@", "\012"), "ISO-8859-1");
        IoUtility.saveFile(file2, StringUtility.replace(text + "@", "@", "\015\012"), "ISO-8859-1");
        assertTrue(IoUtility.compareTextFiles(file1, file2, "ISO-8859-1"));
        assertTrue(IoUtility.compareTextFiles(file2, file1, "ISO-8859-1"));
        assertTrue(IoUtility.compareTextFiles(file1, file2, "UTF8"));
        assertTrue(IoUtility.compareTextFiles(file2, file1, "UTF8"));
        assertFalse(IoUtility.compareTextFiles(file1, file2, "UTF16"));
        assertFalse(IoUtility.compareTextFiles(file2, file1, "UTF16"));

        IoUtility.saveFile(file1, StringUtility.replace(text, "@", "\012"), "ISO-8859-1");
        IoUtility.saveFile(file2, StringUtility.replace(text, "@", "\012"), "UTF8");
        assertTrue(IoUtility.compareTextFiles(file1, file2, "ISO-8859-1"));
        assertTrue(IoUtility.compareTextFiles(file2, file1, "ISO-8859-1"));
        assertTrue(IoUtility.compareTextFiles(file1, file2, "UTF8"));
        assertTrue(IoUtility.compareTextFiles(file2, file1, "UTF8"));
        assertTrue(IoUtility.compareTextFiles(file1, file2, "UTF16"));
        assertTrue(IoUtility.compareTextFiles(file2, file1, "UTF16"));

        IoUtility.saveFile(file1, StringUtility.replace(text, "@", "\012"), "UTF16");
        IoUtility.saveFile(file2, StringUtility.replace(text, "@", "\012"), "UTF8");
        assertFalse(IoUtility.compareTextFiles(file1, file2, "UTF16"));
        assertFalse(IoUtility.compareTextFiles(file2, file1, "UTF8"));

        IoUtility.saveFile(file1, StringUtility.replace(text, "@", "\012"), "UTF16");
        IoUtility.saveFile(file2, StringUtility.replace(text, "@", "\012"), "UTF16");
        assertTrue(IoUtility.compareTextFiles(file1, file2, "ISO-8859-1"));
        assertTrue(IoUtility.compareTextFiles(file2, file1, "ISO-8859-1"));
        assertTrue(IoUtility.compareTextFiles(file1, file2, "UTF8"));
        assertTrue(IoUtility.compareTextFiles(file2, file1, "UTF8"));
        assertTrue(IoUtility.compareTextFiles(file1, file2, "UTF16"));
        assertTrue(IoUtility.compareTextFiles(file2, file1, "UTF16"));

        IoUtility.saveFile(file1, StringUtility.replace(text, "@", "\012"), "UTF16");
        IoUtility.saveFile(file2, StringUtility.replace(text, "@", "\012\015"), "UTF16");
        assertFalse(IoUtility.compareTextFiles(file1, file2, "ISO-8859-1"));
        assertFalse(IoUtility.compareTextFiles(file2, file1, "ISO-8859-1"));
        assertFalse(IoUtility.compareTextFiles(file1, file2, "UTF8"));
        assertFalse(IoUtility.compareTextFiles(file2, file1, "UTF8"));
        assertTrue(IoUtility.compareTextFiles(file1, file2, "UTF16"));
        assertTrue(IoUtility.compareTextFiles(file2, file1, "UTF16"));

        IoUtility.saveFile(file1, StringUtility.replace(text, "@", "\012"), "UTF16");
        IoUtility.saveFile(file2, StringUtility.replace(text, "@", "\015\012"), "UTF16");
        assertFalse(IoUtility.compareTextFiles(file1, file2, "ISO-8859-1"));
        assertFalse(IoUtility.compareTextFiles(file2, file1, "ISO-8859-1"));
        assertFalse(IoUtility.compareTextFiles(file1, file2, "UTF8"));
        assertFalse(IoUtility.compareTextFiles(file2, file1, "UTF8"));
        assertTrue(IoUtility.compareTextFiles(file1, file2, "UTF16"));
        assertTrue(IoUtility.compareTextFiles(file2, file1, "UTF16"));

        IoUtility.saveFile(file1, StringUtility.replace(text, "@", "\012"), "ISO-8859-1");
        IoUtility.saveFile(file2, StringUtility.replace(text, "@", "\015"), "ISO-8859-1");
        assertTrue(IoUtility.compareTextFiles(file1, file2, "ISO-8859-1"));
        assertTrue(IoUtility.compareTextFiles(file2, file1, "ISO-8859-1"));
        assertTrue(IoUtility.compareTextFiles(file1, file2, "UTF8"));
        assertTrue(IoUtility.compareTextFiles(file2, file1, "UTF8"));
        assertFalse(IoUtility.compareTextFiles(file1, file2, "UTF16"));
        assertFalse(IoUtility.compareTextFiles(file2, file1, "UTF16"));

        IoUtility.saveFile(file1, StringUtility.replace(text, "@", "\015\012"), "ISO-8859-1");
        IoUtility.saveFile(file2, StringUtility.replace(text, "@", "" + (char) 0x2029),
            "ISO-8859-1");
        assertTrue(IoUtility.compareTextFiles(file1, file2, "ISO-8859-1"));
        assertTrue(IoUtility.compareTextFiles(file2, file1, "ISO-8859-1"));
        assertTrue(IoUtility.compareTextFiles(file1, file2, "UTF8"));
        assertTrue(IoUtility.compareTextFiles(file2, file1, "UTF8"));
        assertFalse(IoUtility.compareTextFiles(file1, file2, "UTF16"));
        assertFalse(IoUtility.compareTextFiles(file2, file1, "UTF16"));

        assertTrue(file1.delete());
        assertTrue(file2.delete());
    }

    /**
     * Test {@link IoUtility#testCompareTextFiles(File, File, String)}.
     *
     * @throws Exception Test failed.
     */
    public void testDeleteDirFileBoolean() throws Exception {
        final File dir1 = new File("testDeleteDirFileBoolean_directory");
        if (dir1.exists()) {
            assertTrue(IoUtility.deleteDir(dir1, true));
        }
        final File dir2 = new File(dir1, "testDeleteDirFileBoolean_directory2");
        final File file1 = new File(dir1, "testDeleteDirFileBoolean1.txt");
        final File file2 = new File(dir1, "testDeleteDirFileBoolean2.txt");
        final File file3 = new File(dir2, "testDeleteDirFileBoolean3.txt");
        assertTrue(dir2.mkdirs());

        IoUtility.saveFile(file1, "File 1", "ISO-8859-1");
        IoUtility.saveFile(file2, "File 2", "ISO-8859-1");
        IoUtility.saveFile(file3, "File 3", "ISO-8859-1");
        assertTrue(IoUtility.deleteDir(dir1, true));
        assertTrue(!dir1.exists());
    }

    /**
     * Test {@link IoUtility#testCompareTextFiles(File, File, String)}.
     *
     * @throws Exception Test failed.
     */
    public void testDeleteDirFileFileFilter() throws Exception {
        final File dir1 = new File("testDeleteDirFileFileFilter_directory");
        if (dir1.exists()) {
            assertTrue(IoUtility.deleteDir(dir1, true));
        }
        final File dir2 = new File(dir1, "testDeleteDirFileFileFilter_directory2");
        final File file1 = new File(dir1, "testDeleteDirFileFileFilter1.txt");
        final File file2 = new File(dir1, "testDeleteDirFileFileFilter2.txt");
        final File file3 = new File(dir2, "testDeleteDirFileFileFilter_3.txt");
        assertTrue(dir2.mkdirs());

        IoUtility.saveFile(file1, "File 1", "ISO-8859-1");
        IoUtility.saveFile(file2, "File 2", "ISO-8859-1");
        IoUtility.saveFile(file3, "File 3", "ISO-8859-1");
        assertTrue(IoUtility.deleteDir(dir1, new FileFilter() {

            public boolean accept(final File pathname) {
                return pathname.toString().indexOf("testDeleteDirFileFileFilter_") >= 0;
            }}));
        assertTrue(dir1.exists());
        assertFalse(dir2.exists());
        assertFalse(file1.exists());
        assertFalse(file2.exists());
        assertFalse(file3.exists());
    }

}
