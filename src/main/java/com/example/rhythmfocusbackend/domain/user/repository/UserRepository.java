package com.example.rhythmfocusbackend.domain.user.repository;

import com.example.rhythmfocusbackend.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Boolean existsByUsername(String username);

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByUsernameAndIsLockAndIsSocial(String username, Boolean isLock, Boolean isSocial);

    Optional<UserEntity> findByUsernameAndIsLock(String username, Boolean isLock);

    @Transactional
    void deleteByUsername(String username);
}
