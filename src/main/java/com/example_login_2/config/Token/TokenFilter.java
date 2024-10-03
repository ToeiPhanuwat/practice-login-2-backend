package com.example_login_2.config.Token;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example_login_2.config.CustomUserDetails;
import com.example_login_2.service.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TokenFilter extends GenericFilterBean {

    private final JwtTokenService jwtTokenService;

    public TokenFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String authorization = request.getHeader("Authorization");
        if (ObjectUtils.isEmpty(authorization) || !authorization.startsWith("Bearer")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String token = authorization.substring(7);
        try {
            DecodedJWT decodedJWT = jwtTokenService.verify(token);
            if (decodedJWT == null) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }

            Long userId = decodedJWT.getClaim("userId").asLong();
            List<String> roles = Optional.ofNullable(decodedJWT.getClaim("roles").asList(String.class))
                    .orElse(new ArrayList<>());
            List<GrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            CustomUserDetails userDetails = new CustomUserDetails(userId, token, authorities);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authenticationToken);
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

}
