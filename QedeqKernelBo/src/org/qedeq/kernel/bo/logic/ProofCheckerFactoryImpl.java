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

package org.qedeq.kernel.bo.logic;

import org.qedeq.kernel.bo.logic.common.ProofChecker;
import org.qedeq.kernel.bo.logic.common.ProofCheckerFactory;
import org.qedeq.kernel.bo.logic.proof.basic.ProofChecker0Impl;
import org.qedeq.kernel.bo.logic.proof.basic.ProofChecker2Impl;
import org.qedeq.kernel.bo.logic.proof.basic.ProofChecker1Impl;



/**
 * Factory implementation for {@link ProofChecker}s.
 *
 * @author  Michael Meyling
 */
public class ProofCheckerFactoryImpl implements ProofCheckerFactory {

    public boolean isRuleVersionSupported(final String ruleVersion) {
        if ("0.00.00".equals(ruleVersion)) {
            return true;
        } else if ("0.01.00".equals(ruleVersion)) {
            return true;
        } else if ("0.02.00".equals(ruleVersion)) {
            return true;
        }
        return false;
    }

    public ProofChecker createProofChecker(final String ruleVersion) {
        if ("0.00.00".equals(ruleVersion)) {
            return new ProofChecker0Impl(ruleVersion);
        } else if ("0.01.00".equals(ruleVersion)) {
            return new ProofChecker1Impl(ruleVersion);
        } else if ("0.02.00".equals(ruleVersion)) {
            return new ProofChecker2Impl(ruleVersion);
        }
        // not found, so we take the best one we have
        return new ProofChecker2Impl(ruleVersion);
    }


}
