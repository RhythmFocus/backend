package com.example.rhythmfocusbackend.api;

import com.example.rhythmfocusbackend.domain.jwt.dto.JWTResponseDTO;
import com.example.rhythmfocusbackend.domain.jwt.dto.RefreshRequestDTO;
import com.example.rhythmfocusbackend.domain.jwt.service.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JwtController {

    private final JWTService jwtService;

    // Refresh 토큰으로 Access 토큰 재발급 (Rotate 포함)
    @PostMapping(value = "/jwt/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JWTResponseDTO jwtRefreshApi(
            @Validated @RequestBody RefreshRequestDTO dto
    ) {
        return jwtService.refreshRotate(dto);
    }
}
