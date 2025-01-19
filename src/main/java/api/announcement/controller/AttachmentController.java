package api.announcement.controller;


import api.announcement.controller.dto.AttachmentResponseDto;
import api.announcement.controller.dto.AttachmentUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/attachment")
@RequiredArgsConstructor
public class AttachmentController {
    private final AttachmentService attachmentService;

    @PutMapping("/{id}")
    public ResponseEntity<AttachmentResponseDto> update(
            @PathVariable Long id, @RequestBody AttachmentUpdateRequestDto attachmentUpdateRequestDto
    ) {
        return ResponseEntity.ok(attachmentService.update(id, attachmentUpdateRequestDto));
    }
}
