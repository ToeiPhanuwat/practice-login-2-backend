package com.example_login_2.config;

import com.example_login_2.config.Token.TokenFilter;
import com.example_login_2.exception.customImp.CustomAccessDeniedHandler;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtTokenService jwtTokenService;

    public SecurityConfig(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
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
    CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); // สร้างเพื่อกำหนดค่า CORS ให้กับ URL pattern ต่างๆ
        CorsConfiguration config = new CorsConfiguration(); // สร้างเพื่อกำหนดนโยบาย CORS
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config); // กำหนดนโยบาย CORS ให้กับ URL pattern ทั้งหมด
        return new CorsFilter(source); // สร้างและคืนค่า CorsFilter เพื่อใช้ในระบบ
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/admin").hasRole("ADMIN")
                        .requestMatchers(PUBLIC).permitAll().anyRequest().authenticated())
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(tokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(
                                (request, response, authException) -> response.sendError(
                                        HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token")
                        )
                        .accessDeniedHandler(new CustomAccessDeniedHandler()));
        return http.build();
    }
}
