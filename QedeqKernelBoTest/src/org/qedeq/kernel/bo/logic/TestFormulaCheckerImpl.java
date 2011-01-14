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

package org.qedeq.kernel.bo.logic;

import java.lang.reflect.InvocationTargetException;

import org.qedeq.base.test.DynamicGetter;
import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.bo.test.KernelFacade;


/**
 * This class deals with {@link org.qedeq.kernel.base.list.Element}s which represent a
 * formula. It has methods to check those elements for being well-formed.
 *
 * LATER mime 20070307: here are sometimes error messages that get concatenated with
 * an {@link org.qedeq.kernel.base.list.ElementList#getOperator()} string. Perhaps these
 * strings must be translated into the original input format and a mapping must be done.
 *
 * @author  Michael Meyling
 */
public class TestFormulaCheckerImpl extends FormulaCheckerImpl {

    /**
     * Constructor.
     *
     */
    public TestFormulaCheckerImpl() {
    }

    /**
     * Set location information where are we within the original module.
     *
     * @param   locationWithinModule    Location within module.
     */
    protected void setLocationWithinModule(final String locationWithinModule) {
        final Qedeq qedeq = KernelFacade.getKernelContext().getQedeqBo(getCurrentContext()
            .getModuleLocation()).getQedeq();
        try {
//            System.out.println(locationWithinModule);
            DynamicGetter.get(qedeq, getCurrentContext().getLocationWithinModule());
        } catch (IllegalAccessException e) {
            System.out.println("checking " + locationWithinModule);
            e.printStackTrace(System.out);
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            System.out.println("checking " + locationWithinModule);
            e.printStackTrace(System.out);
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            System.out.println("checking " + locationWithinModule);
            e.printStackTrace(System.out);
            throw e;
        }
        getCurrentContext().setLocationWithinModule(locationWithinModule);
    }

}
