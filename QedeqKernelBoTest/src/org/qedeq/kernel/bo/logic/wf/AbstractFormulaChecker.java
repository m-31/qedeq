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

package org.qedeq.kernel.bo.logic.wf;


import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.logic.common.ExistenceChecker;
import org.qedeq.kernel.bo.logic.common.FunctionKey;
import org.qedeq.kernel.bo.logic.common.PredicateKey;
import org.qedeq.kernel.se.common.RuleKey;

/**
 * For testing the {@link org.qedeq.kernel.bo.logic.FormulaChecker}.
 *
 * @author  Michael Meyling
 */
public abstract class AbstractFormulaChecker extends QedeqTestCase {

    /** This class. */
    private static final Class CLASS = AbstractFormulaChecker.class;


    private ExistenceChecker checker = new ExistenceChecker() {

        public boolean predicateExists(final String name, final int arguments) {
            Trace.param(CLASS, this, "predicateExists(String, int)", "name", name);
            Trace.param(CLASS, this, "predicateExists(String, int)", "arguments", arguments);
            if ("in".equals(name) && arguments == 2) {
                return true;
            } else if ("isSet".equals(name) && arguments == 1) {
                return true;
            } else if ("equal".equals(name) && arguments == 2) {
                return true;
            } else if ("true".equals(name) && arguments == 0) {
                return true;
            }
            return false;
        }

        public boolean predicateExists(final PredicateKey predicate) {
            return predicateExists(predicate.getName(), Integer.parseInt(predicate.getArguments()));
        }

        public boolean functionExists(final String name, final int arguments) {
            Trace.param(CLASS, this, "functionExists(String, int)", "name", name);
            Trace.param(CLASS, this, "functionExists(String, int)", "arguments", arguments);
            if ("power".equals(name) && arguments == 1) {
                return true;
            } else if ("union".equals(name) && arguments == 2) {
                return true;
            } else if ("intersection".equals(name) && arguments == 2) {
                return true;
            } else if ("empty".equals(name) && arguments == 0) {
                return true;
            }
            return false;
        }

        public boolean functionExists(FunctionKey function) {
            return functionExists(function.getName(), Integer.parseInt(function.getArguments()));
        }

        public boolean classOperatorExists() {
            return true;
        }

        public boolean identityOperatorExists() {
            return true;
        }

        public String getIdentityOperator() {
            return "equal";
        }

        public boolean isInitialPredicate(PredicateKey predicate) {
            if ("in".equals(predicate.getName()) && predicate.getArguments() == "2") {
                return true;
            }
            return false;
        }

        public boolean isInitialFunction(FunctionKey function) {
            return false;
        }

        public boolean ruleExists(RuleKey ruleKey) {
            return false;
        }

    };

    private ExistenceChecker checkerWithoutClass = new ExistenceChecker() {

        public boolean predicateExists(String name, int arguments) {
            Trace.param(CLASS, this, "predicateExists(String, int)", "name", name);
            Trace.param(CLASS, this, "predicateExists(String, int)", "arguments", arguments);
            if ("in".equals(name) && arguments == 2) {
                return true;
            } else if ("isSet".equals(name) && arguments == 1) {
                return true;
            } else if ("equal".equals(name) && arguments == 2) {
                return true;
            } else if ("true".equals(name) && arguments == 0) {
                return true;
            }
            return false;
        }

        public boolean predicateExists(PredicateKey predicate) {
            return predicateExists(predicate.getName(), Integer.parseInt(predicate.getArguments()));
        }

        public boolean functionExists(String name, int arguments) {
            Trace.param(CLASS, this, "functionExists(String, int)", "name", name);
            Trace.param(CLASS, this, "functionExists(String, int)", "arguments", arguments);
            if ("power".equals(name) && arguments == 1) {
                return true;
            } else if ("union".equals(name) && arguments == 2) {
                return true;
            } else if ("intersection".equals(name) && arguments == 2) {
                return true;
            } else if ("empty".equals(name) && arguments == 0) {
                return true;
            }
            return false;
        }

        public boolean functionExists(FunctionKey function) {
            return functionExists(function.getName(), Integer.parseInt(function.getArguments()));
        }

        public boolean classOperatorExists() {
            return false;
        }

        public boolean identityOperatorExists() {
            return true;
        }

        public String getIdentityOperator() {
            return "equal";
        }

        public boolean isInitialPredicate(PredicateKey predicate) {
            if ("in".equals(predicate.getName()) && predicate.getArguments() == "2") {
                return true;
            }
            return false;
        }

        public boolean isInitialFunction(FunctionKey function) {
            return false;
        }

        public boolean ruleExists(RuleKey ruleKey) {
            return false;
        }

    };

    protected ExistenceChecker getChecker() {
        return checker;
    }

    protected ExistenceChecker getCheckerWithoutClass() {
        return checkerWithoutClass;
    }


}
