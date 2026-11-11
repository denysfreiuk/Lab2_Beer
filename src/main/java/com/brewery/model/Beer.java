package com.brewery.model;

import java.util.List;
import java.util.Objects;

/** A sort of beer: one {@code BeerItem} element of the XML document. */
public class Beer {

    private String id;
    private String name;
    private BeerType type;
    private boolean alcoholic;
    private String manufacturer;
    private List<String> ingredients = List.of();
    private Chars chars;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BeerType getType() {
        return type;
    }

    public void setType(BeerType type) {
        this.type = type;
    }

    public boolean isAlcoholic() {
        return alcoholic;
    }

    public void setAlcoholic(boolean alcoholic) {
        this.alcoholic = alcoholic;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /** @return an unmodifiable list of ingredients */
    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = List.copyOf(ingredients);
    }

    public Chars getChars() {
        return chars;
    }

    public void setChars(Chars chars) {
        this.chars = chars;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Beer other)) {
            return false;
        }
        return alcoholic == other.alcoholic
                && Objects.equals(id, other.id)
                && Objects.equals(name, other.name)
                && type == other.type
                && Objects.equals(manufacturer, other.manufacturer)
                && Objects.equals(ingredients, other.ingredients)
                && Objects.equals(chars, other.chars);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, alcoholic, manufacturer, ingredients, chars);
    }

    @Override
    public String toString() {
        return "Beer{id=" + id
                + ", name='" + name + '\''
                + ", type=" + type
                + ", alcoholic=" + alcoholic
                + ", manufacturer='" + manufacturer + '\''
                + ", ingredients=" + ingredients
                + ", chars={" + chars + "}}";
    }
}
