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

package org.qedeq.kernel.se.base.module;


/**
 * Marker interface for different node types. Might be a definition or theorem or else.
 *
 * TODO mime 20050707: an interface must not know its implementations. This one does! This was
 * necessary as a quick hack to get some information in QedeqBoFactory and Context2XPath.
 * mime 20051216 I still see no solution to get round this. Some JUnit tests work against
 * this interface and get no information about the instance.
 *
 * @author Michael Meyling
 */
public interface NodeType {

    /**
     * Get axiom, if this is an instance of {@link Axiom}.
     *
     * @return  Axiom, maybe <code>null</code>.
     */
    public Axiom getAxiom();

    /**
     * Get definition, if this is an instance of {@link PredicateDefinition}.
     *
     * @return  Definition, maybe <code>null</code>.
     */
    public PredicateDefinition getPredicateDefinition();

    /**
     * Get initial definition, if this is an instance of {@link InitialPredicateDefinition}.
     *
     * @return  Definition, maybe <code>null</code>.
     */
    public InitialPredicateDefinition getInitialPredicateDefinition();

    /**
     * Get definition, if this is an instance of {@link InitialFunctionDefinition}.
     *
     * @return  Definition, maybe <code>null</code>.
     */
    public InitialFunctionDefinition getInitialFunctionDefinition();

    /**
     * Get definition, if this is an instance of {@link FunctionDefinition}.
     *
     * @return  Definition, maybe <code>null</code>.
     */
    public FunctionDefinition getFunctionDefinition();

    /**
     * Get proposition, if this is an instance of {@link Proposition}.
     *
     * @return  Proposition, maybe <code>null</code>.
     */
    public Proposition getProposition();

    /**
     * Get rule, if this is an instance of {@link Rule}.
     *
     * @return  Rule, maybe <code>null</code>.
     */
    public Rule getRule();

}
