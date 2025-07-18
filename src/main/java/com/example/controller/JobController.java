package com.example.controller;

import com.example.api.JobRequestDTO;
import com.example.api.JobResponseDTO;
import com.example.model.user.User;
import com.example.service.JobService;
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
@RequestMapping("/api/v1/jobs")
@Validated
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @GetMapping("/{id}")
    public ResponseEntity<JobResponseDTO> findById(@PathVariable("id") Long id) {
        var job = jobService.findById(id);
        if (job == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(job);
    }

    @GetMapping
    public ResponseEntity<List<JobResponseDTO>> findAll(@RequestParam(required = false) String keyword) {
        var jobs = jobService.findAll(keyword);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/employer/{employerId}")
    public ResponseEntity<List<JobResponseDTO>> findAllByEmployer(@PathVariable("employerId") Long employerId) {
        var jobs = jobService.findAllByEmployer(employerId);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('EMPLOYER')")
    public ResponseEntity<List<JobResponseDTO>> findAllByCurrentEmployer(
            @AuthenticationPrincipal UserDetails userDetails) {
        var userId = getUserId(userDetails);
        var jobs = jobService.findAllByEmployer(userId);
        return ResponseEntity.ok(jobs);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EMPLOYER')")
    public ResponseEntity<JobResponseDTO> create(@RequestBody @Valid JobRequestDTO dto, BindingResult bindingResult,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        var userId = getUserId(userDetails);
        var jobCreated = jobService.create(dto, userId);
        return ResponseEntity.ok(jobCreated);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPLOYER')")
    public ResponseEntity<JobResponseDTO> update(@PathVariable("id") Long id,
                                                 @RequestBody JobRequestDTO dto,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        var userId = getUserId(userDetails);
        var jobCreated = jobService.update(id, dto, userId);
        return ResponseEntity.ok(jobCreated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPLOYER')")
    public ResponseEntity<?> update(@PathVariable("id") Long id,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        var userId = getUserId(userDetails);
        jobService.delete(id, userId);
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
