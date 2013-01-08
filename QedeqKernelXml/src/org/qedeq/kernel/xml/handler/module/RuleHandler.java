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

import org.qedeq.kernel.se.base.module.Rule;
import org.qedeq.kernel.se.dto.module.RuleVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.handler.common.AbstractSimpleHandler;
import org.qedeq.kernel.xml.handler.common.SimpleAttributes;


/**
 * Parse a rule.
 *
 * @author  Michael Meyling
 */
public class RuleHandler extends AbstractSimpleHandler {

    /** Handler for rule description. */
    private final LatexListHandler descriptionHandler;

    /** Handle proofs. */
    private final ProofHandler proofHandler;

    /** Handle rule changes. */
    private final ChangedRuleHandler changedRuleHandler;

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
        changedRuleHandler = new ChangedRuleHandler(this);
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
            if (null != attributes.getString("version")) {
                rule.setVersion(attributes.getString("version"));
            } else {
                throw XmlSyntaxException.createMissingAttributeException(name, "version");
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
        } else if (changedRuleHandler.getStartTag().equals(name)) {
            changeHandler(changedRuleHandler, name, attributes);
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
        } else if (changedRuleHandler.getStartTag().equals(name)) {
            rule.addChangedRule(changedRuleHandler.getChangedRule());
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }


}
