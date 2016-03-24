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

package org.qedeq.kernel.bo.service.basis;

import org.qedeq.base.io.SourceArea;
import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.bo.module.DefaultReference;
import org.qedeq.kernel.bo.module.InternalKernelServices;
import org.qedeq.kernel.bo.module.InternalModuleServiceCall;
import org.qedeq.kernel.bo.module.InternalServiceJob;
import org.qedeq.kernel.bo.module.KernelNodeBo;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.ModuleErrors;
import org.qedeq.kernel.bo.module.Reference;
import org.qedeq.kernel.bo.module.ReferenceLinkException;
import org.qedeq.kernel.se.base.module.Axiom;
import org.qedeq.kernel.se.base.module.FunctionDefinition;
import org.qedeq.kernel.se.base.module.Node;
import org.qedeq.kernel.se.base.module.PredicateDefinition;
import org.qedeq.kernel.se.base.module.Proposition;
import org.qedeq.kernel.se.base.module.Rule;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.RuleKey;
import org.qedeq.kernel.se.common.Service;
import org.qedeq.kernel.se.common.SourceFileException;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.visitor.AbstractModuleVisitor;
import org.qedeq.kernel.se.visitor.InterruptException;
import org.qedeq.kernel.se.visitor.QedeqNotNullTraverser;
import org.qedeq.kernel.se.visitor.QedeqNumbers;
import org.qedeq.kernel.se.visitor.QedeqTraverser;


/**
 * Basic visitor that gives some error collecting features. Also hides the
 * traverser that does the work.
 *
 * @author  Michael Meyling
 */
public abstract class ControlVisitor extends AbstractModuleVisitor {

    /** This class. */
    private static final Class CLASS = ControlVisitor.class;

    /** This service we work for. */
    private final Service service;

    /** QEDEQ BO object to work on. */
    private final KernelQedeqBo prop;

    /** We work in this service call. */
    private InternalModuleServiceCall call;

    /** Traverse QEDEQ module with this traverser. */
    private final QedeqNotNullTraverser traverser;

    /** List of Exceptions of type error during Module visit. */
    private SourceFileExceptionList errorList;

    /** List of Exceptions of type warnings during Module visit. */
    private SourceFileExceptionList warningList;

    /** Was traverse interrupted by user? */
    private boolean interrupted;

    /**
     * Constructor. Can only be used if instance also implements {@link Service}.
     *
     * @param   prop        Internal QedeqBo.
     */
    protected ControlVisitor(final KernelQedeqBo prop) {
        this.prop = prop;
        this.service = (Service) this;
        this.traverser = new QedeqNotNullTraverser(prop.getModuleAddress(), this);
        this.errorList = new SourceFileExceptionList();
        this.warningList = new SourceFileExceptionList();
    }

    /**
     * Constructor.
     *
     * @param   service      This service we work for.
     * @param   prop        Internal QedeqBo.
     */
    protected ControlVisitor(final Service service, final KernelQedeqBo prop) {
        this.service = service;
        this.prop = prop;
        this.traverser = new QedeqNotNullTraverser(prop.getModuleAddress(), this);
        this.errorList = new SourceFileExceptionList();
        this.warningList = new SourceFileExceptionList();
    }

    /**
     * Get QedeqBo.
     *
     * @return  QEDEQ module were are in.
     */
    public KernelQedeqBo getKernelQedeqBo() {
        return this.prop;
    }

    /**
     * Get node that is currently parsed. Might be <code>null</code>.
     *
     * @return  QEDEQ node were are currently in.
     */
    public KernelNodeBo getNodeBo() {
        final Node node = traverser.getNode();
        if (node == null || getKernelQedeqBo() == null) {
            return null;
        }
        return getKernelQedeqBo().getLabels().getNode(node.getId());
    }

