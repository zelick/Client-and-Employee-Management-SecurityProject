package org.example.securityproject.service;

import lombok.AllArgsConstructor;
import org.example.securityproject.model.Ad;
import org.example.securityproject.repository.AdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AdService {
    @Autowired
    private AdRepository adRepository;

    public void createAd(Ad ad) {
        adRepository.save(ad);
    }
}
