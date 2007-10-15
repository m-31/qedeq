/* $Id: SyntaxExceptionList.java,v 1.1 2007/04/12 23:50:10 m31 Exp $
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

package org.qedeq.kernel.common;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * Type save SyntaxException list.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public final class SyntaxExceptionList {

    /** List with parse exceptions. */
    private List exceptions;

    /** Maximum number of exceptions. */
    public static final int MAXIMUM = 10;

    /**
     * Constructor.
     */
    public SyntaxExceptionList() {
        exceptions = new ArrayList(MAXIMUM);
    }

    /**
     * Add Exception. This is only done if the number of already collected exceptions is below
     * {@link #MAXIMUM}.
     *
     * @param   e   Exception to add.
     * @throws  SAXException    Maximum number of exception reached.
     */
    public final void add(final SyntaxException e) throws  SAXException {
        if (e == null) {
            final NullPointerException ex = new NullPointerException("Exception expected!");
            exceptions.add(ex);
            throw ex;
        }
        if (size() < MAXIMUM) {
            exceptions.add(e);
        } else {
            throw new SAXException("Too much errors already occurred.");
        }
    }

    /**
     * Get number of collected exceptions.
     *
     * @return  Number of collected exceptions.
     */
    public final int size() {
        return exceptions.size();
    }

    /**
     * Get <code>i</code>-th exception.
     *
     * @param   i   Starts with 0 and must be smaller than {@link #size()}.
     * @return  Wanted exception.
     */
    public final SyntaxException get(final int i) {
        return (SyntaxException) exceptions.get(i);
    }

    /**
     * Get all exceptions.
     *
     * @return  All exceptions.
     */
    public final SyntaxException[] toArray() {
        return (SyntaxException[]) exceptions.toArray(new SyntaxException[0]);
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < size(); i++) {
            if (i != 0) {
                buffer.append("\n");
            }
            final Exception e = get(i);
            if (e instanceof SAXParseException) {
                final SAXParseException ex = (SAXParseException) e;
                buffer.append(ex.getSystemId() != null ? ex.getPublicId() + " " : "");
                buffer.append(ex.getSystemId() != null ? ex.getSystemId() + " " : "");
                buffer.append(ex.getLineNumber() != -1 ? "Row: " + ex.getLineNumber() + ". " : "");
                buffer.append(ex.getColumnNumber() != -1
                    ? "Column: " + ex.getColumnNumber() + ". "
                    : "");
                buffer.append((ex.getException() != null
                    ? ex.getException().getMessage()
                    : ex.getMessage()));
            } else {
                buffer.append(get(i));
            }
        }
        return buffer.toString();
    }

}
