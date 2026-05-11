package com.skillbridge.repository;

import com.skillbridge.entity.Session;
import com.skillbridge.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByStatus(SessionStatus status);

    Long countByStatus(SessionStatus status);
}
