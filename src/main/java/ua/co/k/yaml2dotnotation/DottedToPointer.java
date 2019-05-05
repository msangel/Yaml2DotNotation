package ua.co.k.yaml2dotnotation;

import org.petitparser.context.Result;
import org.petitparser.tools.GrammarDefinition;
import org.petitparser.tools.GrammarParser;
import org.petitparser.utils.Functions;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.petitparser.parser.primitive.CharacterParser.anyOf;
import static org.petitparser.parser.primitive.StringParser.of;

public class DottedToPointer {
    private static class DottedGrammarDefinition extends GrammarDefinition {

        public DottedGrammarDefinition() {

            // http://pharobooks.gforge.inria.fr/PharoByExampleTwo-Eng/latest/PetitParser.pdf
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
                    ));

            //
            // as.as.as        // {as} {.} {as} {.} {as}
            // as['as'].as     // {as}
            // as['as']['as']

            // ['as'] .as
            // ['as'] ['as']


            action("elements", new Function<List, Object>() {
                @Override
                public Object apply(List o) {

                    if (!o.isEmpty()) {
                        System.out.println(o.getClass());
                        o.forEach(new Consumer() {
                            @Override
                            public void accept(Object o) {
                                System.out.println(">" + o.getClass() + "  " + o.toString());
                            }
                        });

                    } else {
                        System.out.println(o.getClass() + " (empty)");
                    }
                    System.out.println();
                    return "";
                }
            });
        }
    }


    private static GrammarParser parser = new GrammarParser(new DottedGrammarDefinition());

    private final String source;

    public DottedToPointer(String source) {
        this.source = source;
    }

    public String convert() {
        return parser.parse(source).get();
    }


    public static void main(String[] args) {
        String input = "as.bas.das";
        System.out.println(input);
        String val = parser.parse(input).get().toString();
        // System.out.println(val);
        input = "['as'].bas.das";
        System.out.println(input);
        val = parser.parse(input).get().toString();
        // System.out.println(val);
        input = "as['bas'].das";
        System.out.println(input);
        val = parser.parse(input).get().toString();
        // System.out.println(val);
        input = "as.bas['das']";
        System.out.println(input);
        val = parser.parse(input).get().toString();
        // System.out.println(val);
        input = "as['bas']['das']";
        System.out.println(input);
        val = parser.parse(input).get().toString();
        // System.out.println(val);
    }
}
