package com.springdemo.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.springdemo.Exceptions.UserAlreadyExistsException;
import com.springdemo.Exceptions.UserNotFoundException;
import com.springdemo.dto.LoginRequestDTO;
import com.springdemo.dto.UserRequestDTO;
import com.springdemo.dto.UserResponseDTO;
import com.springdemo.dto.UserUpdateDTO;
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
	
	public UserResponseDTO register(UserRequestDTO user) {
		if(userRepository.getUserModelByUsername(user.getUsername()).isPresent() ||
				userRepository.getUserModelByEmail(user.getEmail()).isPresent()) {
			throw new UserAlreadyExistsException("Username or email already exists!");
		}
		UserModel newUser = new UserModel(user.getEmail(), user.getUsername(),
				passwordEncoder.encode(user.getPassword()), "USER");
		userRepository.save(newUser);
		return new UserResponseDTO(newUser);
	}
	
	public String login(LoginRequestDTO user) {
	    Authentication authentication =
	            authenticationManager.authenticate(
	                    new UsernamePasswordAuthenticationToken(
	                            user.getUsername(),
	                            user.getPassword()
	                    )
	            );

	    return jwtService.generateToken(authentication.getName());
	}
	
	//Gondolkoztam blacklisten, de ide nem éri meg
	public String logout(Authentication authentication) {
	    String username = authentication.getName();
	    return "User logged out successfully: " + username;
	}
	
	//User Funkciók
	
	public UserResponseDTO profileSelf(Authentication authentication) {
		String username = authentication.getName();
		UserModel user = userRepository.getUserModelByUsername(username).orElseThrow(
												() -> new UserNotFoundException("User not found!"));
		return new UserResponseDTO(user);
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
	
	public List<UserResponseDTO> getAll(){
		return userRepository.findAll().stream().map(x -> new UserResponseDTO(x)).toList();
	}
	
	public UserResponseDTO getUser(Long id) {
		return new UserResponseDTO(userRepository.findById(id).
				orElseThrow(() -> new UserNotFoundException("User not found!")));
	}
	
	public UserResponseDTO updateUser(Long id, UserUpdateDTO user) {
		UserModel listUser = userRepository.findById(id).orElseThrow(
				() -> new UserNotFoundException("User not found!"));

		listUser.setEmail(user.getEmail());
		listUser.setUsername(user.getUsername());
			
		userRepository.save(listUser);
		return new UserResponseDTO(listUser);
	}
	
	public void deleteUser(Long id) {
		UserModel user = userRepository.findById(id).orElseThrow(
				() -> new UserNotFoundException("User not found!"));
		userRepository.delete(user);
	}
}
