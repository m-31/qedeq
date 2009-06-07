/* $Id: DynamicGetter.java,v 1.1 2008/07/26 07:56:13 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2009,  Michael Meyling <mime@qedeq.org>.
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.qedeq.base.trace.Trace;

/**
 * Get result of getter expression chain. A string is interpreted as an getter call chain and
 * is called on a given object. The resulting object is returned.
 *
 * @version $Revision: 1.1 $
 * @author Michael Meyling
 */
public final class DynamicGetter {

    /** This class. */
    private static final Class CLASS = DynamicGetter.class;

    /**
     * Constructor.
     */
    private DynamicGetter() {
        // nothing to do
    }

    private static final Object getMethodResult(final Object obj, final String methodParam)
            throws IllegalAccessException, InvocationTargetException  {
        String methodName = methodParam;
        Object[] param;
        int pos = methodName.indexOf("(");
        if (pos >= 0) {
            methodName = methodName.substring(0, pos);
            if (')' == methodParam.charAt(pos + 1)) {
                param = new Object[0];
            } else {
                param = new Object[1];
                param[0] = new Integer(Integer.parseInt(methodParam.substring(pos + 1,
                    methodParam.length() - 1)));
            }
        } else {
            param = new Object[0];
        }
        // mime 20050622: check parameter types and length, support string parameters
        if (obj == null) {
            Trace.trace(DynamicGetter.class, "getMethodResult(Object)", "obj = null!");
        }
        final Method[] method = obj.getClass().getDeclaredMethods();
        for (int i = 0; i < method.length; i++) {
            if (method[i].getName().equals(methodName)) {
                Trace.trace(CLASS, "getMethodResult(Object)", "invoking:",
                    method[i].getName());
                return method[i].invoke(obj, param);
            }
        }
        // mime 20050622: other exception?
        throw new RuntimeException("method not found: " + methodName);
    }

    /**
     * Interpret string as getter call chain end return result.
     *
     * @param   object      Call getters on this object.
     * @param   methodParam Getter chain like <code>getHeader().getAuthorList().getAuthor(0)</code>.
     * @return  Expression result.
     * @throws  IllegalAccessException
     * @throws  InvocationTargetException
     */
    public static final Object get(final Object object, final String methodParam)
            throws IllegalAccessException, InvocationTargetException {
        Object result = object;
        int posOld = 0; // last found "." position
        int pos = 0;    // current "." position

        Trace.param(CLASS, "get(Object, String)", "methodParam", methodParam);
        // iterate expressions
        while (0  <= (pos = methodParam.indexOf(".", posOld))) {
            String method = methodParam.substring(posOld, pos);
            posOld = pos + 1;
            Trace.param(CLASS, "get(Object, String)", "method", method);
            Object result2 = getMethodResult(result, method);
            if (result2 == null) {
                Trace.trace(CLASS, "get(Object, String)", "result = null");
            }
            result = result2;
        }

//        ????????????

        // last expression (must not be an getter)
        Trace.param(CLASS, "get(Object, String)", "method",
            methodParam.substring(posOld));
        return getMethodResult(result, methodParam.substring(posOld));
    }
}
