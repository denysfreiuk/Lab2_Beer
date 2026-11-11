package com.brewery.model;

/** Type of beer, the {@code BeerType} enumeration of the schema. */
public enum BeerType implements XmlValue {
    DARK("dark"),
    LIGHT("light"),
    LAGER("lager"),
    LIVE("live");

    private final String xmlValue;

    BeerType(String xmlValue) {
        this.xmlValue = xmlValue;
    }

    @Override
    public String xmlValue() {
        return xmlValue;
    }
}
