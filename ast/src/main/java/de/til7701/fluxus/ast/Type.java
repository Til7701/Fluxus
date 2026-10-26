package de.til7701.fluxus.ast;

import java.util.function.Function;

public sealed interface Type extends Node {

    Type mapNames(Function<String, String> nameMapper);

    String name();

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
        }
    }

}
