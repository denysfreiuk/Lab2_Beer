package com.brewery;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

/** Locates documents used by the tests on the classpath. */
public final class TestFiles {

    public static final Path BEER_XML = resource("beer.xml");
    public static final Path BEER_XSD = resource("beer.xsd");
    public static final Path BEER_XSL = resource("beer.xsl");

    public static final Path VALID = resource("beers-valid.xml");
    public static final Path INVALID = resource("beers-invalid.xml");
    public static final Path MALFORMED = resource("beers-malformed.xml");

    private TestFiles() {
    }

    public static Path resource(String name) {
        URL url = TestFiles.class.getClassLoader().getResource(name);
        if (url == null) {
            throw new IllegalStateException("Test resource not found: " + name);
        }
        try {
            return Path.of(url.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}
