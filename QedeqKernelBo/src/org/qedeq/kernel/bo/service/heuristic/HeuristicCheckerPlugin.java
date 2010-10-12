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

package org.qedeq.kernel.bo.service.heuristic;

import java.util.Map;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.logic.model.DefaultModel;
import org.qedeq.kernel.bo.logic.model.Model;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.PluginBo;
import org.qedeq.kernel.common.SourceFileExceptionList;


/**
 * Plugin to transfer a QEDEQ module into a LaTeX file.
 *
 * @author  Michael Meyling
 */
public class HeuristicCheckerPlugin implements PluginBo {

    /** This class. */
    public static final Class CLASS = HeuristicCheckerPlugin.class;

    /**
     * Constructor.
     */
    public HeuristicCheckerPlugin() {
        // nothing to do
    }

    public String getPluginId() {
        return CLASS.getName();
    }

    public String getPluginName() {
        return "Heuristic tester";
    }

    public String getPluginDescription() {
        return "checks mathematical correctness by interpreting within a model";
    }

    public Object executePlugin(final KernelQedeqBo qedeq, final Map parameters) {
        final String method = "executePlugin(QedeqBo, Map)";
        try {
            QedeqLog.getInstance().logRequest("Heuristic test for \""
                + IoUtility.easyUrl(qedeq.getUrl()) + "\"");
            final String modelClass
                = (parameters != null ? (String) parameters.get("model") : null);
            Model model = null;
            if (modelClass != null && modelClass.length() > 0) {
                try {
                    Class cl = Class.forName(modelClass);
                    model = (Model) cl.newInstance();
                } catch (ClassNotFoundException e) {
                    Trace.fatal(CLASS, this, method, "Model class not in class path: "
                        + modelClass, e);
                    throw new RuntimeException(e);
                } catch (InstantiationException e) {
                    Trace.fatal(CLASS, this, method, "Model class could not be instanciated: "
                        + modelClass, e);
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    Trace.fatal(CLASS, this, method,
                        "Programming error, access for instantiation failed for model: "
                        + modelClass, e);
                    throw new RuntimeException(e);
                } catch (RuntimeException e) {
                    Trace.fatal(CLASS, this, method,
                        "Programming error, instantiation failed for model: " + modelClass, e);
                    throw new RuntimeException(e);
                }
            }
            // fallback is the default model
            if (model == null) {
                model = new DefaultModel();
            }
            QedeqHeuristicChecker.check(this, model, qedeq);
            QedeqLog.getInstance().logSuccessfulReply(
                "Heuristic test succesfull for \""
                + IoUtility.easyUrl(qedeq.getUrl()) + "\"");
        } catch (final SourceFileExceptionList e) {
            final String msg = "Test failed for \""
                + IoUtility.easyUrl(qedeq.getUrl()) + "\"";
            Trace.fatal(CLASS, this, method, msg, e);
            QedeqLog.getInstance().logFailureReply(msg, e.getMessage());
        } catch (final RuntimeException e) {
            Trace.fatal(CLASS, this, method, "unexpected problem", e);
            QedeqLog.getInstance().logFailureReply(
                "Test failed", "unexpected problem: "
                + (e.getMessage() != null ? e.getMessage() : e.toString()));
        }
        return null;
    }

}
