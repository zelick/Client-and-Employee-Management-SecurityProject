package org.example.securityproject.config;

import org.example.securityproject.auth.CustomAuthenticationProvider;
import org.example.securityproject.repository.UserRepository;
import org.example.securityproject.service.UserDataEncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import org.example.securityproject.auth.RestAuthenticationEntryPoint;
import org.example.securityproject.auth.TokenAuthenticationFilter;
import org.example.securityproject.services.CustomUserDetailsService;
import org.example.securityproject.util.TokenUtils;

import static org.example.securityproject.enums.Permission.ADMIN_READ;
import static org.example.securityproject.enums.UserRole.*;

@Configuration
// Injektovanje bean-a za bezbednost
@EnableWebSecurity
// Ukljucivanje podrske za anotacije "@Pre*" i "@Post*" koje ce aktivirati autorizacione provere za svaki pristup metodi
@EnableGlobalMethodSecurity(prePostEnabled = false, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {

    @Value("${security.project.secret}")
    private String SECRET_KEY;

    @Autowired
    private UserRepository userRepository; // mora da bi uzeo salt iz usera
    @Autowired
    private UserDataEncryptionService userDataEncryptionService;

    // Servis koji se koristi za citanje podataka o korisnicima aplikacije
    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    // Implementacija PasswordEncoder-a koriscenjem BCrypt hashing funkcije.
    // BCrypt po defalt-u radi 10 rundi hesiranja prosledjene vrednosti.
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        // 1. koji servis da koristi da izvuce podatke o korisniku koji zeli da se autentifikuje
//        // prilikom autentifikacije, AuthenticationManager ce sam pozivati loadUserByUsername() metodu ovog servisa
//        authProvider.setUserDetailsService(userDetailsService());
//        // 2. kroz koji enkoder da provuce lozinku koju je dobio od klijenta u zahtevu
//        // da bi adekvatan hash koji dobije kao rezultat hash algoritma uporedio sa
//        // onim koji se nalazi u bazi (posto se u bazi ne cuva plain lozinka)
//        authProvider.setPasswordEncoder(passwordEncoder());
//
//        return authProvider;
//    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new CustomAuthenticationProvider(userDetailsService(), userRepository, userDataEncryptionService);
    }



    // Handler za vracanje 401 kada klijent sa neodogovarajucim korisnickim imenom i lozinkom pokusa da pristupi resursu
    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    // Registrujemo authentication manager koji ce da uradi autentifikaciju korisnika za nas
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }


    // Injektujemo implementaciju iz TokenUtils klase kako bismo mogli da koristimo njene metode za rad sa JWT u TokenAuthenticationFilteru
    @Autowired
    private TokenUtils tokenUtils;

    // Definisemo prava pristupa za zahteve ka odredjenim URL-ovima/rutama
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // svim korisnicima dopusti da pristupe sledecim putanjama:
        // komunikacija izmedju klijenta i servera je stateless posto je u pitanju REST aplikacija
        // ovo znaci da server ne pamti nikakvo stanje, tokeni se ne cuvaju na serveru
        // ovo nije slucaj kao sa sesijama koje se cuvaju na serverskoj strani - STATEFULL aplikacija
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // sve neautentifikovane zahteve obradi uniformno i posalji 401 gresku
        http.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint);


        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/auth/login").permitAll()		// /auth/**
                .antMatchers("/api/h2-console/**").permitAll()	// /h2-console/** ako se koristi H2 baza)
                //.antMatchers("/api/foo").permitAll()		// /api/foo
                // ukoliko ne zelimo da koristimo @PreAuthorize anotacije nad metodama kontrolera, moze se iskoristiti hasRole() metoda da se ogranici
                // koji tip korisnika moze da pristupi odgovarajucoj ruti. Npr. ukoliko zelimo da definisemo da ruti 'admin' moze da pristupi
                // samo korisnik koji ima rolu 'ADMIN', navodimo na sledeci nacin:
                // .antMatchers("/admin").hasRole("ADMIN") ili .antMatchers("/admin").hasAuthority("ROLE_ADMIN")

                // EMPLOYEE AUTHORIZATION
               // .antMatchers("/api/ads/all").hasAuthority("EMPLOYEE")

                //.antMatchers(HttpMethod.GET,"/api/ads/all").hasAuthority("ADMIN_SEEPROFILE")

               // .antMatchers("/api/ads/create").hasAuthority("EMPLOYEE")
                //.antMatchers("/api/ad-requests/all").hasAuthority("EMPLOYEE")
               // .antMatchers("/api/ad-requests/id").hasAuthority("EMPLOYEE")

                // CLIENT AUTHORIZATION
               // .antMatchers("/api/ad-requests/create").hasAuthority("CLIENT")
              //  .antMatchers("/api/ads/by-email").hasAuthority("CLIENT")

                // EMPLOYEE NOVO
                .antMatchers(HttpMethod.GET,"/api/ads/all").hasAuthority("EMPLOYEE_READ")
                .antMatchers(HttpMethod.POST,"/api/ads/create").hasAuthority("EMPLOYEE_CREATE")
                .antMatchers(HttpMethod.GET,"/api/ad-requests/all").hasAuthority("EMPLOYEE_READ")
                .antMatchers(HttpMethod.GET,"/api/ad-requests/**").hasAuthority("EMPLOYEE_READ")

                // CLIENT NOVO
                .antMatchers(HttpMethod.POST,"/api/ad-requests/create").hasAuthority("CLIENT_CREATE")
                .antMatchers(HttpMethod.GET,"/api/ads/by-email/**").hasAuthority("CLIENT_READ")
                //PO ROLI ZA PUTANJU USERS:

                //.antMatchers("/api/users/**").hasAnyRole(CLIENT.name(), EMPLOYEE.name(), ADMINISTRATOR.name())

                // ADMINISTRATOR AUTHORIZATION
                //.antMatchers(HttpMethod.GET, "/api/users/getAllEmployees").hasAuthority("ADMINISTRATOR")

                //ADMINISTRATOR
                //.antMatchers("/api/admins/**").hasRole("ADMINISTRATOR")
                .antMatchers(HttpMethod.GET, "/api/admins/getAllEmployees").hasAuthority("ADMIN_READ")
                .antMatchers(HttpMethod.GET,"/api/admins/getAdminData").hasAuthority("ADMIN_READ")
                .antMatchers(HttpMethod.GET,"/api/admins/getAllClients").hasAuthority("ADMIN_READ")
                .antMatchers(HttpMethod.GET,"/api/admins/getAllRegistrationRequests").hasAuthority("ADMIN_READ")
                .antMatchers(HttpMethod.PUT,"/api/admins/processRegistrationRequest").hasAuthority("ADMIN_UPDATE")
                .antMatchers(HttpMethod.PUT,"/api/admins/updateAdminData").hasAuthority("ADMIN_UPDATE")
                .antMatchers(HttpMethod.GET,"/api/admins/getAllRoles").hasAuthority("ADMIN_READ")
                .antMatchers(HttpMethod.GET, "/api/admins/getAllPermissionsForRole/**").hasAuthority("ADMIN_READ")
                .antMatchers(HttpMethod.PUT,"/api/admins/removePermission").hasAuthority("ADMIN_UPDATE")
                .antMatchers(HttpMethod.GET, "/api/admins/getAllCanBeAddedPermissions/**").hasAuthority("ADMIN_READ")
                .antMatchers(HttpMethod.PUT,"/api/admins/addPermission").hasAuthority("ADMIN_UPDATE")
                .antMatchers(HttpMethod.PUT,"/api/admins/updateAdminPassword").hasAuthority("ADMIN_UPDATE")
                .antMatchers(HttpMethod.PUT,"/api/admins/blockUser").hasAuthority("ADMIN_UPDATE")
                .antMatchers(HttpMethod.PUT,"/api/admins/unblockUser").hasAuthority("ADMIN_UPDATE")

                //.antMatchers(HttpMethod.PUT,"/api/users/updatePassword").hasAnyAuthority("ADMIN_UPDATE", "EMPLOYEE_UPDATE")

                // za svaki drugi zahtev korisnik mora biti autentifikovan
                .anyRequest().authenticated().and()
                // za development svrhe ukljuci konfiguraciju za CORS iz WebConfig klase
                .cors().and()

                // umetni custom filter TokenAuthenticationFilter kako bi se vrsila provera JWT tokena umesto cistih korisnickog imena i lozinke (koje radi BasicAuthenticationFilter)
                .addFilterBefore(new TokenAuthenticationFilter(tokenUtils,  userDetailsService(), userRepository), BasicAuthenticationFilter.class);

        // zbog jednostavnosti primera ne koristimo Anti-CSRF token (https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html)
        http.csrf().disable();


        // ulancavanje autentifikacije
        http.authenticationProvider(authenticationProvider());

        return http.build();
    }

    // metoda u kojoj se definisu putanje za igorisanje autentifikacije
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // Autentifikacija ce biti ignorisana ispod navedenih putanja (kako bismo ubrzali pristup resursima)
        // Zahtevi koji se mecuju za web.ignoring().antMatchers() nemaju pristup SecurityContext-u
        // Dozvoljena POST metoda na ruti /auth/login, za svaki drugi tip HTTP metode greska je 401 Unauthorized
        return (web) -> web.ignoring()
                .antMatchers("/actuator/**")// Dodajte putanju /actuator/**
