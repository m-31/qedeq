/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2013,  Michael Meyling <mime@qedeq.org>.
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
import org.qedeq.kernel.se.common.LogicalModuleState;
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
    public LogicalModuleState getLogicalState();

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
     * TODO 20110329 m31: this is currently only a check for "are all formulas well formed and dependencies ok"
     *
     * @return  Module is checked?
     */
    public boolean isChecked();

    /**
     * Get all supported languages for this QEDEQ module.
     *
     * @return  Array of supported languages.
     */
    public String[] getSupportedLanguages();

    /**
     * Is the given language supported.
     *
     * @param   language    Language.
     * @return  Is this language supported?
     */
    public boolean isSupportedLanguage(String language);

    /**
     * Get default language for this QEDEQ module. This should be the original language
     * of the module before it was translated. This value should be also within
     * {@link #getSupportedLanguages()}.
     *
     * @return  Original language.
     */
    public String getOriginalLanguage();

}
