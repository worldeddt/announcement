package api.announcement.services;


import api.announcement.controller.dto.AttachmentResponseDto;
import api.announcement.controller.dto.NoticeRequestDto;
import api.announcement.controller.dto.NoticeResponseDto;
import api.announcement.entities.Notice;
import api.announcement.entities.User;
import api.announcement.exception.ErrorCode;
import api.announcement.repositories.NoticeRepository;
import api.announcement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    @Transactional(rollbackFor = Exception.class)
    public Notice createNotice(NoticeRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(ErrorCode.NOT_FOUND_USER::build);

        Notice notice = new Notice();
        notice.setTitle(requestDto.getTitle());
        notice.setContent(requestDto.getContent());
        notice.setStartDate(requestDto.getStartDate());
        notice.setEndDate(requestDto.getEndDate());
        notice.setCreatedUser(user);

        return noticeRepository.save(notice);
    }

    public NoticeResponseDto getNoticeById(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(ErrorCode.NOT_FOUND_NOTICE::build);

        NoticeResponseDto noticeResponseDto
                = NoticeResponseDto.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .startDate(notice.getStartDate())
                .endDate(notice.getEndDate())
                .createdUser(notice.getCreatedUser())
                .attachments(
                        notice.getAttachments().stream()
                                .map( noticeAttachment ->
                                        AttachmentResponseDto.builder()
                                                .id(noticeAttachment.getId())
                                                .fileName(noticeAttachment.getFileName())
                                                .fileUrl(noticeAttachment.getFileUrl())
                                                .build()
                                ).toList()
                )
                .build();

        return noticeResponseDto;
    }
}
