package api.announcement.controller.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AttachmentUpdateRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validDtoShouldPassValidation() {
        // Given
        AttachmentUpdateRequestDto dto = AttachmentUpdateRequestDto.builder()
                .fileName("example.jpg")
                .fileUrl("/files/example.jpg")
                .updateUserId(1L) // 필수 필드
                .build();

        // When
        Set<ConstraintViolation<AttachmentUpdateRequestDto>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty()); // 유효성 검증 통과
    }

    @Test
    void missingUpdateUserIdShouldFailValidation() {
        // Given
        AttachmentUpdateRequestDto dto = AttachmentUpdateRequestDto.builder()
                .fileName("example.jpg")
                .fileUrl("/files/example.jpg")
                .build(); // updateUserId 누락

        // When
        Set<ConstraintViolation<AttachmentUpdateRequestDto>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty()); // 유효성 검증 실패
        assertEquals(1, violations.size());
        ConstraintViolation<AttachmentUpdateRequestDto> violation = violations.iterator().next();
        assertEquals("널이어서는 안됩니다", violation.getMessage());
        assertEquals("updateUserId", violation.getPropertyPath().toString());
    }
}