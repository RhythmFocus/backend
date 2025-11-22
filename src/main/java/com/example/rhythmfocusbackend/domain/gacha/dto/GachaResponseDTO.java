package com.example.rhythmfocusbackend.domain.gacha.dto;

import java.util.List;

public record GachaResponseDTO(
        Integer usedTickets,
        Integer remainingTickets,
        List<String> items
) {
}
