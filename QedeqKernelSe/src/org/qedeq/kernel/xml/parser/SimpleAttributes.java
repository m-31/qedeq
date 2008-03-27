/* $Id: SimpleAttributes.java,v 1.16 2008/03/27 05:16:29 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2008,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.xml.parser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * Value object that contains unsorted key value pairs. Both arguments are string type, but there
 * are access methods for different data types. If the type conversion in not possible an
 * appropriate {@link RuntimeException} is thrown.
 * <p>
 * With {@link #add} another key value pair is added. An {@link IllegalArgumentException} is thrown,
 * if the key is already known.
 *
 * @version $Revision: 1.16 $
 * @author    Michael Meyling
 */
public class SimpleAttributes {

    /** Key value storage. */
    private Map map = new HashMap();

    /**
     * Adds a key value pair. This key must be still unknown.
     *
     * @param   key     Key.
     * @param   value   Value, maybe <code>null</code>.
     */
    public final void add(final String key, final String value) {
        if (map.containsKey(key)) {
            throw new IllegalArgumentException(
                "Key " + key + " already known with value: " + map.get(key));
        }
        map.put(key, value);
    }

    /**
     * Returns the value for a key. If the key dosn't exist <code>null</code> is returned.
     *
     * @param   key     Key.
     * @return  Associated value.
     */
    public final String getString(final String key) {
        return (String) map.get(key);
    }

    /**
     * Returns the value for a key as an Integer. If the key dosn't exist
     * <code>null</code> is returned.
     * If the value must be transformable into an Integer value.
     *
     * @param   key     Key.
     * @return  Associated value converted into an Integer.
     */
    public final Integer getInteger(final String key) {
        String value = (String) map.get(key);
        if (value != null) {
            value = value.trim();
        }
        if (value == null || value.length() == 0) {
            return null;
        }
        return new Integer(value);
    }

    /**
     * Returns the value for a key as an Boolean. If the key dosn't exist
     * <code>null</code> is returned.
     * If the value must be transformable into an Boolean value.
     *
     * @param   key     Key.
     * @return  Associated value converted into an Boolean.
     */
    public final Boolean getBoolean(final String key) {
        String value = ((String) map.get(key));
        if (value != null) {
            value = value.trim();
        }
        if (value == null || value.length() == 0) {
            return null;
        }
        return Boolean.valueOf(value);
    }

    /**
     * Returns the value for a key as an Date. If the key dosn't exist
     * <code>null</code> is returned.
     * If the value must be transformable into an Date value.
     * The expected date format is "yyyy-MM-dd'T'HH:mm:ss".
     *
     * @param   key     Key.
     * @return  Associated value converted into an Date.
     */
    public final Date getDate(final String key) {
        String value = (String) map.get(key);
        if (value != null) {
            value = value.trim();
        }
        if (value == null || value.length() == 0) {
            return null;
        }
        try {
            DateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = formater.parse(value);
            return date;
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.toString());
        }
    }

    /**
     * Get the attribute values, sorted by their keys.
     *
     * @return  Key sorted string values.
     */
    public final String[] getKeySortedStringValues() {
        SortedMap sorted = new TreeMap(map);
        return (String[]) sorted.values().toArray(new String[0]);
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        final Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            buffer.append(entry.getKey() + "=\"" + entry.getValue() + "\" ");
        }
        return buffer.toString();
    }

}
