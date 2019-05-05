package ua.co.k.yaml2dotnotation;

import org.petitparser.context.Result;
import org.petitparser.tools.GrammarDefinition;
import org.petitparser.tools.GrammarParser;
import org.petitparser.utils.Functions;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.petitparser.parser.primitive.CharacterParser.anyOf;
import static org.petitparser.parser.primitive.StringParser.of;

public class DottedToPointer {
    private static class DottedPathParser extends GrammarParser {

        public DottedPathParser() {
            super(new DottedGrammarDefinition());
        }
    }

    private static class DottedGrammarDefinition extends GrammarDefinition {

        public DottedGrammarDefinition() {
            def("start", ref("elements").end());
            def("element", ref("separator").neg().star());
            def("elements", ref("element").seq(ref("separator").seq(ref("element")).star()));

            //
            def("separator", of("."));
            // as.as.as        // {as} {.} {as} {.} {as}
            // as['as'].as     // {as}
            // as['as']['as']


//            action("elements", Functions.withoutSeparators().andThen((Function<List, List<String>>) list -> {
//                Function<List<Character>, String> listToString = o -> o.stream().map(Object::toString).collect(Collectors.joining());
//                return ((List<List<Character>>)list).stream().map(listToString).collect(Collectors.toList());
//            }));
        }
    }


    private static DottedPathParser parser = new DottedPathParser();

    private final String source;

    public DottedToPointer(String source) {
        this.source = source;
    }

    public String convert() {
        return parser.parse(source).get();
    }


    public static void main(String[] args) {
        Result parseResult = parser.parse("as.bas.das");
        Object res = parseResult.get();
        System.out.println(res);
    }
}
