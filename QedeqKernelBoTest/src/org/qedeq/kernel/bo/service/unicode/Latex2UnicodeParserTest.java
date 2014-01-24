/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.base.io.SourceArea;
import org.qedeq.base.io.SourcePosition;
import org.qedeq.kernel.bo.common.ModuleService;
import org.qedeq.kernel.bo.service.latex.LatexContentException;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.SourceFileException;
import org.qedeq.kernel.se.common.SourceFileExceptionList;

/**
 * For test of generating Utf8 output.
 *
 * @author Michael Meyling
 */
public class Latex2UnicodeParserTest extends QedeqBoTestCase {

    private ReferenceFinder finder;

    private SourceFileExceptionList warnings;
    
    public Latex2UnicodeParserTest() {
        super();
    }

    public Latex2UnicodeParserTest(final String name) {
        super(name);
    }

    public void setUp() {
        finder = new ReferenceFinder() {
            
            public String getReferenceLink(String reference,
                    SourcePosition startDelta, SourcePosition endDelta) {
//                System.out.println("reference: " + reference);
                if ("missing".equals(reference)) {
                    addWarning(1, "reference not found: " + reference, startDelta, endDelta);
                    return "[" + reference + "?]";
                }
                return "[" + reference + "]";
            }
            
            public void addWarning(int code, String msg, SourcePosition startDelta,
                    SourcePosition endDelta) {
                ModuleDataException e = new LatexContentException(code, msg,
                        new ModuleContext(new DefaultModuleAddress(), "", startDelta, endDelta));
                final SourceFileException sf = new SourceFileException((ModuleService) null, e,
                        new SourceArea("memory", startDelta, endDelta), null);

                warnings.add(sf);
                
            }
        };
        warnings = new SourceFileExceptionList();
    }

    /**
     * Check that there are no LaTeX warnings in Q2L001.
     *
     * @throws  Exception
     */
    public void test01() throws Exception {
        final String text = "The project \\textbf{Hilbert II} deals with the formal "
            + "presentation and documentation of mathematical knowledge. For this reason "
            + "\\textbf{Hilbert II} provides a program suite to accomplish that tasks. The "
            + "concrete documentation of mathematical basics is also a purpose of this project."
            + "   \n"
            + "          For further informations about the \\textbf{Hilbert II} project see under "
            + "\\url{http://www.qedeq.org}.\n"
            + "\n"
            + "          \\par\n"
            + "          This document describes the logical axioms and the rules and meta rules "
            + "that are used to derive new propositions.\n"
            + "\n"
            + "            \\par\n"
            + "            The presentation is axiomatic and in a formal form. A formal calculus "
            + "is given that enables us to derive all true formulas. Additional derived rules, "
            + "theorems, definitions, abbreviations and syntax extensions basically correspond "
            + "with the mathematical practice.\n"
            + "\n"
            + "            \\par\n"
            + "            This document is also written in a formal language, the original "
            + "text is a XML file with a syntax defined by the XSD \\url{http://www.qedeq.org/"
            + "current/xml/qedeq.xsd}.\n"
            + "\n"
            + "            \\par\n"
            + "            This document is work in progress and is updated from time to time. "
            + "Especially at the locations marked by {\\glqq+++\\grqq} additions or changes will "
            + "take place.\n";
        final String result = "The project \uff28\uff49\uff4c\uff42\uff45\uff52\uff54 \uff29\uff29 deals with the formal presentation and "
            + "documentation of mathematical knowledge. For this reason \uff28\uff49\uff4c\uff42\uff45\uff52\uff54 \uff29\uff29 provides a "
            + "program suite to accomplish that tasks. The concrete documentation of mathematical "
            + "basics is also a purpose of this project. For further informations about the "
            + "\uff28\uff49\uff4c\uff42\uff45\uff52\uff54 \uff29\uff29 project see under  http://www.qedeq.org .\n\n"
            + "This document describes "
            + "the logical axioms and the rules and meta rules that are used to derive new "
            + "propositions.\n\n"
            + "The presentation is axiomatic and in a formal form. A formal calculus "
            + "is given that enables us to derive all true formulas. Additional derived rules, "
            + "theorems, definitions, abbreviations and syntax extensions basically correspond "
            + "with the mathematical practice.\n\n"
            + "This document is also written in a formal "
            + "language, the original text is a XML file with a syntax defined by the XSD  "
            + "http://www.qedeq.org/current/xml/qedeq.xsd .\n\n"
            + "This document is work in progress "
            + "and is updated from time to time. Especially at the locations marked by \u201e+++\u201d "
            + "additions or changes will take place.";
        assertEquals(result, Latex2UnicodeParser.transform(finder, text, 0));
        assertEquals(0, warnings.size());
    }

