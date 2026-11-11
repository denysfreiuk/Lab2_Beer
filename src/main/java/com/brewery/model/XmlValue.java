package com.brewery.model;

/**
 * An enum constant that is written in the XML document as a fixed string
 * (the value of an {@code xs:enumeration} facet in the schema).
 */
public interface XmlValue {

    /** @return the text used for this constant in the XML document */
    String xmlValue();

    /**
     * Finds the constant of the given enum type by its XML text.
     *
     * @throws IllegalArgumentException if no constant has such XML value
     */
    static <E extends Enum<E> & XmlValue> E parse(Class<E> type, String text) {
        for (E constant : type.getEnumConstants()) {
            if (constant.xmlValue().equals(text)) {
                return constant;
            }
        }
        throw new IllegalArgumentException(
                "Unknown " + type.getSimpleName() + " value: '" + text + "'");
    }
}
