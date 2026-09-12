package com.springdemo.controller;

import com.springdemo.Exceptions.UserAlreadyExistsException;
import com.springdemo.Exceptions.UserNotFoundException;
import com.springdemo.dto.LoginRequestDTO;
import com.springdemo.dto.UserRequestDTO;
import com.springdemo.dto.UserResponseDTO;
import com.springdemo.dto.UserUpdateDTO;
import com.springdemo.model.UserModel;
import com.springdemo.service.JwtService;
import com.springdemo.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController{
	
	private final UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	//Regisztrálás
	@PostMapping("/register")
	public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO user) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(user));
	}
	
	//Bejelentkezés
	@PostMapping("/login")
	public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDTO user) {
		return ResponseEntity.ok(userService.login(user));
	}
	
	//Kijelentkezés
	@PostMapping("/logout")
    public ResponseEntity<String> logout(Authentication authentication) {
        return ResponseEntity.ok(userService.logout(authentication) + "!");
    }
	
	//Felhasználó profilja
	@GetMapping("/profile")
	public ResponseEntity<UserResponseDTO> profileSelf(Authentication authentication){
		return ResponseEntity.ok(userService.profileSelf(authentication));
	}
	
	//Felhasználó harakiri
	@DeleteMapping("/me")
	public ResponseEntity<String> deleteSelf(Authentication authentication){
		return ResponseEntity.ok(userService.deleteSelf(authentication));
	}
	
	//Felhasználó törlése
	@DeleteMapping("/admin/delete/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
			
		return ResponseEntity.noContent().build();
	}
	
	//Felhasználó frissítése
	@PutMapping("/admin/update/{id}")
	public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, 
			@Valid @RequestBody UserUpdateDTO user)  {
		return ResponseEntity.ok(userService.updateUser(id, user));
	}
	
	//Felhasználó keresése
	@GetMapping("/admin/search/{id}")
	public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id){
		return ResponseEntity.ok(userService.getUser(id));
	}
	
	//Felhasználók lekérése ADMIN FUNKCIÓ
	@GetMapping("/admin/users")
	public ResponseEntity<List<UserResponseDTO>> getAll(){
		return ResponseEntity.ok(userService.getAll());
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<String> handleUsernameNotFound(UserNotFoundException ex){
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}
	
	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<String> handleUserAlreadyExists(UserAlreadyExistsException ex){
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex){
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getBindingResult().getFieldError().getDefaultMessage());
	}
}