package org.qedeq.kernel.parser;

import org.qedeq.kernel.test.QedeqTestCase;
import org.qedeq.kernel.utility.IoUtility;
import org.qedeq.kernel.utility.TextInput;


public abstract class AbstractParserTest extends QedeqTestCase {

    public AbstractParserTest(String arg0) {
        super(arg0);
    }
        
    protected abstract String[][] getTest();
    
    protected abstract MathParser createParser(TextInput input) throws Exception;

    public void testAllTogether() throws Exception {
        final StringBuffer in = new StringBuffer();
        final StringBuffer expected = new StringBuffer();
        for (int i = 0; i < getTest().length; i++) {
            in.append(getTest()[i][0]).append("\n");
            expected.append(getTest()[i][1]).append("\n");
        }
        final TextInput input = new TextInput(in);
        MathParser parser = createParser(input);
        final StringBuffer out = new StringBuffer();
        try {
            Term term = null;
            do {
                term = parser.readTerm();
                if (term != null) {
                    out.append(term.getQedeq()).append("\n");
                    System.out.println(term.getQedeq());
                }
            } while (term != null || !parser.eof());
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.out.println(input.getRow() + ":" + input.getColumn() + ":");
            System.out.println(e.getMessage());
            System.out.println(input.getLine().replace('\t', ' ').replace('\015', ' '));
            final StringBuffer pointer = IoUtility.getSpaces(input.getColumn());
            pointer.append('^');
            System.out.println(pointer);
            throw e;
        }
/*        
        final  TextInput e = new TextInput(expected, "expected", "expected");
        final  TextInput o = new TextInput(out, "out", "out");
        int i = 0;
        do {
            System.out.println("comparing " + i);
            e.setRow(i);
            o.setRow(i);
            assertEquals(e.getLine(), o.getLine());
            i++;
        } while(!e.isEmpty());
*/        
        assertEquals(expected.toString(), out.toString());
    }
    
    protected void internalTest(int number) throws Exception {
        final TextInput input =  new TextInput(
            new StringBuffer(getTest()[number][0]));
        final MathParser parser = createParser(input);
        Term result = null;
        try {
            result = parser.readTerm();
        } catch (ParserException e) {
            e.printStackTrace(System.out);
            System.out.println(input.getRow() + ":" + input.getColumn() + ":");
            System.out.println(e.getMessage());
            System.out.println(input.getLine().replace('\t', ' ').replace('\015', ' '));
            final StringBuffer pointer = IoUtility.getSpaces(input.getColumn());
            pointer.append('^');
            System.out.println(pointer);
            throw e;
        }
        assertEquals(getTest()[number][1], result.getQedeq());
        assertEquals(0, parser.getRewindStackSize());
    }

}
