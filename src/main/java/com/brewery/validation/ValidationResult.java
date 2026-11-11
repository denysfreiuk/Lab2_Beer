package com.brewery.validation;

import java.util.List;

/**
 * Outcome of validating an XML document against a schema.
 *
 * @param errors human readable descriptions of the violations; empty for a valid document
 */
public record ValidationResult(List<String> errors) {

    public ValidationResult {
        errors = List.copyOf(errors);
    }

    public boolean isValid() {
        return errors.isEmpty();
    }
}
