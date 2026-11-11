package com.brewery.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ValidationResultTest {

    @Test
    void resultWithoutErrorsIsValid() {
        assertTrue(new ValidationResult(List.of()).isValid());
    }

    @Test
    void resultWithErrorsIsNotValid() {
        ValidationResult result = new ValidationResult(List.of("line 1: broken"));

        assertFalse(result.isValid());
        assertEquals(List.of("line 1: broken"), result.errors());
    }

    @Test
    void errorsAreCopiedAndCannotBeModified() {
        List<String> source = new ArrayList<>(List.of("first"));
        ValidationResult result = new ValidationResult(source);

        source.add("second");

        assertEquals(List.of("first"), result.errors());
        List<String> errors = result.errors();
        assertThrows(UnsupportedOperationException.class, () -> errors.add("third"));
    }
}
