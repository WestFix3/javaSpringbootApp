package com.springdemo.controller;

import com.springdemo.model.UserModel;
import com.springdemo.service.JwtService;
import com.springdemo.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
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
	
	//Felhasználók lekérése ADMIN FUNKCIÓ
	@GetMapping
	public ResponseEntity<List<UserModel>> getAll(){
	    List<UserModel> users = userService.getAll();
	    return ResponseEntity.ok(users);
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
    public ResponseEntity<String> logout() {
		if(true) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nincs bejelentkezve felhasználó!");
		}
        return ResponseEntity.ok("Kijelentkeztetve: " + "asd" + "!");
    }
	
	//Felhasználó frissítése
	@PutMapping("/{id}")
	public ResponseEntity<UserModel> updateUser(@PathVariable Long id, @RequestBody UserModel user)  {
		UserModel updated = userService.updateUser(id, user);
		if(updated == null) {
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.ok(updated);
	}
	
	//Felhasználó keresése
	@GetMapping("/{id}")
	public ResponseEntity<UserModel> getUser(@PathVariable Long id){
		UserModel foundUser = userService.getUser(id);
		if(foundUser == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(foundUser);
	}

	//Felhasználó törlése
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		boolean deleted = userService.deleteUser(id);
		if(!deleted) {
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.noContent().build();
	}
}