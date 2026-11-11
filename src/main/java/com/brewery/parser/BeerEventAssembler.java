package com.brewery.parser;

import com.brewery.model.Beer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Builds the collection of beers from a stream of "element started / text /
 * element ended" events. Both event-driven parsers (SAX pushes the events,
 * StAX pulls them) feed this class, so the assembling logic exists only once.
 */
final class BeerEventAssembler {

    private final List<Beer> beers = new ArrayList<>();
    private final StringBuilder text = new StringBuilder();
    private BeerBuilder current;

    /**
     * @param name       local name of the started element
     * @param attributes returns the value of an attribute of the element by its name,
     *                   or {@code null} if there is no such attribute
     */
    void startElement(String name, UnaryOperator<String> attributes) {
        text.setLength(0);
        BeerXmlTag.fromName(name).ifPresent(tag -> {
            if (tag == BeerXmlTag.BEER_ITEM) {
                current = new BeerBuilder(attributes.apply(BeerXmlTag.ATTR_ID));
            } else if (tag == BeerXmlTag.BOTTLING) {
                current(tag).bottling(
                        attributes.apply(BeerXmlTag.ATTR_VOLUME),
                        attributes.apply(BeerXmlTag.ATTR_MATERIAL));
            }
        });
    }

    /** Accumulates a chunk of character data; parsers may deliver one text in several chunks. */
    void characters(String chunk) {
        text.append(chunk);
    }

    /** @param name local name of the ended element */
    void endElement(String name) {
        BeerXmlTag.fromName(name).ifPresent(tag -> {
            if (tag == BeerXmlTag.BEER_ITEM) {
                beers.add(current(tag).build());
                current = null;
            } else if (tag.hasText()) {
                current(tag).text(tag, text.toString());
            }
        });
        text.setLength(0);
    }

    /** @return the beers assembled so far */
    List<Beer> beers() {
        return beers;
    }

    private BeerBuilder current(BeerXmlTag tag) {
        if (current == null) {
            throw new IllegalArgumentException("<" + tag.xmlName() + "> is outside of <"
                    + BeerXmlTag.BEER_ITEM.xmlName() + ">");
        }
        return current;
    }
}
