package com.brewery.parser;

import com.brewery.model.Beer;
import com.brewery.xml.XmlFiles;
import com.brewery.xml.XmlProcessingException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * Template for all parsers: opens the file, logs the progress and translates
 * every failure into {@link XmlProcessingException}. Subclasses only supply the
 * parsing technology itself.
 */
public abstract class AbstractBeerParser implements BeerParser {

    private final Logger log = LogManager.getLogger(getClass());

    @Override
    public final List<Beer> parse(Path xml) {
        XmlFiles.requireReadable(xml);
        log.debug("Parsing {}", xml);
        try (InputStream in = new BufferedInputStream(Files.newInputStream(xml))) {
            List<Beer> beers = read(in);
            log.info("{} beers read from {}", beers.size(), xml.getFileName());
            return beers;
        } catch (IOException | SAXException | ParserConfigurationException | XMLStreamException
                 | IllegalArgumentException e) {
            log.error("Failed to parse {}: {}", xml, e.getMessage());
            throw new XmlProcessingException("Failed to parse " + xml + ": " + e.getMessage(), e);
        }
    }

    /** Reads all beers from the stream using the technology of the concrete parser. */
    protected abstract List<Beer> read(InputStream in)
            throws IOException, SAXException, ParserConfigurationException, XMLStreamException;
}
