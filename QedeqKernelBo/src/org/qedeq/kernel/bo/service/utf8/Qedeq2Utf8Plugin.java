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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.io.TextOutput;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.base.module.LatexList;
import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.bo.context.KernelContext;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.PluginBo;
import org.qedeq.kernel.bo.module.PluginExecutor;
import org.qedeq.kernel.common.SourceFileExceptionList;


/**
 * Plugin to transfer a QEDEQ module into a LaTeX file.
 *
 * @author  Michael Meyling
 */
public final class Qedeq2Utf8Plugin implements PluginBo {

    /** This class. */
    public static final Class CLASS = Qedeq2Utf8Plugin.class;

    /**
     * Constructor.
     */
    public Qedeq2Utf8Plugin() {
    }

    public String getPluginId() {
        return CLASS.getName();
    }

    public String getPluginName() {
        return "Create UTF-8";
    }

    public String getPluginDescription() {
        return "transforms QEDEQ module into UTF-8 file";
    }

    public Object executePlugin(final KernelQedeqBo qedeq, final Map parameters) {
        final String method = "executePlugin(QedeqBo, Map)";
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
        final boolean info = "true".equalsIgnoreCase(infoString);
        int maxColumns = 0;
        try {
            maxColumns = Integer.parseInt(maxColumnsString.trim());
        } catch (RuntimeException e) {
            // ignore
        }
        try {
            QedeqLog.getInstance().logRequest("Generate UTF-8 from \""
                + IoUtility.easyUrl(qedeq.getUrl()) + "\"");
            final String[] languages = getSupportedLanguages(qedeq);
            for (int j = 0; j < languages.length; j++) {
                final String result = generateUtf8(qedeq, languages[j], "1", info,
                    maxColumns).toString();
                if (languages[j] != null) {
                    QedeqLog.getInstance().logSuccessfulReply(
                        "UTF-8 for language \"" + languages[j]
                        + "\" was generated from \""
                        + IoUtility.easyUrl(qedeq.getUrl()) + "\" into \"" + result + "\"");
                } else {
                    QedeqLog.getInstance().logSuccessfulReply(
                        "UTF-8 for default language "
                        + "was generated from \""
                        + IoUtility.easyUrl(qedeq.getUrl()) + "\" into \"" + result + "\"");
                }
            }
        } catch (final SourceFileExceptionList e) {
            final String msg = "Generation failed for \""
                + IoUtility.easyUrl(qedeq.getUrl()) + "\"";
            Trace.fatal(CLASS, this, method, msg, e);
            QedeqLog.getInstance().logFailureReply(msg, e.getMessage());
        } catch (IOException e) {
            final String msg = "Generation failed for \""
                + IoUtility.easyUrl(qedeq.getUrl()) + "\"";
            Trace.fatal(CLASS, this, method, msg, e);
            QedeqLog.getInstance().logFailureReply(msg, e.getMessage());
        } catch (final RuntimeException e) {
            Trace.fatal(CLASS, this, method, "unexpected problem", e);
            QedeqLog.getInstance().logFailureReply(
                "Generation failed", "unexpected problem: "
                + (e.getMessage() != null ? e.getMessage() : e.toString()));
        }
        return null;
    }

    /**
     * Gives a LaTeX representation of given QEDEQ module as InputStream.
     *
     * @param   prop        QEDEQ module.
     * @param   language    Filter text to get and produce text in this language only.
     * @param   level       Filter for this detail level. LATER mime 20050205: not supported
     *                      yet.
     * @param   info        Put additional informations into LaTeX document. E.g. QEDEQ reference
     *                      names. That makes it easier to write new documents, because one can
     *                      read the QEDEQ reference names in the written document.
     * @param   maxColumns  Maximum column number. If zero no line breaking is done automatically.
     * @return  Resulting LaTeX.
     * @throws  SourceFileExceptionList Major problem occurred.
     * @throws  IOException     File IO failed.
     */
    public File generateUtf8(final KernelQedeqBo prop, final String language,
            final String level, final boolean info, final int maxColumns)
            throws SourceFileExceptionList, IOException {

        // first we try to get more information about required modules and their predicates..
        try {
            KernelContext.getInstance().loadRequiredModules(prop.getModuleAddress());
            KernelContext.getInstance().checkModule(prop.getModuleAddress());
        } catch (Exception e) {
            // we continue and ignore external predicates
            Trace.trace(CLASS, "generateUtf8(KernelQedeqBo, String, String)", e);
        }
        String txt = prop.getModuleAddress().getFileName();
        if (txt.toLowerCase(Locale.US).endsWith(".xml")) {
            txt = txt.substring(0, txt.length() - 4);
        }
        if (language != null && language.length() > 0) {
            txt = txt + "_" + language;
        }
        // the destination is the configured destination directory plus the (relative)
        // localized file (or path) name
        File destination = new File(KernelContext.getInstance().getConfig()
            .getGenerationDirectory(), txt + ".txt").getCanonicalFile();
        TextOutput printer = null;
        try {
            printer = new TextOutput(prop.getName(), new FileOutputStream(destination), "UTF-8");
            final Qedeq2Utf8 converter = new Qedeq2Utf8(this, prop, printer, language, level, info,
                maxColumns);
            converter.traverse();
            prop.addPluginErrorsAndWarnings(this, converter.getErrorList(), converter.getWarningList());
        } finally {
            if (printer != null) {
                printer.flush();
                printer.close();
            }
        }
        if (printer.checkError()) {
            throw printer.getError();
        }
        return destination.getCanonicalFile();
    }

    // TODO m31 20070704: this should be part of QedeqBo
    String[] getSupportedLanguages(final QedeqBo prop) {
        // TODO m31 20070704: there should be a better way to
        // get all supported languages. Time for a new visitor?
        if (!prop.isLoaded()) {
            return new String[]{};
        }
        final LatexList list = prop.getQedeq().getHeader().getTitle();
        final String[] result = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i).getLanguage();
        }
        return result;
    }

    public PluginExecutor createExecutor(KernelQedeqBo qedeq, Map parameters) {
        // TODO Auto-generated method stub
        return null;
    }

}
