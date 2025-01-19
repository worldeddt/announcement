package api.announcement.controller;


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

    @Transactional(rollbackFor = Exception.class)
    public AttachmentResponseDto update(Long id, AttachmentUpdateRequestDto attachmentUpdateRequestDto) {
        Attachment attachment = attachmentRepository.findById(id).orElseThrow(
                NOT_FOUND_ATTACHMENT::build
        );

        return attachment.toUpdate(attachmentUpdateRequestDto).toDto();
    }
}
