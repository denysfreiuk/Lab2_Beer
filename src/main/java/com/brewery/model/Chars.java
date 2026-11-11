package com.brewery.model;

import java.util.Objects;

/** Characteristics of a beer (the {@code Chars} element). */
public class Chars {

    /** Alcohol by volume in percent; {@code null} for non-alcoholic beer. */
    private Double strength;
    /** Transparency in percent, 0..100. */
    private int transparency;
    private boolean filtered;
    /** Nutritional value in kcal. */
    private int nutritionalValue;
    private Bottling bottling;

    public Double getStrength() {
        return strength;
    }

    public void setStrength(Double strength) {
        this.strength = strength;
    }

    public int getTransparency() {
        return transparency;
    }

    public void setTransparency(int transparency) {
        this.transparency = transparency;
    }

    public boolean isFiltered() {
        return filtered;
    }

    public void setFiltered(boolean filtered) {
        this.filtered = filtered;
    }

    public int getNutritionalValue() {
        return nutritionalValue;
    }

    public void setNutritionalValue(int nutritionalValue) {
        this.nutritionalValue = nutritionalValue;
    }

    public Bottling getBottling() {
        return bottling;
    }

    public void setBottling(Bottling bottling) {
        this.bottling = bottling;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Chars other)) {
            return false;
        }
        return transparency == other.transparency
                && filtered == other.filtered
                && nutritionalValue == other.nutritionalValue
                && Objects.equals(strength, other.strength)
                && Objects.equals(bottling, other.bottling);
    }

    @Override
    public int hashCode() {
        return Objects.hash(strength, transparency, filtered, nutritionalValue, bottling);
    }

    @Override
    public String toString() {
        return "strength=" + (strength == null ? "n/a" : strength + "%")
                + ", transparency=" + transparency + "%"
                + ", filtered=" + filtered
                + ", nutritionalValue=" + nutritionalValue + " kcal"
                + ", bottling=" + bottling;
    }
}
