/* This file is part of the project "Hilbert II" - http://www.qedeq.org
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

package org.qedeq.kernel.xml.handler.list;

import java.util.ArrayList;
import java.util.List;

import org.qedeq.kernel.base.list.Element;
import org.qedeq.kernel.base.list.ElementList;
import org.qedeq.kernel.dto.list.DefaultAtom;
import org.qedeq.kernel.dto.list.DefaultElementList;
import org.qedeq.kernel.xml.parser.AbstractSimpleHandler;
import org.qedeq.kernel.xml.parser.SaxDefaultHandler;
import org.qedeq.kernel.xml.parser.SimpleAttributes;


/**
 * Parse elements. For example formulas and terms are build of
 * {@link org.qedeq.kernel.base.list.Element}s.
 * <P>
 * This handler knows nothing about special forms. It doesn't do any
 * validating. It simply puts all attributes into string atoms and
 * adds all sub elements. The element name is taken for the operator name.
 *
 * @version $Revision: 1.1 $
 * @author Michael Meyling
 */
public class ElementHandler extends AbstractSimpleHandler {

    /** Value object element. */
    private Element result;

    /** Element stack. */
    private final List elements;


    /**
     * Deals with elements.
     *
     * @param   handler Parent handler.
     */
    public ElementHandler(final AbstractSimpleHandler handler) {
        super(handler);
        elements = new ArrayList(20);
    }

    /**
     * Deals with elements.
     *
     * @param   handler Parent handler.
     */
    public ElementHandler(final SaxDefaultHandler handler) {
        super(handler);
        elements = new ArrayList(20);
    }

    public final void init() {
        result = null;
        elements.clear();
    }

    /**
     * Get parsed element.
     *
     * @return  Parsed element.
     */
    public final Element getElement() {
        return result;
    }

    public final void startElement(final String name, final SimpleAttributes attributes) {
        final String[] values = attributes.getKeySortedStringValues();
        final ElementList element = new DefaultElementList(name, new Element[0]);
        for (int i = 0; i < values.length; i++) {
            element.add(new DefaultAtom(values[i]));
        }
        elements.add(element);
    }

    public final void endElement(final String name) {
        ElementList last = (ElementList) elements.get(elements.size() - 1);
        elements.remove(elements.size() - 1);
        if (elements.size() > 0) {
            ((ElementList) elements.get(elements.size() - 1)).add(last);
        } else {
            result = last;
        }
    }

    public final void characters(final String name, final String data) {
        ElementList last = (ElementList) elements.get(elements.size() - 1);
        last.add(new DefaultAtom(data));
    }

}
