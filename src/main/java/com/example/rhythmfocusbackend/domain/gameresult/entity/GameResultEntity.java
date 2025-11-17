package com.example.rhythmfocusbackend.domain.gameresult.entity;

import com.example.rhythmfocusbackend.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="game_result_entity")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "difficulty", nullable = false)
    private String difficulty; // easy, normal, hard

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "accuracy", nullable = false)
    private Double accuracy;

    @Column(name = "max_combo", nullable = false)
    private Integer maxCombo;

    @Column(name = "clear_rhythm", nullable = false)
    private Integer clearRhythm;

    @Column(name = "perfect_count", nullable = false)
    private Integer perfectCount;

    @Column(name = "good_count", nullable = false)
    private Integer goodCount;

    @Column(name = "bad_count", nullable = false)
    private Integer badCount;

    @Column(name = "miss_count", nullable = false)
    private Integer missCount;

    @Column(name = "total_notes", nullable = false)
    private Integer totalNotes;

    @Column(name = "average_offset")
    private Double averageOffset;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdAt;
}
