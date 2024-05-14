package org.example.securityproject.controller;

import org.example.securityproject.model.AdRequest;
import org.example.securityproject.service.AdRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ad-requests")
public class AdRequestController {
    @Autowired
    private AdRequestService adRequestService;

    @PostMapping
    public ResponseEntity<String> createAdRequest(@RequestBody AdRequest adRequest) {
        adRequestService.createAdRequest(adRequest);
        return new ResponseEntity<>("Ad request created successfully", HttpStatus.OK);
    }
}
