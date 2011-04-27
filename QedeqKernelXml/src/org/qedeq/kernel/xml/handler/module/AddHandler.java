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

import org.qedeq.kernel.se.dto.module.AddVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.handler.common.AbstractSimpleHandler;
import org.qedeq.kernel.xml.handler.common.SimpleAttributes;


/**
 * Parse an Addition usage.
 *
 * @author  Michael Meyling
 */
public class AddHandler extends AbstractSimpleHandler {

    /** Rule value object. */
    private AddVo add;

    /**
     * Deals with definitions.
     *
     * @param   handler Parent handler.
     */
    public AddHandler(final AbstractSimpleHandler handler) {
        super(handler, "ADD");
    }

    public final void init() {
        add = null;
    }

    /**
     * Get Addition usage.
     *
     * @return  Addition usage.
     */
    public final AddVo getAddVo() {
        return add;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            add = new AddVo(attributes.getString("ref"));
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // nothing to do
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

}
