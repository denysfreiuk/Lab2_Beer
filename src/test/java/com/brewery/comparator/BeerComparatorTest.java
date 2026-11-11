package com.brewery.comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.brewery.TestBeers;
import com.brewery.TestFiles;
import com.brewery.model.Beer;
import com.brewery.model.BeerType;
import com.brewery.parser.DomBeerParser;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class BeerComparatorTest {

    private static final Beer PORTER = TestBeers.beer("Porter", "Alpha Brewery", BeerType.DARK, 6.5, 60);
    private static final Beer ZERO = TestBeers.beer("zero", "Charlie Brewery", BeerType.LIGHT, null, 25);
    private static final Beer PILS = TestBeers.beer("Pils", "bravo brewery", BeerType.LAGER, 4.4, 43);
    private static final Beer ALE = TestBeers.beer("Ale", "Alpha Brewery", BeerType.LIGHT, 4.4, 43);

    private static List<String> namesSortedBy(Comparator<Beer> order) {
        List<Beer> beers = new ArrayList<>(List.of(PORTER, ZERO, PILS, ALE));
        beers.sort(order);
        return beers.stream().map(Beer::getName).toList();
    }

    @Test
    void byNameSortsAlphabeticallyIgnoringCase() {
        assertEquals(List.of("Ale", "Pils", "Porter", "zero"), namesSortedBy(BeerComparator.BY_NAME));
    }

    @Test
    void byManufacturerIgnoresCaseAndThenSortsByName() {
        assertEquals(List.of("Ale", "Porter", "Pils", "zero"), namesSortedBy(BeerComparator.BY_MANUFACTURER));
    }

    @Test
    void byTypeFollowsDeclarationOrderAndThenSortsByName() {
        assertEquals(List.of("Porter", "Ale", "zero", "Pils"), namesSortedBy(BeerComparator.BY_TYPE));
    }

    @Test
    void byStrengthPutsBeerWithoutStrengthFirst() {
        assertEquals(List.of("zero", "Ale", "Pils", "Porter"), namesSortedBy(BeerComparator.BY_STRENGTH));
    }

    @Test
    void byNutritionalValueSortsAscendingAndThenByName() {
        assertEquals(List.of("zero", "Ale", "Pils", "Porter"), namesSortedBy(BeerComparator.BY_NUTRITIONAL_VALUE));
    }

    @Test
    void comparatorsCanBeReversed() {
        assertEquals(List.of("zero", "Porter", "Pils", "Ale"), namesSortedBy(BeerComparator.BY_NAME.reversed()));
    }

    @ParameterizedTest
    @EnumSource(BeerComparator.class)
    void beerIsEqualToItself(BeerComparator order) {
        assertEquals(0, order.compare(PORTER, PORTER));
    }

    @ParameterizedTest
    @EnumSource(BeerComparator.class)
    void comparisonIsAntisymmetric(BeerComparator order) {
        assertEquals(-Integer.signum(order.compare(PORTER, ZERO)), Integer.signum(order.compare(ZERO, PORTER)));
    }

    @ParameterizedTest
    @EnumSource(BeerComparator.class)
    void sortsTheMainDocument(BeerComparator order) {
        List<Beer> beers = new ArrayList<>(new DomBeerParser().parse(TestFiles.BEER_XML));

        beers.sort(order);

        for (int i = 1; i < beers.size(); i++) {
            assertTrue(order.compare(beers.get(i - 1), beers.get(i)) <= 0, order + " at " + i);
        }
    }
}
