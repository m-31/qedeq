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

package org.qedeq.base.utility;

import org.qedeq.base.io.TextInput;


/**
 * A version number implementation oriented at the standard:
 * <a href="http://semver.org/">http://semver.org</a>.
 *
 * @author  Michael Meyling
 */
public final class Version implements Comparable {

    /** Major version number. */
    private final int major;

    /** Minor version number. */
    private final int minor;

    /** Patch version number. */
    private final int patch;

    /**
     * Constructs version object. You must give a version string in a form like
     *  <em>a</em>.<em>b</em>.<em>c</em> where a, b and c are non negative integers.
     * These numbers are called <em>Major</em> <em>Minor</em> and <em>Patch</em>.
     *
     * @param   version     Version string.
     * @throws  IllegalArgumentException    Version string has wrong format.
     * @throws  NullPointerException        No null pointer as argument accepted.
     */
    public Version(final String version) {
        final TextInput text = new TextInput(version);
        major = text.readNonNegativeInt();
        if (!".".equals(text.readString(1))) {
            throw new IllegalArgumentException("version number must have two digits");
        }
        minor = text.readNonNegativeInt();
        if (!".".equals(text.readString(1))) {
            throw new IllegalArgumentException("version number must have two digits");
        }
        patch = text.readNonNegativeInt();
        text.skipWhiteSpace();
        if (!text.isEmpty()) {
            throw new IllegalArgumentException("version number to long: " + text.readString(100));
        }
    }

    /**
     * Get major version number.
     *
     * @return  Major version number.
     */
    public int getMajor() {
        return major;
    }

    /**
     * Get minor version number.
     *
     * @return  Minor version number.
     */
    public int getMinor() {
        return minor;
    }

    /**
     * Get patch number.
     *
     * @return  Patch version number.
     */
    public int getPatch() {
        return patch;
    }

    public int compareTo(final Object o) {
        if (!(o instanceof Version)) {
            return -1;
        }
        final Version other = (Version) o;
        if (major < other.major) {
            return -1;
        } else if (major > other.major) {
            return 1;
        }
        if (minor < other.minor) {
            return -1;
        } else if (minor > other.minor) {
            return 1;
        }
        if (patch < other.patch) {
            return -1;
        } else if (patch > other.patch) {
            return 1;
        }
        return 0;
    }

    public int hashCode() {
        return major ^ minor ^ patch;
    }

    public boolean equals(final Object o) {
        return 0 == compareTo(o);
    }

    public String toString() {
        return major + "." + (minor < 10 ? "0" : "") + minor
            + "." + (patch < 10 ? "0" : "") + patch;
    }

    /**
     * Is this version number less than the given other?
     *
     * @param   other   Compare with this number.
     * @return  Less?
     */
    public boolean isLess(final Version other) {
        return -1 == compareTo(other);
    }

    /**
     * Is this version number bigger than the given other?
     *
     * @param   other   Compare with this number.
     * @return  Bigger?
     */
    public boolean isBigger(final Version other) {
        return 1 == compareTo(other);
    }

    /**
     * Is <code>version1</code> &lt; <code>version2</code>?
     *
     * @param   version1    First operand. Must be valid version pattern.
     * @param   version2    Second operand. Must be valid version pattern.
     * @return  Less?
     * @throws  IllegalArgumentException    No valid version pattern.
     * @throws  NullPointerException        No null pointer as argument accepted.
     */
    public static boolean less(final String version1, final String version2) {
        return (new Version(version1)).isLess(new Version(version2));
    }

    /**
     * Is <code>version1</code> &gt; <code>version2</code>?
     *
     * @param   version1    First operand. Must be valid version pattern.
     * @param   version2    Second operand. Must be valid version pattern.
     * @return  Less?
     * @throws  IllegalArgumentException    No valid version pattern.
     * @throws  NullPointerException        No null pointer as argument accepted.
     */
    public static boolean bigger(final String version1, final String version2) {
        return (new Version(version1)).isBigger(new Version(version2));
    }

}
