package ua.co.k.yaml2dotnotation;

import org.petitparser.tools.GrammarDefinition;
import org.petitparser.tools.GrammarParser;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

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
                    )
                    );

            //
            // as.as.as        // {as} {.} {as} {.} {as}
            // as['as'].as     // {as}
            // as['as']['as']

            // ['as'] .as
            // ['as'] ['as']


            action("elements", new Function<List, Object>() {
                @Override
                public Object apply(List o) {
                    List<String> res = new ArrayList<>();

                    if (!o.isEmpty()) {
                        res.add(getFirstEl(o.get(0)));

                        Object second = o.get(1);
                        if (!isSecondEmpty(second)) {
                            res.addAll(getAllFromSecond(second));
                        }


                        class holding {
                            public BiConsumer printer = new BiConsumer<Object, Integer>() {
                                @Override
                                public void accept(Object o, Integer deep) {
                                    String prefix = ((deep>2)?"  ":"") + String.join("", Collections.nCopies(deep, ">"))+ " ";
                                    System.out.println(prefix + o.getClass() + "  " + o.toString());
                                    if (o instanceof List) {
                                        ((List) o).forEach(new Consumer() {
                                            @Override
                                            public void accept(Object o) {
                                                holding.this.printer.accept(o, deep+1);
                                            }
                                        });
                                    }

                                }
                            };
                        }

                        o.forEach(new Consumer() {
                            @Override
                            public void accept(Object o) {
                                new holding().printer.accept(o,1);
                                System.out.println();
                            }
                        });

                    } else {
                        return new ArrayList<String>();
                    }
                    System.out.println();
                    return res;
                }

                private List<String> getAllFromSecond(Object o) {
                    List<String> res = new ArrayList<>();
                    return res;
                }

                private boolean isSecondEmpty(Object in) {
// a['b'].c
//> class java.util.ArrayList  [a]
//>> class java.lang.Character  a
//
//> class java.util.ArrayList  [[[, ', [b], ', ]], [., [c]]]
//>> class java.util.ArrayList  [[, ', [b], ', ]]
//  >>> class java.lang.String  [                      <- string
//  >>> class java.lang.String  '                      <- string
//  >>> class java.util.ArrayList  [b]
//  >>>> class java.lang.Character  b
//  >>> class java.lang.String  '
//  >>> class java.lang.String  ]
//>> class java.util.ArrayList  [., [c]]
//  >>> class java.lang.String  .                      <- string
//  >>> class java.util.ArrayList  [c]                 <- array
//  >>>> class java.lang.Character  c


                    return ((List) in).isEmpty();
                }

                private String getFirstEl(Object in) {
                    List o = (List) in;
                    Object o1 = o.get(0);
                    String res = null;
                    if (o1 instanceof Character) { // plain_el
                        res = listOfCharactersToString((List<Character>) in);
                    } else if (o1 instanceof String) { // squared_el
                        List oo = new ArrayList(o);
                        oo.remove(0); // [
                        oo.remove(0); // '
                        oo.remove(oo.size()-1); // ]
                        oo.remove(oo.size()-1); // '
                        res = listOfCharactersToString((List<Character>) oo.get(0));
                    } else {
                        throw new RuntimeException("type " + o1.getClass() + " is not supported as first var value");
                    }
                    return res;
                }

                private String listOfCharactersToString(List<Character> oo) {
                    return oo.stream().map(Object::toString).collect(Collectors.joining());
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
        String input;
        String val;

//        input = "as.bas.das";
//        System.out.println(input);
//        val = parser.parse(input).get().toString();
//        // System.out.println(val);
//        input = "['as'].bas.das";
//        System.out.println(input);
//        val = parser.parse(input).get().toString();
//        // System.out.println(val);
//        input = "as['bas'].das";
//        System.out.println(input);
//        val = parser.parse(input).get().toString();
//        // System.out.println(val);
//        input = "as.bas['das']";
//        System.out.println(input);
//        val = parser.parse(input).get().toString();
//        // System.out.println(val);
//        input = "as['bas']['das']";
//        System.out.println(input);
//        val = parser.parse(input).get().toString();
//        // System.out.println(val);
//
//        input = "a";
//        System.out.println(input);
//        parser.parse(input).get();

        input = "a.b";
        System.out.println(input);
        parser.parse(input).get();

        input = "a['b']";
        System.out.println(input);
        parser.parse(input).get();

        input = "['a'].b";
        System.out.println(input);
        parser.parse(input).get();

        input = "['a']['b']";
        System.out.println(input);
        parser.parse(input).get();


        input = "['a']['b']['c']";
        System.out.println(input);
        parser.parse(input).get();

        input = "a['b'].c";
        System.out.println(input);
        parser.parse(input).get();
    }
}
