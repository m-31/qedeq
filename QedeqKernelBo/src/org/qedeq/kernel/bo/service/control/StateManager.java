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

package org.qedeq.kernel.bo.service.control;

import java.util.ArrayList;
import java.util.List;

import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.bo.common.ModuleService;
import org.qedeq.kernel.bo.log.ModuleEventLog;
import org.qedeq.kernel.bo.module.InternalModuleServicePlugin;
import org.qedeq.kernel.bo.module.KernelModuleReferenceList;
import org.qedeq.kernel.bo.module.ModuleConstantsExistenceChecker;
import org.qedeq.kernel.bo.module.ModuleLabels;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.dto.module.QedeqVo;
import org.qedeq.kernel.se.state.AbstractState;
import org.qedeq.kernel.se.state.DependencyState;
import org.qedeq.kernel.se.state.FormallyProvedState;
import org.qedeq.kernel.se.state.LoadingImportsState;
import org.qedeq.kernel.se.state.LoadingState;
import org.qedeq.kernel.se.state.WellFormedState;


/**
 * Changes the states of {@link org.qedeq.kernel.bo.service.control.DefaultKernelQedeqBo}s.
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

    /** Describes QEDEQ module loading imports state. */
    private LoadingImportsState loadingImportsState;

    /** Describes QEDEQ module dependency state. */
    private DependencyState dependencyState;

    /** Describes QEDEQ module well formed state. */
    private WellFormedState wellFormedState;

    /** Describes QEDEQ module formally proved state. */
    private FormallyProvedState formallyProvedState;

    /** Holds QEDEQ module plugin results. */
    private PluginResultManager pluginResults;

    /** Failure exceptions for basic operations. */
    private SourceFileExceptionList errors;

    StateManager(final DefaultKernelQedeqBo bo) {
        this.bo = bo;
        loadingState = LoadingState.STATE_UNDEFINED;
        loadingCompleteness = 0;
        loadingImportsState = LoadingImportsState.STATE_UNDEFINED;
        dependencyState = DependencyState.STATE_UNDEFINED;
        wellFormedState = WellFormedState.STATE_UNCHECKED;
        formallyProvedState = FormallyProvedState.STATE_UNCHECKED;
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
        setWellFormedState(WellFormedState.STATE_UNCHECKED);
        setFormallyProvedState(FormallyProvedState.STATE_UNCHECKED);
        setErrors(null);
        ModuleEventLog.getInstance().removeModule(bo);
    }

    /**
     * Is the module in a failure state? That is the case if loading of module or imported modules
     * failed or the logical check failed. Possible plugin failures don't matter.
     *
     * @return  Failure during loading or logical check occurred.
     */
    public boolean hasBasicFailures() {
        return loadingState.isFailure() || dependencyState.isFailure()
            || wellFormedState.isFailure() || formallyProvedState.isFailure();
    }

    /**
     * Has the module any errors? This includes loading, importing, logical and plugin errors.
     *
     * @return  Errors occurred.
     */
    public boolean hasErrors() {
        return hasBasicFailures() || (getErrors() != null && getErrors().size() > 0);
    }

    /**
     * Has the module any warnings? This includes loading, importing, logical and plugin warnings.
     *
     * @return  Warnings occurred.
     */
    public boolean hasWarnings() {
        return (getWarnings() != null && getWarnings().size() > 0);
    }

    /**
     * Set completeness percentage.
     *
     * @param   completeness    Completeness of loading into memory.
     */
    public void setLoadingCompleteness(final int completeness) {
        this.loadingCompleteness = completeness;
    }

    /**
     * Get loading completeness percentage.
     *
     * @return  Completeness as percent number.
     */
    public int getLoadingCompleteness() {
        return this.loadingCompleteness;
    }

    /**
     * Get loading state.
     *
     * @return  Loading state.
     */
    public LoadingState getLoadingState() {
        return this.loadingState;
    }

    /**
     * Is the module loaded?
     *
     * @return  Is the module loaded?
     */
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
        if (state == LoadingState.STATE_DELETED) {
            throw new IllegalArgumentException(
                "call delete for " + state);
        }
        setLoadingState(state);
        bo.setQedeqVo(null);
        bo.getKernelRequiredModules().clear();
        bo.getDependentModules().clear();
        bo.setLabels(null);
        setLoadingImportsState(LoadingImportsState.STATE_UNDEFINED);
        setDependencyState(DependencyState.STATE_UNDEFINED);
        setWellFormedState(WellFormedState.STATE_UNCHECKED);
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
        if (e == null) {
            throw new NullPointerException("Exception must not be null");
        }
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
        bo.setQedeqVo(null);
        bo.getKernelRequiredModules().clear();
        bo.getDependentModules().clear();
        bo.setLabels(null);
        setLoadingState(state);
        setLoadingImportsState(LoadingImportsState.STATE_UNDEFINED);
        setDependencyState(DependencyState.STATE_UNDEFINED);
        setWellFormedState(WellFormedState.STATE_UNCHECKED);
        setFormallyProvedState(FormallyProvedState.STATE_UNCHECKED);
        setErrors(e);
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
        setLoadingState(LoadingState.STATE_LOADED);
        setLoadingImportsState(LoadingImportsState.STATE_UNDEFINED);
        setDependencyState(DependencyState.STATE_UNDEFINED);
        setWellFormedState(WellFormedState.STATE_UNCHECKED);
        setFormallyProvedState(FormallyProvedState.STATE_UNCHECKED);
        setErrors(null);
        bo.setQedeqVo(qedeq);
        bo.getKernelRequiredModules().clear();
        bo.getDependentModules().clear();
        bo.setLabels(labels);
        ModuleEventLog.getInstance().stateChanged(bo);
    }

    /**
     * Set loading imports progress module state.
     *
     * @param   state   Module state. Must not be <code>null</code>.
     * @throws  IllegalStateException       Module is not yet loaded.
     * @throws  IllegalArgumentException    <code>state</code> is failure state or loaded required
     *                                      state.
     * @throws  NullPointerException        <code>state</code> is <code>null</code>.
     */
    public void setLoadingImportsProgressState(final LoadingImportsState state) {
        checkIfDeleted();
        if (!isLoaded() && state != LoadingImportsState.STATE_UNDEFINED) {
            throw new IllegalStateException("module is not yet loaded");
        }
        if (state.isFailure()) {
            throw new IllegalArgumentException(
                "this is a failure state, call setLoadingImportsFailureState");
        }
        if (state == LoadingImportsState.STATE_LOADED_IMPORTED_MODULES) {
            throw new IllegalArgumentException(
                "this state could only be set by calling method setLoadedImports");
        }
        ModuleEventLog.getInstance().stateChanged(bo);
    }

   /**
    * Set failure module state.
    *
    * @param   state   Module loading imports state. Must not be <code>null</code>.
    * @param   e       Exception that occurred during loading. Must not be <code>null</code>.
    * @throws  IllegalStateException       Module is not yet loaded.
    * @throws  IllegalArgumentException    <code>state</code> is no failure state.
    * @throws  NullPointerException        <code>state</code> is <code>null</code>.
    */
    public void setLoadingImportsFailureState(final LoadingImportsState state,
            final SourceFileExceptionList e) {
        if (e == null) {
            throw new NullPointerException("Exception must not be null");
        }
        checkIfDeleted();
        if (!isLoaded()) {
            throw new IllegalStateException("module is not yet loaded");
        }
        if (!state.isFailure()) {
            throw new IllegalArgumentException(
                "this is no failure state, call setLoadingProgressState");
        }
        setLoadingImportsState(state);
        setErrors(e);
        ModuleEventLog.getInstance().stateChanged(bo);
    }

    /**
     * Get loading imports state.
     *
     * @return  Dependency state.
     */
    public LoadingImportsState getLoadingImportsState() {
        return this.loadingImportsState;
    }

    /**
     * Is the module loaded?
     *
     * @return  Is the module loaded?
     */
    public boolean hasLoadedImports() {
        return loadingImportsState == LoadingImportsState.STATE_LOADED_IMPORTED_MODULES;
    }

    /**
     * Set dependency progress module state.
     *
     * @param   state       Module state. Must not be <code>null</code>.
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
        setWellFormedState(WellFormedState.STATE_UNCHECKED);
        setFormallyProvedState(FormallyProvedState.STATE_UNCHECKED);
        setDependencyState(state);
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
        if (e == null) {
            throw new NullPointerException("Exception must not be null");
        }
        checkIfDeleted();
        if (!isLoaded()) {
            throw new IllegalStateException("module is not yet loaded");
        }
        if (!state.isFailure()) {
            throw new IllegalArgumentException(
                "this is no failure state, call setLoadingProgressState");
        }
        setDependencyState(state);
        setErrors(e);
        ModuleEventLog.getInstance().stateChanged(bo);
    }

    /**
     * Get dependency state.
     *
     * @return  Dependency state.
     */
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
            bo.getLabels().resetNodesToWellFormedUnchecked();
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
            invalidateOtherDependentModulesToLoaded();
            invalidateThisModuleToLoaded();
            setLoadingState(LoadingState.STATE_LOADED);
            ModuleEventLog.getInstance().stateChanged(bo);
        }
        Trace.end(CLASS, this, method);
    }

