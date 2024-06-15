package org.example.securityproject.service;

import lombok.AllArgsConstructor;
import org.example.securityproject.enums.RegistrationStatus;
import org.example.securityproject.model.AdRequest;
import org.example.securityproject.model.User;
import org.example.securityproject.repository.AdRequestRepository;
import org.example.securityproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.List;

@Service
@AllArgsConstructor
public class UserDataEncryptionService {
    @Autowired
    private UserRepository userRepository;
    private KeyStoreService keyStoreService;
    private AdRequestRepository adRequestRepository;

   /* @PostConstruct
    public void init() {
        System.out.println("PostConstruct method init() called.");
        try {
            encryptAllAdsRequests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public User findEncryptedUserByEmail(String email) throws Exception {
        SecretKey loadedSecretKey = keyStoreService.loadKeyFromKeyStore();
        String encryptedEmail =  EncryptionService.encrypt(email, loadedSecretKey);

        User encryptedUser = userRepository.findByEmail(encryptedEmail);
        return encryptedUser;
    }

    public User findByEmailAndRegistrationStatus(String email) throws Exception {
        User user = findEncryptedUserByEmail(email);

        if (user!= null && (user.getRegistrationStatus().equals(RegistrationStatus.PENDING) || user.getRegistrationStatus().equals(RegistrationStatus.ACCEPTED))) {
            return user;
        }
        return null;
    }

    public User findByEmailRejcetedUser(String email) throws Exception {
        User user = findEncryptedUserByEmail(email);

        if (user != null && (user.getRegistrationStatus().equals(RegistrationStatus.REJECTED))) {
            return user;
        }
        return null;
    }

    //KO ZELI DA KORISTI USERA SAMO POZOVE DECRYPT USER DATA
    //PRVO findEncryptedUserByEmail I ONDA KAD GA NADJE PROSLEDI SE U OVU METODU
    public User decryptUserData(User encryptedUser) throws Exception {
        SecretKey loadedSecretKey = keyStoreService.loadKeyFromKeyStore();
        User decryptedUser = encryptedUser;

        String decryptedEmail = EncryptionService.decrypt(encryptedUser.getEmail(), loadedSecretKey);
        String decryptedName = EncryptionService.decrypt(encryptedUser.getName(), loadedSecretKey);
        String decryptedSurname = EncryptionService.decrypt(encryptedUser.getSurname(), loadedSecretKey);
        String decryptedCity = EncryptionService.decrypt(encryptedUser.getCity(), loadedSecretKey);
        String decryptedCountry = EncryptionService.decrypt(encryptedUser.getCountry(), loadedSecretKey);
        String decryptedAddress = EncryptionService.decrypt(encryptedUser.getAddress(), loadedSecretKey);
        String decryptedPhoneNumber = EncryptionService.decrypt(encryptedUser.getPhoneNumber(), loadedSecretKey);

        decryptedUser.setEmail(decryptedEmail);
        decryptedUser.setName(decryptedName);
        decryptedUser.setSurname(decryptedSurname);
        decryptedUser.setCity(decryptedCity);
        decryptedUser.setCountry(decryptedCountry);
        decryptedUser.setAddress(decryptedAddress);
        decryptedUser.setPhoneNumber(decryptedPhoneNumber);

        return decryptedUser;
    }

    public void encryptAllAdsRequests() throws Exception {
        List<AdRequest> adRequests = adRequestRepository.findAll();
        int i = 0;

        for (AdRequest ar : adRequests) {
            encryptAdRequestData(ar);
            i++;
        }

        System.out.println("----------------- USPESNO ENKRIPTOVANJE SVIH AD REQUESTOVA ------------");
        System.out.println("ima ih: " + i);
    }

    public void encryptAdRequestData(AdRequest adRequest) throws Exception {
        SecretKey loadedSecretKey = keyStoreService.loadKeyFromKeyStore();

        String encryptedEmail = EncryptionService.encrypt(adRequest.getEmail(), loadedSecretKey);

        adRequest.setEmail(encryptedEmail);

        adRequestRepository.save(adRequest);
    }

    public void encryptAllUsers () throws Exception {
        List<User> users = userRepository.findAll();

        int i = 0;

        for (User user : users) {
            encryptUserData(user);
            i++;
        }

        System.out.println("----------------- USPESNO ENKRIPTOVANJE SVIH KORISNIKA ------------");
        System.out.println("ima ih: " + i);
    }

    public String decryptData(String encryptedData) throws Exception {
        return EncryptionService.decrypt(encryptedData, keyStoreService.loadKeyFromKeyStore());
    }

    public String encryptData(String plainData) throws Exception {
        return EncryptionService.encrypt(plainData, keyStoreService.loadKeyFromKeyStore());
    }

    public void encryptUserData(User user) throws Exception {
        SecretKey loadedSecretKey = keyStoreService.loadKeyFromKeyStore();

        String encryptedEmail = EncryptionService.encrypt(user.getEmail(), loadedSecretKey);
        String encryptedName = EncryptionService.encrypt(user.getName(), loadedSecretKey);
        String encryptedSurname = EncryptionService.encrypt(user.getSurname(), loadedSecretKey);
        String encryptedAddress = EncryptionService.encrypt(user.getAddress(), loadedSecretKey);
        String encryptedCountry = EncryptionService.encrypt(user.getCountry(), loadedSecretKey);
        String encryptedCity = EncryptionService.encrypt(user.getCity(), loadedSecretKey);
        String encryptedPhoneNumber = EncryptionService.encrypt(user.getPhoneNumber(), loadedSecretKey);

        user.setEmail(encryptedEmail);
        user.setName(encryptedName);
        user.setSurname(encryptedSurname);
        user.setCity(encryptedCity);
        user.setCountry(encryptedCountry);
        user.setAddress(encryptedAddress);
        user.setPhoneNumber(encryptedPhoneNumber);

        userRepository.save(user);
    }

    public void encryptUpdateUserData(User user) throws Exception {
        SecretKey loadedSecretKey = keyStoreService.loadKeyFromKeyStore();

        //String encryptedEmail = EncryptionService.encrypt(user.getEmail(), loadedSecretKey);
        String encryptedName = EncryptionService.encrypt(user.getName(), loadedSecretKey);
        String encryptedSurname = EncryptionService.encrypt(user.getSurname(), loadedSecretKey);
        String encryptedAddress = EncryptionService.encrypt(user.getAddress(), loadedSecretKey);
        String encryptedCountry = EncryptionService.encrypt(user.getCountry(), loadedSecretKey);
        String encryptedCity = EncryptionService.encrypt(user.getCity(), loadedSecretKey);
        String encryptedPhoneNumber = EncryptionService.encrypt(user.getPhoneNumber(), loadedSecretKey);

        //user.setEmail(encryptedEmail);
        user.setName(encryptedName);
        user.setSurname(encryptedSurname);
        user.setCity(encryptedCity);
        user.setCountry(encryptedCountry);
        user.setAddress(encryptedAddress);
        user.setPhoneNumber(encryptedPhoneNumber);

        userRepository.save(user);
    }

    //ovo je samo test funkcionisanja
    public void encryptUserData() throws Exception {
        //User user = userRepository.findByEmail("anaa.radovanovic2001+5@gmail.com");

        SecretKey loadedSecretKey1 = keyStoreService.loadKeyFromKeyStore();
        System.out.println("LOADED SECRET KEY:::" + loadedSecretKey1);

        String email1 = "anaa.radovanovic2001+5@gmail.com";
        String encryptedEmail21 = EncryptionService.encrypt(email1, loadedSecretKey1);

        User user = userRepository.findByEmail(encryptedEmail21);

        System.out.println("----- KORISNIK PRE ENKRICPIJE -----");
        System.out.println(user.getEmail());
        System.out.println(user.getName());
        System.out.println(user.getSurname());
        System.out.println(user.getAddress());
        System.out.println(user.getCountry());
        System.out.println(user.getCity());
        System.out.println(user.getPhoneNumber());

        SecretKey secretKey = EncryptionService.generateSecretKey();
        keyStoreService.saveKeyToKeyStore(secretKey);

        String encryptedEmail = EncryptionService.encrypt(user.getEmail(), secretKey);
        String encryptedName = EncryptionService.encrypt(user.getName(), secretKey);
        String encryptedSurname = EncryptionService.encrypt(user.getSurname(), secretKey);
        String encryptedAddress = EncryptionService.encrypt(user.getAddress(), secretKey);
        String encryptedCountry = EncryptionService.encrypt(user.getCountry(), secretKey);
        String encryptedCity = EncryptionService.encrypt(user.getCity(), secretKey);
        String encryptedPhoneNumber = EncryptionService.encrypt(user.getPhoneNumber(), secretKey);

        user.setEmail(encryptedEmail);
        user.setName(encryptedName);
        user.setSurname(encryptedSurname);
        user.setCity(encryptedCity);
        user.setCountry(encryptedCountry);
        user.setAddress(encryptedAddress);
        user.setPhoneNumber(encryptedPhoneNumber);

        userRepository.save(user);

        SecretKey loadedSecretKey = keyStoreService.loadKeyFromKeyStore();
        System.out.println("LOADED SECRET KEY:::" + loadedSecretKey);

        String email = "anaa.radovanovic2001+5@gmail.com";
        String encryptedEmail2 = EncryptionService.encrypt(email, loadedSecretKey);

        User encryptedUser = userRepository.findByEmail(encryptedEmail2);

        System.out.println("----- KORISNIK NAKON ENKRICPIJE -----");
        System.out.println(encryptedUser.getEmail());
        System.out.println(encryptedUser.getName());
        System.out.println(encryptedUser.getSurname());
        System.out.println(encryptedUser.getAddress());
        System.out.println(encryptedUser.getCountry());
        System.out.println(encryptedUser.getCity());
        System.out.println(encryptedUser.getPhoneNumber());

        String decryptedEmail = EncryptionService.decrypt(encryptedUser.getEmail(), loadedSecretKey);
        String decryptedName = EncryptionService.decrypt(encryptedUser.getName(), loadedSecretKey);
        String decryptedSurname = EncryptionService.decrypt(encryptedUser.getSurname(), loadedSecretKey);
        String decryptedCity = EncryptionService.decrypt(encryptedUser.getCity(), loadedSecretKey);
        String decryptedCountry = EncryptionService.decrypt(encryptedUser.getCountry(), loadedSecretKey);
        String decryptedAddress = EncryptionService.decrypt(encryptedUser.getAddress(), loadedSecretKey);
        String decryptedPhoneNumber = EncryptionService.decrypt(encryptedUser.getPhoneNumber(), loadedSecretKey);

        System.out.println("----- KORISNIK NAKON DEKRIPCIJE -----");
        System.out.println(decryptedEmail);
        System.out.println(decryptedName);
        System.out.println(decryptedSurname);
        System.out.println(decryptedCity);
        System.out.println(decryptedCountry);
        System.out.println(decryptedAddress);
        System.out.println(decryptedPhoneNumber);
    }
}
