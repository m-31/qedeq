/* $Id: Operator.java,v 1.6 2008/03/27 05:16:27 m31 Exp $
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

package org.qedeq.kernel.bo.parser;


/**
 * This class describes an term or logical operator. An operator is of either
 * prefix, infix or postfix type and has a minimum and maximum number of operands.
 * It has a symbol or token that enables to recognize it and a QEDEQ representation.
 *
 * @version $Revision: 1.6 $
 * @author  Michael Meyling
 */
public final class Operator {

    /** Marks infix operator. */
    public static final int INFIX = 0;

    /** Marks prefix operator. */
    public static final int SIMPLE_PREFIX = 1;

    /** Marks postfix operator. */
    public static final int POSTFIX = 2;

    /** Marks function operator. */
    public static final int FUNCTION = 4;

    /** Prefix, function, infix or postfix. See {@link #SIMPLE_PREFIX}, {@link #FUNCTION},
     * {@link #INFIX} and {@link #POSTFIX}. */
    private final int type;

    /** Start symbol or token for this operator. */
    private final String startSymbol;

    /** Separator symbol or token for this operator. This could be a comma for example. */
    private final String separatorSymbol;

    /** End symbol or token for this operator. */
    private final String endSymbol;

    /** Operator priority. Highest is 0. */
    private final int priority;

    /** QEDEQ token for this operator. */
    private final String qedeq;

    /** First QEDEQ argument. Can be <code>null</code>. */
    private final String qedeqArgument;

    /** Minimum number of arguments. */
    private final int min;

    /** Maximum number of arguments. */
    private final int max;


    /**
     * Constructor.
     *
     * @param   symbol      Symbol or token for this operator.
     * @param   qedeq       QEDEQ operator symbol.
     * @param   qedeqArgument       First Argument in QEDEQ-Syntax - if any.
     * @param   priority    Operator priority, highest is 0.
     * @param   type        Prefix, infix or postfix. See {@link #SIMPLE_PREFIX}, {@link #FUNCTION},
     *                      {@link #INFIX} and {@link #POSTFIX}.
     * @param   min         Minimum number of arguments for this operator.
     */
    public Operator(final String symbol,
             final String qedeq,
             final String qedeqArgument,
             final int priority,
             final int type,
             final int min) {
        this(symbol, qedeq, qedeqArgument, priority, type, min, -1);
    }

    /**
     * Constructor.
     *
     * @param   symbol              Symbol or token for this operator.
     * @param   qedeq               QEDEQ operator symbol.
     * @param   qedeqArgument       First Argument in QEDEQ-Syntax - if any.
     * @param   priority            Operator priority, highest is 0.
     * @param   type                Prefix, infix or postfix. See {@link #SIMPLE_PREFIX},
     *                              {@link #FUNCTION},
     *                              {@link #INFIX} and {@link #POSTFIX}.
     * @param   min                 Minimum number of arguments for this operator.
     * @param   max                 Maximum number of arguments for this operator.
     */
    public Operator(final String symbol,
             final String qedeq,
             final String qedeqArgument,
             final int priority,
             final int type,
             final int min,
             final int max) {
        this(symbol, null, null, qedeq, qedeqArgument, priority, type, min, max);
    }

    /**
     * Constructor for prefix operators like <code>{x | x > 0}</code>.
     *
     * @param   startSymbol         Starting symbol or token for this operator.
     * @param   separatorSymbol     Symbol or token that separates arguments for this operator.
     * @param   endSymbol           End symbol or token for this operator.
     * @param   qedeq               QEDEQ operator symbol.
     * @param   qedeqArgument       First Argument in QEDEQ-Syntax - if any
     * @param   priority            Operator priority, highest is 0.
     * @param   min                 Minimum number of arguments for this operator.
     */
    public Operator(final String startSymbol,
             final String separatorSymbol,
             final String endSymbol,
             final String qedeq,
             final String qedeqArgument,
             final int priority,
             final int min) {
        this(startSymbol, separatorSymbol, endSymbol, qedeq, qedeqArgument, priority, SIMPLE_PREFIX,
            min, -1);
    }

    /**
     * Constructor for prefix operators like <code>{x, y, z}</code>.
     *
     * @param   startSymbol         Starting symbol or token for this operator.
     * @param   separatorSymbol     Symbol or token that separates arguments for this operator.
     * @param   endSymbol           End symbol or token for this operator.
     * @param   qedeq               QEDEQ operator symbol.
     * @param   qedeqArgument       First Argument in QEDEQ-Syntax - if any.
     * @param   priority            Operator priority, highest is 0.
     * @param   min                 Minimum number of arguments for this operator.
     * @param   max                 Maximum number of arguments for this operator.
     */
    public Operator(final String startSymbol,
             final String separatorSymbol,
             final String endSymbol,
             final String qedeq,
             final String qedeqArgument,
             final int priority,
             final int min,
             final int max) {
        this(startSymbol, separatorSymbol, endSymbol, qedeq, qedeqArgument, priority, SIMPLE_PREFIX,
            min, max);
    }

