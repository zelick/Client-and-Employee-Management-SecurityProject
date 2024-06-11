package org.example.securityproject.service;

import org.example.securityproject.dto.ReCaptchaResponseDto;
import org.example.securityproject.dto.VerificationReCaptchaRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

@Service
public class ReCaptchaService {

    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    private final String GOOGLE_RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    private final RestTemplate restTemplate;

    public ReCaptchaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /*
    public ReCaptchaResponseDto verifyReCaptchaToken(VerificationReCaptchaRequestDto verificationRequest) {
        ReCaptchaResponseDto responseDto = new ReCaptchaResponseDto();

        // Formiranje zahteva
        VerificationReCaptchaServerRequestDto serverRequest = new VerificationReCaptchaServerRequestDto();
        serverRequest.setSecret(recaptchaSecret);
        serverRequest.setResponse(verificationRequest.getReCaptchaToken());

        // Pozivanje Google ReCAPTCHA API
        ReCaptchaResponseDto apiResponse = restTemplate.postForObject(GOOGLE_RECAPTCHA_VERIFY_URL, serverRequest, ReCaptchaResponseDto.class);

        // Upisivanje odgovora u ResponseDto
        responseDto.setSuccess(apiResponse.isSuccess());
        responseDto.setChallenge_ts(apiResponse.getChallenge_ts());
        responseDto.setHostname(apiResponse.getHostname());
        responseDto.setError_codes(apiResponse.getError_codes());

        return responseDto;
    }

     */

    public ReCaptchaResponseDto verifyReCaptchaToken(VerificationReCaptchaRequestDto verificationReCaptchaRequestDto) {
        // Definišemo URL Google ReCAPTCHA API-ja
        String apiUrl = "https://www.google.com/recaptcha/api/siteverify";

        // Postavljamo parametre zahteva
        URI uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("secret", recaptchaSecret)
                .queryParam("response", verificationReCaptchaRequestDto.getReCaptchaToken())
                .build().toUri();

        // Kreiramo HTTP zaglavlje sa Content-Type application/json
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Kreiramo REST template za slanje HTTP zahteva
        RestTemplate restTemplate = new RestTemplate();

        // Kreiramo HTTP zahtev sa definisanim URL-om, zaglavljima i telom zahteva
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Šaljemo POST zahtev ka Google ReCAPTCHA API-ju
        ResponseEntity<ReCaptchaResponseDto> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                requestEntity,
                ReCaptchaResponseDto.class
        );

        // Dobijamo odgovor od Google ReCAPTCHA API-ja
        return responseEntity.getBody();
    }
}