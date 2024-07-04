package com.example_login_2.config.Token;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example_login_2.model.JwtToken;
import com.example_login_2.service.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
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
        String authorization = request.getHeader("Authorization");
        if (ObjectUtils.isEmpty(authorization)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        if (!authorization.startsWith("Bearer")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String token = authorization.substring(7);

        DecodedJWT decodedJWT = jwtTokenService.verify(token);
        if (decodedJWT == null) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        Optional<JwtToken> optJwtToken = jwtTokenService.getJwtToken(token);
        if (optJwtToken.isPresent() && optJwtToken.get().isRevoked()) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        Long principal = decodedJWT.getClaim("principal").asLong();
        List<GrantedAuthority> authorities = new ArrayList<>();
        List<String> roles = decodedJWT.getClaim("roles").asList(String.class);

        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(principal, "protected", authorities);

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authenticationToken);

        filterChain.doFilter(servletRequest, servletResponse);
    }

}