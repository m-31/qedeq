/* $Id: DefaultXmlFileExceptionList.java,v 1.4 2007/10/07 16:40:12 m31 Exp $
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

package org.qedeq.kernel.xml.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.qedeq.kernel.common.SourceArea;
import org.qedeq.kernel.common.SourceFileException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.xml.sax.SAXException;


/**
 * Type save {@link org.qedeq.kernel.common.SourceFileException} list.
 *
 * @version $Revision: 1.4 $
 * @author  Michael Meyling
 */
public class DefaultSourceFileExceptionList extends SourceFileExceptionList {

    /** List with parse exceptions. */
    private final List exceptions = new ArrayList();

    /**
     * Constructor.
     */
    public DefaultSourceFileExceptionList() {
    }

    /**
     * Constructor.
     *
     * @param   e   Wrap me.
     */
    public DefaultSourceFileExceptionList(final IOException e) {
        initCause(e);
        add(new SourceFileException(e));
    }

    /**
     * Constructor.
     *
     * @param   e   Wrap me.
     */
    public DefaultSourceFileExceptionList(final SAXException e) {
        initCause(e);
        add(new SourceFileException(e));
    }

    /**
     * Constructor.
     *
     * @param   e   Wrap me.
     */
    public DefaultSourceFileExceptionList(final RuntimeException e) {
        initCause(e);
        add(new SourceFileException(e));
    }

    /**
     * Constructor.
     *
     * @param   e   Wrap me.
     */
    public DefaultSourceFileExceptionList(final Throwable e) {
        initCause(e);
        add(new SourceFileException(e));
    }

    /**
     * Add exception.
     *
     * @param   e   Exception to add.
     */
    public void add(final SourceFileException e) {
        exceptions.add(e);
    }

    /**
     * Add exception.
     *
     * @param   e   Exception to add.
     */
    public final void add(final XmlSyntaxException e) {
        final SourceFileException sfe = new SourceFileException(e.getErrorCode(), e.getMessage(), e,
            new SourceArea(e.getErrorPosition().getAddress(), e.getErrorPosition(), null), null);

        exceptions.add(sfe);
    }

    /**
     * Add exception.
     *
     * @param   e   Exception to add.
     */
    public final void add(final SAXException e) {
        exceptions.add(new SourceFileException(e));
    }

    /**
     * Add exception.
     *
     * @param   e   Exception to add.
     */
    public final void add(final IOException e) {
        exceptions.add(new SourceFileException(e));
    }

    /**
     * Add exception.
     *
     * @param   e   Exception to add.
     */
    public void add(final ParserConfigurationException e) {
        exceptions.add(new SourceFileException(e));
    }

    /**
     * Add exception.
     *
     * @param   e   Exception to add.
     */
    public void add(final RuntimeException e) {
        exceptions.add(new SourceFileException(e));
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
    public final SourceFileException get(final int i) {
        return (SourceFileException) exceptions.get(i);
    }

    /**
     * Get all exceptions.
     *
     * @return  All exceptions.
     */
    public final SourceFileException[] toArray() {
        return (SourceFileException[]) exceptions.toArray(new SourceFileException[0]);
    }

    public String getMessage() {
        final StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < size(); i++) {
            if (i != 0) {
                buffer.append("\n");
            }
            final SourceFileException e = get(i);
            buffer.append(i).append(": ");
            buffer.append(e.toString());
        }
        return buffer.toString();
    }

}
