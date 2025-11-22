package com.example.rhythmfocusbackend.domain.diagnosis.entity;

import com.example.rhythmfocusbackend.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "diagnosis_result")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class DiagnosisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private String surveyType;

    @Column(nullable = false)
    private Boolean isPositive;

    @Column(length = 500, nullable = false)
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column
    private Integer totalScore;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public DiagnosisResult(UserEntity user, String surveyType, Boolean isPositive, String summary, String details, Integer totalScore) {
        this.user = user;
        this.surveyType = surveyType;
        this.isPositive = isPositive;
        this.summary = summary;
        this.details = details;
        this.totalScore = totalScore;
    }
}
