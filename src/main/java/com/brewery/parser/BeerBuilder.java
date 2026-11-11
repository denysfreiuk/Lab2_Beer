package com.brewery.parser;

import com.brewery.model.Beer;
import com.brewery.model.BeerType;
import com.brewery.model.Bottling;
import com.brewery.model.Chars;
import com.brewery.model.ContainerMaterial;
import com.brewery.model.XmlValue;
import java.util.ArrayList;
import java.util.List;

/**
 * Assembles a {@link Beer} from raw XML strings.
 * This is the only place where text of the document is converted to typed
 * values, so the DOM, SAX and StAX parsers do not repeat that logic.
 */
final class BeerBuilder {

    private final Beer beer = new Beer();
    private final Chars chars = new Chars();
    private final List<String> ingredients = new ArrayList<>();

    /**
     * @param id value of the {@code id} attribute of the {@code BeerItem} element
     * @throws IllegalArgumentException if the id is missing
     */
    BeerBuilder(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("<BeerItem> without the '" + BeerXmlTag.ATTR_ID + "' attribute");
        }
        beer.setId(id.strip());
    }

    /**
     * Stores the text value of an element.
     *
     * @throws IllegalArgumentException if the text cannot be converted to the type of
     *                                  the element or the element holds no text at all
     */
    BeerBuilder text(BeerXmlTag tag, String rawText) {
        String text = rawText.strip();
        switch (tag) {
            case NAME -> beer.setName(text);
            case TYPE -> beer.setType(XmlValue.parse(BeerType.class, text));
            case AL -> beer.setAlcoholic(parseBoolean(text));
            case MANUFACTURER -> beer.setManufacturer(text);
            case INGREDIENT -> ingredients.add(text);
            case STRENGTH -> chars.setStrength(Double.valueOf(text));
            case TRANSPARENCY -> chars.setTransparency(Integer.parseInt(text));
            case FILTERED -> chars.setFiltered(parseBoolean(text));
            case NUTRITIONAL_VALUE -> chars.setNutritionalValue(Integer.parseInt(text));
            default -> throw new IllegalArgumentException("<" + tag.xmlName() + "> does not hold a text value");
        }
        return this;
    }

    /** Stores the attributes of the {@code Bottling} element. */
    BeerBuilder bottling(String volume, String material) {
        if (volume == null || material == null) {
            throw new IllegalArgumentException("<" + BeerXmlTag.BOTTLING.xmlName() + "> requires the '"
                    + BeerXmlTag.ATTR_VOLUME + "' and '" + BeerXmlTag.ATTR_MATERIAL + "' attributes");
        }
        chars.setBottling(new Bottling(
                Double.parseDouble(volume.strip()),
                XmlValue.parse(ContainerMaterial.class, material.strip())));
        return this;
    }

    /**
     * @return the assembled beer
     * @throws IllegalArgumentException if a mandatory element has not been supplied
     */
    Beer build() {
        requirePresent(beer.getName(), BeerXmlTag.NAME);
        requirePresent(beer.getType(), BeerXmlTag.TYPE);
        requirePresent(beer.getManufacturer(), BeerXmlTag.MANUFACTURER);
        requirePresent(chars.getBottling(), BeerXmlTag.BOTTLING);
        beer.setIngredients(ingredients);
        beer.setChars(chars);
        return beer;
    }

    private void requirePresent(Object value, BeerXmlTag tag) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "Beer '" + beer.getId() + "' has no <" + tag.xmlName() + "> element");
        }
    }

    /** Converts the lexical forms of {@code xs:boolean}: true, false, 1, 0. */
    private static boolean parseBoolean(String text) {
        return switch (text) {
            case "true", "1" -> true;
            case "false", "0" -> false;
            default -> throw new IllegalArgumentException("Not a boolean value: '" + text + "'");
        };
    }
}
