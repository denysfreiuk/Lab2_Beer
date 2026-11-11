package com.brewery.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class XmlValueTest {

    @ParameterizedTest
    @EnumSource(BeerType.class)
    void findsBeerTypeByItsXmlValue(BeerType type) {
        assertEquals(type, XmlValue.parse(BeerType.class, type.xmlValue()));
    }

    @ParameterizedTest
    @EnumSource(ContainerMaterial.class)
    void findsContainerMaterialByItsXmlValue(ContainerMaterial material) {
        assertEquals(material, XmlValue.parse(ContainerMaterial.class, material.xmlValue()));
    }

    @Test
    void xmlValuesMatchTheSchemaEnumerations() {
        assertEquals("dark", BeerType.DARK.xmlValue());
        assertEquals("light", BeerType.LIGHT.xmlValue());
        assertEquals("lager", BeerType.LAGER.xmlValue());
        assertEquals("live", BeerType.LIVE.xmlValue());
        assertEquals("glass", ContainerMaterial.GLASS.xmlValue());
        assertEquals("plastic", ContainerMaterial.PLASTIC.xmlValue());
        assertEquals("aluminium", ContainerMaterial.ALUMINIUM.xmlValue());
        assertEquals("steel", ContainerMaterial.STEEL.xmlValue());
    }

    @Test
    void rejectsUnknownValue() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> XmlValue.parse(BeerType.class, "strong"));

        assertTrue(e.getMessage().contains("BeerType"));
        assertTrue(e.getMessage().contains("strong"));
    }

    @Test
    void isCaseSensitiveLikeTheSchema() {
        assertThrows(IllegalArgumentException.class, () -> XmlValue.parse(BeerType.class, "DARK"));
    }
}
