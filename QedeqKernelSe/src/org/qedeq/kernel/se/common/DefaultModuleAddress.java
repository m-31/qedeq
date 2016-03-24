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

package org.qedeq.kernel.se.common;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.qedeq.base.io.Path;
import org.qedeq.base.io.UrlUtility;
import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.se.base.module.LocationList;
import org.qedeq.kernel.se.base.module.Specification;


/**
 * An object of this class represents an address for a QEDEQ module.
 *
 * @author  Michael Meyling
 */
public class DefaultModuleAddress implements ModuleAddress {

    /** Default memory module address with identifier "default". */
    public static final DefaultModuleAddress MEMORY = new DefaultModuleAddress();

    /** This class. */
    private static final Class CLASS = DefaultModuleAddress.class;

    /** URL form of this address. */
    private final String url;

    /** Header (including protocol, host, port, user) but without file path. */
    private final String header;

    /** Path (without protocol, host, port and file name). */
    private final String path;

    /** File name of QEDEQ module including <code>.xml</code>. */
    private final String fileName;

    /** Is module address relative? */
    private final boolean relativeAddress;

    /** Is module address a file? */
    private final boolean fileAddress;

    /** Module name. That is file name without <code>.xml</code> */
    private final String name;

    /**
     * Constructor.
     *
     * @param   u       Address of module. Must not be <code>null</code>.
     *                  Must be a URL with protocol "file" or "http" and address a file
     *                  with extension ".xml".
     * @throws  MalformedURLException    Address is formally incorrect.
     */
    public DefaultModuleAddress(final String u) throws MalformedURLException {
        this(u, (DefaultModuleAddress) null);
    }

    /**
     * Constructor.
     *
     * @param   u       Address of module. Must not be <code>null</code>.
     *                  Must be a URL with protocol "file" or "http" and address a file
     *                  with extension ".xml".
     * @throws  MalformedURLException    Address is formally incorrect.
     */
    public DefaultModuleAddress(final URL u) throws MalformedURLException {
        this(u.toExternalForm(), (ModuleAddress) null);
    }

    /**
     * Constructor.
     *
     * @param   file    File path of module. Must address a file
     *                  with extension ".xml".
     * @throws  IOException Problem with file location.
     */
    public DefaultModuleAddress(final File file) throws IOException {
        this(UrlUtility.toUrl(file.getCanonicalFile()));
    }

    /**
     * Default constructor for memory modules.
     */
    public DefaultModuleAddress() {
        this(true, "default");
    }

    /**
     * Constructor mainly used for memory modules.
     * TODO 20110227 m31: this is no object oriented design: a parameter must be true???
     *                    refactor code and create extra memory module address class!
     *
     * @param   memory      Must be true. If not a runtime exception occurs.
     * @param   identifier  Identifies the module in memory. Must not be <code>null</code>.
     */
    public DefaultModuleAddress(final boolean memory, final String identifier) {
        if (!memory) {
            throw new IllegalArgumentException("memory must be true");
        }
        if (identifier == null) {
            throw new NullPointerException();
        }
        url = "memory://" + identifier;
        name = identifier;
        fileAddress = false;
        fileName = identifier;
        header = "memory:";
        path = "";
        relativeAddress = false;
    }

    /**
     * Constructor.
     *
     * @param   address  Address of module. Must not be <code>null</code>.
     *                  Must be a URL with protocol "file" or "http" (if <code>parent</code> is
     *                  <code>null</code>) and address a file
     *                  with extension ".xml".
     * @param   parent   Address of parent module. Can be <code>null</code>.
     * @throws  MalformedURLException     Address is formally incorrect.
     */
    public DefaultModuleAddress(final String address, final ModuleAddress parent)
            throws MalformedURLException {
        final String method = "ModuleAddress(String, ModuleAddress)";
        if (address == null) {
            throw new NullPointerException();
        }
        URL urmel;
        try {
            if (parent != null) {
                urmel = new URL(new URL(StringUtility.replace(parent.getUrl(), "file://", "file:")), address);
            } else {
                urmel = new URL(address);
            }
        } catch (MalformedURLException e) {
            Trace.trace(CLASS, this, method, "address=" + address);
            Trace.trace(CLASS, this, method, "parent=" + parent);
            Trace.trace(CLASS, this, method, e);
            try {
                final String newAddress = "file:" + address;
                if (parent != null) {
                    urmel = new URL(new URL(parent.getUrl()), newAddress);
                } else {
                    urmel = new URL(newAddress);
                }
            } catch (MalformedURLException ex) {
                throw e;    // throw original exception
            }
        }
        final Path p = new Path(urmel.getPath());
        this.path = p.getDirectory();
        this.fileName = p.getFileName();
        Trace.trace(CLASS, this, method, "path=" + this.path);
        Trace.trace(CLASS, this, method, "fileName=" + this.fileName);
        this.relativeAddress = p.isRelative();
        if (!this.fileName.endsWith(".xml")) {
            throw new MalformedURLException("file name doesn't end with \".xml\": "
                + this.fileName);
        }
        Trace.trace(CLASS, this, method, "protocol=" + urmel.getProtocol());
        fileAddress = urmel.getProtocol().equalsIgnoreCase("file");
        String urm = urmel.toString();
        Trace.trace(CLASS, this, method, "replacing " + urmel.getPath() + " by " + p.toString());
        urm = StringUtility.replace(urm, urmel.getPath(), p.toString());
        if (fileAddress) {
            if (urm.startsWith("file:") && !urm.startsWith("file://")) {
                urm = "file://" + urm.substring("file:".length());
            }
        }
        url = urm;
        final int positionBefore = this.fileName.lastIndexOf(".");
        final String mname = this.fileName.substring(0, positionBefore);
        this.name = mname;
        final int positionPath = url.lastIndexOf(p.toString());
        if (positionPath < 0) {
            throw new IllegalArgumentException(
                "couldn't determine begin of file path: "
                + url + "\nsearching for: " + p);
        }
        this.header = url.substring(0, positionPath);
    }