    public void test02() throws Exception {
        final String text = "At the beginning we quote \\emph{D. Hilbert} from the lecture "
            + "{\\glqq The Logical Basis of Mathematics\\grqq}, September \n"
            + "          1922\\footnote{Lecture given at the Deutsche Naturforscher-Gesellschaft, "
            + "September 1922.}.\n"
            + "\n"
            + "          \\par\n"
            + "          \\begin{quote} {\n"
            + "          \\glqq The fundamental idea of my proof theory is the following:\n"
            + "          \n"
            + "          \\par\n"
            + "          All the propositions that constitute in mathematics are converted into "
            + "formulas, so that mathematics proper becomes all inventory of formulas. These "
            + "differ from the ordinary formulas of mathematics only in that, besides the "
            + "ordinary signs, \n"
            + "          the logical signs especially {\\glqq implies\\grqq} ($\\rightarrow$) "
            + "and for {\\glqq not\\grqq} ($\\bar{\\quad}$) occur in them. \n"
            + "          Certain formulas, which serve as building blocks for the formal edifice "
            + "of mathematics, are called axioms. A proof is an array that must be given as such "
            + "to our perceptual intuition of it of inferences according to the schema\\\\\n"
            + "          \\begin{eqnarray*}\n"
            + "          & A & \\\\\n"
            + "          & A \\rightarrow B& \\\\\n"
            + "          \\cline{2-3}\n"
            + "           & B &\n"
            + "          \\end{eqnarray*}\n"
            + "          where each of the premises, that is, the formulae, $A$ and "
            + "$A \\rightarrow B$ in the array either is an axiom or directly from an axiom by "
            + "substitution, or else coincides with the end formula $B$ of an inference occurring "
            + "earlier in the proof or results from it by substitution. A formula is said to be "
            + "provable if it is either an axiom or the end formula of a proof.\\grqq}\n"
            + "          \\end{quote}\n";
        final String result = "At the beginning we quote  D .   H i l b e r t  from the lecture "
            + "\u201eThe Logical \n"
            + "Basis of Mathematics\u201d, September 1922\n"
            + "          \u250c\n"
            + "          \u2502 Lecture given at the Deutsche Naturforscher-Gesellschaft, September \n"
            + "          \u2502 1922.\n"
            + "          \u2514\n"
            + ".\n"
            + "\n"
            + "\n"
            + "      \u201eThe fundamental idea of my proof theory is the following:\n"
            + "\n"
            + "      All the propositions that constitute in mathematics are converted into \n"
            + "      formulas, so that mathematics proper becomes all inventory of formulas. \n"
            + "      These differ from the ordinary formulas of mathematics only in that, \n"
            + "      besides the ordinary signs, the logical signs especially \u201eimplies\u201d (\u2192) and\n"
            + "      for \u201enot\u201d (\u203e ) occur in them. Certain formulas, which serve as building \n"
            + "      blocks for the formal edifice of mathematics, are called axioms. A proof \n"
            + "      is an array that must be given as such to our perceptual intuition of it \n"
            + "      of inferences according to the schema\n"
            + "          A   \n"
            + "         A \u2192 B  \n"
            + "       _______________________________________\n"
            + "         B  \n"
            + "       where each of the premises, that is, the formulae, A and A \u2192 B in the \n"
            + "      array either is an axiom or directly from an axiom by substitution, or \n"
            + "      else coincides with the end formula B of an inference occurring earlier in\n"
            + "      the proof or results from it by substitution. A formula is said to be \n"
            + "      provable if it is either an axiom or the end formula of a proof.\u201d\n";
//        System.out.println(warnings);
        assertEquals(result, Latex2UnicodeParser.transform(finder, text, 80));
        assertEquals(0, warnings.size());
    }

    /**
     * Test if leading whitespace is removed.
     *
     * @throws Exception
     */
    public void test03() throws Exception {
        final String text = "   \n \t hi";
        final String result = "hi";
        assertEquals(result, Latex2UnicodeParser.transform(finder, text, 80));
        assertEquals(0, warnings.size());
    }

    /**
     * Test problems during "\qref" are found.
     *
     * @throws Exception
     */
    public void test04() throws Exception {
        final String text  = "    \\qref{123] missing";
        final String result = "123] missing";
        assertEquals(result, Latex2UnicodeParser.transform(finder, text, 80));
//        System.out.println(warnings);
        assertEquals(2, warnings.size());
        final SourceFileException first = warnings.get(0);
        assertEquals("memory:1:10:1:23", first.getSourceArea().toString());
        final SourceFileException second = warnings.get(1);
        assertEquals("memory:1:10:1:11", second.getSourceArea().toString());
    }

    /**
     * Test if missing "}" is correctly warned.
     *
     * @throws Exception
     */
    public void test05() throws Exception {
        final String text  = "    {%% ignore me\n  missing\n  still missing";
        final String result = "missing still missing";
        assertEquals(result, Latex2UnicodeParser.transform(finder, text, 80));
        assertEquals(1, warnings.size());
        final SourceFileException first = warnings.get(0);
        assertEquals("memory:1:5:3:16", first.getSourceArea().toString());
    }

    /**
     * Test if leading whitespace is removed.
     *
     * @throws Exception
     */
    public void test06() throws Exception {
        final String text  = "A \\emph{Hilbert} B \\emph{Bernays}.";
        final String result = "A  H i l b e r t  "
        + "B  B e r n a y s .";
        assertEquals(result, Latex2UnicodeParser.transform(finder, text, 0));
        assertEquals(0, warnings.size());
    }

