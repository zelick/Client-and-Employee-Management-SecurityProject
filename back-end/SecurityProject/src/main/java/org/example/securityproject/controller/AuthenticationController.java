package org.example.securityproject.controller;


import javax.servlet.http.HttpServletResponse;
//import org.apache.coyote.BadRequestException;
import org.example.securityproject.dto.JwtAuthenticationRequest;
import org.example.securityproject.dto.UserRequest;
import org.example.securityproject.dto.UserTokenState;
import org.example.securityproject.model.User;
import org.example.securityproject.service.UserService;
import org.example.securityproject.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
//import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@CrossOrigin(origins = "*", maxAge = 3600)
//Kontroler zaduzen za autentifikaciju korisnika
@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    // Prvi endpoint koji pogadja korisnik kada se loguje.
    // Tada zna samo svoje korisnicko ime i lozinku i to prosledjuje na backend.
    //@HasRoleClient
    @PostMapping("/login")
   // @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<UserTokenState> createAuthenticationToken(
            @RequestBody JwtAuthenticationRequest authenticationRequest, HttpServletResponse response) {
        // Ukoliko kredencijali nisu ispravni, logovanje nece biti uspesno, desice se
        // AuthenticationException
       // System.out.println("USAOOOO" + authenticationRequest.getUsername() + " " + authenticationRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getUsername(), authenticationRequest.getPassword()));

        // Ukoliko je autentifikacija uspesna, ubaci korisnika u trenutni security
        // kontekst
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Kreiraj token za tog korisnika
        User user = (User) authentication.getPrincipal();  // OVO NE RADI
        String jwt = tokenUtils.generateToken(user.getUsername());
        //String jwt = tokenUtils.generateToken(authenticationRequest.getUsername());
        //String jwt = tokenUtils.generateToken("kristina.zelic@gmail.com");
        int expiresIn = tokenUtils.getExpiredIn();
        // Vrati token kao odgovor na uspesnu autentifikaciju
        return ResponseEntity.ok(new UserTokenState(jwt, expiresIn));
    }

    //OVO JE SAMO PROBA
    @GetMapping("/getUserByEmail/{email}")
    public ResponseEntity<UserRequest> getUser(@PathVariable String email) {
        User user = userService.findByUsername(email);
        UserRequest userRequestFound = new UserRequest();
        userRequestFound.setId(user.getId());
        userRequestFound.setEmail(user.getEmail());
        userRequestFound.setFirstname(user.getName());
        userRequestFound.setLastname(user.getSurname());
        userRequestFound.setPassword(user.getPassword());
        return new ResponseEntity<>(userRequestFound, HttpStatus.OK);
    }


}
