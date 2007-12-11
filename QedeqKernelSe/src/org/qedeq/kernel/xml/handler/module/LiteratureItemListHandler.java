/* $Id: LiteratureItemListHandler.java,v 1.5 2007/05/10 00:37:50 m31 Exp $
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

package org.qedeq.kernel.xml.handler.module;

import org.qedeq.kernel.dto.module.LiteratureItemListVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.parser.AbstractSimpleHandler;
import org.qedeq.kernel.xml.parser.SimpleAttributes;

/**
 * Parse literature list.
 *
 * @version $Revision: 1.5 $
 * @author  Michael Meyling
 */
public class LiteratureItemListHandler extends AbstractSimpleHandler {

     /** List of literatureItems. */
    private LiteratureItemListVo list;

    /** Parses an literatureItem. */
    private final LiteratureItemHandler literatureItemHandler;


    /**
     * Handles list of literatureItems.
     *
     * @param   handler Parent handler.
     */
    public LiteratureItemListHandler(final AbstractSimpleHandler handler) {
        super(handler, "BIBLIOGRAPHY");
        literatureItemHandler = new LiteratureItemHandler(this);
    }

    public final void init() {
        list = new LiteratureItemListVo();
    }

    /**
     * Get list of literatureItems.
     *
     * @return  LiteratureItem list.
     */
    public final LiteratureItemListVo getLiteratureItemList() {
        return list;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // nothing to do
        } else if (literatureItemHandler.getStartTag().equals(name)) {
            changeHandler(literatureItemHandler, name, attributes);
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // nothing to do
        } else if (literatureItemHandler.getStartTag().equals(name)) {
            list.add(literatureItemHandler.getLiteratureItem());
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }
}
