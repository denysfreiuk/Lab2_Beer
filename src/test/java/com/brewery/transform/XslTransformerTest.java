package com.brewery.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.brewery.TestFiles;
import com.brewery.xml.XmlFactories;
import com.brewery.xml.XmlProcessingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class XslTransformerTest {

    private final XslTransformer transformer = new XslTransformer();

    @TempDir
    Path dir;

    private static Document read(Path xml) throws Exception {
        return XmlFactories.documentBuilderFactory().newDocumentBuilder().parse(xml.toFile());
    }

    private static List<Element> childElements(Node parent) {
        List<Element> elements = new ArrayList<>();
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i) instanceof Element element) {
                elements.add(element);
            }
        }
        return elements;
    }

    private static List<String> names(List<Element> elements) {
        return elements.stream().map(Element::getTagName).toList();
    }

    private static String text(Element parent, String tag) {
        return parent.getElementsByTagName(tag).item(0).getTextContent();
    }

    @Test
    void groupsBeersIntoSubListsByType() throws Exception {
        Path output = transformer.transform(TestFiles.BEER_XML, TestFiles.BEER_XSL, dir.resolve("grouped.xml"));

        Element root = read(output).getDocumentElement();
        List<Element> groups = childElements(root);

        assertEquals("Beer", root.getTagName());
        assertEquals(List.of("dark", "lager", "light", "live"), names(groups));
        for (Element group : groups) {
            List<Element> items = childElements(group);
            assertEquals(2, items.size(), group.getTagName());
            assertEquals("2", group.getAttribute("count"));
            for (Element item : items) {
                assertEquals("BeerItem", item.getTagName());
                assertEquals(group.getTagName(), text(item, "Type"));
            }
        }
    }

    @Test
    void keepsEveryBeerWithItsContent() throws Exception {
        Path output = transformer.transform(TestFiles.VALID, TestFiles.BEER_XSL, dir.resolve("grouped.xml"));

        Element root = read(output).getDocumentElement();
        Element stout = childElements(childElements(root).get(0)).get(0);

        assertEquals(3, root.getElementsByTagName("BeerItem").getLength());
        assertEquals("beer-10", stout.getAttribute("id"));
        assertEquals(1, stout.getAttributes().getLength());
        assertEquals("Test Stout", text(stout, "Name"));
        assertEquals("Stout & Sons", text(stout, "Manufacturer"));
        assertEquals(3, stout.getElementsByTagName("Ingredient").getLength());
        assertEquals("6.5", text(stout, "Strength"));
        Element bottling = (Element) stout.getElementsByTagName("Bottling").item(0);
        assertEquals("0.5", bottling.getAttribute("volume"));
        assertEquals("glass", bottling.getAttribute("material"));
    }

    @Test
    void sortsBeersInsideGroupByName() throws Exception {
        Path output = transformer.transform(TestFiles.BEER_XML, TestFiles.BEER_XSL, dir.resolve("grouped.xml"));

        Element dark = childElements(read(output).getDocumentElement()).get(0);
        List<String> names = childElements(dark).stream().map(item -> text(item, "Name")).toList();

        assertEquals(List.of("Guinness Draught", "Leffe Brune"), names);
    }

    @Test
    void createsMissingOutputDirectories() {
        Path output = dir.resolve("a").resolve("b").resolve("grouped.xml");

        Path written = transformer.transform(TestFiles.BEER_XML, TestFiles.BEER_XSL, output);

        assertTrue(Files.isRegularFile(output));
        assertEquals(output.toAbsolutePath(), written);
    }

    @Test
    void failsWhenDocumentIsMissing() {
        Path output = dir.resolve("grouped.xml");

        assertThrows(XmlProcessingException.class,
                () -> transformer.transform(dir.resolve("missing.xml"), TestFiles.BEER_XSL, output));
        assertFalse(Files.exists(output));
    }

    @Test
    void failsWhenStylesheetIsMissing() {
        assertThrows(XmlProcessingException.class,
                () -> transformer.transform(TestFiles.BEER_XML, dir.resolve("missing.xsl"), dir.resolve("out.xml")));
    }

    @Test
    void failsOnBrokenStylesheet() throws IOException {
        Path brokenXsl = Files.writeString(dir.resolve("broken.xsl"), "<xsl:stylesheet");

        XmlProcessingException e = assertThrows(XmlProcessingException.class,
                () -> transformer.transform(TestFiles.BEER_XML, brokenXsl, dir.resolve("out.xml")));

        assertTrue(e.getMessage().contains("broken.xsl"), e.getMessage());
    }

    @Test
    void failsOnMalformedDocument() {
        assertThrows(XmlProcessingException.class,
                () -> transformer.transform(TestFiles.MALFORMED, TestFiles.BEER_XSL, dir.resolve("out.xml")));
    }
}
