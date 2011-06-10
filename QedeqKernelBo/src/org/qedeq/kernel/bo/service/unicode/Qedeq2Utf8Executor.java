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

package org.qedeq.kernel.bo.service.unicode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import org.qedeq.base.io.Parameters;
import org.qedeq.base.io.TextOutput;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.bo.common.PluginExecutor;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
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
public class Qedeq2Utf8Executor implements PluginExecutor {

    /** This class. */
    private static final Class CLASS = Qedeq2Utf8Executor.class;

    /** Output goes here. */
    private TextOutput printer;

    /** Current destination file. */
    private File destination;

    /** Visit all nodes with this visitor. */
    private final Qedeq2UnicodeVisitor visitor;

    /** Break automatically before this column number. */
    private int maxColumns;

    /** Generate text for these languages. */
    private String[] languages;

    /** Current language selection. See {@link #languages}. */
    private int run = 0;

    /**
     * Constructor.
     *
     * @param   plugin      This plugin we work for.
     * @param   prop        QEDEQ BO object.
     * @param   parameters  Plugin parameter.
     */
    public Qedeq2Utf8Executor(final Plugin plugin, final KernelQedeqBo prop, final Parameters parameters) {
        final boolean info = parameters.getBoolean("info");
        // automatically line break after this column. 0 means no automatic line breaking
        maxColumns = Math.max(10, parameters.getInt("maximumColumn"));
        visitor = new Qedeq2UnicodeVisitor(plugin, prop, info , maxColumns, true);
    }

    public Object executePlugin() {
        final String method = "executePlugin()";
        try {
            QedeqLog.getInstance().logRequest("Generate UTF-8", visitor.getQedeqBo().getUrl());
            languages = visitor.getQedeqBo().getSupportedLanguages();
            for (run = 0; run < languages.length; run++) {
                final String result = generateUtf8(languages[run], "1");
                if (languages[run] != null) {
                    QedeqLog.getInstance().logSuccessfulReply(
                        "UTF-8 for language \"" + languages[run]
                        + "\" was generated into \"" + result + "\"", visitor.getQedeqBo().getUrl());
                } else {
                    QedeqLog.getInstance().logSuccessfulReply(
                        "UTF-8 for default language "
                        + "was generated into \"" + result + "\"", visitor.getQedeqBo().getUrl());
                }
            }
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
        return null;
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

        // first we try to get more information about required modules and their predicates..
        try {
            visitor.getQedeqBo().getKernelServices().loadRequiredModules(
                visitor.getQedeqBo().getModuleAddress());
            visitor.getQedeqBo().getKernelServices().checkModule(
                visitor.getQedeqBo().getModuleAddress());
        } catch (Exception e) {
            // we continue and ignore external predicates
            Trace.trace(CLASS, "generateUtf8(KernelQedeqBo, String, String)", e);
        }
        String lan = "en";
        if (language != null) {
            lan = language;
        }
//        if (level == null) {
//            this.level = "1";
//        } else {
//            this.level = level;
//        }
        String txt = visitor.getQedeqBo().getModuleAddress().getFileName();
        if (txt.toLowerCase(Locale.US).endsWith(".xml")) {
            txt = txt.substring(0, txt.length() - 4);
        }
        if (lan != null && lan.length() > 0) {
            txt = txt + "_" + lan;
        }
        destination = new File(KernelContext.getInstance().getConfig()
            .getGenerationDirectory(), txt + ".txt").getCanonicalFile();
        printer = new TextOutput(visitor.getQedeqBo().getName(), new FileOutputStream(destination),
            "UTF-8");

        try {
            visitor.generateUtf8(printer, lan, level);
        } finally {
            if (printer != null) {
                printer.flush();
                printer.close();
            }
        }
        if (printer != null && printer.checkError()) {
            throw printer.getError();
        }
        return destination.toString();
    }

    public String getLocationDescription() {
        if (languages != null && run < languages.length) {
            return languages[run] + " " + visitor.getLocationDescription();
        }
        if (languages != null && languages.length > 0) {
            return languages[languages.length] + " " + visitor.getLocationDescription();
        }
        return "unknown";
    }

    public double getExecutionPercentage() {
        return visitor.getExecutionPercentage() / languages.length * (run + 1);
    }

}
