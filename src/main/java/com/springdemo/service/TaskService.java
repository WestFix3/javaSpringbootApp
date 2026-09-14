package com.springdemo.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.springdemo.dto.TaskRequestDTO;
import com.springdemo.dto.TaskResponseDTO;
import com.springdemo.exceptions.Exceptions.TaskNotFoundException;
import com.springdemo.exceptions.Exceptions.UserNotFoundException;
import com.springdemo.model.TaskModel;
import com.springdemo.model.UserModel;
import com.springdemo.repository.TaskRepository;
import com.springdemo.repository.UserRepository;

@Service
public class TaskService {
	private final TaskRepository taskRepository;
	private final UserRepository userRepository;
	
	public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
		this.taskRepository = taskRepository;
		this.userRepository = userRepository;
	}
	
	//Feladat létrehozása
	public TaskResponseDTO createTask(TaskRequestDTO task, Authentication authentication) {
		TaskModel newTask = new TaskModel(task.getTitle(), task.getDescription(),
				task.getPriority(), task.getDueDate());
		UserModel user = userRepository.getUserModelByUsername(authentication.getName()).orElseThrow(
				() -> new UserNotFoundException("User not found!"));
		newTask.setUser(user);
		taskRepository.save(newTask);
		return new TaskResponseDTO(newTask);
	}
	
	//Feladatok lekérése felhasználó szerint
	public List<TaskResponseDTO> getByUser(Authentication authentication){
		UserModel user = userRepository.getUserModelByUsername(authentication.getName()).orElseThrow(
				() -> new UsernameNotFoundException("User not found!"));
		
		return taskRepository.findByUser(user).stream().map(x -> new TaskResponseDTO(x)).toList();
	}
	
	//Feladat módositása
	public TaskResponseDTO updateTask(Long id, TaskRequestDTO task, Authentication authentication) {
		UserModel user = userRepository.getUserModelByUsername(authentication.getName()).orElseThrow(
				() -> new UsernameNotFoundException("User not found!"));
		TaskModel oldTask = taskRepository.findByUser(user).stream().filter(x -> x.getId().equals(id)).
				findFirst().orElseThrow(() -> new TaskNotFoundException("Task not found!"));
		BeanUtils.copyProperties(task, oldTask, "id");
		taskRepository.save(oldTask);
		return new TaskResponseDTO(oldTask);
	}
	
	//Feladat törlése
	public void deleteTask(Long id, Authentication authentication) {
		UserModel user = userRepository.getUserModelByUsername(authentication.getName()).orElseThrow(
				() -> new UsernameNotFoundException("User not found!"));
		TaskModel task = taskRepository.findByUser(user).stream().filter(x -> x.getId().equals(id)).
				findFirst().orElseThrow(() -> new TaskNotFoundException("Task not found!"));
		taskRepository.delete(task);
	}
	
	//Teljesitve érték változtatása
	public TaskResponseDTO changeComplete(Long id, boolean completed, Authentication authentication) {
		UserModel user = userRepository.getUserModelByUsername(authentication.getName()).orElseThrow(
				() -> new UsernameNotFoundException("User not found!"));
		TaskModel task = taskRepository.findByUser(user).stream().filter(x -> x.getId().equals(id)).
				findFirst().orElseThrow(() -> new TaskNotFoundException("Task not found!"));
		if(task.isCompleted() != completed) {
			task.setCompleted(completed);
			taskRepository.save(task);
		}
		
		return new TaskResponseDTO(task);
	}
	
	//ADMIN
	//Minden feladat kiadása
	public Page<TaskResponseDTO> getAllTasks(Pageable pageable){
		return taskRepository.findAll(pageable).map(x -> new TaskResponseDTO(x));
		//return taskRepository.findAll().stream().map(x -> new TaskResponseDTO(x)).toList();
	}
}
