package com.shortstay.api.controller;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import com.shortstay.api.dto.response.ListingReportResponse;
import com.shortstay.api.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/report")
    public ResponseEntity<Page<ListingReportResponse>> getReport(
            @RequestParam String country,
            @RequestParam String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseEntity.ok(adminService.getListingsReport(country, city, page, size, username));
    }
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadListingsFile(@RequestParam("file") MultipartFile file) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String status = adminService.processListingFile(file, username);
        return ResponseEntity.ok(status);
    }

}