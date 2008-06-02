/* $Id: AbstractValueObjectTest.java,v 1.13 2008/03/27 05:12:46 m31 Exp $
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

package org.qedeq.kernel.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.kernel.base.list.Element;
import org.qedeq.kernel.dto.list.DefaultAtom;


/**
 * Basis for value object tests.
 * @see #testAll()
 *
 * @version $Revision: 1.13 $
 * @author    Michael Meyling
 */
public abstract class AbstractValueObjectTest extends QedeqTestCase {

    /** Set of all methods to check. */
    private Set methodsToCheck;

    /** Set of already checked methods. */
    private Set checkedMethods;

    protected void setUp() throws Exception {
        methodsToCheck = new HashSet();
        checkedMethods = new HashSet();

        final Class clazz = getTestedClass();

        Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            getMethodsToCheck().add(methods[i].getName());
        }
    }

    /**
     * Return the value object class to test.
     *
     * @return  Class to test.
     */
    protected abstract Class getTestedClass();

    /**
     * Test everything we could. This includes getter and setter, but
     * the combination of <code>add</code>, <code>size</code> and
     * <code>get(int)</code> is possible too.
     * <p>
     * This method also tests <code>equal</code>, <code>hashCode</code>
     * and <code>toString</code>.
     * <p>
     * Preconditions for using this method are the following.
     * <ul>
     * <li>For every attribute of the tested class exists a combination
     * of getter and setter with matching names or a triple of
     * <code>add</code>, <code>size</code>, <code>get(int)</code>.</li>
     * <li>Every attribute is of type {@link String}, {@link Integer},
     * {@link Boolean}, {@link List} or fulfills the preconditions for
     * itself.</li>
     * </ul>
     *
     * @throws  Exception   Something went wrong. Perhaps the preconditions were
     *                      violated.
     */
    public void testAll() throws Exception {
        checkGetterAndSetter();
        checkToString();
        checkHashCode();
        checkEquals();
        checkAllChecked();
    }

    /**
     * Check the setters and getters and <code>add</code>, <code>size</code> and 
     * <code>get(int)</code>.
     * If the attribute names don't match the method names we have a problem.
     *
     * @throws  Exception   Something went wrong. Perhaps the preconditions in {@link #testAll()}
     *                      were violated.
     */
    protected void checkGetterAndSetter() throws Exception {
        final Class clazz = getTestedClass();
        Field[] attrs = clazz.getDeclaredFields();
        if (attrs.length == 0) {
            fail("no attributes found in class " + clazz.getName());
        }
        // iterate over every attribute of the value object
        for (int i = 0; i < attrs.length; i++) {
            String attrName = attrs[i].getName();
            if (attrName.startsWith("__")) {    // because of clover, ignore those attributes
                continue;
            }
            boolean tested = false;
            tested = tested || testGetSetAdd(clazz, attrs[i]);
            tested = tested || testAddSizeGet(clazz, attrs[i]);
            if (!tested) {
                fail("could not test attribute " + attrName + " in class " + clazz.getName());
            }
        }
    }

    /**
     * Tests getter and setter methods for an attribute of a class. Also tests an <code>add</code> 
     * method for list attributes.
     *
     * @param   clazz       Attribute is in this class.
     * @param   attr        Test methods for this attribute.
     * @return  Did this test match? If <code>true</code> all those methods were tested.
     * @throws  Exception   Test failure.
     */
    protected boolean testGetSetAdd(final Class clazz, final Field attr) throws Exception {
        final Method[] methods = clazz.getDeclaredMethods();
        final String attrName = attr.getName();
        Method getter = null;
        Method setter = null;
        Method adder = null;
        for (int j = 0; j < methods.length; j++) {
            if (methods[j].getName().equals("get" + getUName(attrName))) {
                getter = methods[j];
            }
            if (methods[j].getName().equals("set" + getUName(attrName))) {
                setter = methods[j];
            }
            if ((methods[j].getName() + "List").equals("add" + getUName(attrName))) {
                adder = methods[j];
            }
        }
        // are getter and setter known?
        if (getter == null || setter == null) {
            return false;
        }
        final Object vo = getObject(clazz);
        final Object result1 = getter.invoke(vo, new Object[0]);
        assertNull(result1);
        final Object value = getFilledObject(getter.getReturnType(), clazz, attrName);
        setter.invoke(vo, new Object[] {value});
        final Object result2 = getter.invoke(vo, new Object[0]);
        assertEquals(value, result2);
        if (adder != null) {
            final Method size1 = result2.getClass().getMethod("size", new Class[0]);
            final int number1 = ((Integer) size1.invoke(result2, new Object[0])).intValue();

            final Object add = getFilledObject(adder.getParameterTypes()[0], clazz, attrName);
            adder.invoke(vo, new Object[] {add});

            final Object result3 = getter.invoke(vo, new Object[0]);
            final Method size2 = result3.getClass().getMethod("size", new Class[0]);
            final int number2 = ((Integer) size2.invoke(result3, new Object[0])).intValue();
            assertEquals(number1 + 1, number2);
            addChecked(adder);
            removeMethodToCheck(adder.getName());
        }
        setter.invoke(vo, new Object[] {null});
        final Object result4 = getter.invoke(vo, new Object[0]);
        assertNull(result4);
        addChecked(setter);
        removeMethodToCheck(setter.getName());
        addChecked(getter);
        removeMethodToCheck(getter.getName());
        return true;
    }

    protected void addChecked(Method adder) {
        checkedMethods.add(adder.getName());
    }

    /**
     * Tests <code>add</code>, <code>get</code> and <code>size</code> methods for an attribute of a
     * class.
     *
     * @param   clazz       Attribute is in this class.
     * @param   attr        Test methods for this attribute.
     * @return  Did this test match? If <code>true</code> all those methods were tested.
     * @throws  Exception   Test failure.
     */
    protected boolean testAddSizeGet(final Class clazz, final Field attr) throws Exception {
        final Method[] methods = clazz.getDeclaredMethods();
        final String attrName = attr.getName();
        Method getter = null;
        Method setter = null;
        // check if attribute  could be set by "add"
        Method size = null;
        for (int j = 0; j < methods.length; j++) {
            if (methods[j].getName().equals("add")) {
                setter = methods[j];
            }
            if (methods[j].getName().equals("get")) {
                getter = methods[j];
            }
            if (methods[j].getName().equals("size")) {
                size = methods[j];
            }
        }
        if (getter == null || setter == null || size == null) { // testable?
            return false;
        }
        final Object vo = getObject(clazz);
        try {
            getter.invoke(vo, new Object[] {new Integer(0)});
            fail("IndexOutOfBoundsException expected");
        } catch (InvocationTargetException e) {
            if (!(e.getCause() instanceof IndexOutOfBoundsException)) {
                fail("IndexOutOfBoundsException expected");
            }
        }
        final Object zero = size.invoke(vo, new Object[0]);
        assertEquals(new Integer(0), zero);
        final Object value = getFilledObject(getter.getReturnType(), clazz, attrName);
        setter.invoke(vo, new Object[] {value});
        final Object result2 = getter.invoke(vo, new Object[] {new Integer(0)});
        assertEquals(value, result2);
        final Object one = size.invoke(vo, new Object[0]);
        assertEquals(new Integer(1), one);
        assertFalse(vo.equals(getFilledObject(getter.getReturnType(), clazz, attrName)));
        addChecked(setter);
        removeMethodToCheck(setter.getName());
        addChecked(getter);
        removeMethodToCheck(getter.getName());
        addChecked(size);
        removeMethodToCheck(size.getName());
        return true;
    }

    /**
     * Test the <code>toString</code> method. This method is also tested with other check methods.
     *
     * @throws  Exception   Something went wrong. Perhaps the preconditions in {@link #testAll()} 
     *                      were violated.
     */
    protected void checkToString() throws Exception {
        {
            final Object vo1 = getObject(getTestedClass());
            assertNotNull(vo1.toString());
            final Object vo2 = getObject(getTestedClass());
            assertEquals(vo1.toString(), vo2.toString());
        }
        {
            final Object vo1 = getFilledObject(getTestedClass());
            assertNotNull(vo1.toString());
            final Object vo2 = getFilledObject(getTestedClass());
            assertEquals(vo1.toString(), vo2.toString());
        }
        removeMethodToCheck("toString");
    }

    /**
     * Remove method from list of methods to test. Be careful: overloaded
     * methods must be tested within one method!
     * 
     * @param   name    Remove this method name.
     */
    protected final void removeMethodToCheck(final String name) {
        getMethodsToCheck().remove(name);
    }

    /**
     * Test the <code>hashCode</code> method. This method is also tested with other check methods.
     *
     * @throws  Exception   Something went wrong. Perhaps the preconditions in {@link #testAll()} 
     *                      were violated.
     */
    protected void checkHashCode() throws Exception {
        {
            final Object vo1 = getObject(getTestedClass());
            final Object vo2 = getObject(getTestedClass());
            assertTrue(vo1.hashCode() == vo2.hashCode());
            final Method[] methods = getTestedClass().getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().startsWith("set")) {
                    final Method setter = methods[i];
                    if (setter.getParameterTypes().length > 1) {
                        continue;
                    }
                    final Class setClazz = setter.getParameterTypes()[0];
                    final Object value1 = getFilledObject(setClazz, getTestedClass(), 
                        setter.getName());
                    setter.invoke(vo1, new Object[] {value1});
                    assertTrue(vo1.hashCode() != vo2.hashCode());
                    setter.invoke(vo2, new Object[] {value1});
                    assertTrue(vo1.hashCode() == vo2.hashCode());
                    final Object value2 = getFilledObject(setClazz, getTestedClass(), 
                        setter.getName());
                    setter.invoke(vo2, new Object[] {value2});
                    assertTrue(vo1.hashCode() == vo2.hashCode());
                }
            }
        }
        {
            final Object vo1 = getFilledObject(getTestedClass());
            final Object vo2 = getFilledObject(getTestedClass());
            assertEquals(vo1.hashCode(), vo2.hashCode());
        }
        removeMethodToCheck("hashCode");
    }

    /**
     * Test the <code>equals</code> method. This method is also tested with other check methods.
     *
     * @throws  Exception   Something went wrong. Perhaps the preconditions in {@link #testAll()}
     *                      were violated.
     */
    protected void checkEquals() throws Exception {
        checkEqualsFillUp();
        checkEqualsForEachSetter();
        {
            final Object vo1 = getFilledObject(getTestedClass());
            final Object vo2 = getFilledObject(getTestedClass());
            final Object vo3 = getEmptyObject(getTestedClass(), null, null);
            assertTrue(vo1.equals(vo1));
            assertTrue(vo1.equals(vo2));
            assertTrue(vo2.equals(vo1));
            assertFalse(vo1.equals(null));
            assertFalse(vo1.equals(vo3));
        }
        {
            Field[] attrs = getTestedClass().getDeclaredFields();
            for (int i = 0; i < attrs.length; i++) {
                if (attrs[i].getName().startsWith("__")) {  // because of clover
                    continue;
                }
                final Object vo1 = getFilledObject(attrs[i].getType(), getTestedClass(), "", 
                    attrs[i].getName());
                final Object vo2 = getFilledObject(attrs[i].getType(), getTestedClass(), "", 
                    attrs[i].getName());
                assertTrue(vo1.equals(vo1));
                assertTrue(vo1.equals(vo2));
                assertTrue(vo2.equals(vo1));
            }
        }
        removeMethodToCheck("equals");
    }

    /**
     * Check equals after calling setter on empty object.
     *
     * @throws Exception
     */
    protected void checkEqualsForEachSetter() throws Exception {
        {
            final Method[] methods = getTestedClass().getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                final Object vo1 = getObject(getTestedClass());
                final Object vo2 = getObject(getTestedClass());
                if (methods[i].getName().startsWith("set") 
                        || methods[i].getName().startsWith("add")) {
                    final Method setter = methods[i];
                    if (setter.getParameterTypes().length > 1) {
                        continue;
                    }
                    final Class setClazz = setter.getParameterTypes()[0];
                    setter.invoke(vo1, new Object[] {null});
                    if (methods[i].getName().startsWith("set")) {
                        assertTrue(vo1.equals(vo2));
                        assertTrue(vo2.equals(vo1));
                    } else {
                        assertFalse(vo1.equals(vo2));
                        assertFalse(vo2.equals(vo1));
                    }
                    final Object value1 = getFilledObject(setClazz, getTestedClass(), setter.getName());
                    setter.invoke(vo2, new Object[] {value1});
                    assertFalse(vo1.equals(vo2));
                    assertFalse(vo2.equals(vo1));
                    assertTrue(vo1.hashCode() != vo2.hashCode());
                    assertFalse(vo1.toString().equals(vo2.toString()));
                }
            }
        }
    }

    /**
     * Check equals during successive fill up by calling setters.
     *
     * @throws Exception
     */
    protected void checkEqualsFillUp() throws Exception {
        {
            final Object vo1 = getObject(getTestedClass());
            final Object vo2 = getObject(getTestedClass());
            assertTrue(vo1.equals(vo1));
            assertTrue(vo1.equals(vo2));
            assertTrue(vo2.equals(vo1));
            assertFalse(vo1.equals(null));

            final Method[] methods = getTestedClass().getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().startsWith("set") 
                        || methods[i].getName().startsWith("add")) {
                    final Method setter = methods[i];
                    if (setter.getParameterTypes().length > 1) {
                        continue;
                    }
                    final Class setClazz = setter.getParameterTypes()[0];
                    final Object value1 = getFilledObject(setClazz, getTestedClass(), 
                        setter.getName());
                    setter.invoke(vo1, new Object[] {value1});
                    assertFalse(vo1.equals(vo2));
                    assertFalse(vo2.equals(vo1));
                    assertFalse(vo1.equals(null));
                    setter.invoke(vo2, new Object[] {value1});
                    assertTrue(vo1.equals(vo2));
                    assertTrue(vo2.equals(vo1));
                    assertTrue(vo2.equals(vo1));
                    final Object value2 = getFilledObject(setClazz, getTestedClass(), 
                        setter.getName());
                    setter.invoke(vo2, new Object[] {value2});
                    if (methods[i].getName().startsWith("set")) {
                        assertTrue(vo1.equals(vo2));
                        assertTrue(vo2.equals(vo1));
                    } else {
                        assertFalse(vo1.equals(vo2));
                        assertFalse(vo2.equals(vo1));
                        setter.invoke(vo1, new Object[] {value2});
                        assertTrue(vo1.equals(vo2));
                        assertTrue(vo2.equals(vo1));
                    }
                }
                assertTrue(vo1.hashCode() == vo2.hashCode());
                assertTrue(vo1.toString().equals(vo2.toString()));
            }
        }
    }

    /**
     * Test if all methods of the value object were tested..
     */
    protected void checkAllChecked() {
        if (!getMethodsToCheck().isEmpty()) {
            final String[] missing = (String[]) getMethodsToCheck().toArray(new String[0]);
            final StringBuffer buffer 
                = new StringBuffer("tests for following methods are missing: ");
            for (int i = 0; i < missing.length; i++) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append("\"" + missing[i] + "\"");
            }
            fail(buffer.toString());
        }
    }

    protected Set getMethodsToCheck() {
        return methodsToCheck;
    }

    /**
     * Creates an data filled instance of a class.
     *
     * @param   clazz       Create instance of this class.
     * @return  Object filled by its setters.
     * @throws  Exception   Something went wrong. Perhaps the preconditions in {@link #testAll()} were
     *                      violated.
     */
    protected final Object getFilledObject(final Class clazz) throws Exception {
        return getFilledObject(clazz, null, "", "");
    }

    /**
     * Creates an data filled instance of a class.
     *
     * @param   clazz       Create instance of this class.
     * @param   parent      This is the class that contains an element of <code>clazz</code>. 
     *                      Maybe <code>null</code>.
     * @param   attribute   Attribute name of parent that shall be filled.
     * @return  Object filled by its setters.
     * @throws  Exception   Something went wrong. Perhaps the preconditions in {@link #testAll()} 
     *                      were violated.
     */
    protected final Object getFilledObject(final Class clazz, final Class parent, 
            final String attribute) throws Exception {
        return getFilledObject(clazz, parent, attribute, "");
    }

    /**
     * Creates an data filled instance of a class.
     *
     * @param   clazz       Create instance of this class.
     * @param   parent      This is the class that contains an element of <code>clazz</code>. 
     *                      Maybe <code>null</code>.
     * @param   attribute   Attribute name of parent that shall be filled.
     * @param   ignore      Fill every attribute but not this one. Could be empty, but shouldn't be
     *                      <code>null</code>.
     * @return  Object filled by its setters.
     * @throws  Exception   Something went wrong. Perhaps the preconditions in {@link #testAll()}
     *                      were violated.
     */
    protected final Object getFilledObject(final Class clazz, final Class parent, 
            final String attribute, final String ignore) throws Exception {

        final Object vo = getObject(clazz, parent, attribute);
        final Class voClazz = vo.getClass();

        if (clazz.equals(String.class) || clazz.equals(Integer.class) || clazz.equals(Boolean.class)
            || clazz.equals(List.class)) {
            return vo;
        }

        Method[] methods = voClazz.getDeclaredMethods();
//        boolean found = false;
        for (int i = 0; i < methods.length; i++) {
//            System.out.println("\t" + methods[i].getName());
            if (methods[i].getName().startsWith("set") && !methods[i].equals("set"
                    + getUName(ignore))) {
                final Method setter = methods[i];
                if (setter.getParameterTypes().length > 1) {
                    fail("setter with more than one parameter found: " + setter.getName());
                }
                final Class setClazz = setter.getParameterTypes()[0];
                final Object value = getFilledObject(setClazz, clazz, setter.getName());
//                System.out.println(clazz.getName() + "." + setter.getName());
//                System.out.println(value.getClass()+ ":" + value);
                setter.invoke(vo, new Object[] {value});
//                found = true;
            } else if (methods[i].getName().equals("add")) {
                final Method adder = methods[i];
                if (adder.getParameterTypes().length > 1) {
                    final StringBuffer buffer = new StringBuffer("in class \"" + clazz
                            + "\" method \"add\" with more than one parameter found: "
                            + adder.getName());
                    for (int j = 0; j < adder.getParameterTypes().length; j++) {
                        buffer.append(" " + adder.getParameterTypes()[j]);
                    }
                    fail(buffer.toString());
                }
                final Class setClazz = adder.getParameterTypes()[0];
                if (setClazz != clazz) {
                    final Object value = getFilledObject(setClazz, clazz, adder.getName());
                    adder.invoke(vo, new Object[] {value});
//                    found = true;
                }
            }
        }
/*
        if (!found) {
            fail("nothing to set in object of class " + clazz.getName());
        }
*/
        return vo;
    }

    /**
     * Get (if possible) empty instance of an class.
     *
     * @param   clazz   For this class an instance is wanted.
     * @return  Just the result of the default constructor (if existing).
     * @throws  Exception   Something went wrong. Perhaps the preconditions in {@link #testAll()}
     *                      were violated.
     */
    protected Object getObject(final Class clazz) throws Exception {
        return getObject(clazz, null, "");
    }

    /**
     * Get (if possible) empty instance of an class.
     *
     * @param   clazz   For this class an instance is wanted.
     * @param   parent  This class has <code>clazz</code> as an attribute. Maybe <code>null</code>.
     * @param   attribute   Attribute name of parent that shall be filled.
     * @return  Just the result of the default constructor (if existing).
     * @throws  Exception   Something went wrong. Perhaps the preconditions in {@link #testAll()}
     *                      were violated.
     */
    protected Object getObject(final Class clazz, final Class parent, final String attribute)
            throws Exception {
        final Object vo = getEmptyObject(clazz, parent, attribute);
        if (vo == null) {
            fail("no default constructor for " + clazz.getName() + " found");
        }
        return vo;
    }

    /**
     * Gets an implementation for an abstract class.
     *
     * @param   clazz   Get implementation for this class.
     * @return  Concrete class, if any.
     */
    protected abstract Class abstractToConcreteClass(Class clazz);

    /**
     * Get (if possible) empty instance of an class. This method could be overwritten to get more
     * objects.
     *
     * @param   clazz   For this class an instance is wanted.
     * @param   parent  This class has <code>clazz</code> as an attribute. Maybe <code>null</code>.
     * @param   attribute   Attribute name of parent that shall be filled.
     * @return  Just the result of the default constructor (if existing). Might be 
     *          <code>null</code>.
     * @throws  Exception   Something went wrong. Perhaps the preconditions in {@link #testAll()}
     *                      were violated.
     */
    protected Object getEmptyObject(Class clazz, final Class parent, final String attribute)
            throws Exception {

        if (abstractToConcreteClass(clazz) != null) {
            clazz = abstractToConcreteClass(clazz);
        }
        if (clazz.equals(Element.class)) {              // application specific
            return new DefaultAtom((parent != null ? parent.getName() + ":" : "") + attribute);
        } else if (clazz.equals(String.class)) {
            return (parent != null ? parent.getName() + ":" : "") + new String("StringAtom:"
                + attribute);
        } else if (clazz.equals(Integer.class)) {
            return new Integer(10);
        } else if (clazz.equals(Boolean.class)) {
            return new Boolean("true");
        } else if (clazz.equals(List.class)) {
            return new ArrayList();
        }
        Constructor[] constructors = clazz.getConstructors();
        Constructor constructor = null;
        for (int j = 0; j < constructors.length; j++) {
            if (constructors[j].getParameterTypes().length == 0) {
                constructor = constructors[j];
            }
        }
        if (constructor == null) {
            return null;
        }
        return constructor.newInstance(new Object[0]);
    }

    /**
     * Get name with first letter upper case.
     *
     * @param   name
     * @return  Name with first letter upper case.
     */
    public static final String getUName(final String name) {
        if (name.length() > 0) {
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        return "";
    }

}
