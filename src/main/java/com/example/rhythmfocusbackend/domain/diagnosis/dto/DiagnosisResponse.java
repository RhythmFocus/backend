package com.example.rhythmfocusbackend.domain.diagnosis.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class DiagnosisResponse {
    private String type;
    @JsonProperty("isPositive")
    private boolean isPositive; // 위험군 여부
    private String summary;     // 한줄 요약
    private List<DiagnosisDetail> details; // 세부 항목

    @Getter
    @Builder
    public static class DiagnosisDetail {
        private String label;
        private double score;
        private double threshold;
        private String status; // "양호", "위험"
    }
}