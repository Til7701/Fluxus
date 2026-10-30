package de.til7701.fluxus.ast;

import java.util.function.Function;

public sealed interface Type extends Node {

    Type mapNames(Function<String, String> nameMapper);

    String name();

    default String simpleName() {
        String name = name();
        if (name.contains("."))
            return name.substring(name.lastIndexOf('.') + 1);
        else
            return name;
    }

    String toCode();

    record Simple(Span span,
                  String name
    ) implements Type {
        @Override
        public Type mapNames(Function<String, String> nameMapper) {
            return new Simple(
                    span,
                    nameMapper.apply(name)
            );
        }

        @Override
        public String toCode() {
            return simpleName();
        }
    }

    sealed interface Trait extends Type {
        record Collection(Span span,
                          Type indexType, Type elementType
        ) implements Trait {
            @Override
            public Type mapNames(Function<String, String> nameMapper) {
                return new Collection(
                        span,
                        indexType.mapNames(nameMapper),
                        elementType.mapNames(nameMapper)
                );
            }

            @Override
            public String name() {
                return elementType.name() + "[" + indexType.name() + "]";
            }

            @Override
            public String toCode() {
                return elementType.toCode() + "[" + indexType.toCode() + "]";
            }
        }

        record I(Span span,
                 long bitCount
        ) implements Trait {
            @Override
            public Type mapNames(Function<String, String> nameMapper) {
                return this;
            }

            @Override
            public String name() {
                return "fluxus.fons.I";
            }

            @Override
            public String toCode() {
                return "I" + bitCount;
            }
        }

        record U(Span span,
                 long bitCount
        ) implements Trait {
            @Override
            public Type mapNames(Function<String, String> nameMapper) {
                return this;
            }

            @Override
            public String name() {
                return "fluxus.fons.U";
            }

            @Override
            public String toCode() {
                return "U" + bitCount;
            }
        }

        record F(Span span,
                 long bitCount
        ) implements Trait {
            @Override
            public Type mapNames(Function<String, String> nameMapper) {
                return this;
            }

            @Override
            public String name() {
                return "fluxus.fons.F";
            }

            @Override
            public String toCode() {
                return "F" + bitCount;
            }
        }
    }

}
