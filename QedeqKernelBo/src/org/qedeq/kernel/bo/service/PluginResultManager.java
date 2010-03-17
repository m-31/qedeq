/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2010,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.qedeq.kernel.bo.module.PluginBo;
import org.qedeq.kernel.bo.module.PluginResults;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.SourceFileExceptionList;

/**
 * Holds all known QEDEQ modules.
 */
public class PluginResultManager {

    /** This class. */
    private static final Class CLASS = PluginResultManager.class;

    /** Maps plugins to results. */
    private final Map plugins = new HashMap();


    /**
     * Get all registered plugins.
     *
     * @return  Registered plugins.
     */
    synchronized PluginBo[] getPlugins() {
        return (PluginBo[]) plugins.keySet().toArray(new PluginBo[] {});
    }

    /**
     * Set a plugin results.
     *
     * @param   Set plugin execution results.
     */
    public synchronized void setResult(final PluginBo plugin, final SourceFileExceptionList errors,
            final SourceFileExceptionList warnings) {
        PluginResults results = (PluginResults) plugins.get(plugin);
        if (results == null) {
            results = new PluginResults();
            plugins.put(plugin, results);
        }
        results.clear();
        results.addErrors(errors);
        results.addWarnings(errors);
    }

    /**
     * Add a plugin results.
     *
     * @param   Set plugin execution results.
     */
    public synchronized void addResult(final PluginBo plugin, final SourceFileExceptionList errors,
            final SourceFileExceptionList warnings) {
        PluginResults results = (PluginResults) plugins.get(plugin);
        if (results == null) {
            results = new PluginResults();
            plugins.put(plugin, results);
        }
        results.addErrors(errors);
        results.addWarnings(errors);
    }

    public SourceFileExceptionList getAllErrors() {
        final DefaultSourceFileExceptionList errors = new DefaultSourceFileExceptionList();
        Iterator iterator = plugins.keySet().iterator();
        while (iterator.hasNext()) {
            errors.add(((PluginResults) plugins.get(iterator.next())).getErrors());
        }
        return errors;
    }

    public SourceFileExceptionList getAllWarnings() {
        final DefaultSourceFileExceptionList warnings = new DefaultSourceFileExceptionList();
        Iterator iterator = plugins.keySet().iterator();
        while (iterator.hasNext()) {
            warnings.add(((PluginResults) plugins.get(iterator.next())).getWarnings());
        }
        return warnings;
    }
}

