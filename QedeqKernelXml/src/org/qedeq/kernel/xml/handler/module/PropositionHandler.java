/* $Id: PropositionHandler.java,v 1.16 2008/03/27 05:16:27 m31 Exp $
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

import org.qedeq.kernel.dto.module.PropositionVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.parser.AbstractSimpleHandler;
import org.qedeq.kernel.xml.parser.SimpleAttributes;


/**
 * Parse a proposition.
 *
 * @version $Revision: 1.16 $
 * @author  Michael Meyling
 */
public class PropositionHandler extends AbstractSimpleHandler {

    /** Handler for proposition formula. */
    private final FormulaHandler formulaHandler;

    /** Handler for rule description. */
    private final LatexListHandler descriptionHandler;

    /** Handle proofs. */
    private final ProofHandler proofHandler;

    /** Proposition value object. */
    private PropositionVo proposition;


    /**
     * Deals with propositions.
     *
     * @param   handler Parent handler.
     */
    public PropositionHandler(final AbstractSimpleHandler handler) {
        super(handler, "THEOREM");
        formulaHandler = new FormulaHandler(this);
        descriptionHandler = new LatexListHandler(this, "DESCRIPTION");
        proofHandler = new ProofHandler(this);
    }

    public final void init() {
        proposition = null;
    }

    /**
     * Get proposition.
     *
     * @return  Proposition.
     */
    public final PropositionVo getProposition() {
        return proposition;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            proposition = new PropositionVo();
        } else if (formulaHandler.getStartTag().equals(name)) {
            changeHandler(formulaHandler, name, attributes);
        } else if (descriptionHandler.getStartTag().equals(name)) {
            changeHandler(descriptionHandler, name, attributes);
        } else if (proofHandler.getStartTag().equals(name)) {
            changeHandler(proofHandler, name, attributes);
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // nothing to do
        } else if (formulaHandler.getStartTag().equals(name)) {
            proposition.setFormula(formulaHandler.getFormula());
        } else if (descriptionHandler.getStartTag().equals(name)) {
            proposition.setDescription(descriptionHandler.getLatexList());
        } else if (proofHandler.getStartTag().equals(name)) {
            proposition.addProof(proofHandler.getProof());
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

}
