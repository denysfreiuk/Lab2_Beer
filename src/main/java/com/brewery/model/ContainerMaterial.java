package com.brewery.model;

/** Material of the container the beer is bottled into. */
public enum ContainerMaterial implements XmlValue {
    GLASS("glass"),
    PLASTIC("plastic"),
    ALUMINIUM("aluminium"),
    STEEL("steel");

    private final String xmlValue;

    ContainerMaterial(String xmlValue) {
        this.xmlValue = xmlValue;
    }

    @Override
    public String xmlValue() {
        return xmlValue;
    }
}
