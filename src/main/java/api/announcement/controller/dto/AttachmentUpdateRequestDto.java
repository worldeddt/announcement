package api.announcement.controller.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AttachmentUpdateRequestDto {
    private String fileName;
    private String fileUrl;

    @NotBlank
    private Long updateUserId;
}
