package com.brewery.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class BeerXmlTagTest {

    @ParameterizedTest
    @EnumSource(BeerXmlTag.class)
    void everyTagIsFoundByItsXmlName(BeerXmlTag tag) {
        assertEquals(Optional.of(tag), BeerXmlTag.fromName(tag.xmlName()));
    }

    @Test
    void unknownNameGivesEmptyResult() {
        assertTrue(BeerXmlTag.fromName("Wine").isEmpty());
        assertTrue(BeerXmlTag.fromName("beeritem").isEmpty());
    }

    @Test
    void xmlNamesMatchTheSchema() {
        assertEquals("Beer", BeerXmlTag.BEER.xmlName());
        assertEquals("BeerItem", BeerXmlTag.BEER_ITEM.xmlName());
        assertEquals("NutritionalValue", BeerXmlTag.NUTRITIONAL_VALUE.xmlName());
        assertEquals("id", BeerXmlTag.ATTR_ID);
        assertEquals("volume", BeerXmlTag.ATTR_VOLUME);
        assertEquals("material", BeerXmlTag.ATTR_MATERIAL);
    }

    @Test
    void textTagsAreTheLeafElementsInDocumentOrder() {
        assertEquals(List.of(
                BeerXmlTag.NAME,
                BeerXmlTag.TYPE,
                BeerXmlTag.AL,
                BeerXmlTag.MANUFACTURER,
                BeerXmlTag.INGREDIENT,
                BeerXmlTag.STRENGTH,
                BeerXmlTag.TRANSPARENCY,
                BeerXmlTag.FILTERED,
                BeerXmlTag.NUTRITIONAL_VALUE), BeerXmlTag.textTags());
    }

    @Test
    void containerElementsHoldNoText() {
        assertFalse(BeerXmlTag.BEER.hasText());
        assertFalse(BeerXmlTag.BEER_ITEM.hasText());
        assertFalse(BeerXmlTag.INGREDIENTS.hasText());
        assertFalse(BeerXmlTag.CHARS.hasText());
        assertFalse(BeerXmlTag.BOTTLING.hasText());
        assertTrue(BeerXmlTag.NAME.hasText());
    }
}
