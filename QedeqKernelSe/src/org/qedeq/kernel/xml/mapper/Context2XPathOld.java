/* $Id: Context2XPathOld.java,v 1.2 2007/04/12 23:50:10 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2007,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.xml.mapper;

import java.util.StringTokenizer;

import org.qedeq.kernel.bo.module.ModuleContext;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.utility.ReplaceUtility;
import org.qedeq.kernel.xml.tracker.SimpleXPath;

/**
 * This class is outdated. Worked with XSD version 0.03.01. Can not find locations
 * within formulas or terms.
 *
 * <p>
 * Map content string to SimpleXPath string. This class makes it possible to transfer an location
 * of an {@link org.qedeq.kernel.base.module.Qedeq} object into an XPath like position description
 * for an XML file representation of that object.
 * <p>
 * This class has maps something like<br>
 * <code>
 * getChapterList().get(4).getSectionList().get(0).getSubsectionList().get(4).getLatex().get(0)
 * </code><br>
 * into<br>
 * <code>QEDEQ/CHAPTER[5]/SECTION/SUBSECTIONS/*[5]/TEXT/LATEX</code>
 *
 * mime 20070109: use visitor and Qedeq-Object to get XPath; this is necessary because
 * an Atom (if it is the first in an ElementList) is always an attribute tag - therefore the
 * XML element counting doesn't work (you have to subtract one if the first element is an
 * Atom)
 *
 * @version $Revision: 1.2 $
 * @author Michael Meyling
 * @deprecated
 */
public final class Context2XPathOld {

    /**
     * Constructor.
     */
    private Context2XPathOld() {
        // nothing to do
    }

    /**
     * Get file path out of context information.
     *
     * @param   context Context information.
     * @return  File path and name.
     * @deprecated
     */
    public static final String getFileName(final ModuleContext context) {
        return context.getModuleLocation();
    }

