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



/**
 * QEDEQ kernel.
 *
 * @author  Michael Meyling
 */
public interface KernelProperties {

    /**
     * Get relative version directory of this kernel.
     *
     * @return  Version sub directory.
     */
    public String getKernelVersionDirectory();

    /**
     * Get build information.
     *
     * @return  Implementation-version.
     */
    public String getBuildId();

    /**
     * Get version of this kernel.
     *
     * @return  Kernel version.
     */
    public String getKernelVersion();

    /**
     * Get code name of this kernel.
     *
     * @return  Kernel code name.
     */
    public String getKernelCodeName();

    /**
     * Get descriptive version information of this kernel.
     *
     * @return  Version Information.
     */
    public String getDescriptiveKernelVersion();

    /**
     * Get dedication for this kernel.
     *
     * @return  Kernel code dedication.
     */
    public String getDedication();

    /**
     * Get maximal supported rule version of this kernel.
     *
     * @return  Maximal supported rule version.
     */
    public String getMaximalRuleVersion();

    /**
     * Is a given rule version supported?
     *
     * @param   ruleVersion Check this one.
     * @return  Is the given rule version supported?
     */
    public boolean isRuleVersionSupported(String ruleVersion);

    /**
     * Does {@link java.net.URLConnection} support the method <code>setConnectionTimeOut</code>
     * in the currently running JVM. This should be true since version 1.5 but false for 1.4.2.
     *
     * @return Method is supported?
     */
    public boolean isSetConnectionTimeOutSupported();

    /**
     * Does {@link java.net.URLConnection} support the method <code>setReadTimeOut</code>
     * in the currently running JVM. This should be true since version 1.5 but false for 1.4.2.
     *
     * @return Method is supported?
     */
    public boolean isSetReadTimeoutSupported();

}
