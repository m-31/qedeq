package org.qedeq.kernel.bo.parser;

import java.io.File;
import java.util.List;

import org.qedeq.base.io.TextInput;
import org.qedeq.kernel.xml.handler.parser.LoadXmlOperatorListUtility;

public class AsciiMathParserTest extends AbstractParserTest {

    private static String[][] test = new String[][] {
        {    // 00
            "set(x) <-> Ey x in y",
            "EQUI(isSet(x), EXISTS(y, IN(x, y)))"
        }, { // 01
            "Ex Ay(y in x <-> set(y) & P(y))",
            "EXISTS(x, ALL(y, EQUI(IN(y, x), AND(isSet(y), PREDVAR_1(y)))))"
        }, { // 02
            "x = y <-> Az(z in x <-> z in y)",
            "EQUI(EQUAL(x, y), ALL(z, EQUI(IN(z, x), IN(z, y))))"
        }, { // 03
            "x = x\n",
            "EQUAL(x, x)"
        }, { // 04
            "a in x <-> a in x",
            "EQUI(IN(a, x), IN(a, x))"
        }, { // 05
            "Az(z in x <-> z in x)",
            "ALL(z, EQUI(IN(z, x), IN(z, x)))"
        }, { // 06
            "x = y -> y = x",
            "IMPL(EQUAL(x, y), EQUAL(y, x))"
        }, { // 07
            "x = y",
            "EQUAL(x, y)"
        }, { // 08
            "Az(z in x <-> z in y)",
            "ALL(z, EQUI(IN(z, x), IN(z, y)))"
        }, { // 09
            "Az(z in y <-> z in x)",
            "ALL(z, EQUI(IN(z, y), IN(z, x)))"
        }, { // 10
            "y = x",
            "EQUAL(y, x)"
        }, { // 11
            "x = y -> y = x",
            "IMPL(EQUAL(x, y), EQUAL(y, x))"
        }, { // 12
            "x = y & y = z -> x = z",
            "IMPL(AND(EQUAL(x, y), EQUAL(y, z)), EQUAL(x, z))"
        }, { // 13
            "x = y & y = z",
            "AND(EQUAL(x, y), EQUAL(y, z))"
        }, { // 14
            "Au(u in x <-> u in y) & Au(u in y <-> u in z)",
            "AND(ALL(u, EQUI(IN(u, x), IN(u, y))), ALL(u, EQUI(IN(u, y), IN(u, z))))"
        }, { // 15
            "Au((u in x <-> u in y) & (u in y <-> u in z))",
            "ALL(u, AND(EQUI(IN(u, x), IN(u, y)), EQUI(IN(u, y), IN(u, z))))"
        }, { // 16
            "(a in x <-> a in y) & (a in y <-> a in z)",
            "AND(EQUI(IN(a, x), IN(a, y)), EQUI(IN(a, y), IN(a, z)))"
        }, { // 17
            "a in x <-> a in z",
            "EQUI(IN(a, x), IN(a, z))"
        }, { // 18
            "Au(u in x <-> u in z)",
            "ALL(u, EQUI(IN(u, x), IN(u, z)))"
        }, { // 19
            "x = z",
            "EQUAL(x, z)"
        }, { // 20
            "x = y & z in x -> z in y",
            "IMPL(AND(EQUAL(x, y), IN(z, x)), IN(z, y))"
        }, { // 21
            "x = y & z in x",
            "AND(EQUAL(x, y), IN(z, x))"
        }, { // 22
            "z in x",
            "IN(z, x)"
        }, { // 23
            "x = y",
            "EQUAL(x, y)"
        }, { // 24
            "Au(u in x <-> u in y)",
            "ALL(u, EQUI(IN(u, x), IN(u, y)))"
        }, { // 25
            "z in x <-> z in y",
            "EQUI(IN(z, x), IN(z, y))"
        }, { // 26
            "z in y",
            "IN(z, y)"
        }, { // 27
            "x = y & x in z -> y in z",
            "IMPL(AND(EQUAL(x, y), IN(x, z)), IN(y, z))"
        }, { // 28
            "x in {y | P(y)} <-> set(x) & P(x)",
            "EQUI(IN(x, SETDEF(y, PREDVAR_1(y))), AND(isSet(x), PREDVAR_1(x)))"
        }, { // 29
            "{y | P(y)} in x <-> Ez(Ay((set(y) & P(y)) <-> y in z) & z in x)",
            "EQUI(IN(SETDEF(y, PREDVAR_1(y)), x), EXISTS(z, AND(ALL(y, EQUI(AND(isSet(y), PREDVAR_1(y)), IN(y, z))), IN(z, x))))"
        }, { // 30
            "{y | P(y)} in x <-> Ez(z = {y | P(y)} & z in x)",
            "EQUI(IN(SETDEF(y, PREDVAR_1(y)), x), EXISTS(z, AND(EQUAL(z, SETDEF(y, PREDVAR_1(y))), IN(z, x))))"
        }, { // 31
            "{y | P(y)} in x <-> {y | P(y)} in x",
            "EQUI(IN(SETDEF(y, PREDVAR_1(y)), x), IN(SETDEF(y, PREDVAR_1(y)), x))"
        }, { // 32
            "Ez(Ay((set(y) & P(y)) <-> y in z) & z in x)",
            "EXISTS(z, AND(ALL(y, EQUI(AND(isSet(y), PREDVAR_1(y)), IN(y, z))), IN(z, x)))"
        }, { // 33
            "Ez(Au((set(u) & P(u)) <-> u in z) & z in x)",
            "EXISTS(z, AND(ALL(u, EQUI(AND(isSet(u), PREDVAR_1(u)), IN(u, z))), IN(z, x)))"
        }, { // 34
            "Ez(Au(u in z <-> (set(u) & P(u))) & z in x)",
            "EXISTS(z, AND(ALL(u, EQUI(IN(u, z), AND(isSet(u), PREDVAR_1(u)))), IN(z, x)))"
        }, { // 35
            "Ez(Au(u in z <-> u in {y | P(y)}) & z in x)",
            "EXISTS(z, AND(ALL(u, EQUI(IN(u, z), IN(u, SETDEF(y, PREDVAR_1(y))))), IN(z, x)))"
        }, { // 36
            "Ez(z = {y | P(y)} & z in x)",
            "EXISTS(z, AND(EQUAL(z, SETDEF(y, PREDVAR_1(y))), IN(z, x)))"
        }, { // 37
            "({y | P(y)} in x <-> {y | P(y)} in x)"
            + "<-> (Ez(Ay((set(y) & P(y)) <-> y in z) & z in x))"
            + "<-> (Ez(Au((set(u) & P(u)) <-> u in z) & z in x))"
            + "<-> (Ez(Au(u in z <-> (set(u) & P(u))) & z in x))"
            + "<-> (Ez(Au(u in z <-> u in {y | P(y)}) & z in x))"
            + "<-> (Ez(z = {y | P(y)} & z in x))",
            "EQUI(EQUI(IN(SETDEF(y, PREDVAR_1(y)), x), IN(SETDEF(y, PREDVAR_1(y)), x)),"
            + " EXISTS(z, AND(ALL(y, EQUI(AND(isSet(y), PREDVAR_1(y)), IN(y, z))), IN(z, x))),"
            + " EXISTS(z, AND(ALL(u, EQUI(AND(isSet(u), PREDVAR_1(u)), IN(u, z))), IN(z, x))),"
            + " EXISTS(z, AND(ALL(u, EQUI(IN(u, z), AND(isSet(u), PREDVAR_1(u)))), IN(z, x))),"
            + " EXISTS(z, AND(ALL(u, EQUI(IN(u, z), IN(u, SETDEF(y, PREDVAR_1(y))))), IN(z, x))),"
            + " EXISTS(z, AND(EQUAL(z, SETDEF(y, PREDVAR_1(y))), IN(z, x))))"
        }, { // 38
            "{x | P(x)} = {x | Q(x)} <-> (Ax(set(x) -> (P(x) <-> Q(x))))",
            "EQUI(EQUAL(SETDEF(x, PREDVAR_1(x)), SETDEF(x, PREDVAR_2(x))), ALL(x, IMPL(isSet(x), EQUI(PREDVAR_1(x), PREDVAR_2(x)))))"
        }, { // 39
            "{x | P(x)} = {x | Q(x)}",
            "EQUAL(SETDEF(x, PREDVAR_1(x)), SETDEF(x, PREDVAR_2(x)))"
        }, { // 40
            "Ay(y in {x | P(x)} <-> y in {x | Q(x)})",
            "ALL(y, EQUI(IN(y, SETDEF(x, PREDVAR_1(x))), IN(y, SETDEF(x, PREDVAR_2(x)))))"
        }, { // 41
            "Ay((set(y) & P(y)) <-> (set(y) & Q(y)))",
            "ALL(y, EQUI(AND(isSet(y), PREDVAR_1(y)), AND(isSet(y), PREDVAR_2(y))))"
        }, { // 42
            "Ay(((set(y) & P(y)) -> (set(y) & Q(y))) & ((set(y) & Q(y)) -> (set(y) & P(y))))",
            "ALL(y, AND(IMPL(AND(isSet(y), PREDVAR_1(y)), AND(isSet(y), PREDVAR_2(y))), IMPL(AND(isSet(y), PREDVAR_2(y)), AND(isSet(y), PREDVAR_1(y)))))"
        }, { // 43
            "Ay(((set(y) & P(y)) -> Q(y)) & ((set(y) & Q(y)) -> P(y)))",
            "ALL(y, AND(IMPL(AND(isSet(y), PREDVAR_1(y)), PREDVAR_2(y)), IMPL(AND(isSet(y), PREDVAR_2(y)), PREDVAR_1(y))))"
        }, { // 44
            "Ay((set(y) -> (P(y) -> Q(y))) & (set(y) -> (Q(y) -> P(y))))",
            "ALL(y, AND(IMPL(isSet(y), IMPL(PREDVAR_1(y), PREDVAR_2(y))), IMPL(isSet(y), IMPL(PREDVAR_2(y), PREDVAR_1(y)))))"
        }, { // 45
            "Ay(set(y) -> ((P(y) -> Q(y)) & (Q(y) -> P(y))))",
            "ALL(y, IMPL(isSet(y), AND(IMPL(PREDVAR_1(y), PREDVAR_2(y)), IMPL(PREDVAR_2(y), PREDVAR_1(y)))))"
        }, { // 46
            "Ay(set(y) -> (P(y) <-> Q(y)))",
            "ALL(y, IMPL(isSet(y), EQUI(PREDVAR_1(y), PREDVAR_2(y))))"
        }, { // 47
            "(x union y) <-> {z | z in x v z in y}",
            "EQUI(UNION(x, y), SETDEF(z, OR(IN(z, x), IN(z, y))))"
        }, { // 48
            "(x inter y) <-> {z | z in x & z in y}",
            "EQUI(INTER(x, y), SETDEF(z, AND(IN(z, x), IN(z, y))))"
        }, { // 49
            "z in (x union y) <-> z in x v z in y",
            "EQUI(IN(z, UNION(x, y)), OR(IN(z, x), IN(z, y)))"
        }, { // 50
            "z in {x, y} <-> z = x v z = y",
            "EQUI(IN(z, SET(x, y)), OR(EQUAL(z, x), EQUAL(z, y)))"
        }
    };

    public AsciiMathParserTest(String arg0) {
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
        final List operators = LoadXmlOperatorListUtility.getOperatorList(new File(getIndir(),
            "parser/asciiMathOperators.xml"));
        return new AsciiMathParser(input, operators);
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

    public void testReadMaximalTerm50() throws Exception {
        internalTest(50);
    }
}
