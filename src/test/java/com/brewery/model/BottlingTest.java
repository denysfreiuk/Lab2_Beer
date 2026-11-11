package com.brewery.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class BottlingTest {

    @Test
    void defaultConstructorCreatesEmptyBottling() {
        Bottling bottling = new Bottling();

        assertEquals(0.0, bottling.getVolume());
        assertNull(bottling.getMaterial());
    }

    @Test
    void settersChangeTheState() {
        Bottling bottling = new Bottling();

        bottling.setVolume(1.5);
        bottling.setMaterial(ContainerMaterial.PLASTIC);

        assertEquals(1.5, bottling.getVolume());
        assertEquals(ContainerMaterial.PLASTIC, bottling.getMaterial());
    }

    @Test
    void bottlingsWithTheSameStateAreEqual() {
        Bottling first = new Bottling(0.5, ContainerMaterial.GLASS);
        Bottling second = new Bottling(0.5, ContainerMaterial.GLASS);

        assertEquals(first, first);
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void bottlingsWithDifferentStateAreNotEqual() {
        Bottling bottling = new Bottling(0.5, ContainerMaterial.GLASS);

        assertNotEquals(bottling, new Bottling(0.33, ContainerMaterial.GLASS));
        assertNotEquals(bottling, new Bottling(0.5, ContainerMaterial.STEEL));
        assertNotEquals(bottling, null);
        assertNotEquals(bottling, "0.5 l, GLASS");
    }

    @Test
    void toStringShowsVolumeAndMaterial() {
        assertEquals("0.5 l, GLASS", new Bottling(0.5, ContainerMaterial.GLASS).toString());
    }
}
