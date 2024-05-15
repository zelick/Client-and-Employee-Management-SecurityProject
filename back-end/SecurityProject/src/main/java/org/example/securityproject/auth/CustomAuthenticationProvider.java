package org.example.securityproject.auth;

import org.example.securityproject.model.User;
import org.example.securityproject.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    public CustomAuthenticationProvider(UserDetailsService userDetailsService, UserRepository userRepository) {
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        String storedPasswordHash = userDetails.getPassword();
        User foundUser= userRepository.findByEmail(userDetails.getUsername()); // username isti kao email
        String storedSalt = foundUser.getSalt();
        String hashedPassword = null;
        try {
            hashedPassword = hashPassword(password, storedSalt);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        if (hashedPassword.equals(storedPasswordHash)) {
            //return new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities()); //mozda izmena ?
            return new UsernamePasswordAuthenticationToken(foundUser, password, userDetails.getAuthorities());
        } else {
            throw new BadCredentialsException("Incorrect password");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private String hashPassword(String password, String salt) throws NoSuchAlgorithmException {
        String input = password + salt;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encodedhash);
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
