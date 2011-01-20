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

package org.qedeq.base.test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.qedeq.base.utility.StringUtility;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This test suite makes it possible to test also "pest"-Methods.
 * <p>
 * If your tests are all implemented you would have red JUnit tests till the implementation is fully
 * finished. To solve this dilemma tests methods that fail because the implementation is still
 * missing get a name starting with "pest". If the implementation should work the test methods are
 * renamed to names starting with "test". Therefore you should have green JUnit tests through the
 * whole development process.
 * <p>
 * To get an overview over the test result of all test methods this suite provides the possibility
 * to test also "pest" methods.
 * <p>
 * The basic source code is based on the superclass source code (JUnit 3.7).
 *
 * @see junit.framework.TestSuite
 * @author Michael Meyling
 */
public class QedeqTestSuite extends TestSuite {

    /** Test tests that start with "test". */
    private static boolean withTest = true;

    /** Test also tests that start with "pest" instead of "test"? */
    private static boolean withPest = false;

    /**
     * Constructs an empty TestSuite.
     */
    public QedeqTestSuite() {
    }

    /**
     * Constructs an empty TestSuite.
     *
     * @param   withTest    Execute "test*" methods.
     * @param   withPest    Execute "pest*" methods.
     */
    public QedeqTestSuite(final boolean withTest, final boolean withPest) {
        setWithTest(withTest);
        setWithPest(withPest);
    }

    /**
     * Constructs a TestSuite from the given class. Adds all the methods starting with "test" and
     * eventually "pest" and test cases to the suite. Parts of this method was written at 2337
     * meters in the Huffihutte, Kanton Uri. Other parts were developed at 4 meters in the
     * Stoltenstrasse, Hamburg, Germany.
     *
     * @param   theClass    Take test methods from this class.
     * @param   withTest    Execute "test*" methods.
     * @param   withPest    Execute "pest*" methods.
     */
    public QedeqTestSuite(final Class theClass, final boolean withTest, final boolean withPest) {
        setWithTest(withTest);
        setWithPest(withPest);
        setName(theClass.getName());
        try {
            getTestConstructor(theClass); // Avoid generating multiple error messages
        } catch (NoSuchMethodException e) {
            addTest(warning("Class " + theClass.getName()
                + " has no public constructor TestCase(String name) or TestCase()"));
            return;
        }

        if (!Modifier.isPublic(theClass.getModifiers())) {
            addTest(warning("Class " + theClass.getName() + " is not public"));
            return;
        }

        Class superClass = theClass;
        Vector names = new Vector();
        while (Test.class.isAssignableFrom(superClass)) {
            Method[] methods = superClass.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                addTestMethod(methods[i], names, theClass);
            }
            superClass = superClass.getSuperclass();
        }
        if (testCount() == 0 && withTest) {
            addTest(warning("No tests found in " + theClass.getName()));
        }
    }

    /**
     * Test also tests that start with "test"?
     *
     * @param withTest Test "test"-Tests?
     */
    public void setWithTest(final boolean withTest) {
        QedeqTestSuite.withTest = withTest;
    }

    /**
     * Test also tests that start with "pest" instead of "test"?
     *
     * @param withPest Test also "pest"-Tests?
     */
    public void setWithPest(final boolean withPest) {
        QedeqTestSuite.withPest = withPest;
    }

    /**
     * Adds the tests from the given class to the suite.
     *
     * @param   testClass   Add this class.
     */
    public void addTestSuite(final Class testClass) {
        addTest(new QedeqTestSuite(testClass, withTest, withPest));
    }

    private void addTestMethod(final Method m, final Vector names, final Class theClass) {
        String name = m.getName();
        if (names.contains(name)) {
            return;
        }
        if (!Modifier.isPublic(m.getModifiers()) || !isTestMethod(m)) {
            if (isTestMethod(m)) {
                addTest(warning("Test method isn't public: " + m.getName()));
            }
            return;
        }
        names.addElement(name);
        addTest(createTest(theClass, name));
    }

    private boolean isTestMethod(final Method m) {
        String name = m.getName();
        Class[] parameters = m.getParameterTypes();
        Class returnType = m.getReturnType();
        return parameters.length == 0
            && ((withTest ? name.startsWith("test") : false) || (withPest ? name.startsWith("pest")
                : false)) && returnType.equals(Void.TYPE);
    }

    /**
     * Returns a test which will fail and log a warning message.
     *
     * @param   message Log this warning message.
     * @return  Failure test.
     */
    protected static Test warning(final String message) {
        return new TestCase("warning") {
            protected void runTest() {
                fail(message);
            }
        };
    }

    /**
     * Returns the set of test classes.
     *
     * @param   test    TestSuite we want to analyze.
     * @return  Set of all test classes.
     */
    public Set getTestClasses(final TestSuite test) {
        Set result = new HashSet();
        Enumeration e = test.tests();
        while (e.hasMoreElements()) {
            Test t = (Test) e.nextElement();
            if (t instanceof TestSuite) {
                TestSuite suite = (TestSuite) t;
                result.addAll(getTestClasses(suite));
            } else {
                result.add(t.getClass());
            }
        }
        return result;
    }

    /**
     * Test if all classes that inherit from {@link QedeqTestCase} are within this
     * <code>TestSuite</code>. To know about this the class path is analyzed.
     *
     * @param   packagePrefix   Tests of this package or its sub packages must occur.
     */
    public void addTestIfEveryExistingTestIsCalled(final String packagePrefix) {
        String prefix = packagePrefix;
        if (!prefix.endsWith(".")) {
            prefix += ".";
        }
        final Set testClasses = getTestClasses(this);
        final Set possibleTestClasses = (new ClassFinder()).findSubclasses(
            QedeqTestCase.class.getName(), prefix);
        possibleTestClasses.removeAll(testClasses);
        Iterator i = possibleTestClasses.iterator();
        HashSet result = new HashSet();
        while (i.hasNext()) {
            Class c = (Class) i.next();
            if (c.getName().indexOf("$") < 0 && !Modifier.isAbstract(c.getModifiers())) {
                result.add(c);
            }
        }
        if (!result.isEmpty()) {
            addTest(warning("the following tests are missing in the main test suite: "
                + StringUtility.toString(result)));
        }
    }

}
