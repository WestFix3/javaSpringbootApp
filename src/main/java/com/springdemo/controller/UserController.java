package com.springdemo.controller;

import com.springdemo.Exceptions.UserAlreadyExistsException;
import com.springdemo.Exceptions.UserNotFoundException;
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
	public ResponseEntity<UserModel> register(@Valid @RequestBody UserModel user) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(user));
	}
	
	//Bejelentkezés
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody UserModel user) {
		return ResponseEntity.ok(userService.login(user));
	}
	
	//Kijelentkezés
	@PostMapping("/logout")
    public ResponseEntity<String> logout(Authentication authentication) {
        return ResponseEntity.ok(userService.logout(authentication) + "!");
    }
	
	//Felhasználó profilja
	@GetMapping("/profile")
	public ResponseEntity<UserModel> profileSelf(Authentication authentication){
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
	public ResponseEntity<UserModel> updateUser(@PathVariable Long id, @RequestBody UserModel user)  {
		UserModel updated = userService.updateUser(id, user);
		if(updated == null) {
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.ok(updated);
	}
	
	//Felhasználó keresése
	@GetMapping("/admin/search/{id}")
	public ResponseEntity<UserModel> getUser(@PathVariable Long id){
		UserModel foundUser = userService.getUser(id);
		if(foundUser == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(foundUser);
	}
	
	//Felhasználók lekérése ADMIN FUNKCIÓ
	@GetMapping("/admin/users")
	public ResponseEntity<List<UserModel>> getAll(){
		List<UserModel> users = userService.getAll();
		return ResponseEntity.ok(users);
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