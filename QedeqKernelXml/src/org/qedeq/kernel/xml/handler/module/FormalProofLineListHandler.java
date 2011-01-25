/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.kernel.se.base.module.Reason;
import org.qedeq.kernel.se.dto.module.FormalProofLineListVo;
import org.qedeq.kernel.se.dto.module.FormalProofLineVo;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.parser.AbstractSimpleHandler;
import org.qedeq.kernel.xml.parser.SimpleAttributes;


/**
 * Parse author list.
 *
 * @author  Michael Meyling
 */
public class FormalProofLineListHandler extends AbstractSimpleHandler {

    /** Value object with list of all module imports. */
    private FormalProofLineListVo list;

    /** Handler for proposition formula. */
    private final FormulaHandler formulaHandler;

    /** Handler for Modus Ponens usage. */
    private final ModusPonensHandler modusPonensHandler;

    /** Handler for Addition usage. */
    private final AddHandler addHandler;

    /** Handler for Substitution Predicate Variable usage. */
    private final SubstPredvarHandler substPredvarHandler;

    /** Label for a single module. */
    private String label;

    /** Reason for proof line. */
    private Reason reason;


    /**
     * Handles list of imports.
     *
     * @param   handler Parent handler.
     */
    public FormalProofLineListHandler(final AbstractSimpleHandler handler) {
        super(handler, "LINES");
        formulaHandler = new FormulaHandler(this);
        modusPonensHandler = new ModusPonensHandler(this);
        addHandler = new AddHandler(this);
        substPredvarHandler = new SubstPredvarHandler(this);
    }

    public final void init() {
        list = null;
        reason = null;
        label = null;
    }

    /**
     * Get parsed result.
     *
     * @return  Location list.
     */
    public final FormalProofLineListVo getFormalProofLineList() {
        return list;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            list = new FormalProofLineListVo();
        } else if ("L".equals(name)) {
            label = attributes.getString("label");
            reason = null;
        } else if (formulaHandler.getStartTag().equals(name)) {
            changeHandler(formulaHandler, name, attributes);
        } else if (modusPonensHandler.getStartTag().equals(name)) {
            changeHandler(modusPonensHandler, name, attributes);
        } else if (addHandler.getStartTag().equals(name)) {
            changeHandler(addHandler, name, attributes);
        } else if (substPredvarHandler.getStartTag().equals(name)) {
            changeHandler(substPredvarHandler, name, attributes);
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // nothing to do
        } else if (formulaHandler.getStartTag().equals(name)) {
            // nothing to do
        } else if ("L".equals(name)) {
            list.add(new FormalProofLineVo(label, formulaHandler.getFormula(), reason));
        } else if (modusPonensHandler.getStartTag().equals(name)) {
            reason = modusPonensHandler.getModusPonensVo();
        } else if (addHandler.getStartTag().equals(name)) {
            reason = addHandler.getAddVo();
        } else if (substPredvarHandler.getStartTag().equals(name)) {
            reason = substPredvarHandler.getSubstPredVo();
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }
}
