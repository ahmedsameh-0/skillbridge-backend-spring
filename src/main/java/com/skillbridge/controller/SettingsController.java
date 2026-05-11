package com.skillbridge.controller;

import com.skillbridge.dto.ProfileDTO;
import com.skillbridge.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/settings")
public class SettingsController {

    private final SettingsService settingsService;

    @PutMapping("/profile")
    public  ResponseEntity<Mono<ProfileDTO>> updateProfile(@RequestBody @Validated ProfileDTO profileDTO) {
        return ResponseEntity.ok(settingsService.updateProfile(profileDTO));
    }

    @PutMapping("/password")
    public ResponseEntity<Mono<?>> updatePassword(@RequestBody Map<String, String> passwords) {
        return ResponseEntity.ok(settingsService.updatePassword(passwords.get("oldPassword"), passwords.get("newPassword")));
    }
}
