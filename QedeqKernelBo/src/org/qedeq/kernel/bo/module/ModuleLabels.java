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

package org.qedeq.kernel.bo.module;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.qedeq.kernel.bo.common.ModuleReferenceList;
import org.qedeq.kernel.se.base.module.ChangedRule;
import org.qedeq.kernel.se.base.module.FunctionDefinition;
import org.qedeq.kernel.se.base.module.PredicateDefinition;
import org.qedeq.kernel.se.base.module.Rule;
import org.qedeq.kernel.se.common.CheckLevel;
import org.qedeq.kernel.se.common.IllegalModuleDataException;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.common.RuleKey;
import org.qedeq.kernel.se.dto.module.NodeVo;
import org.qedeq.kernel.se.visitor.QedeqNumbers;

/**
 * Maps labels of an QEDEQ module to their nodes. Knows all label names.
 *
 * @author  Michael Meyling
 */
public final class ModuleLabels {

    /** External QEDEQ module references. */
    private ModuleReferenceList references = new KernelModuleReferenceList();

    /** Maps labels to business objects. */
    private final Map label2Bo = new HashMap();

    /** Maps labels to context of business objects. */
    private final Map label2Context = new HashMap();

    /** Maps predicate identifiers to {@link PredicateDefinition}s. */
    private final Map predicateDefinitions = new HashMap();

    /** Maps predicate identifiers to {@link ModuleContext}s. */
    private final Map predicateContexts = new HashMap();

    /** Maps function identifiers to {@link FunctionDefinition}s. */
    private final Map functionDefinitions = new HashMap();

    /** Maps function identifiers to {@link ModuleContext}s. */
    private final Map functionContexts = new HashMap();

    /** Maps rule names to maximum {@link RuleKey}s. (That is rule key with maximum version number). */
    private final Map ruleMaximum = new HashMap();

    /** Maps rule keys to {@link Rule}s. */
    private final Map ruleDefinitions = new HashMap();

    /** Maps rule keys to {@link ModuleContext}s. */
    private final Map ruleContexts = new HashMap();

    /** Maps rule keys to labels. */
    private final Map ruleLabels = new HashMap();

    /**
     * Constructs a new empty module label list.
     */
    public ModuleLabels() {
        // nothing to do
    }

    /**
     * Set list of external QEDEQ module references.
     *
     * @param   references  External QEDEQ module references.
     */
    public void setModuleReferences(final ModuleReferenceList references) {
        this.references = references;
    }

    /**
     * Get list of external QEDEQ module references.
     *
     * @return  External QEDEQ module references.
     */
    public ModuleReferenceList getReferences() {
        return this.references;
    }

    /**
     * Add node with certain id. All numbers should start with 1.
     *
     * @param   node        Add this node.
     * @param   context     The node has this context.
     * @param   qedeq       Parent module the node is within.
     * @param   data        Various number counters.
     * @throws  IllegalModuleDataException  The <code>id</code> already exists (perhaps as a label)
     *          or is <code>null</code>.
     */
    public final void addNode(final ModuleContext context, final NodeVo node, final KernelQedeqBo qedeq,
            final QedeqNumbers data) throws IllegalModuleDataException {
        // don't forget to use the copy constructor because the context could change!
        final ModuleContext con = new ModuleContext(context);
        if (null == node.getId()) {
            throw new IllegalModuleDataException(10001, "An id was not defined.", con, null,
                null);  // LATER mime 20071026: organize exception codes
        }
        checkLabelIntern(con, node.getId());
        label2Context.put(node.getId(), con);
        final KernelNodeBo nodeBo = new KernelNodeBo(node, context, qedeq, data);
        label2Bo.put(node.getId(), nodeBo);
    }

    /**
     * Add unique label for module.
     *
     * @param   label   Add this label.
     * @param   context With this context.
     * @throws  IllegalModuleDataException  The <code>id</code> already exists or is <code>null</code>.
     */
    public final void addLabel(final ModuleContext context,  final String label)
            throws IllegalModuleDataException {
        // don't forget to use the copy constructor because the context could change!
        final ModuleContext con = new ModuleContext(context);
        checkLabelIntern(con, label);
        label2Context.put(label, con);
    }

