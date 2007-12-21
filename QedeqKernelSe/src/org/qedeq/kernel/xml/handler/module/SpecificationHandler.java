/* $Id: SpecificationHandler.java,v 1.15 2007/12/21 23:33:46 m31 Exp $
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

import org.qedeq.kernel.dto.module.LocationListVo;
import org.qedeq.kernel.dto.module.LocationVo;
import org.qedeq.kernel.dto.module.SpecificationVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.parser.AbstractSimpleHandler;
import org.qedeq.kernel.xml.parser.SimpleAttributes;


/**
 * Parse specification informations.
 *
 * @version $Revision: 1.15 $
 * @author  Michael Meyling
 */
public class SpecificationHandler extends AbstractSimpleHandler {

    /** Specification value object. */
    private SpecificationVo specification;

    /** Module name. */
    private String moduleName;

    /** Rule version which is at least needed to verify this module. */
    private String ruleVersion;

    /** List of locations for module. */
    private LocationListVo locations;


    /**
     * Constructor.
     *
     * @param   handler     Parent handler.
     */
    public SpecificationHandler(final AbstractSimpleHandler handler) {
        super(handler, "SPECIFICATION");
    }

    public final void init() {
        specification = null;
        moduleName = null;
        ruleVersion = null;
        locations = null;
    }

    /**
     * Get specification.
     *
     * @return  Module specification.
     */
    public final SpecificationVo getSpecification() {
        return specification;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            locations = new LocationListVo();
            moduleName = attributes.getString("name");
            ruleVersion = attributes.getString("ruleVersion");
        } else if ("LOCATIONS".equals(name)) {
            // ignore
        } else if ("LOCATION".equals(name)) {
            locations.add(new LocationVo(attributes.getString("value")));
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            specification = new SpecificationVo(moduleName, ruleVersion, locations);
        } else if ("LOCATIONS".equals(name)) {
            // ignore
        } else if ("LOCATION".equals(name)) {
            // ignore
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

}
