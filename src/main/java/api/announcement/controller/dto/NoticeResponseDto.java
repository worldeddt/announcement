package api.announcement.controller.dto;


import api.announcement.entities.Attachment;
import api.announcement.entities.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class NoticeResponseDto {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private int viewCount = 0;
    private User createdUser;

    private List<AttachmentResponseDto> attachments;
}
