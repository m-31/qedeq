/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2013,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.kernel.se.dto.module.UsedByListVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.handler.common.AbstractSimpleHandler;
import org.qedeq.kernel.xml.handler.common.SimpleAttributes;


/**
 * Parse list of referencing modules.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class UsedByListHandler extends AbstractSimpleHandler {

    /** Value object for the "this module is used by" list. */
    private UsedByListVo list;

    /** Handler for a single module specification. */
    private final SpecificationHandler specificationHandler;


    /**
     * Handles list of modules that use the current one.
     *
     * @param   handler Parent handler.
     */
    public UsedByListHandler(final AbstractSimpleHandler handler) {
        super(handler, "USEDBY");
        specificationHandler = new SpecificationHandler(this);
    }

    public final void init() {
        list = new UsedByListVo();
    }

    /**
     * Get parsed result.
     *
     * @return  Location list.
     */
    public final UsedByListVo getUsedByList() {
        return list;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // ignore
        } else if (specificationHandler.getStartTag().equals(name)) {
            changeHandler(specificationHandler, name, attributes);
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // ignore
        } else if (specificationHandler.getStartTag().equals(name)) {
            list.add(specificationHandler.getSpecification());
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

}
