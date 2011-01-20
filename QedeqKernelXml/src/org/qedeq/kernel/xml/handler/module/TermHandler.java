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

import org.qedeq.kernel.se.dto.module.TermVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.handler.list.ElementHandler;
import org.qedeq.kernel.xml.parser.AbstractSimpleHandler;
import org.qedeq.kernel.xml.parser.SimpleAttributes;


/**
 * Parse term.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class TermHandler extends AbstractSimpleHandler {

    /** Value object for term. */
    private TermVo term;

    /** Handles {@link org.qedeq.kernel.se.base.list.Element}s. */
    private final ElementHandler elementHandler;


    /**
     * Handles terms.
     *
     * @param   handler     Parent handler.
     */
    public TermHandler(final AbstractSimpleHandler handler) {
        super(handler, "TERM");
        elementHandler = new ElementHandler(this);
    }

    public final void init() {
        term = null;
    }

    /**
     * Get parsed result.
     *
     * @return  Term.
     */
    public final TermVo getTerm() {
        return term;
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
            term = new TermVo(elementHandler.getElement());
        } else if (getLevel() <= 1) {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }
}
