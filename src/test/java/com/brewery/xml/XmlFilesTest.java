package com.brewery.xml;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.brewery.TestFiles;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class XmlFilesTest {

    @Test
    void returnsTheSamePathForExistingFile() {
        assertSame(TestFiles.BEER_XML, XmlFiles.requireReadable(TestFiles.BEER_XML));
    }

    @Test
    void rejectsMissingFile(@TempDir Path dir) {
        Path missing = dir.resolve("missing.xml");

        XmlProcessingException e = assertThrows(XmlProcessingException.class,
                () -> XmlFiles.requireReadable(missing));

        assertTrue(e.getMessage().contains("missing.xml"));
    }

    @Test
    void rejectsDirectory(@TempDir Path dir) {
        assertThrows(XmlProcessingException.class, () -> XmlFiles.requireReadable(dir));
    }

    @Test
    void rejectsNull() {
        assertThrows(XmlProcessingException.class, () -> XmlFiles.requireReadable(null));
    }
}
