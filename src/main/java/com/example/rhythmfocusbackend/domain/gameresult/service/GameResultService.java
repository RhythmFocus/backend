package com.example.rhythmfocusbackend.domain.gameresult.service;

import com.example.rhythmfocusbackend.domain.gameresult.dto.GameResultRequestDTO;
import com.example.rhythmfocusbackend.domain.gameresult.dto.UserGameStats;
import com.example.rhythmfocusbackend.domain.gameresult.entity.GameResultEntity;
import com.example.rhythmfocusbackend.domain.gameresult.repository.GameResultRepository;
import com.example.rhythmfocusbackend.domain.user.entity.UserEntity;
import com.example.rhythmfocusbackend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameResultService {

    private final GameResultRepository gameResultRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long saveGameResult(GameResultRequestDTO dto) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userRepository.findByUsernameAndIsLock(username, false)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        GameResultEntity gameResultEntity = GameResultEntity.builder()
                .user(user)
                .difficulty(dto.getDifficulty())
                .score(dto.getScore())
                .accuracy(dto.getAccuracy())
                .maxCombo(dto.getMaxCombo())
                .clearRhythm(dto.getClearRhythm())
                .perfectCount(dto.getPerfectCount())
                .goodCount(dto.getGoodCount())
                .badCount(dto.getBadCount())
                .missCount(dto.getMissCount())
                .totalNotes(dto.getTotalNotes())
                .averageOffset(dto.getAverageOffset())
                .build();

        return gameResultRepository.save(gameResultEntity).getId();
    }

    @Transactional(readOnly = true)
    public List<GameResultEntity> getUserGameResults() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return gameResultRepository.findByUserUsernameOrderByCreatedAt(username);
    }

    @Transactional(readOnly = true)
    public UserGameStats getUserStats() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Integer maxScore = gameResultRepository.findMaxScoreByUsername(username);
        Double  maxAccuracy = gameResultRepository.findMaxAccuracyByUsername(username);
        Long playCount = gameResultRepository.countByUserUsername(username);

        return new UserGameStats(maxScore, maxAccuracy, playCount);
    }
}
