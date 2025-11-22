package com.example.rhythmfocusbackend.domain.user.dto;

public record UserResponseDTO(String username, Boolean social, String nickname, String email, Integer level, Integer tickets) {
}
