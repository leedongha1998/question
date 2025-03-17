package common.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.validator.internal.constraintvalidators.hv.UUIDValidator;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = {UUIDValidator.class})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUUID {

    String message() default "유효하지 않은 UUID 입니다!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
