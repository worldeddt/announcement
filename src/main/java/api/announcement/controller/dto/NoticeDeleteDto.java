package api.announcement.controller.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NoticeDeleteDto {
    private Long userId;
}
