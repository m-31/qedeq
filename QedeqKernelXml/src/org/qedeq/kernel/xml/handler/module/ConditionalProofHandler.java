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

import org.qedeq.kernel.se.dto.module.ConditionalProofVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.handler.common.AbstractSimpleHandler;
import org.qedeq.kernel.xml.handler.common.SimpleAttributes;


/**
 * Parse a conditional proof rule usage.
 *
 * @author  Michael Meyling
 */
public class ConditionalProofHandler extends AbstractSimpleHandler {

    /** Rule value object. */
    private ConditionalProofVo conditionalProof;

    /** Handle hypothesis. */
    private HypothesisHandler hypothesisHandler;

    /** Handle elements. */
    private FormalProofLineListHandler proofListHandler;

    /** Handle hypothesis. */
    private ConclusionHandler conclusionHandler;

    /**
     * Deals with definitions.
     *
     * @param   handler Parent handler.
     */
    public ConditionalProofHandler(final AbstractSimpleHandler handler) {
        super(handler, "CP");
    }

    public final void init() {
        conditionalProof = null;
        // we initialize the parsers only when really needed (so we have no recursive calls)
        hypothesisHandler = new HypothesisHandler(this);
        proofListHandler = new FormalProofLineListHandler(this);
        conclusionHandler = new ConclusionHandler(this);
    }

    /**
     * Get conditional proof usage.
     *
     * @return  Substitute Free Variable usage.
     */
    public final ConditionalProofVo getConditionalProofVo() {
        return conditionalProof;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // ok
        } else if (hypothesisHandler.getStartTag().equals(name)) {
            changeHandler(hypothesisHandler, name, attributes);
        } else if (proofListHandler.getStartTag().equals(name)) {
            changeHandler(proofListHandler, name, attributes);
        } else if (conclusionHandler.getStartTag().equals(name)) {
            changeHandler(conclusionHandler, name, attributes);
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            conditionalProof = new ConditionalProofVo(hypothesisHandler.getHypothesis(),
                proofListHandler.getFormalProofLineList(),
                conclusionHandler.getConclusion());
        } else if (hypothesisHandler.getStartTag().equals(name)) {
            // ok
        } else if (proofListHandler.getStartTag().equals(name)) {
            // ok
        } else if (conclusionHandler.getStartTag().equals(name)) {
            // ok
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

}