    /**
     * Check that label doesn't exist.
     *
     * @param   label   Check this label.
     * @param   context With this context (already copied).
     * @throws  IllegalModuleDataException  The <code>id</code> already exists or is
     *          <code>null</code>.
     */
    private final void checkLabelIntern(final ModuleContext context,  final String label)
            throws IllegalModuleDataException {
        if (label2Context.containsKey(label)) {
            throw new IllegalModuleDataException(ModuleErrors.LABEL_DEFINED_MORE_THAN_ONCE_CODE,
                ModuleErrors.LABEL_DEFINED_MORE_THAN_ONCE_TEXT + "\"" + label + "\"",
                context, (ModuleContext) label2Context.get(label), null);
        }
    }

    /**
     * Get node for given id.
     *
     * @param   id   Label to search node for.
     * @return  Node for given label. Maybe <code>null</code>.
     */
    public final KernelNodeBo getNode(final String id) {
        return (KernelNodeBo) label2Bo.get(id);
    }

    /**
     * Is the given label id a node? Local node labels are not considered.
     *
     * @param   id   Label to search node for.
     * @return  Is this an node of this module?
     */
    public final boolean isNode(final String id) {
        return label2Bo.get(id) != null;
    }

    /**
     * Is the given label id a module?
     *
     * @param   id   Label to search module reference for.
     * @return  Is this an module reference id?
     */
    public final boolean isModule(final String id) {
        return label2Bo.get(id) == null && label2Context.get(id) != null;
    }

    /**
     * Add predicate definition. If such a definition already exists it is overwritten.
     *
     * @param   definition  Definition to add.
     * @param   context     Here the definition stands.
     */
    public void addPredicate(final PredicateDefinition definition, final ModuleContext context) {
        final String identifier = definition.getName() + "_" + definition.getArgumentNumber();
        getPredicateDefinitions().put(identifier, definition);
        predicateContexts.put(identifier, new ModuleContext(context));
    }

    /**
     * Get predicate definition.
     *
     * @param   name            Predicate name.
     * @param   argumentNumber  Number of predicate arguments.
     * @return  Definition. Might be <code>null</code>.
     */
    public PredicateDefinition getPredicate(final String name, final int argumentNumber) {
        return (PredicateDefinition) getPredicateDefinitions().get(name + "_" + argumentNumber);
    }

    /**
     * Get predicate context. This is only a copy.
     *
     * @param   name            Predicate name.
     * @param   argumentNumber  Number of predicate arguments.
     * @return  Module context. Might be <code>null</code>.
     */
    public ModuleContext getPredicateContext(final String name, final int argumentNumber) {
        final ModuleContext context = (ModuleContext) predicateContexts.get(name + "_" + argumentNumber);
        if (context != null) {
            return new ModuleContext(context);
        }
        return null;
    }

    /**
     * Add function definition. If such a definition already exists it is overwritten.
     *
     * @param   definition  Definition to add.
     * @param   context     Here the definition stands.
     */
    public void addFunction(final FunctionDefinition definition, final ModuleContext context) {
        final String identifier = definition.getName() + "_" + definition.getArgumentNumber();
        getFunctionDefinitions().put(identifier, definition);
        functionContexts.put(identifier, new ModuleContext(context));
    }

    /**
     * Get function definition.
     *
     * @param   name            Function name.
     * @param   argumentNumber  Number of function arguments.
     * @return  Definition. Might be <code>null</code>.
     */
    public FunctionDefinition getFunction(final String name, final int argumentNumber) {
        return (FunctionDefinition) getFunctionDefinitions().get(name + "_" + argumentNumber);
    }

    /**
     * Get function context. This is only a copy.
     *
     * @param   name            Function name.
     * @param   argumentNumber  Number of function arguments.
     * @return  Module context. Might be <code>null</code>.
     */
    public ModuleContext getFunctionContext(final String name, final int argumentNumber) {
        final ModuleContext context = (ModuleContext) functionContexts.get(name + "_" + argumentNumber);
        if (context != null) {
            return new ModuleContext(context);
        }
        return null;
    }

