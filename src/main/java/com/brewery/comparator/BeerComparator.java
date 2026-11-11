package com.brewery.comparator;

import com.brewery.model.Beer;
import java.util.Comparator;

/**
 * Ready-made orderings of beers. Every constant is a {@link Comparator}, so it
 * can be passed directly to {@code List.sort} or {@code Stream.sorted}.
 * Beers that are equal by the main criterion are ordered by name.
 */
public enum BeerComparator implements Comparator<Beer> {

    /** Alphabetically by name, ignoring case. */
    BY_NAME(Comparator.comparing(Beer::getName, String.CASE_INSENSITIVE_ORDER)),

    /** By manufacturer, ignoring case. */
    BY_MANUFACTURER(Comparator.comparing(Beer::getManufacturer, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(BY_NAME)),

    /** By beer type in the order the types are declared. */
    BY_TYPE(Comparator.comparing(Beer::getType)
            .thenComparing(BY_NAME)),

    /** From the weakest to the strongest; non-alcoholic beers without strength go first. */
    BY_STRENGTH(Comparator.comparing((Beer beer) -> beer.getChars().getStrength(),
                    Comparator.nullsFirst(Comparator.naturalOrder()))
            .thenComparing(BY_NAME)),

    /** From the least to the most nutritious. */
    BY_NUTRITIONAL_VALUE(Comparator.comparingInt((Beer beer) -> beer.getChars().getNutritionalValue())
            .thenComparing(BY_NAME));

    private final Comparator<Beer> order;

    BeerComparator(Comparator<Beer> order) {
        this.order = order;
    }

    @Override
    public int compare(Beer first, Beer second) {
        return order.compare(first, second);
    }
}
