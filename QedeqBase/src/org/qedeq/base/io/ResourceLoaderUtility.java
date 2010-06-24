/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2010,  Michael Meyling <mime@qedeq.org>.
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

import org.qedeq.base.trace.Trace;

/**
 * Utility methods for accessing classes and resources using an appropriate class loader.
 * Adapted from org.apache.myfaces.trinidad.util.ClassLoaderUtils.
 *
 * @author  Michael Meyling
 */
public final class ResourceLoaderUtility {

    /** This class. */
    private static final Class CLASS = ResourceLoaderUtility.class;

    /**
     * Constructor, should never be called.
     */
    private ResourceLoaderUtility() {
        // don't call me
    }

    /**
     * Loads the class with the specified name. For Java 2 callers, the current thread's context
     * class loader is preferred, falling back on the system class loader of the caller when the
     * current thread's context is not set, or the caller is pre Java 2.
     *
     * @param   name    Name of class to load.
     * @return  The resulting <code>Class</code> object
     * @exception ClassNotFoundException    Class was not found.
     */
    public static Class loadClass(final String name) throws ClassNotFoundException {
        return loadClass(name, null);
    }

    /**
     * Locates the resource with the specified name. For Java 2 callers, the current thread's
     * context class loader is preferred, falling back on the system class loader of the caller when
     * the current thread's context is not set, or the caller is pre Java 2.
     *
     * @param   name  Resource name.
     * @return  Resulting <code>URL</code> object. Maybe <code>null</code>.
     */
    public static URL getResourceUrl(final String name) {
        return getResourceUrl(name, ResourceLoaderUtility.class.getClassLoader());
    }

    /**
     * Locates the stream resource with the specified name. For Java 2 callers, the current thread's
     * context class loader is preferred, falling back on the system class loader of the caller when
     * the current thread's context is not set, or the caller is pre Java 2.
     *
     * @param name the name of the resource
     * @return the resulting <code>InputStream</code> object
     */
    public static InputStream getResourceAsStream(final String name) {
        return getResourceAsStream(name, null);
    }

    /**
     * Loads the class with the specified name. For Java 2 callers, the current thread's context
     * class loader is preferred, falling back on the class loader of the caller when the current
     * thread's context is not set, or the caller is pre Java 2. If the callerClassLoader is null,
     * then fall back on the system class loader.
     *
     * @param name the name of the class
     * @param callerClassLoader the calling class loader context
     * @return the resulting <code>Class</code> object
     * @exception ClassNotFoundException if the class was not found
     */
    public static Class loadClass(final String name, final ClassLoader callerClassLoader)
            throws ClassNotFoundException {
        Class clazz = null;

        try {
            final ClassLoader loader = getContextClassLoader();

            if (loader != null) {
                clazz = loader.loadClass(name);
            }
        } catch (ClassNotFoundException e) {
            // treat as though loader not set
        }

        if (clazz == null) {
            if (callerClassLoader != null) {
                clazz = callerClassLoader.loadClass(name);
            } else {
                clazz = Class.forName(name);
            }
        }

        return clazz;
    }

    /**
     * Locates the resource with the specified name. For Java 2 callers, the current thread's
     * context class loader is preferred, falling back on the class loader of the caller when the
     * current thread's context is not set, or the caller is pre Java 2. If the callerClassLoader is
     * null, then fall back on the system class loader.
     *
     * @param name the name of the resource
     * @param callerClassLoader the calling class loader context
     * @return the resulting <code>URL</code> object
     */
    public static URL getResourceUrl(final String name, final ClassLoader callerClassLoader) {
        checkResourceName(name);

        URL url = null;

        final ClassLoader loader = getContextClassLoader();

        if (loader != null) {
            url = loader.getResource(name);
        }

        if (url == null) {
            // no success, now we try the given class loader
            if (callerClassLoader != null) {
                url = callerClassLoader.getResource(name);
            } else {
                // last try: get resource via classpath
                url = ClassLoader.getSystemResource(name);
            }
        }
        return url;
    }

    /**
     * Locates the resource stream with the specified name. For Java 2 callers, the current thread's
     * context class loader is preferred, falling back on the class loader of the caller when the
     * current thread's context is not set, or the caller is pre Java 2. If the callerClassLoader is
     * null, then fall back on the system class loader.
     *
     * @param name the name of the resource
     * @param callerClassLoader the calling class loader context
     * @return the resulting <code>InputStream</code> object
     */
    public static InputStream getResourceAsStream(final String name,
            final ClassLoader callerClassLoader) {
        checkResourceName(name);

        InputStream stream = null;

        final ClassLoader loader = getContextClassLoader();

        if (loader != null) {
            stream = loader.getResourceAsStream(name);
        }
        if (stream == null) {
            if (callerClassLoader != null) {
                stream = callerClassLoader.getResourceAsStream(name);
            } else {
                stream = ClassLoader.getSystemResourceAsStream(name);
            }
        }

        return stream;
    }

    /**
     * Dynamically accesses the current context class loader. Returns <code>null</code> if there is
     * no per-thread context class loader. Also if the JRE is below 1.2 or something else went wrong
     * the method returns <code>null</code>.
     *
     * @return ClassLoader.
     */
    public static ClassLoader getContextClassLoader() {
        try {
            final Method method = Thread.class.getMethod("getContextClassLoader", null);
            return (ClassLoader) method.invoke(Thread.currentThread(), null);
        } catch (RuntimeException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static void checkResourceName(final String name) {
        if ((name != null) && name.startsWith("/")) {
            Trace.info(CLASS, "ClassLoaderUtility", "checkResourceName",
                "resource name not portable: " + name);

        }
    }

    /**
     * Get resource file. The resource is located within the file system if it exists already.
     * If not it is loaded as resource and then saved as a file.
     *
     * @param   startDirectory          Start looking from this directory.
     * @param   resourceDirectoryName   Within this directory
     *                                  (relative to <code>startDirectory</code>).
     * @param   resourceName            Look for this resource file.
     * @return  Resource file.
     */
    public static File getResourceFile(final File startDirectory,
            final String resourceDirectoryName, final String resourceName) {
        final File resourceDir = new File(startDirectory, resourceDirectoryName);
        final File resource = new File(resourceDir, resourceName);
        if (!resource.exists()) {
            final URL url = getResourceUrl(resourceDirectoryName + "/" + resourceName);
            if (url == null) {
                Trace.info(ResourceLoaderUtility.class, "getResourceUrlAndMakeLocalCopy",
                    "URL not found for: " + resourceDirectoryName + "/" + resourceName);
                return null;
            }
            try {
                if (!resourceDir.exists()) {
                    if (!resourceDir.mkdirs()) {
                        Trace.info(ResourceLoaderUtility.class, "getResourceUrlAndMakeLocalCopy",
                            "creation failed: " + resourceDir);
                    }
                }
                IoUtility.saveFile(url, resource);
            } catch (IOException e) {
                Trace.fatal(ResourceLoaderUtility.class, "getResourceUrlAndMakeLocalCopy",
                    "resource can not be saved", e);
            }
        }
        return resource;
    }

}
