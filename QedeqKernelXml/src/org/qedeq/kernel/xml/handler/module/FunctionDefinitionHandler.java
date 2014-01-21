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

import org.qedeq.kernel.se.dto.module.FunctionDefinitionVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.handler.common.AbstractSimpleHandler;
import org.qedeq.kernel.xml.handler.common.SimpleAttributes;


/**
 * Parse a function definition.
 *
 * @author  Michael Meyling
 */
public class FunctionDefinitionHandler extends AbstractSimpleHandler {

    /** Handler for formulas. */
    private final FormulaHandler formulaHandler;

    /** Handler for rule description. */
    private final LatexListHandler descriptionHandler;

    /** Definition value object. */
    private FunctionDefinitionVo definition;

    /** LaTeX pattern for the definition. */
    private String latexPattern;


    /**
     * Deals with definitions.
     *
     * @param   handler Parent handler.
     */
    public FunctionDefinitionHandler(final AbstractSimpleHandler handler) {
        super(handler, "DEFINITION_FUNCTION");
        formulaHandler = new FormulaHandler(this);
        descriptionHandler = new LatexListHandler(this, "DESCRIPTION");
    }

    public final void init() {
        definition = null;
        latexPattern = null;
    }

    /**
     * Get definition.
     *
     * @return  Definition.
     */
    public final FunctionDefinitionVo getDefinition() {
        return definition;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            definition = new FunctionDefinitionVo();
            definition.setArgumentNumber(attributes.getString("arguments"));
            definition.setName(attributes.getString("name"));
        } else if ("LATEXPATTERN".equals(name)) {
            // nothing to do yet
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
        } else if ("LATEXPATTERN".equals(name)) {
            definition.setLatexPattern(latexPattern);
        } else if (formulaHandler.getStartTag().equals(name)) {
            definition.setFormula(formulaHandler.getFormula());
        } else if (descriptionHandler.getStartTag().equals(name)) {
            definition.setDescription(descriptionHandler.getLatexList());
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void characters(final String name, final String data) {
        if ("LATEXPATTERN".equals(name)) {
            latexPattern = data;
        } else {
            throw new RuntimeException("Unexpected character data in tag: " + name);
        }
    }

}
