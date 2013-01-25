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

package org.qedeq.kernel.se.config;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.io.Parameters;
import org.qedeq.base.io.Path;
import org.qedeq.kernel.se.common.Plugin;


/**
 * This class gives a type save access to properties of the application.
 *
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
     *                           new relative paths
     * @throws  IOException      Config file couldn't be loaded.
     */
    public QedeqConfig(final File configFile, final String description, final File basisDirectory)
            throws IOException {
        configAccess = new ConfigAccess(configFile, description);
        this.basisDirectory = basisDirectory.getCanonicalFile();
    }

    /**
     * Store properties in config file.
     *
     * @throws  IOException Writing failed.
     */
    public final void store() throws IOException {
        configAccess.store();
    }

    /**
     * Get local file directory to save generated files in.
     *
     * @return  Generation directory.
     */
    public final File getGenerationDirectory() {
        String location = getKeyValue("generationLocation");
        if (location == null) {
            location = QedeqConfig.DEFAULT_GENERATED;
        }
        return createAbsolutePath(location);
    }

    /**
     * Set local file directory for generated files.
     *
     * @param   location    generation directory.
     */
    public final void setGenerationDirectory(final File location) {
        final String relative = createRelativePath(location);
        setKeyValue("generationLocation", relative);
    }

    /**
     * Get local file directory for module buffering.
     *
     * @return  Buffer directory.
     */
    public final File getBufferDirectory() {
        String location = getKeyValue("bufferLocation");
        if (location == null) {
            location = QedeqConfig.DEFAULT_LOCAL_BUFFER;
        }
        return createAbsolutePath(location);
    }


    /**
     * Set local file directory for module buffering.
     * After changing this location the buffer should eventually be cleared.
     *
     * @param   location    buffer directory.
     */
    public final void setBufferDirectory(final File location) {
        final String relative = createRelativePath(location);
        setKeyValue("bufferLocation", relative);
    }

    /**
     * Get directory for newly created QEDEQ module files.
     *
     * @return  Directory for newly created QEDEQ modules.
     */
    public final File getLocalModulesDirectory() {
        String location = getKeyValue("localModulesDirectory");
        if (location == null) {
            location = QedeqConfig.DEFAULT_LOCAL_MODULES_DIRECTORY;
        }
        return createAbsolutePath(location);
    }


    /**
     * Set directory for newly created module files.
     * After changing this location the buffer should eventually be cleared.
     *
     * @param   location    Buffer directory.
     */
    public final void setLocalModulesDirectory(final File location) {
        final String relative = createRelativePath(location);
        setKeyValue("localModulesDirectory", relative);
    }

    /**
     * Get relative file location for log file.
     *
     * @return  Log file path relative to basis directory.
     */
    private final String getLogFileString() {
        final String location = getKeyValue("logLocation");
        if (location == null) {
            return QedeqConfig.DEFAULT_LOG_FILE;
        }
        return location;
    }

    /**
     * Get file location for log file.
     *
     * @return  Log file path.
     */
    public final File getLogFile() {
        return new File(getBasisDirectory(), getLogFileString());
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
    public final void setLoadedModules(final String[] moduleAddresses) {
        configAccess.removeProperties("checkedModule.");
        for (int i = 0; i < moduleAddresses.length; i++) {
            setKeyValue("checkedModule." + (i + 1), moduleAddresses[i]);
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
     * @return  File path resolved against basis application directory as an absolute path.
     */
    public final File createAbsolutePath(final String path) {
        File result = new File(path);
        final Path ptest = new Path(path.replace(File.separatorChar, '/'), "");
        if (ptest.isAbsolute()) {
            try {
                return result.getCanonicalFile();
            } catch (Exception e) {
                // we don't know if we can log something already
                e.printStackTrace(System.out);
                System.out.println("we try to continue with file " + result);
                return result;
            }
        }
        result = new File(getBasisDirectory(), path);
        try {
            result = result.getCanonicalFile();
        } catch (IOException e) {
            // we don't know if we can log something already
            e.printStackTrace(System.out);
        }
        return result;
    }

    /**
     * Create relative file path starting from basis directory of this application.
     *
     * @param   path    Reach this path starting from basis directory.
     * @return  File path relative to basis application directory.
     */
    private final String createRelativePath(final File path) {
        return IoUtility.createRelativePath(getBasisDirectory(), path);
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
     * Is tracing on? If not, only business and fatal messages are logged.
     * Otherwise all events are logged according to the log level settings.
     *
     * @return  Is tracing on?
     */
    public final boolean isTraceOn() {
        return "true".equals(getKeyValue("traceOn", "false"));
    }

    /**
     * Set tracing on.
     *
     * @param  traceOn     Set trace on.
     */
    public final void setTraceOn(final boolean traceOn) {
        setKeyValue("traceOn", (traceOn ? "true" : "false"));
    }

    /**
     * Get connection timeout, especially for TCP/IP connections.
     *
     * @return  Connection timeout (in milliseconds).
     */
    public int getConnectTimeout() {
        return getKeyValue("connectionTimeout", 2000);
    }

    /**
     * Set connection timeout, especially for TCP/IP connections.
     *
     * @param   timeout Connection timeout, especially for TCP/IP connections. In milliseconds.
     */
    public final void setConnectionTimeout(final int timeout) {
        setKeyValue("connectionTimeout", timeout);
    }

    /**
     * Get read timeout, especially for TCP/IP connections.
     *
     * @return  Read timeout (in milliseconds).
     */
    public int getReadTimeout() {
        return getKeyValue("readTimeout", 1000);
    }

    /**
     * Set read timeout, especially for TCP/IP connections.
     *
     * @param   timeout Read timeout, especially for TCP/IP connections. In milliseconds.
     */
    public final void setReadTimeout(final int timeout) {
        setKeyValue("readTimeout", timeout);
    }

    /**
     * Set http proxy host.
     *
     * @param  httpProxyHost    Http proxy server.
     */
    public final void setHttpProxyHost(final String httpProxyHost) {
        setKeyValue("http.proxyHost", httpProxyHost);
    }

    /**
     * Get http proxy host. It might be a good idea to ignore this value, if the application
     * was started via Java Webstart.
     *
     * @return  Http proxy host.
     */
    public final String getHttpProxyHost() {
        final String def = System.getProperty("http.proxyHost");
        if (def != null) {
            return getKeyValue("http.proxyHost", def);
        }
        return getKeyValue("http.proxyHost");
    }

    /**
     * Set http proxy port.
     *
     * @param  httpProxyPort    Http proxy port.
     */
    public final void setHttpProxyPort(final String httpProxyPort) {
        setKeyValue("http.proxyPort", httpProxyPort);
    }

    /**
     * Get http proxy port. It might be a good idea to ignore this value, if the application
     * was started via Java Webstart.
     *
     * @return  Http proxy port.
     */
    public final String getHttpProxyPort() {
        final String def = System.getProperty("http.proxyPort");
        if (def != null) {
            return getKeyValue("http.proxyPort", def);
        }
        return getKeyValue("http.proxyPort");
    }

    /**
     * Set http non proxy hosts.
     *
     * @param  httpNonProxyHosts    Http non proxy hosts.
     */
    public final void setHttpNonProxyHosts(final String httpNonProxyHosts) {
        setKeyValue("http.nonProxyHosts", httpNonProxyHosts);
    }

    /**
     * Get non http proxy hosts. It might be a good idea to ignore this value, if the application
     * was started via Java Webstart.
     *
     * @return  Http non proxy hosts.
     */
    public final String getHttpNonProxyHosts() {
        final String def = System.getProperty("http.nonProxyHosts");
        if (def != null) {
            return getKeyValue("http.nonProxyHosts", def);
        }
        return getKeyValue("http.nonProxyHosts");
    }

    /**
     * Get value for given key.
     *
     * @param   key     Get value for this key.
     * @return  Value, maybe <code>null</code>.
     */
    protected synchronized String getKeyValue(final String key) {
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
    protected synchronized String getKeyValue(final String key, final String defaultValue) {
        return configAccess.getString(key, defaultValue);
    }

    /**
     * Set value for given key.
     *
     * @param   key     For this key.
     * @param   value   Set this value.
     */
    protected synchronized void setKeyValue(final String key, final String value) {
        configAccess.setString(key, value);
    }

    /**
     * Get value for given key.
     *
     * @param   key             Get value for this key.
     * @param   defaultValue    Default value..
     * @return  Value. If value for key is originally <code>null</code> <code>defaultValue</code>
     *          is returned..
     */
    protected synchronized int getKeyValue(final String key, final int defaultValue) {
        return configAccess.getInteger(key, defaultValue);
    }

    /**
     * Set value for given key.
     *
     * @param   key     For this key.
     * @param   value   Set this value.
     */
    protected synchronized void setKeyValue(final String key, final int value) {
        configAccess.setInteger(key, value);
    }

    /**
     * Get value for given key.
     *
     * @param   key             Get value for this key.
     * @param   defaultValue    Default value..
     * @return  Value. If value for key is originally <code>null</code> <code>defaultValue</code>
     *          is returned.
     */
    protected synchronized boolean getKeyValue(final String key, final boolean defaultValue) {
        return "true".equals(getKeyValue(key, (defaultValue ? "true" : "false")));
    }

    /**
     * Set value for given key.
     *
     * @param   key     For this key.
     * @param   value   Set this value.
     */
    protected void setKeyValue(final String key, final boolean value) {
        setKeyValue(key, (value ? "true" : "false"));
    }

    /**
     * Get plugin properties from configuration file.
     *
     * @param   plugin  We want to know properties for this plugin
     * @return  Map with properties for this plugin.
     */
    public Parameters getPluginEntries(final Plugin plugin) {
        return new Parameters(configAccess.getProperties(plugin.getPluginId() + "$"));
    }

    /**
     * Get value for given plugin key.
     *
     * @param   plugin  Setting for this plugin.
     * @param   key             Get value for this key.
     * @param   defaultValue    Default value..
     * @return  Value. If value for key is originally <code>null</code> <code>defaultValue</code>
     *          is returned.
     */
    public String getPluginKeyValue(final Plugin plugin, final String key, final String defaultValue) {
        return getKeyValue(plugin.getPluginId() + "$" + key, defaultValue);
    }

    /**
     * Set value for given plugin key.
     *
     * @param   plugin  Setting for this plugin.
     * @param   key     For this key.
     * @param   value   Set this value.
     */
    public void setPluginKeyValue(final Plugin plugin, final String key, final String value) {
        setKeyValue(plugin.getPluginId() + "$" + key, value);
    }

    /**
     * Set value for given plugin key.
     *
     * @param   plugin      Setting for this plugin.
     * @param   parameters  Parameters for this plugin.
     */
    public void setPluginKeyValues(final Plugin plugin, final Parameters parameters) {
        final Iterator it = parameters.keySet().iterator();
        while (it.hasNext()) {
            final String key = (String) it.next();
            setKeyValue(plugin.getPluginId() + "$" + key, parameters.getString(key));
        }
    }

    /**
     * Get value for given plugin key.
     *
     * @param   plugin  Setting for this plugin.
     * @param   key             Get value for this key.
     * @param   defaultValue    Default value..
     * @return  Value. If value for key is originally <code>null</code> <code>defaultValue</code>
     *          is returned.
     */
    public int getPluginKeyValue(final Plugin plugin, final String key, final int defaultValue) {
        return getKeyValue(plugin.getPluginId() + "$" + key, defaultValue);
    }

    /**
     * Set value for given plugin key.
     *
     * @param   plugin  Setting for this plugin.
     * @param   key     For this key.
     * @param   value   Set this value.
     */
    public void setPluginKeyValue(final Plugin plugin, final String key, final int value) {
        setKeyValue(plugin.getPluginId() + "$" + key, value);
    }

    /**
     * Get value for given plugin key.
     *
     * @param   plugin  Setting for this plugin.
     * @param   key             Get value for this key.
     * @param   defaultValue    Default value..
     * @return  Value. If value for key is originally <code>null</code> <code>defaultValue</code>
     *          is returned.
     */
    public boolean getPluginKeyValue(final Plugin plugin, final String key, final boolean defaultValue) {
        return getKeyValue(plugin.getPluginId() + "$" + key, defaultValue);
    }

    /**
     * Set value for given plugin key.
     *
     * @param   plugin  Setting for this plugin.
     * @param   key     For this key.
     * @param   value   Set this value.
     */
    public void setPluginKeyValue(final Plugin plugin, final String key, final boolean value) {
        setKeyValue(plugin.getPluginId() + "$" + key, value);
    }

}