    /**
     * Start traverse of QedeqBo. If during the traverse a {@link ModuleDataException}
     * occurs it is thrown till high level, transformed into a
     * {@link SourceFileException} and added to the error list. All collected exceptions
     * (via {@link #addError(ModuleDataException)} and
     * {@link #addError(SourceFileException)}) are thrown (if there were any).
     * <br/>
     * If you are interested in warnings you have to call {@link #getWarningList()} afterwards.
     *
     * @param   process    We work in this service process.
     * @throws  SourceFileExceptionList  All collected error exceptions.
     */
    public void traverse(final InternalServiceJob process) throws SourceFileExceptionList {
        this.call = process.getInternalServiceCall();
        interrupted = false;
        if (getKernelQedeqBo().getQedeq() == null) {
            addWarning(new SourceFileException(getService(),
                ModuleErrors.QEDEQ_MODULE_NOT_LOADED_CODE,
                ModuleErrors.QEDEQ_MODULE_NOT_LOADED_TEXT,
                new IllegalArgumentException(),
                new SourceArea(getKernelQedeqBo().getModuleAddress().getUrl()),
                null));
            return; // we can do nothing without a loaded module
        }
        try {
            this.traverser.accept(getKernelQedeqBo().getQedeq());
        } catch (InterruptException ie) {
            addError(ie);
            interrupted = true;
        } catch (ModuleDataException me) {
            addError(me);
        } catch (RuntimeException e) {
            Trace.fatal(CLASS, this, "traverse", "looks like a programming error", e);
            addError(new RuntimeVisitorException(getCurrentContext(), e));
        }
        if (errorList.size() > 0) {
            throw errorList;
        }
    }

    /**
     * Get current context within original. Remember to use the copy constructor
     * when trying to remember this context!
     *
     * @return  Current context.
     */
    public ModuleContext getCurrentContext() {
        return this.traverser.getCurrentContext();
    }

    /**
     * Add exception to error collection.
     *
     * @param   me  Exception to be added.
     */
    protected void addError(final ModuleDataException me) {
        addError(prop.createSourceFileException(getService(), me));
    }

    /**
     * Add exception to error collection.
     *
     * @param   sf  Exception to be added.
     */
    protected void addError(final SourceFileException sf) {
        errorList.add(sf);
    }

    /**
     * Get list of errors that occurred during visit.
     *
     * @return  Exception list.
     */
    public SourceFileExceptionList getErrorList() {
        return errorList;
    }

    /**
     * Did any errors occur yet?
     *
     * @return  Non error free visits?
     */
    public boolean hasErrors() {
        return errorList.size() > 0;
    }

    /**
     * Did no errors occur yet?
     *
     * @return  Error free visits?
     */
    public boolean hasNoErrors() {
        return !hasErrors();
    }

    /**
     * Add exception to warning collection.
     *
     * @param   me  Exception to be added.
     */
    protected void addWarning(final ModuleDataException me) {
        // TODO 20101026 m31: here no SourcefileException should be added!
        // there might exist different representations (e.g. XML, utf8 text, html)
        // and we might want to resolve the location for them also.
        // And perhaps resolving all error locations at the same time is
        // faster because one has to load the source file only once...
        addWarning(prop.createSourceFileException(getService(), me));
    }

    /**
     * Add exception to warning collection.
     *
     * @param   sf  Exception to be added.
     */
    private void addWarning(final SourceFileException sf) {
        warningList.add(sf);
    }

    /**
     * Get list of warnings that occurred during visit.
     *
     * @return  Exception list.
     */
    public SourceFileExceptionList getWarningList() {
        return warningList;
    }

    /**
     * Set if further traverse is blocked.
     *
     * @param   blocked     Further traverse blocked?
     */
    protected void setBlocked(final boolean blocked) {
        traverser.setBlocked(blocked);
    }

    /**
     * Get if further traverse is blocked.
     *
     * @return   Further traverse blocked?
     */
    public boolean getBlocked() {
        return traverser.getBlocked();
    }

