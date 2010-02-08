/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2010,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.kernel.dto.module.ProofVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.parser.AbstractSimpleHandler;
import org.qedeq.kernel.xml.parser.SimpleAttributes;


/**
 * Parse a proposition.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class ProofHandler extends AbstractSimpleHandler {

    /** Handle informal proofs. */
    private final LatexListHandler informalProofHandler;

    /** Value object. */
    private ProofVo proof;

    /**
     * Deals with propositions.
     *
     * @param   handler Parent handler.
     */
    public ProofHandler(final AbstractSimpleHandler handler) {
        super(handler, "PROOF");
        informalProofHandler = new LatexListHandler(this, "PROOF");
    }

    public final void init() {
        proof = null;
    }

    /**
     * Get proof.
     *
     * @return  Proof.
     */
    public final ProofVo getProof() {
        return proof;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            proof = new ProofVo();
            proof.setKind(attributes.getString("kind"));
            proof.setLevel(attributes.getString("level"));
            changeHandler(informalProofHandler, name, attributes);
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            proof.setNonFormalProof(informalProofHandler.getLatexList());
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

}