    /**
     * Constructor.
     *
     * @param   startSymbol         Starting symbol or token for this operator.
     * @param   separatorSymbol     Symbol or token that separates arguments for this operator.
     * @param   endSymbol           End symbol or token for this operator.
     * @param   qedeq               QEDEQ operator symbol.
     * @param   qedeqArgument       First Argument in QEDEQ-Syntax - if any.
     * @param   priority            Operator priority, highest is 0.
     * @param   type                Prefix, infix or postfix. See {@link #SIMPLE_PREFIX},
     *                              {@link #FUNCTION}, {@link #INFIX} and {@link #POSTFIX}.
     * @param   min                 Minimum number of arguments for this operator.
     * @param   max                 Maximum number of arguments for this operator.
     */
    public Operator(final String startSymbol, final String separatorSymbol, final String endSymbol,
             final String qedeq,
             final String qedeqArgument,
             final int priority,
             final int type,
             final int min,
             final int max) {
        this.startSymbol = startSymbol;
        this.separatorSymbol = separatorSymbol;
        this.endSymbol = endSymbol;
        this.type = type;
        this.qedeq = qedeq;
        this.qedeqArgument = qedeqArgument;
        this.priority = priority;
        this.min = min;
        this.max = max;
        switch (type) {
            case INFIX:
            case SIMPLE_PREFIX:
            case FUNCTION:
            case POSTFIX:
                break;
            default:
                    throw new IllegalArgumentException("unknown operator type: "
                        + type);
        }
        if (max != -1 && min > max) {
            throw new IllegalArgumentException("Min greater than max: " + min + " > " + max);
        }
        if (isInfix() && min < 2) {
            throw new IllegalArgumentException("Infix needs at least two arguments");
        }
    }

    /**
     * Returns symbol or token to identify this operator.
     *
     * @return  Symbol or token to identify a start of this operator.
     */
    public final String getStartSymbol() {
        return startSymbol;
    }

    /**
     * Returns symbol or token to separate different arguments for this operator. Can only be
     * different from <code>null</code> if this is a prefix operator.
     *
     * @return  Symbol or token to identify the start of a new argument of this operator.
     */
    public String getSeparatorSymbol() {
        return separatorSymbol;
    }

    /**
     * Returns symbol or token to identify the end of this operator. Can only be different from
     * <code>null</code> if this is a prefix operator.
     *
     * @return  Symbol or token to identify the end of this operator. Maybe <code>null</code>.
     */
    public String getEndSymbol() {
        return endSymbol;
    }

    /**
     * Is this an infix operator?
     *
     * @return  Is this an infix operator?
     */
    public final boolean isInfix() {
        return type == INFIX;
    }

    /**
     * Is this a prefix operator?
     *
     * @return  Is this a prefix operator?
     */
    public final boolean isPrefix() {
        return type == SIMPLE_PREFIX || type == FUNCTION;
    }

    /**
     * Is this a function operator?
     *
     * @return  Is this a function operator?
     */
    public final boolean isFunction() {
        return type == FUNCTION;
    }

    /**
     * Is this a postfix operator?
     *
     * @return  Is this a postfix operator?
     */
    public final boolean isPostfix() {
        return type == POSTFIX;
    }

    /**
     * Get operator priority. 0 is the highest priority.
     *
     * @return  Priority.
     */
    public final int getPriority() {
        return this.priority;
    }

    /**
     * Get minimum argument number.
     *
     * @return  Minimum argument number.
     */
    public final int getMin() {
        return this.min;
    }

    /**
     * Get maximum argument number.
     *
     * @return  Maximum argument number.
     */
    public final int getMax() {
        return max;
    }

    /**
     * Get QEDEQ operator name.
     *
     * @return  QEDEQ operator name.
     */
    public final String getQedeq() {
        return qedeq;
    }

    /**
     * Get first QEDEQ argument.
     *
     * @return  First QEDEQ argument. Can be <code>null</code>.
     */
    public final String getQedeqArgument() {
        return qedeqArgument;
    }

    public final String toString() {
        final StringBuffer buffer = new StringBuffer(getStartSymbol());
        buffer.append("[" + getMin() + ", ");
        if (getMax() == -1) {
            buffer.append("..");
        } else {
            buffer.append(getMax());
        }
        buffer.append("]");
        if (getSeparatorSymbol() != null) {
            buffer.append(" ").append(getSeparatorSymbol());
        }
        if (getEndSymbol() != null) {
            buffer.append(" ").append(getEndSymbol());
        }
        if (isFunction()) {
            buffer.append(", is function");
        }
        if (isPrefix()) {
            buffer.append(", is prefix");
        }
        if (isInfix()) {
            buffer.append(", is infix");
        }
        return buffer.toString();
    }

}

