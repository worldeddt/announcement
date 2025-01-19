package api.announcement.services;


import api.announcement.controller.dto.AttachmentResponseDto;
import api.announcement.controller.dto.NoticeRequestDto;
import api.announcement.controller.dto.NoticeResponseDto;
import api.announcement.controller.dto.NoticeUpdateRequestDto;
import api.announcement.entities.Attachment;
import api.announcement.entities.Notice;
import api.announcement.entities.User;
import api.announcement.enums.AttachmentStatus;
import api.announcement.enums.NoticeStatus;
import api.announcement.exception.ErrorCode;
import api.announcement.repositories.NoticeRepository;
import api.announcement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final AttachmentService attachmentService;

    @Transactional(rollbackFor = Exception.class)
    public Notice createNotice(NoticeRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getCreateId())
                .orElseThrow(ErrorCode.NOT_FOUND_USER::build);

        Notice notice = new Notice();
        notice.setTitle(requestDto.getTitle());
        notice.setContent(requestDto.getContent());
        notice.setStartDate(requestDto.getStartDate());
        notice.setEndDate(requestDto.getEndDate());
        notice.setCreatedUser(user);
        notice.setAttachments(
                requestDto.getAttachments().stream().map(
                        attachmentRequestDto -> {
                            Attachment attachment = new Attachment();
                            attachment.setFileUrl(attachmentRequestDto.getFilePath());
                            attachment.setFileName(attachmentRequestDto.getFileName());
                            attachment.setStatus(AttachmentStatus.ACTIVE);
                            attachment.setNotice(notice);
                            return attachment;
                        }
                ).toList()
        );
        notice.setStatus(NoticeStatus.ACTIVE);

        return noticeRepository.save(notice);
    }

    public NoticeResponseDto getNoticeById(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(ErrorCode.NOT_FOUND_NOTICE::build);

        return notice.toDto();
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(ErrorCode.NOT_FOUND_NOTICE::build);

        notice.setDeletedAt(LocalDateTime.now());
        notice.setStatus(NoticeStatus.DELETED);
        notice.getAttachments()
                .forEach(attachment ->
                        {
                            attachment.setDeletedAt(LocalDateTime.now());
                            attachment.setStatus(AttachmentStatus.DELETED);
                        }
                );
    }

    public Page<NoticeResponseDto> getNotices(Pageable pageable) {
        Page<Notice> allNotice = noticeRepository.findAll(pageable);
        return allNotice.map(Notice::toDto);
    }

    @Transactional(rollbackFor = Exception.class)
    public NoticeResponseDto updateNotice(Long noticeId, NoticeUpdateRequestDto requestDto) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(ErrorCode.NOT_FOUND_NOTICE::build);

        notice.setTitle(requestDto.getTitle());
        notice.setContent(requestDto.getContent());
        notice.setStartDate(requestDto.getStartDate());
        notice.setEndDate(requestDto.getEndDate());

        notice.setRecentUpdateUser(requestDto.getUpdateUserId());

        if (requestDto.isViewed()) notice.setViewCount(notice.getViewCount() + 1);

        return notice.toDto();
    }
}
