package com.example.rhythmfocusbackend.api;

import com.example.rhythmfocusbackend.domain.gameresult.dto.GameResultRequestDTO;
import com.example.rhythmfocusbackend.domain.gameresult.dto.UserGameStats;
import com.example.rhythmfocusbackend.domain.gameresult.entity.GameResultEntity;
import com.example.rhythmfocusbackend.domain.gameresult.service.GameResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/game-result")
@RequiredArgsConstructor
public class GameResultController {

    private final GameResultService gameResultService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> saveGameResult(
            @Validated @RequestBody GameResultRequestDTO dto
    ) {
        Long id =  gameResultService.saveGameResult(dto);
        return ResponseEntity.ok().body(Collections.singletonMap("id", id));
    }

    @GetMapping(value = "/list")
    public ResponseEntity<List<GameResultEntity>> getGameResults() {
        List<GameResultEntity> results = gameResultService.getUserGameResults();
        return ResponseEntity.ok(results);
    }

    @GetMapping(value = "/stats")
    public ResponseEntity<UserGameStats> getUserGameStats() {
        UserGameStats stats = gameResultService.getUserStats();
        return ResponseEntity.ok(stats);
    }

}
