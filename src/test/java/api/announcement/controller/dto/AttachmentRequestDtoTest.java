package api.announcement.controller.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AttachmentRequestDtoTest {


    @Test
    void testAttachmentResponseDtoBuilder() {
        // Arrange: 테스트 데이터를 설정
        Long id = 1L;
        String fileName = "test-file.txt";
        String fileUrl = "http://example.com/test-file.txt";

        // Act: Builder를 이용하여 객체 생성
        AttachmentResponseDto dto = AttachmentResponseDto.builder()
                .id(id)
                .fileName(fileName)
                .fileUrl(fileUrl)
                .build();

        // Assert: 각 필드 값을 검증
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getFileName()).isEqualTo(fileName);
        assertThat(dto.getFileUrl()).isEqualTo(fileUrl);
    }
}