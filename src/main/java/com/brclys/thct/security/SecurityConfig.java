package com.brclys.thct.security;

import com.brclys.thct.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public static final String H2_CONSOLE_CSRF_URL = "/h2-console/**";
    public static final String API_DOCS_BASE = "/api-docs";
    public static final String SWAGGER_UI_URL = "/swagger-ui";
    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationEntryPointForJwt unauthorizedHandler;


    @Bean
    public AuthenticationTokenFilter authenticationJwtTokenFilter() {
        return new AuthenticationTokenFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Updated configuration for Spring Security 6.x
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers(H2_CONSOLE_CSRF_URL,
                        AppConstants.API_BASE_URL + AppConstants.AUTH_URL_BASE + "/**",
                        AppConstants.API_BASE_URL + "/users",
                        AppConstants.API_BASE_URL + AppConstants.ACCOUNTS_BASE_URL + "/**"))
                .headers(headers -> headers.frameOptions((HeadersConfigurer.FrameOptionsConfig::sameOrigin)))
                .cors(AbstractHttpConfigurer::disable) // Disable CORS (or configure if needed)
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorizeRequests ->
                                authorizeRequests.requestMatchers(
                                        AppConstants.API_BASE_URL + AppConstants.AUTH_URL_BASE + "/**",
                                                AppConstants.API_BASE_URL + "/users/**",
                                                AppConstants.API_BASE_URL + "/users",
                                                API_DOCS_BASE + "*", API_DOCS_BASE, API_DOCS_BASE + "/**"
                                                , SWAGGER_UI_URL + "/**", H2_CONSOLE_CSRF_URL
                                        ).permitAll()
                                        .requestMatchers(AppConstants.API_BASE_URL + AppConstants.ACCOUNTS_BASE_URL + "/**").authenticated()
                );
        return http.build();
    }


}
