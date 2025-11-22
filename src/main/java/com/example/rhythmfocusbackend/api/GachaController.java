package com.example.rhythmfocusbackend.api;

import com.example.rhythmfocusbackend.domain.gacha.dto.GachaRequestDTO;
import com.example.rhythmfocusbackend.domain.gacha.dto.GachaResponseDTO;
import com.example.rhythmfocusbackend.domain.gacha.entity.GachaResultEntity;
import com.example.rhythmfocusbackend.domain.gacha.service.GachaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gacha")
@RequiredArgsConstructor
public class GachaController {

    private final GachaService gachaService;

    @PostMapping()
    public ResponseEntity<GachaResponseDTO> performGacha(@RequestBody GachaRequestDTO gachaRequestDTO) {
        GachaResponseDTO response = gachaService.performGacha(gachaRequestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<GachaResultEntity>> getGachaHistory() {
        List<GachaResultEntity> history = gachaService.getUserGachaHistory();
        return ResponseEntity.ok(history);
    }
}
