package ua.co.k.yaml2dotnotation;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

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

    void validateInvalid(String exp) {
        try {
            parse(exp);
            fail();
        } catch (Exception e) {}
    }


    @Test
    public void testIt() {

        validateValid("as.bas.das", newArray("as", "bas", "das"));
        validateValid("['as'].bas.das", newArray("as", "bas", "das"));
        validateValid("as['bas'].das", newArray("as", "bas", "das"));
        validateValid("as.bas['das']", newArray("as", "bas", "das"));
        validateValid("as['bas']['das']", newArray("as", "bas", "das"));
        validateValid("['as']['bas']['das']", newArray("as", "bas", "das"));
        validateValid("['as'].bas['das']", newArray("as", "bas", "das"));
        validateValid("a", newArray("a"));
        validateValid("['a']", newArray("a"));
        validateValid("a.b", newArray("a", "b"));
        validateValid("a['b']", newArray("a", "b"));
        validateValid("['a'].b", newArray("a", "b"));
        validateValid("['a']['b']", newArray("a", "b"));
        validateValid("['a']['b']['c']", newArray("a", "b", "c"));
        validateValid("a['b'].c.ddd", newArray("a", "b", "c", "ddd"));
        validateValid("a['b\\'s'].c.ddd", newArray("a", "b's", "c", "ddd"));
        validateValid("a['b\\\\\\'s'].c.ddd", newArray("a", "b\\'s", "c", "ddd"));
        validateValid("a['bs\\''].c.ddd", newArray("a", "bs'", "c", "ddd"));
        validateInvalid("a['bs\\\\''].c.ddd");
    }

}
