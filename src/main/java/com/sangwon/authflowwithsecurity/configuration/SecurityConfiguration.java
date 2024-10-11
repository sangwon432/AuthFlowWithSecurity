package com.sangwon.authflowwithsecurity.configuration;

import com.sangwon.authflowwithsecurity.utilty.ApiEndpointSecurityInspector;
import com.sangwon.authflowwithsecurity.utilty.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final ApiEndpointSecurityInspector apiEndpointSecurityInspector;

    @Bean
    @SneakyThrows
    public SecurityFilterChain configure(HttpSecurity http, CorsConfigurationSource corsConfigurationSource, ApiEndpointSecurityInspector apiEndpointSecurityInspector) {
        http
                .cors(corsConfigurer -> corsConfigurer.disable())
                .csrf(csrfConfigurer -> csrfConfigurer.disable())
                .exceptionHandling(exceptionConfigurer -> exceptionConfigurer.authenticationEntryPoint(customAuthenticationEntryPoint))
                .sessionManagement(sessionConfigurer -> sessionConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authManager -> {
                    authManager
                            .requestMatchers(HttpMethod.GET, apiEndpointSecurityInspector.getPublicGetEndpoints().toArray(String[]::new)).permitAll()
                            .requestMatchers(HttpMethod.POST, apiEndpointSecurityInspector.getPublicPostEndpoints().toArray(String[]::new)).permitAll()
                            .requestMatchers(HttpMethod.PUT, apiEndpointSecurityInspector.getPublicPutEndpoints().toArray(String[]::new)).permitAll()
                            .anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
