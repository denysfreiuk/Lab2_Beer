package com.brewery.parser;

class StaxBeerParserTest extends BeerParserContractTest {

    @Override
    protected BeerParser parser() {
        return new StaxBeerParser();
    }
}
