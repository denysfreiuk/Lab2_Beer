package com.brewery.parser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Names of the elements and attributes of the beer document.
 * Shared by all parsers so that no tag name is hard-coded twice.
 */
public enum BeerXmlTag {
    BEER("Beer", false),
    BEER_ITEM("BeerItem", false),
    NAME("Name", true),
    TYPE("Type", true),
    AL("Al", true),
    MANUFACTURER("Manufacturer", true),
    INGREDIENTS("Ingredients", false),
    INGREDIENT("Ingredient", true),
    CHARS("Chars", false),
    STRENGTH("Strength", true),
    TRANSPARENCY("Transparency", true),
    FILTERED("Filtered", true),
    NUTRITIONAL_VALUE("NutritionalValue", true),
    BOTTLING("Bottling", false);

    public static final String ATTR_ID = "id";
    public static final String ATTR_VOLUME = "volume";
    public static final String ATTR_MATERIAL = "material";

    private static final Map<String, BeerXmlTag> BY_NAME = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(BeerXmlTag::xmlName, Function.identity()));

    private static final List<BeerXmlTag> TEXT_TAGS = Arrays.stream(values())
            .filter(BeerXmlTag::hasText)
            .toList();

    private final String xmlName;
    private final boolean text;

    BeerXmlTag(String xmlName, boolean text) {
        this.xmlName = xmlName;
        this.text = text;
    }

    /** @return the element name as it is written in the document */
    public String xmlName() {
        return xmlName;
    }

    /** @return {@code true} if the element holds a text value, {@code false} if it only groups other elements */
    public boolean hasText() {
        return text;
    }

    /** @return the tag with the given element name, or empty for an unknown element */
    public static Optional<BeerXmlTag> fromName(String xmlName) {
        return Optional.ofNullable(BY_NAME.get(xmlName));
    }

    /** @return all tags that hold a text value, in document order */
    public static List<BeerXmlTag> textTags() {
        return TEXT_TAGS;
    }
}
