package ua.co.k.yaml2dotnotation;

import org.junit.Test;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class DottedPathLexerTest {

    String[] parse(String in) {
        DottedPathLexer lexer = new DottedPathLexer(in);
        return lexer.getTokens().toArray(new String[]{});
    }

    String [] newArray(String... in){
        return in;
    }

    void validateValid(String exp, String[] parts ) {
        String[] parse = parse(exp);
        assertArrayEquals(parts, parse);
    }


    @Test
    public void testIt() {

        validateValid("as.bas.das", newArray("as", "bas", "das"));

        String input;

        input = "as.bas.das";
        System.out.println(input);
        System.out.println(parser.parse(input).get().toString());
        // System.out.println(val);
        input = "['as'].bas.das";
        System.out.println(input);
        System.out.println(parser.parse(input).get().toString());
        // System.out.println(val);
        input = "as['bas'].das";
        System.out.println(input);
        System.out.println(parser.parse(input).get().toString());
        // System.out.println(val);
        input = "as.bas['das']";
        System.out.println(input);
        System.out.println(parser.parse(input).get().toString());
        // System.out.println(val);
        input = "as['bas']['das']";
        System.out.println(input);
        System.out.println(parser.parse(input).get().toString());

        input = "a";
        System.out.println(input);
        System.out.println(parser.parse(input).get().toString());

        input = "a.b";
        System.out.println(input);
        System.out.println(parser.parse(input).get().toString());

        input = "a['b']";
        System.out.println(input);
        System.out.println(parser.parse(input).get().toString());

        input = "['a'].b";
        System.out.println(input);
        System.out.println(parser.parse(input).get().toString());

        input = "['a']['b']";
        System.out.println(input);
        System.out.println(parser.parse(input).get().toString());


        input = "['a']['b']['c']";
        System.out.println(input);
        System.out.println(parser.parse(input).get().toString());

        input = "a['b'].c.ddd";
        System.out.println(input);
        System.out.println(parser.parse(input).get().toString());

        input = "a['b\\'s'].c.ddd";
        System.out.println(input);
        System.out.println(parser.parse(input).get().toString());
        input = "a['b\\\\\\'s'].c.ddd";
        System.out.println(input);
        System.out.println(parser.parse(input).get().toString());
    }

}
