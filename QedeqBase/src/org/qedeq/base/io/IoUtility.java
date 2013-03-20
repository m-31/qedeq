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

package org.qedeq.base.io;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.commons.lang.SystemUtils;


/**
 * A collection of useful static methods for input and output.
 *
 * LATER mime 20070101: use StringBuilder instead of StringBuffer if working under JDK 1.5
 *
 * @author  Michael Meyling
 */
public final class IoUtility {

    /**
     * Constructor, should never be called.
     */
    private IoUtility() {
        // don't call me
    }

    /**
     * Get default encoding for this system.
     *
     * @return  Default encoding for this system.
     */
    public static String getDefaultEncoding() {
        return SystemUtils.FILE_ENCODING;
// mime 20090630: under ubuntu the following gave the encoding ASCII:
//        return new InputStreamReader(
//              new ByteArrayInputStream(new byte[0])).getEncoding();
// but it was: file.encoding="ANSI_X3.41968"
    }

    /**
     * Get working Java encoding.
     *
     * @param   encoding    Try this encoding.
     * @return              This is <code>encoding</code> if it is supported. Or an other
     *                      encoding that is supported by this system.
     */
    public static String getWorkingEncoding(final String encoding) {
        if (encoding != null) {
            try {
                if (Charset.isSupported(encoding)
                        && Charset.forName(encoding).canEncode()) {
                    return encoding;
                }
            } catch (RuntimeException e) {
                // ignore
            }
        }
        // we must inform someone, but using
        // Trace within this class is not wise, because it is used
        // before the Trace is initialized.
        System.err.println("not supported encoding: " + encoding);
        return "ISO-8859-1";    // every system must support this
    }

    /**
     * Reads a file and returns the contents as a <code>String</code>.
     *
     * @param   filename    Name of the file (could include path).
     * @param   encoding    Take this encoding.
     * @return  Contents of file.
     * @throws  IOException File exception occurred.
     */
    public static String loadFile(final String filename, final String encoding)
            throws IOException {

        final StringBuffer buffer = new StringBuffer();
        loadFile(filename, buffer, encoding);
        return buffer.toString();
    }

    /**
     * Reads contents of a file into a string buffer.
     *
     * @param   filename    Name of the file (could include path).
     * @param   buffer      Buffer to fill with file contents.
     * @param   encoding    Take this encoding.
     * @throws  IOException File exception occurred.
     */
    public static void loadFile(final String filename,
            final StringBuffer buffer, final String encoding)
            throws IOException {
        loadFile(new File(filename), buffer, encoding);
    }

    /**
     * Reads contents of a stream into a string buffer. Stream is not closed.
     *
     * @param   in          This stream will be loaded.
     * @param   buffer      Buffer to fill with file contents.
     * @throws  IOException File exception occurred.
     *
     * @deprecated  Use {@link #loadReader(Reader, StringBuffer)}.
     */
    public static void loadStream(final InputStream in, final StringBuffer buffer)
            throws IOException {

        buffer.setLength(0);
        int c;
        while ((c = in.read()) >= 0) {
            buffer.append((char) c);
        }
    }

    /**
     * Returns contents of a stream into a string, respecting a maximum length.
     * No exceptions are thrown. Stream is not closed.
     *
     * @param   in          This stream will be loaded.
     * @param   maxLength   This length is not exceeded.
     * @return  readData    Data read, is not <code>null</code>.
     */
    public static String loadStreamWithoutException(final InputStream in, final int maxLength) {

        if (in == null) {
            return "";
        }
        final StringBuffer buffer = new StringBuffer();
        buffer.setLength(0);
        try {
            int counter = 0;
            int c;
            while (counter++ < maxLength) {
                c = in.read();
                if (c < 0) {
                    break;
                }
                buffer.append((char) c);
            }
        } catch (IOException e) {
            // ignored
        } catch (RuntimeException e) {
            // ignored
        }
        return buffer.toString();
    }

    /**
     * Reads contents of a {@link Reader} into a string buffer. Reader is not closed.
     *
     * @param   in          This reader will be loaded.
     * @param   buffer      Buffer to fill with file contents.
     * @throws  IOException File exception occurred.
     */
    public static void loadReader(final Reader in, final StringBuffer buffer)
            throws IOException {

        buffer.setLength(0);
        int c;
        while ((c = in.read()) >= 0) {
            buffer.append((char) c);
        }
    }

