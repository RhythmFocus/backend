package com.example.rhythmfocusbackend.domain.gacha.service;

import com.example.rhythmfocusbackend.domain.gacha.dto.GachaRequestDTO;
import com.example.rhythmfocusbackend.domain.gacha.dto.GachaResponseDTO;
import com.example.rhythmfocusbackend.domain.gacha.entity.GachaResultEntity;
import com.example.rhythmfocusbackend.domain.gacha.repository.GachaRepository;
import com.example.rhythmfocusbackend.domain.user.entity.UserEntity;
import com.example.rhythmfocusbackend.domain.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class GachaService {

    private final GachaRepository gachaRepository;
    private final UserRepository userRepository;
    private final Random random = new Random();
    private final ObjectMapper objectMapper;

    // 먹이 아이템 목록 (일단 5개)
    private static final List<String> FOOD_ITEMS = List.of(
            "Burger", "Hotdog", "Chicken", "Pizza", "Donut"
    );

    @Transactional
    public GachaResponseDTO performGacha(GachaRequestDTO gachaRequestDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user= userRepository.findByUsernameAndIsLock(username, false)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        int count = gachaRequestDTO.count();

        user.useTickets(count);

        // 먹이 아이템 뽑기
        List<String> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            items.add(getRandomItem());
        }

        try {
            String itemJson = objectMapper.writeValueAsString(items);
            GachaResultEntity gachaEntity = GachaResultEntity.builder()
                    .user(user)
                    .usedTickets(count)
                    .items(itemJson)
                    .build();

            gachaRepository.save(gachaEntity);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("아이템 저장 실패", e);
        }

        return new GachaResponseDTO(count, user.getTickets(), items);
    }

    private String getRandomItem() {
        int index = random.nextInt(FOOD_ITEMS.size());
        return FOOD_ITEMS.get(index);
    }

    public List<GachaResultEntity> getUserGachaHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return gachaRepository.findByUserUsernameOrderByCreatedDateDesc(username);
    }
}
