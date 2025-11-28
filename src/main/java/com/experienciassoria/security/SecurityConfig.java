package com.experienciassoria.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableMethodSecurity(prePostEnabled = true) // ✅ Habilitar @PreAuthorize
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, CorsConfigurationSource corsConfigurationSource) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos (sin JWT requerido)
                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                .requestMatchers("GET", "/api/experiencias").permitAll()
                .requestMatchers("GET", "/api/experiencias/{id}").permitAll()
                .requestMatchers("GET", "/api/experiencias/uid/{uid}").permitAll()
                .requestMatchers("GET", "/api/experiencias/{id}/comentarios").permitAll()
                .requestMatchers("GET", "/api/top").permitAll()
                .requestMatchers("/api/public/admin/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                // Endpoints que requieren autenticación (USER o ADMIN - con JWT)
                .requestMatchers("/api/auth/me").authenticated()
                .requestMatchers("/api/pasaporte/**").authenticated()
                .requestMatchers("POST", "/api/experiencias/{id}/comentarios").authenticated()
                // Endpoints que requieren rol ADMIN
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("POST", "/api/experiencias").hasRole("ADMIN")
                .requestMatchers("PUT", "/api/experiencias/**").hasRole("ADMIN")
                .requestMatchers("DELETE", "/api/experiencias/**").hasRole("ADMIN")
                .requestMatchers("/api/experiencias/{id}/uids").hasRole("ADMIN")
                .requestMatchers("/api/experiencias/{id}/generar-uid").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
