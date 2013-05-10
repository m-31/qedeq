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

package org.qedeq.kernel.bo.service.unicode;

import java.io.IOException;

import org.qedeq.base.io.Parameters;
import org.qedeq.base.io.StringOutput;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.module.InternalServiceProcess;
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
public final class Qedeq2UnicodeTextExecutor implements PluginExecutor {

    /** This class. */
    private static final Class CLASS = Qedeq2UnicodeTextExecutor.class;

    /** Output goes here. */
    private StringOutput printer;

    /** Filter text to get and produce text in this language. */
    private String language;

    /** Visitor for producing the text output. */
    private final Qedeq2UnicodeVisitor visitor;


    /**
     * Constructor.
     *
     * @param   plugin      This plugin we work for.
     * @param   prop        QEDEQ BO object.
     * @param   parameters  Plugin parameter.
     */
    Qedeq2UnicodeTextExecutor(final Plugin plugin, final KernelQedeqBo prop,
            final Parameters parameters) {
        language = parameters.getString("language");
        final boolean info = parameters.getBoolean("info");
        // automatically line break after this column. 0 means no automatic line breaking
        int maxColumns = parameters.getInt("maximumColumn");
        maxColumns = Math.max(10, maxColumns);
        visitor = new Qedeq2UnicodeVisitor(plugin, prop, info , maxColumns, false, false);
    }

    public Object executePlugin(final InternalServiceProcess process, final Object data) {
        final String method = "executePlugin()";
        String result = "";
        try {
            QedeqLog.getInstance().logRequest("Show UTF-8 text", visitor.getQedeqBo().getUrl());
            result = generateUtf8(process, language, "1");
            QedeqLog.getInstance().logSuccessfulReply(
                "UTF-8 text was shown", visitor.getQedeqBo().getUrl());
        } catch (final SourceFileExceptionList e) {
            final String msg = "Generation failed";
            Trace.fatal(CLASS, this, method, msg, e);
            QedeqLog.getInstance().logFailureReply(msg, visitor.getQedeqBo().getUrl(), e.getMessage());
        } catch (IOException e) {
            final String msg = "Generation failed";
            Trace.fatal(CLASS, this, method, msg, e);
            QedeqLog.getInstance().logFailureReply(msg, visitor.getQedeqBo().getUrl(), e.getMessage());
        } catch (final RuntimeException e) {
            Trace.fatal(CLASS, this, method, "unexpected problem", e);
            QedeqLog.getInstance().logFailureReply(
                "Generation failed", visitor.getQedeqBo().getUrl(), "unexpected problem: "
                + (e.getMessage() != null ? e.getMessage() : e.toString()));
        }
        return result;
    }

    /**
     * Gives a UTF-8 representation of given QEDEQ module as InputStream.
     *
     * @param   process     This process executes us.
     * @param   language    Filter text to get and produce text in this language only.
     * @param   level       Filter for this detail level. LATER mime 20050205: not supported
     *                      yet.
     * @return  Name of generated file.
     * @throws  SourceFileExceptionList Major problem occurred.
     * @throws  IOException     File IO failed.
     */
    public String generateUtf8(final InternalServiceProcess process, final String language,
            final String level) throws SourceFileExceptionList, IOException {

        String lan = "en";
        if (language != null) {
            lan = language;
        }
        printer = new StringOutput();

        visitor.generateUtf8(process, printer, lan, level);
        return printer.toString();
    }

    public String getActionDescription() {
        return visitor.getActionDescription();
    }

    public double getExecutionPercentage() {
        return visitor.getExecutionPercentage();
    }

    public boolean getInterrupted() {
        return visitor.getInterrupted();
    }

}
