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

package org.qedeq.kernel.xml.handler.module;

import org.qedeq.kernel.se.dto.module.ModusPonensVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.handler.common.AbstractSimpleHandler;
import org.qedeq.kernel.xml.handler.common.SimpleAttributes;


/**
 * Parse a Modus Ponens usage.
 *
 * @author  Michael Meyling
 */
public class ModusPonensHandler extends AbstractSimpleHandler {

    /** Rule value object. */
    private ModusPonensVo modusPonens;

    /**
     * Deals with definitions.
     *
     * @param   handler Parent handler.
     */
    public ModusPonensHandler(final AbstractSimpleHandler handler) {
        super(handler, "MP");
    }

    public final void init() {
        modusPonens = null;
    }

    /**
     * Get Modus Ponens usage.
     *
     * @return  Modus Ponens usage.
     */
    public final ModusPonensVo getModusPonensVo() {
        return modusPonens;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            modusPonens = new ModusPonensVo(attributes.getString("ref1"),
                attributes.getString("ref2"));
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
