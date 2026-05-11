package com.skillbridge.repository;

import com.skillbridge.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByUserId(Long userId);

    List<Match> findByUserIdAndStatus(Long userId, String status);

    List<Match> findByMatchedUserId(Long matchedUserId);

    List<Match> findByUserIdAndMatchedUserId(Long userId, Long matchedUserId);

    // Find matches created today (useful for dashboard new matches)
    @Query("SELECT m FROM Match m WHERE YEAR(m.createdAt) = YEAR(CURRENT_DATE) AND MONTH(m.createdAt) = MONTH(CURRENT_DATE) AND DAY(m.createdAt) = DAY(CURRENT_DATE)")
    List<Match> findMatchesCreatedToday();

    // Count matches created today
    @Query("SELECT COUNT(m) FROM Match m WHERE YEAR(m.createdAt) = YEAR(CURRENT_DATE) AND MONTH(m.createdAt) = MONTH(CURRENT_DATE) AND DAY(m.createdAt) = DAY(CURRENT_DATE)")
    Long countMatchesCreatedToday();
}