    /**
     * Reads contents of a file into a string buffer. Uses default encoding.
     *
     * @param   file        This file will be loaded.
     * @param   buffer      Buffer to fill with file contents.
     * @throws  IOException File exception occurred.
     *
     * @deprecated  Use {@link #loadFile(File, StringBuffer, String)}.
     */
    public static void loadFile(final File file,
            final StringBuffer buffer)
            throws IOException {

        final int size = (int) file.length();
        final char[] data = new char[size];
        buffer.setLength(0);
        FileReader in = null;
        try {
            in = new FileReader(file);
            int charsread = 0;
            while (charsread < size) {
                charsread += in.read(data, charsread, size - charsread);
            }
        } finally {
            close(in);
        }
        buffer.insert(0, data);
    }

    /**
     * Reads contents of a file into a string buffer.
     *
     * @param   file        This file will be loaded.
     * @param   buffer      Buffer to fill with file contents.
     * @param   encoding    Take this encoding.
     * @throws  IOException File exception occurred.
     */
    public static void loadFile(final File file,
            final StringBuffer buffer, final String encoding)
            throws IOException {

        buffer.setLength((int) file.length());    // ensure capacity
        buffer.setLength(0);
        final InputStreamReader in = new InputStreamReader(new FileInputStream(file), encoding);
        final char[] data = new char[10 * 1024];

        try {
            int charsread = 0;
            while (0 < (charsread = in.read(data, 0, data.length))) {
                buffer.append(data, 0, charsread);
            }
        } finally {
            in.close();
        }
    }

    /**
     * Reads a file and returns the contents as a <code>String</code>.
     *
     * @param   file        File to load from.
     * @return  Contents of file.
     * @throws  IOException File exception occurred.
     */
    public static final byte[] loadFileBinary(final File file) throws IOException {
        final int size = (int) file.length();
        final FileInputStream in = new FileInputStream(file);
        try {
            final byte[] data = new byte[size];
            int charsread = 0;
            while (charsread < size) {
                final int read = in.read(data, charsread, size - charsread);
                if (read == -1) {
                    final byte[] result = new byte[charsread];
                    System.arraycopy(data, 0, result, 0, charsread);
                    return result;
                }
                charsread += read;
            }
            in.close();
            return data;
        } finally {
            close(in);
        }
    }


    /**
     * Reads contents of an URL into a string buffer. The filling is character set dependent.
     * Content is added to the end of buffer. (Existing data is not cleared.)
     * <p>
     * All parameters should not be <code>null</code>.
     * @param   url         This URL will be loaded.
     * @param   buffer      Buffer to fill with file contents.
     * @throws  IOException Reading failed.
     *
     * @deprecated  Choose correct encoding.
     */
    public static void loadFile(final URL url, final StringBuffer buffer) throws IOException {
        InputStream in = null;
        BufferedReader dis = null;
        try {
            in = url.openStream();
            dis = new BufferedReader(new InputStreamReader(in));
            int i;
            while ((i = dis.read()) != -1) {
                buffer.append((char) i);
            }
        } finally {
            close(in);
            close(dis);
        }
    }

    /**
     * Reads contents of an URL into a StringBuffer. The filling is character set dependent. The
     * buffer is not cleared, contents is just added.
     * <p>
     * All parameters should not be <code>null</code>.
     * @param   url         This URL will be loaded.
     * @param   buffer      Buffer to fill with file contents.
     * @param   encoding    Take this encoding.
     * @throws  IOException Reading failed.
     */
    public static void loadFile(final URL url, final StringBuffer buffer, final String encoding)
            throws IOException {
        InputStream in = null;
        BufferedReader dis = null;
        try {
            in = url.openStream();
            dis = new BufferedReader(new InputStreamReader(in, encoding));
            int i;
            while ((i = dis.read()) != -1) {
                buffer.append((char) i);
            }
        } finally {
            close(in);
            close(dis);
        }
    }

    /**
     * Save binary contents of an URL into a file. Existing files are overwritten.
     *
     * @param   url     This URL will be loaded.
     * @param   file    Write into this file.
     * @throws  IOException Reading or writing failed.
     */
    public static void saveFile(final URL url, final File file) throws IOException {
        saveFile(url.openStream(), file);
    }

