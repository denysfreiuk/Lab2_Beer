package com.brewery.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

class CharsTest {

    private static Chars sample() {
        Chars chars = new Chars();
        chars.setStrength(4.7);
        chars.setTransparency(90);
        chars.setFiltered(true);
        chars.setNutritionalValue(42);
        chars.setBottling(new Bottling(0.5, ContainerMaterial.GLASS));
        return chars;
    }

    private static Chars sampleWith(Consumer<Chars> change) {
        Chars chars = sample();
        change.accept(chars);
        return chars;
    }

    @Test
    void newCharsAreEmpty() {
        Chars chars = new Chars();

        assertNull(chars.getStrength());
        assertEquals(0, chars.getTransparency());
        assertFalse(chars.isFiltered());
        assertEquals(0, chars.getNutritionalValue());
        assertNull(chars.getBottling());
    }

    @Test
    void settersChangeTheState() {
        Chars chars = sample();

        assertEquals(4.7, chars.getStrength());
        assertEquals(90, chars.getTransparency());
        assertTrue(chars.isFiltered());
        assertEquals(42, chars.getNutritionalValue());
        assertEquals(new Bottling(0.5, ContainerMaterial.GLASS), chars.getBottling());
    }

    @Test
    void charsWithTheSameStateAreEqual() {
        Chars chars = sample();

        assertEquals(chars, chars);
        assertEquals(chars, sample());
        assertEquals(chars.hashCode(), sample().hashCode());
    }

    @Test
    void charsDifferingInAnyFieldAreNotEqual() {
        Chars chars = sample();

        assertNotEquals(chars, sampleWith(c -> c.setStrength(5.0)));
        assertNotEquals(chars, sampleWith(c -> c.setStrength(null)));
        assertNotEquals(chars, sampleWith(c -> c.setTransparency(50)));
        assertNotEquals(chars, sampleWith(c -> c.setFiltered(false)));
        assertNotEquals(chars, sampleWith(c -> c.setNutritionalValue(1)));
        assertNotEquals(chars, sampleWith(c -> c.setBottling(null)));
        assertNotEquals(chars, null);
        assertNotEquals(chars, "chars");
    }

    @Test
    void toStringListsAllCharacteristics() {
        assertEquals("strength=4.7%, transparency=90%, filtered=true, nutritionalValue=42 kcal, "
                + "bottling=0.5 l, GLASS", sample().toString());
    }

    @Test
    void toStringMarksMissingStrength() {
        String text = sampleWith(c -> c.setStrength(null)).toString();

        assertTrue(text.startsWith("strength=n/a,"), text);
    }
}
