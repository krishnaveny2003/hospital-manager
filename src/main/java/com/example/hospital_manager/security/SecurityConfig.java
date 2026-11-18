
package com.example.hospital_manager.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    private final JsonAuthenticationEntryPoint entryPoint;

    private final JsonAccessDeniedHandler deniedHandler;

    public SecurityConfig(
            JwtAuthenticationFilter jwtFilter,
            JsonAuthenticationEntryPoint entryPoint,
            JsonAccessDeniedHandler deniedHandler) {

        this.jwtFilter = jwtFilter;
        this.entryPoint = entryPoint;
        this.deniedHandler = deniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(entryPoint)
                .accessDeniedHandler(deniedHandler)
            )

            .authorizeHttpRequests(auth -> auth

                // Public login
                .requestMatchers(
                    "/api/auth/login"
                ).permitAll()

                // Admin registration only
                .requestMatchers(
                    "/api/auth/register"
                ).hasRole("ADMIN")

                // User and doctor management
                .requestMatchers(
                    "/api/users/**",
                    "/api/doctors/**"
                ).hasRole("ADMIN")

                // Patient management
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/patients/**"
                ).hasAnyRole("ADMIN", "RECEPTIONIST")

                .requestMatchers(
                    "/api/patients/**"
                ).hasAnyRole("ADMIN", "RECEPTIONIST")

                // Appointment management
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/appointments"
                ).hasAnyRole("ADMIN", "RECEPTIONIST")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/appointments/doctor"
                ).hasRole("DOCTOR")

                .requestMatchers(
                    HttpMethod.PATCH,
                    "/api/appointments/**"
                ).hasRole("DOCTOR")

                // Medical records
                .requestMatchers(
                    "/api/records/**"
                ).hasRole("DOCTOR")

                // All other URLs need authentication
                .anyRequest().authenticated()
            )

            .addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

}