    /**
     * Save binary contents of an input stream into a file. The input stream is closed even
     * if exceptions occur.  Existing files are overwritten.
     * @param   in      Read this stream.
     * @param   file    Write into this file.
     *
     * @throws  IOException Reading or writing failed.
     */
    public static void saveFile(final InputStream in, final File file) throws IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            final byte[] data = new byte[8 * 1024];
            int length;
            while ((length = in.read(data)) != -1) {
                out.write(data, 0, length);
            }
        } finally {
            close(in);
            close(out);
        }
    }

    /**
     * Convert String into a {@link Reader}.
     *
     * <a href="http://bugs.sun.com/bugdatabase/view_bug.do;:WuuT?bug_id=4094886">
     * Bug ID: 4094886</a>
     *
     * @param   data    Convert this.
     * @return  Resulting reader.
     */
    public static final Reader stringToReader(final String data) {
        try {
            return new InputStreamReader(new ByteArrayInputStream(data.getBytes("ISO-8859-1")));
        } catch (UnsupportedEncodingException e) {
            // should never occur
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves a <code>String</code> into a file. Existing files are overwritten.
     *
     * @param   filename    Name of the file (could include path).
     * @param   text        Data to save in the file.
     * @throws  IOException File exception occurred.
     *
     * @deprecated  Use {@link #saveFile(File, String, String)} that has an encoding.
     */
    public static void saveFile(final String filename, final String text)
            throws IOException {
        saveFile(new File(filename), text);
    }

    /**
     * Saves a <code>StringBuffer</code> in a file. Existing files are overwritten.
     *
     * @param   filename    Name of the file (could include path).
     * @param   text        Data to save in the file.
     * @throws  IOException File exception occurred.
     *
     * @deprecated  Use {@link #saveFile(File, StringBuffer, String)} that has an encoding.
     */
    public static void saveFile(final String filename, final StringBuffer text)
            throws IOException {
        saveFile(new File(filename), text.toString());
    }

    /**
     * Saves a <code>StringBuffer</code> in a file. Existing files are overwritten.
     *
     * @param   file        File to save into.
     * @param   text        Data to save in the file.
     * @throws  IOException File exception occurred.
     *
     * @deprecated  Use {@link #saveFile(File, StringBuffer, String)} that has an encoding
     * parameter.
     */
    public static void saveFile(final File file, final StringBuffer text)
            throws IOException {
        saveFile(file, text.toString());
    }

    /**
     * Saves a <code>String</code> in a file. Uses default encoding.  Existing files are
     * overwritten.
     *
     * @param   file        File to save the data in.
     * @param   text        Data to save in the file.
     * @throws  IOException File exception occurred.
     *
     * @deprecated  Use {@link #saveFile(File, String, String)} that has an encoding parameter.
     */
    public static void saveFile(final File file, final String text)
            throws IOException {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(file));
            out.write(text);
        } finally {
            close(out);
        }
    }

    /**
     * Saves a <code>String</code> in a file.  Existing files are overwritten.
     *
     * @param   file        File to save the data in.
     * @param   text        Data to save in the file.
     * @param   encoding    Use this encoding.
     * @throws  IOException File exception occurred.
     */
    public static void saveFile(final File file, final StringBuffer text, final String encoding)
            throws IOException {
        saveFile(file, text.toString(), encoding);
    }

    /**
     * Saves a <code>String</code> in a file.
     *
     * @param   file        File to save the data in.
     * @param   text        Data to save in the file.
     * @param   encoding    Use this encoding.
     * @throws  IOException File exception occurred.
     */
    public static void saveFile(final File file, final String text, final String encoding)
            throws IOException {
        BufferedWriter out = new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(file), encoding));
        try {
            out.write(text);
        } finally {
            out.close();
        }
    }

    /**
     * Saves a <code>data</code> in a file. Existing files are overwritten.
     *
     * @param   file        File to save the data in.
     * @param   data        Data to save in the file.
     * @throws  IOException File exception occurred.
     */
    public static void saveFileBinary(final File file, final byte[] data)
            throws IOException {
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        try {
            out.write(data);
        } finally {
            out.close();
        }
    }

    /**
     * Copies a file to a different location.
     *
     * @param   from    Copy source.
     * @param   to      Copy destination.
     * @throws  IOException File exception occurred.
     */
    public static void copyFile(final File from, final File to)
            throws IOException {

        if (from.getCanonicalFile().equals(to.getCanonicalFile())) {
            return;
        }
        createNecessaryDirectories(to);
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(from);
            out = new FileOutputStream(to);

            byte[] data = new byte[8 * 1024];
            int length;
            while ((length = in.read(data)) != -1) {
                out.write(data, 0, length);
            }
        } finally {
            close(in);
            close(out);
        }
    }

    /**
     * Copy one file or directory to another location.
     * If targetLocation does not exist, it will be created.
     *
     * @param   sourceLocation  Copy from here. This can be a file or a directory.
     * @param   targetLocation  Copy to this location. If source is a file this must be a file too.
     * @throws  IOException     Something went wrong.
     */
    public static void copy(final String sourceLocation, final String targetLocation)
            throws IOException {
        copy(new File(sourceLocation), new File(targetLocation));
    }

    /**
     * Copy one directory to another location.
     * If targetLocation does not exist, it will be created.
     *
     * @param   sourceLocation  Copy from here.
     * @param   targetLocation  Copy to this location
     * @throws  IOException     Something went wrong.
     */
    public static void copy(final File sourceLocation, final File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }
            String[] children = sourceLocation.list();
            for (int i = 0; i < children.length; i++) { // recursive call for all children
                copy(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {    // copy file
            copyFile(sourceLocation, targetLocation);
        }
    }

    /**
     * List all matching files. Searches all matching sub directories recursively.
     * Remember to return <code>true</code> for <code>accept(File pathname)</code> if
     * <code>pathname</code> is a directory if you want to search all sub directories!
     * If <code>sourceLocation</code> is a single file, this is the only file that will
     * be in the resulting list.
     *
     * @param   sourceLocation  Check all files in this directory. (Or add this single file.)
     * @param   filter          Accept only these directories and files.
     * @return  List of matching files. Contains no directories.
     * @throws  IOException     Something went wrong.
     */
    public static List listFilesRecursively(final File sourceLocation, final FileFilter filter)
            throws IOException {
        final List result = new ArrayList();
        if (sourceLocation.isDirectory()) {
            final File[] children = sourceLocation.listFiles();
            for (int i = 0; i < children.length; i++) { // recursive call for all children
                result.addAll(listFilesRecursivelyIntern(children[i], filter));
            }
        } else {
            result.add(sourceLocation);
        }
        return result;
    }

    /**
     * List all matching files. Searches all matching sub directories recursively.
     * Remember to return <code>true</code> for <code>accept(File pathname)</code> if
     * <code>pathname</code> is a directory if you want to search all sub directories!
     *
     * @param   sourceLocation  Check all files in this directory.
     * @param   filter          Accept only these directories and files.
     * @return  List of matching files. Contains no directories.
     * @throws  IOException     Something went wrong.
     */
    private static List listFilesRecursivelyIntern(final File sourceLocation,
            final FileFilter filter) throws IOException {
        final List result = new ArrayList();
        if (filter.accept(sourceLocation)) {
            if (sourceLocation.isDirectory()) {
                File[] children = sourceLocation.listFiles();
                for (int i = 0; i < children.length; i++) { // recursive call for all children
                    result.addAll(listFilesRecursivelyIntern(children[i], filter));
                }
            } else {
                result.add(sourceLocation);
            }
        }
        return result;
    }

    /**
     * Compare two files binary.
     *
     * @param   from    Compare source. This file must be <code>null</code> or be an existing file.
     * @param   with    Compare with this file. This file must be <code>null</code> or be an
     *                  existing file.
     * @return  Is the contents of the two files binary equal?
     * @throws  IOException File exception occurred.
     */
    public static boolean compareFilesBinary(final File from, final File with)
            throws IOException {
        if (from == null && with == null) {
            return true;
        }
        if (from == null || with == null) {
            return false;
        }
        if (from.getAbsoluteFile().equals(with.getAbsoluteFile())) {
            return true;
        }
        if (from.length() != with.length()) {
            return false;
        }
        byte[] dataOne = new byte[8 * 1024];
        byte[] dataTwo = new byte[8 * 1024];
        int length;

        FileInputStream one = null;
        FileInputStream two = null;
        try {
            one = new FileInputStream(from);
            two = new FileInputStream(with);

            while ((length = one.read(dataOne)) != -1) {
                if (length != two.read(dataTwo)) {
                    return false;
                }
                if (!Arrays.equals(dataOne, dataTwo)) {
                    return false;
                }
            }
            return true;
        } finally {
            close(one);
            close(two);
        }
    }

    /**
     * Compare two text files. Ignores different line separators. As there are:
     * LF, CR, CR + LF, NEL, FF, LS, PS.
     *
     * @param   from        Compare source.
     * @param   with        Compare with this file.
     * @param   encoding    Use this character encoding. Must not be <code>null</code>.
     * @return  Is the contents of the two text files equal?
     * @throws  IOException File exception occurred or encoding is not supported.
     * @throws  NullPointerException    Is encoding different from <code>null</code>?
     */
    public static boolean compareTextFiles(final File from, final File with, final String encoding)
            throws IOException {
        if (from == null && with == null) {
            return true;
        }
        if (from == null || with == null) {
            return false;
        }
        if (from.getAbsoluteFile().equals(with.getAbsoluteFile())) {
            return true;
        }

        BufferedReader one = null;
        BufferedReader two = null;
        FileInputStream fromIn = null;
        FileInputStream withIn = null;
        try {
            fromIn = new FileInputStream(from);
            one = new BufferedReader(new InputStreamReader(fromIn, encoding));
            withIn = new FileInputStream(with);
            two = new BufferedReader(new InputStreamReader(withIn, encoding));

            boolean crOne = false;
            boolean crTwo = false;
            do {
                int readOne = one.read();
                int readTwo = two.read();
                if (readOne == readTwo) {
                    if (readOne < 0) {
                        break;
                    }
                } else {
                    crOne = readOne == 0x0D;
                    crTwo = readTwo == 0x0D;
                    if (crOne) {
                        readOne = one.read();
                    }
                    if (crTwo) {
                        readTwo = two.read();
                    }
                    if (crOne && readOne != 0x0A && isCr(readTwo)) {
                        readTwo = two.read();
                    }
                    if (crTwo && readTwo != 0x0A && isCr(readOne)) {
                        readOne = one.read();
                    }
                    if (readOne != readTwo && (!isCr(readOne) && !isCr(readTwo))) {
                        return false;
                    }
                }
            } while (true);
            return true;
        } finally {
            close(fromIn);
            close(one);
            close(two);
            close(withIn);
        }
    }

    /**
     * Compare two text files. Ignores different line separators. As there are:
     * LF, CR, CR + LF
     *
     * @param   from        Compare source.
     * @param   with        Compare with this file.
     * @param   startAtLine Start comparing at this line (beginning with 0).
     * @param   encoding    Use this character encoding. Must not be <code>null</code>.
     * @return  Is the contents of the two text files equal?
     * @throws  IOException File exception occurred or encoding is not supported.
     * @throws  NullPointerException    Is encoding different from <code>null</code>?
     */
    public static boolean compareTextFiles(final File from, final File with, final int startAtLine,
            final String encoding) throws IOException {

        if (from == null && with == null) {
            return true;
        }
        if (from == null || with == null) {
            return false;
        }
        if (from.getAbsoluteFile().equals(with.getAbsoluteFile())) {
            return true;
        }
        if (startAtLine < 0) {
            return true;
        }
        BufferedReader one = null;
        BufferedReader two = null;
        FileInputStream fromIn = null;
        FileInputStream withIn = null;
        try {
            fromIn = new FileInputStream(from);
            one = new BufferedReader(new InputStreamReader(fromIn, encoding));
            withIn = new FileInputStream(with);
            two = new BufferedReader(new InputStreamReader(withIn, encoding));
            int pos = 0;
            do {
                String lineOne = one.readLine();
                String lineTwo = two.readLine();
                if (lineOne == null) {
                    if (lineTwo == null) {
                        break;
                    }
                    return false;
                }
                if (pos++ >= startAtLine && !lineOne.equals(lineTwo)) {
                    return false;
                }
            } while (true);
            return true;
        } finally {
            close(fromIn);
            close(one);
            close(two);
            close(withIn);
        }
    }

    /**
     * Test if character is LF, CR, NEL, FF, LS, PS.
     * @param   c   Character to test.
     * @return  Is character a line terminator?
     */
    private static boolean isCr(final int c) {
        return c == 0x0A || c == 0x0D || c == 0x85 || c == 0x0C || c == 0x2028 || c == 0x2029;
    }

    /**
     * Delete file directory recursive.
     *
     * @param   directory   Directory to delete. Must not be a symbolic link.
     * @param   deleteDir   Delete directory itself too?
     * @return  Was deletion successful?
     */
    public static boolean deleteDir(final File directory, final boolean deleteDir) {

        // first we check if the file is a symbolic link
        try {
            if (isSymbolicLink(directory)) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
        final File candir;
        try {
            candir = directory.getCanonicalFile();
        } catch (IOException e) {
            return false;
        }

        // now we go through all of the files and subdirectories in the
        // directory and delete them one by one
        boolean success = true;
        File[] files = candir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];

                // in case this directory is actually a symbolic link, or it's
                // empty, we want to try to delete the link before we try
                // anything
                boolean deleted = file.delete();
                if (!deleted) {
                    // deleting the file failed, so maybe it's a non-empty
                    // directory
                    if (file.isDirectory()) {
                        deleted = deleteDir(file, true);
                    }

                    // otherwise, there's nothing else we can do
                }
                success = success && deleted;
            }
        }

        // now that we tried to clear the directory out, we can try to delete it
        if (deleteDir && directory.exists()) {
            return directory.delete();
        }
        return success;
    }

    /**
     * Delete directory contents for all files that match the filter. The main directory itself is
     * not deleted.
     *
     * @param   directory   Directory to scan for files to delete.
     * @param   filter      Filter files (and directories) to delete.
     * @return  Was deletion successful?
     */
    public static boolean deleteDir(final File directory, final FileFilter filter) {
        // first we check if the file is a symbolic link
        try {
            if (isSymbolicLink(directory)) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
        final File candir;
        try {
            candir = directory.getCanonicalFile();
        } catch (IOException e) {
            return false;
        }

        // now we go through all of the files and subdirectories in the
        // directory and delete them one by one
        boolean success = true;
        File[] files = candir.listFiles(filter);
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];

                // in case this directory is actually a symbolic link, or it's
                // empty, we want to try to delete the link before we try
                // anything
                boolean deleted = file.delete();
                if (!deleted) {
                    // deleting the file failed, so maybe it's a non-empty
                    // directory
                    if (file.isDirectory()) {
                        deleted = deleteDir(file, true);
                    }

                    // otherwise, there's nothing else we can do
                }
                success = success && deleted;
            }
        }

        return success;
    }

    /**
     * Determines whether the specified file is a symbolic link rather than an actual file.
     * See {@link
     * https://svn.apache.org/repos/asf/commons/proper/io/trunk/src/main/java/org/apache/commons/io/FileUtils.java}.
     * @param   file    File to check.
     * @return  Is the file is a symbolic link?
     * @throws  IOException     IO error while checking the file.
     */
    public static boolean isSymbolicLink(final File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("File must not be null");
        }
        // is windows file system in use?
        if (File.separatorChar == '\\') {
            // we have no symbolic links
            return false;
        }
        File fileInCanonicalDir = null;
        if (file.getParent() == null) {
            fileInCanonicalDir = file;
        } else {
            File canonicalDir = file.getParentFile().getCanonicalFile();
            fileInCanonicalDir = new File(canonicalDir, file.getName());
        }
        if (fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile())) {
            return false;
        }
        return true;
    }

    /**
     * Print current system properties to System.out.
     */
    public static void printAllSystemProperties() {
        Properties sysprops = System.getProperties();
        for (Enumeration e = sysprops.propertyNames(); e.hasMoreElements(); ) {
            String key = (String) e.nextElement();
            String value = sysprops.getProperty(key);
            System.out.println(key + "=" + value);
        }
    }

    /**
     * Get home directory of user.
     *
     * @return  Home directory of user.
     */
    public static File getUserHomeDirectory() {
        return new File((String) System.getProperties().get("user.home"));
    }

    /**
     * Creates necessary parent directories for a file.
     *
     * @param   file    File.
     * @throws  IOException Creation failed.
     */
    public static void createNecessaryDirectories(final File file) throws IOException {
        if (file != null && file.getParentFile() != null) {
            file.getParentFile().mkdirs();
            if (!file.getParentFile().exists()) {
                throw new IOException("directory creation failed: " + file.getParent());
            }
        }
    }

    /**
     * Create relative address from <code>origin</code> to <code>next</code>.
     * The resulting file path has "/" as directory name separator.
     * If the resulting file path is the same as origin specifies, we return "".
     * Otherwise the result will always have an "/" as last character.
     *
     * @param   origin  This is the original location. Must be a directory.
     * @param   next    This should be the next location. Must also be a directory.
     * @return  Relative (or if necessary absolute) file path.
     */
    public static final String createRelativePath(final File origin, final File next) {
        if (origin.equals(next)) {
            return "";
        }
        final Path org = new Path(origin.getPath().replace(File.separatorChar, '/'), "");
        final Path ne = new Path(next.getPath().replace(File.separatorChar, '/'), "");
        return org.createRelative(ne.toString()).toString();
    }

    /**
     * Waits until a '\n' was read from System.in.
     */
    public static void waitln() {
        System.out.println("\n..press <return> to continue");
        try {
            (new java.io.BufferedReader(new java.io.InputStreamReader(
                System.in))).readLine();
        } catch (IOException e) {
            // ignore
        }
    }

    /**
     * Closes input stream without exception.
     *
     * @param   in  Input stream, maybe <code>null</code>.
     */
    public static void close(final InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    /**
     * Closes writer without exception.
     *
     * @param   writer  Writer, maybe <code>null</code>.
     */
    public static void close(final Writer writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    /**
     * Closes out stream without exception.
     *
     * @param   out Output stream, maybe <code>null</code>.
     */
    public static void close(final OutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    /**
     * Closes input reader without exception.
     *
     * @param   reader  Reader, maybe <code>null</code>.
     */
    public static void close(final Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    /**
     * Get start directory for application. Within the start directory all newly created data is
     * stored in. If this is no Java Webstart version the result is
     * <code>new File(".")</code>. Otherwise the start directory is the subdirectory
     * "." concatenated <code>application</code> within <code>user.home</code>.
     *
     * @param   application Application name, used for Java Webstart version. Should
     *          be written in lowercase letters. A "." is automatically appended at
     *          the beginning.
     * @return  Start directory for application.
     */
    public static final File getStartDirectory(final String application) {
        final File startDirectory;
        if (isWebStarted()) {
            startDirectory = new File(getUserHomeDirectory(), "." + application);
        } else {
            startDirectory = new File(".");
        }
        return startDirectory;
    }

    /**
     * Was the application started by Java Webstart?
     *
     * @return  Was the application started by Java Webstart.
     */
    public static final boolean isWebStarted() {
        final String webStart = (String) System.getProperties().get("javawebstart.version");
        return webStart != null;
    }

    /**
     * Loads a property file from given URL.
     *
     * @param   url     URL to load properties from. Must not be <code>null</code>.
     * @return  Loaded properties.
     * @throws  IOException             Reading error.
     */
    public static Properties loadProperties(final URL url)
           throws IOException {
        Properties newprops = new Properties();
        InputStream in = null;
        try {
            in = url.openStream();
            newprops.load(in);
        } finally {
            close(in);
        }
        return newprops;
    }

    /**
     * Sleep my little class.
     *
     * @param   ms  Milliseconds to wait.
     */
    public static void sleep(final int ms) {
        final Object monitor = new Object();
        synchronized (monitor) {
            try {
                monitor.wait(ms);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * Get currently running java version and subversion numbers. This is the running JRE version.
     * If no version could be identified <code>null</code> is returned.
     *
     * @return  Array of version and subversion numbers.
     */
    public static int[] getJavaVersion() {
        final String version = System.getProperty("java.version");
        final List numbers = new ArrayList();
        final StringTokenizer tokenizer = new StringTokenizer(version, ".");
        while (tokenizer.hasMoreElements()) {
            String sub = tokenizer.nextToken();
            for (int i = 0; i < sub.length(); i++) {
                if (!Character.isDigit(sub.charAt(i))) {
                    sub = sub.substring(0, i);
                    break;
                }
            }
            try {
                numbers.add(new Integer(Integer.parseInt(sub)));
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        if (numbers.size() == 0) {
            return null;
        }
        final int[] result = new int[numbers.size()];
        for (int i = 0; i < numbers.size(); i++) {
            result[i] = ((Integer) numbers.get(i)).intValue();
        }
        return result;
    }

    /**
     * Get key sorted list of all System Properties.
     *
     * @return  Array with the two columns key and value.
     */
    public static String[][] getSortedSystemProperties() {
        final Map map = new TreeMap(System.getProperties());
        String[][] rowData = new String[map.size()][2];
        int rowNum = 0;
        final Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            rowData[rowNum][0] = (String) entry.getKey();
            rowData[rowNum][1] = (String) entry.getValue();
            rowNum++;
        }
        return rowData;
    }

}
