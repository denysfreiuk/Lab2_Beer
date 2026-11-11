package com.brewery.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXParseException;

class ErrorCollectorTest {

    private static SAXParseException problem(String message, int line, int column) {
        return new SAXParseException(message, null, null, line, column);
    }

    @Test
    void describesProblemWithItsPosition() {
        assertEquals("line 7, column 12: bad value", ErrorCollector.describe(problem("bad value", 7, 12)));
    }

    @Test
    void collectsErrorsInOrder() {
        ErrorCollector collector = new ErrorCollector();

        collector.error(problem("first", 1, 2));
        collector.error(problem("second", 3, 4));

        assertEquals(List.of("line 1, column 2: first", "line 3, column 4: second"), collector.errors());
    }

    @Test
    void warningsAreNotErrors() {
        ErrorCollector collector = new ErrorCollector();

        collector.warning(problem("just a hint", 1, 1));

        assertTrue(collector.errors().isEmpty());
    }

    @Test
    void fatalErrorIsCollectedAndRethrown() {
        ErrorCollector collector = new ErrorCollector();
        SAXParseException fatal = problem("not well-formed", 5, 6);

        SAXParseException thrown = assertThrows(SAXParseException.class, () -> collector.fatalError(fatal));

        assertSame(fatal, thrown);
        assertEquals(List.of("line 5, column 6: not well-formed"), collector.errors());
    }
}
