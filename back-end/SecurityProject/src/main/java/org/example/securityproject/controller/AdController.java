package org.example.securityproject.controller;

import org.example.securityproject.dto.AdDto;
import org.example.securityproject.model.Ad;
import org.example.securityproject.model.AdRequest;
import org.example.securityproject.service.AdRequestService;
import org.example.securityproject.service.AdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ads")
public class AdController {
    @Autowired
    private AdService adService;

    @PostMapping
    public ResponseEntity<String> createAd(@RequestBody AdDto ad) {
        adService.createAd(ad);
        return new ResponseEntity<>("Ad request created successfully", HttpStatus.OK);
    }
}
