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

package org.qedeq.kernel.bo.logic.heuristic;

import java.util.ArrayList;
import java.util.List;


/**
 * This class calculates a new truth value for a given formula for a given interpretation.
 *
 * @author  Michael Meyling
 */
public final class Interpretation {

    /** List of predicate variables. */
    private List predVars;

    /** List of predicate variable values. */
    private List predValues;


    /**
     * Constructor.
     */
    public Interpretation() {
        predVars = new ArrayList();
        predValues = new ArrayList();
    }

    public boolean getPredValue(final String identifier) {
        boolean result = false;
        if (predVars.contains(identifier)) {
            result = ((Boolean) predValues.get(predVars.indexOf(identifier))).booleanValue();
        } else {
            System.out.println("added predvar " + identifier);
            result = false;
            predVars.add(identifier);
            predValues.add(Boolean.FALSE);
        }
        return result;
    }

    public boolean iterationIsNotFinished() {
        return !predVars.isEmpty();
    }

    /**
     * Change to next valuation.
     */
    public void iterate() {
        if (predVars.isEmpty()) {
            System.out.println("empty");
            return;
        }
        System.out.println("iterate");
        for (int i = predVars.size() - 1; i >= -1; i--) {
            if (i < 0) {
                predVars.clear();
                break;
            }
            boolean old = ((Boolean) predValues.get(i)).booleanValue();
            if (!old) {
                predValues.set(i, Boolean.TRUE);
                break;
            } else {
                predValues.set(i, Boolean.FALSE);
            }
        }
    }

    public String toString() {
        if (predVars.isEmpty()) {
            return "{}";
        }
        final StringBuffer buffer = new StringBuffer(5 * predVars.size());
        buffer.append("{");
        for (int i = 0; i < predVars.size(); i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            boolean value = ((Boolean) predValues.get(i)).booleanValue();
            buffer.append(predVars.get(i) + "=");
            buffer.append((value ? "1" : "0"));
        }
        buffer.append("}");
        return buffer.toString();
    }



}
