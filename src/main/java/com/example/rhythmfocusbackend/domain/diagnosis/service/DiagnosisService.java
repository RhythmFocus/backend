package com.example.rhythmfocusbackend.domain.diagnosis.service;

import com.example.rhythmfocusbackend.domain.diagnosis.dto.DiagnosisRequest;
import com.example.rhythmfocusbackend.domain.diagnosis.dto.DiagnosisResponse;
import com.example.rhythmfocusbackend.domain.diagnosis.dto.DiagnosisResponse.DiagnosisDetail;
import com.example.rhythmfocusbackend.domain.diagnosis.entity.DiagnosisResult;
import com.example.rhythmfocusbackend.domain.diagnosis.repository.DiagnosisResultRepository;
import com.example.rhythmfocusbackend.domain.user.entity.UserEntity;
import com.example.rhythmfocusbackend.domain.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiagnosisService {

    private final DiagnosisResultRepository diagnosisResultRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public DiagnosisResponse diagnose(DiagnosisRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        Map<String, Integer> answers = request.getAnswers() != null ? request.getAnswers() : Collections.emptyMap();

        DiagnosisResponse response;
        if ("ASRS".equals(request.getSurveyType())) {
            response = calculateASRS(answers);
        } else if ("SNAP_IV".equals(request.getSurveyType())) {
            response = calculateSNAPIV(answers, request.getGender());
        } else {
            throw new IllegalArgumentException("지원하지 않는 진단 도구입니다.");
        }

        saveDiagnosisResult(user, request.getSurveyType(), response);

        return response;
    }

    private void saveDiagnosisResult(UserEntity user, String surveyType, DiagnosisResponse response) {
        try {
            String detailsJson = objectMapper.writeValueAsString(response.getDetails());

            DiagnosisResult result = DiagnosisResult.builder()
                    .user(user)
                    .surveyType(surveyType)
                    .isPositive(response.isPositive())
                    .summary(response.getSummary())
                    .details(detailsJson)
                    .totalScore(null)
                    .build();

            diagnosisResultRepository.save(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize diagnosis details to JSON", e);
        }
    }

    // 1. ASRS 채점
    private DiagnosisResponse calculateASRS(Map<String, Integer> answers) {
        int positiveCount = 0;
        int[] thresholds = {0, 2, 2, 2, 3, 3, 3};

        for (int i = 1; i <= 6; i++) {
            // String Key("1")를 사용하여 조회
            int score = answers.getOrDefault(String.valueOf(i), 0);
            if (score >= thresholds[i]) {
                positiveCount++;
            }
        }

        boolean isPositive = positiveCount >= 4;

        DiagnosisDetail detail = DiagnosisDetail.builder()
                .label("Part A (선별 문항) 양성 개수")
                .score(positiveCount)
                .threshold(4)
                .status(isPositive ? "위험" : "양호")
                .build();

        return DiagnosisResponse.builder()
                .type("ASRS")
                .isPositive(isPositive)
                .summary(isPositive ? "성인 ADHD 위험군일 가능성이 높습니다." : "정상 범위입니다.")
                .details(List.of(detail))
                .build();
    }

    // 2. SNAP-IV 채점
    private DiagnosisResponse calculateSNAPIV(Map<String, Integer> answers, String gender) {
        boolean isMale = "male".equalsIgnoreCase(gender);
        double inattentionCutoff = isMale ? 1.38 : 1.02;
        double hyperCutoff = isMale ? 0.98 : 0.62;

        double inattentionScore = calculateAverage(answers, 1, 9);
        double hyperScore = calculateAverage(answers, 10, 18);

        List<DiagnosisDetail> details = new ArrayList<>();

        details.add(DiagnosisDetail.builder()
                .label("주의력 결핍 (Inattention)")
                .score(inattentionScore)
                .threshold(inattentionCutoff)
                .status(inattentionScore > inattentionCutoff ? "위험" : "양호")
                .build());

        details.add(DiagnosisDetail.builder()
                .label("과잉행동/충동성 (Hyperactivity)")
                .score(hyperScore)
                .threshold(hyperCutoff)
                .status(hyperScore > hyperCutoff ? "위험" : "양호")
                .build());

        boolean isPositive = details.stream().anyMatch(d -> "위험".equals(d.getStatus()));

        return DiagnosisResponse.builder()
                .type("SNAP_IV")
                .isPositive(isPositive)
                .summary(isPositive ? "또래 아동 대비 ADHD 성향이 높게 측정되었습니다." : "일반적인 수준입니다.")
                .details(details)
                .build();
    }

    // 평균 계산 (String Key 대응)
    private double calculateAverage(Map<String, Integer> answers, int start, int end) {
        double sum = 0;
        int count = 0;
        for (int i = start; i <= end; i++) {
            // String.valueOf(i)로 키 변환
            sum += answers.getOrDefault(String.valueOf(i), 0);
            count++;
        }
        if (count == 0) return 0.0;
        return Math.round((sum / count) * 100.0) / 100.0;
    }
}