//    /**
//     * Reset this and all (recursive) dependent modules (if any) to state loaded.
//     */
//    private void invalidateDependentModulesToLoadedImports() {
//        final String method = "invalidateDependentModulesToLoadedImports";
//        Trace.begin(CLASS, this, method);
//        Trace.param(CLASS, this, method, "bo", bo);
//        if (hasLoadedImports()) {
//            final KernelModuleReferenceList dependent = bo.getDependentModules();
//            Trace.trace(CLASS, this, method, "begin list of dependent modules");
//            // remember dependent modules
//            final List list = new ArrayList();
//            for (int i = 0; i < dependent.size(); i++) {
//                Trace.param(CLASS, this, method, "" + i, dependent.getKernelQedeqBo(i));
//                list.add(dependent.getKernelQedeqBo(i));
//            }
//            Trace.trace(CLASS, this, method, "end list of dependent modules");
//            for (int i = 0; i < list.size(); i++) {
//                DefaultKernelQedeqBo ref = (DefaultKernelQedeqBo) list.get(i);
//                // work on it, if still in list of dependent modules
//                if (dependent.contains(ref)) {
//                    ref.getStateManager().invalidateDependentModulesToLoadedImports();
//                }
//            }
//            list.clear();
//
//            invalidateThisModuleToLoadedImports();
//            ModuleEventLog.getInstance().stateChanged(bo);
//        }
//        Trace.end(CLASS, this, method);
//    }
//
//    /**
//     * Reset all (recursive) dependent modules (if any) to state loaded required.
//     */
//    private void invalidateOtherDependentModulesToLoadedRequired() {
//        if (isWellFormed()) {
//            final KernelModuleReferenceList dependent = bo.getDependentModules();
//            for (int i = 0; i < dependent.size(); i++) {
//                DefaultKernelQedeqBo ref = (DefaultKernelQedeqBo) dependent.getKernelQedeqBo(i);
//                ref.getStateManager().invalidateDependentModulesToLoadedRequired();
//            }
//        }
//    }
//
//    /**
//     * Reset this and all (recursive) dependent modules (if any) to state loaded required.
//     */
//    private void invalidateDependentModulesToLoadedRequired() {
//        if (isWellFormed()) {
//            final KernelModuleReferenceList dependent = bo.getDependentModules();
//            for (int i = 0; i < dependent.size(); i++) {
//                DefaultKernelQedeqBo ref = (DefaultKernelQedeqBo) dependent.getKernelQedeqBo(i);
//                ref.getStateManager().invalidateDependentModulesToLoadedRequired();
//            }
//            invalidateThisModuleToLoadedReqired();
//            ModuleEventLog.getInstance().stateChanged(bo);
//        }
//    }
//
//    /**
//     * Reset all (recursive) dependent modules (if any) to state well formed.
//     */
//    private void invalidateOtherDependentModulesToWellFormed() {
//        if (isFullyFormallyProved()) {
//            final KernelModuleReferenceList dependent = bo.getDependentModules();
//            for (int i = 0; i < dependent.size(); i++) {
//                DefaultKernelQedeqBo ref = (DefaultKernelQedeqBo) dependent.getKernelQedeqBo(i);
//                ref.getStateManager().invalidateDependentModulesToWellFormed();
//            }
//        }
//    }
//
//    /**
//     * Reset this and all (recursive) dependent modules (if any) to state well formed.
//     */
//    private void invalidateDependentModulesToWellFormed() {
//        if (isFullyFormallyProved()) {
//            final KernelModuleReferenceList dependent = bo.getDependentModules();
//            for (int i = 0; i < dependent.size(); i++) {
//                DefaultKernelQedeqBo ref = (DefaultKernelQedeqBo) dependent.getKernelQedeqBo(i);
//                ref.getStateManager().invalidateDependentModulesToWellFormed();
//            }
//            invalidateThisModuleToWellFormed();
//            ModuleEventLog.getInstance().stateChanged(bo);
//        }
//    }

    private void invalidateThisModuleToLoaded() {
        if (isLoaded()) {
            setLoadingState(LoadingState.STATE_LOADED);
            setLoadingImportsState(LoadingImportsState.STATE_UNDEFINED);
            setDependencyState(DependencyState.STATE_UNDEFINED);
            setWellFormedState(WellFormedState.STATE_UNCHECKED);
            setFormallyProvedState(FormallyProvedState.STATE_UNCHECKED);
            bo.getKernelRequiredModules().clear();
            setErrors(null);
        }
    }

