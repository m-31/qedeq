/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.xml.handler.list;

import java.util.ArrayList;
import java.util.List;

import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.handler.common.AbstractSimpleHandler;
import org.qedeq.kernel.xml.handler.common.SaxDefaultHandler;
import org.qedeq.kernel.xml.handler.common.SimpleAttributes;

/**
 * Parse unknown number of elements.
 *
 * @author  Michael Meyling
 */
public class BasicHandler extends AbstractSimpleHandler {

    /** Our parsed elements. */
    private List elements;

    /** Handles {@link org.qedeq.kernel.se.base.list.Element}s. */
    private final ElementHandler elementHandler;


    /**
     * Deals with elements.
     *
     * @param   handler Parent handler.
     */
    public BasicHandler(final SaxDefaultHandler handler) {
        super(handler, "basic");
        elements = new ArrayList();
        elementHandler = new ElementHandler(this);
    }

    /**
     * Handles formulas.
     *
     * @param   handler     Parent handler.
     */
    public BasicHandler(final AbstractSimpleHandler handler) {
        super(handler, "basic");
        elements = new ArrayList();
        elementHandler = new ElementHandler(this);
    }

    public final void init() {
        elements.clear();
    }

    /**
     * Get parsed result.
     *
     * @return  List of elements.
     */
    public final List getElements() {
        return elements;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // nothing to do
        } else if (getLevel() > 1) {
            changeHandler(elementHandler, name, attributes);
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // nothing to do, we are ready!
        } else if (getLevel() <= 1) {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        } else {
            elements.add(elementHandler.getElement());
        }
    }
}
