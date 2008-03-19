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

import org.qedeq.kernel.bo.logic.ExistenceChecker;
import org.qedeq.kernel.common.DependencyState;
import org.qedeq.kernel.common.LoadingState;
import org.qedeq.kernel.common.LogicalState;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.dto.module.QedeqVo;
import org.qedeq.kernel.log.ModuleEventLog;


/**
 * Changes the states of {@link org.qedeq.kernel.bo.control.KernelQedeqBo}s.
 *
 * @version $Revision: 1.6 $
 * @author  Michael Meyling
 */
public class StateManager {

    /** Main BO to care about. */
    private final KernelQedeqBo bo;

    StateManager(final KernelQedeqBo bo) {
        this.bo = bo;
    }

    /**
     * Set completeness percentage.
     *
     * @param   completeness    Completeness of loading into memory.
     */
    public void setLoadingCompleteness(final int completeness) {
        bo.setLoadingCompleteness(completeness);
    }

    /**
     * Set loading state to deleted, remove entry from dependent modules
     * and log status without further actions.
     */
    protected void setDeleted() {
        bo.setXXXLoadingState(LoadingState.STATE_DELETED);
        final KernelModuleReferenceList required = bo.getKernelRequiredModules();
        for (int i = 0; i < required.size(); i++) {
            final KernelQedeqBo ref = required.getKernelQedeqBo(i);
            KernelModuleReferenceList dependents = ref.getDependentModules();
            if (!dependents.contains(bo)) {
                System.out.println(ref + " missing dependent module: " + bo.getName());
                throw new RuntimeException(ref + " missing dependent module: " + bo.getUrl()); // FIXME
            }
            dependents.remove(bo);
        }
        ModuleEventLog.getInstance().removeModule(bo);
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
        if (bo.getLoadingState() == LoadingState.STATE_UNDEFINED) {
            ModuleEventLog.getInstance().addModule(bo);
        }
        if (state == LoadingState.STATE_LOADING_FROM_BUFFER) {
            invalidateOtherDependentModulesToLoaded();
        }
        if (state == LoadingState.STATE_DELETED) {
            invalidateOtherDependentModulesToLoaded();
            setDeleted();
        } else {
            bo.setXXXLoadingState(state);
        }
        bo.setXXXQedeqVo(null);
        bo.getKernelRequiredModules().clear();
        bo.setXXXDependencyState(DependencyState.STATE_UNDEFINED);
        bo.setXXXLogicalState(LogicalState.STATE_UNCHECKED);
        bo.setXXXException(null);
        if (state != LoadingState.STATE_DELETED) {  // FIXME: Logging in POOL but not here?
            ModuleEventLog.getInstance().stateChanged(bo);
        }
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
        if (bo.getLoadingState() == LoadingState.STATE_UNDEFINED) {
            ModuleEventLog.getInstance().addModule(bo);
        }
        invalidateOtherDependentModulesToLoaded();
        bo.setXXXQedeqVo(null);
        bo.getKernelRequiredModules().clear();
        bo.setLabels(null);
        bo.setXXXLoadingState(state);
        bo.setXXXDependencyState(DependencyState.STATE_UNDEFINED);
        bo.setXXXLogicalState(LogicalState.STATE_UNCHECKED);
        bo.setXXXException(e);
        if (e == null) {
            throw new NullPointerException("Exception must not be null");
        }
        ModuleEventLog.getInstance().stateChanged(bo);
    }

