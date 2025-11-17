package com.example.rhythmfocusbackend.domain.gameresult.repository;

import com.example.rhythmfocusbackend.domain.gameresult.entity.GameResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameResultRepository extends JpaRepository<GameResultEntity, Long> {

    List<GameResultEntity> findByUserUsernameOrderByCreatedAt(String username);

    @Query("SELECT MAX(g.score) FROM GameResultEntity g WHERE g.user.username = :username")
    Integer findMaxScoreByUsername(@Param("username") String username);

    @Query("SELECT MAX(g.accuracy) FROM GameResultEntity g WHERE g.user.username = :user")
    Double findMaxAccuracyByUsername(@Param("username") String username);

    @Query("SELECT COUNT(g) FROM GameResultEntity g WHERE g.user.username = :name")
    Long countByUserUsername(@Param("username") String username);
}