    /**
     * Get XPath out of context information.
     *
     * @param   context Context information.
     * @return  XPath.
     * @deprecated
     */
    public static final String getXPath(final ModuleContext context) {
        final String method = "getXPath(String)";
        String xpath = context.getLocationWithinModule();
        Trace.param(Context2XPathOld.class, method, "context", xpath);
        xpath = ReplaceUtility.replace(xpath, ".get(", "[");
        xpath = ReplaceUtility.replace(xpath, "()", "");
        xpath = ReplaceUtility.replace(xpath, ")", "]");
        xpath = ReplaceUtility.replace(xpath, ".get", "/");
        xpath = ReplaceUtility.replace(xpath, "get", "/Qedeq/");

        // mime 20050807: what if no Latex, Author, or other, exist? For regular files this is
        // ok, but if there is no element in the list?
        // mime 20050708: isn't a replacement only one element by one better?
        xpath = ReplaceUtility.replace(xpath, "Title[", "Title/Latex[");
        // mime 20050708: definition in XML file only formula
        xpath = ReplaceUtility.replace(xpath, "PredicateDefinition", "DEFINITION_PREDICATE");
        xpath = ReplaceUtility.replace(xpath, "FunctionDefinition", "DEFINITION_FUNCTION");
        xpath = ReplaceUtility.replace(xpath, "AuthorList[", "Authors/Author[");
        xpath = ReplaceUtility.replace(xpath, "ImportList[", "Imports/Import[");
        xpath = ReplaceUtility.replace(xpath, "LiteratureItemList[", "BIBLIOGRAPHY/ITEM[");
        xpath = ReplaceUtility.replace(xpath, "LiteratureItemList", "BIBLIOGRAPHY");
        xpath = ReplaceUtility.replace(xpath, "/Item[", "/Latex[");
        xpath = ReplaceUtility.replace(xpath, "/Item", "/Latex");
        xpath = ReplaceUtility.replace(xpath, "UsedByList[", "UsedBy/Specification[");
        xpath = ReplaceUtility.replace(xpath, "ChapterList[", "Chapter[");
        xpath = ReplaceUtility.replace(xpath, "AuthorList[", "Author[");
        xpath = ReplaceUtility.replace(xpath, "AuthorList", "Authors");
        xpath = ReplaceUtility.replace(xpath, "ImportList", "Imports");
        xpath = ReplaceUtility.replace(xpath, "LocationList", "Locations");
        xpath = ReplaceUtility.replace(xpath, "LinkList[", "Link[");
        xpath = ReplaceUtility.replace(xpath, "SectionList[", "Section[");
        xpath = ReplaceUtility.replace(xpath, "SubsectionList", "Subsections/*");
        xpath = ReplaceUtility.replace(xpath, "VariableList", "VARLIST/*");
        xpath = ReplaceUtility.replace(xpath, "ProofList[", "PROOF[");
        xpath = ReplaceUtility.replace(xpath, "ProofList", "PROOF");
        xpath = ReplaceUtility.replace(xpath, "/NodeType", "");
        xpath = ReplaceUtility.replace(xpath, "Summary", "Abstract/Latex");
        xpath = ReplaceUtility.replace(xpath, "Introduction", "Introduction/Latex");
        xpath = ReplaceUtility.replace(xpath, "PrecedingText", "PRECEDING/Latex");
        xpath = ReplaceUtility.replace(xpath, "SucceedingText", "SUCCEEDING/Latex");
        xpath = ReplaceUtility.replace(xpath, "Description[", "Description/Latex[");
        xpath = ReplaceUtility.replace(xpath, "Proposition", "Theorem");
        xpath = ReplaceUtility.replace(xpath, "Formula/Element/", "Formula/*/");
        xpath = ReplaceUtility.replace(xpath, "Element", "*");

        // TODO mime 20050807: integrate non formal proofs
        xpath = ReplaceUtility.replace(xpath, "/NonFormalProof[", "/Latex[");
        xpath = ReplaceUtility.replace(xpath, "/NonFormalProof", "/Latex");

        xpath = ReplaceUtility.replace(xpath, "/List", "");
        xpath = ReplaceUtility.replace(xpath, "List", "");
        xpath = ReplaceUtility.replace(xpath, "(", "[");
        xpath = xpath.toUpperCase();

        xpath = incrementNumbers(xpath);


        SimpleXPath sxp = new SimpleXPath(xpath);

        final String beforeLast = sxp.getBeforeLastElement();
        final String last = sxp.getLastElement();
        if ("EMAIL".equals(last)) {
            sxp.deleteLastElement();
            sxp.setAttribute("email");
        } else if ("LABEL".equals(last)) {
            sxp.deleteLastElement();
            sxp.setAttribute("label");
        } else if ("ID".equals(last)) {
            sxp.deleteLastElement();
            sxp.setAttribute("id");
        } else if ("SPECIFICATION".equals(beforeLast) && "NAME".equals(last)) {
            sxp.deleteLastElement();
            sxp.setAttribute("name");
        } else if ("SPECIFICATION".equals(beforeLast) && "RULEVERSION".equals(last)) {
            sxp.deleteLastElement();
            sxp.setAttribute("ruleVersion");
        } else if ("CHAPTER".equals(beforeLast) && "NONUMBER".equals(last)) {
            sxp.deleteLastElement();
            sxp.setAttribute("noNumber");
        } else if ("SECTION".equals(beforeLast) && "NONUMBER".equals(last)) {
            sxp.deleteLastElement();
            sxp.setAttribute("noNumber");
        } else if ("*".equals(beforeLast) && "LATEX".equals(last)) {
            sxp.deleteLastElement();
            sxp.addElement("TEXT");
            sxp.addElement("LATEX");
        } else if ("DEFINITION_PREDICATE".equals(beforeLast) && "ARGUMENTNUMBER".equals(last)) {
            sxp.deleteLastElement();
            sxp.setAttribute("arguments");
        } else if ("DEFINITION_PREDICATE".equals(beforeLast) && "NAME".equals(last)) {
            sxp.deleteLastElement();
            sxp.setAttribute("name");
        } else if ("DEFINITION_FUNCTION".equals(beforeLast) && "ARGUMENTNUMBER".equals(last)) {
            sxp.deleteLastElement();
            sxp.setAttribute("arguments");
        } else if ("DEFINITION_FUNCTION".equals(beforeLast) && "NAME".equals(last)) {
            sxp.deleteLastElement();
            sxp.setAttribute("name");
        } else if ("RULE".equals(beforeLast) && "NAME".equals(last)) {
            sxp.deleteLastElement();
            sxp.setAttribute("name");
        } else if ("*".equals(beforeLast) && "LEVEL".equals(last)) {
            sxp.deleteLastElement();
            sxp.setAttribute("level");
        } else if ("*".equals(beforeLast) && "NONUMBER".equals(last)) {
            sxp.deleteLastElement();
            sxp.setAttribute("noNumber");
        } else if ("*".equals(beforeLast) && "NAME".equals(last)) {
            final int len = sxp.getElementOccurrence(sxp.size() - 1);
            sxp.deleteLastElement();
            sxp.addElement("NAME");
            sxp.addElement("LATEX", len);
        }

        xpath = sxp.toString();
        Trace.param(Context2XPathOld.class, method, "xpath", xpath);
        return xpath;
    }

    /**
     * Increment all element occurrence numbers in "[]" by one.
     *
     * @param   xpath   Like "a[0]b[1]".
     * @return  Like "a[1]b[2]".
     */
    private static String incrementNumbers(final String xpath) {
        final StringTokenizer tokenizer = new StringTokenizer(xpath, "/", true);
        String newXpath = "";
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.indexOf('[') >= 0) {
                final StringTokenizer getnu = new StringTokenizer(token, "[]");
                newXpath += getnu.nextToken() + "[";
                newXpath += ((new Integer(getnu.nextToken())).intValue() + 1) + "]";
            } else {
                newXpath += token;
            }
        }
        return newXpath;
    }

}
