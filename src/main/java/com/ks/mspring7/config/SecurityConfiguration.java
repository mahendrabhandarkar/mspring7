package com.ks.mspring7.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsUtils;

import static com.ks.mspring7.user.Permission.ADMIN_CREATE;
import static com.ks.mspring7.user.Permission.ADMIN_DELETE;
import static com.ks.mspring7.user.Permission.ADMIN_READ;
import static com.ks.mspring7.user.Permission.ADMIN_UPDATE;
import static com.ks.mspring7.user.Permission.MANAGER_CREATE;
import static com.ks.mspring7.user.Permission.MANAGER_DELETE;
import static com.ks.mspring7.user.Permission.MANAGER_READ;
import static com.ks.mspring7.user.Permission.MANAGER_UPDATE;
import static com.ks.mspring7.user.Role.ADMIN;
import static com.ks.mspring7.user.Role.MANAGER;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * Security configuration class - Used to add JWT filter.
 */
@Configuration
@EnableWebSecurity //(debug = true)
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private static final String[] WHITE_LIST_URL = {"/api/v1/auth/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html",
            "/web/v1",
            "/web/ks/**",
    "/web/v1/login", "/web/v1/loginCheck",
    };

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    /**
     * Spring security look it first
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                       /* req.requestMatchers("/**")
                                .permitAll()
                        */
                        req.requestMatchers(WHITE_LIST_URL)
                                .permitAll()
                                .requestMatchers("/api/v1/management/**").hasAnyRole(ADMIN.name(), MANAGER.name())
                                .requestMatchers(GET, "/api/v1/management/**").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name())
                                .requestMatchers(POST, "/api/v1/management/**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name())
                                .requestMatchers(PUT, "/api/v1/management/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name())
                                .requestMatchers(DELETE, "/api/v1/management/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name())
                                .anyRequest()
                                .authenticated()
                )
                .formLogin((formLogin) -> formLogin.loginPage("/web/v1/login")
                 //       .failureUrl("/web/v1/login-error")
                        .loginProcessingUrl("/web/v1/loginCheck")
                        .successForwardUrl("/web/v1/welcome")
                        .permitAll()
                )
                /**
                 * Every request should be authenticated. Spring will create new session for each request.
                 */
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                /**
                 *
                 */
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout.logoutUrl("/api/v1/auth/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                )
        ;

        return http.build();
    }
/*
// COde with error
    @Bean
    public SecurityFilterChain securityFilterChain(WebSecurity web) throws Exception {
        web.ignoring().requestMatchers(CorsUtils::isPreFlightRequest)
                .requestMatchers("/webjars/**")
                .anyRequest();
        return (SecurityFilterChain) web.build();
    }
 */
}
