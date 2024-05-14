package org.example.securityproject.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.securityproject.model.AdRequest;
import org.example.securityproject.repository.AdRequestRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AdRequestService {

    @Autowired
    private AdRequestRepository adRequestRepository;

    public void createAdRequest(AdRequest adRequest) {
        adRequestRepository.save(adRequest);
    }

    public List<AdRequest> getAllAdRequests() {
        return adRequestRepository.findAll();
    }

    public Optional<AdRequest> findById(Integer id) {
        return adRequestRepository.findById(id);
    }

}
