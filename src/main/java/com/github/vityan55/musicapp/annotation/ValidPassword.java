package com.github.vityan55.musicapp.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({
        ElementType.FIELD,
        ElementType.PARAMETER
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Password must be at least 8 characters, include letters, digits and upper case letters";

    // для разных сценариев валидации одного объекта, например, создание vs обновление
    Class<?>[] groups() default {};

    // дополнительная информация о нарушении
    Class<? extends Payload>[] payload() default {};
}
