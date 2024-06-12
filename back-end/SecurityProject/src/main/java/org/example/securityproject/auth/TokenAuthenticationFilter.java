package org.example.securityproject.auth;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.example.securityproject.model.User;
import org.example.securityproject.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import org.example.securityproject.util.TokenUtils;

// Filter koji ce presretati SVAKI zahtev klijenta ka serveru
// (sem nad putanjama navedenim u WebSecurityConfig.configure(WebSecurity web))
// Filter proverava da li JWT token postoji u Authorization header-u u zahtevu koji stize od klijenta
// Ukoliko token postoji, proverava se da li je validan. Ukoliko je sve u redu, postavlja se autentifikacija
// u SecurityContext holder kako bi podaci o korisniku bili dostupni u ostalim delovima aplikacije gde su neophodni
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private TokenUtils tokenUtils;

    private UserDetailsService userDetailsService;
    private UserRepository userRepository;

    protected final Log LOGGER = LogFactory.getLog(getClass());

    public TokenAuthenticationFilter(TokenUtils tokenHelper, UserDetailsService userDetailsService, UserRepository userRepository) {
        this.tokenUtils = tokenHelper;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {


        String username;

        // 1. Preuzimanje JWT tokena iz zahteva
        String authToken = tokenUtils.getToken(request);
        System.out.println("NE BI TREBAO UCI OVDE");
        if(authToken.isEmpty())
        {
            System.out.println("Token je prazan!!!!!!!!!");
        }
        try {

            if (authToken != null) {

                // 2. Citanje korisnickog imena iz tokena
                username = tokenUtils.getUsernameFromToken(authToken);
                User user = userRepository.findByEmail(username);
                if (user != null && user.isBlocked()) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("User is blocked.");
                    return;
                }

                if (username != null) {

                    // 3. Preuzimanje korisnika na osnovu username-a
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // 4. Provera da li je prosledjeni token validan
                    if (tokenUtils.validateToken(authToken, userDetails)) {

                        // 5. Kreiraj autentifikaciju
                        TokenBasedAuthentication authentication = new TokenBasedAuthentication(userDetails);
                        authentication.setToken(authToken);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }

        } catch (ExpiredJwtException ex) {
            LOGGER.debug("Token expired!");
        }

        // prosledi request dalje u sledeci filter
        chain.doFilter(request, response);
    }
}