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

import org.qedeq.kernel.se.base.module.Axiom;
import org.qedeq.kernel.se.dto.module.AxiomVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.handler.common.AbstractSimpleHandler;
import org.qedeq.kernel.xml.handler.common.SimpleAttributes;


/**
 * Parse an axiom.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class AxiomHandler extends AbstractSimpleHandler {

    /** Handler for axiom formula. */
    private final FormulaHandler formulaHandler;

    /** Handler for rule description. */
    private final LatexListHandler descriptionHandler;

    /** Axiom value object. */
    private AxiomVo axiom;

    /**
     * Deals with axioms.
     *
     * @param   handler Parent handler.
     */
    public AxiomHandler(final AbstractSimpleHandler handler) {
        super(handler, "AXIOM");
        formulaHandler = new FormulaHandler(this);
        descriptionHandler = new LatexListHandler(this, "DESCRIPTION");
    }

    public final void init() {
        axiom = null;
    }

    /**
     * Get axiom.
     *
     * @return  Axiom.
     */
    public final Axiom getAxiom() {
        return axiom;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            axiom = new AxiomVo();
            axiom.setDefinedOperator(attributes.getString("definedOperator"));
        } else if (formulaHandler.getStartTag().equals(name)) {
            changeHandler(formulaHandler, name, attributes);
        } else if (descriptionHandler.getStartTag().equals(name)) {
            changeHandler(descriptionHandler, name, attributes);
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // nothing to do
        } else if (formulaHandler.getStartTag().equals(name)) {
            axiom.setFormula(formulaHandler.getFormula());
        } else if (descriptionHandler.getStartTag().equals(name)) {
            axiom.setDescription(descriptionHandler.getLatexList());
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

}
