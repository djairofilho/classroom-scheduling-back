package com.classroomscheduler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            RestAuthenticationEntryPoint authenticationEntryPoint,
            RestAccessDeniedHandler accessDeniedHandler
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/**",
                                "/health",
                                "/h2-console/**",
                                "/docs/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers("/usuarios/**").hasRole("ADMIN")
                        .requestMatchers("/users/**").hasRole("ADMIN")
                        .requestMatchers("/solicitantes/**").hasRole("ADMIN")
                        .requestMatchers("/requesters/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/reservas").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/reservations").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/reservas/ativas").authenticated()
                        .requestMatchers(HttpMethod.GET, "/reservas/por-espaco").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/reservas/*/aprovar").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/reservas/*/recusar").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/reservations/*/approve").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/reservations/*/reject").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/notificacoes").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/notifications").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/notificacoes").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/notifications").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/espacos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/spaces").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/espacos/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/spaces/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/espacos/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/spaces/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/espacos/*/indisponibilidade").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/spaces/*/availability").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/predios").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/buildings").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/predios/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/buildings/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/predios/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/buildings/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/usuarios/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/users/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/usuarios/*/status").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/users/*/status").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/solicitantes/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/requesters/*").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
