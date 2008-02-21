/* $Id: ModuleProperties.java,v 1.6 2008/01/26 12:39:09 m31 Exp $
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

package org.qedeq.kernel.common;

import java.net.URL;

import org.qedeq.kernel.base.module.Qedeq;


/**
 * Represents a module and its states.
 *
 * @version $Revision: 1.6 $
 * @author  Michael Meyling
 */
public interface ModuleProperties {

    /**
     * Is this a failure state the module is in?
     *
     * @return  were there any errors?
     */
    public boolean hasFailures();

    /**
     * Get {@link ModuleAddress} of module.
     *
     * @return  address of module.
     */
    public ModuleAddress getModuleAddress();

    /**
     * Get module loading state.
     *
     * @return  module state.
     */
    public LoadingState getLoadingState();

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
     * Get exception.
     *
     * @return  exception.
     */
    public SourceFileExceptionList getException();

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
    public URL getUrl();

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
     */
    public ModuleReferenceList getRequiredModules();

    /**
     * Was the module checked?
     *
     * @return  Module is checked?
     */
    public boolean isChecked();

    /**
     * In what encoding the module was parsed.
     *
     * @return  Encoding.
     */
    public String getEncoding();

}
