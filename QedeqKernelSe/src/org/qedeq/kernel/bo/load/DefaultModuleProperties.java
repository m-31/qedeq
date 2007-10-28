/* $Id: DefaultModuleProperties.java,v 1.4 2007/10/07 16:40:12 m31 Exp $
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

import org.qedeq.kernel.bo.module.LoadingState;
import org.qedeq.kernel.bo.module.LogicalState;
import org.qedeq.kernel.bo.module.ModuleAddress;
import org.qedeq.kernel.bo.module.ModuleProperties;
import org.qedeq.kernel.bo.module.ModuleReferenceList;
import org.qedeq.kernel.bo.module.QedeqBo;
import org.qedeq.kernel.common.XmlFileExceptionList;


/**
 * Represents a module and its states.
 *
 * @version $Revision: 1.4 $
 * @author  Michael Meyling
 */
public class DefaultModuleProperties implements ModuleProperties {

    /** Address and module specification. */
    private final ModuleAddress address;

    /** Completeness during loading from web. */
    private int loadingCompleteness;

    /** Describes QEDEQ module loading state. */
    private LoadingState loadingState;

    /** Describes QEDEQ module logical state. */
    private LogicalState logicalState;

    /** Loaded QEDEQ module. */
    private QedeqBo module;

    /** Failure exception. */
    private XmlFileExceptionList exception;

    /** Required QEDEQ modules. */
    private ModuleReferenceList required;


    /**
     * Creates new module properties.
     *
     * @param   address     module address
     */
    public DefaultModuleProperties(final ModuleAddress address) {
        this.address = address;
        this.loadingState = LoadingState.STATE_UNDEFINED;
        this.loadingCompleteness = 0;
        this.logicalState = LogicalState.STATE_UNCHECKED;
    }

    public final boolean hasFailures() {
        return this.loadingState.isFailure();
    }

    public final String getAddress() {
        if (address == null) {
            return "null";
        }
        return address.getAddress();
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
        if (state == LoadingState.STATE_LOADED_REQUIRED_MODULES) {
            throw new IllegalArgumentException(
                "this state could only be set by calling method setLoadedRequiredModules");
        }
        if (this.loadingState.getCode() < LoadingState.STATE_LOADED.getCode()) {
            this.module = null;
        }
        if (state.isFailure()) {
            throw new IllegalArgumentException(
                "this is a failure state, call setLoadingFailureState");
        }
        this.exception = null;
        this.loadingState = state;
    }

    public final void setLoadingFailureState(final LoadingState state,
            final XmlFileExceptionList e) {
        if (!state.isFailure()) {
            throw new IllegalArgumentException(
                "this is no failure state, call setProgressState");
        }
        if (this.loadingState.getCode() < LoadingState.STATE_LOADED.getCode()) {
            this.module = null;
        }
        this.loadingState = state;
        this.exception = e;
    }

    public final LoadingState getLoadingState() {
        return this.loadingState;
    }

    // TODO mime 20070704: shouldn't stand here:
    //  ModuleEventLog.getInstance().stateChanged(props[i]);
    //  and not in DefaultModuleFactory?
    public final void setLogicalProgressState(final LogicalState state) {
        if (state != LogicalState.STATE_UNCHECKED
                && loadingState.getCode() < LoadingState.STATE_LOADED_REQUIRED_MODULES.getCode()) {
            throw new IllegalArgumentException(
                "this state could only be set if all required modules are loaded ");
        }
        if (state.isFailure()) {
            throw new IllegalArgumentException(
                "this is a failure state, call setLogicalFailureState");
        }
        this.exception = null;
        this.logicalState = state;
    }

    public final void setLogicalFailureState(final LogicalState state,
            final XmlFileExceptionList e) {
        if (state != LogicalState.STATE_UNCHECKED
                && loadingState.getCode() < LoadingState.STATE_LOADED_REQUIRED_MODULES.getCode()) {
            throw new IllegalArgumentException(
            "this state could only be set if all required modules are loaded ");
        }
        if (!state.isFailure()) {
            throw new IllegalArgumentException(
                "this is no failure state, call setProgressState");
        }
        this.logicalState = state;
        this.exception = e;
    }

    public final LogicalState getLogicalState() {
        return this.logicalState;
    }

    public final XmlFileExceptionList getException() {
        return this.exception;
    }

    public final String getStateDescription() {
        if (loadingState == LoadingState.STATE_LOADING_FROM_WEB) {
            return loadingState.getText() + " (" + loadingCompleteness + "%)";
        } else if (!isLoaded()) {
            return loadingState.getText();
        } else if (getLogicalState() != LogicalState.STATE_UNCHECKED) {
            return logicalState.getText();
        } else {
            return loadingState.getText();
        }
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

    public final boolean isLoaded() {
        return (loadingState.getCode() >= LoadingState.STATE_LOADED.getCode());
    }

    public final void setLoaded(final QedeqBo module) {
        loadingState = LoadingState.STATE_LOADED;
        this.module = module;
    }

    public final QedeqBo getModule() {
        if (loadingState.getCode() < LoadingState.STATE_LOADED.getCode()) {
            throw new IllegalStateException(
                "module exists only if state is \"" + LoadingState.STATE_LOADED.getText()
                +   "\"");
        }
        return this.module;
    }

    public final ModuleReferenceList getRequiredModules() {
        if (loadingState != LoadingState.STATE_LOADED_REQUIRED_MODULES) {
            throw new IllegalStateException(
                "module exists only if state is \"" + LoadingState.STATE_LOADED_REQUIRED_MODULES
                .getText() + "\"");
        }
        return required;
    }

    public final void setLoadedRequiredModules(final ModuleReferenceList list) {
        if (!isLoaded()) {
            throw new IllegalStateException(
                "Required modules can only be set if module is loaded."
                + "\"\nCurrently the status for the module"
                + "\"" + getName() + "\" is \"" + getLoadingState() + "\"");
        }
        loadingState = LoadingState.STATE_LOADED_REQUIRED_MODULES;
        required = list;
    }

    public final String toString() {
       return super.toString() + ":" + address + ";" + loadingState;
    }

}
