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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class NoticeServiceTest {

    @Autowired
    private RedisService redisService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private NoticeRepository noticeRepository;

    @MockitoBean
    private UserRepository userRepository;


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

        System.out.printf("ddd :"+responseDto.getAttachments().get(0).getFileName());

        // Then
        assertNotNull(responseDto);
        assertNotNull(responseDto.getAttachments());
        assertEquals(noticeRequestDto.getTitle(), responseDto.getTitle());
        assertEquals(noticeRequestDto.getContent(), responseDto.getContent());
        assertEquals(noticeRequestDto.getAttachments().size(), responseDto.getAttachments().size());

        // Mock 동작 검증
        verify(userRepository, times(1)).findById(1L);
        verify(noticeRepository, times(1)).save(any(Notice.class));
        verify(redisService, times(1)).putValue(eq("notice:1"), eq(notice));
    }

    @Test
    void getNoticeById() {
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