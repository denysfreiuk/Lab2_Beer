package com.brewery.parser;

class SaxBeerParserTest extends BeerParserContractTest {

    @Override
    protected BeerParser parser() {
        return new SaxBeerParser();
    }
}
