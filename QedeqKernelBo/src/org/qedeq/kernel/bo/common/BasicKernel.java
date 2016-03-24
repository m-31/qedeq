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

package org.qedeq.kernel.bo.common;

import java.net.URLConnection;

import org.qedeq.base.utility.YodaUtility;


/**
 * This class provides basic informations about the kernel.
 *
 * @author  Michael Meyling
 */
public final class BasicKernel implements KernelProperties {


    /** Version of this kernel. */
    private static final String KERNEL_VERSION = "0.04.08";

    /** Version dependent directory of this kernel. */
    private static final String KERNEL_VERSION_DIRECTORY = KERNEL_VERSION.replace('.', '_');

    /** Version code . */
    private static final String KERNEL_CODE_NAME = "gaffsie";

    /** Kernel version dedication. */
    private static final String KERNEL_DEDICATED
        = "dedicated to Balsa, a spear woman and mercenary from Kanbal, the kingdom across the mountains.";

    /** Descriptive version information of this kernel. */
    private static final String DESCRIPTIVE_KERNEL_VERSION
        = "Hilbert II - Version " + KERNEL_VERSION + " (" + KERNEL_CODE_NAME + ") ["
        + getBuildIdFromManifest() + "] " + KERNEL_DEDICATED;

    /** Maximal supported rule version of this kernel. */
    private static final String MAXIMAL_RULE_VERSION = "1.01.00";

    /**
     * Constructor.
     */
    public BasicKernel() {
        // nothing to do
    }

    /**
     * Get build information from JAR manifest file. Is also non empty string if no manifest
     * information is available.
     *
     * @return  Implementation-version.
     */
    private static String getBuildIdFromManifest() {
        String build = BasicKernel.class.getPackage().getImplementationVersion();
        if (build == null) {
            build = "no regular build";
        }
        return build;
    }

    public String getBuildId() {
        return getBuildIdFromManifest();
    }

    public final String getKernelVersion() {
        return KERNEL_VERSION;
    }

    public final String getKernelCodeName() {
        return KERNEL_CODE_NAME;
    }

    public final String getKernelVersionDirectory() {
        return KERNEL_VERSION_DIRECTORY;
    }

    public final String getDescriptiveKernelVersion() {
        return DESCRIPTIVE_KERNEL_VERSION;
    }

    public final String getDedication() {
        return KERNEL_DEDICATED;
    }

    public final String getMaximalRuleVersion() {
        return MAXIMAL_RULE_VERSION;
    }

    public final boolean isRuleVersionSupported(final String ruleVersion) {
        // FIXME 20130113 m31: this must change if we really want to use it
        return MAXIMAL_RULE_VERSION.equals(ruleVersion);
    }

    /**
     * This class ist just for solving the lazy loading problem thread save.
     * see <a href="http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom">
     * Initialization_on_demand_holder_idiom</a>.
     */
    private static final class LazyHolderTimeoutMethods {

        /** Lazy initialized constant that knows about the existence of the method
         * <code>URLConnection.setConnectTimeout</code>. This depends on the currently running
         * JVM. */
        private static final boolean IS_SET_CONNECTION_TIMEOUT_SUPPORTED = YodaUtility.existsMethod(
            URLConnection.class, "setConnectTimeout",
            new Class[] {Integer.TYPE});

        /** Lazy initialized constant that knows about the existence of the method
         * <code>URLConnection.setReadTimeout</code>. This depends on the currently running
         * JVM. */
        private static final boolean IS_SET_READ_TIMEOUT_SUSPPORTED = YodaUtility.existsMethod(
            URLConnection.class, "setReadTimeout",
            new Class[] {Integer.TYPE});

        /**
         * Hidden constructor.
         */
        private LazyHolderTimeoutMethods() {
            // nothing to do
        }

    }

    public boolean isSetConnectionTimeOutSupported() {
        return LazyHolderTimeoutMethods.IS_SET_CONNECTION_TIMEOUT_SUPPORTED;
    }

    public boolean isSetReadTimeoutSupported() {
        return LazyHolderTimeoutMethods.IS_SET_READ_TIMEOUT_SUSPPORTED;
    }

}
