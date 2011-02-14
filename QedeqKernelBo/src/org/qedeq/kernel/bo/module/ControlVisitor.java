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

package org.qedeq.kernel.bo.module;

import org.qedeq.base.io.SourceArea;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.se.base.module.Node;
import org.qedeq.kernel.se.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.Plugin;
import org.qedeq.kernel.se.common.SourceFileException;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.visitor.AbstractModuleVisitor;
import org.qedeq.kernel.se.visitor.QedeqNotNullTraverser;
import org.qedeq.kernel.se.visitor.QedeqNumbers;


/**
 * Basic visitor that gives some error collecting features. Also hides the
 * traverser that does the work.
 *
 * @author  Michael Meyling
 */
public abstract class ControlVisitor extends AbstractModuleVisitor {

    /** This plugin we work for. */
    private final Plugin plugin;

    /** QEDEQ BO object to work on. */
    private final KernelQedeqBo prop;

    /** Traverse QEDEQ module with this traverser. */
    private final QedeqNotNullTraverser traverser;

    /** List of Exceptions of type error during Module visit. */
    private DefaultSourceFileExceptionList errorList;

    /** List of Exceptions of type warnings during Module visit. */
    private DefaultSourceFileExceptionList warningList;


    /**
     * Constructor. Can only be used if instance also implements {@link Plugin}.
     *
     * @param   prop        Internal QedeqBo.
     */
    protected ControlVisitor(final KernelQedeqBo prop) {
        this.prop = prop;
        this.plugin = (Plugin) this;
        this.traverser = new QedeqNotNullTraverser(prop.getModuleAddress(), this);
    }

    /**
     * Constructor.
     *
     * @param   plugin      This plugin we work for.
     * @param   prop        Internal QedeqBo.
     */
    protected ControlVisitor(final Plugin plugin, final KernelQedeqBo prop) {
        this.plugin = plugin;
        this.prop = prop;
        this.traverser = new QedeqNotNullTraverser(prop.getModuleAddress(), this);
    }

    /**
     * Get QedeqBo.
     *
     * @return  QEDEQ module were are in.
     */
    public KernelQedeqBo getQedeqBo() {
        return this.prop;
    }

    /**
     * Get node that is currently parsed. Might be <code>null</code>.
     *
     * @return  QEDEQ node were are currently in.
     */
    public KernelNodeBo getNodeBo() {
        final Node node = traverser.getNode();
        if (node == null) {
            return null;
        }
        return getQedeqBo().getLabels().getNode(node.getId());
    }

    /**
     * Start traverse of QedeqBo. If during the traverse a {@link ModuleDataException}
     * occurs it is thrown till high level and transformed into a
     * {@link DefaultSourceFileExceptionList}. Otherwise all collected exceptions
     * (via {@link #addError(ModuleDataException)} and
     * {@link #addError(SourceFileException)}) are thrown.
     *
     * @throws  SourceFileExceptionList  All collected exceptions.
     */
    public void traverse() throws SourceFileExceptionList {
        if (getQedeqBo().getQedeq() == null) {
            addWarning(new SourceFileException(getPlugin(),
                ModuleErrors.QEDEQ_MODULE_NOT_LOADED_CODE,
                ModuleErrors.QEDEQ_MODULE_NOT_LOADED_TEXT,
                new IllegalArgumentException(),
                new SourceArea(getQedeqBo().getModuleAddress().getUrl()),
                null));
            return; // we can do nothing without a loaded module
        }
        try {
            this.traverser.accept(getQedeqBo().getQedeq());
        } catch (ModuleDataException me) {
            addError(me);
        } catch (RuntimeException e) {
            addError(new RuntimeVisitorException(getCurrentContext(), e));
        }
        if (errorList != null && errorList.size() > 0) {
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
        addError(prop.createSourceFileException(getPlugin(), me));
    }

    /**
     * Add exception to error collection.
     *
     * @param   sf  Exception to be added.
     */
    protected void addError(final SourceFileException sf) {
        if (errorList == null) {
            errorList = new DefaultSourceFileExceptionList(sf);
        } else {
            errorList.add(sf);
        }
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
     * Add exception to warning collection.
     *
     * @param   me  Exception to be added.
     */
    protected void addWarning(final ModuleDataException me) {
        // FIXME 20101026 m31: here no SourcefileException should be added!
        // there might exist different representations (e.g. XML, utf8 text, html)
        // and we might want to resolve the location for them also.
        // And perhaps resolving all error locations at the same time is
        // faster because one has to load the source file only once...
        addWarning(prop.createSourceFileException(getPlugin(), me));
    }

    /**
     * Add exception to warning collection.
     *
     * @param   sf  Exception to be added.
     */
    private void addWarning(final SourceFileException sf) {
        if (warningList == null) {
            warningList = new DefaultSourceFileExceptionList(sf);
        } else {
            warningList.add(sf);
        }
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
     * Get plugin we work for.
     *
     * @return  Plugin we work for.
     */
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Get location info from traverser.
     *
     * @return  Location description.
     */
    public String getExecutionActionDescription() {
        return traverser.getVisitAction();
    }

    /**
     * Get percentage of visit currently done.
     *
     * @return  Value between 0 and 100.
     */
    public double getExecutionPercentage() {
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
     * Get link for given reference.
     *
     * @param   reference   String to parse.
     * @param   context     Here the link is in the source text.
     * @param   addWarning  Should we add a warning if an error occurs?
     * @param   addError    Should we add an error if an error occurs?
     * @return  Generated link.
     */
    public Reference getReference(final String reference, final ModuleContext context,
            final boolean addWarning, final boolean addError) {

        // get node we are currently in
        KernelNodeBo node = getNodeBo();

        final Reference fallback = new DefaultReference(node, null, "", null, reference + "?", "", "");

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
        if (node != null && node.isLocalLabel(reference)) {
            return new DefaultReference(node, null, "", node, node.getNodeVo().getId(), "", reference);
        }

        // is the reference a pure node label?
        if (getQedeqBo().getLabels().isNode(reference)) {
            return new DefaultReference(node, null, "", node, reference, "", "");
        }

        // do we have an external module reference without node?
        if (getQedeqBo().getLabels().isModule(reference)) {
            return new DefaultReference(node,
                 (KernelQedeqBo) getQedeqBo().getLabels().getReferences().getQedeqBo(reference),
                 reference, null, "", "", "");

        }

        final String[] split = StringUtility.split(reference, ".");
        if (split.length <= 1 || split.length > 2) {
            if (split.length <= 1) {
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
            }
            if (split.length > 2) {
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
            }
            return fallback;
        }

        final String moduleLabel = split[0];    // module import
        final String nodeLabel = split[1];      // module intern node reference
        String lineLabel = "";                  // proof line label
        String subLabel = "";                   // sub label
        if (nodeLabel.indexOf("!") >= 0) {
            nodeLabel.substring(nodeLabel.indexOf("!") + 1);
        }
        if (nodeLabel.indexOf("/") >= 0) {
            nodeLabel.substring(nodeLabel.indexOf("/") + 1);
        }
        final KernelQedeqBo module = getQedeqBo().getKernelRequiredModules().getKernelQedeqBo(moduleLabel);
        final KernelNodeBo eNode = (module != null ? module.getLabels().getNode(nodeLabel) : null);
        if (module == null) {
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

}
