package org.example.securityproject.service;

import lombok.AllArgsConstructor;
import org.example.securityproject.dto.AdDto;
import org.example.securityproject.model.Ad;
import org.example.securityproject.model.User;
import org.example.securityproject.repository.AdRepository;
import org.example.securityproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AdService {
    @Autowired
    private AdRepository adRepository;
    private UserRepository userRepository;

    public void createAd(AdDto ad) {
        Ad ad1 = new Ad();
        User user = userRepository.findByEmail(ad.getEmail());
        ad1.setUser(user);
        ad1.setSlogan(ad.getSlogan());
        ad1.setActiveFrom(ad.getActiveFrom());
        ad1.setActiveTo(ad.getActiveTo());
        ad1.setDescription(ad.getDescription());
        adRepository.save(ad1);
    }
}
