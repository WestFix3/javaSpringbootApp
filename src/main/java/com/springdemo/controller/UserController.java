package com.springdemo.controller;

import com.springdemo.model.UserModel;
import com.springdemo.service.UserService;

import java.util.List;

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
	
	private UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping
	public List<UserModel> getAll(){
		return userService.getAll();
	}
	
	//Új user létrehozása
	@PostMapping
	public UserModel createUser(@RequestBody UserModel user) {
		return userService.createUser(user);
	}
	
	//User lekérdezése
	@GetMapping("/{id}")
	public UserModel getUser(@PathVariable Long id) {
		return userService.getUser(id);
	}
	
	//User frissítése
	@PutMapping("/{id}")
	public UserModel updateUser(@PathVariable Long id, @RequestBody UserModel user) {
		return userService.updateUser(id, user);
	}

	//User törlése
	@DeleteMapping("/{id}")
	public UserModel deleteUser(@PathVariable Long id) {
		return userService.deleteUser(id);
	}
}