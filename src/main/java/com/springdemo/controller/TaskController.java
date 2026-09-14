package com.springdemo.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springdemo.dto.TaskRequestDTO;
import com.springdemo.dto.TaskResponseDTO;
import com.springdemo.service.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/tasks")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {
	private final TaskService taskService;
	
	public TaskController(TaskService taskService) {
		this.taskService = taskService;
	}
	
	//Saját feladat létrehozása
	@PostMapping("/create")
	@Operation(summary = "Feladat létrehozása")
	@ApiResponses({
	    @ApiResponse(responseCode = "201", description = "Sikeres létrehozás"),
	    @ApiResponse(responseCode = "400", description = "Hibás adatok"),
	    @ApiResponse(responseCode = "404", description = "A felhasználó nem található")
	})
	public ResponseEntity<TaskResponseDTO> createTsak(@Valid @RequestBody TaskRequestDTO task, 
																		  Authentication authentication){
		return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(task, authentication));
	}
	
	//Saját feladatok lekérése
	@GetMapping("/all")
	@Operation(summary = "Saját feladatok lekérése")
	@ApiResponse(responseCode = "200", description = "Sikeres lekérés")
	public ResponseEntity<List<TaskResponseDTO>> getByUser(Authentication authentication){
		return ResponseEntity.ok(taskService.getByUser(authentication));
	}
	
	//Feladat módosítása
	@PutMapping("/{id}")
	@Operation(summary = "Feladat módosítása")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Sikeres módosítás"),
		@ApiResponse(responseCode = "400", description = "Hibás adatok"),
		@ApiResponse(responseCode = "404", description = "A feladat nem található")
	})
	public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long id, 
													  @Valid @RequestBody TaskRequestDTO task,
													  Authentication authentication){
		return ResponseEntity.ok(taskService.updateTask(id, task, authentication));
	}
	
	//Feladat törlése
	@DeleteMapping("/{id}")
	@Operation(summary = "Feladat törlése")
	@ApiResponses({
	    @ApiResponse(responseCode = "204", description = "Sikeres törlés"),
	    @ApiResponse(responseCode = "404", description = "A feladat nem található")
	})
	public ResponseEntity<Void> deleteTask(@PathVariable Long id, Authentication authentication){
		taskService.deleteTask(id, authentication);
		return ResponseEntity.noContent().build();
	}
	
	//Feladat befejezése
	@PatchMapping("/{id}/completed")
	@Operation(summary = "Feladat teljesítettségének módosítása")
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "Sikeres módosítás"),
	    @ApiResponse(responseCode = "400", description = "Hiányzó vagy hibás completed paraméter"),
	    @ApiResponse(responseCode = "404", description = "A feladat nem található")
	})
	public ResponseEntity<TaskResponseDTO> changeComplete(@PathVariable Long id, 
														  @RequestParam boolean completed,
														  Authentication authentication){
		return ResponseEntity.ok(taskService.changeComplete(id, completed, authentication));
	}
	
	
	//ADMIN: 
	//Összes feladat lekérése
	//http://localhost:8080/task/admin/all?page=1&size=1&sort=username,asc
	@GetMapping("/admin/all")
	@Operation(summary = "Összes feladat lekérése")
	@ApiResponse(
	    responseCode = "200",
	    description = "Sikeres lekérés"
	)
	public ResponseEntity<Page<TaskResponseDTO>> getAllTasks(Pageable pageable){
		return ResponseEntity.ok(taskService.getAllTasks(pageable));
	}
}
