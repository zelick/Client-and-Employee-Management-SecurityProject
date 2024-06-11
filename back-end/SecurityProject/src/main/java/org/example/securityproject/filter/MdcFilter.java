package org.example.securityproject.filter;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@Component
public class MdcFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicijalizacija filtera
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String eventId = UUID.randomUUID().toString();
        MDC.put("eventId", eventId);
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove("eventId");
        }
    }
}
