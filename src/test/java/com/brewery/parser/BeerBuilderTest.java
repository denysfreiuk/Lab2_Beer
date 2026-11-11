package com.brewery.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.brewery.TestBeers;
import com.brewery.model.Beer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class BeerBuilderTest {

    /** A builder with all mandatory elements supplied. */
    private static BeerBuilder minimal() {
        return new BeerBuilder("beer-1")
                .text(BeerXmlTag.NAME, "Minimal")
                .text(BeerXmlTag.TYPE, "light")
                .text(BeerXmlTag.MANUFACTURER, "Nobody")
                .bottling("0.5", "glass");
    }

    @Test
    void buildsBeerFromRawStrings() {
        Beer beer = new BeerBuilder("beer-10")
                .text(BeerXmlTag.NAME, "Test Stout")
                .text(BeerXmlTag.TYPE, "dark")
                .text(BeerXmlTag.AL, "true")
                .text(BeerXmlTag.MANUFACTURER, "Stout & Sons")
                .text(BeerXmlTag.INGREDIENT, "water")
                .text(BeerXmlTag.INGREDIENT, "roasted malt")
                .text(BeerXmlTag.INGREDIENT, "hops")
                .text(BeerXmlTag.STRENGTH, "6.5")
                .text(BeerXmlTag.TRANSPARENCY, "10")
                .text(BeerXmlTag.FILTERED, "false")
                .text(BeerXmlTag.NUTRITIONAL_VALUE, "60")
                .bottling("0.5", "glass")
                .build();

        assertEquals(TestBeers.stout(), beer);
    }

    @Test
    void stripsWhitespaceAroundValues() {
        Beer beer = new BeerBuilder("  beer-10 ")
                .text(BeerXmlTag.NAME, "\n    Test Stout\n  ")
                .text(BeerXmlTag.TYPE, " dark ")
                .text(BeerXmlTag.MANUFACTURER, "\tNobody")
                .text(BeerXmlTag.TRANSPARENCY, " 10 ")
                .bottling(" 0.5 ", " glass ")
                .build();

        assertEquals("beer-10", beer.getId());
        assertEquals("Test Stout", beer.getName());
        assertEquals("Nobody", beer.getManufacturer());
        assertEquals(10, beer.getChars().getTransparency());
        assertEquals(0.5, beer.getChars().getBottling().getVolume());
    }

    @Test
    void strengthStaysEmptyWhenNotSupplied() {
        assertNull(minimal().build().getChars().getStrength());
    }

    @ParameterizedTest
    @CsvSource({"true, true", "1, true", "false, false", "0, false"})
    void understandsAllFormsOfXsBoolean(String text, boolean expected) {
        Beer beer = minimal()
                .text(BeerXmlTag.AL, text)
                .text(BeerXmlTag.FILTERED, text)
                .build();

        assertEquals(expected, beer.isAlcoholic());
        assertEquals(expected, beer.getChars().isFiltered());
    }

    @Test
    void booleanFlagsAreFalseByDefault() {
        Beer beer = minimal().build();

        assertFalse(beer.isAlcoholic());
        assertFalse(beer.getChars().isFiltered());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void rejectsMissingId(String id) {
        assertThrows(IllegalArgumentException.class, () -> new BeerBuilder(id));
    }

    @Test
    void rejectsValuesOfWrongType() {
        BeerBuilder builder = minimal();

        assertThrows(IllegalArgumentException.class, () -> builder.text(BeerXmlTag.TYPE, "strong"));
        assertThrows(IllegalArgumentException.class, () -> builder.text(BeerXmlTag.AL, "yes"));
        assertThrows(IllegalArgumentException.class, () -> builder.text(BeerXmlTag.STRENGTH, "strong"));
        assertThrows(IllegalArgumentException.class, () -> builder.text(BeerXmlTag.TRANSPARENCY, "4.5"));
        assertThrows(IllegalArgumentException.class, () -> builder.text(BeerXmlTag.NUTRITIONAL_VALUE, ""));
        assertThrows(IllegalArgumentException.class, () -> builder.bottling("half", "glass"));
        assertThrows(IllegalArgumentException.class, () -> builder.bottling("0.5", "wood"));
    }

    @ParameterizedTest
    @EnumSource(value = BeerXmlTag.class, names = {"BEER", "BEER_ITEM", "INGREDIENTS", "CHARS", "BOTTLING"})
    void rejectsTextForContainerElements(BeerXmlTag tag) {
        BeerBuilder builder = minimal();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> builder.text(tag, "text"));

        assertTrue(e.getMessage().contains(tag.xmlName()));
    }

    @Test
    void rejectsBottlingWithoutAttributes() {
        BeerBuilder builder = minimal();

        assertThrows(IllegalArgumentException.class, () -> builder.bottling(null, "glass"));
        assertThrows(IllegalArgumentException.class, () -> builder.bottling("0.5", null));
    }

    @Test
    void rejectsBeerWithoutMandatoryElements() {
        assertMissing("Name", new BeerBuilder("beer-1")
                .text(BeerXmlTag.TYPE, "light")
                .text(BeerXmlTag.MANUFACTURER, "Nobody")
                .bottling("0.5", "glass"));
        assertMissing("Type", new BeerBuilder("beer-1")
                .text(BeerXmlTag.NAME, "Minimal")
                .text(BeerXmlTag.MANUFACTURER, "Nobody")
                .bottling("0.5", "glass"));
        assertMissing("Manufacturer", new BeerBuilder("beer-1")
                .text(BeerXmlTag.NAME, "Minimal")
                .text(BeerXmlTag.TYPE, "light")
                .bottling("0.5", "glass"));
        assertMissing("Bottling", new BeerBuilder("beer-1")
                .text(BeerXmlTag.NAME, "Minimal")
                .text(BeerXmlTag.TYPE, "light")
                .text(BeerXmlTag.MANUFACTURER, "Nobody"));
    }

    private static void assertMissing(String element, BeerBuilder builder) {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, builder::build);

        assertTrue(e.getMessage().contains("<" + element + ">"), e.getMessage());
        assertTrue(e.getMessage().contains("beer-1"), e.getMessage());
    }
}
