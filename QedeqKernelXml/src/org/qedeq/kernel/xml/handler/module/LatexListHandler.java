/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2010,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.kernel.se.dto.module.LatexListVo;
import org.qedeq.kernel.se.dto.module.LatexVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.parser.AbstractSimpleHandler;
import org.qedeq.kernel.xml.parser.SimpleAttributes;

/**
 * Parse header informations.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class LatexListHandler extends AbstractSimpleHandler {

    /** Text language. */
    private String language;

    /** LaTeX text. */
    private LatexListVo list;

    /** Character data. */
    private String data;

    /**
     * Handles LaTeX tags.
     *
     * @param   handler     Handler that uses this handler.
     * @param   startTag    This is the starting tag.
     */
    public LatexListHandler(final AbstractSimpleHandler handler, final String startTag) {
        super(handler, startTag);
    }

    public final void init() {
        list = new LatexListVo();
    }

    /**
     * Get parsed result.
     *
     * @return  LaTeX text parts.
     */
    public final LatexListVo getLatexList() {
        return list;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // ignore
        } else if ("LATEX".equals(name)) {
            this.data = null;
            language = attributes.getString("language");
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // ignore
        } else if ("LATEX".equals(name)) {
            list.add(new LatexVo(language, data));
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void characters(final String name, final String data) {
        if ("LATEX".equals(name)) {
            this.data = data;
        } else {
            throw new RuntimeException("Unexpected character data in tag: " + name);
        }
    }

}