    /**
     * Get service call we work in.
     *
     * @return  Service process we work for.
     */
    public InternalModuleServiceCall getInternalServiceCall() {
        return call;
    }

    /**
     * Get service we work for.
     *
     * @return  Service we work for.
     */
    public Service getService() {
        return service;
    }

    /**
     * Get location info from traverser.
     *
     * @return  Location description.
     */
    public String getLocationDescription() {
        return traverser.getLocationDescription();
    }

    /**
     * Get percentage of visit currently done.
     *
     * @return  Value between 0 and 100.
     */
    public double getVisitPercentage() {
        return traverser.getVisitPercentage();
    }

    /**
     * Get copy of current counters.
     *
     * @return  Values of various counters.
     */
    public QedeqNumbers getCurrentNumbers() {
        return traverser.getCurrentNumbers();
    }

    /**
     * Get current (QEDEQ module local) rule version for given rule name.
     *
     * @param   name    Rule name
     * @return  Current (local) rule version. Might be <code>null</code>.
     */
    public RuleKey getLocalRuleKey(final String name) {
        return traverser.getLocalRuleKey(name);
    }

    /**
     * Get internal kernel services. Convenience method.
     *
     * @return  Internal kernel services.
     */
    public InternalKernelServices getServices() {
        return prop.getKernelServices();
    }

    /**
     * Was traverse interrupted by user?
     *
     * @return  Did we get an interrupt?
     */
    public boolean getInterrupted() {
        return interrupted;
    }

