/* $Id: NodeHandler.java,v 1.14 2007/05/10 00:37:50 m31 Exp $
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

import org.qedeq.kernel.base.module.Node;
import org.qedeq.kernel.common.SyntaxException;
import org.qedeq.kernel.dto.module.NodeVo;
import org.qedeq.kernel.xml.parser.AbstractSimpleHandler;
import org.qedeq.kernel.xml.parser.SimpleAttributes;


/**
 * Handles node data.
 *
 * @version $Revision: 1.14 $
 * @author  Michael Meyling
 */
public class NodeHandler extends AbstractSimpleHandler {

    /** Handler for node name. The node name is a visible, language specific node reference,
     * e.g. "Axiom of Choice" or "is set". */
    private final LatexListHandler nameHandler;

    /** Handler for title. This is simply the title of this subsection. */
    private final LatexListHandler titleHandler;

    /** Handler for the formula preceding text. */
    private final LatexListHandler precedingHandler;

    /** Handler for the formula succeeding text. */
    private final LatexListHandler succeedingHandler;

    /** Handler for an axiom. */
    private final AxiomHandler axiomHandler;

    /** Handler for a predicate definition. */
    private final PredicateDefinitionHandler predicateDefinitionHandler;

    /** Handler for a function definition. */
    private final FunctionDefinitionHandler functionDefinitionHandler;

    /** Handler for a proposition. */
    private final PropositionHandler propositionHandler;

    /** Handler for a rule. */
    private final RuleHandler ruleHandler;

    /** Node value object. */
    private NodeVo node;


    /**
     * Constructor.
     *
     * @param   handler Parent handler.
     */
    public NodeHandler(final AbstractSimpleHandler handler) {
        super(handler, "NODE");
        nameHandler = new LatexListHandler(this, "NAME");
        titleHandler = new LatexListHandler(this, "TITLE");
        precedingHandler = new LatexListHandler(this, "PRECEDING");
        succeedingHandler = new LatexListHandler(this, "SUCCEEDING");
        axiomHandler = new AxiomHandler(this);
        predicateDefinitionHandler = new PredicateDefinitionHandler(this);
        functionDefinitionHandler = new FunctionDefinitionHandler(this);
        propositionHandler = new PropositionHandler(this);
        ruleHandler = new RuleHandler(this);
    }

    public final void init() {
        node = null;
    }

    /**
     * Get node.
     *
     * @return  Node.
     */
    public final Node getNode() {
        return node;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws SyntaxException {
        if (getStartTag().equals(name)) {
            node = new NodeVo();
            node.setId(attributes.getString("id"));
            node.setLevel(attributes.getString("level"));
        } else if (nameHandler.getStartTag().equals(name)) {
            changeHandler(nameHandler, name, attributes);
        } else if (titleHandler.getStartTag().equals(name)) {
            changeHandler(titleHandler, name, attributes);
        } else if (precedingHandler.getStartTag().equals(name)) {
            changeHandler(precedingHandler, name, attributes);
        } else if (succeedingHandler.getStartTag().equals(name)) {
            changeHandler(succeedingHandler, name, attributes);
        } else if (axiomHandler.getStartTag().equals(name)) {
            changeHandler(axiomHandler, name, attributes);
        } else if (predicateDefinitionHandler.getStartTag().equals(name)) {
            changeHandler(predicateDefinitionHandler, name, attributes);
        } else if (functionDefinitionHandler.getStartTag().equals(name)) {
            changeHandler(functionDefinitionHandler, name, attributes);
        } else if (propositionHandler.getStartTag().equals(name)) {
            changeHandler(propositionHandler, name, attributes);
        } else if (ruleHandler.getStartTag().equals(name)) {
            changeHandler(ruleHandler, name, attributes);
        } else {
            throw SyntaxException.createUnexpectedTagException(name);
        }
    }

    public void endElement(final String name) throws SyntaxException {
        if (getStartTag().equals(name)) {
            // thats why we handle it
        } else if (nameHandler.getStartTag().equals(name)) {
            node.setName(nameHandler.getLatexList());
        } else if (titleHandler.getStartTag().equals(name)) {
            node.setTitle(titleHandler.getLatexList());
        } else if (precedingHandler.getStartTag().equals(name)) {
            node.setPrecedingText(precedingHandler.getLatexList());
        } else if (succeedingHandler.getStartTag().equals(name)) {
            node.setSucceedingText(succeedingHandler.getLatexList());
        } else if (axiomHandler.getStartTag().equals(name)) {
            node.setNodeType(axiomHandler.getAxiom());
        } else if (predicateDefinitionHandler.getStartTag().equals(name)) {
            node.setNodeType(predicateDefinitionHandler.getDefinition());
        } else if (functionDefinitionHandler.getStartTag().equals(name)) {
            node.setNodeType(functionDefinitionHandler.getDefinition());
        } else if (propositionHandler.getStartTag().equals(name)) {
            node.setNodeType(propositionHandler.getProposition());
        } else if (ruleHandler.getStartTag().equals(name)) {
            node.setNodeType(ruleHandler.getRule());
        } else {
            throw SyntaxException.createUnexpectedTagException(name);
       }
    }

}
