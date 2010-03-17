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

package org.qedeq.kernel.bo.service.latex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.io.TextOutput;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.base.module.LatexList;
import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.bo.context.KernelContext;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.PluginBo;
import org.qedeq.kernel.common.SourceFileExceptionList;


/**
 * Plugin to transfer a QEDEQ module into a LaTeX file.
 *
 * @author  Michael Meyling
 */
public final class Qedeq2LatexPlugin implements PluginBo {

    /** This class. */
    private static final Class CLASS = Qedeq2LatexPlugin.class;

    public Qedeq2LatexPlugin() {
    }

    public String getPluginDescription() {
        return "transforms QEDEQ module into LaTeX";
    }

    public String getPluginName() {
        return "to LaTeX";
    }

    public void executePlugin(final KernelQedeqBo qedeq) throws SourceFileExceptionList {
        final String method = "execute(QedeqBo)";
        try {
            QedeqLog.getInstance().logRequest("Generate LaTeX from \""
                + IoUtility.easyUrl(qedeq.getUrl()) + "\"");
            final String[] languages = getSupportedLanguages(qedeq);
            for (int j = 0; j < languages.length; j++) {
                final String result = generateLatex(
                        qedeq, languages[j],
                        "1").toString();
                if (languages[j] != null) {
                    QedeqLog.getInstance().logSuccessfulReply(
                        "LaTeX for language \"" + languages[j]
                        + "\" was generated from \""
                        + IoUtility.easyUrl(qedeq.getUrl()) + "\" into \"" + result + "\"");
                } else {
                    QedeqLog.getInstance().logSuccessfulReply(
                        "LaTeX for default language "
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
            Trace.fatal(CLASS, this, method, "unexpected problem",
                e);
            QedeqLog.getInstance().logFailureReply(
                "Generation failed", e.getMessage());
        }
    }

    /**
     * Gives a LaTeX representation of given QEDEQ module as InputStream.
     *
     * @param   prop            QEDEQ module.
     * @param   language        Filter text to get and produce text in this language only.
     * @param   level           Filter for this detail level. LATER mime 20050205: not supported
     *                          yet.
     * @return  Resulting LaTeX.
     * @throws  SourceFileExceptionList Major problem occurred.
     * @throws  IOException
     */
    public File generateLatex(final KernelQedeqBo prop, final String language,
            final String level) throws SourceFileExceptionList, IOException {

        // first we try to get more information about required modules and their predicates..
        try {
            KernelContext.getInstance().loadRequiredModules(prop.getModuleAddress());
            KernelContext.getInstance().checkModule(prop.getModuleAddress());
        } catch (Exception e) {
            // we continue and ignore external predicates
            Trace.trace(CLASS, "generateLatex(KernelQedeqBo, String, String)", e);
        }
        String tex = prop.getModuleAddress().getFileName();
        if (tex.toLowerCase(Locale.US).endsWith(".xml")) {
            tex = tex.substring(0, tex.length() - 4);
        }
        if (language != null && language.length() > 0) {
            tex = tex + "_" + language;
        }
        // the destination is the configured destination directory plus the (relative)
        // localized file (or path) name
        File destination = new File(KernelContext.getInstance().getConfig()
            .getGenerationDirectory(), tex + ".tex").getCanonicalFile();
        TextOutput printer = null;
        try {
            printer = new TextOutput(prop.getName(), new FileOutputStream(destination));
            final Qedeq2Latex converter = new Qedeq2Latex(this, prop, printer, language, level);
            converter.traverse();
            prop.addPluginErrors(this, converter.getErrorList());
            converter.getWarningList();
        } finally {
            if (printer != null) {
                printer.flush();
                printer.close();
            }
        }
        if (printer.checkError()) {
            throw printer.getError();
        }
        // TODO m31 20080520: just for testing purpose the following check is
        // integrated here after the LaTeX print. The checking results should be maintained
        // later on as additional information to a module. (Warnings...)
        QedeqBoDuplicateLanguageChecker.check(prop);
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

    public InputStream createLatex(final KernelQedeqBo prop, final String language, final String level)
            throws SourceFileExceptionList, IOException {
        return new FileInputStream(generateLatex(prop, language, level));
    }

}
