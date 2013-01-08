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

import org.qedeq.kernel.se.dto.module.FormalProofVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.handler.common.AbstractSimpleHandler;
import org.qedeq.kernel.xml.handler.common.SimpleAttributes;


/**
 * Parse a proposition.
 *
 * @author  Michael Meyling
 */
public class FormalProofHandler extends AbstractSimpleHandler {

    /** Handle formal proofs. */
    private final FormalProofLineListHandler formalProofLineListHandler;

    /** Value object. */
    private FormalProofVo proof;

    /**
     * Deals with propositions.
     *
     * @param   handler Parent handler.
     */
    public FormalProofHandler(final AbstractSimpleHandler handler) {
        super(handler, "FORMAL_PROOF");
        formalProofLineListHandler = new FormalProofLineListHandler(this);
    }

    public final void init() {
        proof = null;
    }

    /**
     * Get proof.
     *
     * @return  Proof.
     */
    public final FormalProofVo getProof() {
        return proof;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            proof = new FormalProofVo();
        } else if (formalProofLineListHandler.getStartTag().equals(name)) {
            changeHandler(formalProofLineListHandler, name, attributes);
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // nothing to do
        } else if (formalProofLineListHandler.getStartTag().equals(name)) {
            proof.setFormalProofLineList(formalProofLineListHandler.getFormalProofLineList());
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

}
