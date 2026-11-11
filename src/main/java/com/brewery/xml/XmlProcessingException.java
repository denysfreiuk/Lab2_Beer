package com.brewery.xml;

/** Signals that an XML document could not be read, parsed, validated or transformed. */
public class XmlProcessingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public XmlProcessingException(String message) {
        super(message);
    }

    public XmlProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
