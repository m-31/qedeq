package org.qedeq.kernel.parser;

import java.util.List;

import org.qedeq.kernel.utility.ResourceLoaderUtility;
import org.qedeq.kernel.utility.TextInput;
import org.qedeq.kernel.xml.handler.parser.LoadXmlOperatorListUtility;

public class LatexMathParserTest extends AbstractParserTest {

    private static String[][] test = new String[][] {
        {    // 00
            "(A \\lor A) \\impl A", 
            "IMPL(OR(A, A), A)"
        }, { // 01
            "A \\impl (A \\lor B)",
            "IMPL(A, OR(A, B))"
        }, { // 02
            "(A \\lor B) \\impl (B \\lor A)",
            "IMPL(OR(A, B), OR(B, A))"
        }, { // 03
            "(A \\impl B) \\impl ((C \\lor A) \\impl (C \\lor B))",
            "IMPL(IMPL(A, B), IMPL(OR(C, A), OR(C, B)))"
        }, { // 04
            "(\\forall x~\\phi(x)) \\impl \\phi(y)",
            "IMPL(ALL(x, PREDVAR_1(x)), PREDVAR_1(y))"
        }, { // 05
            "\\phi(y) \\impl (\\exists x~\\phi(x))",
            "IMPL(PREDVAR_1(y), EXISTS(x, PREDVAR_1(x)))"
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
            "IMPL(PREDVAR_3(), ALL(x_1, PREDVAR_4(x_1)))"
        }, { // 10
            "",
            ""
        }, { // 11
            "",
            ""
        }, { // 12
            "",
            ""
        }, { // 13
            "",
            ""
        }, { // 14
            "",
            ""
        }, { // 15
            "",
            ""
        }, { // 16
            "",
            ""
        }, { // 17
            "",
            ""
        }, { // 18
            "",
            ""
        }, { // 19
            "",
            ""
        }, { // 20
            "",
            ""
        }, { // 21
            "",
            ""
        }, { // 22
            "",
            ""
        }, { // 23
            "",
            ""
        }, { // 24
            "",
            ""
        }, { // 25
            "",
            ""
        }, { // 26
            "",
            ""
        }, { // 27
            "",
            ""
        }, { // 28
            "",
            ""
        }, { // 29
            "",
            ""
        }, { // 30
            "",
            ""
        }, { // 31
            "",
            ""
        }, { // 32
            "",
            ""
        }, { // 33
            "",
            ""
        }, { // 34
            "",
            ""
        }, { // 35
            "",
            ""
        }, { // 36
            "",
            ""
        }, { // 37
            "",
            ""
        }, { // 38
            "",
            ""
        }, { // 39
            "",
            ""
        }, { // 40
            "",
            ""
        }, { // 41
            "",
            ""
        }, { // 42
            "",
            ""
        }, { // 43
            "",
            ""
        }, { // 44
            "",
            ""
        }, { // 45
            "",
            ""
        }, { // 46
            "",
            ""
        }, { // 47
            "",
            ""
        }, { // 48
            "",
            ""
        }, { // 49
            "",
            ""
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
            ResourceLoaderUtility.getResourceUrl("org/qedeq/kernel/parser/latexMathOperators.xml"));
        return new LatexMathParser(input, operators);
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

    public void testReadMaximalTerm10() throws Exception {
        internalTest(10);
    }

    public void testReadMaximalTerm11() throws Exception {
        internalTest(11);
    }

    public void testReadMaximalTerm12() throws Exception {
        internalTest(12);
    }

    public void testReadMaximalTerm13() throws Exception {
        internalTest(13);
    }

    public void testReadMaximalTerm14() throws Exception {
        internalTest(14);
    }

    public void testReadMaximalTerm15() throws Exception {
        internalTest(15);
    }

    public void testReadMaximalTerm16() throws Exception {
        internalTest(16);
    }

    public void testReadMaximalTerm17() throws Exception {
        internalTest(17);
    }

    public void testReadMaximalTerm18() throws Exception {
        internalTest(18);
    }

    public void testReadMaximalTerm19() throws Exception {
        internalTest(19);
    }

    public void testReadMaximalTerm20() throws Exception {
        internalTest(20);
    }

    public void testReadMaximalTerm21() throws Exception {
        internalTest(21);
    }

    public void testReadMaximalTerm22() throws Exception {
        internalTest(22);
    }

    public void testReadMaximalTerm23() throws Exception {
        internalTest(23);
    }

    public void testReadMaximalTerm24() throws Exception {
        internalTest(24);
    }

    public void testReadMaximalTerm26() throws Exception {
        internalTest(26);
    }

    public void testReadMaximalTerm27() throws Exception {
        internalTest(27);
    }

    public void testReadMaximalTerm28() throws Exception {
        internalTest(28);
    }

    public void testReadMaximalTerm29() throws Exception {
        internalTest(29);
    }

    public void testReadMaximalTerm30() throws Exception {
        internalTest(30);
    }

    public void testReadMaximalTerm31() throws Exception {
        internalTest(31);
    }

    public void testReadMaximalTerm32() throws Exception {
        internalTest(32);
    }

    public void testReadMaximalTerm33() throws Exception {
        internalTest(33);
    }

    public void testReadMaximalTerm34() throws Exception {
        internalTest(34);
    }

    public void testReadMaximalTerm35() throws Exception {
        internalTest(35);
    }

    public void testReadMaximalTerm36() throws Exception {
        internalTest(36);
    }

    public void testReadMaximalTerm37() throws Exception {
        internalTest(37);
    }

    public void testReadMaximalTerm38() throws Exception {
        internalTest(38);
    }

    public void testReadMaximalTerm39() throws Exception {
        internalTest(39);
    }

    public void testReadMaximalTerm40() throws Exception {
        internalTest(40);
    }

    public void testReadMaximalTerm41() throws Exception {
        internalTest(41);
    }

    public void testReadMaximalTerm42() throws Exception {
        internalTest(42);
    }

    public void testReadMaximalTerm43() throws Exception {
        internalTest(43);
    }

    public void testReadMaximalTerm44() throws Exception {
        internalTest(44);
    }

    public void testReadMaximalTerm45() throws Exception {
        internalTest(45);
    }

    public void testReadMaximalTerm46() throws Exception {
        internalTest(46);
    }

    public void testReadMaximalTerm47() throws Exception {
        internalTest(47);
    }

    public void testReadMaximalTerm48() throws Exception {
        internalTest(48);
    }

    public void testReadMaximalTerm49() throws Exception {
        internalTest(49);
    }

}
