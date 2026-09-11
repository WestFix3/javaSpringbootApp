package com.springdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.springdemo.security.JwtAuthenticationFilter;
import com.springdemo.service.CustomUserDetailsService;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http,
	                                               JwtAuthenticationFilter jwtFilter,
	                                               AuthenticationProvider authenticationProvider) throws Exception {
		http
	    .sessionManagement(session ->
	        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

	    .authenticationProvider(authenticationProvider)

	    .authorizeHttpRequests(auth -> auth
	        .requestMatchers(
	            "/users/register",
	            "/users/login",
	            "/h2-console/**" //http://localhost:8080/h2-console
	        ).permitAll()
	        .requestMatchers("/users/admin/**").hasRole("ADMIN")
	        .anyRequest().authenticated()
	    )
	    
	    .exceptionHandling(exception -> exception

	    	    // Nincs bejelentkezve
	    	    .authenticationEntryPoint((request, response, ex) -> {
	    	        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    	        response.setContentType("text/plain;charset=UTF-8");
	    	        response.getWriter().write("Bejelentkezés szükséges!");
	    	    })

	    	    // Be van jelentkezve, de nincs jogosultsága
	    	    .accessDeniedHandler((request, response, ex) -> {
	    	        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
	    	        response.setContentType("text/plain;charset=UTF-8");
	    	        response.getWriter().write("Nincs jogosultságod ehhez a művelethez!");
	    	    })
	    	)

	    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

	    .csrf(csrf -> csrf.disable())

	    .headers(headers ->
	        headers.frameOptions(frame -> frame.disable())
	    );

	    return http.build();
	}

    
    @Bean
    public AuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
    	DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    	provider.setUserDetailsService(userDetailsService);
    	provider.setPasswordEncoder(passwordEncoder);
    	return provider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}