package com.example.controller;

import com.example.api.ApplicationRequestDTO;
import com.example.api.ApplicationResponseDTO;
import com.example.model.user.User;
import com.example.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/applications")
@Validated
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping
    @PreAuthorize("hasAuthority('APPLICANT')")
    public ResponseEntity<List<ApplicationResponseDTO>> findAllByCurrentApplicant(
            @AuthenticationPrincipal UserDetails userDetails) {
        var userId = getUserId(userDetails);
        var applications = applicationService.findAllByApplicant(userId);
        return ResponseEntity.ok(applications);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('APPLICANT')")
    public ResponseEntity<?> create(@RequestBody @Valid ApplicationRequestDTO dto, BindingResult bindingResult,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        var userId = getUserId(userDetails);
        applicationService.applyApplicantToJob(userId, dto);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{jobId}")
    @PreAuthorize("hasAuthority('APPLICANT')")
    public ResponseEntity<?> remove(@PathVariable("jobId") Long jobId,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        var userId = getUserId(userDetails);
        applicationService.deleteApplication(userId, jobId);
        return ResponseEntity.accepted().build();
    }

    private Long getUserId(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        var user = (User) userDetails;
        return user.getId();
    }
}
