package org.example.securityproject.controller;

import org.example.securityproject.dto.AdDto;
import org.example.securityproject.model.Ad;
import org.example.securityproject.model.AdRequest;
import org.example.securityproject.service.AdRequestService;
import org.example.securityproject.service.AdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ads")
public class AdController {
    @Autowired
    private AdService adService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<String> createAd(@RequestBody AdDto ad) {
        adService.createAd(ad);
        return new ResponseEntity<>("Ad request created successfully", HttpStatus.OK);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<List<AdDto>> getAllAds() {
        List<AdDto> ads = adService.getAllAds();
        return new ResponseEntity<>(ads, HttpStatus.OK);
    }

    @GetMapping("/by-email")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<List<AdDto>> getAllAdsByEmail(@RequestParam String email) {
        List<AdDto> ads = adService.getAllAdsByEmail(email);
        return new ResponseEntity<>(ads, HttpStatus.OK);
    }

}
