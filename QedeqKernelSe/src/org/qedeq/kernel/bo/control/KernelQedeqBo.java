/* $Id: DefaultModuleProperties.java,v 1.6 2008/01/26 12:39:08 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2008,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.control;

import java.net.URL;

import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.bo.logic.ExistenceChecker;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.DependencyState;
import org.qedeq.kernel.common.LoadingState;
import org.qedeq.kernel.common.LogicalState;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.ModuleContext;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.ModuleNodes;
import org.qedeq.kernel.common.ModuleReferenceList;
import org.qedeq.kernel.common.QedeqBo;
import org.qedeq.kernel.common.SourceArea;
import org.qedeq.kernel.common.SourceFileException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.dto.module.QedeqVo;
import org.qedeq.kernel.utility.EqualsUtility;


/**
 * Represents a module and its states. This is a kernel intern representation.
 *
 * @version $Revision: 1.6 $
 * @author  Michael Meyling
 */
public class KernelQedeqBo implements QedeqBo {

    /** Address and module specification. */
    private final ModuleAddress address;

    /** Completeness during loading from web. */
    private int loadingCompleteness;

    /** Describes QEDEQ module loading state. */
    private LoadingState loadingState;

    /** Describes QEDEQ module dependency state. */
    private DependencyState dependencyState;

    /** Describes QEDEQ module logical state. */
    private LogicalState logicalState;

    /** Loaded QEDEQ module. */
    private QedeqVo qedeq;

    /** Failure exception. */
    private SourceFileExceptionList exception;

    /** Required QEDEQ modules. */
    private KernelModuleReferenceList required;

    /** Dependent QEDEQ modules. */
    private KernelModuleReferenceList dependent;

    /** Predicate and function constant existence checker. */
    private ExistenceChecker checker;

    /** Character encoding for this module. */
    private String encoding;

    /** Labels for this module. */
    private ModuleNodes labels;

    /** Loader used for loading this object. */
    private ModuleLoader loader;

    /** State change manager. */
    private final StateManager stateManager;

    /**
     * Creates new module properties.
     *
     * @param   address     Module address (not <code>null</code>).
     * @throws  NullPointerException    <code>address</code> is null.
     */
    KernelQedeqBo(final ModuleAddress address) {
        this.address = address;
        if (address == null) {
            throw new NullPointerException("ModuleAddress must not be null");
        }
        loadingState = LoadingState.STATE_UNDEFINED;
        loadingCompleteness = 0;
        dependencyState = DependencyState.STATE_UNDEFINED;
        logicalState = LogicalState.STATE_UNCHECKED;
        required = new KernelModuleReferenceList();
        dependent = new KernelModuleReferenceList();
        stateManager = new StateManager(this);
    }

    /**
     * Set loader used for loading this object.
     *
     * @param loader
     */
    public void setLoader(final ModuleLoader loader) {
        this.loader = loader;
    }

    /**
     * Get loader used to load this object.
     *
     * @return  Loader.
     */
    public ModuleLoader getLoader() {
        return this.loader;
    }

    public boolean hasFailures() {
        return loadingState.isFailure() || dependencyState.isFailure() || logicalState.isFailure();
    }

    public ModuleAddress getModuleAddress() {
        return address;
    }

    /**
     * Set completeness percentage.
     *
     * FIXME manage per state handler?
     *
     * @param   completeness    Completeness of loading into memory.
     */
    public void setLoadingCompleteness(final int completeness) {
        this.loadingCompleteness = completeness;
    }

    public int getLoadingCompleteness() {
        return this.loadingCompleteness;
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
        return this.loadingState;
    }

    public boolean isLoaded() {
        return loadingState == LoadingState.STATE_LOADED;
    }

    /**
     * Set loading state to "loaded". Also puts <code>null</code> to {@link #getLabels()}.
     *
     * @param   qedeq   This module was loaded. Must not be <code>null</code>.
     * @throws  NullPointerException    One argument was <code>null</code>.
     */
    public void setLoaded(final QedeqVo qedeq) {
        stateManager.setLoaded(qedeq);
    }

    /**
     * FIXME only called by StataManager!
     *
     * @param qedeq
     */
    public void setXXXQedeqVo(final QedeqVo qedeq) {
        this.qedeq = qedeq;
    }

    /**
     * FIXME only called by StataManager!
     *
     * @param state
     */
    public void setXXXLoadingState(final LoadingState state) {
        this.loadingState = state;
    }

    /**
     * FIXME only called by StataManager!
     *
     * @param state
     */
    public void setXXXDependencyState(final DependencyState state) {
        this.dependencyState = state;
    }

    /**
     * FIXME only called by StataManager!
     *
     * @param state
     */
    public void setXXXLogicalState(final LogicalState state) {
        this.logicalState = state;
    }

    /**
     * FIXME only called by StataManager!
     *
     * @param e
     */
    public void setXXXException(final SourceFileExceptionList e) {
        this.exception = e;
    }

