package com.brewery.parser;

import com.brewery.model.Beer;
import com.brewery.xml.XmlProcessingException;
import java.nio.file.Path;
import java.util.List;

/** Reads the collection of beers from an XML document. */
public interface BeerParser {

    /**
     * @param xml path to the XML document
     * @return beers in the order they appear in the document
     * @throws XmlProcessingException if the file cannot be read or is not a well-formed beer document
     */
    List<Beer> parse(Path xml);
}
