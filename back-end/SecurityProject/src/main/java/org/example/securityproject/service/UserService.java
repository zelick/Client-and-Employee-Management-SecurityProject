package org.example.securityproject.service;

import lombok.AllArgsConstructor;
import org.example.securityproject.dto.*;
import org.example.securityproject.enums.RegistrationStatus;
import org.example.securityproject.enums.UserRole;
import org.example.securityproject.enums.ServicesPackage;
import org.example.securityproject.model.ConfirmationToken;
import org.example.securityproject.model.User;
import org.example.securityproject.repository.ConfirmationTokenRepository;
import org.example.securityproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class UserService {
    @Autowired
    private UserRepository userRepository;
    private EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;
    private ConfirmationTokenRepository confirmationTokenRepository;
    private final TwoFactorAuthenticationService tfaService;
    private ReCaptchaService reCaptchaService;

    public LoginReponseDto loginUser(UserLoginData loginData) {
        LoginReponseDto loginResponseDto = new LoginReponseDto();
        User user = userRepository.findByEmail(loginData.getEmail());

        if (user.getRoles().contains(UserRole.EMPLOYEE)) {
            loginResponseDto.setEmployeed(true);
        }
        else {
            loginResponseDto.setEmployeed(false);
        }

        System.out.println("EMPLOYEE: " + loginResponseDto.isEmployeed());

        //PROBLEM ZA EMPLOYEE-A AKO ISPRAVNO RESI RECAHPCA ONDA TEK DA MU SE SETUJE LOGGED IN ONCE NA TRUE!!!

        if (!(user.isActive() && user.isEnabled())) {
            loginResponseDto.setMfaEnabled(false);
            loginResponseDto.setLoggedInOnce(false);
            loginResponseDto.setResponse("This account is not active, please wait for admin to activate your account.");
            return loginResponseDto;
        }

        /*
        if (!(user.getRole().equals(UserRole.CLIENT)) && !user.isLoggedInOnce()) {
            loginReponseDto.setLoggedInOnce(false);
            loginReponseDto.setResponse("This user must change his password because this is his first login.");
            return loginReponseDto;
        }
         */

        boolean hasAdministratorOrEmployeeRole = user.getRoles().stream()
                .anyMatch(role -> role.equals(UserRole.ADMINISTRATOR) || role.equals(UserRole.EMPLOYEE));

        if (hasAdministratorOrEmployeeRole && !user.isLoggedInOnce()) {
            loginResponseDto.setMfaEnabled(false);
            loginResponseDto.setLoggedInOnce(false);
            loginResponseDto.setResponse("This user must change his password because this is his first login.");
            return loginResponseDto;
        }

        if (user.isMfaEnabled() && !user.isVerifiedMfaCode()) {
            loginResponseDto.setMfaEnabled(false);
            loginResponseDto.setLoggedInOnce(false);
            loginResponseDto.setResponse("The user did not enter the two-factor authentication code correctly, and his account is not active.");
            return loginResponseDto;
        }

        if (loginResponseDto.isEmployeed()) {
            if (user.isLoggedInOnce()) {
                loginResponseDto.setLoggedInOnce(true);
            }
            else {
                loginResponseDto.setLoggedInOnce(false);
            }            
            
            loginResponseDto.setResponse("You are employed, you have to solve this problem and show that you are not a robot.");

            if (user.isMfaEnabled() && user.isVerifiedMfaCode()) {
                loginResponseDto.setMfaEnabled(true);
                //loginonce -- tek ako potvrdi pravilno 2fa
                loginResponseDto.setResponse("You are employed, you must solve this problem and show that you are not a robot, after that enter a 6-digit number because you have confirmed two-factor authentication.");
            }

            return loginResponseDto;
        }

        if (user.isMfaEnabled() && user.isVerifiedMfaCode()) {
            loginResponseDto.setMfaEnabled(true);
            //loginonce -- tek ako potvrdi pravilno 2fa
            if (user.isLoggedInOnce()) {
                loginResponseDto.setLoggedInOnce(true);
            }
            else {
                loginResponseDto.setLoggedInOnce(false);
            }
            loginResponseDto.setResponse("Enter the 6-digit authentication code.");
            return loginResponseDto;
        }

        loginResponseDto.setMfaEnabled(false);
        loginResponseDto.setLoggedInOnce(true);
        loginResponseDto.setResponse("This user has successfully logged in.");
        return loginResponseDto;
    }
    public RegistrationResponseDto registerUser (UserDto userDto) {
        RegistrationResponseDto response = new RegistrationResponseDto();
        if (!validatePassword(userDto.getPassword())) {
            response.setResponseMessage("The password does not meet the requirements.");
            response.setFlag(false);
            response.setSecretImageUri("");
            return response;
        }

        User existingUser = userRepository.findByEmailAndRegistrationStatusIn(userDto.getEmail(), Arrays.asList(RegistrationStatus.PENDING, RegistrationStatus.ACCEPTED));
        if (existingUser != null) {
            response.setResponseMessage("A user with this email is already registered.");
            response.setFlag(false);
            response.setSecretImageUri("");
            return response;
        }

        if (!isValidEmail(userDto.getEmail())) {
            response.setResponseMessage("Invalid email format.");
            response.setFlag(false);
            response.setSecretImageUri("");
            return response;
        }

        if (!areAllFieldsFilled(userDto)) {
            response.setResponseMessage("All fields are required.");
            response.setFlag(false);
            response.setSecretImageUri("");
            return response;
        }

        User existingRejectedUser = userRepository.findByEmailAndRegistrationStatus(userDto.getEmail(), RegistrationStatus.REJECTED);

        if (existingRejectedUser != null && existingRejectedUser.getRequestProcessingDate() != null) {
            Date requestProcessedDate = existingRejectedUser.getRequestProcessingDate();
            LocalDateTime requestProcessedTime = LocalDateTime.ofInstant(requestProcessedDate.toInstant(), ZoneId.systemDefault());
            LocalDateTime currentTime = LocalDateTime.now();
            long minutesPassed = ChronoUnit.MINUTES.between(requestProcessedTime, currentTime);

            if (minutesPassed < 5) {
                response.setResponseMessage("It is not possible to register - your request was recently rejected.");
                response.setFlag(false);
                response.setSecretImageUri("");
                return response;
            }
            else {
               userRepository.delete(existingRejectedUser);
            }
        }

        User user = new User();

        user.setEmail(userDto.getEmail());
        user.setAddress(userDto.getAddress());
        user.setCity(userDto.getCity());
        user.setCountry(userDto.getCountry());
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setRegistrationStatus(RegistrationStatus.PENDING);
        user.setClientType(userDto.getClientType());

        //roles.add(UserRole.ADMINISTRATOR);
        //List<UserRole> rolesAdmin = new ArrayList();
        //rolesAdmin.add(UserRole.ADMINISTRATOR);
        user.setRoles(userDto.getRoles());

        user.setServicesPackage(userDto.getServicesPackage());
        user.setRequestProcessingDate(null);
        user.setLoggedInOnce(false);
        user.setEnabled(false); //proveri!

        String salt = BCrypt.gensalt();
        //String hashedPassword = passwordEncoder.encode(userDto.getPassword() + salt);

        String hashedPassword = "";
        try {
            hashedPassword = hashPassword(userDto.getPassword(), salt);
            user.setPassword(hashedPassword);
            user.setSalt(salt);
        } catch (NoSuchAlgorithmException e) {
            // Handle exception
        }

        //DODATO ZA 2FA
        user.setMfaEnabled(userDto.isMfaEnabled());

        if (userDto.isMfaEnabled()) {
            user.setSecret(tfaService.generateNewSecret());
        }

        user.setVerifiedMfaCode(false);
        //

        user.setPassword(hashedPassword);
        user.setSalt(salt);

        userRepository.save(user);
        response.setResponseMessage("You have successfully registered.");
        response.setFlag(true);
        response.setSecretImageUri(tfaService.generateQrCodeImageUri(user.getSecret()));
        return response;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean areAllFieldsFilled(UserDto userDto) {
        return userDto.getEmail() != null && !userDto.getEmail().isEmpty() &&
                userDto.getAddress() != null && !userDto.getAddress().isEmpty() &&
                userDto.getCity() != null && !userDto.getCity().isEmpty() &&
                userDto.getCountry() != null && !userDto.getCountry().isEmpty() &&
                userDto.getName() != null && !userDto.getName().isEmpty() &&
                userDto.getSurname() != null && !userDto.getSurname().isEmpty() &&
                userDto.getPhoneNumber() != null && !userDto.getPhoneNumber().isEmpty() &&
                userDto.getClientType() != null &&
                userDto.getRoles() != null &&
                userDto.getServicesPackage() != null;
    }

    private boolean areAllFieldsFilledAdmin(EditAdminDto adminData) {
        return adminData.getAddress() != null && !adminData.getAddress().isEmpty() &&
                adminData.getCity() != null && !adminData.getCity().isEmpty() &&
                adminData.getCountry() != null && !adminData.getCountry().isEmpty() &&
                adminData.getName() != null && !adminData.getName().isEmpty() &&
                adminData.getSurname() != null && !adminData.getSurname().isEmpty() &&
                adminData.getPhoneNumber() != null && !adminData.getPhoneNumber().isEmpty();
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

    private boolean validatePassword(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&*!]).{12,}$";
        return password.matches(passwordRegex);
    }

    public List<User> getAllRegistrationRequests() {
        return userRepository.findByRegistrationStatus(RegistrationStatus.PENDING);
    }

    public void processRegistrationRequest(RegistrationRequestResponseDto responseData) throws NoSuchAlgorithmException, InvalidKeyException {
        User user = userRepository.findByEmail(responseData.getEmail());
        if (!responseData.isAccepted()) {
            user.setRegistrationStatus(RegistrationStatus.REJECTED);
        }
        else {
            user.setRegistrationStatus(RegistrationStatus.WAITING);
        }
        user.setRequestProcessingDate(new Date());
        userRepository.save(user);
        emailService.sendRegistrationEmail(responseData);
    }

    public boolean checkIfExists(String email)
    {
        User user = userRepository.findByEmail(email);
        return user != null;
    }

    public boolean checkServicePackage(String email)
    {
        User user = userRepository.findByEmail(email);
        return user.getServicesPackage() == ServicesPackage.STANDARD || user.getServicesPackage() == ServicesPackage.GOLDEN;
    }

    public boolean checkRole(String email)
    {
        return userRepository.findByEmail(email).getRoles().contains(UserRole.CLIENT);
    }
    
    public String confirmToken(String token) throws NoSuchAlgorithmException, InvalidKeyException {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token);
        String htmlResponse = "";

        if (confirmationToken != null) {
            boolean hmacValid = verifyHmac(token, "ana123", confirmationToken.getHmac());

            if (!hmacValid) {
                return "HMAC signature invalid";
            }

            Date createdDate = confirmationToken.getCreatedDate();
            int duration = confirmationToken.getDuration();
            Date expiryDate = calculateExpiryDate(createdDate, duration);
            User user = confirmationToken.getUser();

            if (new Date().after(expiryDate)) {
                htmlResponse = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><title>Token Expired</title></head><body><h1>Token Expired</h1><p>The token has expired. Please register again.</p></body></html>";
                confirmationTokenRepository.delete(confirmationToken);
                userRepository.delete(user);
            }
            else {
                user.setRegistrationStatus(RegistrationStatus.ACCEPTED);
                user.setActive(true);
                user.setEnabled(true); //proveri!!
                userRepository.save(user);
                confirmationTokenRepository.delete(confirmationToken);
                htmlResponse = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><title>Confirmation Success</title></head><body><h1>Congratulations!</h1><p>You have successfully confirmed your registration. You can now log in.</p></body></html>";
            }
            return htmlResponse;
        } else {
            return"Token not found";
        }
    }
    private boolean verifyHmac(String data, String key, String hmacToVerify) throws NoSuchAlgorithmException, InvalidKeyException {
        String generatedHmac = generateHmac(data, key);
        return hmacToVerify.equals(generatedHmac);
    }

    private String generateHmac(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        sha256Hmac.init(secretKey);
        byte[] hmacData = sha256Hmac.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(hmacData);
    }

    private Date calculateExpiryDate(Date createdDate, int duration) {
        long expiryTimeMillis = createdDate.getTime() + (duration * 60 * 1000);
        return new Date(expiryTimeMillis);
    }

    public User getUserData() {
        return getLoggedInUser();
    }

    public String updateUserPassword(PasswordDataDto passwordData) throws NoSuchAlgorithmException {
        if (passwordData.getEmail().isEmpty()) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                User user = (User)auth.getPrincipal();
                passwordData.setEmail(user.getEmail());
        }
        if (!checkOldPassword(passwordData)) {
            return "You have not entered a good current password.";
        }
        if (!passwordData.getNewPassword().equals(passwordData.getConfirmPassword())) {
            return "The new password and confirm password do not match.";
        }
        if (!validatePassword(passwordData.getNewPassword())) {
            return "The password does not meet the requirements.";
        }

        User user = userRepository.findByEmail(passwordData.getEmail());

        String salt = BCrypt.gensalt();
        String hashedPassword = "";
        try {
            hashedPassword = hashPassword(passwordData.getNewPassword(), salt);
            user.setPassword(hashedPassword);
            user.setSalt(salt);
        } catch (NoSuchAlgorithmException e) {
        }

        if (!user.isLoggedInOnce()) {
            user.setLoggedInOnce(true);
        }

        userRepository.save(user);

        return "Password successfully changed.";
    }

    private boolean checkOldPassword(PasswordDataDto passwordData) throws NoSuchAlgorithmException {
        User user = userRepository.findByEmail(passwordData.getEmail());
        String salt = user.getSalt();
        String hashedOldPassword = hashPassword(passwordData.getOldPassword(), salt);
        return hashedOldPassword.equals(user.getPassword());
    }

    public String updateUserData(EditAdminDto adminData) {
        //ovde bi trebalo da znam koji user je ulogovan i njega da izvucem iz baze
        //za sad zakucam sa emailom, pa cemo videti kad budemo dobavljali ulogovanog usera
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = (User)auth.getPrincipal();
        System.out.println("ULOGOVAN USERRR:  " + loggedInUser.getEmail());

        User user = userRepository.findByEmail(loggedInUser.getEmail());

        if (!areAllFieldsFilledAdmin(adminData)) {
            return "All fields are required.";
        }

        user.setName(adminData.getName());
        user.setSurname(adminData.getSurname());
        user.setAddress(adminData.getAddress());
        user.setCity(adminData.getCity());
        user.setCountry(adminData.getCountry());
        user.setPhoneNumber(adminData.getPhoneNumber());

        userRepository.save(user);

        return "User successfully updated.";
    }

    public List<User> getAllEmployees() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = (User)auth.getPrincipal();
        System.out.println("ULOGOVAN USERRR:  " + loggedInUser.getEmail());
        return userRepository.findByRolesAndRegistrationStatus(UserRole.EMPLOYEE, RegistrationStatus.ACCEPTED);
    }

    public List<User> getAllClients() {
        return userRepository.findByRolesAndRegistrationStatus(UserRole.CLIENT, RegistrationStatus.ACCEPTED);
    }
    public void updateUser(UserDto userDto) {
        User user = userRepository.findByEmail(userDto.getEmail());
        if (user != null) {
            user.setAddress(userDto.getAddress());
            user.setCity(userDto.getCity());
            user.setCountry(userDto.getCountry());
            user.setName(userDto.getName());
            user.setSurname(userDto.getSurname());
            user.setPhoneNumber(userDto.getPhoneNumber());
            user.setClientType(userDto.getClientType());

            //OVO VIDETI!!!
            user.setRoles(userDto.getRoles());

            user.setServicesPackage(userDto.getServicesPackage());

            userRepository.save(user);
        }
    }

    public User findByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username);
    }

    public User getLoggedInUser() {
        Authentication auth = null;
        try {
            auth = SecurityContextHolder.getContext().getAuthentication();
        }
        catch (Exception e) {
            return null;
        }
        return (User)auth.getPrincipal();
    }

    //OVDE DODATI ODAKLE DOLAZI ZAHTEV AKO JE IZ REGISTRACIJE DA NE SETUJE NA LOGGED IN ONCE TRUE
    //AKO JE IZ LOGINA ONDA DA SETUJE NA LOGGED IN ONCE TRUE
    public ResponseDto verifyCode(VerificationRequestDto verificationRequestDto) {
        ResponseDto responseDto = new ResponseDto();
        User user = userRepository.findByEmail(verificationRequestDto.getEmail());
        if (tfaService.isOtpNotValid(user.getSecret(), verificationRequestDto.getCode())) {

            //throw new BadCredentialsException("Code is not correct");
            responseDto.setFlag(false);
            responseDto.setResponseMessage("Code is not correct");
            return responseDto;
        }
        responseDto.setFlag(true);
        responseDto.setResponseMessage("Code is correct.");

        if (verificationRequestDto.isFromLogin() && !user.isLoggedInOnce()) {
            user.setLoggedInOnce(true);
        }

        user.setVerifiedMfaCode(true);
        userRepository.save(user);
        return responseDto;
    }

    public ResponseDto verifyReCaptchaToken(VerificationReCaptchaRequestDto verificationRequest) {
        ResponseDto responseDto = new ResponseDto();

        ReCaptchaResponseDto reCaptchaResponseDto = reCaptchaService.verifyReCaptchaToken(verificationRequest);

        System.out.println("RECAPTCHA RESPONSE:");
        System.out.println("RECAP SUCC: " + reCaptchaResponseDto.isSuccess());
        System.out.println("RECAP HOST: " + reCaptchaResponseDto.getHostname());
        System.out.println("RECAP ERROR: " + reCaptchaResponseDto.getError_codes());

        if (reCaptchaResponseDto.isSuccess()) {
            responseDto.setResponseMessage("Successful verification using ReCaptcha.");
            responseDto.setFlag(true);
            return responseDto;
        }

        responseDto.setResponseMessage("ReCaptcha verification failed.");
        responseDto.setFlag(false);
        return responseDto;
    }
}
//kada hocu da proverim da li mi je korisnik uneo dobru lozinku
//onda uzmem njehovu lozinku, uzmem salt koji imam u bazi spojim ih HESIRAM i poredim onda HESOVE


//PREPORUKE ZA LOZINKE:
//-minimum 12 karaktera
//-bar jedno malo slovo
//-bar jedno veliko slovo
//-bar jedan specijalan karakter
//-bar jedan broj