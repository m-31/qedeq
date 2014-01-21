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

import org.qedeq.kernel.se.dto.module.SubsectionListVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.handler.common.AbstractSimpleHandler;
import org.qedeq.kernel.xml.handler.common.SimpleAttributes;

/**
 * Parse subsection list.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class SubsectionListHandler extends AbstractSimpleHandler {

     /** List of subsections. */
    private SubsectionListVo list;

    /** Parses an subsection. */
    private final SubsectionHandler subsectionHandler;

    /** Handle single node of a section. */
    private final NodeHandler nodeHandler;


    /**
     * Handles list of subsections.
     *
     * @param   handler Parent handler.
     */
    public SubsectionListHandler(final AbstractSimpleHandler handler) {
        super(handler, "SUBSECTIONS");
        subsectionHandler = new SubsectionHandler(this);
        nodeHandler = new NodeHandler(this);
    }

    public final void init() {
        list = new SubsectionListVo();
    }

    /**
     * Get list of subsections.
     *
     * @return  Subsection list.
     */
    public final SubsectionListVo getSubsectionList() {
        return list;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // nothing to do
        } else if (subsectionHandler.getStartTag().equals(name)) {
            changeHandler(subsectionHandler, name, attributes);
        } else if (nodeHandler.getStartTag().equals(name)) {
            changeHandler(nodeHandler, name, attributes);
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // nothing to do
        } else if (subsectionHandler.getStartTag().equals(name)) {
            list.add(subsectionHandler.getSubsection());
        } else if (nodeHandler.getStartTag().equals(name)) {
            list.add(nodeHandler.getNode());
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }
}
