package api.announcement.controller.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AttachmentUpdateRequestDto {
    @NotBlank
    private String fileName;

    @NotBlank
    private String fileUrl;
}
