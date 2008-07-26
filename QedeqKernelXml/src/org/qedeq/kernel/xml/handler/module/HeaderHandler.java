/* $Id: HeaderHandler.java,v 1.1 2008/07/26 08:00:51 m31 Exp $
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

package org.qedeq.kernel.xml.handler.module;

import org.qedeq.kernel.dto.module.HeaderVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.parser.AbstractSimpleHandler;
import org.qedeq.kernel.xml.parser.SimpleAttributes;


/**
 * Parse header informations.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class HeaderHandler extends AbstractSimpleHandler {

    /** Value object for module header. */
    private HeaderVo header;

    /** Handler for module specification. */
    private final SpecificationHandler specificationHandler;

    /** Handler for module title. */
    private final LatexListHandler titleHandler;

    /** Handler for module abstract. */
    private final LatexListHandler abstractHandler;

    /** Handler for list of module authors. */
    private final AuthorListHandler authorListHandler;

    /** Handler for list of module imports. */
    private final ImportListHandler importListHandler;

    /** Handler for list of modules that need this one. */
    private final UsedByListHandler usedbyListHandler;


    /**
     * Deals with header of qedeq file.
     *
     * @param   handler Parent handler.
     */
    public HeaderHandler(final AbstractSimpleHandler handler) {
        super(handler, "HEADER");
        titleHandler = new LatexListHandler(this, "TITLE");
        abstractHandler = new LatexListHandler(this, "ABSTRACT");
        specificationHandler = new SpecificationHandler(this);
        authorListHandler = new AuthorListHandler(this);
        importListHandler = new ImportListHandler(this);
        usedbyListHandler = new UsedByListHandler(this);
    }

    public final void init() {
        header = null;
    }

    /**
     * Get header of qedeq module.
     *
     * @return  Header of qedeq module.
     */
    public final HeaderVo getHeader() {
        return header;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            header = new HeaderVo();
            header.setEmail(attributes.getString("email"));
        } else if (specificationHandler.getStartTag().equals(name)) {
            changeHandler(specificationHandler, name, attributes);
        } else if (titleHandler.getStartTag().equals(name)) {
            changeHandler(titleHandler, name, attributes);
        } else if (abstractHandler.getStartTag().equals(name)) {
            changeHandler(abstractHandler, name, attributes);
        } else if (authorListHandler.getStartTag().equals(name)) {
            changeHandler(authorListHandler, name, attributes);
        } else if (importListHandler.getStartTag().equals(name)) {
            changeHandler(importListHandler, name, attributes);
        } else if (usedbyListHandler.getStartTag().equals(name)) {
            changeHandler(usedbyListHandler, name, attributes);
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // nothing to do
        } else if (specificationHandler.getStartTag().equals(name)) {
            header.setSpecification(specificationHandler.getSpecification());
        } else if (titleHandler.getStartTag().equals(name)) {
            header.setTitle(titleHandler.getLatexList());
        } else if (abstractHandler.getStartTag().equals(name)) {
            header.setSummary(abstractHandler.getLatexList());
        } else if (authorListHandler.getStartTag().equals(name)) {
            header.setAuthorList(authorListHandler.getAuthorList());
        } else if (importListHandler.getStartTag().equals(name)) {
            header.setImportList(importListHandler.getImportList());
        } else if (usedbyListHandler.getStartTag().equals(name)) {
            header.setUsedByList(usedbyListHandler.getUsedByList());
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

}
