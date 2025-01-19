package api.announcement.controller.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoticeUpdateRequestDto {
    private String title;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @NotBlank
    private Long updateUserId;

    private boolean viewed;
}
