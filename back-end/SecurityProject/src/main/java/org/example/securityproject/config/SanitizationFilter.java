package org.example.securityproject.config;

import org.example.securityproject.service.HtmlSanitizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

@Component
public class SanitizationFilter implements Filter {

    @Autowired
    private HtmlSanitizerService htmlSanitizerService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        SanitizedRequest sanitizedRequest = new SanitizedRequest((HttpServletRequest) request, htmlSanitizerService);
        chain.doFilter(sanitizedRequest, response);
    }

    @Override
    public void destroy() {
        // Cleanup logic if needed
    }

    private static class SanitizedRequest extends HttpServletRequestWrapper {

        private final HtmlSanitizerService htmlSanitizerService;

        public SanitizedRequest(HttpServletRequest request, HtmlSanitizerService htmlSanitizerService) {
            super(request);
            this.htmlSanitizerService = htmlSanitizerService;
        }

        @Override
        public String getParameter(String name) {
            String value = super.getParameter(name);
            return sanitize(value);
        }

        @Override
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            if (values == null) {
                return null;
            }
            String[] sanitizedValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                sanitizedValues[i] = sanitize(values[i]);
            }
            return sanitizedValues;
        }

        private String sanitize(String value) {
            if (value != null) {
                return htmlSanitizerService.sanitize(value);
            }
            return null;
        }
    }
}
