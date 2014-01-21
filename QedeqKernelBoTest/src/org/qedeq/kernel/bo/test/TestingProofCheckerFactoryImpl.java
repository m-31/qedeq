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

package org.qedeq.kernel.bo.test;

import java.lang.reflect.InvocationTargetException;

import org.qedeq.base.io.Version;
import org.qedeq.base.test.DynamicGetter;
import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.bo.logic.ProofCheckerFactoryImpl;
import org.qedeq.kernel.bo.logic.proof.checker.ProofChecker0Impl;
import org.qedeq.kernel.bo.logic.proof.checker.ProofChecker1Impl;
import org.qedeq.kernel.bo.logic.proof.checker.ProofChecker2Impl;
import org.qedeq.kernel.bo.logic.proof.common.ProofChecker;



/**
 * Extra testing factory implementation for {@link ProofChecker}s.
 *
 * @author  Michael Meyling
 */
public class TestingProofCheckerFactoryImpl extends ProofCheckerFactoryImpl {

    public ProofChecker createProofChecker(final Version ruleVersion) {
        if (ruleVersion.equals("0.00.00")) {
            return new ProofChecker0Impl() {
                
            };
        } else if (ruleVersion.equals("0.01.00")) {
            return new ProofChecker1Impl() {
                protected void setLocationWithinModule(final String locationWithinModule) {
                    getCurrentContext().setLocationWithinModule(locationWithinModule);
                    try {
//                        System.out.println("testing context " + locationWithinModule);
                        QedeqBo qedeq = KernelContext.getInstance().getQedeqBo(getCurrentContext().getModuleLocation());
                        DynamicGetter.get(qedeq.getQedeq(), getCurrentContext().getLocationWithinModule());
                    } catch (RuntimeException e) {
                        System.out.println(getCurrentContext().getLocationWithinModule());
                        throw e;
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }

            };
        } else if (ruleVersion.equals("0.02.00")) {
            return new ProofChecker2Impl() {
                protected void setLocationWithinModule(final String locationWithinModule) {
                    getCurrentContext().setLocationWithinModule(locationWithinModule);
                    try {
//                        System.out.println("testing context " + locationWithinModule);
                        QedeqBo qedeq = KernelContext.getInstance().getQedeqBo(getCurrentContext().getModuleLocation());
                        DynamicGetter.get(qedeq.getQedeq(), getCurrentContext().getLocationWithinModule());
                    } catch (RuntimeException e) {
                        System.out.println(getCurrentContext().getLocationWithinModule());
                        throw e;
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }

            };
        }
        // not found, so we take the best one we have
        return new ProofChecker2Impl();
    }


}
