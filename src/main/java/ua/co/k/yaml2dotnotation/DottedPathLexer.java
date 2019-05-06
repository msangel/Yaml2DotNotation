package ua.co.k.yaml2dotnotation;

import com.fasterxml.jackson.core.JsonPointer;
import org.petitparser.tools.GrammarDefinition;
import org.petitparser.tools.GrammarParser;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.petitparser.parser.primitive.StringParser.of;

public class DottedPathLexer {

    private static String listOfCharactersToString(List<Character> oo) {
        String collected = oo.stream().map(Object::toString).collect(Collectors.joining());
        return collected;
    }

    // debugger helper
    // new holding().printer.accept(o, 1);
    private static class holding {
        public BiConsumer printer = new BiConsumer<Object, Integer>() {
            @Override
            public void accept(Object o, Integer deep) {
                String prefix = ((deep > 2) ? "  " : "") + String.join("", Collections.nCopies(deep, ">")) + " ";
                System.out.println(prefix + o.getClass() + "  " + o.toString());
                if (o instanceof List) {
                    ((List) o).forEach(new Consumer() {
                        @Override
                        public void accept(Object o) {
                            holding.this.printer.accept(o, deep + 1);
                        }
                    });
                }
                System.out.println();
            }
        };
    }

    private static class DottedGrammarDefinition extends GrammarDefinition {

        public DottedGrammarDefinition() {

            def("start", ref("elements").end());
            def("plain_el",
                    of(".").or(of("[")).neg().plus()
            );
            def("squared_el",
                    of("[")
                    .seq(of("'"))
                    .seq(ref("escaped_string"))
                    .seq(of("'"))
                    .seq(of("]"))
            );
            def("escaped_string", of("'").neg().star());

            def("elements",
                    ref("plain_el").or(ref("squared_el")) // first
                    .seq(
                            of(".").seq(ref("plain_el"))
                                    .or(ref("squared_el"))
                                    .star()
                    )
                    );

            action("plain_el", new Function<List<Character>, Object>() {
                @Override
                public Object apply(List<Character> o) {
                    return listOfCharactersToString(o);
                }
            });

            action("squared_el", new Function<List<Character>, Object>() {
                @Override
                public Object apply(List o) {
                    o.remove(0); // [
                    o.remove(0); // '
                    o.remove(o.size() - 1); // ]
                    o.remove(o.size() - 1); // '
                    return listOfCharactersToString((List<Character>)o.get(0));
                }
            });

            action("elements", new Function<List, Object>() {
                @Override
                public Object apply(List o) {
                    List<String> res = new ArrayList<>();
                    if (!o.isEmpty()) {
                        res.add((String)o.get(0));
                        List rest = (List)o.get(1);
                        if (!rest.isEmpty()) {
                            for (Object el: rest) {
                                if (el instanceof String) {
                                    res.add((String) el);
                                } else if (el instanceof List){
                                    res.add((String)((List) el).get(1));
                                } else {
                                    throw new RuntimeException("Parsed format changed(unexpected class):" + el.getClass());
                                }
                            }
                        }
                    }
                    return res;
                }
            });
        }
    }


    private static GrammarParser parser = new GrammarParser(new DottedGrammarDefinition());

    private final String source;

    public DottedPathLexer(String source) {
        this.source = source;
    }

    public JsonPointer convert() {
        List<String> tokens = parser.parse(source).get();
        String pointerPath = tokens.stream().map(new Function<String, String>() {
            @Override
            public String apply(String s) {
                // https://tools.ietf.org/html/draft-ietf-appsawg-json-pointer-03#section-3
                // If a reference token contains '~' (%x7E) or '/' (%x2F) characters,
                // they MUST be encoded as '~0' and '~1' respectively.
                return s.replaceAll("~", "~0").replaceAll("\\/", "~1");
            }
        }).collect(Collectors.joining("/", "/", ""));
        return JsonPointer.compile(pointerPath);
    }


    public static void main(String[] args) {
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
    }
}
