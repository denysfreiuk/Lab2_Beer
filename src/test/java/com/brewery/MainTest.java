package com.brewery;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MainTest {

    @TempDir
    Path dir;

    private String[] args(Path xml, String... rest) {
        String[] args = new String[4 + rest.length];
        args[0] = xml.toString();
        args[1] = TestFiles.BEER_XSD.toString();
        args[2] = TestFiles.BEER_XSL.toString();
        args[3] = dir.resolve("grouped.xml").toString();
        System.arraycopy(rest, 0, args, 4, rest.length);
        return args;
    }

    @Test
    void returnsOkForValidDocument() {
        assertEquals(Main.EXIT_OK, Main.run(args(TestFiles.BEER_XML, "type")));
        assertTrue(Files.isRegularFile(dir.resolve("grouped.xml")));
    }

    @Test
    void returnsInvalidDocumentCodeWhenValidationFails() {
        assertEquals(Main.EXIT_INVALID_DOCUMENT, Main.run(args(TestFiles.INVALID)));
        assertFalse(Files.exists(dir.resolve("grouped.xml")));
    }

    @Test
    void returnsErrorCodeForWrongArguments() {
        assertEquals(Main.EXIT_ERROR, Main.run(args(TestFiles.BEER_XML, "price")));
        assertEquals(Main.EXIT_ERROR, Main.run("1", "2", "3", "4", "name", "6"));
    }

    @Test
    void returnsErrorCodeWhenFileIsMissing() {
        assertEquals(Main.EXIT_ERROR, Main.run(args(dir.resolve("missing.xml"))));
    }

    @Test
    void mainDoesNotThrow() {
        assertDoesNotThrow(() -> Main.main(args(TestFiles.VALID)));
        assertDoesNotThrow(() -> Main.main(args(dir.resolve("missing.xml"))));
    }
}
