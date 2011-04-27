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

import org.qedeq.kernel.se.dto.module.ChapterVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.handler.common.AbstractSimpleHandler;
import org.qedeq.kernel.xml.handler.common.SimpleAttributes;

/**
 * Handles a chapter.
 *
 * @author  Michael Meyling
 */
public class ChapterHandler extends AbstractSimpleHandler {

    /** Handles chapter title information. */
    private final LatexListHandler titleHandler;

    /** Handles chapter introduction. */
    private final LatexListHandler introductionHandler;

    /** Handles sections. */
    private final SectionHandler sectionHandler;

    /** Chapter value object. */
    private ChapterVo chapter;

    /**
     * Constructor.
     *
     * @param   handler Parent handler.
     */
    public ChapterHandler(final AbstractSimpleHandler handler) {
        super(handler, "CHAPTER");
        titleHandler = new LatexListHandler(this, "TITLE");
        introductionHandler = new LatexListHandler(this, "INTRODUCTION");
        sectionHandler = new SectionHandler(this);
    }

    public void init() {
        chapter = null;
    }

    /**
     * Get parsed chapter.
     *
     * @return  Chapter.
     */
    public final ChapterVo getChapter() {
        return chapter;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            chapter = new ChapterVo();
            chapter.setNoNumber(attributes.getBoolean("noNumber"));
        } else if (titleHandler.getStartTag().equals(name)) {
            changeHandler(titleHandler, name, attributes);
        } else if (introductionHandler.getStartTag().equals(name)) {
            changeHandler(introductionHandler, name, attributes);
        } else if (sectionHandler.getStartTag().equals(name)) {
            changeHandler(sectionHandler, name, attributes);
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // nothing to do
        } else if (titleHandler.getStartTag().equals(name)) {
            chapter.setTitle(titleHandler.getLatexList());
        } else if (introductionHandler.getStartTag().equals(name)) {
            chapter.setIntroduction(introductionHandler.getLatexList());
        } else if (sectionHandler.getStartTag().equals(name)) {
            chapter.addSection(sectionHandler.getSection());
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }


}
