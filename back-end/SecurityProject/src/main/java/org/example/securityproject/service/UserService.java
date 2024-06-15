package org.example.securityproject.service;

import lombok.AllArgsConstructor;
import org.example.securityproject.controller.AuthenticationController;
import org.example.securityproject.dto.*;
import org.example.securityproject.enums.RegistrationStatus;
import org.example.securityproject.enums.UserRole;
import org.example.securityproject.enums.ServicesPackage;
import org.example.securityproject.model.ConfirmationToken;
import org.example.securityproject.model.Notification;
import org.example.securityproject.model.User;
import org.example.securityproject.repository.ConfirmationTokenRepository;
import org.example.securityproject.repository.NotificationRepository;
import org.example.securityproject.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;
    private EmailService emailService;
    private ConfirmationTokenRepository confirmationTokenRepository;
    private NotificationRepository notificationRepository;
    private AdRequestService adRequestService;
    private AdService adService;
    private final TwoFactorAuthenticationService tfaService;
    private ReCaptchaService reCaptchaService;
    private UserDataEncryptionService userDataEncryptionService;

    public void deleteUserDataByEmail(String email)
    {
        adRequestService.deleteAdRequestsByEmail(email);
        System.out.println("Prosao je brisanje zahteva za reklame");
        adService.deleteAdsByEmail(email);
        System.out.println("Prosoa je brisanje reklama i zahteva za reklame");
        User user = userRepository.findByEmail(email);
        System.out.println("Pronadjen user: " + user.getName());
        if (user != null) {
            user.setPhoneNumber("");
            user.setCountry("");
            user.setAddress("");
            user.setCity("");
            user.setSurname("");
            user.setName("");
            userRepository.save(user);
        }
    }


    public LoginReponseDto resetPassword(UserLoginData loginData){
        LoginReponseDto loginResponseDto = new LoginReponseDto();

        if(!validatePassword(loginData.getPassword())){
            loginResponseDto.setLoggedInOnce(true);
            loginResponseDto.setResponse("Password do not meet the requirements.");
            return loginResponseDto;
        }

        //OVO CEMO NA DRUGACIJI NACIN DOBAVITI USERA - MOZDA??? zbog jwt
        //User user = userRepository.findByEmail(loginData.getEmail());

        try{
            User user = userDataEncryptionService.findEncryptedUserByEmail(loginData.getEmail());
            String enteredPasswordHash = hashPassword(loginData.getPassword(), user.getSalt());
            user.setPassword(enteredPasswordHash);
            userRepository.save(user);
            loginResponseDto.setLoggedInOnce(true);
            loginResponseDto.setResponse("You have successfully reset your password.");
        }catch (Exception e) {
            // Handle exception
        }
        loginResponseDto.setLoggedInOnce(true); 
        loginResponseDto.setResponse("Reset password failed.");
        return loginResponseDto;
    }


    public LoginReponseDto loginUser(UserLoginData loginData) throws Exception {
        LoginReponseDto loginResponseDto = new LoginReponseDto();
        logger.debug("Starting try login registration for email: {}", loginData.getEmail());
        //User user = userRepository.findByEmail(loginData.getEmail());
        //umesto metode userRepo.findByEmail koristiti ove dve metode
        User user = userDataEncryptionService.findEncryptedUserByEmail(loginData.getEmail());
        //NAJVRV NI NE TREBA DEKRIPTOVANJE - jer su svi ti podaci ne enkriptovani
        //User user = userDataEncryptionService.decryptUserData(encryptedUser);

        // Provera postoji li korisnik s tim emailom
        if (user == null) {
            String errorMessage = "User login failed: User not found for email " + loginData.getEmail();
            logger.error(errorMessage);
            loginResponseDto.setMfaEnabled(false);
            loginResponseDto.setLoggedInOnce(false); 
            loginResponseDto.setResponse("This email does not exist.");
            return loginResponseDto;
            //return null; //PROBLEM: ako vrati 200 poziva metodu login iz auth controllera - tu dobijamo 401
        }

        System.out.println("ENKRIPTOCAN USER: " + user.getEmail());
        //System.out.println("DEKRIPTOVAN USER: " + user.getEmail());

        if (user.getRoles().contains(UserRole.EMPLOYEE)) {
            loginResponseDto.setEmployeed(true);
        }
        else {
            loginResponseDto.setEmployeed(false);
        }

        System.out.println("EMPLOYEE: " + loginResponseDto.isEmployeed());

        //PROBLEM ZA EMPLOYEE-A AKO ISPRAVNO RESI RECAHPCA ONDA TEK DA MU SE SETUJE LOGGED IN ONCE NA TRUE!!!

        if (!(user.isActive() && user.isEnabled())) {
            String errorMessage = "User login failed: Account is not active for email " + loginData.getEmail();
            logger.error(errorMessage);
            loginResponseDto.setMfaEnabled(false);
            loginResponseDto.setLoggedInOnce(false);
            loginResponseDto.setResponse("This account is not active, please wait for admin to activate your account.");
            return loginResponseDto;
        }


        if (user.isBlocked()) {
            String errorMessage = "User login failed: User is blocked email " + loginData.getEmail();
            logger.error(errorMessage);
            loginResponseDto.setLoggedInOnce(true);
            loginResponseDto.setResponse("Your account has been blocked by administrator.");
            return loginResponseDto;
        }

         try {
            System.out.println("Usao u try catch blok");
            System.out.println(loginData.getEmail() + "" + loginData.getPassword());
            String enteredPasswordHash = hashPassword(loginData.getPassword(), user.getSalt());
            if (!enteredPasswordHash.equals(user.getPassword())) {
                loginResponseDto.setLoggedInOnce(true);
                loginResponseDto.setResponse("Wrong password. Try again or click ‘Forgot password’ to reset it.");
                return loginResponseDto;
            }
        } catch (NoSuchAlgorithmException e) {
            // Handle exception
        }

        boolean hasAdministratorOrEmployeeRole = user.getRoles().stream()
                .anyMatch(role -> role.equals(UserRole.ADMINISTRATOR) || role.equals(UserRole.EMPLOYEE));

        if (hasAdministratorOrEmployeeRole && !user.isLoggedInOnce()) {
            String infoMessage = "User login requires password change for email " + loginData.getEmail();
            logger.info(infoMessage);
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

        String successMessage = "User logged in successfully: " + loginData.getEmail();
        logger.info(successMessage);
        loginResponseDto.setMfaEnabled(false);
        loginResponseDto.setLoggedInOnce(true);
        loginResponseDto.setResponse("This user has successfully logged in.");
        return loginResponseDto;
    }

    //dodaj logove
    public RegistrationResponseDto registerUser (UserDto userDto) throws Exception {
        RegistrationResponseDto response = new RegistrationResponseDto();
        if (!validatePassword(userDto.getPassword())) {
            response.setResponseMessage("The password does not meet the requirements.");
            response.setFlag(false);
            response.setSecretImageUri("");
            return response;
        }

        //User existingUser = userRepository.findByEmailAndRegistrationStatusIn(userDto.getEmail(), Arrays.asList(RegistrationStatus.PENDING, RegistrationStatus.ACCEPTED));

        User existingEncryptedUser = userDataEncryptionService.findByEmailAndRegistrationStatus(userDto.getEmail());
        //User existingUser = userDataEncryptionService.decryptUserData(encryptedUser);

        if (existingEncryptedUser != null) {
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

        //User existingRejectedUser = userRepository.findByEmailAndRegistrationStatus(userDto.getEmail(), RegistrationStatus.REJECTED);
        User existingEncryptedRejectedUser = userDataEncryptionService.findByEmailRejcetedUser(userDto.getEmail());

        if (existingEncryptedRejectedUser != null && existingEncryptedRejectedUser.getRequestProcessingDate() != null) {
            Date requestProcessedDate = existingEncryptedRejectedUser.getRequestProcessingDate();
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
               userRepository.delete(existingEncryptedRejectedUser);
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

        //userRepository.save(user);

        userDataEncryptionService.encryptUserData(user);

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

    //OVDE DEKRIPTOVATI NEKE PODATKE
    public List<User> getAllRegistrationRequests() {
        logger.info("Fetching all registration requests with status: PENDING");
        return userRepository.findByRegistrationStatus(RegistrationStatus.PENDING);
    }

    public void processRegistrationRequest(RegistrationRequestResponseDto responseData) throws Exception {
        //User user = userRepository.findByEmail(responseData.getEmail());
        logger.info("Processing registration request for email: {}", responseData.getEmail());
        User encryptedUser = userDataEncryptionService.findEncryptedUserByEmail(responseData.getEmail());
        //User user = userDataEncryptionService.decryptUserData(encryptedUser);

        if (!responseData.isAccepted()) {
             logger.info("Registration request rejected for email: {}", responseData.getEmail());
            encryptedUser.setRegistrationStatus(RegistrationStatus.REJECTED);
        }
        else {
            logger.info("Registration request accepted for email: {}", responseData.getEmail());
            encryptedUser.setRegistrationStatus(RegistrationStatus.WAITING);
        }
        encryptedUser.setRequestProcessingDate(new Date());
        userRepository.save(encryptedUser);
        emailService.sendRegistrationEmail(responseData);
        logger.info("Registration request processing completed for email: {}", responseData.getEmail());
    }

    public boolean checkIfExists(String email) throws Exception {
        //User user = userRepository.findByEmail(email);
        User encryptedUser = userDataEncryptionService.findEncryptedUserByEmail(email);

        return encryptedUser != null;
    }

    public boolean checkServicePackage(String email) throws Exception {
        //User user = userRepository.findByEmail(email);
        User user = userDataEncryptionService.findEncryptedUserByEmail(email);

        return user.getServicesPackage() == ServicesPackage.STANDARD || user.getServicesPackage() == ServicesPackage.GOLDEN;
    }

    public boolean checkRole(String email) throws Exception {
        User user = userDataEncryptionService.findEncryptedUserByEmail(email);

        return user.getRoles().contains(UserRole.CLIENT);
    }
    
    public String confirmToken(String token) throws NoSuchAlgorithmException, InvalidKeyException {
        logger.info("Confirming token: {}", token);
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token);
        String htmlResponse = "";

        if (confirmationToken != null) {
            boolean hmacValid = verifyHmac(token, "ana123", confirmationToken.getHmac());

            if (!hmacValid) {
                String errorMessage = "HMAC signature invalid for token: " + token;
                logger.error(errorMessage);
                return "HMAC signature invalid";
            }

            Date createdDate = confirmationToken.getCreatedDate();
            int duration = confirmationToken.getDuration();
            Date expiryDate = calculateExpiryDate(createdDate, duration);
            User user = confirmationToken.getUser();

            if (new Date().after(expiryDate)) {
                logger.warn("Token expired for token: {}", token);
                htmlResponse = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><title>Token Expired</title></head><body><h1>Token Expired</h1><p>The token has expired. Please register again.</p></body></html>";
                confirmationTokenRepository.delete(confirmationToken);
                userRepository.delete(user);
            }
            else {
                logger.info("Token confirmed successfully for token: {}", token);
                user.setRegistrationStatus(RegistrationStatus.ACCEPTED);
                user.setActive(true);
                user.setEnabled(true); //proveri!!
                userRepository.save(user);
                confirmationTokenRepository.delete(confirmationToken);
                htmlResponse = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><title>Confirmation Success</title></head><body><h1>Congratulations!</h1><p>You have successfully confirmed your registration. You can now log in.</p></body></html>";
            }
            return htmlResponse;
        } else {
            logger.error("Token not found: {}", token);
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

    public User getUserData() throws Exception {
        User encryptedUser = getLoggedInUser();

        User decryptedUser = userDataEncryptionService.decryptUserData(encryptedUser);

        return decryptedUser;
    }

    //OVDE MOZDA PROBLEM ZBOG ENKRIPCIJE PODATAKA
    public String updateUserPassword(PasswordDataDto passwordData) throws Exception {

         logger.info("Updating password for user with email: {}", passwordData.getEmail());

        if (passwordData.getEmail().isEmpty()) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                User user = (User)auth.getPrincipal(); //OVDE PROBLEM MOZDA
                //ovom ulogovanom useru je trenutno ENKRIPTOVAN MEJL
                String decryptedEmail = userDataEncryptionService.decryptData(user.getEmail());
                passwordData.setEmail(decryptedEmail); //OVDE PROBLEM MOZDA
        }
        if (!checkOldPassword(passwordData)) {
            String errorMessage = "Failed to update password: Incorrect current password for email " + passwordData.getEmail();
            logger.error(errorMessage);
            return "You have not entered a good current password.";
        }
        if (!passwordData.getNewPassword().equals(passwordData.getConfirmPassword())) {
            String errorMessage = "Failed to update password: New password and confirm password do not match for email " + passwordData.getEmail();
            logger.error(errorMessage);
            return "The new password and confirm password do not match.";
        }
        if (!validatePassword(passwordData.getNewPassword())) {
            String errorMessage = "Failed to update password: Password does not meet the requirements for email " + passwordData.getEmail();
            logger.error(errorMessage);
            return "The password does not meet the requirements.";
        }

        //User user = userRepository.findByEmail(passwordData.getEmail());
        User user = userDataEncryptionService.findEncryptedUserByEmail(passwordData.getEmail());

        String salt = BCrypt.gensalt();
        String hashedPassword = "";
        try {
            hashedPassword = hashPassword(passwordData.getNewPassword(), salt);
            user.setPassword(hashedPassword);
            user.setSalt(salt);
        } catch (NoSuchAlgorithmException e) {
            String errorMessage = "Failed to update password: Error hashing password for email " + passwordData.getEmail();
            logger.error(errorMessage, e);
        }

        if (!user.isLoggedInOnce()) {
            user.setLoggedInOnce(true);
        }

        userRepository.save(user);
        logger.info("Password updated successfully for user with email: {}", passwordData.getEmail());
        return "Password successfully changed.";
    }

    private boolean checkOldPassword(PasswordDataDto passwordData) throws Exception {
        //User user = userRepository.findByEmail(passwordData.getEmail());
        User user = userDataEncryptionService.findEncryptedUserByEmail(passwordData.getEmail());

        String salt = user.getSalt();
        String hashedOldPassword = hashPassword(passwordData.getOldPassword(), salt);
        return hashedOldPassword.equals(user.getPassword());
    }

    public String updateUserData(EditAdminDto adminData) throws Exception {
        //ovde bi trebalo da znam koji user je ulogovan i njega da izvucem iz baze
        //za sad zakucam sa emailom, pa cemo videti kad budemo dobavljali ulogovanog usera
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = (User)auth.getPrincipal();
        logger.info("Updating user data for user with email: {}", loggedInUser.getEmail());

        //ovde moze ova metoda da ostane jer kada je dobavio ulogovanog njemu je mejl vec enkriptovan
        User user = userRepository.findByEmail(loggedInUser.getEmail());

        if (!areAllFieldsFilledAdmin(adminData)) {
            String errorMessage = "Failed to update user data: All fields are required for user with email " + loggedInUser.getEmail();
            logger.error(errorMessage);
            return "All fields are required.";
        }

        user.setName(adminData.getName());
        user.setSurname(adminData.getSurname());
        user.setAddress(adminData.getAddress());
        user.setCity(adminData.getCity());
        user.setCountry(adminData.getCountry());
        user.setPhoneNumber(adminData.getPhoneNumber());

        //userRepository.save(user);
        logger.info("User data updated successfully for user with email: {}", loggedInUser.getEmail());
        userDataEncryptionService.encryptUpdateUserData(user);
        return "User successfully updated.";
    }

    public List<User> getAllEmployees() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = (User)auth.getPrincipal();
        logger.info("Fetching all employees for logged in user: {}", loggedInUser.getEmail());
        return userRepository.findByRolesAndRegistrationStatus(UserRole.EMPLOYEE, RegistrationStatus.ACCEPTED);
    }

    public List<User> getAllClients() {
        return userRepository.findByRolesAndRegistrationStatus(UserRole.CLIENT, RegistrationStatus.ACCEPTED);
    }

    //proveri da li je enkripotvano
    public List<User> getAllUsers() {
        List<User> allUsers = new ArrayList<>();
        allUsers.addAll(getAllClients());
        allUsers.addAll(getAllEmployees());
        List<User> allFilteredUsers = new ArrayList<>();
        for(User u : allUsers)
        {
            allFilteredUsers.add(u);
        }
        return allFilteredUsers;
    }


    public void updateUser(UserDto userDto) throws Exception {
        //User user = userRepository.findByEmail(userDto.getEmail());
        User user = userDataEncryptionService.findEncryptedUserByEmail(userDto.getEmail());

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

            //userRepository.save(user);
            userDataEncryptionService.encryptUpdateUserData(user);
            logger.info("User {} updated successfully.", user.getEmail());
        }else {
            logger.warn("User with email {} not found. Update operation aborted.", userDto.getEmail());
        }
    }

    public User findByUsername(String username) throws Exception {
        User user = userDataEncryptionService.findEncryptedUserByEmail(username);

        if (user == null) {
            logger.warn("User with username {} not found.", username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        } else {
            logger.debug("User found with username {}: {}", username, user);
        }

        return user;
    }

    public User getLoggedInUser() {
        Authentication auth = null;
        try {
            auth = SecurityContextHolder.getContext().getAuthentication();
        }
        catch (Exception e) {
            logger.error("Failed to retrieve Authentication object: {}", e.getMessage());
            return null;
        }
        return (User)auth.getPrincipal();
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
    
    public ResponseDto blockUser(String email) throws Exception {
        ResponseDto response = new ResponseDto();
        //User user = userRepository.findByEmail(email);
        User user = userDataEncryptionService.findEncryptedUserByEmail(email);

        if (user == null) {
            response.setResponseMessage("User not found.");
            response.setFlag(false);
            return response;
        }

        user.setBlocked(true);
        userRepository.save(user);

        response.setResponseMessage("User successfully blocked.");
        response.setFlag(true);
        return response;
    }

    public ResponseDto unblockUser(String email) throws Exception {
        ResponseDto response = new ResponseDto();
        //User user = userRepository.findByEmail(email);
        User user = userDataEncryptionService.findEncryptedUserByEmail(email);
        
        if (user == null) {
            response.setResponseMessage("User not found.");
            response.setFlag(false);
            return response;
        }

        user.setBlocked(false);
        userRepository.save(user);

        response.setResponseMessage("User successfully unblocked.");
        response.setFlag(true);
        return response;
    }

    public ResponseDto verifyCode(VerificationRequestDto verificationRequestDto) throws Exception {
        ResponseDto responseDto = new ResponseDto();
        //User user = userRepository.findByEmail(verificationRequestDto.getEmail());
        User user = userDataEncryptionService.findEncryptedUserByEmail(verificationRequestDto.getEmail());

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