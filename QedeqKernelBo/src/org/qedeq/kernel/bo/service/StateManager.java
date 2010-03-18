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

import java.util.ArrayList;
import java.util.List;

import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.bo.log.ModuleEventLog;
import org.qedeq.kernel.bo.logic.wf.ExistenceChecker;
import org.qedeq.kernel.bo.module.KernelModuleReferenceList;
import org.qedeq.kernel.bo.module.ModuleLabels;
import org.qedeq.kernel.bo.module.PluginBo;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.DependencyState;
import org.qedeq.kernel.common.LoadingState;
import org.qedeq.kernel.common.LogicalState;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.dto.module.QedeqVo;

import com.sun.corba.se.impl.legacy.connection.DefaultSocketFactory;


/**
 * Changes the states of {@link org.qedeq.kernel.bo.service.DefaultKernelQedeqBo}s.
 * All state changing is done here.
 *
 * @author  Michael Meyling
 */
public class StateManager {

    /** This class. */
    private static final Class CLASS = StateManager.class;

    /** Main BO to care about. */
    private final DefaultKernelQedeqBo bo;

    /** Completeness during loading from web. */
    private int loadingCompleteness;

    /** Describes QEDEQ module loading state. */
    private LoadingState loadingState;

    /** Describes QEDEQ module dependency state. */
    private DependencyState dependencyState;

    /** Describes QEDEQ module logical state. */
    private LogicalState logicalState;

    /** Holds QEDEQ module plugin results. */
    private PluginResultManager pluginResults;

    /** Failure exceptions for basic operations. */
    private SourceFileExceptionList errors;


    StateManager(final DefaultKernelQedeqBo bo) {
        this.bo = bo;
        loadingState = LoadingState.STATE_UNDEFINED;
        loadingCompleteness = 0;
        dependencyState = DependencyState.STATE_UNDEFINED;
        logicalState = LogicalState.STATE_UNCHECKED;
        pluginResults = new PluginResultManager();
    }

    /**
     * Delete QEDEQ module. Invalidates all dependent modules.
     */
    public void delete() {
        checkIfDeleted();
        invalidateOtherDependentModulesToLoaded();
        setLoadingState(LoadingState.STATE_DELETED);
        bo.setQedeqVo(null);
        bo.getKernelRequiredModules().clear();
        bo.getDependentModules().clear();
        bo.setLabels(null);
        setDependencyState(DependencyState.STATE_UNDEFINED);
        setLogicalState(LogicalState.STATE_UNCHECKED);
        setErrors(null);
        ModuleEventLog.getInstance().removeModule(bo);
    }

    public boolean hasFailures() {
        return loadingState.isFailure() || dependencyState.isFailure() || logicalState.isFailure();
    }

    /**
     * Set completeness percentage.
     *
     * @param   completeness    Completeness of loading into memory.
     */
    public void setLoadingCompleteness(final int completeness) {
        bo.setLoadingCompleteness(completeness);
    }

    public int getLoadingCompleteness() {
        return this.loadingCompleteness;
    }

    public LoadingState getLoadingState() {
        return this.loadingState;
    }

    public boolean isLoaded() {
        return loadingState == LoadingState.STATE_LOADED;
    }

