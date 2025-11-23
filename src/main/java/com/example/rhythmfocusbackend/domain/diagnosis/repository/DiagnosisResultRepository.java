package com.example.rhythmfocusbackend.domain.diagnosis.repository;

import com.example.rhythmfocusbackend.domain.diagnosis.entity.DiagnosisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiagnosisResultRepository extends JpaRepository<DiagnosisResult, Long> {
}
