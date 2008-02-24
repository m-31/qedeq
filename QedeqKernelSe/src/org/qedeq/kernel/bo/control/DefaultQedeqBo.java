/* $Id: DefaultModuleProperties.java,v 1.6 2008/01/26 12:39:08 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2007,  Michael Meyling <mime@qedeq.org>.
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
import org.qedeq.kernel.common.DependencyState;
import org.qedeq.kernel.common.LoadingState;
import org.qedeq.kernel.common.LogicalState;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.ModuleLabels;
import org.qedeq.kernel.common.QedeqBo;
import org.qedeq.kernel.common.ModuleReferenceList;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.utility.EqualsUtility;


/**
 * Represents a module and its states.
 *
 * @version $Revision: 1.6 $
 * @author  Michael Meyling
 */
public class DefaultQedeqBo implements QedeqBo {

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
    private Qedeq qedeq;

    /** Failure exception. */
    private SourceFileExceptionList exception;

    /** Required QEDEQ modules. */
    private ModuleReferenceList required;

    /** Predicate and function constant existence checker. */
    private ExistenceChecker checker;

    /** Character encoding for this module. */
    private String encoding;

    /** Labels for this module. */
    private ModuleLabels labels;


    /**
     * Creates new module properties.
     *
     * @param   address     Module address (not <code>null</code>).
     * @throws  NullPointerException    <code>address</code> is null.
     */
    public DefaultQedeqBo(final ModuleAddress address) {
        this.address = address;
        if (address == null) {
            throw new NullPointerException("ModuleAddress must not be null");
        }
        loadingState = LoadingState.STATE_UNDEFINED;
        loadingCompleteness = 0;
        dependencyState = DependencyState.STATE_UNDEFINED;
        logicalState = LogicalState.STATE_UNCHECKED;
    }

    public final boolean hasFailures() {
        return loadingState.isFailure() || dependencyState.isFailure() || logicalState.isFailure();
    }

    public final ModuleAddress getModuleAddress() {
        return address;
    }