    /**
     * Set loading progress module state.
     *
     * @param   state   Module loading state. Must not be <code>null</code>.
     * @throws  IllegalStateException   State is a failure state or module loaded state.
     */
    public void setLoadingProgressState(final LoadingState state) {
        checkIfDeleted();
        if (state == LoadingState.STATE_LOADED) {
            throw new IllegalArgumentException(
                "this state could only be set by calling method setLoaded");
        }
        if (state != LoadingState.STATE_DELETED  && state.isFailure()) {
            throw new IllegalArgumentException(
                "this is a failure state, call setLoadingFailureState for " + state);
        }
        // if module has no loading state we give him one before creating an event
        if (getLoadingState() == LoadingState.STATE_UNDEFINED) {
            setLoadingState(state);
            ModuleEventLog.getInstance().addModule(bo);
        }
        if (state == LoadingState.STATE_LOADING_FROM_BUFFER) {
            invalidateOtherDependentModulesToLoaded();
        }
        if (state == LoadingState.STATE_DELETED) {
            throw new IllegalArgumentException(
                "call delete for " + state);
        }
        setLoadingState(state);
        bo.setQedeqVo(null);
        bo.getKernelRequiredModules().clear();
        bo.getDependentModules().clear();
        bo.setLabels(null);
        setDependencyState(DependencyState.STATE_UNDEFINED);
        setLogicalState(LogicalState.STATE_UNCHECKED);
        setErrors(null);
        ModuleEventLog.getInstance().stateChanged(bo);
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
        checkIfDeleted();
        if (!state.isFailure()) {
            throw new IllegalArgumentException(
                "this is no failure state, call setLoadingProgressState");
        }
        // if module has no loading state we give him one before creating an event
        if (getLoadingState() == LoadingState.STATE_UNDEFINED) {
            setLoadingState(state);
            ModuleEventLog.getInstance().addModule(bo);
        }
        invalidateOtherDependentModulesToLoaded();
        bo.setQedeqVo(null);
        bo.getKernelRequiredModules().clear();
        bo.getDependentModules().clear();
        bo.setLabels(null);
        setLoadingState(state);
        setDependencyState(DependencyState.STATE_UNDEFINED);
        setLogicalState(LogicalState.STATE_UNCHECKED);
        setErrors(e);
        if (e == null) {
            throw new NullPointerException("Exception must not be null");
        }
        ModuleEventLog.getInstance().stateChanged(bo);
    }

    /**
     * Set loading state to "loaded". Also puts <code>null</code> to
     * {@link DefaultKernelQedeqBo#getLabels()}.
     *
     * @param   qedeq   This module was loaded. Must not be <code>null</code>.
     * @param   labels  Module labels.
     * @throws  NullPointerException    One argument was <code>null</code>.
     */
    public void setLoaded(final QedeqVo qedeq, final ModuleLabels labels) {
        checkIfDeleted();
        if (qedeq == null) {
            throw new NullPointerException("Qedeq is null");
        }
        invalidateOtherDependentModulesToLoaded();
        setLoadingState(LoadingState.STATE_LOADED);
        bo.setQedeqVo(qedeq);
        bo.getKernelRequiredModules().clear();
        bo.getDependentModules().clear();
        bo.setLabels(labels);
        setDependencyState(DependencyState.STATE_UNDEFINED);
        setLogicalState(LogicalState.STATE_UNCHECKED);
        setErrors(null);
        ModuleEventLog.getInstance().stateChanged(bo);
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
        checkIfDeleted();
        if (!isLoaded() && state != DependencyState.STATE_UNDEFINED) {
            throw new IllegalStateException("module is not yet loaded");
        }
        if (state.isFailure()) {
            throw new IllegalArgumentException(
                "this is a failure state, call setDependencyFailureState");
        }
        if (state == DependencyState.STATE_LOADED_REQUIRED_MODULES) {
            throw new IllegalArgumentException(
                "this state could only be set by calling method setLoadedRequiredModules");
        }
        if (state == DependencyState.STATE_LOADING_REQUIRED_MODULES) {
            invalidateOtherDependentModulesToLoaded();
        }
        setLogicalState(LogicalState.STATE_UNCHECKED);
        setDependencyState(state);
        bo.getKernelRequiredModules().clear();
        setErrors(null);
        ModuleEventLog.getInstance().stateChanged(bo);
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
        checkIfDeleted();
        if (!isLoaded()) {
            throw new IllegalStateException("module is not yet loaded");
        }
        if (!state.isFailure()) {
            throw new IllegalArgumentException(
                "this is no failure state, call setLoadingProgressState");
        }
        invalidateOtherDependentModulesToLoadedRequired();
        setDependencyState(state);
        setErrors(e);
        if (e == null) {
            throw new NullPointerException("Exception must not be null");
        }
        ModuleEventLog.getInstance().stateChanged(bo);
    }

