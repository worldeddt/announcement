package api.announcement.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentResponseDto {
    private Long id;
    private String fileName;
    private String fileUrl;
}
