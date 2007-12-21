/* $Id: QedeqConfig.java,v 1.4 2007/12/21 23:33:46 m31 Exp $
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

package org.qedeq.kernel.config;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;


/**
 * This class gives a type save access to properties of the application.
 *
 * @version $Revision: 1.4 $
 * @author  Michael Meyling
 */
public class QedeqConfig {

    /** Default location for newly created QEDEQ modules. */
    private static final String DEFAULT_LOCAL_MODULES_DIRECTORY
        = "local";

    /** Default location for locally buffered module files. */
    private static final String DEFAULT_LOCAL_BUFFER
        = "buffer";

    /** Default location for generated module and document files. */
    private static final String DEFAULT_GENERATED
        = "generated";

    /** Default log file path. */
    private static final String DEFAULT_LOG_FILE
        = "log/log.txt";

    /** This class organizes the access to the config parameters. */
    private final ConfigAccess configAccess;

    /** Basis directory of application for all variable data. Basis for all relative paths. */
    private final File basisDirectory;

    /**
     * Constructor.
     *
     * @param   configFile       Config file.
     * @param   description      Config file description.
     * @param   basisDirectory   Basis directory of application for all variable data. Basis for all
     *                           relative paths
     * @throws  IOException      Config file couldn't be loaded.
     */
    public QedeqConfig(final File configFile, final String description, final File basisDirectory)
            throws IOException {
        configAccess = new ConfigAccess(configFile, description);
        this.basisDirectory = basisDirectory;
    }

    /**
     * Store properties in config file.
     *
     * @throws  IOException
     */
    public final void store() throws IOException {
        configAccess.store();
    }

    /**
     * Get local file directory to save generated files in.
     *
     * @return  Generation directory.
     */
    public final String getGenerationDirectory() {
        final String location = getKeyValue("generationLocation");
        if (location == null) {
            return QedeqConfig.DEFAULT_GENERATED;
        }
        return location;
    }

    /**
     * Set local file directory for generated files.
     *
     * @param   location    generation directory.
     */
    public final void setGenerationDirectory(final String location) {
        setKeyValue("generationLocation", location);
    }

    /**
     * Get local file directory for module buffering.
     *
     * @return  Buffer directory.
     */
    public final String getBufferDirectory() {
        final String location = getKeyValue("bufferLocation");
        if (location == null) {
            return QedeqConfig.DEFAULT_LOCAL_BUFFER;
        }
        return location;
    }


    /**
     * Set local file directory for module buffering.
     * After changing this location the buffer should eventually be cleared.
     *
     * @param   location    buffer directory.
     */
    public final void setBufferDirectory(final String location) {
        setKeyValue("bufferLocation", location);
    }

    /**
     * Get local file location for log file.
     *
     * @return  Log file path.
     */
    public final String getLogFile() {
        final String location = getKeyValue("logLocation");
        if (location == null) {
            return QedeqConfig.DEFAULT_LOG_FILE;
        }
        return location;
    }

    /**
     * Get history of modules, which were tried to load.
     *
     * @return  list of modules.
     */
    public final String[] getModuleHistory() {
        return configAccess.getStringProperties("moduleHistory.");
    }

    /**
     * Save history of modules, which were tried to load.
     *
     * @param  modules  list of modules.
     */
    public final void saveModuleHistory(final List modules) {
        configAccess.removeProperties(("moduleHistory."));
        for (int i = 0; i < modules.size(); i++) {
            setKeyValue("moduleHistory." + (i + 101),
            modules.get(i).toString());
        }
    }

    /**
     * Get list of previously checked modules.
     *
     * @return  list of modules.
     */
    public final String[] getPreviouslyCheckedModules() {
        return configAccess.getStringProperties("checkedModule.");
    }

    /**
     * Set successfully list of successfully loaded QEDEQ modules.
     *
     * @param   moduleAddresses     This modules were successfully checked.
     */
    public final void setLoadedModules(final URL[] moduleAddresses) {
        configAccess.removeProperties("checkedModule.");
        for (int i = 0; i < moduleAddresses.length; i++) {
            setKeyValue("checkedModule." + (i + 1), moduleAddresses[i].toExternalForm());
        }
    }

    /**
     * Get basis directory of this application.
     *
     * @return  Basis directory of application for all variable data. Basis for all relative paths.
     */
    public final File getBasisDirectory() {
        return basisDirectory;
    }

    /**
     * Get file path starting from basis directory of this application.
     *
     * @param   path    Go to this path starting from basis directory.
     * @return  File path resolved against basis application directory.
     */
    public final File getRelativeToBasis(final String path) {
        return new File(getBasisDirectory(), path);
    }

    /**
     * Get auto reload of last session successfully loaded modules.
     *
     * @return auto reload enabled?
     */
    public boolean isAutoReloadLastSessionChecked() {
        return "true".equals(
            getKeyValue("sessionAutoReload", "true"));
    }


    /**
     * Set auto reload checked modules of last session mode.
     *
     * @param  mode     enable auto reload?
     */
    public final void setAutoReloadLastSessionChecked(final boolean mode) {
        setKeyValue("sessionAutoReload", (mode ? "true" : "false"));
    }


    /**
     * Should old html code be generated?
     *
     * @return  old html code?
     */
    public final boolean isOldHtml() {
        return "true".equals(getKeyValue("oldHtml", "true"));
    }

    /**
     * Set old html code generation flag.
     *
     * @param  mode     set old html code generation?
     */
    public final void setOldHtml(final boolean mode) {
        setKeyValue("oldHtml", (mode ? "true" : "false"));
    }


    /**
     * Get directory for newly created QEDEQ module files.
     *
     * @return  Directory for newly created QEDEQ modules.
     */
    public final String getLocalModulesDirectory() {
        final String location = getKeyValue("localModulesDirectory");
        if (location == null) {
            return QedeqConfig.DEFAULT_LOCAL_MODULES_DIRECTORY;
        }
        return location;
    }


    /**
     * Set directory for newly created module files.
     * After changing this location the buffer should eventually be cleared.
     *
     * @param   location    Buffer directory.
     */
    public final void setLocalModulesDirectory(final String location) {
        setKeyValue("localModulesDirectory", location);
    }

    /**
     * Get value for given key.
     *
     * @param   key     Get value for this key.
     * @return  Value, maybe <code>null</code>.
     */
    protected String getKeyValue(final String key) {
        return configAccess.getString(key);
    }

    /**
     * Get value for given key.
     *
     * @param   key             Get value for this key.
     * @param   defaultValue    Default value..
     * @return  Value. If value for key is originally <code>null</code> <code>defaultValue</code>
     *          is returned..
     */
    protected String getKeyValue(final String key, final String defaultValue) {
        return configAccess.getString(key, defaultValue);
    }

    /**
     * Set value for given key.
     *
     * @param   key     For this key.
     * @param   value   Set this value.
     */
    protected void setKeyValue(final String key, final String value) {
        configAccess.setString(key, value);
    }

}