    public DependencyState getDependencyState() {
        return this.dependencyState;
    }

    /**
     * Reset all (recursive) dependent modules (if any) to state loaded.
     */
    private void invalidateOtherDependentModulesToLoaded() {
        final String method = "invalidateOtherDependModulesToLoaded";
        Trace.begin(CLASS, this, method);
        Trace.param(CLASS, this, method, "bo", bo);
        if (hasLoadedRequiredModules()) {
            final KernelModuleReferenceList dependent = bo.getDependentModules();
            Trace.trace(CLASS, this, method, "begin list of dependent modules");
            // remember dependent modules
            final List list = new ArrayList();
            for (int i = 0; i < dependent.size(); i++) {
                Trace.param(CLASS, this, method, "" + i, dependent.getKernelQedeqBo(i));
                list.add(dependent.getKernelQedeqBo(i));
            }
            Trace.trace(CLASS, this, method, "end list of dependent modules");
            for (int i = 0; i < list.size(); i++) {
                DefaultKernelQedeqBo ref = (DefaultKernelQedeqBo) list.get(i);
                // work on it, if still in list of dependent modules
                if (dependent.contains(ref)) {
                    ref.getStateManager().invalidateDependentModulesToLoaded();
                }
            }
            list.clear();
            dependent.clear();

            final KernelModuleReferenceList required = bo.getKernelRequiredModules();
            for (int i = 0; i < required.size(); i++) {
                DefaultKernelQedeqBo ref = (DefaultKernelQedeqBo) required.getKernelQedeqBo(i);
                Trace.param(CLASS, this, method, "remove dependence from", ref);
                ref.getDependentModules().remove(bo);
            }
            required.clear();
        }
        Trace.end(CLASS, this, method);
    }

    /**
     * Reset this and all (recursive) dependent modules (if any) to state loaded.
     */
    private void invalidateDependentModulesToLoaded() {
        final String method = "invalidateDependentModulesToLoaded";
        Trace.begin(CLASS, this, method);
        Trace.param(CLASS, this, method, "bo", bo);
        if (hasLoadedRequiredModules()) {
            final KernelModuleReferenceList dependent = bo.getDependentModules();
            Trace.trace(CLASS, this, method, "begin list of dependent modules");
            // remember dependent modules
            final List list = new ArrayList();
            for (int i = 0; i < dependent.size(); i++) {
                Trace.param(CLASS, this, method, "" + i, dependent.getKernelQedeqBo(i));
                list.add(dependent.getKernelQedeqBo(i));
            }
            Trace.trace(CLASS, this, method, "end list of dependent modules");
            for (int i = 0; i < list.size(); i++) {
                DefaultKernelQedeqBo ref = (DefaultKernelQedeqBo) list.get(i);
                // work on it, if still in list of dependent modules
                if (dependent.contains(ref)) {
                    ref.getStateManager().invalidateDependentModulesToLoaded();
                }
            }
            list.clear();
            dependent.clear();

            final KernelModuleReferenceList required = bo.getKernelRequiredModules();
            for (int i = 0; i < required.size(); i++) {
                DefaultKernelQedeqBo ref = (DefaultKernelQedeqBo) required.getKernelQedeqBo(i);
                Trace.param(CLASS, this, method, "remove dependence from", ref);
                ref.getDependentModules().remove(bo);
            }
            required.clear();

            invalidateThisModule();
            setLoadingState(LoadingState.STATE_LOADED);
            ModuleEventLog.getInstance().stateChanged(bo);
        }
        Trace.end(CLASS, this, method);
    }

