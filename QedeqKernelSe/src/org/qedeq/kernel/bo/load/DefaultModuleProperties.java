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

package org.qedeq.kernel.bo.load;

import java.net.URL;

import org.qedeq.kernel.bo.logic.ExistenceChecker;
import org.qedeq.kernel.bo.module.DependencyState;
import org.qedeq.kernel.bo.module.LoadingState;
import org.qedeq.kernel.bo.module.LogicalState;
import org.qedeq.kernel.bo.module.ModuleAddress;
import org.qedeq.kernel.bo.module.ModuleProperties;
import org.qedeq.kernel.bo.module.ModuleReferenceList;
import org.qedeq.kernel.bo.module.QedeqBo;
import org.qedeq.kernel.common.SourceFileExceptionList;


/**
 * Represents a module and its states.
 *
 * @version $Revision: 1.6 $
 * @author  Michael Meyling
 */
public class DefaultModuleProperties implements ModuleProperties {

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
    private QedeqBo module;

    /** Failure exception. */
    private SourceFileExceptionList exception;

    /** Required QEDEQ modules. */
    private ModuleReferenceList required;

    /** Predicate and function constant existence checker. */
    private ExistenceChecker checker;


    /**
     * Creates new module properties.
     *
     * @param   address     Module address (not <code>null</code>).
     */
    public DefaultModuleProperties(final ModuleAddress address) {
        this.address = address;
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

    public final void setLoadingCompleteness(final int completeness) {
        this.loadingCompleteness = completeness;
    }

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
        this.module = null;
        this.dependencyState = DependencyState.STATE_UNDEFINED;
        this.logicalState = LogicalState.STATE_UNCHECKED;
        this.exception = null;
    }

    public final void setLoadingFailureState(final LoadingState state,
            final SourceFileExceptionList e) {
        if (!state.isFailure()) {
            throw new IllegalArgumentException(
                "this is no failure state, call setLoadingProgressState");
        }
//          FIXME mime 20071113: what about LogicalState and DependencyState of dependent modules?
//          they must be killed too!
        this.module = null;
        this.loadingState = state;
        this.dependencyState = DependencyState.STATE_UNDEFINED;
        this.logicalState = LogicalState.STATE_UNCHECKED;
        this.exception = e;
    }

    public final LoadingState getLoadingState() {
        return this.loadingState;
    }

    public final boolean isLoaded() {
        return loadingState == LoadingState.STATE_LOADED;
    }

    public final void setLoaded(final QedeqBo module) {
        loadingState = LoadingState.STATE_LOADED;
        this.module = module;
    }

    public final QedeqBo getModule() {
        if (loadingState != LoadingState.STATE_LOADED) {
            throw new IllegalStateException(
                "module exists only if state is \"" + LoadingState.STATE_LOADED.getText()
                +   "\"");
        }
        return this.module;
    }

    public final void setDependencyProgressState(final DependencyState state) {
        if (!isLoaded() && state != DependencyState.STATE_UNDEFINED) {
            throw new IllegalArgumentException("module is not yet loaded");
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

    public final void setDependencyFailureState(final DependencyState state,
            final SourceFileExceptionList e) {
        if (!isLoaded()) {
            throw new IllegalArgumentException("module is not yet loaded");
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
    }

    public final DependencyState getDependencyState() {
        return this.dependencyState;
    }

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
        return loadingState == LoadingState.STATE_LOADED
            && dependencyState == DependencyState.STATE_LOADED_REQUIRED_MODULES;
    }

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

    public final ExistenceChecker getExistenceChecker() {
        if (!hasLoadedRequiredModules()) {
            throw new IllegalStateException(
                "existence checker exists only if state is \""
                + LogicalState.STATE_CHECKED.getText() + "\"");
        }
        return checker;
    }

    public final boolean isChecked() {
        return loadingState == LoadingState.STATE_LOADED
            && dependencyState == DependencyState.STATE_LOADED_REQUIRED_MODULES
            && logicalState == LogicalState.STATE_CHECKED;
    }

    // TODO mime 20070704: shouldn't stand here:
    //  ModuleEventLog.getInstance().stateChanged(props[i]);
    //  and not in DefaultModuleFactory?
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
        if (address == null || module == null || module.getQedeq() == null
                || module.getQedeq().getHeader() == null
                || module.getQedeq().getHeader().getSpecification() == null
                || module.getQedeq().getHeader().getSpecification().getRuleVersion() == null) {
            return "";
        }
        return module.getQedeq().getHeader().getSpecification().getRuleVersion();
    }

    public final URL getUrl() {
        if (address == null) {
            return null;
        }
        return address.getURL();
    }

    public final String toString() {
       return super.toString() + ":" + address + ";" + loadingState;
    }

}
