/* $Id: AbstractFormulaChecker.java,v 1.2 2007/02/25 20:04:32 m31 Exp $
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

package org.qedeq.kernel.bo.logic;



import org.qedeq.kernel.test.QedeqTestCase;

/**
 * For testing the {@link org.qedeq.kernel.bo.logic.FormulaChecker}.
 *
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */
public abstract class AbstractFormulaChecker extends QedeqTestCase {

    private ExistenceChecker checker = new ExistenceChecker() {

        public boolean predicateExists(String name, int arguments) {
            System.out.println("name=" + name + "    arguments=" + arguments);
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

        public boolean functionExists(String name, int arguments) {
            System.out.println("name=" + name + "    arguments=" + arguments);
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

        public boolean classOperatorExists() {
            return true;
        }

        public boolean equalityOperatorExists() {
            return true;
        }

        public String getEqualityOperator() {
            return "equal";
        }
        
    };

    private ExistenceChecker checkerWithoutClass = new ExistenceChecker() {

        public boolean predicateExists(String name, int arguments) {
            System.out.println("name=" + name + "    arguments=" + arguments);
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

        public boolean functionExists(String name, int arguments) {
            System.out.println("name=" + name + "    arguments=" + arguments);
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

        public boolean classOperatorExists() {
            return false;
        }

        public boolean equalityOperatorExists() {
            return true;
        }

        public String getEqualityOperator() {
            return "equal";
        }
        
    };

    protected ExistenceChecker getChecker() {
        return checker;
    }

    protected ExistenceChecker getCheckerWithoutClass() {
        return checkerWithoutClass;
    }
    
    
}
