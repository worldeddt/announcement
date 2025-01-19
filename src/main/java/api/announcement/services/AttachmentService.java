package api.announcement.services;


import api.announcement.controller.dto.AttachmentResponseDto;
import api.announcement.controller.dto.AttachmentUpdateRequestDto;
import api.announcement.entities.Attachment;
import api.announcement.repositories.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static api.announcement.exception.ErrorCode.NOT_FOUND_ATTACHMENT;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final RedisService redisService;
    private static final String NOTICE_CACHE_PREFIX = "notice:";

    @Transactional(rollbackFor = Exception.class)
    public AttachmentResponseDto update(Long id, AttachmentUpdateRequestDto attachmentUpdateRequestDto) {
        Attachment attachment = attachmentRepository.findById(id).orElseThrow(
                NOT_FOUND_ATTACHMENT::build
        );

        Attachment updatedAttachment = attachment.toUpdate(attachmentUpdateRequestDto);

        if (redisService.hasKey(NOTICE_CACHE_PREFIX + updatedAttachment.getNotice().getId())) {
            redisService.deleteValue(NOTICE_CACHE_PREFIX + updatedAttachment.getNotice().getId());
        }

        return updatedAttachment.toDto();
    }
}
