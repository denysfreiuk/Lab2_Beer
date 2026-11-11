package com.brewery.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.brewery.TestFiles;
import com.brewery.comparator.BeerComparator;
import com.brewery.model.Beer;
import com.brewery.parser.ParserType;
import com.brewery.xml.XmlProcessingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BeerApplicationTest {

    private final BeerApplication application = new BeerApplication();

    @TempDir
    Path dir;

    private AppConfig config(Path xml, BeerComparator sortOrder) {
        return new AppConfig(xml, TestFiles.BEER_XSD, TestFiles.BEER_XSL, dir.resolve("grouped.xml"), sortOrder);
    }

    @Test
    void validDocumentIsProcessedAndTransformed() {
        AppConfig config = config(TestFiles.BEER_XML, BeerComparator.BY_NAME);

        assertTrue(application.run(config));
        assertTrue(Files.isRegularFile(config.output()));
    }

    @Test
    void invalidDocumentIsNotProcessed() {
        AppConfig config = config(TestFiles.INVALID, BeerComparator.BY_NAME);

        assertFalse(application.run(config));
        assertFalse(Files.exists(config.output()));
    }

    @Test
    void everyParserGivesTheSameSortedCollection() {
        AppConfig config = config(TestFiles.VALID, BeerComparator.BY_NAME);

        Map<ParserType, List<Beer>> parsed = application.parseWithEveryParser(config);

        assertEquals(ParserType.values().length, parsed.size());
        for (List<Beer> beers : parsed.values()) {
            assertEquals(List.of("Amber Live", "Test Stout", "Zero Lager"),
                    beers.stream().map(Beer::getName).toList());
        }
    }

    @Test
    void sortsInTheConfiguredOrder() {
        AppConfig config = config(TestFiles.VALID, BeerComparator.BY_STRENGTH);

        List<Beer> beers = application.parseWithEveryParser(config).get(ParserType.SAX);

        assertEquals(List.of("Zero Lager", "Amber Live", "Test Stout"),
                beers.stream().map(Beer::getName).toList());
    }

    @Test
    void failsWhenDocumentIsMissing() {
        AppConfig config = config(dir.resolve("missing.xml"), BeerComparator.BY_NAME);

        assertThrows(XmlProcessingException.class, () -> application.run(config));
    }
}
