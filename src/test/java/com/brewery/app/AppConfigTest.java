package com.brewery.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.brewery.comparator.BeerComparator;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AppConfigTest {

    @Test
    void usesDefaultsWithoutArguments() {
        AppConfig config = AppConfig.fromArgs();

        assertEquals(AppConfig.DEFAULT_XML, config.xml());
        assertEquals(AppConfig.DEFAULT_XSD, config.xsd());
        assertEquals(AppConfig.DEFAULT_XSL, config.xsl());
        assertEquals(AppConfig.DEFAULT_OUTPUT, config.output());
        assertEquals(BeerComparator.BY_NAME, config.sortOrder());
    }

    @Test
    void defaultInputFilesExistInTheProject() {
        assertTrue(Files.isRegularFile(AppConfig.DEFAULT_XML));
        assertTrue(Files.isRegularFile(AppConfig.DEFAULT_XSD));
        assertTrue(Files.isRegularFile(AppConfig.DEFAULT_XSL));
    }

    @Test
    void takesPathsFromArguments() {
        AppConfig config = AppConfig.fromArgs("a.xml", "b.xsd", "c.xsl", "out/d.xml", "strength");

        assertEquals(Path.of("a.xml"), config.xml());
        assertEquals(Path.of("b.xsd"), config.xsd());
        assertEquals(Path.of("c.xsl"), config.xsl());
        assertEquals(Path.of("out", "d.xml"), config.output());
        assertEquals(BeerComparator.BY_STRENGTH, config.sortOrder());
    }

    @Test
    void missingTrailingArgumentsFallBackToDefaults() {
        AppConfig config = AppConfig.fromArgs("a.xml", "b.xsd");

        assertEquals(Path.of("a.xml"), config.xml());
        assertEquals(Path.of("b.xsd"), config.xsd());
        assertEquals(AppConfig.DEFAULT_XSL, config.xsl());
        assertEquals(AppConfig.DEFAULT_OUTPUT, config.output());
        assertEquals(AppConfig.DEFAULT_SORT_ORDER, config.sortOrder());
    }

    @ParameterizedTest
    @CsvSource({
            "name, BY_NAME",
            "MANUFACTURER, BY_MANUFACTURER",
            "Type, BY_TYPE",
            "' strength ', BY_STRENGTH",
            "nutritional_value, BY_NUTRITIONAL_VALUE"})
    void understandsEverySortOrder(String argument, BeerComparator expected) {
        assertEquals(expected, AppConfig.fromArgs("a", "b", "c", "d", argument).sortOrder());
    }

    @Test
    void rejectsUnknownSortOrder() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> AppConfig.fromArgs("a", "b", "c", "d", "price"));

        assertTrue(e.getMessage().contains("price"));
    }

    @Test
    void rejectsTooManyArguments() {
        assertThrows(IllegalArgumentException.class,
                () -> AppConfig.fromArgs("a", "b", "c", "d", "name", "extra"));
    }
}
