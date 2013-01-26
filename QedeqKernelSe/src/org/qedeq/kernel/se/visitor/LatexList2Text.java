package org.qedeq.kernel.se.visitor;

import org.qedeq.kernel.se.base.module.Latex;
import org.qedeq.kernel.se.base.module.LatexList;

/**
 * Transform latex list into text. We make here only a basic conversion to have a plain text
 * description of such things as chapter titles. So we have to remove something like
 * "\index".
 * <br/>
 * TODO 20130126 m31: this transformation is mainly used to get a good location description
 * when a plugin is running. So it must work with chapter, section and subsection titles.
 * We just have to check what LaTeX stuff is used there.
 * <br/>
 * Another problem: currently only the method {@link #transform(LatexList)} is called.
 * <br/>
 * It might be a good idea to idea to externalize the usage. So one could use a better transformer
 * transformer (like Latex2UnicodeParser) if we have it in our class path... 
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
        // if we got no language we take "en"
        String lan = (language != null ? language : "en");
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != null && lan.equals(list.get(i).getLanguage())) {
                return getLatex(list.get(i));
            }
        }
        // assume entry with missing language as default
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != null && list.get(i).getLanguage() == null) {
                return getLatex(list.get(i));
            }
        }
        // if we haven't tried "en" yet we give it a try
        if (!"en".equals(lan)) {
            lan = "en";
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) != null && lan.equals(list.get(i).getLanguage())) {
                    return getLatex(list.get(i));
                }
            }
        }
        // fallback: now we take the first non empty entry
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != null) {
                return getLatex(list.get(i));
            }
        }
        // nothing found, so we return just an empty string
        return "";
    }

    protected String getLatex(final Latex latex) {
        if (latex == null) {
            return "";
        }
        String result = latex.getLatex();
        if (result == null) {
            result = "";
        }
        result = result.trim();
        result = result.replaceAll("\\\\index\\{.*\\}", "");
        result = result.replaceAll("\\\\(\\w*)\\{(.*)\\}", "$2");
        result = result.replace("{", "");
        result = result.replace("}", "");
        return result.trim();
    }

}
