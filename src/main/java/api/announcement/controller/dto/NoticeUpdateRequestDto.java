package api.announcement.controller.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoticeUpdateRequestDto {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    @NotBlank
    private Long updateUserId;
}
