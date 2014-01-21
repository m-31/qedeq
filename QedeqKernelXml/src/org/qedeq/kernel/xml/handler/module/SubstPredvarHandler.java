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

import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.module.Formula;
import org.qedeq.kernel.se.dto.module.SubstPredVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.handler.common.AbstractSimpleHandler;
import org.qedeq.kernel.xml.handler.common.SimpleAttributes;
import org.qedeq.kernel.xml.handler.list.ElementHandler;


/**
 * Parse a Substitute Predicate Variable Rule usage.
 *
 * @author  Michael Meyling
 */
public class SubstPredvarHandler extends AbstractSimpleHandler {

    /** Rule value object. */
    private SubstPredVo substPredvar;

    /** Reference to previously proved formula. */
    private String ref;

    /** Predicate variable that will be substituted. */
    private Element predicateVariable;

    /** Replacement formula. */
    private Formula substituteFormula;

    /** Handle formal proofs. */
    private final FormulaHandler formulaHandler;

    /** Handle elements. */
    private final ElementHandler elementHandler;

    /**
     * Deals with definitions.
     *
     * @param   handler Parent handler.
     */
    public SubstPredvarHandler(final AbstractSimpleHandler handler) {
        super(handler, "SUBST_PREDVAR");
        formulaHandler = new FormulaHandler(this);
        elementHandler = new ElementHandler(this);
    }

    public final void init() {
        substPredvar = null;
        predicateVariable = null;
        substituteFormula = null;
        ref = null;
    }

    /**
     * Get Substitute Predicate Variable Rule usage.
     *
     * @return  Substitute Predicate Variable usage.
     */
    public final SubstPredVo getSubstPredVo() {
        return substPredvar;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            ref = attributes.getString("ref");
        } else if ("PREDVAR".equals(name)) {
            changeHandler(elementHandler, name, attributes);
        } else if (formulaHandler.getStartTag().equals(name)) {
            changeHandler(formulaHandler, name, attributes);
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            substPredvar = new SubstPredVo(ref, predicateVariable,
                (substituteFormula != null ? substituteFormula.getElement() : null));
        } else if ("PREDVAR".equals(name)) {
            predicateVariable = elementHandler.getElement();
        } else if (formulaHandler.getStartTag().equals(name)) {
            substituteFormula = formulaHandler.getFormula();
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

}
