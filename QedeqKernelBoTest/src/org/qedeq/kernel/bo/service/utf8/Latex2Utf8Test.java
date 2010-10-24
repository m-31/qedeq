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

import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.bo.context.KernelContext;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;
import org.qedeq.kernel.common.DefaultModuleAddress;
import org.qedeq.kernel.common.LogicalState;
import org.qedeq.kernel.common.ModuleAddress;

/**
 * For test of generating Utf8 output.
 *
 * @author Michael Meyling
 */
public class Latex2Utf8Test extends QedeqBoTestCase {

    public Latex2Utf8Test() {
        super();
    }

    public Latex2Utf8Test(final String name) {
        super(name);
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
        final String result = "The project Ｈｉｌｂｅｒｔ ＩＩ deals with the formal presentation and "
            + "documentation of mathematical knowledge. For this reason Ｈｉｌｂｅｒｔ ＩＩ provides a "
            + "program suite to accomplish that tasks. The concrete documentation of mathematical "
            + "basics is also a purpose of this project. For further informations about the "
            + "Ｈｉｌｂｅｒｔ ＩＩ project see under  http://www.qedeq.org .\n\n"
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
            + "and is updated from time to time. Especially at the locations marked by „+++” "
            + "additions or changes will take place.";
        assertEquals(result, Latex2Utf8Parser.transform(text, 0));
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
        final String result = "At the beginning we quote  D .   H i l b e r t  from the lecture "
            + "„The Logical\n"
            + "Basis of Mathematics”, September 1922\n"
            + "          ┌\n"
            + "          │ Lecture given at the Deutsche Naturforscher-Gesellschaft, September\n"
            + "          │ 1922.\n"
            + "          └\n"
            + ".\n"
            + "\n"
            + "\n"
            + "      „The fundamental idea of my proof theory is the following:\n"
            + "\n"
            + "      All the propositions that constitute in mathematics are converted into\n"
            + "      formulas, so that mathematics proper becomes all inventory of formulas.\n"
            + "      These differ from the ordinary formulas of mathematics only in that,\n"
            + "      besides the ordinary signs, the logical signs especially „implies” (→) and\n"
            + "      for „not” (‾ ) occur in them. Certain formulas, which serve as building\n"
            + "      blocks for the formal edifice of mathematics, are called axioms. A proof\n"
            + "      is an array that must be given as such to our perceptual intuition of it\n"
            + "      of inferences according to the schema\n"
            + "          A\n"
            + "         A → B\n"
            + "      _______________________________________\n"
            + "\n"
            + "         B\n"
            + "       where each of the premises, that is, the formulae, A and A → B in the\n"
            + "      array either is an axiom or directly from an axiom by substitution, or\n"
            + "      else coincides with the end formula B of an inference occurring earlier in\n"
            + "      the proof or results from it by substitution. A formula is said to be\n"
            + "      provable if it is either an axiom or the end formula of a proof.”\n";
        assertEquals(result, Latex2Utf8Parser.transform(text, 80));
    }

}
