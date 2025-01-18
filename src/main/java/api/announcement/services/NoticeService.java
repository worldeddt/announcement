package api.announcement.services;


import api.announcement.controller.dto.NoticeRequestDto;
import api.announcement.entities.Notice;
import api.announcement.repositories.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;

    public Notice createNotice(NoticeRequestDto noticeRequestDto) {
        Notice notice = new Notice();


        return noticeRepository.save(notice);
    }
}
