/* $Id: LiteratureItemHandler.java,v 1.1 2008/07/26 08:00:51 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2009,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.kernel.dto.module.LiteratureItemVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.parser.AbstractSimpleHandler;
import org.qedeq.kernel.xml.parser.SimpleAttributes;


/**
 * Handle bibliography entry.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public  class LiteratureItemHandler extends AbstractSimpleHandler {

    /** Tag for this handler. */
    public static final String ITEM_TAG = "ITEM";

    /** Handle reference texts. */
    private final LatexListHandler itemHandler;

    /** Value object. */
    private LiteratureItemVo literatureItem;


    /**
     * Constructor.
     *
     * @param handler
     *            Parent handler.
     */
    public LiteratureItemHandler(final AbstractSimpleHandler handler) {
        super(handler, ITEM_TAG);
        itemHandler = new LatexListHandler(this, ITEM_TAG);
    }

    public final void init() {
        literatureItem = null;
    }

    /**
     * Get section.
     *
     * @return  Section.
     */
    public final LiteratureItemVo getLiteratureItem() {
        return literatureItem;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            literatureItem = new LiteratureItemVo();
            literatureItem.setLabel(attributes.getString("label"));
            changeHandler(itemHandler, name, attributes);
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            literatureItem.setItem(itemHandler.getLatexList());
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }


}
