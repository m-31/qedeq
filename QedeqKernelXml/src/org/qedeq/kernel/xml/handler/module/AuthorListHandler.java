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

package org.qedeq.kernel.xml.handler.module;

import org.qedeq.kernel.se.dto.module.AuthorListVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.handler.common.AbstractSimpleHandler;
import org.qedeq.kernel.xml.handler.common.SimpleAttributes;

/**
 * Parse author list.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class AuthorListHandler extends AbstractSimpleHandler {

     /** List of authors. */
    private AuthorListVo list;

    /** Parses an author. */
    private final AuthorHandler authorHandler;


    /**
     * Handles list of authors.
     *
     * @param   handler Parent handler.
     */
    public AuthorListHandler(final AbstractSimpleHandler handler) {
        super(handler, "AUTHORS");
        authorHandler = new AuthorHandler(this);
    }

    public final void init() {
        list = new AuthorListVo();
    }

    /**
     * Get list of authors.
     *
     * @return  Author list.
     */
    public final AuthorListVo getAuthorList() {
        return list;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // nothing to do
        } else if (authorHandler.getStartTag().equals(name)) {
            changeHandler(authorHandler, name, attributes);
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // nothing to do
        } else if (authorHandler.getStartTag().equals(name)) {
            list.add(authorHandler.getAuthor());
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }
}