    /**
     * Get link for given reference.
     *
     * @param   reference   String to parse.
     * @param   context     Here the link is in the source text.
     * @param   addWarning  Should we add a warning if an error occurs?
     * @param   addError    Should we add an error if an error occurs?
     * @return  Generated link. Never <code>null</code>.
     */
    public Reference getReference(final String reference, final ModuleContext context,
            final boolean addWarning, final boolean addError) {
        // get node we are currently in
        KernelNodeBo node = getNodeBo();
        final Reference fallback = new DefaultReference(node, null, "", null,
            (reference != null ? reference : "") + "?", "", "");
        if (reference == null || reference.length() <= 0) {
            return fallback;
        }
        if (reference.indexOf("!") >= 0 && reference.indexOf("/") >= 0) {
            if (addWarning) {
                addWarning(new ReferenceLinkException(
                    ModuleErrors.REFERENCE_CAN_NOT_CONTAIN_SUB_AND_LINE_REFERENCE_CODE,
                    ModuleErrors.REFERENCE_CAN_NOT_CONTAIN_SUB_AND_LINE_REFERENCE_TEXT
                        + "\"" + reference + "\"", context));
            }
            if (addError) {
                addError(new ReferenceLinkException(
                        ModuleErrors.REFERENCE_CAN_NOT_CONTAIN_SUB_AND_LINE_REFERENCE_CODE,
                        ModuleErrors.REFERENCE_CAN_NOT_CONTAIN_SUB_AND_LINE_REFERENCE_TEXT
                            + "\"" + reference + "\"", context));
            }
        }
        // is the reference a pure proof line label?
        if (node != null && node.isProofLineLabel(reference)) {
            return new DefaultReference(node, null, "", node, node.getNodeVo().getId(), "", reference);
        }
        // is the reference a pure node label?
        if (getKernelQedeqBo().getLabels().isNode(reference)) {
            return new DefaultReference(node, null, "", getKernelQedeqBo().getLabels().getNode(
                reference), reference, "", "");
        }
        // do we have an external module reference without node?
        if (getKernelQedeqBo().getLabels().isModule(reference)) {
            return new DefaultReference(node,
                 (KernelQedeqBo) getKernelQedeqBo().getLabels().getReferences().getQedeqBo(reference),
                 reference, null, "", "", "");

        }

        String moduleLabel = "";                // module import
        String nodeLabel = "";                  // module intern node reference
        String lineLabel = "";                  // proof line label
        String subLabel = "";                   // sub label

        final String[] split = StringUtility.split(reference, ".");
        if (split.length <= 1 || split.length > 2) {
            if (split.length == 1) {
                nodeLabel = split[0];
            } else if (split.length > 2) {
                if (addWarning) {
                    addWarning(new ReferenceLinkException(
                        ModuleErrors.NODE_REFERENCE_HAS_MORE_THAN_ONE_DOT_CODE,
                        ModuleErrors.NODE_REFERENCE_HAS_MORE_THAN_ONE_DOT_TEXT
                        + "\"" + reference + "\"", context));
                }
                if (addError) {
                    addError(new ReferenceLinkException(
                        ModuleErrors.NODE_REFERENCE_HAS_MORE_THAN_ONE_DOT_CODE,
                        ModuleErrors.NODE_REFERENCE_HAS_MORE_THAN_ONE_DOT_TEXT
                        + "\"" + reference + "\"", context));
                }
                return fallback;
            }
        } else {
            moduleLabel = split[0];
            nodeLabel = split[1];
        }

        if (nodeLabel.indexOf("!") >= 0) {
            final String[] split2 = StringUtility.split(nodeLabel, "!");
            if (split2.length != 2) {
                if (addWarning) {
                    addWarning(new ReferenceLinkException(
                        ModuleErrors.NODE_REFERENCE_MUST_HAVE_ONLY_ONE_PROOF_LINE_REFERENCE_CODE,
                        ModuleErrors.NODE_REFERENCE_MUST_HAVE_ONLY_ONE_PROOF_LINE_REFERENCE_TEXT
                        + "\"" + reference + "\"", context));
                }
                if (addError) {
                    addError(new ReferenceLinkException(
                        ModuleErrors.NODE_REFERENCE_MUST_HAVE_ONLY_ONE_PROOF_LINE_REFERENCE_CODE,
                        ModuleErrors.NODE_REFERENCE_MUST_HAVE_ONLY_ONE_PROOF_LINE_REFERENCE_TEXT
                        + "\"" + reference + "\"", context));
                }
            }
            nodeLabel = split2[0];
            if (split.length > 1) {
                lineLabel = split2[1];
            }
        }
        if (nodeLabel.indexOf("/") >= 0) {
            final String[] split2 = StringUtility.split(nodeLabel, "/");
            if (split2.length != 2) {
                if (addWarning) {
                    addWarning(new ReferenceLinkException(
                        ModuleErrors.NODE_REFERENCE_MUST_HAVE_ONLY_ONE_SUB_REFERENCE_CODE,
                        ModuleErrors.NODE_REFERENCE_MUST_HAVE_ONLY_ONE_SUB_REFERENCE_TEXT
                        + "\"" + reference + "\"", context));
                }
                if (addError) {
                    addError(new ReferenceLinkException(
                        ModuleErrors.NODE_REFERENCE_MUST_HAVE_ONLY_ONE_SUB_REFERENCE_CODE,
                        ModuleErrors.NODE_REFERENCE_MUST_HAVE_ONLY_ONE_SUB_REFERENCE_TEXT
                        + "\"" + reference + "\"", context));
                }
            }
            nodeLabel = split2[0];
            if (split.length > 1) {
                subLabel = split2[1];
            }
        }
        KernelQedeqBo module = null;
        KernelNodeBo eNode = null;
        if (moduleLabel != null && moduleLabel.length() > 0) {
            module = getKernelQedeqBo().getKernelRequiredModules().getKernelQedeqBo(moduleLabel);
            eNode = (module != null ? module.getLabels().getNode(nodeLabel) : null);
        } else {
            eNode = getKernelQedeqBo().getLabels().getNode(nodeLabel);
        }
        if ((moduleLabel != null && moduleLabel.length() > 0) &&  module == null) {
            if (addWarning) {
                addWarning(new ReferenceLinkException(
                    ModuleErrors.MODULE_REFERENCE_NOT_FOUND_CODE,
                    ModuleErrors.MODULE_REFERENCE_NOT_FOUND_TEXT
                    + "\"" + reference + "\"", context));
            }
            if (addError) {
                addError(new ReferenceLinkException(
                    ModuleErrors.MODULE_REFERENCE_NOT_FOUND_CODE,
                    ModuleErrors.MODULE_REFERENCE_NOT_FOUND_TEXT
                    + "\"" + reference + "\"", context));
            }
            return new DefaultReference(node, module, moduleLabel + "?", eNode, nodeLabel, subLabel, lineLabel);
        }
        if (eNode == null) {
            if (addWarning) {
                addWarning(new ReferenceLinkException(
                    ModuleErrors.NODE_REFERENCE_NOT_FOUND_CODE,
                    ModuleErrors.NODE_REFERENCE_NOT_FOUND_TEXT
                    + "\"" + reference + "\"", context));
            }
            if (addError) {
                addError(new ReferenceLinkException(
                    ModuleErrors.NODE_REFERENCE_NOT_FOUND_CODE,
                    ModuleErrors.NODE_REFERENCE_NOT_FOUND_TEXT
                    + "\"" + reference + "\"", context));
            }
            return new DefaultReference(node, module, moduleLabel, eNode, nodeLabel + "?", subLabel, lineLabel);
        }
        return new DefaultReference(node, module, moduleLabel, eNode, nodeLabel, subLabel, lineLabel);
    }

