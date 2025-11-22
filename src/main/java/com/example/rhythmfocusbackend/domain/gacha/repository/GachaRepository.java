package com.example.rhythmfocusbackend.domain.gacha.repository;

import com.example.rhythmfocusbackend.domain.gacha.entity.GachaResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GachaRepository extends JpaRepository<GachaResultEntity, Long> {
    List<GachaResultEntity> findByUserUsernameOrderByCreatedDateDesc(String username);
}
