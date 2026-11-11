package com.brewery.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.brewery.TestBeers;
import com.brewery.model.Beer;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BeerEventAssemblerTest {

    private BeerEventAssembler assembler;

    @BeforeEach
    void setUp() {
        assembler = new BeerEventAssembler();
    }

    private void start(String name) {
        start(name, Map.of());
    }

    private void start(String name, Map<String, String> attributes) {
        assembler.startElement(name, attributes::get);
    }

    private void element(String name, String text) {
        start(name);
        assembler.characters(text);
        assembler.endElement(name);
    }

    private void stout() {
        start("BeerItem", Map.of("id", "beer-10"));
        element("Name", "Test Stout");
        element("Type", "dark");
        element("Al", "true");
        element("Manufacturer", "Stout & Sons");
        start("Ingredients");
        element("Ingredient", "water");
        element("Ingredient", "roasted malt");
        element("Ingredient", "hops");
        assembler.endElement("Ingredients");
        start("Chars");
        element("Strength", "6.5");
        element("Transparency", "10");
        element("Filtered", "false");
        element("NutritionalValue", "60");
        start("Bottling", Map.of("volume", "0.5", "material", "glass"));
        assembler.endElement("Bottling");
        assembler.endElement("Chars");
        assembler.endElement("BeerItem");
    }

    @Test
    void hasNoBeersBeforeAnyEvent() {
        assertTrue(assembler.beers().isEmpty());
    }

    @Test
    void assemblesBeerFromEvents() {
        start("Beer");
        stout();
        assembler.endElement("Beer");

        assertEquals(List.of(TestBeers.stout()), assembler.beers());
    }

    @Test
    void assemblesSeveralBeersInOrder() {
        start("Beer");
        stout();
        stout();
        assembler.endElement("Beer");

        assertEquals(List.of(TestBeers.stout(), TestBeers.stout()), assembler.beers());
    }

    @Test
    void joinsTextDeliveredInSeveralChunks() {
        start("BeerItem", Map.of("id", "beer-1"));
        start("Name");
        assembler.characters("Test ");
        assembler.characters("Sto");
        assembler.characters("ut");
        assembler.endElement("Name");
        element("Type", "dark");
        element("Manufacturer", "Nobody");
        start("Bottling", Map.of("volume", "0.5", "material", "glass"));
        assembler.endElement("Bottling");
        assembler.endElement("BeerItem");

        assertEquals("Test Stout", assembler.beers().get(0).getName());
    }

    @Test
    void ignoresWhitespaceBetweenElements() {
        start("BeerItem", Map.of("id", "beer-1"));
        assembler.characters("\n        ");
        element("Name", "Test Stout");
        assembler.characters("\n        ");
        element("Type", "dark");
        element("Manufacturer", "Nobody");
        start("Bottling", Map.of("volume", "0.5", "material", "glass"));
        assembler.endElement("Bottling");
        assembler.characters("\n    ");
        assembler.endElement("BeerItem");

        Beer beer = assembler.beers().get(0);
        assertEquals("Test Stout", beer.getName());
        assertEquals("Nobody", beer.getManufacturer());
    }

    @Test
    void ignoresUnknownElements() {
        start("BeerItem", Map.of("id", "beer-1"));
        element("Name", "Test Stout");
        element("Rating", "5");
        element("Type", "dark");
        element("Manufacturer", "Nobody");
        start("Bottling", Map.of("volume", "0.5", "material", "glass"));
        assembler.endElement("Bottling");
        assembler.endElement("BeerItem");

        assertEquals(1, assembler.beers().size());
        assertEquals("Test Stout", assembler.beers().get(0).getName());
    }

    @Test
    void rejectsBeerItemWithoutId() {
        assertThrows(IllegalArgumentException.class, () -> start("BeerItem"));
    }

    @Test
    void rejectsDataOutsideOfBeerItem() {
        start("Beer");

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> element("Name", "Lost"));
        assertTrue(e.getMessage().contains("<Name>"), e.getMessage());

        assertThrows(IllegalArgumentException.class,
                () -> start("Bottling", Map.of("volume", "0.5", "material", "glass")));
        assertThrows(IllegalArgumentException.class, () -> assembler.endElement("BeerItem"));
    }
}