    /**
     * Test if leading whitespace is removed.
     *
     * @throws Exception
     */
    public void test07() throws Exception {
        final String text  = "          In this chapter we start with the very basic axioms and "
            + "definitions of set theory. We shall make no attempt to introduce a formal language"
            + "\\footnote{Despite of this, in the original text of this document the formulas of "
            + "axioms, definitions and propositions are written in a formal language. The "
            + "original text is a XML file with a syntax defined by the XSD "
            + "\\url{http://www.qedeq.org/current/xml/qedeq.xsd}. A more detailed description of "
            + "the formula language is given in "
            + "\\url{http://www.qedeq.org/current/doc/project/qedeq_logic_language_en.pdf}.} but "
            + "shall be content with the common logical operators. To be more precise: "
            + "precondition is a first-order predicate calculus with identity.\n";
        final String result = "In this chapter we start with the very basic axioms and definitions of set \n"
            + "theory. We shall make no attempt to introduce a formal language\n"
            + "          \u250c\n"
            + "          \u2502 Despite of this, in the original text of this document the formulas \n"
            + "          \u2502 of axioms, definitions and propositions are written in a formal \n"
            + "          \u2502 language. The original text is a XML file with a syntax defined by \n"
            + "          \u2502 the XSD  http://www.qedeq.org/current/xml/qedeq.xsd . A more \n"
            + "          \u2502 detailed description of the formula language is given in \n"
            + "          \u2502  http://www.qedeq.org/current/doc/project/qedeq_logic_language_en.pdf .\n"
            + "          \u2514\n"
            + " but shall be content with the common logical operators. To be more precise: \n"
            + "precondition is a first-order predicate calculus with identity.";
        assertEquals(result, Latex2UnicodeParser.transform(finder, text, 80));
        assertEquals(0, warnings.size());
    }

    /**
     * Test problems during "\qref" are found.
     *
     * @throws Exception
     */
    public void test08() throws Exception {
        final String text  = "    in \\qref{gold} we trust.";
        final String result = "in [gold] we trust.";
        assertEquals(result, Latex2UnicodeParser.transform(finder, text, 80));
//        System.out.println(warnings);
        assertEquals(0, warnings.size());
    }

    /**
     * Test problems during "\qref" are found.
     *
     * @throws Exception
     */
    public void test09() throws Exception {
        final String text  = "\n    we {crossed} the \\qref{missing} river at noon";
        final String result = "we crossed the [missing?] river at noon";
        assertEquals(result, Latex2UnicodeParser.transform(finder, text, 80));
//        System.out.println(warnings);
        assertEquals(1, warnings.size());
        final SourceFileException first = warnings.get(0);
        assertEquals("memory:2:27:2:36", first.getSourceArea().toString());
    }

    /**
     * Test problems during "\qref" are found.
     *
     * @throws Exception
     */
    public void test10() throws Exception {
        final String text  = "\n    {we {crossed} the \\qref{missing} river at noon}";
        final String result = "we crossed the [missing?] river at noon";
        assertEquals(result, Latex2UnicodeParser.transform(finder, text, 80));
//        System.out.println(warnings);
        assertEquals(1, warnings.size());
        final SourceFileException first = warnings.get(0);
        assertEquals("memory:2:28:2:37", first.getSourceArea().toString());
    }

    /**
     * Test problems during "\qref" are found.
     *
     * @throws Exception
     */
    public void test11() throws Exception {
        final String text  = "\n    {we {crossed} the \\qref{missing} river at \\qref{missing} }";
        final String result = "we crossed the [missing?] river at [missing?]";
        assertEquals(result, Latex2UnicodeParser.transform(finder, text, 80));
//        System.out.println(warnings);
        assertEquals(2, warnings.size()); 
        final SourceFileException first = warnings.get(0);
//        System.out.println("Area: " + (new TextInput(text)).getSourceArea(first.getSourceArea()));
        assertEquals("memory:2:28:2:37", first.getSourceArea().toString());
        final SourceFileException second = warnings.get(1);
//        System.out.println("Area: " + (new TextInput(text)).getSourceArea(second.getSourceArea()));
        assertEquals("memory:2:52:2:61", second.getSourceArea().toString());
    }

    /**
     * Test problems during "\qref" are found.
     *
     * @throws Exception
     */
    public void test12() throws Exception {
        final String text  = "\n    {we {crossed} the \\qref{missing{}} river at \\qref{missing} }";
        final String result = "we crossed the missing river at [missing?]";
        assertEquals(result, Latex2UnicodeParser.transform(finder, text, 80));
//        System.out.println(warnings);
        assertEquals(2, warnings.size());
        final SourceFileException first = warnings.get(0);
//        System.out.println("Area: " + (new TextInput(text)).getSourceArea(first.getSourceArea()));
        assertEquals("memory:2:28:2:39", first.getSourceArea().toString());
    }

}
