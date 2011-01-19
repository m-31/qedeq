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

package org.qedeq.kernel.bo.service.utf8;

import java.io.IOException;
import java.util.Map;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.io.StringOutput;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.PluginExecutor;
import org.qedeq.kernel.se.common.Plugin;
import org.qedeq.kernel.se.common.SourceFileExceptionList;


/**
 * Transfer a QEDEQ module into a UTF-8 text file.
 * <p>
 * <b>This is just a quick written generator. This class just generates some text output to be able
 * to get a visual impression of a QEDEQ module.</b>
 *
 * @author  Michael Meyling
 */
public final class Qedeq2Utf8TextExecutor implements PluginExecutor {

    /** This class. */
    private static final Class CLASS = Qedeq2Utf8TextExecutor.class;

    /** Output goes here. */
    private StringOutput printer;

    /** Filter text to get and produce text in this language. */
    private String language;

    /** Visitor for producing the text output. */
    private final Qedeq2Utf8Visitor visitor;

    /** Automatically line break after this column. <code>0</code> means no
     * automatic line breaking. */
   private int maxColumns;

    /**
     * Constructor.
     *
     * @param   plugin      This plugin we work for.
     * @param   prop        QEDEQ BO object.
     * @param   parameters  Plugin parameter.
     */
    Qedeq2Utf8TextExecutor(final Plugin plugin, final KernelQedeqBo prop, final Map parameters) {
        System.out.println("TextExecutor");
        language = "en";
        if (parameters != null) {
            language = (String) parameters.get("language");
            if (language == null) {
                language = "en";
            }
        }
        String infoString = null;
        String maxColumnsString = "0";
        if (parameters != null) {
            infoString = (String) parameters.get("info");
            if (infoString == null) {
                infoString = "false";
            }
            maxColumnsString = (String) parameters.get("maximumColumn");
            if (maxColumnsString == null || maxColumnsString.length() == 0) {
                maxColumnsString = "80";
            }
        }
        boolean info = "true".equalsIgnoreCase(infoString);
        maxColumns = 0;
        try {
            maxColumns = Integer.parseInt(maxColumnsString.trim());
        } catch (RuntimeException e) {
            // ignore
        }
        visitor = new Qedeq2Utf8Visitor(plugin, prop, info , maxColumns, false);
    }

    public Object executePlugin() {
        final String method = "executePlugin()";
        final String ref = "\"" + IoUtility.easyUrl(visitor.getQedeqBo().getUrl()) + "\"";
        String result = "";
        try {
            QedeqLog.getInstance().logRequest("Show UTF-8 text for " + ref);
            result = generateUtf8(language, "1");
            QedeqLog.getInstance().logSuccessfulReply(
                "UTF-8 was generated for " + ref + "\"");
        } catch (final SourceFileExceptionList e) {
            final String msg = "Generation failed for " + ref;
            Trace.fatal(CLASS, this, method, msg, e);
            QedeqLog.getInstance().logFailureReply(msg, e.getMessage());
        } catch (IOException e) {
            final String msg = "Generation failed for " + ref;
            Trace.fatal(CLASS, this, method, msg, e);
            QedeqLog.getInstance().logFailureReply(msg, e.getMessage());
        } catch (final RuntimeException e) {
            Trace.fatal(CLASS, this, method, "unexpected problem", e);
            QedeqLog.getInstance().logFailureReply(
                "Generation failed", "unexpected problem: "
                + (e.getMessage() != null ? e.getMessage() : e.toString()));
        }
        return result;
    }

    /**
     * Gives a UTF-8 representation of given QEDEQ module as InputStream.
     *
     * @param   language    Filter text to get and produce text in this language only.
     * @param   level       Filter for this detail level. LATER mime 20050205: not supported
     *                      yet.
     * @return  Name of generated file.
     * @throws  SourceFileExceptionList Major problem occurred.
     * @throws  IOException     File IO failed.
     */
    public String generateUtf8(final String language, final String level)
            throws SourceFileExceptionList, IOException {

        String lan = "en";
        if (language != null) {
            lan = language;
        }
        printer = new StringOutput();

        visitor.generateUtf8(printer, lan, level);
        return printer.toString();
    }

    public String getExecutionActionDescription() {
        return visitor.getExecutionActionDescription();
    }

    public double getExecutionPercentage() {
        return visitor.getExecutionPercentage();
    }

}