//                .antMatchers( "/loki/**") // Dodajte putanje za Loki
//                .antMatchers( "/loki/api/v1/push") // Dodajte putanju za Loki
                //.antMatchers(HttpMethod.GET, "/loki/**") // Dodajte putanje za Loki (ovde je promenjen HttpMethod u GET)
                //.antMatchers(HttpMethod.GET, "/loki/api/v1/push") // Dodajte putanju za Loki
                .antMatchers("/loki/**") // Dodajte putanje za Loki (uklonjen HttpMethod)
                .antMatchers(HttpMethod.POST, "/loki/api/v1/push") // Dodajte putanju za Loki
                .antMatchers(HttpMethod.GET, "/favicon.ico") // Dodajte putanju za favicon.ico (ovde je promenjen HttpMethod u GET)
                .antMatchers(HttpMethod.POST, "/api/auth/login")
                .antMatchers(HttpMethod.POST, "/api/users/registerUser")
                .antMatchers(HttpMethod.GET, "/api/users/confirm-account")
                .antMatchers(HttpMethod.POST, "/api/users/tryLogin")
                .antMatchers(HttpMethod.POST, "/api/users/resetPassword")
                .antMatchers(HttpMethod.PUT, "/api/users/updatePassword")
                .antMatchers(HttpMethod.POST, "/api/users/editUserRole") //TEST VELIKI
                .antMatchers(HttpMethod.POST, "/api/users/editUserPermission") //TEST VELIKI
                .antMatchers(HttpMethod.POST, "/api/login/send-email")
                .antMatchers(HttpMethod.POST, "/api/login/reset-password")
                .antMatchers(HttpMethod.GET, "/api/login/verify")
                .antMatchers(HttpMethod.GET, "/api/login/tokens/**")
                .antMatchers(HttpMethod.GET, "/api/auth/refresh-token")
                .antMatchers(HttpMethod.POST,"/api/ads/visit-ad")
                .antMatchers(HttpMethod.POST, "/api/users/verify/**")
                .antMatchers(HttpMethod.POST, "/api/users/verifyReCaptchaToken/**")
               // .antMatchers(HttpMethod.GET, "/api/admins/getAllNotifications")
                .antMatchers(HttpMethod.GET, "/", "/webjars/**", "/*.html", "favicon.ico",
                        "/**/*.html", "/**/*.css", "/**/*.js");
    }
}