    /**
     * Get display text for node. The module of the node is ignored.
     *
     * @param   label       Label for node. Fallback if <code>kNode == null</code>.
     * @param   kNode       Node for which we want a textual representation.
     * @param   language    Language. Might be <code>null</code>.
     * @return  Textual node representation.
     */
    public String getNodeDisplay(final String label, final KernelNodeBo kNode, final String language) {
        String display = label;
        if (kNode == null) {
            return display;
        }
        QedeqNumbers data = kNode.getNumbers();
        Node node = kNode.getNodeVo();
        if (node.getNodeType() instanceof Axiom) {
            if ("de".equals(language)) {
                display = "Axiom";
            } else {
                display = "axiom";
            }
            display += " " + data.getAxiomNumber();
        } else if (node.getNodeType() instanceof Proposition) {
            if ("de".equals(language)) {
                display = "Proposition";
            } else {
                display = "proposition";
            }
            display += " " + data.getPropositionNumber();
        } else if (node.getNodeType() instanceof FunctionDefinition) {
            if ("de".equals(language)) {
                display = "Definition";
            } else {
                display = "definition";
            }
            display += " " + (data.getPredicateDefinitionNumber() + data.getFunctionDefinitionNumber());
        } else if (node.getNodeType() instanceof PredicateDefinition) {
            if ("de".equals(language)) {
                display = "Definition";
            } else {
                display = "definition";
            }
            display += " " + (data.getPredicateDefinitionNumber() + data.getFunctionDefinitionNumber());
        } else if (node.getNodeType() instanceof Rule) {
            if ("de".equals(language)) {
                display = "Regel";
            } else {
                display = "rule";
            }
            display += " " + data.getRuleNumber();
        } else {
            if ("de".equals(language)) {
                display = "Unbekannt " + node.getId();
            } else {
                display = "unknown " + node.getId();
            }
        }
        return display;
    }

    /**
     * Set location information where are we within the original module.
     *
     * @param   locationWithinModule    Location within module.
     */
    public void setLocationWithinModule(final String locationWithinModule) {
        getCurrentContext().setLocationWithinModule(locationWithinModule);
        getServices().getContextChecker().checkContext(getKernelQedeqBo().getQedeq(), getCurrentContext());
    }

    /**
     * Get traverser for QEDEQ module.
     *
     * @return  Traverser used.
     */
    public QedeqTraverser getTraverser() {
        return traverser;
    }

}
