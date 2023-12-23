package com.ks.mspring7.config;

import com.ks.mspring7.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

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
import static org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * Security configuration class - Used to add JWT filter.
 */
@Configuration
@EnableWebSecurity //(debug = true)
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private final ApplicationConfig applicationConfig;

    private static final String[] API_WHITE_LIST_URL = {"/api/v1/auth/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    private static final String[] WEB_WHITE_LIST_URL = {"/api/v1/auth/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/webjars/**",
            "/web/v1",
            "/web/ks/**",
            "/web/v1/login",
            "/web/v1/loginCheck",
    //        "/web/v1/welcome",
            "/web/v1/accessdenied"
    };

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web)->web.ignoring().requestMatchers(WEB_WHITE_LIST_URL);
    }


    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req
                                .requestMatchers(API_WHITE_LIST_URL)
                                .permitAll()
                                .requestMatchers("/api/v1/management/**").hasAnyRole(ADMIN.name(), MANAGER.name())
                                .requestMatchers(GET, "/api/v1/management/**").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name())
                                .requestMatchers(POST, "/api/v1/management/**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name())
                                .requestMatchers(PUT, "/api/v1/management/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name())
                                .requestMatchers(DELETE, "/api/v1/management/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name())
                                .anyRequest()
                                .permitAll()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
        //        .authenticationProvider(authenticationProvider)
                .authenticationProvider(applicationConfig.authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout.logoutUrl("/api/v1/auth/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                );
        return http.build();
    }


    @Bean
    public SecurityFilterChain formLoginFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(Customizer.withDefaults())
           //     .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req
                                .requestMatchers(WEB_WHITE_LIST_URL)
                                .permitAll()
                                .requestMatchers("/web/v1/management/**").hasAnyRole(ADMIN.name(), MANAGER.name())
                                .requestMatchers(GET, "/web/v1/management/**").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name())
                                .requestMatchers(POST, "/web/v1/management/**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name())
                                .requestMatchers(PUT, "/web/v1/management/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name())
                                .requestMatchers(DELETE, "/web/v1/management/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name())
                                .requestMatchers(GET, "/web/v1/welcome**")
                                .authenticated()

                        //        .anyRequest()
                        //        .permitAll()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(IF_REQUIRED))
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
                    httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(accessDeniedHandler());
                })
                .authenticationProvider(authenticationProvider)
                .logout(logout ->
                        logout.logoutUrl("/web/v1/auth/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                );

        return http.build();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    /*
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    RBACSpecifierService roleBasedAccessSpecifier;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new ShaPasswordEncryptUtil();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        ExpressionUrlAuthorizationConfigurer.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry = httpSecurity.authorizeHttpRequests();
        addRoleMatchers(expressionInterceptUrlRegistry);
        addCommonMatchers(expressionInterceptUrlRegistry);

        System.out.println("registryWithAutentication:" + expressionInterceptUrlRegistry);
        return httpSecurity.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedOrigin("*");
        source.registerCorsConfiguration("/**", configuration);
        return new CorsFilter(source);
    }
 */
}
