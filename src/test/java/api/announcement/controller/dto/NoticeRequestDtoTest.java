package api.announcement.controller.dto;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NoticeRequestDtoTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validDtoShouldPassValidation() {
        // Given
        NoticeRequestDto dto = NoticeRequestDto.builder()
                .title("Valid Title")
                .content("Valid Content")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .attachments(List.of(new AttachmentRequestDto("example.jpg", "/path/example.jpg")))
                .createId(1L)
                .build();

        // When
        Set<ConstraintViolation<NoticeRequestDto>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty()); // 유효성 검증 통과
    }

    @Test
    void missingTitleShouldFailValidation() {
        NoticeRequestDto noticeRequestDto =
                NoticeRequestDto.builder()
                        .content("test contents")
                        .startDate(LocalDateTime.now())
                        .endDate(LocalDateTime.now().plusDays(1))
                        .attachments(
                                List.of(
                                        AttachmentRequestDto.builder()
                                                .fileName("test file name")
                                                .filePath("/fdata/home/file")
                                                .build()
                                )
                        )
                        .createId(1L)
                        .build();


        Set<ConstraintViolation<NoticeRequestDto>> violations = validator.validate(noticeRequestDto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<NoticeRequestDto> violation = violations.iterator().next();
        assertEquals("공백일 수 없습니다", violation.getMessage());
        assertEquals("title", violation.getPropertyPath().toString());
    }

    @Test
    void missingContentShouldFailValidation() {
        NoticeRequestDto noticeRequestDto =
                NoticeRequestDto.builder()
                        .title("test title")
                        .startDate(LocalDateTime.now())
                        .endDate(LocalDateTime.now().plusDays(1))
                        .attachments(
                                List.of(
                                        AttachmentRequestDto.builder()
                                                .fileName("test file name")
                                                .filePath("/fdata/home/file")
                                                .build()
                                )
                        )
                        .createId(1L)
                        .build();


        Set<ConstraintViolation<NoticeRequestDto>> violations = validator.validate(noticeRequestDto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<NoticeRequestDto> violation = violations.iterator().next();
        assertEquals("공백일 수 없습니다", violation.getMessage());
        assertEquals("content", violation.getPropertyPath().toString());
    }
    
    @Test
    void missingStartDateShouldFailValidation() {
        NoticeRequestDto noticeRequestDto =
                NoticeRequestDto.builder()
                        .title("test title")
                        .content("test contents")
                        .endDate(LocalDateTime.now().plusDays(1))
                        .attachments(
                                List.of(
                                        AttachmentRequestDto.builder()
                                                .fileName("test file name")
                                                .filePath("/fdata/home/file")
                                                .build()
                                )
                        )
                        .createId(1L)
                        .build();
        
        
        Set<ConstraintViolation<NoticeRequestDto>> violations = validator.validate(noticeRequestDto);
        
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<NoticeRequestDto> violation = violations.iterator().next();
        assertEquals("널이어서는 안됩니다",violation.getMessage());
        assertEquals("startDate", violation.getPropertyPath().toString());
    }
}