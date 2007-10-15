/* $Id: SectionHandler.java,v 1.15 2007/05/10 00:37:50 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2007,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.kernel.common.SyntaxException;
import org.qedeq.kernel.dto.module.SectionVo;
import org.qedeq.kernel.xml.parser.AbstractSimpleHandler;
import org.qedeq.kernel.xml.parser.SimpleAttributes;


/**
 * Handle sections.
 *
 * @version $Revision: 1.15 $
 * @author  Michael Meyling
 */
public class SectionHandler extends AbstractSimpleHandler {

    /** Tag for introduction part. */
    public static final String INTRODUCTION_TAG = "INTRODUCTION";

    /** Tag for section part. */
    public static final String SECTION_TAG = "SECTION";

    /** Tag for title part. */
    public static final String TITLE_TAG = "TITLE";

    /** Handle section title. */
    private final LatexListHandler titleHandler;

    /** Handle section introduction. */
    private final LatexListHandler introductionHandler;

    /** Handle single subsection. */
    private final SubsectionListHandler subsectionListHandler;

    /** Section value object. */
    private SectionVo section;


    /**
     * Constructor.
     *
     * @param handler
     *            Parent handler.
     */
    public SectionHandler(final AbstractSimpleHandler handler) {
        super(handler, SECTION_TAG);
        titleHandler = new LatexListHandler(this, TITLE_TAG);
        introductionHandler = new LatexListHandler(this, INTRODUCTION_TAG);
        subsectionListHandler = new SubsectionListHandler(this);
    }

    public final void init() {
        section = null;
    }

    /**
     * Get section.
     *
     * @return  Section.
     */
    public final SectionVo getSection() {
        return section;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws SyntaxException {
        if (getStartTag().equals(name)) {
            section = new SectionVo();
            section.setNoNumber(attributes.getBoolean("noNumber"));
        } else if (TITLE_TAG.equals(name)) {
            changeHandler(titleHandler, name, attributes);
        } else if (INTRODUCTION_TAG.equals(name)) {
            changeHandler(introductionHandler, name, attributes);
        } else if ("SUBSECTIONS".equals(name)) {
            changeHandler(subsectionListHandler, name, attributes);
        } else {
            throw SyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void endElement(final String name) throws SyntaxException {
        if (getStartTag().equals(name)) {
            // nothing to do
        } else if (TITLE_TAG.equals(name)) {
            section.setTitle(titleHandler.getLatexList());
        } else if (INTRODUCTION_TAG.equals(name)) {
            section.setIntroduction(introductionHandler.getLatexList());
        } else if ("SUBSECTIONS".equals(name)) {
            section.setSubsectionList(subsectionListHandler.getSubsectionList());
        } else {
            throw SyntaxException.createUnexpectedTagException(name);
        }
    }


}
