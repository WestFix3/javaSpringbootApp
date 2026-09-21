package com.springdemo.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.springdemo.service.CustomUserDetailsService;
import com.springdemo.service.JwtService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import jakarta.servlet.ServletException;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.mock.web.MockFilterChain;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {
	@Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }
	    
    @Test
    void jwtFilter_shouldAuthenticateUser_whenTokenIsValid() throws Exception {
    	String token = "validToken";
        String username = "user";
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        
        when(jwtService.extractUsername(token)).thenReturn(username);
        when(customUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isValid(token)).thenReturn(true);
        
        jwtAuthenticationFilter.doFilter(request, response, filterChain);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        assertNotNull(authentication);
        assertEquals(userDetails, authentication.getPrincipal());
        assertTrue(authentication.isAuthenticated());
        
        verify(jwtService).extractUsername(token);
        verify(customUserDetailsService).loadUserByUsername(username);
        verify(jwtService).isValid(token);
    }
    
    @Test
    void jwtFilter_shouldNotAuthenticateUser_whenTokenIsInvalid() throws ServletException, IOException {
    	String username = "user";
    	String token = "validToken";
    	
    	MockHttpServletRequest request = new MockHttpServletRequest();
    	request.addHeader("Authorization", "Bearer " + token);
    	MockHttpServletResponse response = new MockHttpServletResponse();
    	MockFilterChain filterChain = new MockFilterChain();
    	
    	when(jwtService.extractUsername(token)).thenReturn(username);
    	when(customUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
    	when(jwtService.isValid(token)).thenReturn(false);
    	
    	jwtAuthenticationFilter.doFilter(request, response, filterChain);
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	
    	assertNull(authentication);
    	
    	verify(jwtService).extractUsername(token);
        verify(customUserDetailsService).loadUserByUsername(username);
        verify(jwtService).isValid(token);
    }
    
    @Test
    void jwtFilter_shouldNotAuthenticateUser_whenAuthorizationHeaderIsMissing() throws ServletException, IOException {
    	MockHttpServletRequest request = new MockHttpServletRequest();
    	MockHttpServletResponse response = new MockHttpServletResponse();
    	MockFilterChain filterChain = new MockFilterChain();
    	
    	jwtAuthenticationFilter.doFilter(request, response, filterChain);
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	
    	assertNull(authentication);
    	
    	verify(jwtService, never()).extractUsername(anyString());
        verify(customUserDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtService, never()).isValid(anyString());
    }
    
    @Test
    void jwtFilter_shouldNotAuthenticateUser_whenAuthorizationHeaderIsInvalid() throws ServletException, IOException {
    	String token = "validToken";
    	
    	MockHttpServletRequest request = new MockHttpServletRequest();
    	request.addHeader("Authorization", "Basic " + token);
    	MockHttpServletResponse response = new MockHttpServletResponse();
    	MockFilterChain filterChain = new MockFilterChain();
    	
    	jwtAuthenticationFilter.doFilter(request, response, filterChain);
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	
    	assertNull(authentication);
    	
    	verify(jwtService, never()).extractUsername(anyString());
    	verify(customUserDetailsService, never()).loadUserByUsername(anyString());
    	verify(jwtService, never()).isValid(anyString());
    }
    
    @Test
    void jwtFilter_shouldNotAuthenticateUser_whenUsernameIsNull() throws ServletException, IOException {
    	String token = "validToken";
    	
    	MockHttpServletRequest request = new MockHttpServletRequest();
    	request.addHeader("Authorization", "Bearer " + token);
    	MockHttpServletResponse response = new MockHttpServletResponse();
    	MockFilterChain filterChain = new MockFilterChain();
    	
    	when(jwtService.extractUsername(token)).thenReturn(null);
    	
    	jwtAuthenticationFilter.doFilter(request, response, filterChain);
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	
    	assertNull(authentication);
    	
    	verify(jwtService).extractUsername(token);
    	verify(customUserDetailsService, never()).loadUserByUsername(anyString());
    	verify(jwtService, never()).isValid(anyString());
    }
    
    @Test
    void jwtFilter_shouldThrowException_whenUserNotFound() throws ServletException, IOException {
    	String username = "user";
    	String token = "validToken";
    	
    	MockHttpServletRequest request = new MockHttpServletRequest();
    	request.addHeader("Authorization", "Bearer " + token);
    	MockHttpServletResponse response = new MockHttpServletResponse();
    	MockFilterChain filterChain = new MockFilterChain();
    	
    	when(jwtService.extractUsername(token)).thenReturn(username);
    	when(customUserDetailsService.loadUserByUsername(username)).thenThrow(new UsernameNotFoundException("User not found!"));
    	
    	assertThrows(
                UsernameNotFoundException.class,
                () -> jwtAuthenticationFilter.doFilter(request, response, filterChain)
        );
    	
    	verify(jwtService).extractUsername(token);
    	verify(customUserDetailsService).loadUserByUsername(username);
    	verify(jwtService, never()).isValid(anyString());
    }
    
    @Test
    void jwtFilter_shouldNotAuthenticateUserAgain_whenAuthenticationAlreadyExists() throws ServletException, IOException {
    	UsernamePasswordAuthenticationToken existingAuthentication =
    	        new UsernamePasswordAuthenticationToken("existingUser", null, List.of());

    	SecurityContextHolder.getContext()
    	        .setAuthentication(existingAuthentication);
    	
    	String username = "user";
    	String token = "validToken";
    	
    	MockHttpServletRequest request = new MockHttpServletRequest();
    	request.addHeader("Authorization", "Bearer " + token);
    	MockHttpServletResponse response = new MockHttpServletResponse();
    	MockFilterChain filterChain = new MockFilterChain();
    	
    	when(jwtService.extractUsername(token)).thenReturn(username);
    	
    	jwtAuthenticationFilter.doFilter(request, response, filterChain);
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	
    	assertNotNull(authentication);
        assertSame(existingAuthentication, authentication);

        verify(jwtService).extractUsername(token);
        verify(customUserDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtService, never()).isValid(anyString());
    }
    
    @Test
    void jwtFilter_shouldPropagateException_whenExtractUsernameFails() throws ServletException, IOException {
    	String token = "validToken";
    	
    	MockHttpServletRequest request = new MockHttpServletRequest();
    	request.addHeader("Authorization", "Bearer " + token);
    	MockHttpServletResponse response = new MockHttpServletResponse();
    	MockFilterChain filterChain = new MockFilterChain();
    	
    	when(jwtService.extractUsername(token)).thenThrow(new UsernameNotFoundException("User not found!"));
    	
    	assertThrows(
    			UsernameNotFoundException.class,
                () -> jwtAuthenticationFilter.doFilter(request, response, filterChain)
        );
    	
    	verify(jwtService).extractUsername(token);
        verify(customUserDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtService, never()).isValid(anyString());
    }
}
