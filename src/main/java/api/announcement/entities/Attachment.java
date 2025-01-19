package api.announcement.entities;


import api.announcement.controller.dto.AttachmentResponseDto;
import api.announcement.controller.dto.AttachmentUpdateRequestDto;
import api.announcement.enums.AttachmentStatus;
import jakarta.persistence.*;
import lombok.*;

@Data
@Table
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AttachmentStatus status;

    private Long recentUpdateUser;

    public AttachmentResponseDto toDto() {
        return AttachmentResponseDto.builder()
                .id(this.getId())
                .fileUrl(this.getFileUrl())
                .fileName(this.getFileName())
                .build();
    }

    public Attachment toUpdate(AttachmentUpdateRequestDto attachmentUpdateRequestDto) {

        boolean updateUserColumn = false;

        if (attachmentUpdateRequestDto.getFileUrl() != null) {
            setFileUrl(attachmentUpdateRequestDto.getFileUrl());
            updateUserColumn = true;
        }

        if (attachmentUpdateRequestDto.getFileName() != null) {
            setFileName(attachmentUpdateRequestDto.getFileName());
            updateUserColumn = true;
        }

        if (updateUserColumn) {
            setRecentUpdateUser(attachmentUpdateRequestDto.getUpdateUserId());
        }

        return this;
    }
}
