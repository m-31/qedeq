/* $Id: AbstractSimpleHandler.java,v 1.1 2008/07/26 08:00:50 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2009,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.xml.parser;

import org.qedeq.kernel.xml.common.XmlSyntaxException;


/**
 * Simple handler that gets SAX parser events. These events were received by the
 * {@link org.qedeq.kernel.xml.parser.SaxDefaultHandler} and are delegated to the
 * current {@link AbstractSimpleHandler}.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public abstract class AbstractSimpleHandler {

    /** This handler gets the original SAX events. */
    private final SaxDefaultHandler defaultHandler;

    /** Start tag for this handler .*/
    private final String startTag;

    /**
     * Constructor.
     *
     * @param   defaultHandler  Original SAX event handler.
     * @param   startTag        Start tag for this handler.
     */
    public AbstractSimpleHandler(final SaxDefaultHandler defaultHandler, final String startTag) {
        this.defaultHandler = defaultHandler;
        this.startTag = startTag;
    }

    /**
     * Constructor.
     *
     * @param   defaultHandler  Original SAX event handler.
     */
    public AbstractSimpleHandler(final SaxDefaultHandler defaultHandler) {
        this.defaultHandler = defaultHandler;
        this.startTag = null;
    }

    /**
     * Constructor, should be used for creating handlers within handlers.
     *
     * @param   handler     Already existing simple handler.
     * @param   startTag    Start tag for this handler.
     */
    public AbstractSimpleHandler(final AbstractSimpleHandler handler, final String startTag) {
        this.defaultHandler = handler.defaultHandler;
        this.startTag = startTag;
    }

    /**
     * Constructor, should be used for creating handlers within handlers.
     *
     * @param   handler Already existing simple handler.
     */
    public AbstractSimpleHandler(final AbstractSimpleHandler handler) {
        this.defaultHandler = handler.defaultHandler;
        this.startTag = null;
    }

    /**
     * Must be called before a handler should parse a new section.
     */
    public abstract void init();

    /**
     * Called at begin of element <code>elementName</code>. Must be overwritten.
     *
     * @param   elementName Tag name.
     * @param   attributes  Tag attributes.
     * @throws  XmlSyntaxException   There is a semantic error in this event occurrence.
     */
    public abstract void startElement(final String elementName, final SimpleAttributes attributes)
        throws XmlSyntaxException;

    /**
     * Called at end of element <code>elementName</code>. Must be overwritten.
     *
     * @param   elementName Tag name.
     * @throws  XmlSyntaxException   There is a semantic error in this event occurrence.
     */
    public abstract void endElement(final String elementName) throws XmlSyntaxException;

    /**
     * Called at end of element <code>elementName</code>. Must be overwritten if you expect
     * character data.
     *
     * @param   elementName Tag name.
     * @param   value   String value.
     * @throws  XmlSyntaxException   There is a semantic error in this event occurrence.
     */
    public void characters(final String elementName, final String value) throws XmlSyntaxException {
        // default implementation
        throw XmlSyntaxException.createUnexpectedTextDataException(elementName, value);
    }

    /**
     * Change current handler to new one. The new handler gets automatically a
     * <code>beginElement</code> event.
     *
     * @param   newHandler  Handler that gets all the events now.
     * @param   elementName Current element name.
     * @param   attributes  Current element attributes.
     * @throws  XmlSyntaxException   New handler detected semantical problems.
     */
    public final void changeHandler(final AbstractSimpleHandler newHandler,
            final String elementName, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (newHandler.getStartTag() != null && !newHandler.getStartTag().equals(elementName)) {
            throw new RuntimeException(newHandler.getClass().getName() + " has start tag \""
                + newHandler.getStartTag() + "\", but should start with tag \""
                + elementName + "\"");
        }
        defaultHandler.changeHandler(newHandler, elementName, attributes);
    }

    /**
     * Get current tag level.
     *
     * @return  Current level.
     */
    public final int getLevel() {
        return defaultHandler.getLevel();
    }

    /**
     * Get start tag for this handler. Could be <code>null</code> if there is no specific start tag.
     *
     * @return  Start tag.
     */
    public final String getStartTag() {
        return startTag;
    }

}
