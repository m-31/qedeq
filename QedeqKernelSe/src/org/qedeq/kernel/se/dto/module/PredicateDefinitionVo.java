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

package org.qedeq.kernel.se.dto.module;

import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.se.base.module.Axiom;
import org.qedeq.kernel.se.base.module.Formula;
import org.qedeq.kernel.se.base.module.FunctionDefinition;
import org.qedeq.kernel.se.base.module.InitialFunctionDefinition;
import org.qedeq.kernel.se.base.module.InitialPredicateDefinition;
import org.qedeq.kernel.se.base.module.LatexList;
import org.qedeq.kernel.se.base.module.PredicateDefinition;
import org.qedeq.kernel.se.base.module.Proposition;
import org.qedeq.kernel.se.base.module.Rule;


/**
 * Definition of operator. This is a predicate constant. For example the
 * predicate "x is a set" could be defined in MK with the formula "\exists y: x \in y".
 *
 * @author  Michael Meyling
 */
public class PredicateDefinitionVo implements PredicateDefinition {

    /** Carries information about the argument number the defined object needs.  */
    private String argumentNumber;

    /** This name together with argumentNumber identifies a function. */
    private String name;

    /** LaTeX pattern for definition visualisation. The replaceable arguments must are
     * marked as <code>#1</code>, <code>#2</code> and so on. For example
     * <code>\mathfrak{M}(#1)</code> */
    private String latexPattern;

    /** Formula that defines the object.*/
    private Formula formula;

    /** Further proposition description. Normally <code>null</code>. */
    private LatexList description;

    /**
     * Constructs a new definition.
     */
    public PredicateDefinitionVo() {
        // nothing to do
    }

    public Axiom getAxiom() {
        return null;
    }

    public InitialPredicateDefinition getInitialPredicateDefinition() {
        return null;
    }

    public PredicateDefinition getPredicateDefinition() {
        return this;
    }

    public InitialFunctionDefinition getInitialFunctionDefinition() {
        return null;
    }

    public FunctionDefinition getFunctionDefinition() {
        return null;
    }

    public Proposition getProposition() {
        return null;
    }

    public Rule getRule() {
        return null;
    }

    /**
     * Set information about the argument number the defined object needs.
     *
     * @param   argumentNumber  Argument number information.
     */
    public final void setArgumentNumber(final String argumentNumber) {
        this.argumentNumber = argumentNumber;
    }

    public final String getArgumentNumber() {
        return argumentNumber;
    }

    /**
     * Set predicate name. Together with {@link #getArgumentNumber()} this
     * identifies a predicate.
     *
     * @param   name    Predicate name.
     */
    public void setName(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Set LaTeX pattern for definition visualisation. The replaceable arguments are
     * marked as <code>#1</code>, <code>#2</code> and so on. For example
     * <code>\mathfrak{M}(#1)</code>.
     *
     * @param   latexPattern    LaTeX pattern for definition visualisation.
     */
    public final void setLatexPattern(final String latexPattern) {
        this.latexPattern = latexPattern;
    }

    public final String getLatexPattern() {
        return latexPattern;
    }

    /**
     * Set defining formula or term that defines the object. Could be <code>null</code>.
     *
     * @param   formulaOrTerm   Formula or term that defines the new operator.
     */
    public final void setFormula(final FormulaVo formulaOrTerm) {
        this.formula = formulaOrTerm;
    }

    public final Formula getFormula() {
        return formula;
    }

    /**
     * Set description. Only necessary if formula is not self-explanatory.
     *
     * @param   description Description.
     */
    public final void setDescription(final LatexListVo description) {
        this.description = description;
    }

    public LatexList getDescription() {
        return description;
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof PredicateDefinition)) {
            return false;
        }
        final PredicateDefinition other = (PredicateDefinition) obj;
        return  EqualsUtility.equals(getArgumentNumber(), other.getArgumentNumber())
            &&  EqualsUtility.equals(getName(), other.getName())
            &&  EqualsUtility.equals(getLatexPattern(), other.getLatexPattern())
            &&  EqualsUtility.equals(getFormula(), other.getFormula())
            &&  EqualsUtility.equals(getDescription(), other.getDescription());
    }

    public int hashCode() {
        return (getArgumentNumber() != null ? getArgumentNumber().hashCode() : 0)
            ^ (getName() != null ? 1 ^ getName().hashCode() : 0)
            ^ (getLatexPattern() != null ? 2 ^ getLatexPattern().hashCode() : 0)
            ^ (getFormula() != null ? 4 ^ getFormula().hashCode() : 0)
            ^ (getDescription() != null ? 5 ^ getDescription().hashCode() : 0);
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("Predicate Definition arguments=" + getArgumentNumber() + "\n");
        buffer.append("\tname=" + getName() + "\n");
        buffer.append("\tpattern=" + getLatexPattern() + "\n");
        buffer.append("\tformula:\n" + getFormula() + "\n");
        buffer.append("\tdescription:\n" + getDescription() + "\n");
        return buffer.toString();
    }


}
