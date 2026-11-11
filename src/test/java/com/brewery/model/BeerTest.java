package com.brewery.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.brewery.TestBeers;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

class BeerTest {

    private static Beer stoutWith(Consumer<Beer> change) {
        Beer beer = TestBeers.stout();
        change.accept(beer);
        return beer;
    }

    @Test
    void newBeerIsEmpty() {
        Beer beer = new Beer();

        assertNull(beer.getId());
        assertNull(beer.getName());
        assertNull(beer.getType());
        assertFalse(beer.isAlcoholic());
        assertNull(beer.getManufacturer());
        assertTrue(beer.getIngredients().isEmpty());
        assertNull(beer.getChars());
    }

    @Test
    void settersChangeTheState() {
        Beer beer = TestBeers.stout();

        assertEquals("beer-10", beer.getId());
        assertEquals("Test Stout", beer.getName());
        assertEquals(BeerType.DARK, beer.getType());
        assertTrue(beer.isAlcoholic());
        assertEquals("Stout & Sons", beer.getManufacturer());
        assertEquals(List.of("water", "roasted malt", "hops"), beer.getIngredients());
        assertEquals(6.5, beer.getChars().getStrength());
    }

    @Test
    void ingredientsAreCopiedAndCannotBeModified() {
        List<String> source = new ArrayList<>(List.of("water", "malt"));
        Beer beer = new Beer();
        beer.setIngredients(source);

        source.add("sugar");

        assertEquals(List.of("water", "malt"), beer.getIngredients());
        List<String> ingredients = beer.getIngredients();
        assertThrows(UnsupportedOperationException.class, () -> ingredients.add("hops"));
    }

    @Test
    void beersWithTheSameStateAreEqual() {
        Beer beer = TestBeers.stout();

        assertEquals(beer, beer);
        assertEquals(beer, TestBeers.stout());
        assertEquals(beer.hashCode(), TestBeers.stout().hashCode());
    }

    @Test
    void beersDifferingInAnyFieldAreNotEqual() {
        Beer beer = TestBeers.stout();

        assertNotEquals(beer, stoutWith(b -> b.setId("beer-99")));
        assertNotEquals(beer, stoutWith(b -> b.setName("Other")));
        assertNotEquals(beer, stoutWith(b -> b.setType(BeerType.LIGHT)));
        assertNotEquals(beer, stoutWith(b -> b.setAlcoholic(false)));
        assertNotEquals(beer, stoutWith(b -> b.setManufacturer("Other")));
        assertNotEquals(beer, stoutWith(b -> b.setIngredients(List.of("water"))));
        assertNotEquals(beer, stoutWith(b -> b.setChars(new Chars())));
        assertNotEquals(beer, null);
        assertNotEquals(beer, "beer");
    }

    @Test
    void toStringContainsEveryField() {
        String text = TestBeers.stout().toString();

        assertEquals("Beer{id=beer-10, name='Test Stout', type=DARK, alcoholic=true, "
                + "manufacturer='Stout & Sons', ingredients=[water, roasted malt, hops], "
                + "chars={strength=6.5%, transparency=10%, filtered=false, nutritionalValue=60 kcal, "
                + "bottling=0.5 l, GLASS}}", text);
    }
}
