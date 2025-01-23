package api.announcement.entities;


import api.announcement.controller.dto.AttachmentResponseDto;
import api.announcement.controller.dto.AttachmentUpdateRequestDto;
import api.announcement.enums.AttachmentStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@Table
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@RedisHash(value = "Attachment", timeToLive = 180) // TTL: 180초
public class Attachment extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    @JsonBackReference
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
