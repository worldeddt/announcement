package api.announcement.controller.dto;

import api.announcement.entities.User;
import api.announcement.enums.Role;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NoticeResponseDtoTest {

    @Test
    void shouldCreateNoticeResponseDto() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        User createdUser = new User(1L, "eddy", "eddy@example.com", "password",  Role.ADMIN);
        List<AttachmentResponseDto> attachments = List.of(
                new AttachmentResponseDto(1L, "file1.jpg", "/path/file1.jpg"),
                new AttachmentResponseDto(2L, "file2.jpg", "/path/file2.jpg")
        );

        // When
        NoticeResponseDto dto = NoticeResponseDto.builder()
                .id(1L)
                .title("Test Notice")
                .content("This is a test content.")
                .startDate(now)
                .endDate(now.plusDays(1))
                .viewCount(100)
                .createdUser(createdUser)
                .recentUpdateUser(2L)
                .attachments(attachments)
                .build();

        // Then
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Test Notice", dto.getTitle());
        assertEquals("This is a test content.", dto.getContent());
        assertEquals(now, dto.getStartDate());
        assertEquals(now.plusDays(1), dto.getEndDate());
        assertEquals(100, dto.getViewCount());
        assertEquals(createdUser, dto.getCreatedUser());
        assertEquals(2L, dto.getRecentUpdateUser());
        assertEquals(attachments, dto.getAttachments());
    }
}