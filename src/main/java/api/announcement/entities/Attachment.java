package api.announcement.entities;


import api.announcement.controller.dto.AttachmentResponseDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Attachment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    public AttachmentResponseDto toDto() {
        return AttachmentResponseDto.builder()
                .id(this.getId())
                .fileUrl(this.fileUrl)
                .fileName(this.getFileName())
                .build();
    }
}
