/* $Id: ImportListHandler.java,v 1.1 2008/07/26 08:00:51 m31 Exp $
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

import org.qedeq.kernel.dto.module.ImportListVo;
import org.qedeq.kernel.dto.module.ImportVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.parser.AbstractSimpleHandler;
import org.qedeq.kernel.xml.parser.SimpleAttributes;


/**
 * Parse author list.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class ImportListHandler extends AbstractSimpleHandler {

    /** Value object with list of all module imports. */
    private ImportListVo list;

    /** Handler for a single module specification. */
    private final SpecificationHandler specificationHandler;

    /** Label for a single module. */
    private String label;


    /**
     * Handles list of imports.
     *
     * @param   handler Parent handler.
     */
    public ImportListHandler(final AbstractSimpleHandler handler) {
        super(handler, "IMPORTS");
        specificationHandler = new SpecificationHandler(this);
    }

    public final void init() {
        list = null;
    }

    /**
     * Get parsed result.
     *
     * @return  Location list.
     */
    public final ImportListVo getImportList() {
        return list;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            list = new ImportListVo();
        } else if ("IMPORT".equals(name)) {
            label = attributes.getString("label");
        } else if (specificationHandler.getStartTag().equals(name)) {
            changeHandler(specificationHandler, name, attributes);
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // nothing to do
        } else if (specificationHandler.getStartTag().equals(name)) {
            // nothing to do
        } else if ("IMPORT".equals(name)) {
            list.add(new ImportVo(label, specificationHandler.getSpecification()));
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }
}
