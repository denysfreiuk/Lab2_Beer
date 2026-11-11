package com.brewery.parser;

import com.brewery.model.Beer;
import com.brewery.xml.XmlFactories;
import java.io.InputStream;
import java.util.List;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/** StAX parser: the application pulls events from the reader one by one. */
public final class StaxBeerParser extends AbstractBeerParser {

    @Override
    protected List<Beer> read(InputStream in) throws XMLStreamException {
        BeerEventAssembler assembler = new BeerEventAssembler();
        XMLStreamReader reader = XmlFactories.xmlInputFactory().createXMLStreamReader(in);
        try {
            while (reader.hasNext()) {
                switch (reader.next()) {
                    case XMLStreamConstants.START_ELEMENT -> assembler.startElement(
                            reader.getLocalName(), name -> reader.getAttributeValue(null, name));
                    case XMLStreamConstants.CHARACTERS, XMLStreamConstants.CDATA -> assembler.characters(
                            reader.getText());
                    case XMLStreamConstants.END_ELEMENT -> assembler.endElement(reader.getLocalName());
                    default -> {
                        // comments, processing instructions and document events carry no data
                    }
                }
            }
        } finally {
            reader.close();
        }
        return assembler.beers();
    }
}