    /**
     * Get module address as {@link ModuleContext}. Creates a new object.
     *
     * @return  Module address as {@link ModuleContext}.
     */
    public ModuleContext createModuleContext() {
        return new ModuleContext(this);
    }

    /**
     * Get address header (including protocol, host, port, user)
     * but without file path.
     *
     * @return address header
     */
    public String getHeader() {
        return header;
    }

    /**
     * Get address path (without protocol, host, port and file name).
     *
     * @return module path
     */
    public String getPath() {
        return path;
    }

    /**
     * Get module file name.
     *
     * @return  Module file name.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Get name of module (file name without <code>.xml</code>).
     *
     * @return  Module name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get fully qualified URL of module.
     *
     * @return  URL for QEDEQ module.
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Was this module address created relatively?
     *
     * @return  Relatively created?
     */
    public boolean isRelativeAddress() {
        return this.relativeAddress;
    }

    /**
     * Is this a local QEDEQ file. That means the address starts with <code>file:</code>.
     *
     * @return  Is the QEDEQ module a local file?
     */
    public boolean isFileAddress() {
        return fileAddress;
    }

    public final String toString() {
        return url;
    }

    public final int hashCode() {
        return url.hashCode();
    }

    public final boolean equals(final Object object) {
        if (!(object instanceof DefaultModuleAddress)) {
            return false;
        }
        return url.equals(((DefaultModuleAddress) object).url);
    }

    /**
     * Get the file name of the specified module.
     *
     * @param   spec    Here are the (perhaps relative) addresses to
     *                  another module.
     * @return  File name of specified module.
     */
    private static final String getModuleFileName(final Specification spec) {

        return spec.getName() + ".xml";
    }

    public final ModuleAddress[] getModulePaths(final Specification spec) throws IOException {

        final String fileNameEnd = getModuleFileName(spec);
        final LocationList locations = spec.getLocationList();
        final List result = new ArrayList();
        for (int i = 0; locations != null && i < locations.size(); i++) {
            if (locations.get(i) == null) {
                continue;
            }
            String file = locations.get(i).getLocation();
            if (file.equals(".")) {
                file = "";
            } else if (!file.endsWith("/")) {
                file += "/";
            }
            file += fileNameEnd;
            result.add(new DefaultModuleAddress(file, this));
        }
        return (ModuleAddress[]) result.toArray(new ModuleAddress[] {});
    }

    /**
     * Create relative address from <code>origin</code> to <code>next</code>.
     * If both addresses point to the same file we return "".
     *
     * @param   origin  This is the original location (URL!).
     * @param   next    This should be the next location (URL!).
     * @return  Relative (or if necessary absolute) address.
     */
    public static final String createRelativeAddress(final String origin,
            final String next) {
        if (origin.equals(next)) {
            return "";
        }
        final URL urlOrgin;
        try {
            urlOrgin = new URL(origin);
        } catch (MalformedURLException e) {
            return createRelative(origin, next);
        }
        try {
            final URL urlNext = new URL(next);
            if (urlOrgin.getProtocol().equals(urlNext.getProtocol())
                    && urlOrgin.getHost().equals(urlNext.getHost())
                    && urlOrgin.getPort() == urlNext.getPort()) {
                final String org = urlOrgin.getFile();
                final String nex = urlNext.getFile();
                return createRelative(org, nex);
            }
            // no relative address possible
            return urlNext.toString();
        } catch (MalformedURLException e) {
            return next;
        }
    }

    /**
     * Create relative address. Assume only file paths.
     *
     * @param   org     This is the original location.
     * @param   nex     This should be the next location.
     * @return  Relative path (if possible).
     */
    public static String createRelative(final String org, final String nex) {
        final Path from = new Path(org);
        return from.createRelative(nex).toString();
    }

}

