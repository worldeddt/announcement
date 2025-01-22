package api.announcement.controller.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AttachmentUpdateRequestDto {
    private String fileName;
    private String fileUrl;

    @NotNull
    private Long updateUserId;
}
