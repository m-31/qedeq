/* $Id: QedeqProxy.java,v 1.5 2007/02/25 20:04:32 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2007,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.control;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.qedeq.kernel.utility.IoUtility;

/**
 * For testing QEDEQ modules.
 *
 * @version $Revision: 1.5 $
 * @author Michael Meyling
 */
public class QedeqProxy implements InvocationHandler {

    private Object target;

    private static int level;

    private String history = "";

    /**
     * Create proxy for given object.
     * 
     * @param   obj Object to proxy.
     * @return  Proxy for <code>obj</code>.
     */
    public static Object createProxy(Object obj) {
        return createProxy(obj, null);
    }

    private static Object createProxy(Object obj, Object parent) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Proxy) {
            System.out.println("object is already proxy!");
            return obj;
        }
        System.out.println("instanciating");
        return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass()
            .getInterfaces(), new QedeqProxy(obj, parent));
    }

    private QedeqProxy(Object obj, Object parent) {
        target = obj;
        if (parent != null) {
            System.out.println(parent.getClass().getName());
            history = ((QedeqProxy) Proxy.getInvocationHandler(parent)).history + "/"
                + IoUtility.getClassName(obj.getClass());
        } else {
            history = IoUtility.getClassName(obj.getClass());
        }
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        level++;
        for (int i = 0; i < level; i++) {
            System.out.print("-");
        }
        System.out.println("> " + method.getName());
        System.out.println("> " + history);
        Object result = null;
        try {
            Object[] proxyArgs = null;
            if (args != null) {
                proxyArgs = new Object[args.length];
                for (int i = 0; i < args.length; i++) {
                    if (method.getParameterTypes()[i].isPrimitive()) {
                        proxyArgs[i] = args[i];
                    } else {    // TODO determine interfaces, but other than by getReturnType.getInterfaces()
                        proxyArgs[i] = createProxy(args[i], proxy);
                    }
                }
            }
            result = method.invoke(target, proxyArgs);
        } catch (InvocationTargetException e) {
            System.out.println("exception: " + e);
            throw e.getCause();
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        } finally {
            for (int i = 0; i < level; i++) {
                System.out.print("-");
            }
            System.out.println("< " + method.getName());
            level--;
        }
        if (method.getReturnType().getName().startsWith("java.") || method.getReturnType().isPrimitive() || method.getReturnType().equals(String.class)) {
            System.out.println("creating no proxy for " + method.getReturnType());
            System.out.println("result is: >" + result + "<");
            return result;  // TODO determine interfaces, but other than by getReturnType.getInterfaces()
        }
        System.out.println("creating proxy for " + method.getReturnType());
        return createProxy(result, proxy);
    }

}