    /**
     * Add rule definition. If such a definition already exists it is overwritten. Also sets the
     * key for the maximum rule version.
     *
     * @param   label       Node label the rule is defined within.
     * @param   definition  Definition to add.
     * @param   context     Here the definition stands.
     */
    public void addRule(final String label, final Rule definition, final ModuleContext context) {
        final RuleKey key = new RuleKey(definition.getName(), definition.getVersion());
        ruleLabels.put(key, label);
        final RuleKey oldMaximum = (RuleKey) ruleMaximum.get(definition.getName());
        if (oldMaximum == null || oldMaximum.getVersion() == null || (key.getVersion() != null
                && 0 < key.getVersion().compareTo(oldMaximum.getVersion()))) {
            ruleMaximum.put(definition.getName(), key);
        }
        getRuleDefinitions().put(key, definition);
        ruleContexts.put(key, new ModuleContext(context));
    }

    /**
     * Add rule definition. If such a definition already exists it is overwritten. Also sets the
     * key for the maximum rule version.
     *
     * @param   label       Node label the rule is defined within.
     * @param   definition  Here we have the new rule.
     * @param   cr          New modification to an old rule.
     * @param   context     Here the definition stands.
     */
    public void addChangedRule(final String label, final Rule definition, final ChangedRule cr,
            final ModuleContext context) {
        final RuleKey key = new RuleKey(cr.getName(), cr.getVersion());
        ruleLabels.put(key, label);
        final RuleKey oldMaximum = (RuleKey) ruleMaximum.get(cr.getName());
        if (oldMaximum == null || oldMaximum.getVersion() == null || (key.getVersion() != null
                && 0 < key.getVersion().compareTo(oldMaximum.getVersion()))) {
            ruleMaximum.put(cr.getName(), key);
        }
        getRuleDefinitions().put(key, definition);
        ruleContexts.put(key, new ModuleContext(context));
    }

    /**
     * Get rule key with maximum rule version.
     *
     * @param   ruleName    Get maximum rule key for rule with this name.
     * @return  Rule key with maximum rule version. Might be <code>null</code>.
     */
    public RuleKey getRuleKey(final String ruleName) {
        return (RuleKey) ruleMaximum.get(ruleName);
    }

    /**
     * Get rule definition.
     *
     * @param   key            Rule key.
     * @return  Definition. Might be <code>null</code>.
     */
    public Rule getRule(final RuleKey key) {
        return (Rule) getRuleDefinitions().get(key);
    }

    /**
     * Get node id of rule key.
     *
     * @param   key            Rule key.
     * @return  Label. Might be <code>null</code>.
     */
    public String getRuleLabel(final RuleKey key) {
        return (String) ruleLabels.get(key);
    }

    /**
     * Get rule context. This is only a copy.
     *
     * @param   key            Rule key.
     * @return  Module context. Might be <code>null</code>.
     */
    public ModuleContext getRuleContext(final RuleKey key) {
        final ModuleContext context = (ModuleContext) ruleContexts.get(key);
        if (context != null) {
            return new ModuleContext(context);
        }
        return null;
    }

    /**
     * Get mapping of predicate definitions.
     *
     * @return  Mapping of predicate definitions.
     */
    public Map getPredicateDefinitions() {
        return this.predicateDefinitions;
    }

    /**
     * Get mapping of function definitions.
     *
     * @return  Mapping of function definitions.
     */
    public Map getFunctionDefinitions() {
        return this.functionDefinitions;
    }

    /**
     * Get mapping of rule definitions.
     *
     * @return  Mapping of rule definitions. Key is {@link RuleKey}, value is {@link Rule}.
     */
    public Map getRuleDefinitions() {
        return this.ruleDefinitions;
    }

    /**
     * Set the state of all nodes to UNCHECKED for being well formed and proved.
     */
    public void resetNodesToWellFormedUnchecked() {
        final Iterator i = label2Bo.entrySet().iterator();
        while (i.hasNext()) {
            Object obj = i.next();
            if (obj instanceof KernelNodeBo) {
                KernelNodeBo bo = (KernelNodeBo) obj;
                bo.setWellFormed(CheckLevel.UNCHECKED);
                bo.setProved(CheckLevel.UNCHECKED);
            }
        }
    }

    /**
     * Set the state of all nodes to UNCHECKED for proved.
     */
    public void resetNodesToProvedUnchecked() {
        final Iterator i = label2Bo.entrySet().iterator();
        while (i.hasNext()) {
            Object obj = i.next();
            if (obj instanceof KernelNodeBo) {
                KernelNodeBo bo = (KernelNodeBo) obj;
                bo.setProved(CheckLevel.UNCHECKED);
            }
        }
    }

}
