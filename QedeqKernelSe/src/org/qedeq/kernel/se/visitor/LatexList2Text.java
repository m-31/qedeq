package org.qedeq.kernel.se.visitor;

import org.qedeq.kernel.se.base.module.Latex;
import org.qedeq.kernel.se.base.module.LatexList;

/**
 * Transform latex list into text.
 *
 * TODO 20101221 m31: perhaps we should use the Latex2Utf8 converter?
 *
 * @author  Michael Meyling
 */
public class LatexList2Text {

    /**
     * Filters English entry out of LaTeX list.
     *
     * @param   list    List of LaTeX entries.
     * @return  Selected entry transformed into text.
     */
    public String transform(final LatexList list) {
        return transform(list, "en");
    }

    /**
     * Filters given language entry out of LaTeX list.
     * Fallback is the default language.
     *
     * @param   list        List of LaTeX entries.
     * @param   language    Filter for this language.
     * @return  Selected entry transformed into text.
     */
    public String transform(final LatexList list, final String language) {
        if (list == null) {
            return "";
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != null && language.equals(list.get(i).getLanguage())) {
                return getLatex(list.get(i));
            }
        }
        // assume entry with missing language as default
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != null && list.get(i).getLanguage() == null) {
                return getLatex(list.get(i));
            }
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != null) {
                return getLatex(list.get(i));
            }
        }
        return "";
    }

    protected String getLatex(final Latex latex) {
        String result = latex.getLatex();
        if (result == null) {
            result = "";
        }
        result = result.trim();
        result = result.replaceAll("\\\\index\\{.*\\}", "");
        return result.trim();
    }

}