    /**
     * Set completeness percentage.
     *
     * @param   completeness    Completeness of loading into memory.
     */
    public final void setLoadingCompleteness(final int completeness) {
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
    // TODO mime 20070704: shouldn't stand here:
    //  ModuleEventLog.getInstance().stateChanged(props[i]);
    //  and not in DefaultModuleFactory?
    public final void setLoadingProgressState(final LoadingState state) {
        if (state == LoadingState.STATE_LOADED) {
            throw new IllegalArgumentException(
                "this state could only be set by calling method setLoaded");
        }
        if (state.isFailure()) {
            throw new IllegalArgumentException(
                "this is a failure state, call setLoadingFailureState");
        }
// FIXME mime 20071113: what about LogicalState and DependencyState of dependent modules?
        this.loadingState = state;
        this.qedeq = null;
        this.dependencyState = DependencyState.STATE_UNDEFINED;
        this.logicalState = LogicalState.STATE_UNCHECKED;
        this.exception = null;
    }

    /**
     * Set failure module state.
     *
     * @param   state   Module loading state. Must not be <code>null</code>.
     * @param   e       Exception that occurred during loading. Must not be <code>null</code>.
     * @throws  IllegalArgumentException    <code>state</code> is no failure state
     */
    public final void setLoadingFailureState(final LoadingState state,
            final SourceFileExceptionList e) {
        if (!state.isFailure()) {
            throw new IllegalArgumentException(
                "this is no failure state, call setLoadingProgressState");
        }
//          FIXME mime 20071113: what about LogicalState and DependencyState of dependent modules?
//          they must be killed too!
        this.qedeq = null;
        this.loadingState = state;
        this.dependencyState = DependencyState.STATE_UNDEFINED;
        this.logicalState = LogicalState.STATE_UNCHECKED;
        this.exception = e;
        if (e == null) {
            throw new NullPointerException("Exception must not be null");
        }
    }

    public final LoadingState getLoadingState() {
        return this.loadingState;
    }

    public final boolean isLoaded() {
        return loadingState == LoadingState.STATE_LOADED;
    }

    /**
     * Set loading state to "loaded".
     *
     * @param   qedeq   This module was loaded. Must not be <code>null</code>.
     * @param   labels  Set this label references. Must not be <code>null</code>.
     * @throws  NullPointerException    One argument was <code>null</code>.
     */
    public final void setLoaded(final Qedeq qedeq, final ModuleLabels labels) {
        if (qedeq == null) {
            throw new NullPointerException("Qedeq is null");
        }
        if (labels == null) {
            throw new NullPointerException("ModuleLabels is null");
        }
        loadingState = LoadingState.STATE_LOADED;
        this.qedeq = qedeq;
        this.labels = labels;
    }

    public final String getEncoding() {
        return this.encoding;
    }

    /**
     * Set character encoding for this module. Can be <code>null</code>.
     *
     * @param   encoding    Encoding.
     */
    public final void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    public final Qedeq getQedeq() {
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
    public final void setDependencyProgressState(final DependencyState state) {
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
        if (state != DependencyState.STATE_LOADED_REQUIRED_MODULES) {
//          FIXME mime 20071113: what about LogicalState and DependencyState of dependent modules?
//          they must be killed too!
            this.logicalState = LogicalState.STATE_UNCHECKED;
            this.required = null;
        }
        this.exception = null;
        this.dependencyState = state;
    }

   /**
    * Set failure module state.
    *
    * @param   state   Module dependency state. Must not be <code>null</code>.
    * @param   e       Exception that occurred during loading. Must not be <code>null</code>.
    * @throws  IllegalStateException       Module is not yet loaded.
    * @throws  IllegalArgumentException    <code>state</code> is no failure state
    * @throws  NullPointerException         <code>state</code> is <code>null</code>.
    */
    public final void setDependencyFailureState(final DependencyState state,
            final SourceFileExceptionList e) {
        if (!isLoaded()) {
            throw new IllegalStateException("module is not yet loaded");
        }
        if (!state.isFailure()) {
            throw new IllegalArgumentException(
                "this is no failure state, call setLoadingProgressState");
        }
//  FIXME mime 20071113: what about LogicalState and DependencyState of dependent modules?
//              they must be killed too!
        this.logicalState = LogicalState.STATE_UNCHECKED;
        this.dependencyState = state;
        this.exception = e;
        if (e == null) {
            throw new NullPointerException("Exception must not be null");
        }
    }

    public final DependencyState getDependencyState() {
        return this.dependencyState;
    }

    /**
     * Set loaded required modules state. Also set labels and URLs for all referenced modules.
     *
     * @param   list  URLs of all referenced modules. Must not be <code>null</code>.
     * @throws  IllegalStateException   Module is not yet loaded.
     */
    public final void setLoadedRequiredModules(final ModuleReferenceList list) {
        if (!isLoaded()) {
            throw new IllegalStateException(
                "Required modules can only be set if module is loaded."
                + "\"\nCurrently the status for the module"
                + "\"" + getName() + "\" is \"" + getLoadingState() + "\"");
        }
        dependencyState = DependencyState.STATE_LOADED_REQUIRED_MODULES;
        required = list;
    }

    public final ModuleReferenceList getRequiredModules() {
        if (!hasLoadedRequiredModules()) {
            throw new IllegalStateException(
                "module reference list exists only if state is \""
                + DependencyState.STATE_LOADED_REQUIRED_MODULES.getText() + "\"");
        }
        return required;
    }

    public final boolean hasLoadedRequiredModules() {
        return isLoaded() && dependencyState == DependencyState.STATE_LOADED_REQUIRED_MODULES;
    }

    /**
     * Set logic checked state. Also set the predicate and function existence checker.
     *
     * @param   checker Checks if a predicate or function constant is defined.
     */
    public final void setChecked(final ExistenceChecker checker) {
        if (!hasLoadedRequiredModules()) {
            throw new IllegalStateException(
                "Checked can only be set if all required modules are loaded."
                + "\"\nCurrently the status for the module"
                + "\"" + getName() + "\" is \"" + getLoadingState() + "\"");
        }
        logicalState = LogicalState.STATE_CHECKED;
        this.checker = checker;
    }

    /**
     * Get the predicate and function existence checker. Is only not <code>null</code>
     * if logic was successfully checked.
     *
     * @return  Checker. Checks if a predicate or function constant is defined.
     * @throws  IllegalStateException   Module is not yet checked.
     */
    public final ExistenceChecker getExistenceChecker() {
        if (!isChecked()) {
            throw new IllegalStateException(
                "existence checker exists only if state is \""
                + LogicalState.STATE_CHECKED.getText() + "\"");
        }
        return checker;
    }

    public final boolean isChecked() {
        return isLoaded() && hasLoadedRequiredModules()
            && logicalState == LogicalState.STATE_CHECKED;
    }

    // TODO mime 20070704: shouldn't stand here:
    //  ModuleEventLog.getInstance().stateChanged(props[i]);
    //  and not in DefaultModuleFactory?
   /**
    * Set loading progress module state. Must not be <code>null</code>.
    *
    * @param   state   module state
    */
    public final void setLogicalProgressState(final LogicalState state) {
        if (dependencyState.getCode() < DependencyState.STATE_LOADED_REQUIRED_MODULES.getCode()
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
        this.exception = null;
        this.logicalState = state;
    }

    /**
     * Set failure module state.
     *
     * @param   state   module state
     * @param   e       Exception that occurred during loading.
     * @throws  IllegalArgumentException    <code>state</code> is no failure state
     */
    public final void setLogicalFailureState(final LogicalState state,
            final SourceFileExceptionList e) {
        if ((!isLoaded() || !hasLoadedRequiredModules()) && state != LogicalState.STATE_UNCHECKED) {
            throw new IllegalArgumentException(
                "this state could only be set if all required modules are loaded ");
        }
        if (!state.isFailure()) {
            throw new IllegalArgumentException(
                "this is no failure state, call setLogicalProgressState");
        }
        this.logicalState = state;
        this.exception = e;
    }

    public final LogicalState getLogicalState() {
        return this.logicalState;
    }

    public final SourceFileExceptionList getException() {
        return this.exception;
    }

    public final String getStateDescription() {
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

    public final String getName() {
        if (address == null) {
            return "null";
        }
        return address.getName();
    }

    public final String getRuleVersion() {
        if (address == null || qedeq == null
                || qedeq.getHeader() == null
                || qedeq.getHeader().getSpecification() == null
                || qedeq.getHeader().getSpecification().getRuleVersion() == null) {
            return "";
        }
        return qedeq.getHeader().getSpecification().getRuleVersion();
    }

    public final URL getUrl() {
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
    public void setLabels(final ModuleLabels labels) {
        this.labels = labels;
    }

    /**
     * Get label references for QEDEQ module.
     *
     * @return  Label references.
     */
    public ModuleLabels getLabels() {
        return labels;
    }

    public int hashCode() {
        return (getModuleAddress() == null ? 0 : getModuleAddress().hashCode());
    }

    public boolean equals(final Object obj) {
        if (obj instanceof DefaultQedeqBo) {
            return EqualsUtility.equals(((DefaultQedeqBo) obj).getModuleAddress(),
                this.getModuleAddress());
        }
        return false;
    }

    public String toString() {
       return address + ";" + loadingState;
    }

}
