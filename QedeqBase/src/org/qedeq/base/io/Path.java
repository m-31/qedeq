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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.base.utility.StringUtility;


/**
 * A file path that leads to a directory or file and is absolute or relative.
 * This abstraction of a file location was done to create relative file paths.
 * This class makes some assumptions about a file path:
 * "/" is the directory separator, "/" is also the root directory and ".." specifies
 * the parent directory. "." is the current directory.
 *
 * @author  Michael Meyling
 */
public final class Path {

    /** Path to file with "/" as directory name separator. */
    private final String[] path;

    /** File name. */
    private final String name;

    /**
     * Create file with given path and name.
     *
     * @param   filePath    Path to file with "/" as directory name separator.
     */
    public Path(final String filePath) {
        final String[] p = StringUtility.split(filePath, "/");
        name = p[p.length - 1];
        final String[] p2 = new String[p.length - 1];
        System.arraycopy(p, 0, p2, 0, p2.length);
        path = removeRelativeDirs(p2);
    }

    /**
     * Create file with given path and name. Relative directories are removed from
     * the file path if possible.
     *
     * @param   dirPath    Directory path to file with "/" as directory name separator.
     *                     This value can end with a "/" but it must not.
     * @param   fileName   File name. It should not contain "/" but this is not checked.
     */
    public Path(final String dirPath, final String fileName) {
        this((dirPath.endsWith("/") ? StringUtility.split(dirPath.substring(0,
            dirPath.length() - 1), "/") : StringUtility.split(dirPath, "/")), fileName);
    }

    /**
     * Create file with given path and name. Relative directories are removed
     * from the file path if possible.
     *
     * @param   dirNames    Directory names.
     * @param   fileName   File name. It should not contain "/" but this is not checked.
     */
    public Path(final String[] dirNames, final String fileName) {
        path = removeRelativeDirs(dirNames);
        name = (fileName != null ? fileName : "");
    }

    /**
     * Create new file path relative to given one.
     *
     * @param   filePath    Path to file relative to <code>this</code>.
     * @return  Relative file path (if possible).
     */
    public Path createRelative(final String filePath) {
        final Path to = new Path(filePath);
        if (isRelative()) {
            // if from is relative, then we don't need to think further
            return to;
        }
        if (to.isRelative()) {
            return to;
        }
        // both are absolute so we try to navigate within the file system
        // to get a relative path
        int max = 0;
        while (max < path.length && max < to.path.length) {
            if (!"..".equals(path[max]) && EqualsUtility.equals(path[max], to.path[max])) {
                max++;
            } else {
                break;
            }
        }
        String[] r = new String[path.length - max + to.path.length - max];
        for (int i = max; i < path.length; i++) {
            r[i - max] = "..";
        }
        for (int i = max; i < to.path.length; i++) {
            r[i - max + path.length - max] = to.path[i];
        }
        return new Path(r, to.name);
    }

    /**
     * Create relative address. Assume only file paths.
     *
     * @param   org     This is the original location.
     * @param   nex     This should be the next location.
     * @return  Relative path (if possible).
     */
    public String makeRelative(final String org, final String nex) {
        final String[] orgas = removeRelativeDirs(StringUtility.split(org, "/"));
        final String[] nexas = removeRelativeDirs(StringUtility.split(nex, "/"));
        int max = 0;
        while (max < orgas.length && max < nexas.length) {
            if (!"..".equals(orgas[max]) && EqualsUtility.equals(orgas[max], nexas[max])) {
                max++;
            } else {
                break;
            }
        }
        int d = -1;
        if (orgas.length == 0) {
            d = 0;
        }
        String[] r = new String[orgas.length - max + nexas.length - max + d];
        for (int i = max; i < orgas.length + d; i++) {
            r[i - max] = "..";
        }
        for (int i = max; i < nexas.length; i++) {
            r[i - max + orgas.length + d - max] = nexas[i];
        }
        return toDir(r);
    }

    private String toDir(final String[] dirs) {
        final String[] r = removeRelativeDirs(dirs);
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < r.length; i++) {
            if (i > 0) {
                result.append("/");
            }
            result.append(r[i]);
        }
        return result.toString();
    }


    public boolean isDirectory() {
        return name.length() == 0;
    }

    public boolean isFile() {
        return !isDirectory();
    }

    public boolean isAbsolute() {
        return path.length > 0 && path[0].length() == 0;
    }

    public boolean isRelative() {
        return !isAbsolute();
    }

    public String getFileName() {
        return name;
    }

    public String getDirectory() {
        String result = "";
        for (int i = 0; i < path.length; i++) {
            result += path[i] + "/";
        }
        return result;
    }

    private String[] removeRelativeDirs(final String[] dirNames) {
        List d = new ArrayList();
        for (int i = 0; i < dirNames.length; i++) {
            d.add(dirNames[i]);
        }
        for (int i = 0; i < d.size(); ) {
            if (i > 0 && "..".equals(d.get(i)) && !"".equals(d.get(i - 1))
                    && !"..".equals(d.get(i - 1))) {
                d.remove(i - 1);
                d.remove(i - 1);
            } else if (".".equals(d.get(i))) {
                d.remove(i);
            } else {
                i++;
            }
        }
        return (String[]) d.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public String toString() {
        String result = "";
        for (int i = 0; i < path.length; i++) {
            result += path[i] + "/";
        }
        result += name;
        return result;
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof Path)) {
            return false;
        }
        final Path other = (Path) obj;
        return EqualsUtility.equals(path, other.path) && name.equals(other.name);
    }

    public int hashCode() {
        return toString().hashCode();
    }

}

