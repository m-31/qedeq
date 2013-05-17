package org.qedeq.base.test;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This utility class was based originally on Daniel Le Berre's <code>RTSI</code> class.
 * This class can be called in different modes, but the principal use is to determine what
 * subclasses/implementations of a given class/interface exist in the current
 * runtime environment.
 * @author Daniel Le Berre, Elliott Wade
 * <p>
 * Michael Meyling made some ugly hacks to get some JUnit test checks working.
 * Maybe this class can later be used for finding plugins dynamically.
 */
public class ClassFinder {
    private Class searchClass = null;
    private Map classpathLocations = new HashMap();
    private Map results = new HashMap();
    private Set negativeResults = new TreeSet(CLASS_COMPARATOR);
    private List errors = new ArrayList();
    private boolean working = false;
    private String start;

    public ClassFinder() {
        refreshLocations();
    }

    public boolean isWorking() {
        return working;
    }

    /**
     * Rescan the classpath, cacheing all possible file locations.
     */
    public final void refreshLocations() {
        synchronized (classpathLocations) {
            classpathLocations = getClasspathLocations();
        }
    }

    /**
     * @param fqcn Name of superclass/interface on which to search
     */
    public final Set findSubclasses(final String fqcn, final String start) {
        synchronized (classpathLocations) {
            synchronized (results) {
                try {
                    working = true;
                    searchClass = null;
                    errors = new ArrayList();
                    results = new TreeMap(CLASS_COMPARATOR);
                    negativeResults = new TreeSet(CLASS_COMPARATOR);

                    //
                    // filter malformed FQCN
                    //
                    if (fqcn.startsWith(".") || fqcn.endsWith(".")) {
                        return new TreeSet(CLASS_COMPARATOR);
                    }

                    //
                    // Determine search class from fqcn
                    //
                    try {
                        searchClass = Class.forName(fqcn);
                    } catch (Throwable ex) {
                        // if class not found, let empty vector return...
                        errors.add(ex);
                        return new TreeSet(CLASS_COMPARATOR);
                    }
                    this.start = start;
                    return findSubclasses(searchClass, classpathLocations);
                } finally {
                    working = false;
                }
            }
        }
    }

    public final List getErrors() {
        return new ArrayList(errors);
    }

    /**
     * The result of the last search is cached in this object, along
     * with the URL that corresponds to each class returned. This
     * method may be called to query the cache for the location at
     * which the given class was found. <code>null</code> will be
     * returned if the given class was not found during the last
     * search, or if the result cache has been cleared.
     */
    public final URL getLocationOf(final Class cls) {
        if (results != null) {
            return (URL) results.get(cls);
        } else {
            return null;
        }
    }

    /**
     * The negative result of the last search is cached in this object.
     */
    public final boolean hasNoLocation(final String cls) {
        if (!cls.startsWith(start)) {
            return true;
        }
        return negativeResults != null &&  negativeResults.contains(cls);
    }

    /**
     * Determine every URL location defined by the current classpath, and
     * it's associated package name.
     */
    public final Map getClasspathLocations() {
        Map map = new TreeMap(URL_COMPARATOR);
        File file = null;

        String pathSep = System.getProperty("path.separator");
        String classpath = System.getProperty("java.class.path");
        //System.out.println("classpath=" + classpath);

        StringTokenizer st = new StringTokenizer(classpath, pathSep);
        while (st.hasMoreTokens()) {
            String path = st.nextToken();
            file = new File(path);
            include(null, file, map);
        }

//        Iterator it = map.keySet().iterator();
//        while (it.hasNext()) {
//            URL url = (URL) it.next();
//            System.out.println(url + "-->" + map.get(url));
//        }

        return map;
    }

    private static final FileFilter DIRECTORIES_ONLY = new FileFilter() {
        public boolean accept(final File f) {
            if (f.exists() && f.isDirectory()) {
                return true;
            }
            return false;
        }
    };

    private static final Comparator URL_COMPARATOR = new Comparator() {
        public int compare(final Object u1, final Object u2) {
            return String.valueOf(u1).compareTo(String.valueOf(u2));
        }
    };

    public static final Comparator CLASS_COMPARATOR = new Comparator() {
        public int compare(final Object c1, final Object c2) {
            return String.valueOf(c1).compareTo(String.valueOf(c2));
        }
    };

