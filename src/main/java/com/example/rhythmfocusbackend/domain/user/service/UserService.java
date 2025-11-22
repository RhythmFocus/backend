package com.example.rhythmfocusbackend.domain.user.service;

import com.example.rhythmfocusbackend.domain.jwt.service.JWTService;
import com.example.rhythmfocusbackend.domain.user.dto.UserRequestDTO;
import com.example.rhythmfocusbackend.domain.user.dto.UserResponseDTO;
import com.example.rhythmfocusbackend.domain.user.entity.UserEntity;
import com.example.rhythmfocusbackend.domain.user.entity.UserRoleType;
import com.example.rhythmfocusbackend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    // 회원 존재 여부
    @Transactional(readOnly = true)
    public Boolean existUser(UserRequestDTO dto) {
        return userRepository.existsByUsername(dto.getUsername());
    }

    // 자체 로그인 회원 가입
    @Transactional
    public Long addUser(UserRequestDTO dto) {

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("이미 유저가 존재합니다.");
        }

        UserEntity entity = UserEntity.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .isLock(false)
                .isSocial(false)
                .roleType(UserRoleType.USER)
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .level(1)
                .tickets(0)
                .build();

        return userRepository.save(entity).getId();
    }

    // 자체 로그인
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity entity = userRepository.findByUsernameAndIsLockAndIsSocial(username, false, false)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return User.builder()
                .username(entity.getUsername())
                .password(entity.getPassword())
                .roles(entity.getRoleType().name())
                .accountLocked(entity.getIsLock())
                .build();
    }

    // 자체 로그인 회원 정보 수정
    @Transactional
    public Long updateUser(UserRequestDTO dto) throws AccessDeniedException{

        // 본인만 수정 가능 검증
        String sessionUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!sessionUsername.equals(dto.getUsername())) {
            throw new AccessDeniedException("본인 계정만 수정 가능");
        }

        // 조회
        UserEntity entity = userRepository.findByUsernameAndIsLockAndIsSocial(dto.getUsername(), false, false)
                .orElseThrow(() -> new UsernameNotFoundException(dto.getUsername()));

        // 회원 정보 수정
        entity.updateUser(dto);

        return userRepository.save(entity).getId();
    }

    // 자체/소셜 로그인 회원 탈퇴
    @Transactional
    public void deleteUser(UserRequestDTO dto) throws AccessDeniedException{

        // 본인 또는 어드민만 삭제 가능 검증
        SecurityContext context = SecurityContextHolder.getContext();
        String sessionUsername = context.getAuthentication().getName();
        String sessionRole = context.getAuthentication().getAuthorities().iterator().next().getAuthority();

        boolean isOwner = sessionUsername.equals(dto.getUsername());
        boolean isAdmin = sessionRole.equals("ROLE_" + UserRoleType.ADMIN.name());

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("본인 또는 관리자만 삭제할 수 있습니다.");
        }

        userRepository.deleteByUsername(dto.getUsername());

        jwtService.removeRefreshUser(dto.getUsername());
    }

    // 자체/소셜 유저 정보 조회
    @Transactional(readOnly = true)
    public UserResponseDTO readUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity entity = userRepository.findByUsernameAndIsLock(username, false)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return new UserResponseDTO(username, entity.getIsSocial(), entity.getNickname(), entity.getEmail(), entity.getLevel(), entity.getTickets());
    }
}
