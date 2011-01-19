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

package org.qedeq.kernel.xml.handler.module;

import org.qedeq.kernel.se.dto.module.AuthorVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.parser.AbstractSimpleHandler;
import org.qedeq.kernel.xml.parser.SimpleAttributes;

/**
 * Parse author list.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class AuthorHandler extends AbstractSimpleHandler {

     /** Author. */
    private AuthorVo author;

    /** Email address of one author. */
    private String email;

    /** Parses the name of the author. */
    private final LatexHandler authorNameHandler;


    /**
     * Handles list of authors.
     *
     * @param   handler Parent handler.
     */
    public AuthorHandler(final AbstractSimpleHandler handler) {
        super(handler, "AUTHOR");
        authorNameHandler = new LatexHandler(this, "NAME");
    }

    public final void init() {
        author = null;
        email = null;
    }

    /**
     * Get author.
     *
     * @return  Author.
     */
    public final AuthorVo getAuthor() {
        return author;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            email = attributes.getString("email");
        } else if (authorNameHandler.getStartTag().equals(name)) {
            changeHandler(authorNameHandler, name, attributes);
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // nothing to do
        } else if (authorNameHandler.getStartTag().equals(name)) {
            author = new AuthorVo(authorNameHandler.getLatex(), email);
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }
}
