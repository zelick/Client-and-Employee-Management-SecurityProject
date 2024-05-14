package org.example.securityproject.controller;

import org.example.securityproject.model.AdRequest;
import org.example.securityproject.service.AdRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<AdRequest>> getAllAdRequests() {
        List<AdRequest> adRequests = adRequestService.getAllAdRequests();
        return new ResponseEntity<>(adRequests, HttpStatus.OK);
    }
}
