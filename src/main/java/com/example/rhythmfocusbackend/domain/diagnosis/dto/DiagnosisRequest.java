package com.example.rhythmfocusbackend.domain.diagnosis.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Map;

@Getter
@NoArgsConstructor
public class DiagnosisRequest {
    private String surveyType; // "ASRS" or "SNAP-IV"
    private Map<String, Integer> answers; // 문항번호: 점수
    private String gender; // "male" or "female" (SNAP-IV용)
}