    /**
     * Reset all (recursive) dependent modules (if any) to state loaded required.
     */
    private void invalidateOtherDependentModulesToLoadedRequired() {
        if (isChecked()) {
            final KernelModuleReferenceList dependent = bo.getDependentModules();
            for (int i = 0; i < dependent.size(); i++) {
                DefaultKernelQedeqBo ref = (DefaultKernelQedeqBo) dependent.getKernelQedeqBo(i);
                ref.getStateManager().invalidateDependentModulesToLoadedRequired();
            }
        }
    }

    /**
     * Reset this and all (recursive) dependent modules (if any) to state loaded.
     */
    private void invalidateDependentModulesToLoadedRequired() {
        if (isChecked()) {
            final KernelModuleReferenceList dependent = bo.getDependentModules();
            for (int i = 0; i < dependent.size(); i++) {
                DefaultKernelQedeqBo ref = (DefaultKernelQedeqBo) dependent.getKernelQedeqBo(i);
                ref.getStateManager().invalidateDependentModulesToLoadedRequired();
            }
            invalidateThisModule();
            setDependencyState(DependencyState.STATE_LOADED_REQUIRED_MODULES);
            ModuleEventLog.getInstance().stateChanged(bo);
        }
    }

    private void invalidateThisModule() {
        setLoadingState(LoadingState.STATE_LOADED);
        setDependencyState(DependencyState.STATE_UNDEFINED);
        setLogicalState(LogicalState.STATE_UNCHECKED);
        setErrors(null);
    }

    public boolean hasLoadedRequiredModules() {
        return isLoaded() && dependencyState == DependencyState.STATE_LOADED_REQUIRED_MODULES;
    }

    /**
     * Set loaded required requirements state.
     *
     * @param   required  URLs of all referenced modules. Must not be <code>null</code>.
     * @throws  IllegalStateException   Module is not yet loaded.
     */
    public void setLoadedRequiredModules(final KernelModuleReferenceList required) {
        checkIfDeleted();
        if (!isLoaded()) {
            throw new IllegalStateException(
                "Required modules can only be set if module is loaded."
                + "\"\nCurrently the status for the module"
                + "\"" + bo.getName() + "\" is \"" + bo.getLoadingState() + "\"");
        }
        invalidateDependentModulesToLoadedRequired();

        for (int i = 0; i < required.size(); i++) {
            DefaultKernelQedeqBo current = (DefaultKernelQedeqBo) required.getKernelQedeqBo(i);
            try {
                current.getDependentModules().add(required.getModuleContext(i),
                    required.getLabel(i), bo);
            } catch (ModuleDataException me) {  // should never happen
                throw new RuntimeException(me);
            }
        }


        setDependencyState(DependencyState.STATE_LOADED_REQUIRED_MODULES);
        bo.getKernelRequiredModules().set(required);
        ModuleEventLog.getInstance().stateChanged(bo);
    }

    /**
     * Set logic checked state. Also set the predicate and function existence checker.
     *
     * @param   checker Checks if a predicate or function constant is defined.
     */
    public void setChecked(final ExistenceChecker checker) {
        checkIfDeleted();
        if (!hasLoadedRequiredModules()) {
            throw new IllegalStateException(
                "Checked can only be set if all required modules are loaded."
                + "\"\nCurrently the status for the module"
                + "\"" + bo.getName() + "\" is \"" + bo.getLoadingState() + "\"");
        }
        setLogicalState(LogicalState.STATE_CHECKED);
        bo.setExistenceChecker(checker);
        ModuleEventLog.getInstance().stateChanged(bo);
    }

