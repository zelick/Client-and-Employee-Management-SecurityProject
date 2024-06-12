package org.example.securityproject.service;

import lombok.AllArgsConstructor;
import org.example.securityproject.model.Ad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.securityproject.model.AdRequest;
import org.example.securityproject.repository.AdRequestRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AdRequestService {

    @Autowired
    private AdRequestRepository adRequestRepository;
    private UserDataEncryptionService userDataEncryptionService;

    public void createAdRequest(AdRequest adRequest) throws Exception {
        userDataEncryptionService.encryptAdRequestData(adRequest);

       //adRequestRepository.save(adRequest);
    }

    public List<AdRequest> getAllAdRequests() throws Exception {
        List<AdRequest> adRequests = new ArrayList<>();
        for (AdRequest adRequest : adRequestRepository.findAll()) {
            adRequest.setEmail(userDataEncryptionService.decryptData(adRequest.getEmail()));
            adRequests.add(adRequest);
        }

        return adRequests;
    }

    public Optional<AdRequest> findById(Integer id) {
        return adRequestRepository.findById(id);
    }

}
