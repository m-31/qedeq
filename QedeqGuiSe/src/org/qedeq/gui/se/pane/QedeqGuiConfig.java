/* $Id: QedeqGuiConfig.java,v 1.1 2007/10/07 16:39:59 m31 Exp $
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

package org.qedeq.gui.se.pane;

import java.io.File;
import java.io.IOException;

import org.qedeq.kernel.config.QedeqConfig;
import org.qedeq.kernel.context.KernelContext;
import org.qedeq.kernel.utility.IoUtility;

/**
 * This class gives a type save access to the GUI properties of the application.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public final class QedeqGuiConfig extends QedeqConfig {

    /** The one and only instance. */
    private static QedeqGuiConfig instance = null;

    /**
     * Get instance of config access. Method {@link #init} must have been called before.
     *
     * @return  singleton, which is responsible the config access
     * @throws  IllegalStateException   if {@link #setup} wasn't called before this method call
     */
    public static QedeqGuiConfig getInstance() {
        if (instance == null) {
            throw new IllegalStateException(QedeqGuiConfig.class.getName()
                + " not initialized, call init before!");
        }
        return instance;
    }

    /**
     * Load config file from start directory. This method must be called at the beginning.
     * Look at {link IoUtility#getStartDirectory(String)} with parameter <code>qedeq</code>
     *
     * @param   configFileName   Name of config file.
     * @param   basisDirectory   Start directory of application. Basis for all relative paths.
     * @throws  IOException Config file not found or readable.
     */
    public static void init(final File configFile, final File basisDirectory) throws IOException {
        instance = new QedeqGuiConfig(configFile,
            "This file is part of the project *Hilbert II* - http://www.qedeq.org",
            basisDirectory);
    }

    /**
     * Constructor.
     *
     * @param   configDirectory  Directory location of config file.
     * @param   configFileName   Name of config file.
     * @param   description      Config file description
     * @param   basisDirectory   Start directory of application. Basis for all relative paths.
     * @throws  IOException      Config file couldn't be loaded.
     */
    private QedeqGuiConfig(final File configFile,
            final String description, final File basisDirectory) throws IOException {
        super(configFile, description, basisDirectory);
    }


    /**
     * Should the log window scroll automatically to the last line?
     *
     * @return  Automatic scroll for log window?
     */
    public final boolean isAutomaticLogScroll() {
        return "true".equals(getKeyValue("automaticLogScroll", "true"));
    }

    /**
     * Should the log window scroll automatically to the last line?
     *
     * @return  Automatic scroll for log window?
     */
    public final void setAutomaticLogScroll(final boolean value) {
        setKeyValue("automaticLogScroll", (value ? "true" : "false"));
    }

    /**
     * Get autostart html mode.
     *
     * @return  list of modules.
     */
    public final boolean isAutoStartHtmlBrowser() {
        return "true".equals(
            getKeyValue("autoStartHtmlBrowser", "true"));
    }

    /**
     * Set auto start HTML browser.
     *
     * @param  mode Auto start?
     */
    public final void setAutoStartHtmlBrowser(final boolean mode) {
        setKeyValue("autoStartHtmlBrowser", (mode ? "true" : "false"));
    }

    /**
     * Get direct response mode.
     *
     * @return  Direct response mode.
     */
    public final boolean isDirectResponse() {
        return "true".equals(
            getKeyValue("directResponse", "true"));
    }

    /**
     * Set direct response mode.
     *
     * @param  mode     enable direct response?
     */
    public final void setDirectResponse(final boolean mode) {
        setKeyValue("directResponse", (mode ? "true" : "false"));
    }

    /**
     * Get start directory for file browser.
     *
     * @return  Directory.
     */
    public final File getFileBrowserStartDirecty() {
        final File dflt = (IoUtility.isWebStarted() ? KernelContext.getInstance().getConfig()
            .getBasisDirectory() : new File("./sample"));

        final String dir = getKeyValue("fileBrowserStartDirectory");
        if (dir == null || dir.length() <= 0) {
            return dflt;
        }
        return new File(dir);
    }

    /**
     * Set start directory for file browser.
     *
     * @param  directory    Start directory.
     */
    public final void setFileBrowserStartDirecty(final File directory) {
        setKeyValue("fileBrowserStartDirectory", directory.toString());
    }


}
