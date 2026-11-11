package com.brewery.parser;

import com.brewery.model.Beer;
import com.brewery.xml.XmlFactories;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/** SAX parser: the parser pushes events into a handler while it reads the document. */
public final class SaxBeerParser extends AbstractBeerParser {

    @Override
    protected List<Beer> read(InputStream in) throws IOException, SAXException, ParserConfigurationException {
        BeerEventAssembler assembler = new BeerEventAssembler();
        XmlFactories.saxParserFactory().newSAXParser().parse(in, new BeerHandler(assembler));
        return assembler.beers();
    }

    /** Adapts SAX callbacks to the technology independent {@link BeerEventAssembler}. */
    private static final class BeerHandler extends DefaultHandler {

        private final BeerEventAssembler assembler;

        private BeerHandler(BeerEventAssembler assembler) {
            this.assembler = assembler;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            assembler.startElement(localName, attributes::getValue);
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            assembler.characters(new String(ch, start, length));
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            assembler.endElement(localName);
        }
    }
}
