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

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;


/**
 * A set of version numbers.
 *
 * @author  Michael Meyling
 */
public final class VersionSet {

    /** Set that contains our versions. */
    private final Set set;

    /**
     * Constructs version set object. You must give a version string in a form like
     *  <em>a</em>.<em>b</em>.<em>c</em> where a, b and c are non negative integers.
     * These numbers are called <em>Major</em> <em>Minor</em> and <em>Patch</em>.
     *
     * @param   version     Version string.
     * @throws  IllegalArgumentException    Version string has wrong format.
     * @throws  NullPointerException        No null pointer as argument accepted.
     */
    public VersionSet(final String version) {
        set = new TreeSet();
        add(version);
    }

    /**
     * Constructs version set object.
     */
    public VersionSet() {
        set = new TreeSet();
    }

    /**
     * Does the set contain given version number.
     *
     * @param   version Version string. If it has not the correct form, false is returned.
     * @return  Is given version in set?
     */
    public boolean contains(final String version) {
        Version v = null;
        try {
            v = new Version(version);
        } catch (RuntimeException e) {
            return false;
        }
        return set.contains(v);
    }

    /**
     * Does the set contain given version number.
     *
     * @param   version Version to check for.
     * @return  Is given version in set?
     */
    public boolean contains(final Version version) {
        return set.contains(version);
    }

    /**
     * Add version number.
     *
     * @param   version     Version string.
     * @throws  IllegalArgumentException    Version string has wrong format.
     * @throws  NullPointerException        No null pointer as argument accepted.
     */
    public void add(final String version) {
        add(new Version(version));
    }

    /**
     * Add version number.
     *
     * @param   version     Version string.
     */
    public void add(final Version version) {
        set.add(version);
    }

    /**
     * Add contents of another version set.
     *
     * @param   versions    Other version set.
     * @return  Did current set change?
     */
    public boolean addAll(final VersionSet versions) {
        return set.addAll(versions.set);
    }

    /**
     * Is this set empty?
     *
     * @return  Is set empty?
     */
    public boolean isEmpty() {
        return set.isEmpty();
    }

    /**
     * Removes all of the elements from this set.
     */
    public void clear() {
        set.clear();
    }

    /**
     * Returns an iterator over the elements in this set.  The elements are
     * returned in ascending order.
     *
     * @return  Iterator.
     */
    public Iterator iterator() {
        return set.iterator();
    }

    public int hashCode() {
        return set.hashCode();
    }

    public boolean equals(final Object o) {
        if (!(o instanceof VersionSet)) {
            return false;
        }
        return set.equals(((VersionSet) o).set);
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer(30);
        buffer.append("{");
        if (set != null) {
            Iterator e = set.iterator();
            boolean notFirst = false;
            while (e.hasNext()) {
                if (notFirst) {
                    buffer.append(", ");
                } else {
                    notFirst = true;
                }
                buffer.append(String.valueOf(e.next()));
            }
        }
        buffer.append("}");
        return buffer.toString();
    }

}
