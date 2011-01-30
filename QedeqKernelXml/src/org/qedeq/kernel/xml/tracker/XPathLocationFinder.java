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
package org.qedeq.kernel.xml.tracker;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;

import org.qedeq.base.io.SourceArea;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.bo.KernelContext;
import org.xml.sax.SAXException;


/**
 * Find position of simple XPath expressions within an XML file.
 *
 * @author  Michael Meyling
 */
public final class XPathLocationFinder {

    /**
     * Constructor.
     */
    private XPathLocationFinder() {
        // nothing to do
    }

    /**
     * Main method.
     *
     * @param   args    Various parameters. See implementation of
     *                  {@link #printProgramInformation()}.
     * @throws  ParserConfigurationException    Severe parser configuration problem.
     * @throws  SAXException                    XML problem.
     * @throws  IOException                     IO problem.
     */
    public static final void main(final String[] args) throws ParserConfigurationException,
            SAXException, IOException {
        String from = null;
        String xpath = null;

        if (args.length == 0) {
            printProgramInformation();
            return;
        }

        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {  // option
                final String option = args[i].substring(1).toLowerCase(Locale.US);
                if (option.equals("help") || option.equals("h")
                        || option.equals("?")) {
                    printProgramInformation();
                    return;
                }
                if (option.equals("xpath") || option.equals("xp")) {
                    if (i + 1 >= args.length) {
                        printProgramInformation();
                        System.err.println("\"-xpath\" must be followed by a xpath.");
                        return;
                    }
                    xpath = args[i + 1];
                    i++;
                } else {                    // unknown option
                    printProgramInformation();
                    System.err.println("Unknown option: " + option);
                    return;
                }
            } else {                        // no option, must be file name
                from = args[i];
            }
        }
        if (from == null) {
            printProgramInformation();
            System.err.println("XML file must be specified.");
            return;
        }
        if (xpath == null) {
            printProgramInformation();
            System.err.println("XPath file must be specified.");
            return;
        }
        System.out.println(StringUtility.getClassName(XPathLocationFinder.class) + ", running on: "
            + KernelContext.getInstance().getDescriptiveKernelVersion());
        SourceArea result = XPathLocationParser.findSourceArea(new File(from), new SimpleXPath(xpath));
        System.out.println(result);
    }

    /**
     * Writes calling convention to <code>System.err</code>.
     */
    public static final void printProgramInformation() {
        System.err.println("Name");
        System.err.println("----");
        System.err.println(StringUtility.getClassName(XPathLocationFinder.class)
            + " - find simple XML paths");
        System.err.println();
        System.err.println("Synopsis");
        System.err.println("-------------------");
        System.err.println("[-h] -xp[ath] <simpleXPath> <xmlFile>");
        System.err.println();
        System.err.println("Description");
        System.err.println("-----------");
        System.err.println(
            "This program finds the location of a given simple XPath in an XML file.");
        System.err.println();
        System.err.println("Options and Parameter");
        System.err.println("---------------------");
        System.err.println("-h             writes this text and returns");
        System.err.println("-xpath         set the language filter (default: \"en\")");
        System.err.println(
            "<simpleXPath>  simple XML XPath, a subset of the abbreviation XPath notation");
        System.err.println(
            "                \"/element1/element2[3]@attribute\" is an example for such a");
        System.err.println(
            "                notation. This selects from the first occurrence of \"element1\"");
        System.err.println(
            "                and from the third occurrence of subnode \"element2\" the attribute");
        System.err.println(
            "                \"attribute\". The attribute is optional. It is always exactly one");
        System.err.println("                node or the attribute of one node specified.");
        System.err.println("                General syntax:");
        System.err.println("                {<element>\"[\"<index>\"]}+[\"@\"<attribute>]");
        System.err.println("<xmlFile>      XML file");
        System.err.println();
        System.err.println("Parameter Examples");
        System.err.println("------------------");
        System.err.println(
            "-xp QEDEQ/CHAPTER/SECTION/NODE[2]/PRECEDING/AXIOM/FORMULA/FORALL/VAR@id");
        System.err.println("sample/qedeq_basic_concept.xml");
        System.err.println();
        System.err.println("Further information");
        System.err.println("-------------------");
        System.err.println("For more information about *Hilbert II* look at:");
        System.err.println("\thttp://www.qedeq.org/");
        System.err.println();
    }

}
