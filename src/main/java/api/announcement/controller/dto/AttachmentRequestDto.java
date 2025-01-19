package api.announcement.controller.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AttachmentRequestDto {
    private String fileName;
    private String filePath;
}
