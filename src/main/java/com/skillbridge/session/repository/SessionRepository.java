package com.skillbridge.session.repository;

import com.skillbridge.session.domain.SessionStatus;
import com.skillbridge.session.domain.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByStatus(SessionStatus status);

    Long countByStatus(SessionStatus status);
}
