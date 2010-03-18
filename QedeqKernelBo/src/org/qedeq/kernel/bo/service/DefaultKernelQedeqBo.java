/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2010,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.service;

import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.bo.ModuleReferenceList;
import org.qedeq.kernel.bo.logic.wf.ExistenceChecker;
import org.qedeq.kernel.bo.module.InternalKernelServices;
import org.qedeq.kernel.bo.module.KernelModuleReferenceList;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.ModuleLabels;
import org.qedeq.kernel.bo.module.PluginBo;
import org.qedeq.kernel.bo.module.QedeqFileDao;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.DependencyState;
import org.qedeq.kernel.common.LoadingState;
import org.qedeq.kernel.common.LogicalState;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.ModuleContext;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.Plugin;
import org.qedeq.kernel.common.SourceArea;
import org.qedeq.kernel.common.SourceFileException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.dto.module.QedeqVo;


/**
 * Represents a module and its states. This is a kernel intern representation.
 *
 * @author  Michael Meyling
 */
public class DefaultKernelQedeqBo implements KernelQedeqBo {

    /** Internal kernel services. */
    private final InternalKernelServices services;

    /** Address and module specification. */
    private final ModuleAddress address;

    /** Loaded QEDEQ module. */
    private QedeqVo qedeq;

    /** Required QEDEQ modules. */
    private KernelModuleReferenceList required;

    /** Dependent QEDEQ modules. */
    private KernelModuleReferenceList dependent;

    /** Predicate and function constant existence checker. */
    private ExistenceChecker checker;

    /** Labels for this module. */
    private ModuleLabels labels;

    /** Loader used for loading this object. */
    private QedeqFileDao loader;

    /** State change manager. */
    private final StateManager stateManager;

    /**
     * Creates new module properties.
     *
     * @param   services    Internal kernel services.
     * @param   address     Module address (not <code>null</code>).
     * @throws  NullPointerException    <code>address</code> is null.
     */
    public DefaultKernelQedeqBo(final InternalKernelServices services,
            final ModuleAddress address) {
        this.services = services;
        this.address = address;
        if (address == null) {
            throw new NullPointerException("ModuleAddress must not be null");
        }
        required = new KernelModuleReferenceList();
        dependent = new KernelModuleReferenceList();
        stateManager = new StateManager(this);
    }

    /**
     * Set loader used for loading this object.
     *
     * @param loader
     */
    public void setQedeqFileDao(final QedeqFileDao loader) {
        this.loader = loader;
    }

    /**
     * Get loader used to load this object.
     *
     * @return  Loader.
     */
    public QedeqFileDao getLoader() {
        return this.loader;
    }

    public boolean hasFailures() {
        return stateManager.hasFailures();
    }

    public ModuleAddress getModuleAddress() {
        return address;
    }

    /**
     * Get internal kernel services.
     *
     * @return  Internal kernel services.
     */
    public InternalKernelServices getKernelServices() {
        return this.services;
    }

    /**
     * Set completeness percentage.
     *
     * @param   completeness    Completeness of loading into memory.
     */
    public void setLoadingCompleteness(final int completeness) {
        stateManager.setLoadingCompleteness(completeness);
    }

    public int getLoadingCompleteness() {
        return stateManager.getLoadingCompleteness();
    }

    /**
     * Delete QEDEQ module. Invalidates all dependent modules.
     */
    public void delete() {
        stateManager.delete();
    }

    /**
     * Set loading progress module state.
     *
     * @param   state   Module loading state. Must not be <code>null</code>.
     * @throws  IllegalStateException   State is a failure state or module loaded state.
     */
    public void setLoadingProgressState(final LoadingState state) {
        stateManager.setLoadingProgressState(state);
    }

    /**
     * Set failure module state.
     *
     * @param   state   Module loading state. Must not be <code>null</code>.
     * @param   e       Exception that occurred during loading. Must not be <code>null</code>.
     * @throws  IllegalArgumentException    <code>state</code> is no failure state
     */
    public void setLoadingFailureState(final LoadingState state,
            final SourceFileExceptionList e) {
        stateManager.setLoadingFailureState(state, e);
    }

