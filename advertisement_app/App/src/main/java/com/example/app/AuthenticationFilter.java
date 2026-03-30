package com.example.app;

import com.example.app.security.UserContext;
import com.example.shared.SilahubResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
public class AuthenticationFilter extends OncePerRequestFilter
{

    @Autowired
    private UserContext userContext;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        ;

        if(request.getMethod().equals( HttpMethod.OPTIONS.name()))
        {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader  = request.getHeader("Authorization");

        UUID userId = null;
        try {
            userId = UUID.fromString(authHeader);
            userContext.setUserId(userId);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            String body = objectMapper.writeValueAsString(
                    SilahubResponseUtil.fail(
                            HttpStatus.UNAUTHORIZED,
                            "Not Authenticated",
                            null,
                            Map.of()
                    )
            );

            response.getWriter().write(body) ;
            return;//stop filtering
        }

        filterChain.doFilter(request, response);
    }

}
