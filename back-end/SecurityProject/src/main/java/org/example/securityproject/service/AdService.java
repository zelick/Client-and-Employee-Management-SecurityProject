package org.example.securityproject.service;

import lombok.AllArgsConstructor;
import org.example.securityproject.dto.AdDto;
import org.example.securityproject.model.Ad;
import org.example.securityproject.model.User;
import org.example.securityproject.repository.AdRepository;
import org.example.securityproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdService {
    @Autowired
    private AdRepository adRepository;
    private UserRepository userRepository;
    private UserDataEncryptionService userDataEncryptionService;

    public void createAd(AdDto ad) throws Exception {
        Ad ad1 = new Ad();
        //User user = userRepository.findByEmail(ad.getEmail());
        User user = userDataEncryptionService.findEncryptedUserByEmail(ad.getEmail());

        ad1.setUser(user);
        ad1.setSlogan(ad.getSlogan());
        ad1.setActiveFrom(ad.getActiveFrom());
        ad1.setActiveTo(ad.getActiveTo());
        ad1.setDescription(ad.getDescription());
        adRepository.save(ad1);
    }

    public List<AdDto> getAllAds() {
        List<Ad> ads = adRepository.findAll();
        List<AdDto> adsDtos = new ArrayList<>();

        for (Ad ad: ads) {
            AdDto adDto = new AdDto();
            adDto.setActiveFrom(ad.getActiveFrom());
            adDto.setName(ad.getUser().getName());
            adDto.setDescription(ad.getDescription());
            adDto.setSlogan(ad.getSlogan());
            adDto.setSurname(ad.getUser().getSurname());
            adDto.setActiveTo(ad.getActiveTo());
            adDto.setEmail(ad.getUser().getEmail());
            adsDtos.add(adDto);
        }

        return adsDtos;
    }

    public List<AdDto> getAllAdsByEmail(String email) throws Exception {
        String encryptedEmail = userDataEncryptionService.encryptData(email);
        List<Ad> ads = adRepository.findAllByUser_Email(encryptedEmail);
        return ads.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private AdDto mapToDto(Ad ad) {
        AdDto adDto = new AdDto();
        adDto.setEmail(ad.getUser().getEmail());
        adDto.setSlogan(ad.getSlogan());
        adDto.setActiveFrom(ad.getActiveFrom());
        adDto.setActiveTo(ad.getActiveTo());
        adDto.setDescription(ad.getDescription());
        return adDto;
    }
}
