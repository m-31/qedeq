package org.qedeq.kernel.bo.parser;

import java.io.File;
import java.util.List;

import org.qedeq.base.io.TextInput;
import org.qedeq.kernel.bo.test.DummyInternalKernelServices;
import org.qedeq.kernel.xml.handler.parser.LoadXmlOperatorListUtility;

public class LatexMathParserTest extends AbstractParserTestCase {

    private static String[][] test = new String[][] {
        {    // 00
            "(A \\lor A) \\impl A",
            "IMPL(OR(A(), A()), A())"
        }, { // 01
            "A \\impl (A \\lor B)",
            "IMPL(A(), OR(A(), B()))"
        }, { // 02
            "(A \\lor B) \\impl (B \\lor A)",
            "IMPL(OR(A(), B()), OR(B(), A()))"
        }, { // 03
            "(A \\impl B) \\impl ((C \\lor A) \\impl (C \\lor B))",
            "IMPL(IMPL(A(), B()), IMPL(OR(C(), A()), OR(C(), B())))"
        }, { // 04
            "(\\forall x~\\phi(x)) \\impl \\phi(y)",
            "IMPL(ALL(x(), PREDVAR_1(x())), PREDVAR_1(y()))"
        }, { // 05
            "\\phi(y) \\impl (\\exists x~\\phi(x))",
            "IMPL(PREDVAR_1(y()), EXISTS(x(), PREDVAR_1(x())))"
        }, { // 06
            "$$\\alpha \\land \\beta \\ \\defp \\ \\neg(\\neg\\alpha \\lor \\neg\\beta)$$",
            "DEFP(AND(PREDVAR_3(), PREDVAR_4()), NOT(OR(NOT(PREDVAR_3()), NOT(PREDVAR_4()))))"
        }, { // 07
            "$$\\alpha \\impl \\beta \\ \\defp \\ \\neg\\alpha \\lor \\beta$$",
            "DEFP(IMPL(PREDVAR_3(), PREDVAR_4()), OR(NOT(PREDVAR_3()), PREDVAR_4()))"
        }, { // 08
            "$$\\alpha \\equi \\beta \\ \\defp \\ (\\alpha \\impl \\beta) \\land (\\beta \\impl \\alpha)$$",
            "DEFP(EQUI(PREDVAR_3(), PREDVAR_4()), AND(IMPL(PREDVAR_3(), PREDVAR_4()), IMPL(PREDVAR_4(), PREDVAR_3())))"
        }, { // 09
            "$\\alpha \\impl (\\forall x_1~(\\beta(x_1)))$",
            "IMPL(PREDVAR_3(), ALL(x_1(), PREDVAR_4(x_1())))"
        }
    };

    public LatexMathParserTest(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    protected String[][] getTest() {
        return test;
    }

    protected MathParser createParser(final TextInput input) throws Exception {
        final List operators = LoadXmlOperatorListUtility.getOperatorList(
            new DummyInternalKernelServices(),
            new File(getIndir(),
            "parser/latexMathOperators.xml"));
        final LatexMathParser result = new LatexMathParser();
        result.setParameters(input, operators);
        return result;
    }

    public void testReadMaximalTerm00() throws Exception {
        internalTest(0);
    }

    public void testReadMaximalTerm01() throws Exception {
        internalTest(1);
    }

    public void testReadMaximalTerm02() throws Exception {
        internalTest(2);
    }

    public void testReadMaximalTerm03() throws Exception {
        internalTest(3);
    }

    public void testReadMaximalTerm04() throws Exception {
        internalTest(4);
    }

    public void testReadMaximalTerm05() throws Exception {
        internalTest(5);
    }

    public void testReadMaximalTerm06() throws Exception {
        internalTest(6);
    }

    public void testReadMaximalTerm07() throws Exception {
        internalTest(7);
    }

    public void testReadMaximalTerm08() throws Exception {
        internalTest(8);
    }

    public void testReadMaximalTerm09() throws Exception {
        internalTest(9);
    }

    protected String[][] getExceptionTest() {
        // TODO Auto-generated method stub
        return null;
    }

}
