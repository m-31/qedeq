/* $Id: DefaultModuleAddress.java,v 1.5 2007/12/21 23:33:46 m31 Exp $
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

package org.qedeq.kernel.bo.load;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.qedeq.kernel.base.module.LocationList;
import org.qedeq.kernel.base.module.Specification;
import org.qedeq.kernel.bo.module.ModuleAddress;
import org.qedeq.kernel.bo.module.ModuleContext;
import org.qedeq.kernel.bo.module.QedeqBo;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.utility.IoUtility;


/**
 * An object of this class represents an address for a QEDEQ module.
 *
 * @version $Revision: 1.5 $
 * @author  Michael Meyling
 */
public class DefaultModuleAddress implements ModuleAddress {

    /** URL form of this address. */
    private final URL url;

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
     * @param   u       Address of module.
     * @throws  IOException if address is formally incorrect
     */
    public DefaultModuleAddress(final String u)
            throws IOException {

        this(u, (DefaultModuleAddress) null);
    }

    /**
     * Constructor.
     *
     * @param   u       Address of module.
     * @throws  IOException if address is formally incorrect
     */
    public DefaultModuleAddress(final URL u) throws IOException {
        this(u.toExternalForm(), (DefaultModuleAddress) null);
    }

    /**
     * Constructor.
     *
     * @param   file    File path of module.
     * @throws  IOException     Address is formally incorrect
     */
    public DefaultModuleAddress(final File file)
            throws IOException {
        this(IoUtility.toUrl(file));
    }

    /**
     * Constructor.
     *
     * @param   address  Address of module.
     * @param   parent   Address of parent module.
     * @throws  MalformedURLException     Address is formally incorrect.
     */
    public DefaultModuleAddress(final String address, final DefaultModuleAddress parent)
            throws MalformedURLException {
        final String method = "ModuleAddress(String, ModuleAddress)";
        if (address == null) {
            throw new NullPointerException();
        }
        URL urmel;
        try {
            if (parent != null) {
                urmel = new URL(parent.url, address);
            } else {
                urmel = new URL(address);
            }
        } catch (MalformedURLException e) {
            Trace.trace(this, method, "address=" + address);
            Trace.trace(this, method, "parent=" + parent);
            Trace.trace(this, method, e);
            try {
                final String newAddress = "file:" + address;
                if (parent != null) {
                    urmel = new URL(parent.url, newAddress);
                } else {
                    urmel = new URL(newAddress);
                }
            } catch (MalformedURLException ex) {
                throw e;    // throw original exception
            }
        }
        Trace.trace(this, method, "protocol=" + urmel.getProtocol());
        url = urmel;
        fileAddress = url.getProtocol().equalsIgnoreCase("file");
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
        Trace.trace(this, method, "path=" + this.path);
        Trace.trace(this, method, "fileName=" + this.fileName);
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
    public final ModuleContext createModuleContext() {
        return new ModuleContext(this);
    }

    /**
     * Get address header (including protocol, host, port, user)
     * but without file path.
     *
     * @return address header
     */
    public final String getHeader() {
        return header;
    }

    /**
     * Get address path (without protocol, host, port and file name).
     *
     * @return module path
     */
    public final String getPath() {
        return path;
    }

    /**
     * Get module file name.
     *
     * @return  Module file name.
     */
    public final String getFileName() {
        return fileName;
    }

    /**
     * Get name of module (file name without <code>.xml</code>).
     *
     * @return  Module name.
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Get fully qualified URL of module.
     *
     * @return  URL for QEDEQ module.
     */
    public final URL getURL() {
        return this.url;
    }

    /**
     * Was this module address created relatively?
     *
     * @return  Relatively created?
     */
    public final boolean isRelativeAddress() {
        return this.relativeAddress;
    }

    /**
     * Is this a local QEDEQ file. That means the address starts with <code>file:</code>.
     *
     * @return  Is the QEDEQ module a local file?
     */
    public final boolean isFileAddress() {
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
     * TODO mime 20070326: is this function really necessary?
     *
     * @param   spec    here are the (perhaps relative) addresses to
     *                  another module
     * @return  file name of specified module
     */
    public static final String getModuleFileName(final Specification spec) {

        return spec.getName() + ".xml";
    }

    /**
     * Get all potential module addresses from a module specification.
     *
     * TODO mime 20070326: add context information (for error case)
     *
     * @param   module  Starting from that module (has an absolute
     *                  address).
     * @param   spec    Here are the (perhaps relative) addresses to
     *                  another module.
     * @return  Array of absolute address strings.
     * @throws  IOException One address is not correctly formed.
     */
    public static final ModuleAddress[] getModulePaths(final QedeqBo module,
            final Specification spec) throws IOException {

        final String fileNameEnd = getModuleFileName(spec);
        final LocationList locations = spec.getLocationList();
        final ModuleAddress[] result
            = new ModuleAddress[locations.size()];
        for (int i = 0; i < locations.size(); i++) {
            String fileName
                = locations.get(i).getLocation();
            if (fileName.equals(".")) {
                fileName = "";
            } else if (!fileName.endsWith("/")) {
                fileName += "/";
            }
            fileName += fileNameEnd;
            result[i] = new DefaultModuleAddress(fileName,
                (DefaultModuleAddress) module.getModuleAddress());
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

    /**
     * Get module address with new ending. E.g.: ".html" instead of ".qedeq".
     *
     * @param   address     The address of something (e.g.: a module).
     * @param   newEnding   This should be the new ending (e.g.: "html").
     * @return  module address with substituted ending
     */
    public static final String newEnding(final String address,
            final String newEnding) {
        if (address.length() == 0) {
            return "";
        }
        final int i = address.lastIndexOf(".");
        if (i > 0) {
            return address.substring(0, i + 1) + newEnding;
        } else {
            return address + "." + newEnding;
        }
    }

}

