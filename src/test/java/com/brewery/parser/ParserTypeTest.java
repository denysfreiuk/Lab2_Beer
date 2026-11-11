package com.brewery.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import com.brewery.TestFiles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ParserTypeTest {

    @Test
    void createsParserOfTheMatchingTechnology() {
        assertInstanceOf(DomBeerParser.class, ParserType.DOM.createParser());
        assertInstanceOf(SaxBeerParser.class, ParserType.SAX.createParser());
        assertInstanceOf(StaxBeerParser.class, ParserType.STAX.createParser());
    }

    @Test
    void createsNewParserOnEveryCall() {
        assertNotSame(ParserType.DOM.createParser(), ParserType.DOM.createParser());
    }

    @ParameterizedTest
    @EnumSource(ParserType.class)
    void everyParserReadsTheSameCollection(ParserType type) {
        assertEquals(
                ParserType.DOM.createParser().parse(TestFiles.BEER_XML),
                type.createParser().parse(TestFiles.BEER_XML));
    }
}
