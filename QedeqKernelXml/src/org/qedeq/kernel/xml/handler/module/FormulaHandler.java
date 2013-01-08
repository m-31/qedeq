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

import org.qedeq.kernel.se.dto.module.FormulaVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.handler.common.AbstractSimpleHandler;
import org.qedeq.kernel.xml.handler.common.SimpleAttributes;
import org.qedeq.kernel.xml.handler.list.ElementHandler;


/**
 * Parse formula.
 *
 * @author  Michael Meyling
 */
public class FormulaHandler extends AbstractSimpleHandler {

    /** Value object for formula. */
    private FormulaVo formula;

    /** Handles {@link org.qedeq.kernel.se.base.list.Element}s. */
    private final ElementHandler elementHandler;


    /**
     * Handles formulas.
     *
     * @param   handler     Parent handler.
     */
    public FormulaHandler(final AbstractSimpleHandler handler) {
        super(handler, "FORMULA");
        elementHandler = new ElementHandler(this);
    }

    public final void init() {
        formula = null;
    }

    /**
     * Get parsed result.
     *
     * @return  FormulaOrTerm.
     */
    public final FormulaVo getFormula() {
        return formula;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // nothing to do
        } else if (getLevel() > 1) {
            changeHandler(elementHandler, name, attributes);
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            formula = new FormulaVo(elementHandler.getElement());
        } else if (getLevel() <= 1) {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

}
