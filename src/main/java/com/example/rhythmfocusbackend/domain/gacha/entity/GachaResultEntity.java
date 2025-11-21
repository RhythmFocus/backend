package com.example.rhythmfocusbackend.domain.gacha.entity;

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
@Table(name = "gacha_result_entity")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GachaResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "used_tickets", nullable = false)
    private Integer usedTickets; // 1 or 5

    @Column(name = "items", nullable = false, length = 500)
    private String items; // 획득한 우왕이 먹이 아이템 목록

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}
