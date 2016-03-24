/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.base.test;

import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.qedeq.base.utility.StringUtility;

/**
 * Basis class for all tests that check test class existence.
 *
 * @author Michael Meyling
 */
public abstract class EachClassHasATestCase extends TestCase {


    /**
     * Constructor.
     *
     * @param   name    Test case name.
     */
    public EachClassHasATestCase(final String name) {
        super(name);
    }

    /**
     * Constructor.
     */
    public EachClassHasATestCase() {
        super();
    }

    /**
     * Get package name for those classes and the classes of their sub packages where we
     * want test for tests.
     *
     * @return  Tests for all classes of this package or its sub packages must occur.
     */
    protected abstract String getPackagePrefix();

    /**
     * Should the test fail if a test class is missing?
     * Otherwise the missing classes are just printed to <code>System.out</code>.
     *
     * @return  Failure if test classes are missing?
     */
    protected abstract boolean failIfTestClassesAreMissing();
    
    /**
     * Test if all classes have a test class.
     */
    public void testIfEveryClassIsTested() {
        String prefix = getPackagePrefix();
        if (!prefix.endsWith(".")) {
            prefix += ".";
        }
        final ClassFinder finder = new ClassFinder();
        final Set allMatchingClassesInPath
            = finder.findSubclasses(Object.class.getName(), prefix);
        final Iterator i = allMatchingClassesInPath.iterator();
        final Set classesToTest = new TreeSet(ClassFinder.CLASS_COMPARATOR);
        final Set testClasses = new TreeSet(ClassFinder.CLASS_COMPARATOR);
        while (i.hasNext()) {
            Class c = (Class) i.next();
            if (c.getName().indexOf("$") < 0 && !Modifier.isInterface(c.getModifiers())
                    && c.getName().indexOf(".test.") < 0
                    && !c.getName().endsWith("TestSuite")) {
                if (c.getName().endsWith("Test")) {
//                    System.out.println("adding test class " + c);
                    testClasses.add(c.getName());
                } else {
//                    System.out.println(c + " " + finder.getLocationOf(c));
                    classesToTest.add(c);
                }

            
            }
        }
        final Set result = new TreeSet(ClassFinder.CLASS_COMPARATOR);
        final Iterator j = classesToTest.iterator();
        while (j.hasNext()) {
            Class c = (Class) j.next();
            if (!testClasses.contains(c.getName() + "Test") && !c.getName().endsWith("TestCase")
                    && !c.getName().endsWith("Tester")) {
//                System.out.println("missing test for " + c.getName() + " mod: " +c.getModifiers());
                result.add(c.getName());
            } else {
//                System.out.println("no need to test  " + c.getName() + " mod: " +c.getModifiers());
            }
        }
        if (!result.isEmpty()) {
            System.out.println("The following classes have no test classes:");
            System.out.println(StringUtility.asLines(result));
            if (failIfTestClassesAreMissing()) {
                fail("the following classes have no test classes: "
                    + StringUtility.toString(result));
            }
        }
    }

}
