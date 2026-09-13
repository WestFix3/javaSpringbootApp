package com.springdemo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springdemo.dto.TaskRequestDTO;
import com.springdemo.dto.TaskResponseDTO;
import com.springdemo.service.TaskService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tasks")
public class TaskController {
	private final TaskService taskService;
	
	public TaskController(TaskService taskService) {
		this.taskService = taskService;
	}
	
	//Saját feladat létrehozása
	@PostMapping("/create")
	public ResponseEntity<TaskResponseDTO> createTsak(@Valid @RequestBody TaskRequestDTO task, 
																		  Authentication authentication){
		return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(task, authentication));
	}
	
	//Saját feladatok lekérése
	@GetMapping("/all")
	public ResponseEntity<List<TaskResponseDTO>> getByUser(Authentication authentication){
		return ResponseEntity.ok(taskService.getByUser(authentication));
	}
	
	//Feladat módosítása
	@PostMapping("/{id}")
	public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long id, 
													  @Valid @RequestBody TaskRequestDTO task,
													  Authentication authentication){
		return ResponseEntity.ok(taskService.updateTask(id, task, authentication));
	}
	
	//Feladat törlése
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTask(@PathVariable Long id, Authentication authentication){
		taskService.deleteTask(id, authentication);
		return ResponseEntity.noContent().build();
	}
	
	//Feladat befejezése
	@PatchMapping("/{id}/completed")
	public ResponseEntity<TaskResponseDTO> changeComplete(@PathVariable Long id, 
														  @RequestParam boolean completed,
														  Authentication authentication){
		return ResponseEntity.ok(taskService.changeComplete(id, completed, authentication));
	}
	
	
	//ADMIN: 
	//Összes feladat lekérése
	@GetMapping("/admin/all")
	public ResponseEntity<List<TaskResponseDTO>> getAllTasks(){
		return ResponseEntity.ok(taskService.getAllTasks());
	}
}
