package com.brewery.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.brewery.TestFiles;
import com.brewery.xml.XmlProcessingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class XmlValidatorTest {

    private static final String VALID_ITEM = """
            <BeerItem id="%s">
                <Name>Sample</Name>
                <Type>light</Type>
                <Al>%s</Al>
                <Manufacturer>Nobody</Manufacturer>
                <Ingredients>
                    <Ingredient>water</Ingredient>
                    <Ingredient>malt</Ingredient>
                </Ingredients>
                <Chars>
                    %s
                    <Transparency>%s</Transparency>
                    <Filtered>true</Filtered>
                    <NutritionalValue>%s</NutritionalValue>
                    <Bottling volume="%s" material="glass"/>
                </Chars>
            </BeerItem>
            """;

    private final XmlValidator validator = new XmlValidator();

    @TempDir
    Path dir;

    /** Writes a document with a single beer; every argument fills one checked value. */
    private Path document(String id, String strength, String transparency, String kcal, String volume)
            throws IOException {
        String item = VALID_ITEM.formatted(id, "true", strength, transparency, kcal, volume);
        return Files.writeString(dir.resolve("beer.xml"), "<Beer>" + item + "</Beer>");
    }

    private boolean isValid(String id, String strength, String transparency, String kcal, String volume)
            throws IOException {
        return validator.validate(document(id, strength, transparency, kcal, volume), TestFiles.BEER_XSD).isValid();
    }

    @Test
    void mainDocumentIsValid() {
        ValidationResult result = validator.validate(TestFiles.BEER_XML, TestFiles.BEER_XSD);

        assertTrue(result.isValid(), () -> String.join("\n", result.errors()));
    }

    @Test
    void testDocumentIsValid() {
        ValidationResult result = validator.validate(TestFiles.VALID, TestFiles.BEER_XSD);

        assertTrue(result.isValid(), () -> String.join("\n", result.errors()));
    }

    @Test
    void reportsEverySchemaViolation() {
        ValidationResult result = validator.validate(TestFiles.INVALID, TestFiles.BEER_XSD);
        List<String> errors = result.errors();
        String all = String.join("\n", errors);

        assertFalse(result.isValid());
        assertTrue(all.contains("strong"), all);
        assertTrue(all.contains("150"), all);
        assertTrue(all.contains("material"), all);
        assertTrue(all.contains("beer-1"), all);
        assertTrue(all.contains("Ingredient"), all);
        assertTrue(errors.stream().allMatch(error -> error.startsWith("line ")), all);
    }

    @Test
    void malformedDocumentIsNotValid() {
        ValidationResult result = validator.validate(TestFiles.MALFORMED, TestFiles.BEER_XSD);

        assertFalse(result.isValid());
        assertEquals(1, result.errors().size());
    }

    @Test
    void acceptsValuesOnTheBoundaries() throws IOException {
        assertTrue(isValid("beer-1", "<Strength>0.1</Strength>", "0", "1", "0.01"));
        assertTrue(isValid("beer-9999", "<Strength>20</Strength>", "100", "500", "50"));
        assertTrue(isValid("beer-1", "", "50", "40", "0.5"));
    }

    @Test
    void rejectsValuesOutsideTheBoundaries() throws IOException {
        assertFalse(isValid("beer-1", "<Strength>0</Strength>", "50", "40", "0.5"));
        assertFalse(isValid("beer-1", "<Strength>20.1</Strength>", "50", "40", "0.5"));
        assertFalse(isValid("beer-1", "<Strength>4.55</Strength>", "50", "40", "0.5"));
        assertFalse(isValid("beer-1", "", "-1", "40", "0.5"));
        assertFalse(isValid("beer-1", "", "101", "40", "0.5"));
        assertFalse(isValid("beer-1", "", "50", "0", "0.5"));
        assertFalse(isValid("beer-1", "", "50", "501", "0.5"));
        assertFalse(isValid("beer-1", "", "50", "40", "0"));
        assertFalse(isValid("beer-1", "", "50", "40", "50.5"));
    }

    @Test
    void rejectsIdThatDoesNotFollowThePattern() throws IOException {
        assertFalse(isValid("1", "", "50", "40", "0.5"));
        assertFalse(isValid("beer-12345", "", "50", "40", "0.5"));
        assertFalse(isValid("wine-1", "", "50", "40", "0.5"));
    }

    @Test
    void rejectsEmptyCatalogue() throws IOException {
        Path empty = Files.writeString(dir.resolve("empty.xml"), "<Beer/>");

        assertFalse(validator.validate(empty, TestFiles.BEER_XSD).isValid());
    }

    @Test
    void brokenSchemaIsReportedAsError() throws IOException {
        Path brokenXsd = Files.writeString(dir.resolve("broken.xsd"), "<xs:schema");

        ValidationResult result = validator.validate(TestFiles.BEER_XML, brokenXsd);

        assertFalse(result.isValid());
        assertEquals(1, result.errors().size());
    }

    @Test
    void failsWhenDocumentIsMissing() {
        Path missing = dir.resolve("missing.xml");

        assertThrows(XmlProcessingException.class, () -> validator.validate(missing, TestFiles.BEER_XSD));
    }

    @Test
    void failsWhenSchemaIsMissing() {
        Path missing = dir.resolve("missing.xsd");

        assertThrows(XmlProcessingException.class, () -> validator.validate(TestFiles.BEER_XML, missing));
    }
}