    private final void include(final String fName, final File file, final Map map) {
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            // could be a JAR file
            includeJar(file, map);
            return;
        }
        String name = fName;
        if (name == null) {
            name = "";
        } else {
            name += ".";
        }
        // add subpackages
        File [] dirs = file.listFiles(DIRECTORIES_ONLY);
        for (int i = 0; i < dirs.length; i++) {
            try {
                // add the present package
                map.put(new URL("file://" + dirs[i].getCanonicalPath()),
                    name + dirs[i].getName());
            } catch (IOException ioe) {
                return;
            }
            include(name + dirs[i].getName(), dirs[i], map);
        }
    }

    private void includeJar(final File file, final Map map) {
        if (file.isDirectory()) {
            return;
        }

        URL jarURL = null;
        JarFile jar = null;
        try {
            String canonicalPath = file.getCanonicalPath();
            if (!canonicalPath.startsWith("/")) {
                canonicalPath = "/" + canonicalPath;
            }
            jarURL = new URL("file:" + canonicalPath);
            jarURL = new URL("jar:" + jarURL.toExternalForm() + "!/");
            JarURLConnection conn = (JarURLConnection) jarURL.openConnection();
            jar = conn.getJarFile();
        } catch (Exception e) {
            // not a JAR or disk I/O error
            // either way, just skip
            return;
        }

        if (jar == null) {
            return;
        }
        // include the jar's "default" package(i.e. jar's root)
        map.put(jarURL, "");

        Enumeration e = jar.entries();
        while (e.hasMoreElements()) {
            JarEntry entry = (JarEntry) e.nextElement();
            if (entry.isDirectory()) {
                if (entry.getName().toUpperCase().equals("META-INF/")) {
                    continue;
                }
                try  {
                    map.put(new URL(jarURL.toExternalForm() + entry.getName()),
                        packageNameFor(entry));
                } catch (MalformedURLException murl) {
                    // whacky entry?
                    continue;
                }
            }
        }
    }

    private static String packageNameFor(final JarEntry entry) {
        if (entry == null) {
            return "";
        }
        String s = entry.getName();
        if (s == null) {
            return "";
        }
        if (s.length() == 0) {
            return s;
        }
        if (s.startsWith("/")) {
            s = s.substring(1, s.length());
        }
        if (s.endsWith("/")) {
            s = s.substring(0, s.length() - 1);
        }
        return s.replace('/', '.');
    }

    private final Set findSubclasses(final Class superClass, final Map locations) {
        Set v = new TreeSet(CLASS_COMPARATOR);

        Set w = null; //new Vector<Class<?>> ();

        //Package [] packages = Package.getPackages();
        //for (int i=0;i<packages.length;i++)
        //{
        //  System.out.println("package: " + packages[i]);
        //}
        Iterator it = locations.keySet().iterator();
        while (it.hasNext()) {
            URL url = (URL) it.next();
            //System.out.println(url + "-->" + locations.get(url));

            w = findSubclasses(url, (String) locations.get(url), superClass);
            if (w != null && (w.size() > 0)) {
                v.addAll(w);
            }
        }
        return v;
    }

    private final Set findSubclasses(final URL location, final String packageName,
            final Class superClass) {
        //System.out.println("looking in package:" + packageName);
        //System.out.println("looking for  class:" + superClass);
        synchronized (results) {
            // hash guarantees unique names...
            Map thisResult = new TreeMap(CLASS_COMPARATOR);
            Set v = new TreeSet(CLASS_COMPARATOR); // ...but return a set
            // TODO: double-check for null search class
            String fqcn = searchClass.getName();
            List knownLocations = new ArrayList();
            knownLocations.add(location);
            // TODO: add getResourceLocations() to this list
            // iterate matching package locations...
            for (int loc = 0; loc < knownLocations.size(); loc++) {
                URL url = (URL) knownLocations.get(loc);
                // Get a File object for the package
                File directory = new File(url.getFile());
                //System.out.println("\tlooking in " + directory);
                if (directory.exists()) {
                    // Get the list of the files contained in the package
                    String [] files = directory.list();
                    for (int i = 0; i < files.length; i++) {
                        // we are only interested in .class files
                        if (files[i].endsWith(".class")) {
                            // removes the .class extension
                            String classname = files[i].substring(0, files[i].length() - 6);
                            //System.out.println("\t\tchecking file " + classname);
                            String cls = packageName + "." + classname;
                            if (hasNoLocation(cls)) {
                                continue;
                            }
                            try {
                                Class c = Class.forName(cls);
                                if (superClass.isAssignableFrom(c)
                                        && !fqcn.equals(cls)) {
                                    thisResult.put(c, url);
                                }
                            } catch (ClassNotFoundException cnfex) {
                                errors.add(cnfex);
                                negativeResults.add(cls);
                                //System.err.println(cnfex);
                            } catch (Throwable ex) {
                                errors.add(ex);
                                negativeResults.add(cls);
                                //System.err.println(ex);
                            }
                        }
                    }
                } else {
                    try {
                        // It does not work with the filesystem: we must
                        // be in the case of a package contained in a jar file.
                        JarURLConnection conn = (JarURLConnection) url.openConnection();
                        //String starts = conn.getEntryName();
                        JarFile jarFile = conn.getJarFile();

                        //System.out.println("starts=" + starts);
                        //System.out.println("JarFile=" + jarFile);

                        Enumeration e = jarFile.entries();
                        while (e.hasMoreElements()) {
                            JarEntry entry = (JarEntry) e.nextElement();
                            String entryname = entry.getName();

                            //System.out.println("\tconsidering entry: " + entryname);

                            if (!entry.isDirectory() && entryname.endsWith(".class")) {
                                String classname = entryname.substring(0, entryname.length() - 6);
                                if (classname.startsWith("/")) {
                                    classname = classname.substring(1);
                                }
                                classname = classname.replace('/', '.');
                                if (hasNoLocation(classname)) {
                                    continue;
                                }
                                //System.out.println("\t\ttesting classname: " + classname);

                                try {
                                    // TODO: verify this block
                                    Class c = Class.forName(classname);
                                    if (superClass.isAssignableFrom(c)
                                            && !fqcn.equals(classname)) {
                                        thisResult.put(c, url);
                                    }
                                } catch (ClassNotFoundException cnfex) {
                                    // that's strange since we're scanning
                                    // the same classpath the classloader's
                                    // using... oh, well
                                    errors.add(cnfex);
                                    negativeResults.add(classname);
                                } catch (NoClassDefFoundError ncdfe) {
                                    // dependency problem... class is
                                    // unusable anyway, so just ignore it
                                    errors.add(ncdfe);
                                    negativeResults.add(classname);
                                } catch (UnsatisfiedLinkError ule) {
                                    // another dependency problem... class is
                                    // unusable anyway, so just ignore it
                                    errors.add(ule);
                                    negativeResults.add(classname);
                                } catch (Exception exception) {
                                    // unexpected problem
                                    //System.err.println(ex);
                                    errors.add(exception);
                                    negativeResults.add(classname);
                                } catch (Error error) {
                                    // lots of things could go wrong
                                    // that we'll just ignore since
                                    // they're so rare...
                                    errors.add(error);
                                    negativeResults.add(classname);
                                }
                            }
                        }
                    } catch (IOException ioex) {
                        //System.err.println(ioex);
                        errors.add(ioex);
                    }
                }
            } // while

            //System.out.println("results = " + thisResult);

            results.putAll(thisResult);

            Iterator it = thisResult.keySet().iterator();
            while (it.hasNext()) {
                v.add(it.next());
            }
            return v;
        } // synch results
    }

    private static final String getPackagePath(final String packageName) {
        // Translate the package name into an "absolute" path
        String path = new String(packageName);
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        path = path.replace('.', '/');

        // ending with "/" indicates a directory to the classloader
        if (!path.endsWith("/")) {
            path += "/";
        }

        // for actual classloader interface (NOT Class.getResource() which
        //  hacks up the request string!) a resource beginning with a "/"
        //  will never be found!!! (unless it's at the root, maybe?)
        if (path.startsWith("/")) {
            path = path.substring(1, path.length());
        }

        //System.out.println("package path=" + path);

        return path;
    }

    public static void main(final String []args) {
        //args = new String[] {"rcm.core.Any_String"};

        ClassFinder finder = null;
        Set v = null;
        List errors = null;

        if (args.length == 2) {
            finder = new ClassFinder();
            v = finder.findSubclasses(args[0], args[1]);
            errors = finder.getErrors();
        } else {
            System.out.println("Usage: java ClassFinder <fully.qualified.superclass.name> "
                + "<look only at classes starting with this>");
            return;
        }

        System.out.println("RESULTS:");
        if (v != null && v.size() > 0) {
            Iterator i = v.iterator();
            while (i.hasNext()) {
                Class cls = (Class) i.next();
                System.out.println(cls + " in " + String.valueOf(finder.getLocationOf(cls)));
            }
        } else {
            System.out.println("No subclasses of " + args[0] + " found.");
        }

        // TODO: verbose mode
//              if (errors != null && errors.size() > 0) {
//                  System.out.println("ERRORS:");
//                  for (int i = 0; i < errors.size(); i++) {
//                      Throwable t = (Throwable) errors.get(i);
//                      t.printStackTrace(System.out);
////                      System.out.println(t);
//                  }
//              }
    }
}