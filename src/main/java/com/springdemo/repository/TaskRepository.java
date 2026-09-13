package com.springdemo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springdemo.model.TaskModel;
import com.springdemo.model.UserModel;

@Repository
public interface TaskRepository extends JpaRepository<TaskModel, Long>{
	public List<TaskModel> findByUser(UserModel user);
}
