package api.announcement.services;


import api.announcement.controller.dto.AttachmentRequestDto;
import api.announcement.controller.dto.NoticeRequestDto;
import api.announcement.controller.dto.NoticeResponseDto;
import api.announcement.entities.Notice;
import api.announcement.entities.User;
import api.announcement.enums.Role;
import api.announcement.enums.UserStatus;
import api.announcement.repositories.NoticeRepository;
import api.announcement.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NoticeServiceConcurrencyTest {

    @InjectMocks
    private NoticeService noticeService;

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedisService redisService;

    private static final String NOTICE_CACHE_PREFIX = "notice:";

    private int threadCount;
    private int taskCount;

    @BeforeEach
    public void init() {
        threadCount = 1000; // 최대 동시 실행 스레드 수
        taskCount = 100000; // 실행할 작업 수
    }

    @Test
    void testConcurrentCreateNotice() throws Exception {
        // Given

        User user = new User(1L,
                "testUser",
                "test@example.com",
                "password",
                Role.ADMIN, UserStatus.ACTIVE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Notice notice = new Notice();
        when(noticeRepository.save(any(Notice.class))).thenAnswer(invocation -> {
            Notice savedNotice = invocation.getArgument(0);
            savedNotice.setId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
            return savedNotice;
        });

        doNothing().when(redisService).putValue(anyString(), any(Notice.class), anyLong());

        NoticeRequestDto requestDto = NoticeRequestDto.builder()
                .title("Concurrent Test Notice")
                .content("Concurrent Test Content")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .createId(1L)
                .attachments(List.of(
                        AttachmentRequestDto.builder()
                                .fileName("file1.jpg")
                                .filePath("/path/file1.jpg")
                                .build()
                        )).build();

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        List<Callable<NoticeResponseDto>> tasks = new ArrayList<>();

        for (int i = 0; i < taskCount; i++) {
            tasks.add(() -> noticeService.createNotice(requestDto));
        }

        List<Future<NoticeResponseDto>> results = executorService.invokeAll(tasks);

        Set<Long> uniqueIds = new HashSet<>();
        for (Future<NoticeResponseDto> result : results) {
            NoticeResponseDto response = result.get();
            assertNotNull(response);
            uniqueIds.add(response.getId());
        }

        assertEquals(taskCount, uniqueIds.size(), "Duplicate IDs found!");
        executorService.shutdown();
    }
}
