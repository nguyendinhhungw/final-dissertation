package com.merryblue.api.util.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // Use @NotBlank if you want to forbid null/empty
        }
        // Basic phone validation (starts with 0, has 10-11 digits)
        return value.matches("(0[3|5|7|8|9])+([0-9]{8})\\b");
    }
}
