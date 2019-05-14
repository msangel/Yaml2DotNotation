package ua.co.k.yaml2dotnotation;

import com.fasterxml.jackson.core.JsonPointer;
import org.petitparser.tools.GrammarDefinition;
import org.petitparser.tools.GrammarParser;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.petitparser.parser.primitive.CharacterParser.anyOf;
import static org.petitparser.parser.primitive.StringParser.of;

public class DottedPathLexer {

    final static boolean doDebugPrint = false;

    private static String listToString(List oo) {
        Stream<String> stream = oo.stream().map((Function<Object, String>) o -> {
            if(o instanceof Character) {
                return o.toString();
            } else if (o instanceof String) {
                if("\\'".equalsIgnoreCase((String) o)) {
                    return "'";
                } else if("\\\\".equalsIgnoreCase((String) o)) {
                    return "\\";
                } else {
                    return (String) o;
                }
            }
            throw new RuntimeException("unknown type of object: " + o.getClass());
        });
        return stream.collect(Collectors.joining());
    }

    private static class holding {
        private final String name;

        public holding(String name){
            this.name = name;
        }
        public void inspect(Object o) {
            if(doDebugPrint) {
                System.out.println("Working on: " + name);
                this.printer.accept(o, 0);
            }
        }
        private BiConsumer printer = new BiConsumer<Object, Integer>() {
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

            def("escaped_string",
                    of("\\\\")
                    .or(of("\\'"))
                    .or(of("'").neg())
                    .star());

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
                    new holding("plain_el").inspect(o);
                    return listToString(o);
                }
            });

            action("squared_el", new Function<List<Character>, Object>() {
                @Override
                public Object apply(List o) {
                    new holding("squared_el").inspect(o);
                    o.remove(0); // [
                    o.remove(0); // '
                    o.remove(o.size() - 1); // ]
                    o.remove(o.size() - 1); // '
                    return listToString((List<Character>)o.get(0));
                }
            });

            action("elements", new Function<List, Object>() {
                @Override
                public Object apply(List o) {
                    new holding("elements").inspect(o);
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
        List<String> tokens = getTokens();
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

    /* package */ List<String> getTokens() {
        return parser.parse(source).get();
    }
}
