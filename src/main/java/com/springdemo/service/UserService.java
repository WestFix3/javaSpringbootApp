package com.springdemo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.springdemo.model.UserModel;
import com.springdemo.repository.UserRepository;

@Service
public class UserService{
	private UserRepository userRepository;
	
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	public List<UserModel> getAll(){
		return userRepository.getAll();
	}
	
	public UserModel createUser(UserModel user) {
		return userRepository.createUser(user);
	}
	
	public UserModel getUser(Long id) {
		return userRepository.getUser(id);
	}
	
	public UserModel updateUser(Long id, UserModel user) {
		UserModel listUser = userRepository.getUser(id);
		if(listUser != null) {
			listUser.setUsername(user.getUsername());
			listUser.setPassword(user.getPassword());
			
			return listUser;
		}
		
		return null;
	}
	
	public UserModel deleteUser(Long id) {
		if(userRepository.getUser(id) != null) {
			UserModel user = userRepository.getUser(id);
			userRepository.deleteUser(user);
			return user;
		}
		
		return null;
	}
}
