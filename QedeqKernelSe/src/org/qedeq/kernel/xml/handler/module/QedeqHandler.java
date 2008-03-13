/* $Id: QedeqHandler.java,v 1.14 2007/12/21 23:33:46 m31 Exp $
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

import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.dto.module.QedeqVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.parser.AbstractSimpleHandler;
import org.qedeq.kernel.xml.parser.SaxDefaultHandler;
import org.qedeq.kernel.xml.parser.SimpleAttributes;


/**
 * Parses complete qedeq modules.
 *
 * @version $Revision: 1.14 $
 * @author  Michael Meyling
 */
public class QedeqHandler extends AbstractSimpleHandler {

    /** Module header handler. */
    private final HeaderHandler headerHandler;

    /** Handles a single chapter. */
    private final ChapterHandler chapterHandler;

    /** Handles a bibliography. */
    private final LiteratureItemListHandler bibliographyHandler;

    /** Root value object for a module. */
    private QedeqVo qedeq;


    /**
     * Handle a qedeq module.
     *
     * @param   defaultHandler  Startup handler.
     */
    public QedeqHandler(final SaxDefaultHandler defaultHandler) {
        super(defaultHandler, "QEDEQ");
        headerHandler = new HeaderHandler(this);
        chapterHandler = new ChapterHandler(this);
        bibliographyHandler = new LiteratureItemListHandler(this);
    }

    public final void init() {
        qedeq = null;
    }

    /**
     * Get qedeq value object.
     *
     * @return  Qedeq object.
     */
    public final Qedeq getQedeq() {
        return qedeq;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            qedeq = new QedeqVo();
        } else if (headerHandler.getStartTag().equals(name)) {
            changeHandler(headerHandler, name, attributes);
        } else if (chapterHandler.getStartTag().equals(name)) {
            changeHandler(chapterHandler, name, attributes);
        } else if (bibliographyHandler.getStartTag().equals(name)) {
            changeHandler(bibliographyHandler, name, attributes);
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // nothing to do
        } else if (headerHandler.getStartTag().equals(name)) {
            qedeq.setHeader(headerHandler.getHeader());
        } else if (chapterHandler.getStartTag().equals(name)) {
            qedeq.addChapter(chapterHandler.getChapter());
        } else if (bibliographyHandler.getStartTag().equals(name)) {
            qedeq.setLiteratureItemList(bibliographyHandler.getLiteratureItemList());
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

}
