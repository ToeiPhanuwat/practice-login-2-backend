package com.example_login_2.config;

import com.example_login_2.config.Token.TokenFilter;
import com.example_login_2.service.JwtTokenService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtTokenService jwtTokenService;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;

    public SecurityConfig(JwtTokenService jwtTokenService, AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.jwtTokenService = jwtTokenService;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    private TokenFilter tokenFilter() {
        return new TokenFilter(jwtTokenService);
    }

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private final String[] PUBLIC = {
            "/actuator/**",
            "/api/v1/auth/**"
    };

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/admin").hasRole("ADMIN")
                        .requestMatchers(PUBLIC).permitAll().anyRequest().authenticated())
//                .formLogin(formLogin ->
//                        formLogin
//                                .loginPage("/api/v1/auth/login")
//                                .successHandler(authenticationSuccessHandler)
//                )
                .logout(logout ->
                        logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/api/v1/auth/logout"))
                                .logoutSuccessUrl("/api/v1/auth/login?logout")
                )
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(tokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(
                                (request, response, authException) -> response.sendError(
                                        HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token2")
                        ));
        return http.build();
    }
}