    public LoadingState getLoadingState() {
        return stateManager.getLoadingState();
    }

    public boolean isLoaded() {
        return stateManager.isLoaded();
    }

    /**
     * Set loading state to "loaded". Also puts <code>null</code> to {@link #getLabels()}.
     *
     * @param   qedeq   This module was loaded. Must not be <code>null</code>.
     * @param   labels  Module labels.
     * @throws  NullPointerException    One argument was <code>null</code>.
     */
    public void setLoaded(final QedeqVo qedeq, final ModuleLabels labels) {
        stateManager.setLoaded(qedeq, labels);
    }

    public Qedeq getQedeq() {
        return this.qedeq;
    }

    /**
     * Set dependency progress module state.
     *
     * @param   state   Module state. Must not be <code>null</code>.
     * @throws  IllegalStateException       Module is not yet loaded.
     * @throws  IllegalArgumentException    <code>state</code> is failure state or loaded required
     *                                      state.
     * @throws  NullPointerException        <code>state</code> is <code>null</code>.
     */
    public void setDependencyProgressState(final DependencyState state) {
        stateManager.setDependencyProgressState(state);
    }

   /**
    * Set failure module state.
    *
    * @param   state   Module dependency state. Must not be <code>null</code>.
    * @param   e       Exception that occurred during loading. Must not be <code>null</code>.
    * @throws  IllegalStateException       Module is not yet loaded.
    * @throws  IllegalArgumentException    <code>state</code> is no failure state.
    * @throws  NullPointerException        <code>state</code> is <code>null</code>.
    */
    public void setDependencyFailureState(final DependencyState state,
            final SourceFileExceptionList e) {
        stateManager.setDependencyFailureState(state, e);
    }

    public DependencyState getDependencyState() {
        return stateManager.getDependencyState();
    }

    /**
     * Set loaded required requirements state.
     *
     * @param   list  URLs of all referenced modules. Must not be <code>null</code>.
     * @throws  IllegalStateException   Module is not yet loaded.
     */
    public void setLoadedRequiredModules(final KernelModuleReferenceList list) {
        stateManager.setLoadedRequiredModules(list);
    }

    public ModuleReferenceList getRequiredModules() {
        return getKernelRequiredModules();
    }

    /**
     * Get labels and URLs of all referenced modules.
     *
     * @return  URLs of all referenced modules.
     */
    public KernelModuleReferenceList getKernelRequiredModules() {
        return required;
    }

    public boolean hasLoadedRequiredModules() {
        return stateManager.hasLoadedRequiredModules();
    }

    /**
     * Get labels and URLs of all directly dependent modules.
     *
     * @return  URLs of all referenced modules.
     */
    public KernelModuleReferenceList getDependentModules() {
        return dependent;
    }

    /**
     * Set logic checked state. Also set the predicate and function existence checker.
     *
     * @param   checker Checks if a predicate or function constant is defined.
     */
    public void setChecked(final ExistenceChecker checker) {
        stateManager.setChecked(checker);
    }

    public ExistenceChecker getExistenceChecker() {
        return checker;
    }

    public boolean isChecked() {
        return stateManager.isChecked();
    }

   /**
    * Set loading progress module state. Must not be <code>null</code>.
    *
    * @param   state   module state
    */
    public void setLogicalProgressState(final LogicalState state) {
        stateManager.setLogicalProgressState(state);
    }

    /**
     * Set failure module state.
     *
     * @param   state   module state
     * @param   e       Exception that occurred during loading.
     * @throws  IllegalArgumentException    <code>state</code> is no failure state
     */
    public void setLogicalFailureState(final LogicalState state,
            final SourceFileExceptionList e) {
        stateManager.setLogicalFailureState(state, e);
    }

    public LogicalState getLogicalState() {
        return stateManager.getLogicalState();
    }

    public SourceFileExceptionList getErrors() {
        return stateManager.getErrors();
    }

    public SourceFileExceptionList getWarnings() {
        return stateManager.getWarnings();
    }

    public String getStateDescription() {
        return stateManager.getStateDescription();
    }

    public String getName() {
        if (address == null) {
            return "null";
        }
        return address.getName();
    }

