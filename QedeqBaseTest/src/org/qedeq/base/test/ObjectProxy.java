/* $Id: QedeqProxy.java,v 1.7 2008/03/27 05:12:45 m31 Exp $
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

package org.qedeq.base.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.StringUtility;

/**
 * For testing QEDEQ modules.
 *
 * @version $Revision: 1.7 $
 * @author Michael Meyling
 */
public class ObjectProxy implements InvocationHandler {

    /** This class. */
    private static final Class CLASS = ObjectProxy.class;

    private Object target;

    private static int level;

    private String history = "";

    /**
     * Create proxy for given object.
     *
     * @param obj Object to proxy.
     * @return Proxy for <code>obj</code>.
     */
    public static Object createProxy(final Object obj) {
        return createProxy(obj, null);
    }

    private static Object createProxy(final Object obj, final Object parent) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Proxy) {
            Trace.trace(CLASS, "createProxy", "object is already proxy!");
            return obj;
        }
        Trace.trace(CLASS, "createProxy", "instanciating");
        return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass()
            .getInterfaces(), new ObjectProxy(obj, parent));
    }

    private ObjectProxy(final Object obj, final Object parent) {
        target = obj;
        if (parent != null) {
            Trace.trace(CLASS, "QedeqProxy(Object, Object)", parent.getClass().getName());
            history = ((ObjectProxy) Proxy.getInvocationHandler(parent)).history + "/"
                + StringUtility.getClassName(obj.getClass());
        } else {
            history = StringUtility.getClassName(obj.getClass());
        }
    }

    public Object invoke(final Object proxy, final Method method, final Object[] args)
            throws Throwable {
        level++;
        final StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < level; i++) {
            buffer.append("-");
        }
        Trace.trace(CLASS, "invoke", buffer.toString() + "> " + method.getName());
        Trace.trace(CLASS, "invoke", "> " + history);
        Object result = null;
        try {
            Object[] proxyArgs = null;
            if (args != null) {
                proxyArgs = new Object[args.length];
                for (int i = 0; i < args.length; i++) {
                    if (method.getParameterTypes()[i].isPrimitive()) {
                        proxyArgs[i] = args[i];
                    } else { // TODO determine interfaces, but other than by
                                // getReturnType.getInterfaces()
                        proxyArgs[i] = createProxy(args[i], proxy);
                    }
                }
            }
            result = method.invoke(target, proxyArgs);
        } catch (InvocationTargetException e) {
            Trace.trace(CLASS, "invoke", e);
            throw e.getCause();
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        } finally {
            Trace.trace(CLASS, "invoke", buffer.toString() + "< " + method.getName());
            level--;
        }
        if (method.getReturnType().getName().startsWith("java.")
                || method.getReturnType().isPrimitive()
                || method.getReturnType().equals(String.class)) {
            Trace.trace(CLASS, "invoke", "creating no proxy for " + method.getReturnType());
            Trace.trace(CLASS, "invoke", "result is: >" + result + "<");
            return result;
            // TODO determine interfaces, but other than by getReturnType.getInterfaces()
        }
        Trace.trace(CLASS, "invoke", "creating proxy for " + method.getReturnType());
        return createProxy(result, proxy);
    }

}
