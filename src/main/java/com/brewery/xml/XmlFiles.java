package com.brewery.xml;

import java.nio.file.Files;
import java.nio.file.Path;

/** File checks shared by the parsers, the validator and the transformer. */
public final class XmlFiles {

    private XmlFiles() {
    }

    /**
     * Makes sure the given path points to an existing readable file.
     *
     * @return the same path, for call chaining
     * @throws XmlProcessingException if the file is missing or cannot be read
     */
    public static Path requireReadable(Path file) {
        if (file == null || !Files.isRegularFile(file) || !Files.isReadable(file)) {
            throw new XmlProcessingException("File not found or not readable: " + file);
        }
        return file;
    }
}
