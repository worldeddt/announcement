package api.announcement.services;


import api.announcement.controller.dto.*;
import api.announcement.entities.Notice;
import api.announcement.entities.User;
import api.announcement.enums.NoticeStatus;
import api.announcement.enums.Role;
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
        User user = new User(1L, "testUser", "test@example.com", "password", Role.ADMIN);
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

        Notice notice = new Notice();
        notice.setId(1L);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(noticeRepository.save(any(Notice.class))).thenReturn(notice);

        // When
        NoticeResponseDto response = noticeService.createNotice(requestDto);

        // Then
        assertNotNull(response);
        verify(redisService, times(1))
                .putValue(eq(NOTICE_CACHE_PREFIX + notice.getId()), any(Notice.class), eq(30L));
    }

    @Test
    void getNoticeByIdShouldRetrieveFromRedis() {
        // Given
        Long noticeId = 1L;
        Notice notice = new Notice();
        notice.setId(noticeId);
        notice.setTitle("Cached Notice");
        when(redisService.hasKey(NOTICE_CACHE_PREFIX + noticeId)).thenReturn(true);
        when(redisService.getValue(NOTICE_CACHE_PREFIX + noticeId)).thenReturn(notice);

        // When
        NoticeResponseDto response = noticeService.getNoticeById(noticeId);

        // Then
        assertNotNull(response);
        assertEquals("Cached Notice", response.getTitle());
        verify(redisService, times(1)).hasKey(NOTICE_CACHE_PREFIX + noticeId);
        verify(redisService, times(1)).getValue(NOTICE_CACHE_PREFIX + noticeId);
    }

    @Test
    void deleteNoticeShouldRemoveFromRedis() {
        // Given
        Long noticeId = 1L;
        Long userId = 1L;

        Notice notice = new Notice();
        notice.setId(noticeId);
        notice.setStatus(NoticeStatus.ACTIVE);

        NoticeDeleteDto noticeDeleteDto = NoticeDeleteDto.builder()
                .userId(userId)
                .build();

        User user = new User();
        user.setId(userId);
        user.setRole(Role.ADMIN);

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
        Notice notice = new Notice();
        notice.setId(noticeId);
        notice.setTitle("Old Title");

        NoticeUpdateRequestDto updateRequest =
                NoticeUpdateRequestDto.builder()
                                .title("update title")
                                        .build();

        when(noticeRepository.findByIdAndStatus(noticeId, NoticeStatus.ACTIVE)).thenReturn(Optional.of(notice));

        // When
        NoticeResponseDto response = noticeService.updateNotice(noticeId, updateRequest);

        // Then
        assertNotNull(response);
        assertEquals("update title", response.getTitle());
        verify(redisService, times(1))
                .putValue(eq(NOTICE_CACHE_PREFIX + noticeId), any(Notice.class), eq(90L));
    }
}
