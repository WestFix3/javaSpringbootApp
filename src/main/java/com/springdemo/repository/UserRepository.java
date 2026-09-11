package com.springdemo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springdemo.model.UserModel;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long>{
	public Optional<UserModel> getUserModelByUsername(String username);
	public Optional<UserModel> getUserModelByEmail(String email);
}