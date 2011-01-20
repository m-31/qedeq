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

import org.qedeq.kernel.se.base.module.Reason;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.parser.AbstractSimpleHandler;
import org.qedeq.kernel.xml.parser.SimpleAttributes;


/**
 * Handles rule usage in formal proof lines.
 *
 * @author  Michael Meyling
 */
public class ReasonHandler extends AbstractSimpleHandler {

    /** Handler for Modus Ponens usage. */
    private final ModusPonensHandler modusPonensHandler;

    /** Node value object. */
    private Reason reason;


    /**
     * Constructor.
     *
     * @param   handler Parent handler.
     */
    public ReasonHandler(final AbstractSimpleHandler handler) {
        super(handler, "REASON");
        modusPonensHandler = new ModusPonensHandler(this);
    }

    public final void init() {
        reason = null;
    }

    /**
     * Get reason.
     *
     * @return  Reason.
     */
    public final Reason getReason() {
        return reason;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (modusPonensHandler.getStartTag().equals(name)) {
            changeHandler(modusPonensHandler, name, attributes);
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public void endElement(final String name) throws XmlSyntaxException {
        if (modusPonensHandler.getStartTag().equals(name)) {
            reason = modusPonensHandler.getModusPonensVo();
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
       }
    }

}
