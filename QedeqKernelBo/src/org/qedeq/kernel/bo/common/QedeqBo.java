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

package org.qedeq.kernel.bo.common;

import org.qedeq.kernel.se.base.module.Qedeq;
import org.qedeq.kernel.se.common.DependencyState;
import org.qedeq.kernel.se.common.LoadingState;
import org.qedeq.kernel.se.common.LogicalState;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.SourceFileExceptionList;


/**
 * Represents a module and its states.
 *
 * @author  Michael Meyling
 */
public interface QedeqBo {

    /**
     * Has the module any basic failures? This includes errors during loading the module, during load
     * of imported modules and logical checking. This includes no plugin errors.
     *
     * @return  wWre there any basic errors?
     */
    public boolean hasBasicFailures();

    /**
     * Is this a error state the module is in?
     *
     * @return  Were there any errors?
     */
    public boolean hasErrors();

    /**
     * Is this a warning state the module is in?
     *
     * @return  Were there any warnings?
     */
    public boolean hasWarnings();

    /**
     * Get {@link ModuleAddress} of module.
     *
     * @return  Address of module.
     */
    public ModuleAddress getModuleAddress();

    /**
     * Get module loading state.
     *
     * @return  Module state.
     */
    public LoadingState getLoadingState();

    /**
     * Set completeness percentage.
     *
     * @return  completeness    Completeness of loading into memory in percent.
     */
    public int getLoadingCompleteness();

   /**
    * Get module dependency state.
    *
    * @return  module state.
    */
   public DependencyState getDependencyState();

    /**
     * Get module logical state.
     *
     * @return  module state.
     */
    public LogicalState getLogicalState();

    /**
     * Get error list.
     *
     * @return  Error list.
     */
    public SourceFileExceptionList getErrors();

    /**
     * Get warning list.
     *
     * @return  Warning list.
     */
    public SourceFileExceptionList getWarnings();

    /**
     * Get module state description.
     *
     * @return  module state description.
     */
    public String getStateDescription();

    /**
     * Get name of module.
     *
     * @return  module name.
     */
    public String getName();

    /**
     * Get rule version information.
     *
     * @return  rule version.
     */
    public String getRuleVersion();

    /**
     * Get original URL of module.
     *
     * @return  URL of module.
     */
    public String getUrl();

    /**
     * Is this module already loaded?
     *
     * @return  Is this module already loaded?
     */
    public boolean isLoaded();

    /**
     * Get module. Works only if module is already completely loaded.
     *
     * @return  QEDEQ module if it is already loaded.
     */
    public Qedeq getQedeq();


    /**
     * Are all required modules loaded?
     *
     * @return  Are all required modules loaded?
     */
    public boolean hasLoadedRequiredModules();

    /**
     * Get labels and URLs of all referenced modules. Only available if module has loaded
     * all required modules. Otherwise a runtime exception is thrown.
     *
     * @return  URLs of all referenced modules.
     * @throws  IllegalStateException   Module not yet loaded.
     */
    public ModuleReferenceList getRequiredModules();

    /**
     * Was the module checked?
     *
     * @return  Module is checked?
     */
    public boolean isChecked();

}
