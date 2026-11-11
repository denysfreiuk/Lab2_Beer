package com.brewery;

import com.brewery.model.Beer;
import com.brewery.model.BeerType;
import com.brewery.model.Bottling;
import com.brewery.model.Chars;
import com.brewery.model.ContainerMaterial;
import java.util.List;

/** Creates beers for the tests. */
public final class TestBeers {

    private TestBeers() {
    }

    /** @return the first beer of {@code beers-valid.xml} */
    public static Beer stout() {
        Chars chars = new Chars();
        chars.setStrength(6.5);
        chars.setTransparency(10);
        chars.setFiltered(false);
        chars.setNutritionalValue(60);
        chars.setBottling(new Bottling(0.5, ContainerMaterial.GLASS));

        Beer beer = new Beer();
        beer.setId("beer-10");
        beer.setName("Test Stout");
        beer.setType(BeerType.DARK);
        beer.setAlcoholic(true);
        beer.setManufacturer("Stout & Sons");
        beer.setIngredients(List.of("water", "roasted malt", "hops"));
        beer.setChars(chars);
        return beer;
    }

    /** @return a beer with only the fields the comparators look at */
    public static Beer beer(String name, String manufacturer, BeerType type, Double strength, int nutritionalValue) {
        Chars chars = new Chars();
        chars.setStrength(strength);
        chars.setNutritionalValue(nutritionalValue);

        Beer beer = new Beer();
        beer.setName(name);
        beer.setManufacturer(manufacturer);
        beer.setType(type);
        beer.setAlcoholic(strength != null);
        beer.setChars(chars);
        return beer;
    }
}
