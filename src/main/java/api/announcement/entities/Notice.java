package api.announcement.entities;


import api.announcement.controller.dto.NoticeResponseDto;
import api.announcement.controller.dto.NoticeUpdateRequestDto;
import api.announcement.enums.NoticeStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class Notice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 10000)
    private String content;

    @JsonProperty("startDate")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(nullable = false)
    private LocalDateTime startDate;

    @JsonProperty("endDate")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(nullable = false)
    private LocalDateTime endDate;

    private int viewCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_user")
    private User createdUser;

    private Long recentUpdateUser;

    @OneToMany(mappedBy = "notice",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST},
            orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NoticeStatus status;

    public NoticeResponseDto toDto() {
        return NoticeResponseDto.builder()
                .id(this.getId())
                .title(this.getTitle())
                .content(this.getContent())
                .viewCount(this.getViewCount())
                .startDate(this.getStartDate())
                .endDate(this.getEndDate())
                .createdUser(this.getCreatedUser())
                .recentUpdateUser(this.getRecentUpdateUser())
                .attachments(
                        this.getAttachments()
                                .stream()
                                .map(Attachment::toDto)
                                .toList()
                )
                .build();
    }

    public Notice toUpdate(NoticeUpdateRequestDto noticeUpdateRequestDto) {

        boolean updateUserColumn = false;

        if (noticeUpdateRequestDto.getTitle() != null) {
            setTitle(noticeUpdateRequestDto.getTitle());
            updateUserColumn = true;
        }

        if (noticeUpdateRequestDto.getContent() != null) {
            setContent(noticeUpdateRequestDto.getContent());
            updateUserColumn = true;
        }

        if (noticeUpdateRequestDto.getStartDate() != null) {
            setStartDate(noticeUpdateRequestDto.getStartDate());
            updateUserColumn = true;
        }

        if (noticeUpdateRequestDto.getEndDate() != null) {
            setEndDate(noticeUpdateRequestDto.getEndDate());
            updateUserColumn = true;
        }

        if (noticeUpdateRequestDto.isViewed()) {
            setViewCount(getViewCount() + 1);
            updateUserColumn = true;
        }

        if (updateUserColumn) {
            setRecentUpdateUser(noticeUpdateRequestDto.getUpdateUserId());
        }

        return this;
    }
}
