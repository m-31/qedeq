/* $Id: SubsectionHandler.java,v 1.1 2008/07/26 08:00:51 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2008,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.kernel.base.module.Subsection;
import org.qedeq.kernel.dto.module.SubsectionVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.parser.AbstractSimpleHandler;
import org.qedeq.kernel.xml.parser.SimpleAttributes;


/**
 * Parses subsection data.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class SubsectionHandler extends AbstractSimpleHandler {

    /** Handler for subsection title. */
    private final LatexListHandler titleHandler;

    /** Handler for subsection text. */
    private final LatexListHandler latexHandler;

    /** Subsection. */
    private SubsectionVo subsection;


    /**
     * Constructor.
     *
     * @param   handler Parent handler.
     */
    public SubsectionHandler(final AbstractSimpleHandler handler) {
        super(handler, "SUBSECTION");
        titleHandler = new LatexListHandler(this, "TITLE");
        latexHandler = new LatexListHandler(this, "TEXT");
        subsection = new SubsectionVo();
    }

    public final void init() {
        subsection = null;
    }

    /**
     * Get subsection.
     *
     * @return  Subsection.
     */
    public final Subsection getSubsection() {
        return subsection;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            subsection = new SubsectionVo();
            subsection.setId(attributes.getString("label"));
            subsection.setLevel(attributes.getString("level"));
        } else if (titleHandler.getStartTag().equals(name)) {
            changeHandler(titleHandler, name, attributes);
        } else if (latexHandler.getStartTag().equals(name)) {
            changeHandler(latexHandler, name, attributes);
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // thats why we handle it
        } else if (titleHandler.getStartTag().equals(name)) {
            subsection.setTitle(titleHandler.getLatexList());
        } else if (latexHandler.getStartTag().equals(name)) {
            subsection.setLatex(latexHandler.getLatexList());
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

}
