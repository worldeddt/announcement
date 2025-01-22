package api.announcement.controller.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AttachmentRequestDtoTest {
    @Test
    void testAttachmentRequestDtoBuilder() {

        String fileName = "sample-file.txt";
        String filePath = "/uploads/sample-file.txt";

        AttachmentRequestDto dto = AttachmentRequestDto.builder()
                .fileName(fileName)
                .filePath(filePath)
                .build();

        assertThat(dto).isNotNull();
        assertThat(dto.getFileName()).isEqualTo(fileName);
        assertThat(dto.getFilePath()).isEqualTo(filePath);
    }
}