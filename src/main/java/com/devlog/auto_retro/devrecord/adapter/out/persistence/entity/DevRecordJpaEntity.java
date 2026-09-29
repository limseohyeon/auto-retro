package com.devlog.auto_retro.devrecord.adapter.out.persistence.entity;

import com.devlog.auto_retro.devrecord.domain.DevRecordStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "dev_records")
public class DevRecordJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String workContent;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String problemContent;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String solutionContent;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String learnedContent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DevRecordStatus status;

    @Column(nullable = false)
    private LocalDate recordDate;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    private DevRecordJpaEntity(
        Long userId,
        String title,
        String workContent,
        String problemContent,
        String solutionContent,
        String learnedContent,
        DevRecordStatus status,
        LocalDate recordDate
    ) {
        this.userId = userId;
        this.title = title;
        this.workContent = workContent;
        this.problemContent = problemContent;
        this.solutionContent = solutionContent;
        this.learnedContent = learnedContent;
        this.status = status;
        this.recordDate = recordDate;
    }

    public static DevRecordJpaEntity create(
        Long userId,
        String title,
        String workContent,
        String problemContent,
        String solutionContent,
        String learnedContent,
        DevRecordStatus status,
        LocalDate recordDate
    ) {
        return new DevRecordJpaEntity(
            userId, title, workContent, problemContent,
            solutionContent, learnedContent, status, recordDate
        );
    }
}