    /**
     * Set loading state to "loaded". Also puts <code>null</code> to {@link #getLabels()}.
     *
     * @param   qedeq   This module was loaded. Must not be <code>null</code>.
     * @throws  NullPointerException    One argument was <code>null</code>.
     */
    public void setLoaded(final QedeqVo qedeq) {
        checkIfDeleted();
        if (qedeq == null) {
            throw new NullPointerException("Qedeq is null");
        }
        invalidateOtherDependentModulesToLoaded();
        bo.setXXXLoadingState(LoadingState.STATE_LOADED);
        bo.setXXXQedeqVo(qedeq);
        bo.getKernelRequiredModules().clear();
        bo.setLabels(null);
        bo.setXXXDependencyState(DependencyState.STATE_UNDEFINED);
        bo.setXXXLogicalState(LogicalState.STATE_UNCHECKED);
        bo.setXXXException(null);
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
        if (!bo.isLoaded() && state != DependencyState.STATE_UNDEFINED) {
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
        bo.setXXXLogicalState(LogicalState.STATE_UNCHECKED);
        bo.setXXXDependencyState(state);
        bo.getKernelRequiredModules().clear();
        bo.setXXXException(null);
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
        if (!bo.isLoaded()) {
            throw new IllegalStateException("module is not yet loaded");
        }
        if (!state.isFailure()) {
            throw new IllegalArgumentException(
                "this is no failure state, call setLoadingProgressState");
        }
        invalidateOtherDependentModulesToLoadedRequired();
        bo.setXXXDependencyState(state);
        bo.setXXXException(e);
        if (e == null) {
            throw new NullPointerException("Exception must not be null");
        }
        ModuleEventLog.getInstance().stateChanged(bo);
    }

    /**
     * Reset all (recursive) dependent modules (if any) to state loaded.
     */
    private void invalidateOtherDependentModulesToLoaded() {
        System.out.println("resetting other dependent modules for " + bo.getName());
        if (bo.hasLoadedRequiredModules()) {
            final KernelModuleReferenceList dependent = bo.getDependentModules();
            for (int i = 0; i < dependent.size(); i++) {
                KernelQedeqBo ref = dependent.getKernelQedeqBo(i);
                System.out.println("invalidating to loaded for " + ref.getName());
                ref.getKernelRequiredModules().remove(bo);
                ref.getXXXStateManager().invalidateDependentModulesToLoaded();
            }
            dependent.clear();
            bo.getKernelRequiredModules().clear();
        }
    }

    /**
     * Reset this and all (recursive) dependent modules (if any) to state loaded.
     */
    private void invalidateDependentModulesToLoaded() {
        if (bo.hasLoadedRequiredModules()) {
            final KernelModuleReferenceList dependent = bo.getDependentModules();
            for (int i = 0; i < dependent.size(); i++) {
                KernelQedeqBo ref = dependent.getKernelQedeqBo(i);
                ref.getXXXStateManager().invalidateDependentModulesToLoaded();
                ref.getKernelRequiredModules().remove(bo);
            }
            invalidateThisModule();
            dependent.clear();
            bo.getKernelRequiredModules().clear();
            bo.setXXXLoadingState(LoadingState.STATE_LOADED);
            ModuleEventLog.getInstance().stateChanged(bo);
        }
    }

    /**
     * Reset all (recursive) dependent modules (if any) to state loaded required.
     */
    private void invalidateOtherDependentModulesToLoadedRequired() {
        if (bo.isChecked()) {
            final KernelModuleReferenceList dependent = bo.getDependentModules();
            for (int i = 0; i < dependent.size(); i++) {
                KernelQedeqBo ref = dependent.getKernelQedeqBo(i);
                ref.getXXXStateManager().invalidateDependentModulesToLoadedRequired();
            }
        }
    }

    /**
     * Reset this and all (recursive) dependent modules (if any) to state loaded.
     */
    private void invalidateDependentModulesToLoadedRequired() {
        if (bo.isChecked()) {
            final KernelModuleReferenceList dependent = bo.getDependentModules();
            for (int i = 0; i < dependent.size(); i++) {
                KernelQedeqBo ref = dependent.getKernelQedeqBo(i);
                ref.getXXXStateManager().invalidateDependentModulesToLoadedRequired();
            }
            invalidateThisModule();
            bo.setXXXDependencyState(DependencyState.STATE_LOADED_REQUIRED_MODULES);
            ModuleEventLog.getInstance().stateChanged(bo);
        }
    }

    private void invalidateThisModule() {
        bo.setXXXLoadingState(LoadingState.STATE_LOADED);
        bo.setXXXDependencyState(DependencyState.STATE_UNDEFINED);
        bo.setXXXLogicalState(LogicalState.STATE_UNCHECKED);
        bo.setXXXException(null);
        bo.getKernelRequiredModules().clear();
//        this.dependent.clear();
    }

    /**
     * Set loaded required requirements state.
     *
     * @param   required  URLs of all referenced modules. Must not be <code>null</code>.
     * @throws  IllegalStateException   Module is not yet loaded.
     */
    public void setLoadedRequiredModules(final KernelModuleReferenceList required) {
        checkIfDeleted();
        if (!bo.isLoaded()) {
            throw new IllegalStateException(
                "Required modules can only be set if module is loaded."
                + "\"\nCurrently the status for the module"
                + "\"" + bo.getName() + "\" is \"" + bo.getLoadingState() + "\"");
        }
        invalidateDependentModulesToLoadedRequired();

        for (int i = 0; i < required.size(); i++) {
            KernelQedeqBo current = required.getKernelQedeqBo(i);
            try {
                current.getDependentModules().add(required.getModuleContext(i), // FIXME
                    required.getLabel(i), bo);
                System.out.println(current.getName() + " needed by " + bo.getName());
            } catch (ModuleDataException me) {  // should never happen FIXME
                throw new RuntimeException(me);
            }
        }


        bo.setXXXDependencyState(DependencyState.STATE_LOADED_REQUIRED_MODULES);
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
        if (!bo.hasLoadedRequiredModules()) {
            throw new IllegalStateException(
                "Checked can only be set if all required modules are loaded."
                + "\"\nCurrently the status for the module"
                + "\"" + bo.getName() + "\" is \"" + bo.getLoadingState() + "\"");
        }
        bo.setXXXLogicalState(LogicalState.STATE_CHECKED);
        bo.setXXXExistenceChecker(checker);
        ModuleEventLog.getInstance().stateChanged(bo);
    }

   /**
    * Set loading progress module state. Must not be <code>null</code>.
    *
    * @param   state   module state
    */
    public void setLogicalProgressState(final LogicalState state) {
        if (bo.getDependencyState().getCode() < DependencyState.STATE_LOADED_REQUIRED_MODULES.getCode()
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
        bo.setXXXException(null);
        bo.setXXXLogicalState(state);
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
        if ((!bo.isLoaded() || !bo.hasLoadedRequiredModules()) && state != LogicalState.STATE_UNCHECKED) {
            throw new IllegalArgumentException(
                "this state could only be set if all required modules are loaded ");
        }
        if (!state.isFailure()) {
            throw new IllegalArgumentException(
                "this is no failure state, call setLogicalProgressState");
        }
        invalidateDependentModulesToLoadedRequired();
        bo.setXXXLogicalState(state);
        bo.setXXXException(e);
        ModuleEventLog.getInstance().stateChanged(bo);
    }

    /**
     * Checks if the current instance is already deleted.
     *
     * @throws  IllegalStateException   Module is already deleted.
     */
    private void checkIfDeleted() {
        if (bo.getLoadingState() == LoadingState.STATE_DELETED) {
            throw new IllegalStateException("module is already deleted: " + bo.getUrl());
        }
    }


}
