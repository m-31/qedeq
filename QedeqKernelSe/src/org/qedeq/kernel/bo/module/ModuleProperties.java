/* $Id: ModuleProperties.java,v 1.4 2007/10/07 16:40:13 m31 Exp $
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

package org.qedeq.kernel.bo.module;

import java.net.URL;

import org.qedeq.kernel.common.SourceFileExceptionList;


/**
 * Represents a module and its states.
 *
 * @version $Revision: 1.4 $
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
     * Get address of module.
     *
     * @return  address of module.
     */
    public String getAddress();

    /**
     * Get {@link ModuleAddress} of module. Maybe <code>null</code>.
     *
     * @return  address of module.
     */
    public ModuleAddress getModuleAddress();

    /**
     * Set completeness percentage.
     *
     * @param   completeness    Completeness of loading into memory.
     */
    public void setLoadingCompleteness(final int completeness);

    /**
     * Set loading progress module state.
     *
     * @param   state   module state
     */
    public void setLoadingProgressState(final LoadingState state);

    /**
     * Set failure module state.
     *
     * @param   state   module state
     * @param   e       Exception that occurred during loading.
     * @throws  IllegalArgumentException    <code>state</code> is no failure state
     */
    public void setLoadingFailureState(final LoadingState state, final SourceFileExceptionList e);

    /**
     * Get module loading state.
     *
     * @return  module state.
     */
    public LoadingState getLoadingState();

    /**
     * Set dependency progress module state.
     *
     * @param   state   module state
     */
    public void setDependencyProgressState(final DependencyState state);

   /**
    * Set failure module state.
    *
    * @param   state   module state
    * @param   e       Exception that occurred during loading.
    * @throws  IllegalArgumentException    <code>state</code> is no failure state
    */
   public void setDependencyFailureState(final DependencyState state,
           final SourceFileExceptionList e);

   /**
    * Get module dependency state.
    *
    * @return  module state.
    */
   public DependencyState getDependencyState();

   /**
     * Set loading progress module state.
     *
     * @param   state   module state
     */
    public void setLogicalProgressState(final LogicalState state);

    /**
     * Set failure module state.
     *
     * @param   state   module state
     * @param   e       Exception that occurred during loading.
     * @throws  IllegalArgumentException    <code>state</code> is no failure state
     */
    public void setLogicalFailureState(final LogicalState state, final SourceFileExceptionList e);

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
     * Set checked and loaded state and module.
     *
     * @param  module   checked and loaded module.
     */
    public void setLoaded(final QedeqBo module);

    /**
     * Get module. Works only if module is already completely loaded.
     *
     * @return  QEDEQ module if it is already loaded.
     */
    public QedeqBo getModule();


    /**
     * Get labels and URLs of all referenced modules.
     *
     * @return  URLs of all referenced modules.
     */
    public ModuleReferenceList getRequiredModules();

    /**
     * Set loaded required modules state. Also set labels and URLs for all referenced modules.
     *
     * @param   list  URLs of all referenced modules.
     */
    public void setLoadedRequiredModules(ModuleReferenceList list);

}
