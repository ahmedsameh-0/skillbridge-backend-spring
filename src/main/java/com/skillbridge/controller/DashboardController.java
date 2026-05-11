package com.skillbridge.controller;

import com.skillbridge.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/get-active-sessions")
    public ResponseEntity<?> getActiveSessions() {
        return ResponseEntity.ok(dashboardService.getActiveSessions());
    }

    @GetMapping("/get-users")
    public ResponseEntity<?> getTotalUsers() {
        return ResponseEntity.ok(dashboardService.getTotalUsers());
    }

    @GetMapping("/get-total-skills")
    public ResponseEntity<?> getTotalSkills() {
        return ResponseEntity.ok(dashboardService.getTotalSkills());
    }

    @GetMapping("/get-new-matches")
    public ResponseEntity<?> getNewMatchesToday() {
        return ResponseEntity.ok(dashboardService.getNewMatchesToday());
    }
}

