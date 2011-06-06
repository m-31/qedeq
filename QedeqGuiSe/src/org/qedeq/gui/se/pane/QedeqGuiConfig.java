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

package org.qedeq.gui.se.pane;

import java.io.File;
import java.io.IOException;

import org.qedeq.base.io.IoUtility;
import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.se.config.QedeqConfig;

/**
 * This class gives a type save access to the GUI properties of the application.
 *
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
     * @param   configFile      Config file.
     * @param   basisDirectory  Start directory of application. Basis for all relative paths.
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
     * @param   configFile      Config file.
     * @param   description     Config file description
     * @param   basisDirectory  Start directory of application. Basis for all relative paths.
     * @throws  IOException     Config file couldn't be loaded.
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
     * @param   value   Automatic scroll for log window?
     */
    public final void setAutomaticLogScroll(final boolean value) {
        setKeyValue("automaticLogScroll", (value ? "true" : "false"));
    }

    /**
     * Get icon size. Default is "16x16".
     *
     * @return  Icon size.
     */
    public final String getIconSize() {
        return getKeyValue("iconSize", "16x16");
    }

    /**
     * Set icon size.
     *
     * @param   value   Icon size value. Something like "16x16".
     */
    public final void setIconSize(final String value) {
        setKeyValue("iconSize", value);
    }

    /**
     * Get look and feel setting. Default is "PlasticXP".
     * Might also be a direct class name.
     * Supported keys are "Windows", "Plastic", "Plastic3D", "PlasticXP".
     *
     * @return  Windows look and feel.
     */
    public final String getLookAndFeel() {
        return getKeyValue("lookAndFeel", "PlasticXP");
    }

    /**
     * Set look and feel setting.
     * Supported keys are "Windows", "Plastic", "Plastic3D", "PlasticXP".
     *
     * @param   value   Look and feel key, or class name.
     */
    public final void setLookAndFeel(final String value) {
        setKeyValue("lookAndFeel", value);
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

