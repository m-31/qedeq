/* $Id: IoUtility.java,v 1.14 2008/01/26 12:39:10 m31 Exp $
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

package org.qedeq.kernel.utility;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;


/**
 * A collection of useful static methods for input and output.
 *
 * LATER mime 20070101: use StringBuilder instead of StringBuffer if working under JDK 1.5
 *
 * FIXME mime 20070926: load* and loadBinary are mixed. loarUrl should be binary load!
 *
 * @version $Revision: 1.14 $
 * @author  Michael Meyling
 */
public final class IoUtility {

    /**
     * Constructor, should never be called.
     */
    private IoUtility() {
        // don't call me
    }

    public static String getDefaultEncoding() {
        return new InputStreamReader(
              new ByteArrayInputStream(new byte[0])).getEncoding();
    }


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
        // FIXME
        System.err.println("not supported encoding: " + encoding);
        return "ISO-8859-1";
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
     * Reads contents of a stream into a string buffer.
     *
     * @param   in          This stream will be loaded.
     * @param   buffer      Buffer to fill with file contents.
     * @throws  IOException File exception occurred.
     */
    public static void loadStream(final InputStream in,
            final StringBuffer buffer)
            throws IOException {

        buffer.setLength(0);
        int c;
        while ((c = in.read()) >= 0) {
            buffer.append((char) c);
        }
    }
// FIXME
    /**
     * Reads contents of a file into a string buffer.
     *
     * @param   file        This file will be loaded.
     * @param   buffer      Buffer to fill with file contents.
     * @throws  IOException File exception occurred.
     *
    public static void loadFile(final File file,
            final StringBuffer buffer)
            throws IOException {

        final int size = (int) file.length();
        buffer.setLength(0);
        final FileReader in = new FileReader(file);
        final char[] data = new char[size];
        int charsread = 0;
        while (charsread < size) {
            charsread += in.read(data, charsread, size - charsread);
        }
        in.close();
        buffer.insert(0, data);
    }
*/
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
            closeStream(in);
        }
    }

// FIXME    
    /**
     * Reads contents of an URL into a string buffer. The filling is character set dependent.
     * FIXME mime 20071230: what about binary load?
     * @param   url         This URL will be loaded.
     * @param   buffer      Buffer to fill with file contents.
     * @throws  IOException Reading failed.
     *
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
            closeStream(in);
            closeReader(dis);
        }
    }
*/
    /**
     * Save binary contents of an URL into a file.
     *
     * @param   url     This URL will be loaded.
     * @param   file    Write into this file.
     * @throws  IOException Reading or writing failed.
     */
    public static void saveFile(final URL url, final File file) throws IOException {
        final InputStream in = url.openStream();
        final FileOutputStream out = new FileOutputStream(file);

        byte[] data = new byte[8 * 1024];
        int length;

        while ((length = in.read(data)) != -1) {
            out.write(data, 0, length);
        }
        in.close();
        out.close();
    }
