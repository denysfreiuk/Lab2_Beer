package com.brewery.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class XmlProcessingExceptionTest {

    @Test
    void keepsTheMessage() {
        XmlProcessingException e = new XmlProcessingException("broken");

        assertEquals("broken", e.getMessage());
        assertNull(e.getCause());
    }

    @Test
    void keepsTheCause() {
        IOException cause = new IOException("disk");

        XmlProcessingException e = new XmlProcessingException("broken", cause);

        assertEquals("broken", e.getMessage());
        assertSame(cause, e.getCause());
    }
}
