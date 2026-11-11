package com.brewery.validation;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * Collects every schema violation instead of stopping at the first one, so a
 * single validation run reports all problems of the document.
 */
final class ErrorCollector implements ErrorHandler {

    private static final Logger LOG = LogManager.getLogger(ErrorCollector.class);

    private final List<String> errors = new ArrayList<>();

    @Override
    public void warning(SAXParseException e) {
        LOG.warn("Validation warning: {}", describe(e));
    }

    @Override
    public void error(SAXParseException e) {
        errors.add(describe(e));
    }

    /** A fatal error means the document is not well-formed, so the validation cannot go on. */
    @Override
    public void fatalError(SAXParseException e) throws SAXParseException {
        errors.add(describe(e));
        throw e;
    }

    List<String> errors() {
        return errors;
    }

    static String describe(SAXParseException e) {
        return "line " + e.getLineNumber() + ", column " + e.getColumnNumber() + ": " + e.getMessage();
    }
}
