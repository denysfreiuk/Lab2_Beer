package com.brewery.parser;

class DomBeerParserTest extends BeerParserContractTest {

    @Override
    protected BeerParser parser() {
        return new DomBeerParser();
    }
}
