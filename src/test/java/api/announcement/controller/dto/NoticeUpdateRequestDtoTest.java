package api.announcement.controller.dto;

import api.announcement.exception.NoticeExeption;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NoticeUpdateRequestDtoTest {
    private Validator validator;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        objectMapper = new ObjectMapper();
    }

    @Test
    void validNoticeUpdateRequestDtoShouldPassValidation() {
        // Given
        NoticeUpdateRequestDto dto = NoticeUpdateRequestDto.builder()
                .title("Updated Title")
                .content("Updated Content")
                .startDate(LocalDateTime.of(2025, 1, 1, 10, 0, 0))
                .endDate(LocalDateTime.of(2025, 1, 2, 10, 0, 0))
                .updateUserId(1L)
                .viewed(true)
                .build();

        // When
        Set<ConstraintViolation<NoticeUpdateRequestDto>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty(), "There should be no validation errors.");
    }

    @Test
    void missingUpdateUserIdShouldFailValidation() {
        // Given
        NoticeUpdateRequestDto dto = NoticeUpdateRequestDto.builder()
                .title("Updated Title")
                .content("Updated Content")
                .startDate(LocalDateTime.of(2025, 1, 1, 10, 0, 0))
                .endDate(LocalDateTime.of(2025, 1, 2, 10, 0, 0))
                .viewed(true)
                .build();

        // When
        Set<ConstraintViolation<NoticeUpdateRequestDto>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty(), "There should be validation errors for missing updateUserId.");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("updateUserId")),
                "Validation error for updateUserId is expected.");
    }
}