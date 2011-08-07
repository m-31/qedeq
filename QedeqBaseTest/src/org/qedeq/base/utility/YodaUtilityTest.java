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

import org.qedeq.base.test.QedeqTestCase;

/**
 * Test {@link YodaUtility}.
 *
 * @author  Michael Meyling
 */
public class YodaUtilityTest extends QedeqTestCase {

    /** Very secret number, which is read by yoda with the help of the force. */
    private static final int secret = 967123;

    /** Very secret number, which is changed by yoda with the help of the force. */
    private int changeMe = 815;

    /**
     * Test {@link YodaUtility#getFieldValue(Object, String)}.
     *
     * @throws Exception    Something bad happened.
     */
    public void testGetFieldValue() throws Exception {
        assertEquals(new Integer(secret), YodaUtility.getFieldValue(this, "secret"));
        assertEquals(getOutdir(), YodaUtility.getFieldValue(this, "outdir"));
        try {
            YodaUtility.getFieldValue(this, "unknown");
            fail("Exception expected");
        } catch (NoSuchFieldException e) {
            // ok
        }
    }

    /**
     * Test {@link YodaUtility#getField(Object, String)}.
     *
     * @throws Exception    Something bad happened.
     */
    public void testGetField() throws Exception {
        assertEquals(this.getClass().getDeclaredField("secret"),
            YodaUtility.getField(this, "secret"));
        assertEquals(QedeqTestCase.class.getDeclaredField("outdir"),
            YodaUtility.getField(this, "outdir"));
        try {
            YodaUtility.getField(this, "unknown");
            fail("Exception expected");
        } catch (NoSuchFieldException e) {
            // ok
        }
    }

    /**
     * Test {@link YodaUtility#executeMethod(Object, String, Class[], Object[])}.
     *
     * @throws Exception    Something bad happened.
     */
    public void testExecuteMethod() throws Exception {
        assertEquals(getOutdir(),  YodaUtility.executeMethod(this, "getOutdir",
            new Class[]{}, new Object[]{}));
        try { 
            YodaUtility.executeMethod(this, "getoutdir",
                new Class[]{}, new Object[]{});
            fail("exception expected");
        } catch (NoSuchMethodException e) {
            // ok
        }
        final String time = "" + System.currentTimeMillis();
        assertEquals(myTestMethod(time), 
            YodaUtility.executeMethod(this, "myTestMethod",
            new Class[]{String.class}, new Object[]{ time}));
        try { 
            YodaUtility.executeMethod(this, "myTestMethodi",
                new Class[]{String.class}, new Object[]{ time});
            fail("exception expected");
        } catch (NoSuchMethodException e) {
            // ok
        }
        try { 
            YodaUtility.executeMethod(this, "myTestMethod",
                new Class[]{}, new Object[]{});
            fail("exception expected");
        } catch (NoSuchMethodException e) {
            // ok
        }
    }

    private String myTestMethod(final String my) {
        return my + "done!";
    }

    /**
     * Test {@link YodaUtility#executeMethod(String, String, Class[], Object[]).
     *
     * @throws Exception    Something bad happened.
     */
    public void testExecuteMethod2() throws Exception {
        try {
            YodaUtility.executeMethod(this.getClass().getName(), "getOutdir",
                new Class[]{}, new Object[]{});
            fail("exception expected");
        } catch (NoSuchMethodException e) {
            // ok
        }
        try { 
            YodaUtility.executeMethod(this.getClass().getName(), "getoutdir",
              new Class[]{}, new Object[]{});
            fail("exception expected");
        } catch (NoSuchMethodException e) {
            // ok
        }
        final String time = "" + System.currentTimeMillis();
        assertEquals(myStaticTestMethod(time), 
            YodaUtility.executeMethod(this.getClass().getName(), "myStaticTestMethod",
            new Class[]{String.class}, new Object[]{ time}));
        try { 
            YodaUtility.executeMethod(this.getClass().getName(), "myStaticTestMethodi",
            new Class[]{String.class}, new Object[]{ time});
        } catch (NoSuchMethodException e) {
            // ok
        }
        try { 
            YodaUtility.executeMethod(this.getClass().getName(), "myStaticTestMethod",
            new Class[]{}, new Object[]{});
        } catch (NoSuchMethodException e) {
            // ok
        }
    }

    private static String myStaticTestMethod(final String my) {
        return my + "done!";
    }

    /**
     * Test {@link YodaUtility#existsMethod(Class, String, Class[])}.
     *
     * @throws Exception    Something bad happened.
     */
    public void testExistsMethod() throws Exception {
        assertTrue(YodaUtility.existsMethod(this.getClass(), "getOutdir",
            new Class[]{}));
        assertFalse(YodaUtility.existsMethod(this.getClass(), "getOutdir",
                new Class[]{Integer.TYPE}));
        assertFalse(YodaUtility.existsMethod(this.getClass(), "getTevqOutdir",
                new Class[]{}));
        assertTrue(YodaUtility.existsMethod(this.getClass(), "testExistsMethod",
                new Class[]{}));
    }

    /**
     * Test {@link YodaUtility#getFieldValue(Object, String)}.
     *
     * @throws Exception    Something bad happened.
     */
    public void testSetFieldValue() throws Exception {
        try {
            YodaUtility.setFieldContent(this, "secret", new Integer(37846));
//            System.out.println("secret=" + secret);
            fail("Exception expected");
        } catch (Exception e) {
            // expected;
        }
        changeMe = 0;
        YodaUtility.setFieldContent(this, "changeMe", new Integer(37846));
        assertEquals(37846, changeMe);;
    }

    /**
     * Test {@link YodaUtility#existsMethod(String, String, Class[])}.
     *
     * @throws Exception    Something bad happened.
     */
    public void testExistsMethod2() throws Exception {
        assertTrue(YodaUtility.existsMethod(this.getClass().getName(), "getOutdir",
            new Class[]{}));
        assertFalse(YodaUtility.existsMethod(this.getClass().getName(), "getOutdir",
                new Class[]{Integer.TYPE}));
        assertFalse(YodaUtility.existsMethod(this.getClass().getName(), "getTevqOutdir",
                new Class[]{}));
        assertTrue(YodaUtility.existsMethod(this.getClass().getName(), "testExistsMethod2",
                new Class[]{}));
        assertFalse(YodaUtility.existsMethod("this.is.a.non.existing.Class", "getOutdir",
            new Class[]{}));
    }
    
}
