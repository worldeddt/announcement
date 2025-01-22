package api.announcement.services;

import api.announcement.controller.dto.*;
import api.announcement.entities.Attachment;
import api.announcement.entities.Notice;
import api.announcement.entities.User;
import api.announcement.enums.AttachmentStatus;
import api.announcement.enums.NoticeStatus;
import api.announcement.enums.Role;
import api.announcement.exception.NoticeExeption;
import api.announcement.repositories.NoticeRepository;
import api.announcement.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class NoticeServiceTest {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private NoticeRepository noticeRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private RedisService redisService;

    private static final String NOTICE_CACHE_PREFIX = "notice:";

    String datetimeFormat = "yyyy-MM-dd HH:mm:ss";
    Long noticeId;
    Long attachmentId;
    Long userId;
    Long userRoleOfUserId;
    User user;
    User userRoleOfUser;
    Notice notice;
    Attachment attachment;
    NoticeUpdateRequestDto noticeUpdateRequestDto;
    NoticeRequestDto noticeRequestDto;
    NoticeDeleteDto noticeDeleteDto;
    AttachmentRequestDto attachmentRequestDto;

    @BeforeEach
    public void init() {
        //Mock data
        noticeId = 1L;
        attachmentId = 1L;
        userId = 2L;
        userRoleOfUserId = 3L;

        user = new User();
        user.setId(userId);
        user.setRole(Role.ADMIN);
        user.setUsername("admin");
        user.setEmail("user@example.com");
        user.setUsername(passwordEncoder.encode("eddy"));

        userRoleOfUser = new User();
        user.setId(userRoleOfUserId);
        user.setRole(Role.USER);
        user.setUsername("admin");
        user.setEmail("user@example.com");
        user.setUsername(passwordEncoder.encode("eddy"));

        attachment = new Attachment();
        attachment.setId(attachmentId);
        attachment.setFileUrl("test.jpg");
        attachment.setFileName("test.jpg");
        attachment.setStatus(AttachmentStatus.ACTIVE);

        notice = new Notice();
        notice.setId(noticeId);
        notice.setTitle("test");
        notice.setContent("test contents");
        notice.setStartDate(LocalDateTime.now().minusDays(1));
        notice.setEndDate(LocalDateTime.now().plusDays(1));
        notice.setCreatedUser(
                user
        );
        notice.setAttachments(
                List.of(
                        attachment
                )
        );
        notice.setStatus(NoticeStatus.ACTIVE);

        noticeUpdateRequestDto =
                NoticeUpdateRequestDto.builder()
                        .title("update test")
                        .content("update test contents")
                        .startDate(LocalDateTime.now().minusDays(1))
                        .endDate(LocalDateTime.now().plusDays(1))
                        .updateUserId(userId)
                        .viewed(true)
                        .build();

        attachmentRequestDto = AttachmentRequestDto.builder()
                .fileName("test.jpg")
                .filePath("/test.jpg")
                .build();

        noticeRequestDto =
                NoticeRequestDto.builder()
                        .createId(user.getId())
                        .title("test")
                        .content("test contents")
                        .attachments(List.of(attachmentRequestDto))
                        .endDate(LocalDateTime.now().plusDays(1))
                        .startDate(LocalDateTime.now().minusDays(1))
                        .build();

        noticeDeleteDto =
                NoticeDeleteDto.builder()
                        .userId(user.getId())
                        .build();
    }


    @Test
    void createNotice() {
        // Mock Repository
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(noticeRepository.save(any(Notice.class))).thenReturn(notice);

        //when
        NoticeResponseDto responseDto = noticeService.createNotice(noticeRequestDto);

        // Then
        assertNotNull(responseDto);
        assertNotNull(responseDto.getAttachments());
        assertEquals(noticeRequestDto.getTitle(), responseDto.getTitle());
        assertEquals(noticeRequestDto.getContent(), responseDto.getContent());
        assertEquals(noticeRequestDto.getAttachments().size(), responseDto.getAttachments().size());

        // Mock 동작 검증
        verify(userRepository, times(1)).findById(user.getId());
        verify(noticeRepository, times(1)).save(any(Notice.class));
    }

    @Test
    void getNoticeById() {
        //Mock Repository
        when(noticeRepository.findByIdAndStatus(noticeId, NoticeStatus.ACTIVE)).thenReturn(Optional.of(notice));
        when(redisService.hasKey(NOTICE_CACHE_PREFIX + noticeId)).thenReturn(true);
        when(redisService.getValue(NOTICE_CACHE_PREFIX + noticeId)).thenReturn(notice);

        //when
        NoticeResponseDto responseNotice = noticeService.getNoticeById(noticeId);

        //then
        Assertions.assertNotNull(responseNotice);
        Assertions.assertEquals(notice.getTitle(), responseNotice.getTitle());
        Assertions.assertEquals(notice.getContent(), responseNotice.getContent());
        Assertions.assertEquals(notice.getAttachments().size(), responseNotice.getAttachments().size());

        //Mock 검증
        verify(redisService, times(1)).hasKey(NOTICE_CACHE_PREFIX + noticeId);
        verify(redisService, times(1)).getValue(NOTICE_CACHE_PREFIX + noticeId);
        verify(noticeRepository, never()).findByIdAndStatus(anyLong(), any(NoticeStatus.class));
    }

    @Test
    void deleteNotice() {
        //when
        when(noticeRepository.findByIdAndStatus(noticeId, NoticeStatus.ACTIVE)).thenReturn(Optional.of(notice));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        noticeService.deleteNotice(noticeId, noticeDeleteDto);

        NoticeResponseDto noticeById = noticeService.getNoticeById(noticeId);

        //then
        assertNotNull(notice.getDeletedAt());
        assertEquals(notice.getDeletedAt().format(DateTimeFormatter.ofPattern(datetimeFormat)),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(datetimeFormat)));

        assertEquals(notice.getStatus(), NoticeStatus.DELETED);
        assertEquals(attachment.getStatus(), AttachmentStatus.DELETED);

        // Redis 캐시 삭제 검증
        verify(redisService, times(1)).deleteValue(NOTICE_CACHE_PREFIX + noticeId);
    }


    @Test
    void findNoticeAfterDelete() {

        when(noticeRepository.findByIdAndStatus(noticeId, NoticeStatus.ACTIVE)).thenReturn(Optional.of(notice));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        noticeService.deleteNotice(noticeId, noticeDeleteDto);

        when(noticeRepository.findByIdAndStatus(noticeId, NoticeStatus.ACTIVE)).thenReturn(Optional.empty());

        NoticeExeption noticeExeption = assertThrows(NoticeExeption.class ,
                () -> noticeService.getNoticeById(noticeId));

        assertEquals("not found notice", noticeExeption.getReason());
    }


    @Test
    void getNotices() {
        Pageable pageable = PageRequest.of(0, 5);

        List<Notice> mockList = List.of(notice);

        Page<Notice> mockPage = new PageImpl<>(mockList, pageable, mockList.size());

        //mock check
        when(noticeRepository.findAllByStatus(pageable, NoticeStatus.ACTIVE)).thenReturn(mockPage);

        //when
        Page<NoticeResponseDto> notices = noticeService.getNotices(pageable);

        //then
        assertNotNull(notices);
        assertEquals(1, notices.getTotalElements());
        assertEquals(noticeId, notices.getContent().get(0).getId());
        assertEquals("test", notices.getContent().get(0).getTitle());

        //Mock 검증
        verify(noticeRepository, times(1)).findAllByStatus(pageable, NoticeStatus.ACTIVE);
    }

    @Test
    void updateNotice() {
        //Mock Repository
        when(noticeRepository.findByIdAndStatus(noticeId, NoticeStatus.ACTIVE)).thenReturn(Optional.of(notice));

        //when
        NoticeResponseDto noticeResponseDto = noticeService.updateNotice(noticeId, noticeUpdateRequestDto);

        //then
        assertEquals(noticeResponseDto.getTitle(), noticeUpdateRequestDto.getTitle());
        assertEquals(noticeResponseDto.getContent(), noticeUpdateRequestDto.getContent());

        verify(redisService, times(1)).putValue(
                eq(NOTICE_CACHE_PREFIX + noticeId), any(Notice.class), eq(90L));

        verify(noticeRepository, times(1)).findByIdAndStatus(noticeId, NoticeStatus.ACTIVE);
    }

    @Test
    void createNoticeUserRoleOfUser() {

        userRoleOfUser.setRole(Role.USER);

        noticeRequestDto = noticeRequestDto.toBuilder()
                .createId(userRoleOfUser.getId())
                .build();

        // Mock Repository
        when(userRepository.findById(userRoleOfUser.getId())).thenReturn(Optional.of(userRoleOfUser));

        // Then
        NoticeExeption noticeExeption =
                assertThrows(NoticeExeption.class, () -> noticeService.createNotice(noticeRequestDto));
        assertEquals("is not create role of notice", noticeExeption.getReason());

        // Mock 동작 검증
        verify(userRepository, times(1)).findById(userRoleOfUser.getId());
    }
}