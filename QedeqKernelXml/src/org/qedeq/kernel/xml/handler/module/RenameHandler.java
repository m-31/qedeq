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

import org.qedeq.kernel.se.dto.module.RenameVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.handler.common.AbstractSimpleHandler;
import org.qedeq.kernel.xml.handler.common.SimpleAttributes;
import org.qedeq.kernel.xml.handler.list.ElementHandler;


/**
 * Parse a Rename Bound Subject Variable Rule usage.
 *
 * @author  Michael Meyling
 */
public class RenameHandler extends AbstractSimpleHandler {

    /** Rule value object. */
    private RenameVo rename;

    /** Handle subject variables. */
    private final ElementHandler subjectVariableHandler;

    /**
     * Deals with definitions.
     *
     * @param   handler Parent handler.
     */
    public RenameHandler(final AbstractSimpleHandler handler) {
        super(handler, "RENAME");
        subjectVariableHandler = new ElementHandler(this);
    }

    public final void init() {
        rename = null;
    }

    /**
     * Get Rename Bound Subject Variable Rule usage.
     *
     * @return  Substitute Free Variable usage.
     */
    public final RenameVo getRenameVo() {
        return rename;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            rename = new RenameVo();
            final String ref = attributes.getString("ref");
            if (ref != null) {
                rename.setReference(ref);
            }
            final Integer occurrence = attributes.getInteger("occurrence");
            if (occurrence != null) {
                rename.setOccurrence(occurrence.intValue());
            }
        } else if ("VAR".equals(name)) {
            changeHandler(subjectVariableHandler, name, attributes);
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // nothing to do
        } else if ("VAR".equals(name)) {
            if (rename != null && rename.getOriginalSubjectVariable() == null) {
                rename.setOriginalSubjectVariable(subjectVariableHandler.getElement());
            } else if (rename != null) {
                rename.setReplacementSubjectVariable(subjectVariableHandler.getElement());
            }
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

}
