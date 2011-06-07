/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * Provides convenience methods for getting parameters out of a {@link Map}.
 *
 * @author  Michael Meyling
 */
public class Parameters {

    /** In this map our values are stored. */
    private final Map map;

    /**
     * Constructs parameter access object.
     *
     * @param   map     Herein are the parameters.
     */
    public Parameters(final Map map) {
        this.map = map;
    }

    public Parameters() {
        this.map = new HashMap();
    }

    /**
     * Searches for the value with the specified key.
     * If the key has no String value an empty String is returned.
     *
     * @param   key   The key we want a value for.
     * @return  The value for the specified key value.
     */
    public String getString(final String key) {
        Object oval = map.get(key);
        return (oval instanceof String) ? (String) oval : "";
    }


    /**
     * Searches for the value with the specified key.
     * If the key has no String value <code>def</code> is returned.
     *
     * @param   key   The key we want a value for.
     * @param   def   The default value we get if we have no String value.
     * @return  The value for the specified key value.
     */
    public String getString(final String key, final String def) {
        Object oval = map.get(key);
        return (oval instanceof String) ? (String) oval : def;
    }


    /**
     * Searches for the value with the specified key.
     * If the key has no int value 0 is returned.
     *
     * @param   key   The key we want a value for.
     * @return  The value for the specified key value.
     */
    public int getInt(final String key) {
        final Object oval = map.get(key);
        if (oval instanceof String) {
            try {
                return Integer.parseInt(oval.toString().trim());
            } catch (NumberFormatException ex) {
                // ignore
            }
        }
        return 0;
    }


    /**
     * Searches for the value with the specified key.
     * If the key has no int value <code>def</code> is returned.
     *
     * @param   key   The key we want a value for.
     * @param   def   The default value we get if we have no String value.
     * @return  The value for the specified key value.
     */
    public int getInt(final String key, final int def) {
        final Object oval = map.get(key);
        if (oval instanceof String) {
            try {
                return Integer.parseInt(oval.toString().trim());
            } catch (NumberFormatException ex) {
                // ignore
            }
        }
        return def;
    }

    /**
     * Searches for the value with the specified key.
     * If the key has no boolean value <code>false</code> is returned.
     *
     * @param   key   The key we want a value for.
     * @return  The value for the specified key value.
     */
    public boolean getBoolean(final String key) {
        final Object oval = map.get(key);
        if (oval instanceof String) {
            return "true".equalsIgnoreCase(oval.toString());
        }
        return false;
    }

    /**
     * Searches for the value with the specified key.
     * If the key has no boolean value <code>def</code> is returned.
     *
     * @param   key   The key we want a value for.
     * @param   def   The default value we get if we have no String value.
     * @return  The value for the specified key value.
     */
    public boolean getBoolean(final String key, final boolean def) {
        final Object oval = map.get(key);
        if (oval instanceof String) {
            if ("true".equalsIgnoreCase((String) oval)) {
                return true;
            }
            if ("false".equalsIgnoreCase((String) oval)) {
                return false;
            }
        }
        return def;
    }

    /**
     * Get all parameters as a long string.
     *
     * @return  String in form "a=b, c=d" and so on.
     */
    public String getParameterString() {
        final StringBuffer buffer = new StringBuffer(30);
        if (map != null) {
            Iterator e = map.entrySet().iterator();
            boolean notFirst = false;
            while (e.hasNext()) {
                final Map.Entry entry = (Map.Entry) e.next();
                String key = String.valueOf(entry.getKey());
                if (notFirst) {
                    buffer.append(", ");
                } else {
                    notFirst = true;
                }
                buffer.append(key);
                buffer.append("=");
                buffer.append(String.valueOf(entry.getValue()));
            }
        }
        return buffer.toString();
    }

    /**
     * Set default configuration parameter if the key has still no value.
     *
     * @param   key         Key we want to check.
     * @param   value       Default value.
     */
    public void setDefault(final String key, final int value) {
        if (!map.containsKey(key)) {
            map.put(key, "" + value);
        }
    }

    /**
     * Set default configuration parameter if the key has still no value.
     *
     * @param   key         Key we want to check.
     * @param   value       Default value.
     */
    public void setDefault(final String key, final String value) {
        if (!map.containsKey(key)) {
            map.put(key, value);
        }
    }

    /**
     * Set default configuration parameter if the key has still no value.
     *
     * @param   key         Key we want to check.
     * @param   value       Default value.
     */
    public void setDefault(final String key, final boolean value) {
        if (!map.containsKey(key)) {
            map.put(key, "" + value);
        }
    }

    public Set keySet() {
        return map.keySet();
    }
}
