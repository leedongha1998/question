package common.validator;

import common.annotation.ValidUUID;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.UUID;

public class UUIDValidator implements ConstraintValidator<ValidUUID, UUID> {

    @Override
    public boolean isValid(UUID value, ConstraintValidatorContext context) {
        // UUID가 null이면 유효하지 않음
        if (value == null) {
            return false;
        }

        // UUID의 문자열 표현이 유효한지 검증
        try {
            UUID.fromString(value.toString());
            return true; // 유효한 UUID 형식
        } catch (IllegalArgumentException e) {
            return false; // 유효하지 않은 UUID 형식
        }
    }
}
