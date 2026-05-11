package com.skillbridge.service.impl;

import com.skillbridge.enums.SessionStatus;
import com.skillbridge.repository.MatchRepository;
import com.skillbridge.repository.SessionRepository;
import com.skillbridge.repository.SkillsRepository;
import com.skillbridge.repository.UserRepository;
import com.skillbridge.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final SkillsRepository skillsRepository;
    private final SessionRepository sessionRepository;
    private final MatchRepository matchRepository;

    @Override
    public Long getTotalUsers() {
        return userRepository.count();
    }

    @Override
    public Long getActiveSessions() {
        return sessionRepository.countByStatus(SessionStatus.ACTIVE);
    }

    @Override
    public Long getTotalSkills() {
        return skillsRepository.count();
    }

    @Override
    public Long getNewMatchesToday() {
        return matchRepository.countMatchesCreatedToday();
    }
}

