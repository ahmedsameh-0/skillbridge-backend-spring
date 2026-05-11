package com.skillbridge.dto;

import lombok.Data;

@Data
public class DashboardDTO {
    private long totalUsers;
    private long activeSessions;
    private long totalSkills;
    private long newMatchesToday;
}
