package api.announcement.services;

import api.announcement.controller.dto.AttachmentResponseDto;
import api.announcement.controller.dto.AttachmentUpdateRequestDto;
import api.announcement.entities.Attachment;
import api.announcement.entities.Notice;
import api.announcement.exception.NoticeExeption;
import api.announcement.repositories.AttachmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @InjectMocks
    private AttachmentService attachmentService;

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private RedisService redisService;

    private static final String NOTICE_CACHE_PREFIX = "notice:";

    @Test
    void updateShouldUpdateAttachmentAndInvalidateCache() {
        // Given
        Long attachmentId = 1L;
        Long noticeId = 10L;

        Attachment existingAttachment = new Attachment();
        existingAttachment.setId(attachmentId);
        existingAttachment.setFileName("old.jpg");
        existingAttachment.setFileUrl("/old.jpg");
        existingAttachment.setNotice(Notice.builder()
                        .id(noticeId)
                        .viewCount(0)
                        .title("notice title")
                .build());

        AttachmentUpdateRequestDto updateRequest = AttachmentUpdateRequestDto.builder()
                .fileName("new.jpg")
                .fileUrl("/new.jpg")
                .updateUserId(1L)
                .build();

        // Mock 동작 정의
        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.of(existingAttachment));
        when(redisService.hasKey(NOTICE_CACHE_PREFIX + noticeId)).thenReturn(true);

        // When
        AttachmentResponseDto response = attachmentService.update(attachmentId, updateRequest);

        // Then
        assertNotNull(response);
        assertEquals("new.jpg", response.getFileName());
        assertEquals("/new.jpg", response.getFileUrl());

        // Verify repository interactions
        verify(attachmentRepository, times(1)).findById(attachmentId);

        // Verify Redis interactions
        verify(redisService, times(1)).hasKey(NOTICE_CACHE_PREFIX + noticeId);
        verify(redisService, times(1)).deleteValue(NOTICE_CACHE_PREFIX + noticeId);
    }

    @Test
    void udpateShouldThrowExceptionWhenAttachmentNotFound() {

        Long attachmentId = 1L;

        AttachmentUpdateRequestDto updateRequestDto = AttachmentUpdateRequestDto.builder()
                .fileName("test file name")
                .fileUrl("test file url")
                .updateUserId(1L)
                .build();

        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.empty());
        NoticeExeption exception = assertThrows(NoticeExeption.class ,
                () -> attachmentService.update(attachmentId, updateRequestDto));

        assertEquals("not found attachment", exception.getReason());

        verify(redisService, never()).hasKey(NOTICE_CACHE_PREFIX + attachmentId);
        verify(redisService, never()).deleteValue(NOTICE_CACHE_PREFIX + attachmentId);
        verify(attachmentRepository, times(1)).findById(attachmentId);
    }

}