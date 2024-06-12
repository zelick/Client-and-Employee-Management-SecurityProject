package org.example.securityproject.config;

import org.example.securityproject.filter.MdcFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("https://localhost:4200")
//                        .allowedOrigins("http://loki:3100")  //http://localhost:3100/loki/api/v1/push
//                        .allowedOrigins("http://localhost:3100/loki/api/v1/push")
                        .allowedMethods(HttpMethod.GET.name(),
                                HttpMethod.POST.name(),
                                HttpMethod.DELETE.name(),
                                HttpMethod.PATCH.name(),
                                HttpMethod.PUT.name())
                        .allowedHeaders(HttpHeaders.CONTENT_TYPE,
                                HttpHeaders.AUTHORIZATION,
                                "Authorization", "Refresh-Token")
                       .exposedHeaders("Authorization", "Refresh-Token") // Ovo omogućava pristup ovim zaglavljima iz klijentske aplikacije
                       .allowCredentials(true);;
            }
        };
    }
}