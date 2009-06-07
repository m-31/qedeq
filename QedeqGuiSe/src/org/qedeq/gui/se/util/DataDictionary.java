/* $Id: DataDictionary.java,v 1.5 2008/07/26 07:57:45 m31 Exp $
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

package org.qedeq.gui.se.util;

import java.io.IOException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.qedeq.base.trace.Trace;


/**
 * This class reads entries from property files and
 * gives typed get and set methods.
 *
 * @version $Revision: 1.5 $
 * @author  Michael Meyling
 */
public final class DataDictionary {

    /** This class. */
    private static final Class CLASS = DataDictionary.class;

    /** Resource access. */
    private final ResourceBundle bundle;

    /** The one and only instance. */
    private static DataDictionary instance = null;

    /**
     * Get instance of config access. Method {@link #setup} must have been called before.
     *
     * @return  singleton, which is responsible the config access
     * @throws  IllegalStateException   if {@link #setup} wasn't called before this method call
     */
    public static DataDictionary getInstance() {
        if (instance == null) {
            throw new IllegalStateException(DataDictionary.class.getName()
                + " not initialized, call init before!");
        }
        return instance;
    }


    /**
     * Set resource name for this context. Must be called at first. Couldn't be called again.
     *
     * @param   baseName          name of resource file
     * @throws  IllegalStateException   {@link #setup} was called once before
     */
    public static void init(final String baseName) throws IOException {
        if (instance != null) {
            throw new IllegalStateException(DataDictionary.class.getName()
                + " is already initialized!");
        }
        synchronized (DataDictionary.class) {
            if (instance == null) {
                instance = new DataDictionary("org.qedeq.gui.se.util");
            } else {
                throw new IllegalStateException(DataDictionary.class.getName()
                    + " is already initialized!");
            }
        }
    }


    /**
     * Don't use me outside of this class.
     */
    private DataDictionary(final String baseName) {
        bundle = ResourceBundle.getBundle(baseName);
    }


    /**
     * This method returns a string from the resource bundle.
     */
    public String getString(final String key) {
        String value = null;
        try {
            value = getResourceBundle().getString(key);
        } catch (MissingResourceException e) {
            Trace.fatal(CLASS, this, "getString", "Couldn't find value for: "
                    + key, e);
        }
        if (value == null) {
            value = "Could not find resource: " + key + "  ";
        }
        return value;
    }


    /**
     * Returns the resource bundle associated with this application.
     *
     * @return  resource bundle
     */
    public final ResourceBundle getResourceBundle() {
        return this.bundle;
    }


    /**
     * Returns a mnemonic from the resource bundle. Typically used as
     * keyboard shortcuts in menu items.
     */
    public final char getMnemonic(final String key) {
        return (getString(key)).charAt(0);
    }




}
