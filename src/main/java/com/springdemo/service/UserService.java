package com.springdemo.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.springdemo.model.UserModel;
import com.springdemo.repository.UserRepository;

@Service
public class UserService{
	private UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
	
	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager, JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}
	
	public List<UserModel> getAll(){
		return userRepository.findAll();
	}
	
	public UserModel register(UserModel user) {
		if(userRepository.getUserModelByUsername(user.getUsername()).isPresent() ||
				userRepository.getUserModelByEmail(user.getEmail()).isPresent()) {
			return null;//KÉSŐBB THROW ERROR
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole("USER"); //Alapbol az legyen
		return userRepository.save(user);
	}
	
	public String login(UserModel user) {
	    Authentication authentication =
	            authenticationManager.authenticate(
	                    new UsernamePasswordAuthenticationToken(
	                            user.getUsername(),
	                            user.getPassword()
	                    )
	            );

	    return jwtService.generateToken(authentication.getName());
	}
	
	public UserModel getUser(Long id) {
		return userRepository.findById(id).orElse(null);
	}
	
	public UserModel updateUser(Long id, UserModel user) {
		UserModel listUser = userRepository.findById(id).orElse(null);
		if(listUser != null) {
			listUser.setEmail(user.getEmail());
			listUser.setUsername(user.getUsername());
			listUser.setPassword(user.getPassword());
			
			return userRepository.save(listUser);
		}
		
		return null;
	}
	
	public boolean deleteUser(Long id) {
		UserModel user = userRepository.findById(id).orElse(null);
		if(user != null) {
			userRepository.delete(user);
			return true;
		}
		
		return false;
	}
}
