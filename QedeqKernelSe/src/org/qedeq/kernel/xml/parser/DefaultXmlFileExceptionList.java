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

import org.qedeq.kernel.common.SyntaxException;
import org.qedeq.kernel.common.SyntaxExceptionList;
import org.qedeq.kernel.common.XmlFileException;
import org.qedeq.kernel.common.XmlFileExceptionList;
import org.xml.sax.SAXException;


/**
 * Type save {@link org.qedeq.kernel.common.XmlFileException} list.
 *
 * @version $Revision: 1.4 $
 * @author  Michael Meyling
 */
public class DefaultXmlFileExceptionList extends XmlFileExceptionList {

    /** List with parse exceptions. */
    private final List exceptions = new ArrayList();

    /**
     * Constructor.
     */
    public DefaultXmlFileExceptionList() {
    }

    /**
     * Constructor.
     *
     * @param   e   Wrap me.
     */
    public DefaultXmlFileExceptionList(final IOException e) {
        initCause(e);
        add(new XmlFileException(e));
    }

    /**
     * Constructor.
     *
     * @param   e   Wrap me.
     */
    public DefaultXmlFileExceptionList(final SAXException e) {
        initCause(e);
        add(new XmlFileException(e));
    }

    /**
     * Constructor.
     *
     * @param   e   Wrap me.
     */
    public DefaultXmlFileExceptionList(final RuntimeException e) {
        initCause(e);
        add(new XmlFileException(e));
    }

    /**
     * Constructor.
     *
     * @param   e   Wrap me.
     */
    public DefaultXmlFileExceptionList(final Throwable e) {
        initCause(e);
        add(new XmlFileException(e));
    }

    /**
     * Constructor.
     *
     * @param   exceptionList   Syntax exceptions that are wrapped into {@link XmlFileException}.
     */
    public DefaultXmlFileExceptionList(final SyntaxExceptionList exceptionList) {
        for (int i = 0; i < exceptionList.size(); i++) {
            if (i == 0) {
                initCause(exceptionList.get(0));
            }
            add(exceptionList.get(i));
        }
    }

    /**
     * Add exception.
     *
     * @param   e   Exception to add.
     */
    public void add(final XmlFileException e) {
        exceptions.add(e);
    }

    /**
     * Add exception.
     *
     * @param   e   Exception to add.
     */
    public final void add(final SyntaxException e) {
        exceptions.add(new XmlFileException(e));
    }

    /**
     * Add exception.
     *
     * @param   e   Exception to add.
     */
    public final void add(final SAXException e) {
        exceptions.add(new XmlFileException(e));
    }

    /**
     * Add exception.
     *
     * @param   e   Exception to add.
     */
    public final void add(final IOException e) {
        exceptions.add(new XmlFileException(e));
    }

    /**
     * Add exception.
     *
     * @param   e   Exception to add.
     */
    public void add(final ParserConfigurationException e) {
        exceptions.add(new XmlFileException(e));
    }

    /**
     * Add exception.
     *
     * @param   e   Exception to add.
     */
    public void add(final RuntimeException e) {
        exceptions.add(new XmlFileException(e));
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
    public final XmlFileException get(final int i) {
        return (XmlFileException) exceptions.get(i);
    }

    /**
     * Get all exceptions.
     *
     * @return  All exceptions.
     */
    public final XmlFileException[] toArray() {
        return (XmlFileException[]) exceptions.toArray(new XmlFileException[0]);
    }

    public String getMessage() {
        final StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < size(); i++) {
            if (i != 0) {
                buffer.append("\n");
            }
            final XmlFileException e = get(i);
            buffer.append(i).append(": ");
            buffer.append(e.toString());
        }
        return buffer.toString();
    }

}
