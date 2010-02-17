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

package org.qedeq.base.utility;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * We learned so much from the great Jedi master. Using the force we can get and set private
 * fields of arbitrary objects. We can even execute private methods...
 *
 * @author  Michael Meyling
 */
public abstract class YodaUtility {

    /**
     * Constructor, should never be called.
     */
    private YodaUtility() {
        // don't call me
    }

    /**
     * This method executes a method on an object or one of its super instances (even if it is
     * private).
     *
     * @param   obj             Object.
     * @param   name            Variable name
     * @param   parameterTypes  Parameter types.
     * @param   parameter       Parameter values.
     * @return  Execution result.
     * @throws  NoSuchMethodException       Method not found.
     * @throws  InvocationTargetException   Wrapped exception. 
     */
    public static Object executeMethod(final Object obj, final String name,
            final Class[] parameterTypes, final Object[] parameter) throws NoSuchMethodException,
            InvocationTargetException {
        Method method = null;
        try {
            Class cl = obj.getClass();
            while (!Object.class.equals(cl)) {
                try {
                    method = cl.getDeclaredMethod(name, parameterTypes);
                    break;
                } catch (NoSuchMethodException ex) {
                    cl = cl.getSuperclass();
                }
            }
            if (method == null) {
                throw new NoSuchMethodException(name);
            }
            method.setAccessible(true);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
        try {
            return method.invoke(obj, parameter);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method returns the contents of an object variable .The class hierarchy is recursively
     * searched to find such a field (even if it is private).
     *
     * @param   obj     Object.
     * @param   name    Variable name
     * @return  Contents of variable.
     * @throws  NoSuchFieldException    Variable of given name was not found. 
     */
    public static Object getFieldValue(final Object obj, final String name) throws NoSuchFieldException {
        final Field field = getField(obj, name);
        try {
            return field.get(obj);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * This method sets the contents of an object variable. The class hierarchy is recursively
     * searched to find such a field (even if it is private). 
     * 
     * @param   obj     Object.
     * @param   name    Variable name.
     * @param   value   Value to set.
     * @throws  NoSuchFieldException    Variable of given name was not found. 
     */
    public static void setFieldContent(final Object obj, final String name, final Object value)
            throws NoSuchFieldException {
        final Field field = getField(obj, name);
        try {
            field.set(obj, value);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get field of given name in given object. The class hierarchy is recursively searched
     * to find such a field (even if it is private). 
     * 
     * @param   obj     Object to work on.
     * @param   name    Search this field.
     * @return  Found field.
     * @throws  NoSuchFieldException    Field with name <code>name</code> was not found.
     */
    public static Field getField(final Object obj, final String name) throws NoSuchFieldException {
        Field field = null;
        try {
            Class cl = obj.getClass();
            while (!Object.class.equals(cl)) {
                try {
                    field = cl.getDeclaredField(name);
                    break;
                } catch (NoSuchFieldException ex) {
                    cl = cl.getSuperclass();
                }
            }
            if (field == null) {
                throw (new NoSuchFieldException(name));
            }
            field.setAccessible(true);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
        return field;
    }
    
}

