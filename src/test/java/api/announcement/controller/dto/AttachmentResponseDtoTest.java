package api.announcement.controller.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AttachmentResponseDtoTest {

    @Test
    void testAttachmentResponseDtoBuilder() {
        Long id = 1L;
        String fileName = "test-file.txt";
        String fileUrl = "http://example.com/test-file.txt";

        AttachmentResponseDto dto = AttachmentResponseDto.builder()
                .id(id)
                .fileName(fileName)
                .fileUrl(fileUrl)
                .build();

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getFileName()).isEqualTo(fileName);
        assertThat(dto.getFileUrl()).isEqualTo(fileUrl);
    }
}