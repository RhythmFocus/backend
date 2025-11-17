package com.example.rhythmfocusbackend.domain.gameresult.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameResultRequestDTO {

    @NotBlank
    private String difficulty;

    @NotNull
    @Min(0)
    private Integer score;

    @NotNull
    @Min(0)
    private Double accuracy;

    @NotNull
    @Min(0)
    private Integer maxCombo;

    @NotNull
    @Min(0)
    private Integer clearRhythm;

    @NotNull
    @Min(0)
    private Integer perfectCount;

    @NotNull
    @Min(0)
    private Integer goodCount;

    @NotNull
    @Min(0)
    private Integer badCount;

    @NotNull
    @Min(0)
    private Integer missCount;

    @NotNull
    @Min(0)
    private Integer totalNotes;

    private Double averageOffset;
}
