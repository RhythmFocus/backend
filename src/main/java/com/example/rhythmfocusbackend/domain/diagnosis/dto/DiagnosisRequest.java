package com.example.rhythmfocusbackend.domain.diagnosis.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
public class DiagnosisRequest {
    private String surveyType; //
    private Map<String, Integer> answers; // 문항번호: 점수
    private String gender; // "male" or "female" (SNAP-IV용)
}