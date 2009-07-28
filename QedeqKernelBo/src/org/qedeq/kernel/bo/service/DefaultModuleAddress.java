/* $Id: DefaultModuleAddress.java,v 1.1 2008/07/26 07:58:29 m31 Exp $
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

package org.qedeq.kernel.bo.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.base.module.LocationList;
import org.qedeq.kernel.base.module.Specification;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.ModuleContext;


/**
 * An object of this class represents an address for a QEDEQ module.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class DefaultModuleAddress implements ModuleAddress {

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
        this(u.toExternalForm(), (DefaultModuleAddress) null);
    }

    /**
     * Constructor.
     *
     * @param   file    File path of module. Must address a file
     *                  with extension ".xml".
     * @throws  MalformedURLException   Address is formally incorrect.
     */
    public DefaultModuleAddress(final File file) throws MalformedURLException {
// FIXME 20080804: now less bugs?        this(IoUtility.toUrl(file));
        this(file.toURL());
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
                urmel = new URL(new URL(parent.getUrl()), address);
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
        Trace.trace(CLASS, this, method, "protocol=" + urmel.getProtocol());
        url = urmel.toString();
        fileAddress = urmel.getProtocol().equalsIgnoreCase("file");
/*
        Trace.trace(this, METHOD, "url.getFile=" + this.url.getFile());
        Trace.trace(this, METHOD, "url.getPath=" + this.url.getPath());
        try {
            Trace.trace(this, METHOD, "URI File=" +
                new File(new URI(this.address)).getAbsoluteFile());
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
*/
        final String p = urmel.getFile();
        final int position = p.lastIndexOf("/");
        if (position >= 0 && position + 1 < p.length()) {
            this.path = p.substring(0, position) + "/";
            this.fileName = p.substring(position + 1);
        } else {
            this.path = "";
            this.fileName = p;
        }
        Trace.trace(CLASS, this, method, "path=" + this.path);
        Trace.trace(CLASS, this, method, "fileName=" + this.fileName);
        this.relativeAddress = !this.path.startsWith("/");
        if (!this.fileName.endsWith(".xml")) {
            throw new MalformedURLException("file name doesn't end with \".xml\": "
                + this.fileName);
        }
        final int positionBefore = this.fileName.lastIndexOf(".");
        final String mname = this.fileName.substring(0, positionBefore);
        this.name = mname;
        final int positionPath
            = url.toString().lastIndexOf(this.path + this.fileName);
        if (positionPath < 0) {
            throw new IllegalArgumentException(
                "couldn't determine begin of file path: "
                + url.toString());
        }
        this.header = url.toString().substring(0, positionPath);
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
        return url.toString();
    }

    public final int hashCode() {
        return url.hashCode();
    }

    public final boolean equals(final Object object) {
        if (object == null || !(object instanceof DefaultModuleAddress)) {
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
        final ModuleAddress[] result
            = new ModuleAddress[locations.size()];
        for (int i = 0; i < locations.size(); i++) {
            String file = locations.get(i).getLocation();
            if (file.equals(".")) {
                file = "";
            } else if (!file.endsWith("/")) {
                file += "/";
            }
            file += fileNameEnd;
            result[i] = new DefaultModuleAddress(file, this);
        }
        return result;
    }

    /**
     * Create relative address from <code>orgin</code> to <code>next</code>.
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
        try {
            final URL urlOrgin = new URL(origin);
            final URL urlNext = new URL(next);

            if (urlOrgin.getProtocol().equals(urlNext.getProtocol())
                    && urlOrgin.getHost().equals(urlNext.getHost())
                    && urlOrgin.getPort() == urlNext.getPort()) {
                final String org = urlOrgin.getFile();
                final String nex = urlNext.getFile();
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
                    StringBuffer result = new StringBuffer(nex.length());
                    while (0 <= (i = org.indexOf("/", i))) {
                        i++;
                        result.append("../");
                    }
                    result.append(nex.substring(j));
                    return result.toString();
                } else {
                    return "/" + nex;
                }
            } else {    // no relative address possible
                return urlNext.toString();
            }
        } catch (MalformedURLException e) {
            return next;
        }

    }


}

