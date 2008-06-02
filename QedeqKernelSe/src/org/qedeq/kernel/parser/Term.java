/* $Id: Term.java,v 1.8 2008/05/15 21:27:47 m31 Exp $
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

package org.qedeq.kernel.parser;

import java.util.ArrayList;
import java.util.List;

import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.StringUtility;

/**
 * Parsed term.
 *
 * @version $Revision: 1.8 $
 * @author  Michael Meyling
 */
public class Term {

    /** This class. */
    private static final Class CLASS = Term.class;

    /** Operator, can be <code>null</code>. */
    private final Operator operator;

    /** Arguments, can be <code>null</code>. */
    private final List arguments;

    /** Atom, can be <code>null</code>. */
    private final TermAtom atom;

    /**
     * Constructor.
     *
     * @param   atom    Term atom.
     */
    public Term(final TermAtom atom) {
        this.operator = null;
        this.arguments = null;
        this.atom = atom;
    }


    /**
     * Constructor.
     *
     * @param   operator    Construct new term for this operator.
     */
    public Term(final Operator operator) {
        this.operator = operator;
        this.arguments = new ArrayList();
        this.atom = null;
    }

    /**
     * Constructor.
     *
     * @param   operator        Construct new term for this operator.
     * @param   firstArgument   First argument of operator.
     */
    public Term(final Operator operator, final Term firstArgument) {
        this.operator = operator;
        this.arguments = new ArrayList();
        this.atom = null;
        addArgument(firstArgument);
    }

    /**
     * Is this term an atom?
     *
     * @return  Is this term an atom?
     */
    public final boolean isAtom() {
        return atom != null;
    }

    /**
     * Add next argument term to operator. Overall number of arguments must
     * not exceed {@link Operator#getMax()} (if <code>>= 0</code>). Addition is only possible if
     * this is no atom term (see {@link #Term(TermAtom)}).
     *
     * @param   term    Add this argument at last position.
     * @throws  IllegalArgumentException    This is an atom term or argument
     *          maximum exceeded.
     */
    public final void addArgument(final Term term) {
        if (isAtom()) {
            throw new IllegalArgumentException(
                "this is an atom, no arguments could be added to " + atom.getValue());
        }
        if (operator.getMax() >= 0 && operator.getMax() < arguments.size() + 1) {
            throw new IllegalArgumentException("operator could have maximal "
                + operator.getMax() + " arguments");
        }
        arguments.add(term);
    }

    /**
     * Get operator of term. Can be <code>null</code> if this is an atom term.
     *
     * @return  Term operator.
     */
    public final Operator getOperator() {
        return operator;
    }

    /**
     * Get number of arguments of this operator.
     *
     * @return  Argument number.
     */
    public final int size() {
        if (arguments == null) {
            return 0;
        }
        return arguments.size();
    }

    /**
     * Get QEDEQ representation of this term.
     *
     * @return  QEDEQ representation.
     */
    public final String getQedeq() {
        if (isAtom()) {
            return atom.getValue();
        } else {
            final StringBuffer buffer = new StringBuffer();
            buffer.append(operator.getQedeq()).append('(');
            if (operator.getQedeqArgument() != null) {
                buffer.append(StringUtility.quote(operator.getQedeqArgument()));
            }
            for (int i = 0; i < arguments.size(); i++) {
                if (i > 0 || operator.getQedeqArgument() != null) {
                    buffer.append(", ");
                }
                buffer.append(((Term)
                    arguments.get(i)).getQedeq());
            }
            buffer.append(')');
            return buffer.toString();
        }
    }

    /**
     * Get QEDEQ XML representation of this term.
     *
     * @return  QEDEQ XML representation.
     */
    public final String getQedeqXml() {
        return getQedeqXml(0);
    }

    /**
     * Get QEDEQ XML representation of this term.
     *
     * @param   level   Tabulation level.
     * @return  QEDEQ XML representation.
     */
    private final String getQedeqXml(final int level) {
        if (isAtom()) {
            return StringUtility.getSpaces(level * 2) + atom.getValue() + "\n";
        } else {
            final StringBuffer buffer = new StringBuffer();
            buffer.append(StringUtility.getSpaces(level * 2));
            buffer.append("<").append(operator.getQedeq());
            if (operator.getQedeq().endsWith("VAR")) {  // TODO mime 20060612: ok for all QEDEQ?
                buffer.append(" id=" + quote(operator.getQedeqArgument()));
                if (arguments == null || arguments.size() == 0) {
                    buffer.append(" />" + "\n");
                    return buffer.toString();
                }
            } else if (operator.getQedeq().endsWith("CON")) {
                buffer.append(" ref=" + quote(operator.getQedeqArgument()));
                if (arguments == null || arguments.size() == 0) {
                    buffer.append(" />" + "\n");
                    return buffer.toString();
                }
            }

            buffer.append(">\n");
            if (operator.getQedeqArgument() != null && !operator.getQedeq().endsWith("VAR")
                    && !operator.getQedeq().endsWith("CON")) {
                // no arguments expected!
                Trace.fatal(CLASS, this, "getQedeqXml(int)", "operator argument is not null but: "
                    + operator.getQedeqArgument(), new IllegalArgumentException());
            }
            for (int i = 0; i < arguments.size(); i++) {
                buffer.append(((Term)
                    arguments.get(i)).getQedeqXml(level + 1));
            }
            buffer.append(StringUtility.getSpaces(level * 2));
            buffer.append("</").append(operator.getQedeq()).append(">\n");
            return buffer.toString();
        }
    }

    /**
     * Quote attribute value.
     *
     * @param   text    Attribute text.
     * @return  Quoted attribute.
     */
    private String quote(final String text) {
        return "\"" + StringUtility.replace(text, "\"", "&quot;") + "\"";
    }

}
