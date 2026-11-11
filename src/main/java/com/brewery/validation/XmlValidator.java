package com.brewery.validation;

import com.brewery.xml.XmlFactories;
import com.brewery.xml.XmlFiles;
import com.brewery.xml.XmlProcessingException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

/** Validates XML documents against an XSD schema. */
public final class XmlValidator {

    private static final Logger LOG = LogManager.getLogger(XmlValidator.class);

    /**
     * @param xml the document to check
     * @param xsd the schema the document must conform to
     * @return the list of violations, empty if the document is valid
     * @throws XmlProcessingException if one of the files cannot be read
     */
    public ValidationResult validate(Path xml, Path xsd) {
        XmlFiles.requireReadable(xml);
        XmlFiles.requireReadable(xsd);
        LOG.debug("Validating {} against {}", xml, xsd);

        ErrorCollector collector = new ErrorCollector();
        List<String> errors = new ArrayList<>();
        try {
            Schema schema = XmlFactories.schemaFactory().newSchema(xsd.toFile());
            Validator validator = schema.newValidator();
            validator.setErrorHandler(collector);
            validator.validate(new StreamSource(xml.toFile()));
        } catch (SAXException | IOException e) {
            // a fatal error of the document is already collected;
            // anything else here means the schema is broken or a file became unreadable
            if (collector.errors().isEmpty()) {
                errors.add(e.getMessage());
            }
        }
        errors.addAll(collector.errors());

        ValidationResult result = new ValidationResult(errors);
        if (result.isValid()) {
            LOG.info("{} is valid against {}", xml.getFileName(), xsd.getFileName());
        } else {
            LOG.warn("{} is NOT valid against {}: {} error(s)",
                    xml.getFileName(), xsd.getFileName(), errors.size());
        }
        return result;
    }
}
