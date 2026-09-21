package com.springdemo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import io.jsonwebtoken.JwtException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JwtServiceTest {
	
	@Autowired
	private JwtService jwtService;
	
	@Test
	void generateToken_shouldReturnToken_whenUsernameIsValid() {
		String token = jwtService.generateToken("user");
		
		assertNotNull(token);
	    assertFalse(token.isBlank());
	}
	
	@Test
	void extractUsername_shouldReturnUsername_whenTokenIsValid() {
		String user = "user";
		String token = jwtService.generateToken(user);
		
		String extractedUsername = jwtService.extractUsername(token);
		
		assertEquals(user, extractedUsername);
	}
	
	@Test
	void isValid_shouldReturnTrue_whenTokenIsValid() {
		String username = "user";
		
		String token = jwtService.generateToken(username);
		
		assertTrue(jwtService.isValid(token));
	}
	
	@Test
	void isValid_shouldReturnFalse_whenTokenIsInvalid() {
		String username = "user";
		
		String invalidToken = jwtService.generateToken(username) + "Random test";
		
		assertFalse(jwtService.isValid(invalidToken));
	}
	
	@Test
	void extractUsername_shouldThrowException_whenTokenIsInvalid() {
		String username = "user";
		
		String invalidToken = jwtService.generateToken(username) + "Random test";
		
		assertThrows(JwtException.class, () -> jwtService.extractUsername(invalidToken));
	}
	
	@Test
	void generateToken_shouldReturnDifferentTokens_forDifferentUsernames() {
		String user1 = jwtService.generateToken("user1");
		String user2 = jwtService.generateToken("user2");
		
		assertNotEquals(user1, user2);
	}
	
	@Test
	void isValid_shouldReturnFalse_whenTokenIsMalformed() {
		String invalidToken = "not-a-jwt-token";

	    assertFalse(jwtService.isValid(invalidToken));
	}
}