    public String getRuleVersion() {
        if (address == null || qedeq == null
                || qedeq.getHeader() == null
                || qedeq.getHeader().getSpecification() == null
                || qedeq.getHeader().getSpecification().getRuleVersion() == null) {
            return "";
        }
        return qedeq.getHeader().getSpecification().getRuleVersion();
    }

    public String getUrl() {
        if (this.address == null) {
            return null;
        }
        return this.address.getUrl();
    }

    /**
     * Set label references for QEDEQ module.
     *
     * @param   labels  Label references.
     */
    public void setLabels(final ModuleLabels labels) {
        this.labels = labels;
    }

    public ModuleLabels getLabels() {
        return labels;
    }

    /**
     * Create exception out of {@link ModuleDataException}.
     *
     * @param   plugin      This plugin generated the error.
     * @param   exception   Take this exception.
     * @return  Newly created instance.
     */
    public SourceFileExceptionList createSourceFileExceptionList(final Plugin plugin,
            final ModuleDataException exception) {
        final SourceFileException e = new SourceFileException(plugin, exception,
            createSourceArea(qedeq, exception.getContext()), loader.createSourceArea(qedeq,
                exception.getReferenceContext()));
        final DefaultSourceFileExceptionList list = new DefaultSourceFileExceptionList(e);
        return list;
    }

    /**
     * Create exception out of {@link ModuleDataException}.
     *
     * @param   plugin      This plugin generated the error.
     * @param   exception   Take this exception.
     * @param   qedeq       Take this QEDEQ source. (This might not be accessible via
     *                      {@link #getQedeq()}.
     * @return  Newly created instance.
     */
    public SourceFileExceptionList createSourceFileExceptionList(final Plugin plugin,
            final ModuleDataException exception, final Qedeq qedeq) {
        final SourceFileException e = new SourceFileException(plugin, exception,
            createSourceArea(qedeq, exception.getContext()), loader.createSourceArea(qedeq,
                exception.getReferenceContext()));
        final DefaultSourceFileExceptionList list = new DefaultSourceFileExceptionList(e);
        return list;
    }

    public SourceFileException createSourceFileException(final Plugin plugin, final ModuleDataException
            exception) {
        final SourceFileException e = new SourceFileException(plugin, exception,
            createSourceArea(qedeq, exception.getContext()), loader.createSourceArea(qedeq,
                exception.getReferenceContext()));
        return e;
    }

    /**
     * Create area in source file for QEDEQ module context.
     *
     * @param   qedeq       Look at this QEDEQ module.
     * @param   context     Search for this context.
     * @return  Created file area. Maybe <code>null</code>.
     */
    public SourceArea createSourceArea(final Qedeq qedeq, final ModuleContext context) {
        return loader.createSourceArea(qedeq, context);
    }

    /**
     * Set {@link QedeqVo}. Doesn't do any status handling. Only for internal use.
     *
     * @param   qedeq   Set this value.
     */
    public void setQedeqVo(final QedeqVo qedeq) {
        this.qedeq = qedeq;
    }

    /**
     * Get {@link StateManager}. Only for internal use.
     *
     * @return StateManager
     */
    protected StateManager getStateManager() {
        return this.stateManager;
    }

    /**
     * Set {@link ExistenceChecker}. Doesn't do any status handling. Only for internal use.
     *
     * @param   checker Set this checker.
     */
    protected void setExistenceChecker(final ExistenceChecker checker) {
        this.checker = checker;
    }

    public int hashCode() {
        return (getModuleAddress() == null ? 0 : getModuleAddress().hashCode());
    }

    public boolean equals(final Object obj) {
        if (obj instanceof DefaultKernelQedeqBo) {
            return EqualsUtility.equals(((DefaultKernelQedeqBo) obj).getModuleAddress(),
                this.getModuleAddress());
        }
        return false;
    }

    public String toString() {
       return address.getUrl();
    }

    public void addPluginErrors(final PluginBo plugin, final SourceFileExceptionList errors, final SourceFileExceptionList warnings) {
        stateManager.addPluginResults(plugin, errors, warnings);
    }

}