   /**
    * Set loading progress module state. Must not be <code>null</code>.
    *
    * @param   state   module state
    */
    public void setLogicalProgressState(final LogicalState state) {
        if (getDependencyState().getCode()
                < DependencyState.STATE_LOADED_REQUIRED_MODULES.getCode()
                && state != LogicalState.STATE_UNCHECKED) {
            throw new IllegalArgumentException(
                "this state could only be set if all required modules are loaded ");
        }
        if (state.isFailure()) {
            throw new IllegalArgumentException(
                "this is a failure state, call setLogicalFailureState");
        }
        if (state == LogicalState.STATE_CHECKED) {
            throw new IllegalArgumentException(
                "set with setChecked(ExistenceChecker)");
        }
        invalidateOtherDependentModulesToLoadedRequired();
        setErrors(null);
        setLogicalState(state);
        ModuleEventLog.getInstance().stateChanged(bo);
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
        if ((!isLoaded() || !hasLoadedRequiredModules())
                && state != LogicalState.STATE_UNCHECKED) {
            throw new IllegalArgumentException(
                "this state could only be set if all required modules are loaded ");
        }
        if (!state.isFailure()) {
            throw new IllegalArgumentException(
                "this is no failure state, call setLogicalProgressState");
        }
        invalidateDependentModulesToLoadedRequired();
        setLogicalState(state);
        setErrors(e);
        ModuleEventLog.getInstance().stateChanged(bo);
    }

    /**
     * Checks if the current instance is already deleted.
     *
     * @throws  IllegalStateException   Module is already deleted.
     */
    private void checkIfDeleted() {
        if (getLoadingState() == LoadingState.STATE_DELETED) {
            throw new IllegalStateException("module is already deleted: " + bo.getUrl());
        }
    }

    public boolean isChecked() {
        return isLoaded() && hasLoadedRequiredModules()
            && logicalState == LogicalState.STATE_CHECKED;
    }

    public LogicalState getLogicalState() {
        return this.logicalState;
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

    /**
     * Set {@link LoadingState}. Doesn't do any status handling. Only for internal use.
     *
     * @param   state   Set this loading state.
     */
    protected void setLoadingState(final LoadingState state) {
        this.loadingState = state;
    }

    /**
     * Set {@link DependencyState}. Doesn't do any status handling. Only for internal use.
     *
     * @param   state   Set this dependency state.
     */
    protected void setDependencyState(final DependencyState state) {
        this.dependencyState = state;
    }

    /**
     * Set {@link LogicalState}. Doesn't do any status handling. Only for internal use.
     *
     * @param   state   Set this logical state.
     */
    protected void setLogicalState(final LogicalState state) {
        this.logicalState = state;
    }

    public SourceFileExceptionList getErrors() {
        final DefaultSourceFileExceptionList result = new DefaultSourceFileExceptionList(errors);
        result.add(pluginResults.getAllErrors());
        // FIXME m31 20100317: remove warnings
        result.add(pluginResults.getAllWarnings());
        return result;
    }

    public SourceFileExceptionList getWarnings() {
        final DefaultSourceFileExceptionList result = new DefaultSourceFileExceptionList();
        result.add(pluginResults.getAllWarnings());
        return result;
    }

    /**
     * Set {@link SourceFileExceptionList}. Doesn't do any status handling. Only for internal use.
     *
     * @param   exception   Set this exception.
     */
    protected void setErrors(final SourceFileExceptionList exception) {
        this.errors = exception;
    }

    public void addPluginResults(final PluginBo plugin, final SourceFileExceptionList errors, final SourceFileExceptionList warnings) {
        pluginResults.addResult(plugin, errors, warnings);
    }

    /**
     * Print the dependence tree to <code>System.out</code>.
     */
    public void printDependencyTree() {
        printDependencyTree(0);
        System.out.println();
    }

    private void printDependencyTree(final int tab) {
        System.out.println(StringUtility.getSpaces(tab) + bo.getName());
        final int newTab = tab + bo.getName().length();
        final KernelModuleReferenceList dependent = bo.getDependentModules();
        for (int i = 0; i < dependent.size(); i++) {
            DefaultKernelQedeqBo ref = (DefaultKernelQedeqBo) dependent.getKernelQedeqBo(i);
            ref.getStateManager().printDependencyTree(newTab);
        }
    }


}
