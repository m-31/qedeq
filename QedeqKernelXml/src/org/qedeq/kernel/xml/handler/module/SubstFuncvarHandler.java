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
import org.qedeq.kernel.se.base.module.Term;
import org.qedeq.kernel.se.dto.module.SubstFuncVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.handler.common.AbstractSimpleHandler;
import org.qedeq.kernel.xml.handler.common.SimpleAttributes;
import org.qedeq.kernel.xml.handler.list.ElementHandler;


/**
 * Parse a Substitute Function Variable Rule usage.
 *
 * @author  Michael Meyling
 */
public class SubstFuncvarHandler extends AbstractSimpleHandler {

    /** Rule value object. */
    private SubstFuncVo substFunc;

    /** Reference to previously proved formula. */
    private String ref;

    /** Function variable we want to substitute. */
    private Element functionVariable;

    /** Replacement term. */
    private Term substituteTerm;

    /** Handle terms. */
    private final TermHandler termHandler;

    /** Handle elements. */
    private final ElementHandler elementHandler;

    /**
     * Deals with definitions.
     *
     * @param   handler Parent handler.
     */
    public SubstFuncvarHandler(final AbstractSimpleHandler handler) {
        super(handler, "SUBST_FUNVAR");
        termHandler = new TermHandler(this);
        elementHandler = new ElementHandler(this);
    }

    public final void init() {
        substFunc = null;
        substituteTerm = null;
        functionVariable = null;
        ref = null;
    }

    /**
     * Get Substitute Function Variable Rule usage.
     *
     * @return  Substitute Free Variable usage.
     */
    public final SubstFuncVo getSubstFuncVo() {
        return substFunc;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            ref = attributes.getString("ref");
        } else if ("FUNVAR".equals(name)) {
            changeHandler(elementHandler, name, attributes);
        } else if (termHandler.getStartTag().equals(name)) {
            changeHandler(termHandler, name, attributes);
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            substFunc = new SubstFuncVo(ref, functionVariable,
                (substituteTerm != null ? substituteTerm.getElement() : null));
        } else if ("FUNVAR".equals(name)) {
            functionVariable = elementHandler.getElement();
        } else if (termHandler.getStartTag().equals(name)) {
            substituteTerm = termHandler.getTerm();
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

}