//    private void invalidateThisModuleToLoadedImports() {
//        if (hasLoadedImports()) {
//            setLoadingImportsState(LoadingImportsState.STATE_LOADED_IMPORTED_MODULES);
//            setDependencyState(DependencyState.STATE_UNDEFINED);
//            setWellFormedState(WellFormedState.STATE_UNCHECKED);
//            setFormallyProvedState(FormallyProvedState.STATE_UNCHECKED);
//            setErrors(null);
//        }
//    }
//
//    private void invalidateThisModuleToLoadedReqired() {
//        if (hasLoadedRequiredModules()) {
//            setDependencyState(DependencyState.STATE_LOADED_REQUIRED_MODULES);
//            setWellFormedState(WellFormedState.STATE_UNCHECKED);
//            setFormallyProvedState(FormallyProvedState.STATE_UNCHECKED);
//            setErrors(null);
//        }
//    }
//
//    private void invalidateThisModuleToWellFormed() {
//        if (isWellFormed()) {
//            setWellFormedState(WellFormedState.STATE_CHECKED);
//            setFormallyProvedState(FormallyProvedState.STATE_UNCHECKED);
//            setErrors(null);
//        }
//    }
//
//    private void invalidateThisModuleToFormallyProved() {
//        if (isFullyFormallyProved()) {
//            setFormallyProvedState(FormallyProvedState.STATE_CHECKED);
//            setErrors(null);
//        }
//    }

    /**
     * Are all required modules loaded?
     *
     * @return  All required modules are loaded?
     */
    public boolean hasLoadedRequiredModules() {
        return isLoaded() && dependencyState == DependencyState.STATE_LOADED_REQUIRED_MODULES;
    }

    /**
     * Set loaded imports state.
     *
     * @param   required  URLs of all referenced modules. Must not be <code>null</code>.
     * @throws  IllegalStateException   Module is not yet loaded.
     */
    public void setLoadedImports(final KernelModuleReferenceList required) {
        checkIfDeleted();
        if (!isLoaded()) {
            throw new IllegalStateException(
                "Loaded imported modules can only be set if module is loaded."
                + "\"\nCurrently the status for the module"
                + "\"" + bo.getName() + "\" is \"" + bo.getLoadingState() + "\"");
        }
        setLoadingImportsState(LoadingImportsState.STATE_LOADED_IMPORTED_MODULES);
        setDependencyState(DependencyState.STATE_UNDEFINED);
        setWellFormedState(WellFormedState.STATE_UNCHECKED);
        setErrors(null);
        bo.getKernelRequiredModules().set(required);
        ModuleEventLog.getInstance().stateChanged(bo);
    }

    /**
     * Set loaded required requirements state.
     *
     * @throws  IllegalStateException   Module is not yet loaded.
     */
    public void setLoadedRequiredModules() {
        checkIfDeleted();
        if (!isLoaded()) {
            throw new IllegalStateException(
                "Required modules can only be set if module is loaded."
                + "\"\nCurrently the status for the module"
                + "\"" + bo.getName() + "\" is \"" + bo.getLoadingState() + "\"");
        }
        KernelModuleReferenceList required = bo.getKernelRequiredModules();
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
        setWellFormedState(WellFormedState.STATE_UNCHECKED);
        setErrors(null);
        ModuleEventLog.getInstance().stateChanged(bo);
    }

    /**
     * Set logic checked state. Also set the predicate and function existence checker.
     *
     * @param   checker Checks if a predicate or function constant is defined.
     */
    public void setWellFormed(final ModuleConstantsExistenceChecker checker) {
        checkIfDeleted();
        if (!hasLoadedRequiredModules()) {
            throw new IllegalStateException(
                "Checked can only be set if all required modules are loaded."
                + "\"\nCurrently the status for the module"
                + "\"" + bo.getName() + "\" is \"" + bo.getLoadingState() + "\"");
        }
        setWellFormedState(WellFormedState.STATE_CHECKED);
        bo.setExistenceChecker(checker);
        ModuleEventLog.getInstance().stateChanged(bo);
    }

    /**
     * Set checking for well formed progress module state. Must not be <code>null</code>.
     *
     * @param   state   module state
     */
    public void setWellFormedProgressState(final WellFormedState state) {
        if (getDependencyState().getCode()
                < DependencyState.STATE_LOADED_REQUIRED_MODULES.getCode()
                && state != WellFormedState.STATE_UNCHECKED) {
            throw new IllegalArgumentException(
                "this state could only be set if all required modules are loaded ");
        }
        if (state.isFailure()) {
            throw new IllegalArgumentException(
                "this is a failure state, call setWellFormedFailureState");
        }
        if (state == WellFormedState.STATE_CHECKED) {
            throw new IllegalArgumentException(
                "set with setChecked(ExistenceChecker)");
        }
        setWellFormedState(state);
        setErrors(null);
        ModuleEventLog.getInstance().stateChanged(bo);
    }

    /**
     * Set failure module state.
     *
     * @param   state   module state
     * @param   e       Exception that occurred during loading.
     * @throws  IllegalArgumentException    <code>state</code> is no failure state
     */
    public void setWellFormedFailureState(final WellFormedState state,
            final SourceFileExceptionList e) {
        if ((!isLoaded() || !hasLoadedRequiredModules())
                && state != WellFormedState.STATE_UNCHECKED) {
            throw new IllegalArgumentException(
                "this state could only be set if all required modules are loaded ");
        }
        if (!state.isFailure()) {
            throw new IllegalArgumentException(
                "this is no failure state, call setWellFormedProgressState");
        }
        setWellFormedState(state);
        setErrors(e);
        ModuleEventLog.getInstance().stateChanged(bo);
    }

    /**
     * Set checking for formally proved progress module state. Must not be <code>null</code>.
     *
     * @param   state   module state
     */
    public void setFormallyProvedProgressState(final FormallyProvedState state) {
        if (getDependencyState().getCode()
                < DependencyState.STATE_LOADED_REQUIRED_MODULES.getCode()
                && state != FormallyProvedState.STATE_UNCHECKED) {
            throw new IllegalArgumentException(
                "this state could only be set if all required modules are loaded ");
        }
        if (state.isFailure()) {
            throw new IllegalArgumentException(
                "this is a failure state, call setFormallyProvedFailureState");
        }
//        if (state == FormallyProvedState.STATE_CHECKED) {
//            // FIXME 20130220 m31: do we need something simular?
//            throw new IllegalArgumentException(
//                "set with setChecked(ExistenceChecker)");
//        }
        setFormallyProvedState(state);
        setErrors(null);
        ModuleEventLog.getInstance().stateChanged(bo);
    }

    /**
     * Set failure module state.
     *
     * @param   state   module state
     * @param   e       Exception that occurred during loading.
     * @throws  IllegalArgumentException    <code>state</code> is no failure state
     */
    public void setFormallyProvedFailureState(final FormallyProvedState state,
            final SourceFileExceptionList e) {
        if ((!isLoaded() || !hasLoadedRequiredModules())
                && state != FormallyProvedState.STATE_UNCHECKED) {
            throw new IllegalArgumentException(
                "this state could only be set if all required modules are well formed ");
        }
        if (!state.isFailure()) {
            throw new IllegalArgumentException(
                "this is no failure state, call setFormallyProvedProgressState");
        }
        setFormallyProvedState(state);
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

    /**
     * Was the module successfully checked for well formedness errors?
     *
     * @return  Successfully checked for being well formed?
     */
    public boolean isWellFormed() {
        return hasLoadedRequiredModules() && wellFormedState == WellFormedState.STATE_CHECKED;
    }

    /**
     * Get the well formed state.
     *
     * @return  Well formed state.
     */
    public WellFormedState getWellFormedState() {
        return this.wellFormedState;
    }

    /**
     * Was the module successfully checked having formally correct proofs?
     *
     * @return  Successfully checked for having formally correct proofs?
     */
    public boolean isFullyFormallyProved() {
        return isWellFormed() && formallyProvedState == FormallyProvedState.STATE_CHECKED;
    }

    /**
     * Get the formally proved state.
     *
     * @return  Formally proved state.
     */
    public FormallyProvedState getFormallyProvedState() {
        return this.formallyProvedState;
    }

    /**
     * Get a description of the current state the module is in.
     *
     * @return  Textual representation of module state.
     */
    public String getStateDescription() {
        String result = "";
        if (loadingState == LoadingState.STATE_LOADING_FROM_WEB) {
            result = loadingState.getText() + " (" + loadingCompleteness + "%)";
        } else if (!isLoaded()) {
            result = loadingState.getText();
        } else if (!hasLoadedImports()) {
            if (loadingImportsState == LoadingImportsState.STATE_UNDEFINED) {
                result = loadingState.getText();
            } else {
                result = loadingImportsState.getText();
            }
        } else if (!hasLoadedRequiredModules()) {
            if (dependencyState == DependencyState.STATE_UNDEFINED) {
                result = loadingImportsState.getText();
            } else {
                result = dependencyState.getText();
            }
        } else if (!isWellFormed()) {
            if (wellFormedState == WellFormedState.STATE_UNCHECKED) {
                result = dependencyState.getText();
            } else {
                result = wellFormedState.getText();
            }
        } else if (!isFullyFormallyProved()) {
            if (formallyProvedState == FormallyProvedState.STATE_UNCHECKED) {
                result = wellFormedState.getText();
            } else {
                result = formallyProvedState.getText();
            }
        } else {
            result =  formallyProvedState.getText();
        }
        final String pluginState = pluginResults.getPluginStateDescription();
        if (pluginState.length() > 0) {
            result += "; " + pluginState;
        }
        return result;
    }

    /**
     * Get the current state of associate module.
     *
     * @return  Current module state.
     */
    public AbstractState getCurrentState() {
        if (!isLoaded()) {
            return loadingState;
        } else if (!hasLoadedImports()) {
            if (loadingImportsState == LoadingImportsState.STATE_UNDEFINED) {
                return loadingState;
            }
            return loadingImportsState;
        } else if (!hasLoadedRequiredModules()) {
            if (dependencyState == DependencyState.STATE_UNDEFINED) {
                return loadingImportsState;
            }
            return dependencyState;
        } else if (!isWellFormed()) {
            if (wellFormedState == WellFormedState.STATE_UNCHECKED) {
                return dependencyState;
            }
            return wellFormedState;
        } else if (!isFullyFormallyProved()) {
            if (formallyProvedState == FormallyProvedState.STATE_UNCHECKED) {
                return wellFormedState;
            }
            return formallyProvedState;
        } else {
            return formallyProvedState;
        }
    }

    /**
     * Get the last successful state we were in.
     * As there are: undefined, loaded, loaded required, fully formally proved.
     *
     * @return  Previous major error free state.
     */
    public AbstractState getLastSuccesfulState() {
        if (!isLoaded()) {
            return LoadingState.STATE_UNDEFINED;
        } else if (!hasLoadedImports()) {
            return LoadingState.STATE_LOADED;
        } else if (!hasLoadedRequiredModules()) {
            return LoadingImportsState.STATE_LOADED_IMPORTED_MODULES;
        } else if (!isWellFormed()) {
            return DependencyState.STATE_LOADED_REQUIRED_MODULES;
        } else if (!isFullyFormallyProved()) {
            return WellFormedState.STATE_CHECKED;
        } else {
            return FormallyProvedState.STATE_CHECKED;
        }
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
     * Set {@link LoadingImportsState}. Doesn't do any status handling. Only for internal use.
     *
     * @param   state   Set this loading state.
     */
    protected void setLoadingImportsState(final LoadingImportsState state) {
        this.loadingImportsState = state;
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
     * Set {@link WellFormedState}. Doesn't do any status handling. Only for internal use.
     *
     * @param   state   Set this logical state.
     */
    protected void setWellFormedState(final WellFormedState state) {
        this.wellFormedState = state;
    }

    /**
     * Set {@link FormallyProvedState}. Doesn't do any status handling. Only for internal use.
     *
     * @param   state   Set this logical state.
     */
    protected void setFormallyProvedState(final FormallyProvedState state) {
        this.formallyProvedState = state;
    }

    /**
     * Get all errors.
     *
     * @return  Errors. Is a newly created list.
     */
    public SourceFileExceptionList getErrors() {
        final SourceFileExceptionList result = new SourceFileExceptionList(errors);
        result.add(pluginResults.getAllErrors());
        return result;
    }

    /**
     * Get all warnings.
     *
     * @return  Warnings. Is a newly created list.
     */
    public SourceFileExceptionList getWarnings() {
        final SourceFileExceptionList result = new SourceFileExceptionList();
        result.add(pluginResults.getAllWarnings());
        return result;
    }

    /**
     * Set {@link SourceFileExceptionList}. Doesn't do any status handling. Only for internal use.
     *
     * @param   errors   Set this error list. If this is <code>null</code> the errors are cleared!
     */
    protected void setErrors(final SourceFileExceptionList errors) {
        this.errors = errors;
        // TODO mime 20100625: "if errors==null" this should be a new function "clearErrors" or other
        if (errors == null) {
            pluginResults = new PluginResultManager();
        }
    }

    /**
     * Add the plugin execution errors and warnings.
     *
     * @param   plugin      Plugin that was executed.
     * @param   errors      Resulting errors.
     * @param   warnings    Resulting warnings.
     */
    public void addPluginResults(final ModuleService plugin, final SourceFileExceptionList errors,
            final SourceFileExceptionList warnings) {
        if (plugin instanceof InternalModuleServicePlugin) {
            throw new RuntimeException(
                "Programming error: an internal plugin should not add exeptions here!\n"
                + plugin.getClass().getName());
        }
        pluginResults.addResult(plugin, errors, warnings);
        ModuleEventLog.getInstance().stateChanged(bo);
    }

    /**
     * Remove all plugin errors and warnings.
     */
    public void removeAllPluginResults() {
        pluginResults.removeAllResults();
        ModuleEventLog.getInstance().stateChanged(bo);
    }

    /**
     * Print the dependence tree to <code>System.out</code>.
     */
    public void printDependencyTree() {
        System.out.println("DependencyTree");
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
