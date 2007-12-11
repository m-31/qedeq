/* $Id: RuleHandler.java,v 1.7 2007/05/10 00:37:50 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2007,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.kernel.base.module.Rule;
import org.qedeq.kernel.dto.module.RuleVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.parser.AbstractSimpleHandler;
import org.qedeq.kernel.xml.parser.SimpleAttributes;


/**
 * Parse a rule.
 *
 * @version $Revision: 1.7 $
 * @author  Michael Meyling
 */
public class RuleHandler extends AbstractSimpleHandler {

    /** Handler for rule description. */
    private final LatexListHandler descriptionHandler;

    /** Handle proofs. */
    private final ProofHandler proofHandler;

    /** Rule value object. */
    private RuleVo rule;

    /**
     * Deals with definitions.
     *
     * @param   handler Parent handler.
     */
    public RuleHandler(final AbstractSimpleHandler handler) {
        super(handler, "RULE");
        descriptionHandler = new LatexListHandler(this, "DESCRIPTION");
        proofHandler = new ProofHandler(this);
    }

    public final void init() {
        rule = null;
    }

    /**
     * Get Rule.
     *
     * @return  Rule.
     */
    public final Rule getRule() {
        return rule;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            rule = new RuleVo();
            if (null != attributes.getString("name")) {
                rule.setName(attributes.getString("name"));
            } else {
                throw XmlSyntaxException.createMissingAttributeException(name, "name");
            }
        } else if ("LINK".equals(name)) {
            if (null != attributes.getString("id")) {
                rule.addLink(attributes.getString("id"));
            } else {
                throw XmlSyntaxException.createMissingAttributeException(name, "id");
            }
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
        } else if ("LINK".equals(name)) {
            // nothing to do
        } else if (descriptionHandler.getStartTag().equals(name)) {
            rule.setDescription(descriptionHandler.getLatexList());
        } else if (proofHandler.getStartTag().equals(name)) {
            rule.addProof(proofHandler.getProof());
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }


}
