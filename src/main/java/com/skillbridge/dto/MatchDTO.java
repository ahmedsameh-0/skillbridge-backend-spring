package com.skillbridge.dto;

import lombok.Data;

@Data
public class MatchDTO {
    private Long id;
    private Long userId;
    private Long matchedUserId;
    private Long skillId;
    private Double matchScore;
}
