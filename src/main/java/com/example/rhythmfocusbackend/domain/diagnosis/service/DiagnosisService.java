package com.example.rhythmfocusbackend.domain.diagnosis.service;

import com.example.rhythmfocusbackend.domain.diagnosis.dto.DiagnosisRequest;
import com.example.rhythmfocusbackend.domain.diagnosis.dto.DiagnosisResponse;
import com.example.rhythmfocusbackend.domain.diagnosis.dto.DiagnosisResponse.DiagnosisDetail;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class DiagnosisService {

    public DiagnosisResponse diagnose(DiagnosisRequest request) {
        // answers null 방지
        Map<String, Integer> answers = request.getAnswers() != null ? request.getAnswers() : Collections.emptyMap();

        // 디버깅 로그
        System.out.println("=== 진단 요청 도착 ===");
        System.out.println("Type: " + request.getSurveyType());

        // 1. ASRS
        if ("ASRS".equals(request.getSurveyType())) {
            return calculateASRS(answers);
        }
        // 2. SNAP-IV
        else if ("SNAP_IV".equals(request.getSurveyType())) {
            return calculateSNAPIV(answers, request.getGender());
        }
        // 3. [NEW] CFQ 추가
        else if ("CFQ".equals(request.getSurveyType())) {
            return calculateCFQ(answers);
        }
        // 4. [NEW] BDI-II 추가
        else if ("BDI_II".equals(request.getSurveyType())) {
            return calculateBDIII(answers);
        }

        throw new IllegalArgumentException("지원하지 않는 진단 도구입니다: " + request.getSurveyType());
    }

    private DiagnosisResponse calculateBDIII(Map<String, Integer> answers) {
        int totalScore = answers.values().stream().mapToInt(Integer::intValue).sum();

        String summary;
        boolean isPositive;
        String status; // 약간의 우울, 경미한 우울, 중증도 우울, 심각한 우울

        if (totalScore >= 29) {
            summary = "심각한 우울 수준입니다. 전문가의 도움이 필요할 수 있습니다.";
            isPositive = true;
            status = "심각한 우울";
        } else if (totalScore >= 20) {
            summary = "중증도 우울 수준입니다. 전문가와 상담하는 것을 고려해 보세요.";
            isPositive = true;
            status = "중증도 우울";
        } else if (totalScore >= 14) {
            summary = "경미한 우울 수준입니다. 기분 변화에 주의를 기울이세요.";
            isPositive = true;
            status = "경미한 우울";
        } else { // 0-13
            summary = "약간의 우울 수준입니다. 현재 기분은 정상 범위에 가깝습니다.";
            isPositive = false;
            status = "약간의 우울";
        }

        // 상세 정보 생성
        DiagnosisDetail detail = DiagnosisDetail.builder()
                .label("BDI-II 총점")
                .score(totalScore)
                .threshold(14) // isPositive가 true로 판단되는 최소 점수 (경미한 우울 이상)
                .status(status)
                .build();

        return DiagnosisResponse.builder()
                .type("BDI_II")
                .isPositive(isPositive)
                .summary(summary)
                .details(List.of(detail))
                .build();
    }

    private DiagnosisResponse calculateCFQ(Map<String, Integer> answers) {
        int totalScore = 0;

        // 모든 문항의 점수 합산
        for (Integer score : answers.values()) {
            totalScore += score;
        }

        // [판정 기준]
        // 0~25: 낮음 (정상)
        // 26~43: 보통 (평균)
        // 44~100: 높음 (주의/위험) -> 여기서는 44점 이상을 '위험'으로 판정
        int threshold = 44;
        boolean isPositive = totalScore >= threshold;

        String summary;
        if (totalScore < 26) {
            summary = "일상생활에서의 인지적 실수가 거의 없는 편입니다.";
        } else if (totalScore < 44) {
            summary = "일상생활에서의 인지적 실수가 평균적인 수준입니다.";
        } else {
            summary = "일상생활에서 빈번한 인지적 실수가 관찰되며, 주의력 저하가 의심됩니다.";
        }

        // 상세 정보 생성
        DiagnosisDetail detail = DiagnosisDetail.builder()
                .label("CFQ 총점 (Total Score)")
                .score(totalScore)
                .threshold(threshold)
                .status(isPositive ? "위험" : "양호")
                .build();

        return DiagnosisResponse.builder()
                .type("CFQ")
                .isPositive(isPositive)
                .summary(summary)
                .details(List.of(detail))
                .build();
    }

    private DiagnosisResponse calculateASRS(Map<String, Integer> answers) {
        int positiveCount = 0;
        int[] thresholds = {0, 2, 2, 2, 3, 3, 3};

        for (int i = 1; i <= 6; i++) {
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

    private double calculateAverage(Map<String, Integer> answers, int start, int end) {
        double sum = 0;
        int count = 0;
        for (int i = start; i <= end; i++) {
            sum += answers.getOrDefault(String.valueOf(i), 0);
            count++;
        }
        if (count == 0) return 0.0;
        return Math.round((sum / count) * 100.0) / 100.0;
    }
}