package com.brewery.app;

import com.brewery.comparator.BeerComparator;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Settings of one application run.
 *
 * @param xml       the beer document
 * @param xsd       the schema the document is validated against
 * @param xsl       the stylesheet that groups beers by type
 * @param output    the file the transformed document is written to
 * @param sortOrder the order the parsed beers are printed in
 */
public record AppConfig(Path xml, Path xsd, Path xsl, Path output, BeerComparator sortOrder) {

    public static final String USAGE = "Usage: beer-xml [xml] [xsd] [xsl] [output] [sort]"
            + " (sort: name | manufacturer | type | strength | nutritional_value)";

    static final Path DEFAULT_XML = Path.of("src", "main", "resources", "beer.xml");
    static final Path DEFAULT_XSD = Path.of("src", "main", "resources", "beer.xsd");
    static final Path DEFAULT_XSL = Path.of("src", "main", "resources", "beer.xsl");
    static final Path DEFAULT_OUTPUT = Path.of("output", "beer-by-type.xml");
    static final BeerComparator DEFAULT_SORT_ORDER = BeerComparator.BY_NAME;

    private static final int MAX_ARGUMENTS = 5;
    private static final String SORT_PREFIX = "BY_";

    /**
     * Builds the configuration from positional command line arguments.
     * Every argument is optional; a missing one falls back to its default.
     *
     * @throws IllegalArgumentException if there are too many arguments or the sort order is unknown
     */
    public static AppConfig fromArgs(String... args) {
        if (args.length > MAX_ARGUMENTS) {
            throw new IllegalArgumentException("Too many arguments: " + args.length);
        }
        return new AppConfig(
                pathOrDefault(args, 0, DEFAULT_XML),
                pathOrDefault(args, 1, DEFAULT_XSD),
                pathOrDefault(args, 2, DEFAULT_XSL),
                pathOrDefault(args, 3, DEFAULT_OUTPUT),
                args.length > 4 ? parseSortOrder(args[4]) : DEFAULT_SORT_ORDER);
    }

    private static Path pathOrDefault(String[] args, int index, Path defaultPath) {
        return args.length > index ? Path.of(args[index]) : defaultPath;
    }

    private static BeerComparator parseSortOrder(String name) {
        try {
            return BeerComparator.valueOf(SORT_PREFIX + name.strip().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown sort order: '" + name + "'", e);
        }
    }
}
