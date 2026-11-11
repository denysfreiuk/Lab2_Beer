package com.brewery.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.brewery.TestBeers;
import com.brewery.TestFiles;
import com.brewery.model.Beer;
import com.brewery.model.BeerType;
import com.brewery.model.Bottling;
import com.brewery.model.ContainerMaterial;
import com.brewery.xml.XmlProcessingException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * The behaviour every {@link BeerParser} must have, whatever technology it uses.
 * Each parser gets a subclass that only supplies the implementation under test.
 */
abstract class BeerParserContractTest {

    protected abstract BeerParser parser();

    @Test
    void readsBeersInDocumentOrder() {
        List<Beer> beers = parser().parse(TestFiles.VALID);

        assertEquals(List.of("beer-10", "beer-11", "beer-12"), beers.stream().map(Beer::getId).toList());
    }

    @Test
    void readsEveryFieldOfBeer() {
        Beer stout = parser().parse(TestFiles.VALID).get(0);

        assertEquals(TestBeers.stout(), stout);
    }

    @Test
    void readsNonAlcoholicBeerWithoutStrength() {
        Beer zero = parser().parse(TestFiles.VALID).get(1);

        assertEquals("Zero Lager", zero.getName());
        assertEquals(BeerType.LAGER, zero.getType());
        assertFalse(zero.isAlcoholic());
        assertNull(zero.getChars().getStrength());
        assertTrue(zero.getChars().isFiltered());
        assertEquals(List.of("water", "malt"), zero.getIngredients());
        assertEquals(new Bottling(0.33, ContainerMaterial.ALUMINIUM), zero.getChars().getBottling());
    }

    @Test
    void trimsWhitespaceAroundText() {
        Beer amber = parser().parse(TestFiles.VALID).get(2);

        assertEquals("Amber Live", amber.getName());
        assertEquals(5.2, amber.getChars().getStrength());
        assertEquals(new Bottling(20, ContainerMaterial.STEEL), amber.getChars().getBottling());
    }

    @Test
    void readsTheMainDocument() {
        List<Beer> beers = parser().parse(TestFiles.BEER_XML);

        assertEquals(8, beers.size());
        assertEquals(8, beers.stream().map(Beer::getId).distinct().count());
        for (Beer beer : beers) {
            assertNotNull(beer.getName());
            assertNotNull(beer.getType());
            assertNotNull(beer.getManufacturer());
            assertTrue(beer.getIngredients().size() >= 2, beer.getName());
            assertNotNull(beer.getChars().getBottling());
            assertEquals(beer.isAlcoholic(), beer.getChars().getStrength() != null, beer.getName());
        }
    }

    @Test
    void failsOnMissingFile(@TempDir Path dir) {
        Path missing = dir.resolve("missing.xml");

        assertThrows(XmlProcessingException.class, () -> parser().parse(missing));
    }

    @Test
    void failsOnMalformedDocument() {
        XmlProcessingException e = assertThrows(XmlProcessingException.class,
                () -> parser().parse(TestFiles.MALFORMED));

        assertTrue(e.getMessage().contains("beers-malformed.xml"), e.getMessage());
        assertNotNull(e.getCause());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "beers-bad-number.xml", "beers-no-bottling.xml", "beers-no-material.xml", "beers-no-id.xml"})
    void failsOnBrokenData(String document) {
        Path xml = TestFiles.resource(document);

        XmlProcessingException e = assertThrows(XmlProcessingException.class, () -> parser().parse(xml));

        assertTrue(e.getCause() instanceof IllegalArgumentException, String.valueOf(e.getCause()));
    }

    @Test
    void rejectsDocumentWithDoctype() {
        Path xml = TestFiles.resource("beers-doctype.xml");

        assertThrows(XmlProcessingException.class, () -> parser().parse(xml));
    }
}
