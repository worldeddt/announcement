package api.announcement.controller.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class NoticeRequestDto {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    private List<AttachmentRequestDto> attachments;

    private Long createId;
}
