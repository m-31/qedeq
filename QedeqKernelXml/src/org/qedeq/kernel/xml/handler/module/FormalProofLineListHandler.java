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

import org.qedeq.kernel.se.dto.module.FormalProofLineListVo;
import org.qedeq.kernel.se.dto.module.FormalProofLineVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.parser.AbstractSimpleHandler;
import org.qedeq.kernel.xml.parser.SimpleAttributes;


/**
 * Parse author list.
 *
 * @author  Michael Meyling
 */
public class FormalProofLineListHandler extends AbstractSimpleHandler {

    /** Value object with list of all module imports. */
    private FormalProofLineListVo list;

    /** Handler for proposition formula. */
    private final FormulaHandler formulaHandler;

    /** Handler for a rule usage. */
    private final ReasonHandler reasonHandler;

    /** Label for a single module. */
    private String label;


    /**
     * Handles list of imports.
     *
     * @param   handler Parent handler.
     */
    public FormalProofLineListHandler(final AbstractSimpleHandler handler) {
        super(handler, "LINES");
        formulaHandler = new FormulaHandler(this);
        reasonHandler = new ReasonHandler(this);
    }

    public final void init() {
        list = null;
    }

    /**
     * Get parsed result.
     *
     * @return  Location list.
     */
    public final FormalProofLineListVo getFormalProofLineList() {
        return list;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            list = new FormalProofLineListVo();
        } else if ("L".equals(name)) {
            label = attributes.getString("label");
        } else if (formulaHandler.getStartTag().equals(name)) {
            changeHandler(formulaHandler, name, attributes);
        } else if (reasonHandler.getStartTag().equals(name)) {
            changeHandler(reasonHandler, name, attributes);
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // nothing to do
        } else if (formulaHandler.getStartTag().equals(name)) {
            // nothing to do
        } else if (reasonHandler.getStartTag().equals(name)) {
            // nothing to do
        } else if ("L".equals(name)) {
            list.add(new FormalProofLineVo(label, formulaHandler.getFormula(), reasonHandler.getReason()));
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }
}
