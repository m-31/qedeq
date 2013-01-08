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
import org.qedeq.kernel.se.base.module.ChangedRuleList;
import org.qedeq.kernel.se.base.module.FunctionDefinition;
import org.qedeq.kernel.se.base.module.InitialFunctionDefinition;
import org.qedeq.kernel.se.base.module.InitialPredicateDefinition;
import org.qedeq.kernel.se.base.module.LatexList;
import org.qedeq.kernel.se.base.module.LinkList;
import org.qedeq.kernel.se.base.module.PredicateDefinition;
import org.qedeq.kernel.se.base.module.ProofList;
import org.qedeq.kernel.se.base.module.Proposition;
import org.qedeq.kernel.se.base.module.Rule;


/**
 * Rule declaration.
 *
 * @author  Michael Meyling
 */
public class RuleVo implements Rule {

    /** List of necessary ids. */
    private LinkListVo linkList;

    /** Description. */
    private LatexList description;

    /** Rule name. */
    private String name;

    /** Rule version. */
    private String version;

    /** This rule changes the list of following rules. */
    private ChangedRuleListVo changedRuleList;

    /** Proofs for this rule. */
    private ProofListVo proofList;

    /**
     * Constructs a new rule declaration.
     */
    public RuleVo() {
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
        return null;
    }

    public Rule getRule() {
        return this;
    }

    /**
     * Set rule name.
     *
     * @param   name    Rule name.
     */
    public final void setName(final String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    /**
     * Set rule version.
     *
     * @param   version Rule version.
     */
    public final void setVersion(final String version) {
        this.version = version;
    }

    public final String getVersion() {
        return version;
    }

    /**
     * Set list of necessary ids.
     *
     * @param   linkList    List.
     */
    public final void setLinkList(final LinkListVo linkList) {
        this.linkList = linkList;
    }

    public final LinkList getLinkList() {
        return linkList;
    }

    /**
     * Add link for this rule.
     *
     * @param   id  Id to add.
     */
    public final void addLink(final String id) {
        if (linkList == null) {
            linkList = new LinkListVo();
        }
        linkList.add(id);
    }

    /**
     * Set description.
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
     * Set rule proof list.
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
     * Set list of changed rules.
     *
     * @param   changedRuleList     List of changed rules.
     */
    public final void setChangedRuleList(final ChangedRuleListVo changedRuleList) {
        this.changedRuleList = changedRuleList;
    }

    public final ChangedRuleList getChangedRuleList() {
        return changedRuleList;
    }

    /**
     * Add changed rule to this list.
     *
     * @param   changedRule     Changed rule to add.
     */
    public final void addChangedRule(final ChangedRuleVo changedRule) {
        if (changedRuleList == null) {
            changedRuleList = new ChangedRuleListVo();
        }
        changedRuleList.add(changedRule);
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof RuleVo)) {
            return false;
        }
        final RuleVo other = (RuleVo) obj;
        return  EqualsUtility.equals(getName(), other.getName())
            && EqualsUtility.equals(getVersion(), other.getVersion())
            && EqualsUtility.equals(getLinkList(), other.getLinkList())
            && EqualsUtility.equals(getDescription(), other.getDescription())
            && EqualsUtility.equals(getChangedRuleList(), other.getChangedRuleList())
            && EqualsUtility.equals(getProofList(), other.getProofList());
    }

    public int hashCode() {
        return (getName() != null ? 1 ^ getName().hashCode() : 0)
            ^ (getVersion() != null ? 2 ^ getVersion().hashCode() : 0)
            ^ (getLinkList() != null ? 3 ^ getLinkList().hashCode() : 0)
            ^ (getDescription() != null ? 4 ^ getDescription().hashCode() : 0)
            ^ (getChangedRuleList() != null ? 5 ^ getChangedRuleList().hashCode() : 0)
            ^ (getProofList() != null ? 6 ^ getProofList().hashCode() : 0);
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("Rule: " + getName() + " [" + getVersion() + "]\n");
        buffer.append(getLinkList());
        buffer.append("\nDescription:\n");
        buffer.append(getDescription());
        buffer.append(getProofList());
        buffer.append(getChangedRuleList());
        return buffer.toString();
    }
}
