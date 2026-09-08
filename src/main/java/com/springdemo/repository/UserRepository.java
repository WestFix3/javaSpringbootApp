package com.springdemo.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.springdemo.model.UserModel;

@Repository
public class UserRepository{
	private List<UserModel> users;
	private Long nextId = 1L;
	
	public UserRepository() {
		this.users = new ArrayList<>();
	}
	
	public List<UserModel> getAll(){
		return users;
	}
	
	public UserModel createUser(UserModel user) {
		user.setId(nextId++);
		this.users.add(user);
		return user;
	}
	
	public UserModel getUser(Long id) {
		return users.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
	}
	
	public UserModel deleteUser(UserModel user) {
		users.remove(user);
		return user;
	}
}