package com.brewery.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.brewery.TestFiles;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class XmlFactoriesTest {

    private static final String WITH_DOCTYPE =
            "<?xml version=\"1.0\"?><!DOCTYPE Beer [<!ENTITY x \"y\">]><Beer>&x;</Beer>";

    private static InputStream withDoctype() {
        return new ByteArrayInputStream(WITH_DOCTYPE.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void documentBuilderFactoryIsNamespaceAwareAndRejectsDoctype() throws Exception {
        DocumentBuilderFactory factory = XmlFactories.documentBuilderFactory();
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(new DefaultHandler());

        assertTrue(factory.isNamespaceAware());
        assertFalse(factory.isXIncludeAware());
        assertFalse(factory.isExpandEntityReferences());
        assertThrows(SAXException.class, () -> builder.parse(withDoctype()));
    }

    @Test
    void saxParserFactoryIsNamespaceAwareAndRejectsDoctype() throws Exception {
        SAXParserFactory factory = XmlFactories.saxParserFactory();
        SAXParser parser = factory.newSAXParser();

        assertTrue(factory.isNamespaceAware());
        assertFalse(factory.isXIncludeAware());
        assertThrows(SAXException.class, () -> parser.parse(withDoctype(), new DefaultHandler()));
    }

    @Test
    void xmlInputFactoryDoesNotSupportDtdAndExternalEntities() {
        XMLInputFactory factory = XmlFactories.xmlInputFactory();

        assertEquals(true, factory.getProperty(XMLInputFactory.IS_NAMESPACE_AWARE));
        assertEquals(true, factory.getProperty(XMLInputFactory.IS_COALESCING));
        assertEquals(false, factory.getProperty(XMLInputFactory.SUPPORT_DTD));
        assertEquals(false, factory.getProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES));
    }

    @Test
    void schemaFactoryIsSecureAndForbidsExternalSchemas() throws Exception {
        SchemaFactory factory = XmlFactories.schemaFactory();
        String importingSchema = "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">"
                + "<xs:include schemaLocation=\"http://example.invalid/other.xsd\"/></xs:schema>";

        assertTrue(factory.getFeature(XMLConstants.FEATURE_SECURE_PROCESSING));
        assertNotNull(factory.newSchema(TestFiles.BEER_XSD.toFile()));
        assertThrows(SAXException.class,
                () -> factory.newSchema(new StreamSource(new StringReader(importingSchema))));
    }

    @Test
    void transformerFactoryForbidsExternalAccess() throws Exception {
        TransformerFactory factory = XmlFactories.transformerFactory();

        assertNotNull(factory.newTransformer());
        assertTrue(factory.getFeature(XMLConstants.FEATURE_SECURE_PROCESSING));
        assertEquals("", factory.getAttribute(XMLConstants.ACCESS_EXTERNAL_DTD));
        assertEquals("", factory.getAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET));
    }
}
