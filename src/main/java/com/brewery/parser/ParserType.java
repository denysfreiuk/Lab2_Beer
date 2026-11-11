package com.brewery.parser;

import java.util.function.Supplier;

/** Available parsing technologies; a simple factory of {@link BeerParser} implementations. */
public enum ParserType {
    DOM(DomBeerParser::new),
    SAX(SaxBeerParser::new),
    STAX(StaxBeerParser::new);

    private final Supplier<BeerParser> factory;

    ParserType(Supplier<BeerParser> factory) {
        this.factory = factory;
    }

    /** @return a new parser of this type */
    public BeerParser createParser() {
        return factory.get();
    }
}
