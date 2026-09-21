package com.springdemo.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.springdemo.model.UserModel;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void getUserModelByUsername_shouldReturnUser_whenUserExists() {
    	UserModel user = new UserModel("user@user.com", "user", "user", "USER");
    	
    	userRepository.save(user);
    	UserModel foundUser = userRepository.getUserModelByUsername("user").orElseThrow();
    	
    	assertNotNull(foundUser.getId());
    	assertEquals(user.getEmail(), foundUser.getEmail());
    	assertEquals(user.getUsername(), foundUser.getUsername());
    	assertEquals(user.getPassword(), foundUser.getPassword());
    	assertEquals(user.getRole(), foundUser.getRole());
    }
    
    @Test
    void getUserModelByUsername_shouldReturnEmpty_whenUserDoesNotExist() {
    	Optional<UserModel> result = userRepository.getUserModelByUsername("user");
    	
    	assertTrue(result.isEmpty());
    }
    
    @Test
    void getUserModelByEmail_shouldReturnUser_whenUserExists() {
    	UserModel user = new UserModel("user@user.com", "user", "user", "USER");
    	
    	userRepository.save(user);
    	UserModel foundUser = userRepository.getUserModelByEmail("user@user.com").orElseThrow();
    	
    	assertNotNull(foundUser.getId());
    	assertEquals(user.getEmail(), foundUser.getEmail());
    	assertEquals(user.getUsername(), foundUser.getUsername());
    	assertEquals(user.getPassword(), foundUser.getPassword());
    	assertEquals(user.getRole(), foundUser.getRole());
    }
    
    @Test
    void getUserModelByEmail_shouldReturnEmpty_whenUserDoesNotExist() {
    	Optional<UserModel> result = userRepository.getUserModelByEmail("user@user.com");
    	
    	assertTrue(result.isEmpty());
    }
}
