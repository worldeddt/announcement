package api.announcement.services;

import api.announcement.controller.dto.AttachmentRequestDto;
import api.announcement.controller.dto.NoticeRequestDto;
import api.announcement.controller.dto.NoticeResponseDto;
import api.announcement.entities.Attachment;
import api.announcement.entities.Notice;
import api.announcement.entities.User;
import api.announcement.enums.AttachmentStatus;
import api.announcement.enums.NoticeStatus;
import api.announcement.enums.Role;
import api.announcement.repositories.NoticeRepository;
import api.announcement.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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


    @Test
    void createNotice() {

        //mock data
        User user = new User();
        user.setRole(Role.ADMIN);
        user.setId(1L);
        user.setEmail("example@example.com");
        user.setUsername("eddy");
        user.setPassword(passwordEncoder.encode("user123"));

        AttachmentRequestDto attachmentRequestDto = AttachmentRequestDto.builder()
                .fileName("test.jpg")
                .filePath("/test.jpg")
                .build();

        NoticeRequestDto noticeRequestDto = NoticeRequestDto.builder()
                .createId(user.getId())
                .title("test")
                .content("test contents")
                .attachments(List.of(attachmentRequestDto))
                .endDate(LocalDateTime.now().plusDays(1))
                .startDate(LocalDateTime.now().minusDays(1))
                .build();

        Notice notice = new Notice();
        notice.setTitle(noticeRequestDto.getTitle());
        notice.setContent(noticeRequestDto.getContent());
        notice.setStartDate(noticeRequestDto.getStartDate());
        notice.setEndDate(noticeRequestDto.getEndDate());
        notice.setCreatedUser(user);
        notice.setAttachments(
                noticeRequestDto.getAttachments().stream().map(
                        attachmentRequestDto1 -> {
                            Attachment attachment = new Attachment();
                            attachment.setFileUrl(attachmentRequestDto1.getFilePath());
                            attachment.setFileName(attachmentRequestDto1.getFileName());
                            attachment.setStatus(AttachmentStatus.ACTIVE);
                            return attachment;
                        }
                ).toList()
        );
        notice.setStatus(NoticeStatus.ACTIVE);

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
        verify(userRepository, times(1)).findById(1L);
        verify(noticeRepository, times(1)).save(any(Notice.class));
    }

    @Test
    void getNoticeById() {

        //Given
        Long noticeId = 1L;

        User user = new User();
        user.setId(1L);
        user.setRole(Role.ADMIN);
        user.setUsername("admin");
        user.setEmail("user@example.com");
        user.setUsername(passwordEncoder.encode("eddy"));

        Attachment attachment = new Attachment();
        attachment.setFileUrl("test.jpg");
        attachment.setFileName("test.jpg");
        attachment.setStatus(AttachmentStatus.ACTIVE);

        Notice notice = new Notice();
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

        //Mock Repository
        when(noticeRepository.findById(noticeId)).thenReturn(Optional.of(notice));
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
        verify(noticeRepository, never()).findById(anyLong());
    }

    @Test
    void deleteNotice() {
    }

    @Test
    void getNotices() {
    }

    @Test
    void updateNotice() {
    }
}