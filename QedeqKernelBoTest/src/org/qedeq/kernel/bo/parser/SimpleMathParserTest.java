package org.qedeq.kernel.bo.parser;

import java.io.File;
import java.util.List;

import org.qedeq.base.io.TextInput;
import org.qedeq.kernel.bo.test.DummyInternalKernalServices;
import org.qedeq.kernel.xml.handler.parser.LoadXmlOperatorListUtility;

public class SimpleMathParserTest extends AbstractParserTest {

    private static String[][] test = new String[][] {
        {    // 00
            "A & B | C",
            "OR(AND(A, B), C)"
        }, { // 01
            "A | B & C",
            "OR(A, AND(B, C))"
        }, { // 02
            "A & B & C & D",
            "AND(A, B, C, D)"
        }, { // 03
            "(A & B) & (C & D)",
            "AND(AND(A, B), AND(C, D))"
        }, { // 04
            "((A & B) & C) & D",
            "AND(AND(AND(A, B), C), D)"
        }, { // 05
            "A & (B & (C & D))",
            "AND(A, AND(B, AND(C, D)))"
        }, { // 06
            "(((((A)))))",
            "A"
        }, { // 07
            "~A",
            "NOT(A)"
        }, { // 08
            "~~A",
            "NOT(NOT(A))"
        }, { // 09
            "A => B",
            "IMPL(A, B)"
        }, { // 10
            "A => B | C",
            "IMPL(A, OR(B, C))"
        }, { // 11
            "A | B => C",
            "IMPL(OR(A, B), C)"
        }, { // 12
            "A | B => A & B",
            "IMPL(OR(A, B), AND(A, B))"
        }, { // 13
            "(A => B) => C",
            "IMPL(IMPL(A, B), C)"
        }, { // 14
            "A & B => B & D",
            "IMPL(AND(A, B), AND(B, D))"
        }, { // 15
            "A & B | C => B | D & E",
            "IMPL(OR(AND(A, B), C), OR(B, AND(D, E)))"
        }, { // 16
            "A <=> B",
            "EQUI(A, B)"
        }, { // 17
            "A <=> B | C",
            "EQUI(A, OR(B, C))"
        }, { // 18
            "A | B <=> C",
            "EQUI(OR(A, B), C)"
        }, { // 19
            "A | B <=> A & B",
            "EQUI(OR(A, B), AND(A, B))"
        }, { // 20
            "A <=> B <=> C",
            "EQUI(A, B, C)"
        }, { // 21
            "all x A & B \n",   // "\n" to separate this formula from the next one
            "ALL(x, AND(A, B))"
        }, { // 22
            "all x x = y y",
            "ALL(x, EQUAL(x, y), y)"
        }, { // 23
            "x & y | z | a & -(b | c | d & k) & v",
            "OR(AND(x, y), z, AND(a, NOT(OR(b, c, AND(d, k))), v))"
        }, { // 24
            "x & y | z | a & -(b | c | d & k) & v => exists y z & a  <=> GO <=> GI\n",
            "IMPL(OR(AND(x, y), z, AND(a, NOT(OR(b, c, AND(d, k))), v)), EXISTS(y, EQUI(AND(z, a), GO, GI)))"
        }, { // 25
            "{x, y, z}",
            "SET(x, y, z)"
        }, { // 26
            "{x}",
            "SET(x)"
        }, { // 27
            "{}",
            "SET()"
        }, { // 28
            "{x : y}",
            "SETPROP(x, y)"
        }

    };

    public SimpleMathParserTest(String arg0) {
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
            new DummyInternalKernalServices(),
            new File(getIndir(),
            "parser/simpleMathOperators.xml"));
        final MathParser parser = new SimpleMathParser();
        parser.setParameters(input, operators);
        return parser;
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

    public void testReadMaximalTerm25() throws Exception {
        internalTest(25);
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

}
