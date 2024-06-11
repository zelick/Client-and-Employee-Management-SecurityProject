package org.example.securityproject.controller;


import javax.servlet.http.HttpServletResponse;
//import org.apache.coyote.BadRequestException;
import javax.servlet.http.HttpServletRequest;
import org.example.securityproject.dto.AccessRefreshTokenResponseDto;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;


//@CrossOrigin(origins = "*", maxAge = 3600)
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

    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);


    @PostMapping("/login")
    public ResponseEntity<AccessRefreshTokenResponseDto> createAuthenticationToken(
            @RequestBody JwtAuthenticationRequest authenticationRequest, HttpServletResponse response) {

        try {
            // Ukoliko kredencijali nisu ispravni, logovanje nece biti uspesno, desice se
            // AuthenticationException
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(), authenticationRequest.getPassword()));

            // Ukoliko je autentifikacija uspesna, ubaci korisnika u trenutni security
            // kontekst
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Kreiraj token za tog korisnika
            User user = (User) authentication.getPrincipal();
            String accessToken = tokenUtils.generateAccessToken(user.getUsername());
            String refreshToken = tokenUtils.generateRefreshToken(user.getUsername());
            int accessExpiresIn = tokenUtils.getAccessExpiresIn();
            int refreshExpiresIn = tokenUtils.getRefreshExpiresIn();

            // Vrati token kao odgovor na uspesnu autentifikaciju
            return ResponseEntity.ok(new AccessRefreshTokenResponseDto(accessToken, accessExpiresIn, refreshToken, refreshExpiresIn));
        } catch (AuthenticationException e) {
            // Uhvatite AuthenticationException, koji može biti izuzetak ako autentifikacija nije uspela
            logger.error("Došlo je do greške prilikom autentifikacije korisnika {}: {}", authenticationRequest.getUsername(), e.getMessage());
            // Vratite odgovarajući HTTP status kao odgovor na grešku autentifikacije
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            // Uhvatite ostale izuzetke koje niste predvideli
            logger.error("Došlo je do nepredviđene greške prilikom autentifikacije korisnika {}: {}", authenticationRequest.getUsername(), e.getMessage());
            // Vratite odgovarajući HTTP status kao odgovor na nepredviđenu grešku
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoint za osvežavanje access tokena
    @GetMapping("/refresh-token")
    public ResponseEntity<UserTokenState> refreshToken(HttpServletRequest request) {
         try{
             //String refreshToken = request.getHeader("Authorization"); // Uzmi refresh token iz headera
            String refreshToken = tokenUtils.getToken(request);
            String username = tokenUtils.getUsernameFromToken(refreshToken);
            User user = userService.findByUsername(username);

            if (tokenUtils.isTokenExpired(refreshToken)) { // Provjeri je li refresh token istekao
                // Ako je refresh token istekao, vraćamo grešku
                return ResponseEntity.badRequest().build();
            } else {
                // Ako refresh token nije istekao, osvježavamo access token
                String newAccessToken = tokenUtils.refreshAccessToken(refreshToken);
                int expiresIn = tokenUtils.getExpiredIn();
                return ResponseEntity.ok(new UserTokenState(newAccessToken, expiresIn));
            }
         } catch (Exception e) {
             logger.error("Došlo je do greške prilikom osvežavanja tokena: {}", e.getMessage());
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
         }
    }

//    // Endpoint za proveru validnosti access tokena
//    @GetMapping("/check-token")
//    public ResponseEntity<Void> checkAccessToken(HttpServletRequest request) {
//        //String accessToken = request.getHeader("Authorization"); // Uzmi access token iz headera
//        String accessToken = tokenUtils.getToken(request);
//        if (accessToken == null || accessToken.isEmpty()) {
//            // Ako access token nije pronađen u zaglavlju zahteva, vraćamo grešku
//            return ResponseEntity.badRequest().build();
//        }
//
//        if (tokenUtils.isTokenExpired(accessToken)) {
//            // Ako je access token istekao, vraćamo grešku
//            return ResponseEntity.badRequest().build();
//        }
//
//        // Ako je access token važeći, vraćamo status 200 OK
//        return ResponseEntity.ok().build();
//    }

    // Endpoint za proveru validnosti access tokena
    @GetMapping("/check-token")
    public ResponseEntity<Void> checkAccessToken(HttpServletRequest request) {
        try {
            String accessToken = tokenUtils.getToken(request);

            // Provera da li je access token pronađen
            if (accessToken == null || accessToken.isEmpty()) {
                logger.info("Access token not found in the request headers.");
                return ResponseEntity.badRequest().build();
            }

            // Provera da li je access token istekao
            if (tokenUtils.isTokenExpired(accessToken)) {
                logger.info("Access token has expired.");
                return ResponseEntity.badRequest().build();
            }

            // Ako je access token važeći, vraćamo status 200 OK
            logger.info("Access token is valid.");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // Uhvati sve ostale izuzetke koje niste predvideli
            logger.error("An unexpected error occurred while checking the access token validity: {}", e.getMessage());
            // Vratite odgovarajući HTTP status kao odgovor na nepredviđenu grešku
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
