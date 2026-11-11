package com.brewery.parser;

import com.brewery.model.Beer;
import com.brewery.xml.XmlFactories;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/** DOM parser: loads the whole document into a tree and then walks over it. */
public final class DomBeerParser extends AbstractBeerParser {

    @Override
    protected List<Beer> read(InputStream in) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilder documentBuilder = XmlFactories.documentBuilderFactory().newDocumentBuilder();
        // report malformed documents with an exception instead of printing to System.err
        documentBuilder.setErrorHandler(new DefaultHandler());
        Document document = documentBuilder.parse(in);

        NodeList items = document.getElementsByTagName(BeerXmlTag.BEER_ITEM.xmlName());
        List<Beer> beers = new ArrayList<>(items.getLength());
        for (int i = 0; i < items.getLength(); i++) {
            beers.add(toBeer((Element) items.item(i)));
        }
        return beers;
    }

    private static Beer toBeer(Element item) {
        BeerBuilder builder = new BeerBuilder(item.getAttribute(BeerXmlTag.ATTR_ID));
        for (BeerXmlTag tag : BeerXmlTag.textTags()) {
            NodeList nodes = item.getElementsByTagName(tag.xmlName());
            for (int i = 0; i < nodes.getLength(); i++) {
                builder.text(tag, nodes.item(i).getTextContent());
            }
        }
        NodeList bottlings = item.getElementsByTagName(BeerXmlTag.BOTTLING.xmlName());
        for (int i = 0; i < bottlings.getLength(); i++) {
            Element bottling = (Element) bottlings.item(i);
            builder.bottling(
                    attribute(bottling, BeerXmlTag.ATTR_VOLUME),
                    attribute(bottling, BeerXmlTag.ATTR_MATERIAL));
        }
        return builder.build();
    }

    /** @return the attribute value, or {@code null} (not an empty string) when the attribute is absent */
    private static String attribute(Element element, String name) {
        return element.hasAttribute(name) ? element.getAttribute(name) : null;
    }
}
