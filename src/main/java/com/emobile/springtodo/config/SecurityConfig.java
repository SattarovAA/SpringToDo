package com.emobile.springtodo.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;

/**
 * Security configuration to implement authorization and authentication.
 */
@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {
    /**
     * Bean {@link PasswordEncoder} for security configure.
     *
     * @return default {@link BCryptPasswordEncoder}
     * @see #authenticationProvider(UserDetailsService, PasswordEncoder)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean {@link DaoAuthenticationProvider}
     * for {@link AuthenticationManager} configure.
     *
     * @param userDetailsService {@link UserDetailsService} for
     *                           {@link DaoAuthenticationProvider} configuration.
     * @param passwordEncoder    {@link PasswordEncoder} for
     *                           {@link DaoAuthenticationProvider} configuration.
     * @return {@link DaoAuthenticationProvider} with updated configuration
     * @see #authenticationManager(HttpSecurity, DaoAuthenticationProvider)
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider authenticationProvider =
                new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    /**
     * Bean {@link AuthenticationManager} for authentication configure.
     *
     * @param http                   {@link HttpSecurity} to get
     *                               {@link AuthenticationManagerBuilder}.
     * @param authenticationProvider {@link DaoAuthenticationProvider}
     *                               for {@link AuthenticationManager} configuration.
     * @return {@link AuthenticationManager} with updated configuration.
     * @throws Exception if {@link HttpSecurity} throws exception.
     * @see #filterChain(HttpSecurity, AuthenticationManager)
     */
    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http,
            DaoAuthenticationProvider authenticationProvider
    ) throws Exception {
        var authenticationBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationBuilder.authenticationProvider(authenticationProvider);
        return authenticationBuilder.build();
    }

    /**
     * Bean {@link SecurityFilterChain} initiation
     * to implement base authorization and authentication.<br>
     * Adds the Security headers to the response.
     *
     * @param http                  {@link HttpSecurity} for authorization settings.
     * @param authenticationManager {@link AuthenticationManager} with updated configuration.
     * @return {@link SecurityFilterChain} with updated configuration.
     * @throws Exception if {@link HttpSecurity} throws exception.
     */
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            AuthenticationManager authenticationManager
    ) throws Exception {
//        HeaderWriterLogoutHandler clearSiteData = new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter());
        http.authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/test/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .anyRequest()
                        .authenticated()
                )
//                .logout((logout) -> logout.addLogoutHandler(clearSiteData))
//                .logoutUrl("/perform_logout")
//                .invalidateHttpSession(true)
//                .deleteCookies("JSESSIONID")
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authenticationManager(authenticationManager);
        return http.build();
    }
}
