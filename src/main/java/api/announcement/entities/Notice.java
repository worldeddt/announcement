package api.announcement.entities;


import api.announcement.controller.dto.AttachmentResponseDto;
import api.announcement.controller.dto.NoticeResponseDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class Notice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 10000)
    private String content;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    private int viewCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_user")
    private User createdUser;

    @OneToMany(mappedBy = "notice",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST},
            orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    public NoticeResponseDto toDto() {
        return NoticeResponseDto.builder()
                .id(this.getId())
                .title(this.getTitle())
                .content(this.getContent())
                .viewCount(this.getViewCount())
                .startDate(this.getStartDate())
                .endDate(this.getEndDate())
                .createdUser(this.getCreatedUser())
                .attachments(
                        this.getAttachments()
                                .stream()
                                .map(Attachment::toDto)
                                .toList()
                )
                .build();
    }
}