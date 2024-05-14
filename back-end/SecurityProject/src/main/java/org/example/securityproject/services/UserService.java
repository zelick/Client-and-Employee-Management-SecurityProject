package org.example.securityproject.services;

import org.example.securityproject.dto.UserRequest;
import org.example.securityproject.enums.UserRole;
import org.example.securityproject.model.User;
import org.example.securityproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @Autowired
//    private RoleService roleService;

    public User findByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username);
    }

    public User findById(int id)  {
       return userRepository.findById(id).orElseGet(null);
    }

    public List<User> findAll() throws AccessDeniedException {
        return userRepository.findAll();
    }


    public User save(UserRequest userRequest) {
//        User u = new User();
//        u.setEmail(userRequest.getUsername());
//
//        // pre nego sto postavimo lozinku u atribut hesiramo je kako bi se u bazi nalazila hesirana lozinka
//        // treba voditi racuna da se koristi isi password encoder bean koji je postavljen u AUthenticationManager-u kako bi koristili isti algoritam
//        //u.setPassword(passwordEncoder.encode(userRequest.getPassword()));
//
//        u.setName(userRequest.getFirstname());
//        u.setSurname(userRequest.getLastname());
//        u.setEnabled(true);
//        u.setEmail(userRequest.getEmail());
//
//        // u primeru se registruju samo obicni korisnici i u skladu sa tim im se i dodeljuje samo rola USER
//       // UserRole role = roleService.findByName("ROLE_USER");
//       // u.setRole(UserRole.ADMINISTRATOR);
//
//        return this.userRepository.save(u);
        return null;
    }
}