/*
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
            throw new RuntimeException(e);
        }
    }

//FIXME
    /**
     * Saves a <code>String</code> into a file.
     *
     * @param   filename    Name of the file (could include path).
     * @param   text        Data to save in the file.
     * @throws  IOException File exception occurred.
     *
    public static void saveFile(final String filename, final String text)
            throws IOException {
        saveFile(new File(filename), text);
    }

    /**
     * Saves a <code>StringBuffer</code> in a file.
     *
     * @param   filename    Name of the file (could include path).
     * @param   text        Data to save in the file.
     * @throws  IOException File exception occurred.
     *
    public static void saveFile(final String filename, final StringBuffer text)
            throws IOException {
        saveFile(new File(filename), text.toString());
    }

*/
    /** FIXME comment in
     * Saves a <code>StringBuffer</code> in a file.
     *
     * @param   file        File to save into.
     * @param   text        Data to save in the file.
     * @throws  IOException File exception occurred.
     *
    public static void saveFile(final File file, final StringBuffer text)
            throws IOException {
        saveFile(file, text.toString());
    }


    /** FIXME comment in
     * Saves a <code>String</code> in a file.
     *
     * @param   file        File to save the data in.
     * @param   text        Data to save in the file.
     * @throws  IOException File exception occurred.
     *
    public static void saveFile(final File file, final String text)
            throws IOException {
        BufferedWriter out = new BufferedWriter(
            new FileWriter(file));
        out.write(text);
        out.close();
    }
*/
    /**
     * Saves a <code>String</code> in a file.
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
        out.write(text);
        out.close();
    }

    /**
     * Saves a <code>String</code> in a file.
     *
     * @param   file        File to save the data in.
     * @param   data        Data to save in the file.
     * @throws  IOException File exception occurred.
     */
    public static void saveFileBinary(final File file, final byte[] data)
            throws IOException {
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        out.write(data);
        out.close();
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
        FileInputStream in = new FileInputStream(from);
        FileOutputStream out = new FileOutputStream(to);

        byte[] data = new byte[8 * 1024];
        int length;

        while ((length = in.read(data)) != -1) {
            out.write(data, 0, length);
        }
        in.close();
        out.close();
    }

    /**
     * Compare two files.
     *
     * @param   from    Compare source.
     * @param   with    Compare with this file.
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
            closeStream(one);
            closeStream(two);
        }
    }

    /**
     * Delete file directory recursive.
     *
     * @param   directory   Directory to delete.
     * @param   deleteDir   Delete directory itself too?
     * @return  Was deletion successful?
     */
    public static boolean deleteDir(final File directory, final boolean deleteDir) {
        // to see if this directory is actually a symbolic link to a directory,
        // we want to get its canonical path - that is, we follow the link to
        // the file it's actually linked to
        File candir;
        try {
            candir = directory.getCanonicalFile();
        } catch (IOException e) {
            return false;
        }

        // a symbolic link has a different canonical path than its actual path,
        // unless it's a link to itself
        if (!candir.equals(directory.getAbsoluteFile())) {
            // this file is a symbolic link, and there's no reason for us to
            // follow it, because then we might be deleting something outside of
            // the directory we were told to delete
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
        // again
        if (deleteDir) {
            return directory.delete();
        }
        return success;
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
     * Convert file in URL.
     *
     * @param   file    File.
     * @return  URL.
     */
    public static URL toUrl(final File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) { // should only happen if there is a bug in the JDK
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates necessary parent directories for a file.
     *
     * @param   file    File.
     */
    public static void createNecessaryDirectories(final File file) {
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
    }

    /**
     * Create relative address from <code>orgin</code> to <code>next</code>.
     *
     * @param   orgin   this is the original location
     * @param   next    this should be the next location
     * @return  relative (or if necessary absolute) file path
     */
    public static final String createRelativePath(final File orgin, final File next) {
        try {
            if (orgin.equals(next)) {
                return "";
            }
            try {
                String org = orgin.getCanonicalPath().replace('\\', '/');
                if (!org.endsWith("/")) {
                    org += ('/');
                }
                String nex = next.getCanonicalPath().replace('\\', '/');
                if (!nex.endsWith("/")) {
                    nex += ('/');
                }
                if (org.equals(nex)) {
                    return "";
                }
                int i = -1; // position of next '/'
                int j = 0;  // position of last '/'
                while (0 <= (i = org.indexOf("/", j))) {
                    if (i >= 0 && nex.length() > i
                            && org.substring(j, i).equals(
                            nex.substring(j, i))) {
                        j = i + 1;
                    } else {
                        break;
                    }
                }
                if (j > 0) {
                    i = j;
                    final StringBuffer result = new StringBuffer(nex.length());
                    while (0 <= (i = org.indexOf("/", i))) {
                        i++;
                        result.append("../");
                    }
                    result.append(nex.substring(j, nex.length() - 1));
                    return result.toString();
                }
                return nex.substring(0, nex.length() - 1);
            } catch (RuntimeException e) {
                return next.toString();
            }
        } catch (IOException e) {
            return new File(orgin, next.getPath()).getPath();
        }
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
    public static void closeStream(final InputStream in) {
        if (in != null) {
            try {
                in.close();
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
    public static void closeReader(final Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    /**
     * Get start directory for application. If this is no Java Webstart version
     * the result is <code>new File(".")</code>.
     *
     * @param   application Application name, used for Java Webstart version. Should
     *          be written in lowercase letters. A "." is automatically appended at
     *          the beginning.
     * @return  Start directory for application.
     */
    public static final File getStartDirectory(final String application) {
        final File startDirectory;
        if (isWebStarted()) {
            final String userHomeWS = System.getProperties().get("jnlpx.deployment.user.home")
                != null ? (String) System.getProperties().get("jnlpx.deployment.user.home")
                : "";
            final String userHome = System.getProperties().get("user.home") != null
                ? (String) System.getProperties().get("user.home") : "";
            startDirectory = new File(
                new File((userHomeWS.length() != 0 ? userHomeWS : userHome)), "." + application);
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
     * @param   url     URL to load properties from.
     * @return  Loaded properties.
     * @throws  MalformedURLException   Invalid URL.
     * @throws  IOException             Reading error.
     */
    public static Properties loadProperties(final URL url)
           throws IOException {
        Properties newprops = new Properties();
        InputStream in = url.openStream();
        newprops.load(in);
        in.close();
        return newprops;
    }

}
