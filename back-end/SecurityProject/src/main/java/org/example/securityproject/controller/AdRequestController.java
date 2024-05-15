package org.example.securityproject.controller;

import org.example.securityproject.model.AdRequest;
import org.example.securityproject.service.AdRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ad-requests")
public class AdRequestController {
    @Autowired
    private AdRequestService adRequestService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<String> createAdRequest(@RequestBody AdRequest adRequest) {
        adRequestService.createAdRequest(adRequest);
        return new ResponseEntity<>("Ad request created successfully", HttpStatus.OK);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<List<AdRequest>> getAllAdRequests() {
        List<AdRequest> adRequests = adRequestService.getAllAdRequests();
        return new ResponseEntity<>(adRequests, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<AdRequest> getAdRequestById(@PathVariable Integer id) {
        Optional<AdRequest> adRequest = adRequestService.findById(id);
        return adRequest.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
