package com.springdemo.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.springdemo.Exceptions.UserAlreadyExistsException;
import com.springdemo.Exceptions.UserNotFoundException;
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
	
	public UserModel register(UserModel user) {
		if(userRepository.getUserModelByUsername(user.getUsername()).isPresent() ||
				userRepository.getUserModelByEmail(user.getEmail()).isPresent()) {
			throw new UserAlreadyExistsException("Username or email already exists!");
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
	
	public String logout(Authentication authentication) { //Gondolkoztam blacklisten, de ide nem éri meg
	    String username = authentication.getName();
	    return "User logged out successfully: " + username;
	}
	
	//User Funkciók
	
	public UserModel profileSelf(Authentication authentication) {
		String username = authentication.getName();
		UserModel user = userRepository.getUserModelByUsername(username).orElseThrow(
												() -> new UserNotFoundException("User not found!"));
		return user;
	}
	
	public String deleteSelf(Authentication authentication) {
		String name = authentication.getName();
		UserModel user = userRepository.getUserModelByUsername(name).orElseThrow(
												() ->  new UserNotFoundException("User not found!"));
		userRepository.delete(user);
		logout(authentication);
		return "User deleted Succesfully!";
	}
	
	//ADMIN FUNKCIÓK
	
	public List<UserModel> getAll(){
		return userRepository.findAll();
	}
	
	public UserModel getUser(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found!"));
	}
	
	public UserModel updateUser(Long id, UserModel user) {
		UserModel listUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found!"));

		listUser.setEmail(user.getEmail());
		listUser.setUsername(user.getUsername());
		listUser.setPassword(user.getPassword());
			
		return userRepository.save(listUser);
	}
	
	public void deleteUser(Long id) {
		UserModel user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found!"));
		userRepository.delete(user);
	}
}
