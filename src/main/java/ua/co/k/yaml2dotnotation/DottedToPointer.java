package ua.co.k.yaml2dotnotation;

import org.petitparser.context.Result;
import org.petitparser.tools.GrammarDefinition;
import org.petitparser.tools.GrammarParser;

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
            def("elements", ref("element").separatedBy(of(".")));
            def("element", anyOf(".").neg().star());
            // as.as.as

            ;

            action("elements", (Function<List, List<String>>) list -> {
//                    System.out.println(list.get(0).getClass()); // class java.util.ArrayList<Character>
//                    System.out.println(list.get(1).getClass()); // class java.lang.String
//                    System.out.println(list.get(2).getClass()); // class java.util.ArrayList<Character>

                Stream<String> stream = list.stream().flatMap((Function<Object, Stream<String>>) o -> {
                    if (o instanceof String) {
                        return Stream.empty();
                    } else {
                        List<Character> oo = (List<Character>) o;
                        String value = oo.stream().map(Object::toString).collect(Collectors.joining());
                        return Stream.of(value);
                    }
                });

                return stream.collect(Collectors.toList());
            });
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
        Result parseResult = parser.parse("as.bas");
        List<String> res = parseResult.get();
        System.out.println(res);
    }
}
