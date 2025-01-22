package api.announcement.repositories;

import api.announcement.entities.Notice;
import api.announcement.enums.NoticeStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class NoticeRepositoryTest {

    @Mock
    private NoticeRepository noticeRepository;

    @Test
    void findAllByStatus() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("startDate").descending());

        Notice notice1 = new Notice();
        notice1.setId(1L);
        notice1.setTitle("Test Notice 1");
        notice1.setStatus(NoticeStatus.ACTIVE);

        Notice notice2 = new Notice();
        notice2.setId(2L);
        notice2.setTitle("Test Notice 2");
        notice2.setStatus(NoticeStatus.ACTIVE);

        List<Notice> notices =
                List.of(notice1, notice2);

        Page<Notice> noticePage = new PageImpl<>(notices, pageable, notices.size());

        when(noticeRepository.findAllByStatus(pageable, NoticeStatus.ACTIVE))
                .thenReturn(noticePage);

        Page<Notice> result = noticeRepository.findAllByStatus(pageable, NoticeStatus.ACTIVE);

        assertEquals(2, result.getTotalElements());
        assertEquals("Test Notice 2", result.getContent().get(1).getTitle());
    }
}