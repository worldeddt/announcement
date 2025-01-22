package api.announcement.controller.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentUpdateRequestDto {
    private String fileName;
    private String fileUrl;

    @NotNull
    private Long updateUserId;
}
