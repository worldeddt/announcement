package api.announcement.services;


import api.announcement.controller.dto.*;
import api.announcement.entities.Attachment;
import api.announcement.entities.Notice;
import api.announcement.entities.User;
import api.announcement.enums.AttachmentStatus;
import api.announcement.enums.NoticeStatus;
import api.announcement.enums.Role;
import api.announcement.enums.UserStatus;
import api.announcement.repositories.NoticeRepository;
import api.announcement.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CacheTest {


    @InjectMocks
    private NoticeService noticeService;

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedisService redisService;

    private static final String NOTICE_CACHE_PREFIX = "notice:";

    @Test
    void createNoticeShouldStoreInRedis() {
        // Given

        Long attachmentId = 1L;

        User user = new User(
                1L,
                "testUser",
                "test@example.com",
                "password",
                Role.ADMIN,
                UserStatus.ACTIVE
        );
        NoticeRequestDto requestDto = NoticeRequestDto.builder()
                .title("Test Notice")
                .content("Test Content")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .createId(user.getId())
                .attachments(
                        List.of(
                        AttachmentRequestDto.builder()
                                .fileName("file.jpg")
                                .filePath("/path/file.jpg")
                                .build()
                        )
                )
                .build();

        Attachment attachment = new Attachment();
        attachment.setId(attachmentId);
        attachment.setFileUrl("test.jpg");
        attachment.setFileName("test.jpg");
        attachment.setStatus(AttachmentStatus.ACTIVE);

        Notice notice = new Notice();
        notice.setId(1L);
        notice.setAttachments(List.of(attachment));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(noticeRepository.save(any(Notice.class))).thenReturn(notice);

        // When
        NoticeResponseDto response = noticeService.createNotice(requestDto);

        // Then
        assertNotNull(response);
        verify(redisService, times(1))
                .putValue(eq(NOTICE_CACHE_PREFIX + notice.getId()), any(NoticeResponseDto.class));
    }

    @Test
    void getNoticeByIdShouldRetrieveFromRedis() {
        // Given
        Long noticeId = 1L;
        Long userId = 1L;
        Long attachmentId = 1L;

        Attachment attachment = new Attachment();
        attachment.setId(attachmentId);
        attachment.setFileUrl("test.jpg");
        attachment.setFileName("test.jpg");
        attachment.setStatus(AttachmentStatus.ACTIVE);

        Notice notice = new Notice();
        notice.setId(noticeId);
        notice.setTitle("Cached Notice");
        notice.setAttachments(List.of(attachment));

        when(redisService.hasKey(NOTICE_CACHE_PREFIX + noticeId)).thenReturn(true);
        when(redisService.getValue(NOTICE_CACHE_PREFIX + noticeId)).thenReturn(notice.toDto());

        // When
        NoticeResponseDto response = noticeService.getNoticeById(noticeId);

        // Then
        assertNotNull(response);
        verify(redisService, times(1)).hasKey(NOTICE_CACHE_PREFIX + noticeId);
        verify(redisService, times(1)).getValue(NOTICE_CACHE_PREFIX + noticeId);
    }

    @Test
    void deleteNoticeShouldRemoveFromRedis() {
        // Given
        Long noticeId = 1L;
        Long userId = 1L;
        Long attachmentId = 1L;

        Attachment attachment = new Attachment();
        attachment.setId(attachmentId);
        attachment.setFileUrl("test.jpg");
        attachment.setFileName("test.jpg");
        attachment.setStatus(AttachmentStatus.ACTIVE);

        Notice notice = new Notice();
        notice.setId(noticeId);
        notice.setTitle("Old Title");
        notice.setAttachments(List.of(attachment));

        User user = new User();
        user.setId(userId);
        user.setRole(Role.ADMIN);

        NoticeDeleteDto noticeDeleteDto = NoticeDeleteDto.builder()
                .userId(userId)
                .build();

        when(noticeRepository.findByIdAndStatus(noticeId, NoticeStatus.ACTIVE)).thenReturn(Optional.of(notice));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        noticeService.deleteNotice(noticeId, noticeDeleteDto);

        // Then
        verify(redisService, times(1)).deleteValue(NOTICE_CACHE_PREFIX + noticeId);
    }

    @Test
    void updateNoticeShouldUpdateRedisCache() {
        // Given
        Long noticeId = 1L;
        Long userId = 1L;
        Long attachmentId = 1L;

        Attachment attachment = new Attachment();
        attachment.setId(attachmentId);
        attachment.setFileUrl("test.jpg");
        attachment.setFileName("test.jpg");
        attachment.setStatus(AttachmentStatus.ACTIVE);

        Notice notice = new Notice();
        notice.setId(noticeId);
        notice.setTitle("Old Title");
        notice.setAttachments(List.of(attachment));

        User user = new User();
        user.setId(userId);
        user.setRole(Role.ADMIN);

        NoticeUpdateRequestDto updateRequest =
                NoticeUpdateRequestDto.builder()
                                .updateUserId(userId)
                                .title("update title")
                        .content("update content")
                                        .build();

        when(noticeRepository.findByIdAndStatus(noticeId, NoticeStatus.ACTIVE)).thenReturn(Optional.of(notice));
        when(userRepository.findById(updateRequest.getUpdateUserId())).thenReturn(Optional.of(user));

        // When
        NoticeResponseDto response = noticeService.updateNotice(noticeId, updateRequest);

        // Then
        assertNotNull(response);
        verify(redisService, times(1))
                .putValue(eq(NOTICE_CACHE_PREFIX + noticeId), any(NoticeResponseDto.class));
    }
}
