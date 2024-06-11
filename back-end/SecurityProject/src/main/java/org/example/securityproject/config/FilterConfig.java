package org.example.securityproject.config;

//import ch.qos.logback.classic.turbo.MDCFilter;
import org.example.securityproject.filter.MdcFilter; //valjda je ovo ?
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<MdcFilter> mdcFilter() {
        FilterRegistrationBean<MdcFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new MdcFilter());
        registrationBean.addUrlPatterns("/*"); // Definiši URL obrasce na koje će filter primenjivati
        return registrationBean;
    }
}
