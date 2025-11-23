package com.example.rhythmfocusbackend.api;

import com.example.rhythmfocusbackend.domain.diagnosis.dto.DiagnosisRequest;
import com.example.rhythmfocusbackend.domain.diagnosis.dto.DiagnosisResponse;
import com.example.rhythmfocusbackend.domain.diagnosis.service.DiagnosisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/diagnosis")
@RequiredArgsConstructor
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    @PostMapping("/submit")
    public ResponseEntity<DiagnosisResponse> submitDiagnosis(@RequestBody DiagnosisRequest request) {
        // TODO: 사용자 ID 저장 로직 추가
        DiagnosisResponse response = diagnosisService.diagnose(request);
        return ResponseEntity.ok(response);
    }
}