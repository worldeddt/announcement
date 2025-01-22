package api.announcement.services;


import api.announcement.controller.dto.NoticeDeleteDto;
import api.announcement.controller.dto.NoticeRequestDto;
import api.announcement.controller.dto.NoticeResponseDto;
import api.announcement.controller.dto.NoticeUpdateRequestDto;
import api.announcement.entities.Attachment;
import api.announcement.entities.Notice;
import api.announcement.entities.User;
import api.announcement.enums.AttachmentStatus;
import api.announcement.enums.NoticeStatus;
import api.announcement.enums.Role;
import api.announcement.exception.ErrorCode;
import api.announcement.repositories.NoticeRepository;
import api.announcement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static api.announcement.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final RedisService redisService;
    private static final String NOTICE_CACHE_PREFIX = "notice:";

    @Transactional(rollbackFor = Exception.class)
    public synchronized NoticeResponseDto createNotice(NoticeRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getCreateId())
                .orElseThrow(ErrorCode.NOT_FOUND_USER::build);

        if (user.getRole().equals(Role.USER)) NOT_CREATE_ROLE_OF_NOTICE.build();

        Notice notice = new Notice();
        notice.setTitle(requestDto.getTitle());
        notice.setContent(requestDto.getContent());
        notice.setStartDate(requestDto.getStartDate());
        notice.setEndDate(requestDto.getEndDate());
        notice.setCreatedUser(user);

        List<Attachment> attachments = requestDto.getAttachments().stream()
                .map(attachmentRequestDto -> {
                    Attachment attachment = new Attachment();
                    attachment.setFileUrl(attachmentRequestDto.getFilePath());
                    attachment.setFileName(attachmentRequestDto.getFileName());
                    attachment.setNotice(notice);
                    return attachment;
                }).collect(Collectors.toList());

        notice.setAttachments(attachments);
        notice.setStatus(NoticeStatus.ACTIVE);

        Notice saveNotice = noticeRepository.save(notice);

        redisService.putValue(NOTICE_CACHE_PREFIX+saveNotice.getId(), saveNotice, 30);

        return notice.toDto();
    }

    public NoticeResponseDto getNoticeById(Long noticeId) {
        if (redisService.hasKey(NOTICE_CACHE_PREFIX+noticeId)) {
            Notice value = (Notice) redisService.getValue(NOTICE_CACHE_PREFIX + noticeId);
            return value.toDto();
        }

        Notice notice = noticeRepository.findByIdAndStatus(noticeId, NoticeStatus.ACTIVE)
                .orElseThrow(ErrorCode.NOT_FOUND_NOTICE::build);

        redisService.putValue(NOTICE_CACHE_PREFIX+noticeId, notice, 30);

        return notice.toDto();
    }

    @Transactional(rollbackFor = Exception.class)
    public synchronized void deleteNotice(Long noticeId, NoticeDeleteDto noticeDeleteDto) {
        User user = userRepository.findById(noticeDeleteDto.getUserId())
                .orElseThrow(ErrorCode.NOT_FOUND_USER::build);

        if (user.getRole().equals(Role.USER)) NOT_DELETE_ROLE_OF_NOTICE.build();

        Notice notice = noticeRepository.findByIdAndStatus(noticeId, NoticeStatus.ACTIVE)
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

        redisService.deleteValue(NOTICE_CACHE_PREFIX + noticeId);
    }

    public Page<NoticeResponseDto> getNotices(Pageable pageable) {
        Page<Notice> allNotice = noticeRepository.findAllByStatus(pageable, NoticeStatus.ACTIVE);
        return allNotice.map(Notice::toDto);
    }

    @Transactional(rollbackFor = Exception.class)
    public synchronized NoticeResponseDto updateNotice(Long noticeId, NoticeUpdateRequestDto noticeUpdateRequestDto) {
        User user = userRepository.findById(noticeUpdateRequestDto.getUpdateUserId())
                .orElseThrow(ErrorCode.NOT_FOUND_USER::build);

        if (user.getRole().equals(Role.USER)) NOT_UPDATE_ROLE_OF_NOTICE.build();

        Notice notice = noticeRepository.findByIdAndStatus(noticeId, NoticeStatus.ACTIVE)
                .orElseThrow(ErrorCode.NOT_FOUND_NOTICE::build);

        Notice updatedNotice = notice.toUpdate(noticeUpdateRequestDto);

        redisService.putValue(NOTICE_CACHE_PREFIX + noticeId, updatedNotice, 90);

        return notice.toDto();
    }
}
