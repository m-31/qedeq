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

package org.qedeq.kernel.xml.handler.parser;

import java.util.ArrayList;
import java.util.List;

import org.qedeq.kernel.bo.parser.Operator;
import org.qedeq.kernel.xml.common.XmlSyntaxException;
import org.qedeq.kernel.xml.handler.common.AbstractSimpleHandler;
import org.qedeq.kernel.xml.handler.common.SaxDefaultHandler;
import org.qedeq.kernel.xml.handler.common.SimpleAttributes;


/**
 * Parses list of operators. Result is a list of all parsed operators.
 *
 * @author  Michael Meyling
 */
public final class ParserHandler extends AbstractSimpleHandler {

    /** List of all Operators. */
    private List operators = new ArrayList();

    /** Operator start symbol. */
    private String startSymbol;

    /** QEDEQ representation. E.g. "PREDCON".  */
    private String qedeq;

    /** QEDEQ argument. E.g. "equal". */
    private String qedeqArgument;

    /** Operator priority. */
    private Integer priority;

    /** Minimum argument number. */
    private Integer min;

    /** Maximum argument number. */
    private Integer max;


    /**
     * Handle a parser XML file.
     *
     * @param   defaultHandler  Startup handler.
     */
    public ParserHandler(final SaxDefaultHandler defaultHandler) {
        super(defaultHandler, "parser");
    }

    public final void init() {
        operators.clear();
    }

    /**
     * Get list of operators.
     *
     * @return  Operator list.
     */
    public final List getOperators() {
        return operators;
    }

    public final void startElement(final String name, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // nothing todo
        } else if ("prefixOperator".equals(name)) {
            setBasisAttributes(name, attributes);
            addOperator(Operator.SIMPLE_PREFIX);
        } else if ("infixOperator".equals(name)) {
            setBasisAttributes(name, attributes);
            addOperator(Operator.INFIX);
        } else if ("functionOperator".equals(name)) {
            setBasisAttributes(name, attributes);
            addOperator(Operator.FUNCTION);
        } else if ("complexOperator".equals(name)) {
            setBasisAttributes(name, attributes);
            final String separatorSymbol = attributes.getString("separatorSymbol");
            if (separatorSymbol == null) {
                throw XmlSyntaxException.createEmptyAttributeException(name, "separatorSymbol");
            }
            if (separatorSymbol.length() == 0) {
                throw XmlSyntaxException.createMissingAttributeException(name, "separatorSymbol");
            }
            final String endSymbol = attributes.getString("endSymbol");
            if (endSymbol == null) {
                throw XmlSyntaxException.createEmptyAttributeException(name, "endSymbol");
            }
            if (endSymbol.length() == 0) {
                throw XmlSyntaxException.createMissingAttributeException(name, "endSymbol");
            }
            if (max == null) {
                operators.add(new Operator(startSymbol, separatorSymbol, endSymbol, qedeq,
                    qedeqArgument, priority.intValue(), min.intValue()));
            } else {
                operators.add(new Operator(startSymbol, separatorSymbol, endSymbol, qedeq,
                    qedeqArgument, priority.intValue(), min.intValue(), max.intValue()));
            }
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

    private void addOperator(final int type) {
        if (max == null) {
            operators.add(new Operator(startSymbol, qedeq, qedeqArgument, priority.intValue(),
                type, min.intValue()));
        } else {
            operators.add(new Operator(startSymbol, qedeq, qedeqArgument, priority.intValue(),
                type, min.intValue(), max.intValue()));
        }
    }

    private void setBasisAttributes(final String element, final SimpleAttributes attributes)
            throws XmlSyntaxException {
        startSymbol = attributes.getString("startSymbol");
        if (startSymbol == null) {
            throw XmlSyntaxException.createMissingAttributeException(element, "startSymbol");
        }
        if (startSymbol.length() == 0) {
            throw XmlSyntaxException.createEmptyAttributeException(element, "startSymbol");
        }
        qedeq = attributes.getString("qedeq");
        if (qedeq == null) {
            throw XmlSyntaxException.createMissingAttributeException(element, "qedeq");
        }
        if (qedeq.length() == 0) {
            throw XmlSyntaxException.createEmptyAttributeException(element, "qedeq");
        }
        qedeqArgument = attributes.getString("qedeqArgument");
        priority = attributes.getInteger("priority");
        if (priority == null || priority.intValue() < 0) {
            throw XmlSyntaxException.createMissingAttributeException(element, "priority");
        }
        min = attributes.getInteger("min");
        if (min == null) {
            min = new Integer(0);
        }
        max = attributes.getInteger("max");
    }

    public final void endElement(final String name) throws XmlSyntaxException {
        if (getStartTag().equals(name)) {
            // nothing to do
        } else if ("prefixOperator".equals(name)) {
            // nothing to do
        } else if ("infixOperator".equals(name)) {
            // nothing to do
        } else if ("functionOperator".equals(name)) {
            // nothing to do
        } else if ("complexOperator".equals(name)) {
            // nothing to do
        } else {
            throw XmlSyntaxException.createUnexpectedTagException(name);
        }
    }

}
