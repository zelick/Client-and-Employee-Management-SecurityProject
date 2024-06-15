package org.example.securityproject.service;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Service;

@Service
public class HtmlSanitizerService {
    private final PolicyFactory policy;

    public HtmlSanitizerService() {
        this.policy = new HtmlPolicyBuilder()
                .allowElements("a", "b", "i", "u", "p", "div", "span")
                .allowUrlProtocols("http", "https")
                .allowAttributes("href").onElements("a")
                .toFactory();
    }

    public String sanitize(String input) {
        return policy.sanitize(input);
    }
}