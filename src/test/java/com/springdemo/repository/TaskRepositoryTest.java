package com.springdemo.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.springdemo.model.TaskModel;
import com.springdemo.model.UserModel;

@DataJpaTest
public class TaskRepositoryTest {
	@Autowired
	private TaskRepository taskRepository;
	@Autowired
	private UserRepository userRepository;
	
	@Test
	void findByUser_shouldReturnTasks_whenUserHasTasks() {
		UserModel user = new UserModel("user@user.com", "user", "user", "USER");
		TaskModel task = new TaskModel("Feladat", "Leiras", "Fontos", new Date());
		task.setUser(user);
		
		userRepository.save(user);
		taskRepository.save(task);
		
		List<TaskModel> foundTasks = taskRepository.findByUser(user);
		
		assertFalse(foundTasks.isEmpty());
		assertEquals(task.getTitle(), foundTasks.get(0).getTitle());
		assertEquals(task.getDescription(), foundTasks.get(0).getDescription());
		assertEquals(task.getPriority(), foundTasks.get(0).getPriority());
	}
	
	@Test
	void findByUser_shouldReturnEmpty_whenUserHasNoTasks() {
		UserModel user = new UserModel("user@user.com", "user", "user", "USER");
		userRepository.save(user);
		
		List<TaskModel> foundTasks = taskRepository.findByUser(user);

		assertTrue(foundTasks.isEmpty());
	}
}
