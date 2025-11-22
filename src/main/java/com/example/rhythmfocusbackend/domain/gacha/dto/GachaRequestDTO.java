package com.example.rhythmfocusbackend.domain.gacha.dto;

public record GachaRequestDTO(
        Integer count // 1 or 5 (뽑기 횟수)
) {
}
