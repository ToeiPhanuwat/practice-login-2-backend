package com.example_login_2.exception.customImp;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        ErrorResponse errorResponse = new ErrorResponse()
                .setTimestamp(String.valueOf(Instant.now()))
                .setStatus(HttpStatus.FORBIDDEN.value())
                .setError("Forbidden")
                .setMessage("You do not have permission to access this resource.");

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class ErrorResponse {

        private String timestamp;

        private int status;

        private String error;

        private String message;
    }
}