    /**
     * FIXME only called by StataManager!
     *
     * @return StateManager
     */
    public StateManager getXXXStateManager() {
        return this.stateManager;
    }

    /**
     * FIXME only called by StataManager!
     *
     * @param   checker
     */
    public void setXXXExistenceChecker(final ExistenceChecker checker) {
        this.checker = checker;
    }

    public String getEncoding() {
        return this.encoding;
    }

    /**
     * Set character encoding for this module. Can be <code>null</code>.
     *
     * @param   encoding    Encoding.
     */
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    public Qedeq getQedeq() {
        if (loadingState != LoadingState.STATE_LOADED) {
            throw new IllegalStateException(
                "module exists only if state is \"" + LoadingState.STATE_LOADED.getText()
                +   "\"");
        }
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
        return this.dependencyState;
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
        return isLoaded() && dependencyState == DependencyState.STATE_LOADED_REQUIRED_MODULES;
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

    /**
     * Get the predicate and function existence checker. Is only not <code>null</code>
     * if logic was successfully checked.
     *
     * @return  Checker. Checks if a predicate or function constant is defined.
     */
    public ExistenceChecker getExistenceChecker() {
        return checker;
    }

    public boolean isChecked() {
        return isLoaded() && hasLoadedRequiredModules()
            && logicalState == LogicalState.STATE_CHECKED;
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
        return this.logicalState;
    }

    public SourceFileExceptionList getException() {
        return this.exception;
    }

    public String getStateDescription() {
        if (loadingState == LoadingState.STATE_LOADING_FROM_WEB) {
            return loadingState.getText() + " (" + loadingCompleteness + "%)";
        } else if (!isLoaded()) {
            return loadingState.getText();
        } else if (!hasLoadedRequiredModules()) {
            if (dependencyState == DependencyState.STATE_UNDEFINED) {
                return loadingState.getText();
            }
            return dependencyState.getText();
        } else if (!isChecked()) {
            if (logicalState == LogicalState.STATE_UNCHECKED) {
                return dependencyState.getText();
            }
            return logicalState.getText();
        }
        return logicalState.getText();
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

    public URL getUrl() {
        if (this.address == null) {
            return null;
        }
        return this.address.getURL();
    }

    /**
     * Set label references for QEDEQ module.
     *
     * @param   labels  Label references.
     */
    public void setLabels(final ModuleNodes labels) {
        this.labels = labels;
    }

    /**
     * Get label references for QEDEQ module.
     *
     * @return  Label references.
     */
    public ModuleNodes getLabels() {
        return labels;
    }

    /**
     * Create exception out of {@link ModuleDataException}.
     *
     * @param   exception   Take this exception.
     * @return  Newly created instance.
     */
    public SourceFileExceptionList createSourceFileExceptionList(
            final ModuleDataException exception) {
        final SourceFileException e = new SourceFileException(exception, createSourceArea(qedeq,
            exception.getContext()), loader.createSourceArea(qedeq,
                exception.getReferenceContext()));
        final DefaultSourceFileExceptionList list = new DefaultSourceFileExceptionList(e);
        return list;
    }

    /**
     * Create exception out of {@link ModuleDataException}.
     *
     * @param   exception   Take this exception.
     * @param   qedeq       Take this QEDEQ source. (This might not be accessible via
     *                      {@link #getQedeq()}.
     * @return  Newly created instance.
     */
    public SourceFileExceptionList createSourceFileExceptionList(
            final ModuleDataException exception, final Qedeq qedeq) {
        final SourceFileException e = new SourceFileException(exception, createSourceArea(qedeq,
            exception.getContext()), loader.createSourceArea(qedeq,
                exception.getReferenceContext()));
        final DefaultSourceFileExceptionList list = new DefaultSourceFileExceptionList(e);
        return list;
    }

    /**
     * Create exception out of {@link ModuleDataException}.
     *
     * @param   exception   Take this exception.
     * @return  Newly created instance.
     */
    public SourceFileException createSourceFileException(final ModuleDataException
            exception) {
        final SourceFileException e = new SourceFileException(exception, createSourceArea(qedeq,
            exception.getContext()), loader.createSourceArea(qedeq,
                exception.getReferenceContext()));
        return e;
    }

    /**
     * Get area in XML source file for QEDEQ module context.
     *
     * @param   qedeq       Look at this QEDEQ module.
     * @param   context     Search for this context.
     * @return  Created file area. Maybe <code>null</code>.
     */
    public SourceArea createSourceArea(final Qedeq qedeq, final ModuleContext context) {
        return loader.createSourceArea(qedeq, context);
    }

    public int hashCode() {
        return (getModuleAddress() == null ? 0 : getModuleAddress().hashCode());
    }

    public boolean equals(final Object obj) {
        if (obj instanceof KernelQedeqBo) {
            return EqualsUtility.equals(((KernelQedeqBo) obj).getModuleAddress(),
                this.getModuleAddress());
        }
        return false;
    }

    public String toString() {
       return address.getURL().toString();
    }

}
