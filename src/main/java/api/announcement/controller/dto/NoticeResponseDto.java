package api.announcement.controller.dto;


import api.announcement.entities.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class NoticeResponseDto {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private int viewCount;
    private User createdUser;
    private Long recentUpdateUser;

    private List<AttachmentResponseDto> attachments;
}
