package com.brewery.transform;

import com.brewery.xml.XmlFactories;
import com.brewery.xml.XmlFiles;
import com.brewery.xml.XmlProcessingException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Transforms an XML document with an XSL stylesheet. */
public final class XslTransformer {

    private static final Logger LOG = LogManager.getLogger(XslTransformer.class);
    private static final String INDENT_AMOUNT = "{http://xml.apache.org/xslt}indent-amount";

    /**
     * @param xml    the source document
     * @param xsl    the stylesheet to apply
     * @param output the file to write the result to; missing parent directories are created
     * @return the path of the written file
     * @throws XmlProcessingException if a file cannot be read or written or the transformation fails
     */
    public Path transform(Path xml, Path xsl, Path output) {
        XmlFiles.requireReadable(xml);
        XmlFiles.requireReadable(xsl);
        LOG.debug("Transforming {} with {}", xml, xsl);

        Path target = output.toAbsolutePath();
        try {
            Files.createDirectories(target.getParent());
            Transformer transformer = XmlFactories.transformerFactory()
                    .newTransformer(new StreamSource(xsl.toFile()));
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(INDENT_AMOUNT, "4");
            try (OutputStream out = Files.newOutputStream(target)) {
                transformer.transform(new StreamSource(xml.toFile()), new StreamResult(out));
            }
        } catch (TransformerException | IOException e) {
            LOG.error("Transformation of {} failed: {}", xml, e.getMessage());
            throw new XmlProcessingException("Cannot transform " + xml + " with " + xsl + ": " + e.getMessage(), e);
        }
        LOG.info("{} transformed with {} into {}", xml.getFileName(), xsl.getFileName(), target);
        return target;
    }
}
