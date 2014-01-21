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

package org.qedeq.kernel.se.dto.module;

import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.se.base.module.Axiom;
import org.qedeq.kernel.se.base.module.FormalProofList;
import org.qedeq.kernel.se.base.module.Formula;
import org.qedeq.kernel.se.base.module.FunctionDefinition;
import org.qedeq.kernel.se.base.module.InitialFunctionDefinition;
import org.qedeq.kernel.se.base.module.InitialPredicateDefinition;
import org.qedeq.kernel.se.base.module.LatexList;
import org.qedeq.kernel.se.base.module.PredicateDefinition;
import org.qedeq.kernel.se.base.module.ProofList;
import org.qedeq.kernel.se.base.module.Proposition;
import org.qedeq.kernel.se.base.module.Rule;


/**
 * Proposition.
 *
 * @author  Michael Meyling
 */
public class PropositionVo implements Proposition {

    /** Proposition formula. */
    private Formula formula;

    /** Further proposition description. Normally <code>null</code>. */
    private LatexList description;

    /** List of non formal proofs. */
    private ProofListVo proofList;

    /** List of formal proofs. */
    private FormalProofListVo formalProofList;

    /**
     * Constructs a new proposition.
     */
    public PropositionVo() {
        // nothing to do
    }

    public Axiom getAxiom() {
        return null;
    }

    public InitialPredicateDefinition getInitialPredicateDefinition() {
        return null;
    }

    public PredicateDefinition getPredicateDefinition() {
        return null;
    }

    public InitialFunctionDefinition getInitialFunctionDefinition() {
        return null;
    }

    public FunctionDefinition getFunctionDefinition() {
        return null;
    }

    public Proposition getProposition() {
        return this;
    }

    public Rule getRule() {
        return null;
    }

    /**
     * Set proposition formula.
     *
     * @param   formula Proposition formula.
     */
    public final void setFormula(final FormulaVo formula) {
        this.formula = formula;
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

    /**
     * Set proof list.
     *
     * @param   proofList   Proof list.
     */
    public final void setProofList(final ProofListVo proofList) {
        this.proofList = proofList;
    }

    public final ProofList getProofList() {
        return proofList;
    }

    /**
     * Add proof to this list.
     *
     * @param   proof   Proof to add.
     */
    public final void addProof(final ProofVo proof) {
        if (proofList == null) {
            proofList = new ProofListVo();
        }
        proofList.add(proof);
    }

    /**
     * Set proof list.
     *
     * @param   formalProofList Proof list.
     */
    public final void setFormalProofList(final FormalProofListVo formalProofList) {
        this.formalProofList = formalProofList;
    }

    public final FormalProofList getFormalProofList() {
        return formalProofList;
    }

    /**
     * Add non formal proof to this list.
     *
     * @param   proof   Proof to add.
     */
    public final void addFormalProof(final FormalProofVo proof) {
        if (formalProofList == null) {
            formalProofList = new FormalProofListVo();
        }
        formalProofList.add(proof);
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof PropositionVo)) {
            return false;
        }
        final PropositionVo other = (PropositionVo) obj;
        return  EqualsUtility.equals(getFormula(), other.getFormula())
                && EqualsUtility.equals(getDescription(), other.getDescription())
                && EqualsUtility.equals(getProofList(), other.getProofList())
                && EqualsUtility.equals(getFormalProofList(), other.getFormalProofList());
    }

    public int hashCode() {
        return (getFormula() != null ? getFormula().hashCode() : 0)
            ^ (getDescription() != null ? 1 ^ getDescription().hashCode() : 0)
            ^ (getProofList() != null ? 2 ^ getProofList().hashCode() : 0)
            ^ (getFormalProofList() != null ? 3 ^ getFormalProofList().hashCode() : 0);
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("Proposition:\n");
        buffer.append(getFormula());
        buffer.append("\nDescription:\n");
        buffer.append(getDescription());
        buffer.append("\nProofs:\n");
        buffer.append(getProofList());
        buffer.append("\nFormal Proofs:\n");
        buffer.append(getFormalProofList());
        return buffer.toString();
    }

}
