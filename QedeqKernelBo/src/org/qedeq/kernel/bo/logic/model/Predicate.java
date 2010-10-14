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

package org.qedeq.kernel.bo.logic.model;

import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.StringUtility;

/**
 * One predicate for our model.
 *
 * @author  Michael Meyling
 */
public abstract class Predicate {

    /** This class. */
    private static final Class CLASS = Predicate.class;

    /** Minimum number of arguments this predicate has. */
    private final int minimum;

    /** Maximum number of arguments this predicate has. */
    private final int maximum;

    /** Display text. */
    private final String display;

    /** Description for this predicate. */
    private final String description;

    /**
     * Constructor.
     *
     * @param   minimum         Minimum number of arguments this predicate has.
     * @param   maximum         Maximum number of arguments this predicate has.
     * @param   display         Show this to represent the predicate within outputs.
     * @param   description     Description for this predicate.
     */
    public Predicate(final int minimum, final int maximum, final String display,
            final String description) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.display = display;
        this.description = description;
    }

    /**
     * Construct negation of other predicate.
     *
     * @param   predicate   Negate this predicate.
     * @return  Negation of predicate.
     */
    public static Predicate not(final Predicate predicate) {
        return new Predicate(predicate.getMinimumArgumentNumber(),
            predicate.getMaximumArgumentNumber(), "!" + predicate.toString(),
            "!" + predicate.getDescription()) {
                public boolean calculate(final Entity[] entities) {
                    final String method = "not.calculate(Entity[])";
                    if (Trace.isDebugEnabled(CLASS)) {
                        Trace.param(CLASS, method, "toString", toString());
                        Trace.param(CLASS, method, "entities", StringUtility.toString(entities));
                    }
                    final boolean result = !predicate.calculate(entities);
                    Trace.param(CLASS, method, "result  ", result);
                    return result;
                }
        };
    }

    /**
     * Construct conjunction of two predicates.
     *
     * @param   op1     First predicate.
     * @param   op2     Second predicate.
     * @return  Conjunction of two predicates.
     */
    public static Predicate and(final Predicate op1, final Predicate op2) {
        if (op1.getMinimumArgumentNumber() > op2.getMaximumArgumentNumber()
                || op1.getMaximumArgumentNumber() < op2.getMinimumArgumentNumber()) {
            throw new IllegalArgumentException("Predicates can not be combined " + op1 + " and "
                + op2);
        }
        return new Predicate(Math.max(op1.getMinimumArgumentNumber(),
                op2.getMinimumArgumentNumber()), Math.min(op1.getMaximumArgumentNumber(),
                op2.getMaximumArgumentNumber()), op1.toString() + " and " + op2.toString(),
                op1.getDescription() + " and " + op2.getDescription()) {
            public boolean calculate(final Entity[] entities) {
                final String method = "and.calculate(Entity[])";
                if (Trace.isDebugEnabled(CLASS)) {
                    Trace.param(CLASS, method, "toString", toString());
                    Trace.param(CLASS, method, "entities", StringUtility.toString(entities));
                }
                boolean result = op1.calculate(entities) && op2.calculate(entities);
                Trace.param(CLASS, method, "result  ", result);
                return result;
            }
        };
    }

    /**
     * Construct disjunction of two predicates.
     *
     * @param   op1     First predicate.
     * @param   op2     Second predicate.
     * @return  Disjunction of two predicates.
     */
    public static Predicate or(final Predicate op1, final Predicate op2) {
        if (op1.getMinimumArgumentNumber() > op2.getMaximumArgumentNumber()
                || op1.getMaximumArgumentNumber() < op2.getMinimumArgumentNumber()) {
            throw new IllegalArgumentException("Predicates can not be combined " + op1 + " and "
                + op2);
        }
        return new Predicate(Math.max(op1.getMinimumArgumentNumber(),
                op2.getMinimumArgumentNumber()), Math.min(op1.getMaximumArgumentNumber(),
                op2.getMaximumArgumentNumber()), op1.toString() + " or " + op2.toString(),
                op1.getDescription() + " or " + op2.getDescription()) {
            public boolean calculate(final Entity[] entities) {
                final String method = "or.calculate(Entity[])";
                if (Trace.isDebugEnabled(CLASS)) {
                    Trace.param(CLASS, method, "toString", toString());
                    Trace.param(CLASS, method, "entities", StringUtility.toString(entities));
                }
                if (Trace.isDebugEnabled(CLASS)) {
                    Trace.param(CLASS, method, "toString", toString());
                    Trace.param(CLASS, method, "entities", StringUtility.toString(entities));
                }
                boolean result = op1.calculate(entities) || op2.calculate(entities);
                return result;
            }
        };
    }

    /**
     * Get minimum number of arguments this predicate has.
     *
     * @return  Minimum number of arguments for this predicate.
     */
    public int getMinimumArgumentNumber() {
        return minimum;
    }

    /**
     * Get maximum number of arguments this predicate has.
     *
     * @return  Maximum umber of arguments for this predicate.
     */
    public int getMaximumArgumentNumber() {
        return maximum;
    }

    /**
     * Get display text.
     *
     * @return  Representation of this predicate for textual output.
     */
    public String toString() {
        return display;
    }

    /**
     * Get description.
     *
     * @return  Description of this predicate.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Calculate truth value.
     *
     * @param   entities    Calculate predicate for this entities.
     * @return  Truth value.
     */
    public abstract boolean calculate(Entity[] entities);

}
