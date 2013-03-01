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

package org.qedeq.kernel.se.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.qedeq.base.io.IoUtility;


/**
 * This class reads entries from property files. This class should not
 * be used outside this package.
 *
 * @author  Michael Meyling
 */
final class ConfigAccess {

    /** Config file. */
    private final File configFile;

    /** Collector for properties. */
    private Properties properties = new Properties();

    /** Config file description. */
    private final String description;

    /**
     * Get access for a config file.
     *
     * @param   configFile              Config file.
     * @param   description             Config file description
     * @throws  IOException             Config file couldn't be loaded.
     */
    public ConfigAccess(final File configFile, final String description) throws IOException {
        this.configFile = configFile;
        this.description = description;
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(configFile);
            load(stream);
        } catch (IOException e) {
            System.out.println("no config file found, using default values");
        } finally {
            IoUtility.close(stream);
        }
        setString("configFileLocation", configFile.getCanonicalPath());
    }

    /**
     * Get config file.
     *
     * @return  Config file.
     */
    public final File getConfigFile() {
        return configFile;
    }

    /**
     * Get description for config file.
     *
     * @return  Config file description.
     */
    public final String getConfigDescription() {
        return description;
    }

    /**
     * Get properties.
     *
     * @return  properties.
     */
    private final Properties getProperties() {
        return properties;
    }

    /**
     * Load properties from stream. The properties are
     * added to the previous ones.
     *
     * @param   inStream    load from this stream
     * @throws  IOException loading failed
     */
    private final void load(final InputStream inStream) throws IOException {
        getProperties().load(inStream);
    }

    /**
     * Store properties in config file.
     *
     * @throws  IOException Saving failed.
     */
    public final void store() throws IOException {
        OutputStream out = null;
        try {
            final File file = getConfigFile();
            IoUtility.createNecessaryDirectories(file);
            out = new FileOutputStream(file);
            getProperties().store(out, getConfigDescription());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    throw e;
                } catch (Exception e) {
                    throw new IOException(e.toString());
                }
            }
        }
    }

    /**
     * Return String property.
     *
     * @param   name    Get this property.
     * @return  String for looked property. <code>null</code>, if property is missing.
     */
    public final String getString(final String name) {
        return getProperties().getProperty(name);
    }

    /**
     * Return String property.
     *
     * @param   name            Look for this String property.
     * @param   defaultValue    Return this value if property doesn't exist.
     * @return  Value of property. Equal to default value if parameter doesn't exist.
     */
    public final String getString(final String name, final String defaultValue) {
        final String value = getProperties().getProperty(name);
        if (value == null) {
            setString(name, defaultValue);
            return defaultValue;
        }
        return value;
    }

    /**
     * Set String property.
     *
     * @param name   Set this property.
     * @param value  Set property to this value.
     */
    public final void setString(final String name, final String value) {
        getProperties().setProperty(name, value);
    }

    /**
     * Get list of String properties with certain prefix.
     * Example:
     * <ul>
     * <li>qeq=green</li>
     * <li>module2=tiger</li>
     * <li>module3=tulip</li>
     * <li>modula=green</li>
     * <li>module1=bluebird</li>
     * </ul>
     * If namePrefix == "module" we get:
     * <ul>
     * <li>module1=bluebird</li>
     * <li>module2=tiger</li>
     * <li>module3=tulip</li>
     * </ul>
     * The sequence of resulting properties is sorted by their keys.
     *
     * @param   namePrefix  Prefix of seek property name.
     * @return  List of key sorted string properties (maybe empty).
     */
    public final String[] getStringProperties(final String namePrefix) {
        final List list = new ArrayList();
        final Enumeration keys = getProperties().keys();
        final List keyList = Collections.list(keys);
        Collections.sort(keyList);
        for (int i = 0; i < keyList.size(); i++) {
            final String key = (String) keyList.get(i);
            if (key.startsWith(namePrefix)) {
                list.add(getProperties().get(key));
            }
        }
        return (String []) list.toArray(new String[list.size()]);
    }

    /**
     * Get map of String properties with certain prefix.
     *
     * @param   namePrefix  Prefix of seek property name.
     * @return  Map of properties that have String keys that start with <code>namePrefix</code>.
     *          The prefix is removed of for the resulting map.
     */
    public final Map getProperties(final String namePrefix) {
        final Map result = new HashMap();
        Iterator i = getProperties().entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            final String name = String.valueOf(entry.getKey());
            if (name.startsWith(namePrefix)) {
                result.put(name.substring(namePrefix.length()), entry.getValue());
            }
        }
        return result;
    }

    /**
     * Set int property.
     *
     * @param name   Set this property.
     * @param value  Set property to this value.
     */
    public final void setInteger(final String name, final int value) {
        setString(name, "" + value);
    }


    /**
     * Get int property.
     *
     * @param   name    look for this property
     * @return  property
     * @throws  IllegalArgumentException    Property is no valid int value
     * @throws  NullPointerException        Property doesn't exist
     */
    public final int getInteger(final String name) {
        final String intPropAsString = getProperties().getProperty(name);
        if (intPropAsString == null) {
            throw new NullPointerException("property \"" + name + "\" not found");
        }
        try {
            return Integer.parseInt(intPropAsString);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                "int property " + intPropAsString + " has invalid format");
        }
    }

    /**
     * Return int property.
     *
     * @param   name            Look for this integer property.
     * @param   defaultValue    Return this value if property doesn't exist.
     * @return  int value of property. Equal to default value if parameter doesn't exist.
     * @throws  IllegalArgumentException    Property is no valid int value.
     */
    public final int getInteger(final String name, final int defaultValue) {
        final String intPropAsString = getProperties().getProperty(name);
        if (intPropAsString == null || intPropAsString.length() == 0) {
            setInteger(name, defaultValue);
            return defaultValue;
        }
        try {
            return Integer.parseInt(intPropAsString);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                "Integer-Property " + intPropAsString + " has invalid format");
        }
    }

    /**
     * Remove property.
     *
     * @param   name    Property to delete.
     */
    public final void removeProperty(final String name) {
        getProperties().remove(name);
    }

    /**
     * Remove properties with certain prefix.
     *
     * Example:
     * <ul>
     * <li>module1=bluebird</li>
     * <li>module2=tiger</li>
     * <li>module3=tulip</li>
     * </ul>
     * Calling with value <code>module</code> deletes all.
     *
     * @param   namePrefix  Prefix of seeked property name.
     */
    public final void removeProperties(final String namePrefix) {
        final Enumeration keys = getProperties().keys();
        while (keys.hasMoreElements()) {
            final String key = (String) keys.nextElement();
            if (key.startsWith(namePrefix)) {
                getProperties().remove(key);
            }
        }
    }

}
