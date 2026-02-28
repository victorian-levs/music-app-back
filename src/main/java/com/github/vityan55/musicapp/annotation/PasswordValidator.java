package com.github.vityan55.musicapp.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        if (password == null || password.isBlank()) {
            return false;
        }

        if (password.length() < 8) {
            return false;
        }

        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasLetter = password.chars().anyMatch(Character::isLetter);
        boolean hasUpperCase = password.chars().anyMatch(Character::isUpperCase);

        return hasDigit && hasLetter && hasUpperCase;
    }
}
