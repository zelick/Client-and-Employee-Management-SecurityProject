//package org.example.securityproject.config;
//
//import org.aspectj.lang.annotation.After;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//@Aspect
//@Component
//public class ConfigurationLoggingAspect {
//    private static final Logger logger = LoggerFactory.getLogger(ConfigurationLoggingAspect.class);
//
//    @Pointcut("execution(* org.example.securityproject.service.ConfigurationService.updateConfiguration(..))")
//    public void updateConfigurationPointcut() {
//    }
//
//    @After("updateConfigurationPointcut() && args(configKey, newValue)")
//    public void logConfigurationChange(String configKey, String newValue) {
//        logger.info("Configuration updated: {} = {}", configKey, newValue);
//    }
//
//    @Pointcut("execution(* org.example.securityproject.service.ConfigurationService.loadConfiguration(..))")
//    public void loadConfigurationPointcut() {
//    }
//
//    @After("loadConfigurationPointcut()")
//    public void logConfigurationLoad() {
//        logger.info("Configuration loaded");
//    }
//}
