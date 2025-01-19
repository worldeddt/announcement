package api.announcement.controller.dto;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class AttachmentResponseDto {
    private Long id;
    private String fileName;
    private String fileUrl;
}
