/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.kernel.se.base.module.Hypothesis;
import org.qedeq.kernel.se.dto.module.HypothesisVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.handler.common.AbstractSimpleHandler;
import org.qedeq.kernel.xml.handler.common.SimpleAttributes;


/**
 * Parse a hypothesis.
 *
 * @author  Michael Meyling
 */
public class HypothesisHandler extends AbstractSimpleHandler {

    /** Handler for axiom formula. */
    private final FormulaHandler formulaHandler;

    /** Hypothesis value object. */
    private HypothesisVo hypothesis;

    /**
     * Deals with axioms.
     *
     * @param   handler Parent handler.
     */
    public HypothesisHandler(final AbstractSimpleHandler handler) {
        super(handler, "HYPOTHESIS");
        formulaHandler = new FormulaHandler(this);
    }

    public final void init() {
        hypothesis = null;
    }

    /**
     * Get hypothesis.
     *
     * @return  Hypothesis.
     */
    public final Hypothesis getHypothesis() {
        return hypothesis;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            hypothesis = new HypothesisVo();
            hypothesis.setLabel(attributes.getString("label"));
        } else if (formulaHandler.getStartTag().equals(name)) {
            changeHandler(formulaHandler, name, attributes);
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // nothing to do
        } else if (formulaHandler.getStartTag().equals(name)) {
            hypothesis.setFormula(formulaHandler.getFormula());
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

}
