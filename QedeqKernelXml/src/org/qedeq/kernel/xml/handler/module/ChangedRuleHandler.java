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

import org.qedeq.kernel.se.dto.module.ChangedRuleVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.handler.common.AbstractSimpleHandler;
import org.qedeq.kernel.xml.handler.common.SimpleAttributes;


/**
 * Parse a rule change.
 *
 * @author  Michael Meyling
 */
public class ChangedRuleHandler extends LatexListHandler {

    /** Rule value object. */
    private ChangedRuleVo rule;

    /**
     * Deals with definitions.
     *
     * @param   handler Parent handler.
     */
    public ChangedRuleHandler(final AbstractSimpleHandler handler) {
        super(handler, "CHANGED_RULE");
    }

    public final void init() {
        super.init();
        rule = null;
    }

    /**
     * Get changed rule.
     *
     * @return  Rule.
     */
    public final ChangedRuleVo getChangedRule() {
        return rule;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            rule = new ChangedRuleVo();
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
        } else {
            super.startElement(name, attributes);
        }
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            rule.setDescription(getLatexList());
        } else {
            super.endElement(name);
        }
    }


}
