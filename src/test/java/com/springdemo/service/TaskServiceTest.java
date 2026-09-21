package com.springdemo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.springdemo.dto.TaskRequestDTO;
import com.springdemo.dto.TaskResponseDTO;
import com.springdemo.exceptions.Exceptions.TaskNotFoundException;
import com.springdemo.exceptions.Exceptions.UserNotFoundException;
import com.springdemo.model.TaskModel;
import com.springdemo.model.UserModel;
import com.springdemo.repository.TaskRepository;
import com.springdemo.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
	@Mock
	private TaskRepository taskRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private Authentication authentication;
	@InjectMocks
	private TaskService taskService;
	
	@Test
	void createTask_shouldReturnTask_whenUserExists() {
		String username = "user";
		TaskRequestDTO task = new TaskRequestDTO();
		task.setTitle("Feladat");
		task.setDescription("Leirás");
		task.setPriority("Fontos");
		task.setDueDate(new Date());
		
		UserModel user = new UserModel("user@user.com", "user", "user", "USER");
		
		when(userRepository.getUserModelByUsername(username)).thenReturn(Optional.of(user));
		when(authentication.getName()).thenReturn(username);
		when(taskRepository.save(any(TaskModel.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
		
		TaskResponseDTO taskDto = taskService.createTask(task, authentication);
		
		assertEquals(task.getTitle(), taskDto.getTitle());
		assertEquals(task.getDescription(), taskDto.getDescription());
		assertEquals(task.getPriority(), taskDto.getPriority());
		assertEquals(task.getDueDate(), taskDto.getDueDate());
		
		verify(userRepository).getUserModelByUsername(username);
		verify(taskRepository).save(any(TaskModel.class));
	}
	
	@Test
	void createTask_shouldThrowException_whenUserNotFound() {
		String username = "user";
		TaskRequestDTO task = new TaskRequestDTO();
		task.setTitle("Feladat");
		task.setDescription("Leirás");
		task.setPriority("Fontos");
		task.setDueDate(new Date());
		
		when(authentication.getName()).thenReturn(username);
		when(userRepository.getUserModelByUsername(username)).thenReturn(Optional.empty());
		
		UserNotFoundException ex = assertThrows(UserNotFoundException.class, 
				() -> taskService.createTask(task, authentication));
		
		assertEquals("User not found!", ex.getMessage());
		
		verify(authentication).getName();
		verify(userRepository).getUserModelByUsername(username);
		verify(taskRepository, never()).save(any(TaskModel.class));
	}
	
	@Test
	void getByUser_shouldReturnTasks_whenUserExists() {
		String username = "user";
		UserModel user = new UserModel("user@user.com", "user", "user", "USER");
		TaskModel task = new TaskModel("Feladat", "Leiras", "Fontos", new Date());
		when(authentication.getName()).thenReturn(username);
		when(userRepository.getUserModelByUsername(username)).thenReturn(Optional.of(user));
		when(taskRepository.findByUser(user)).thenReturn(List.of(task));
		
		List<TaskResponseDTO> response = taskService.getByUser(authentication);
		
		assertEquals(1, response.size());
		assertEquals(task.getTitle(), response.get(0).getTitle());
		assertEquals(task.getDescription(), response.get(0).getDescription());
		assertEquals(task.getPriority(), response.get(0).getPriority());
		assertEquals(task.getDueDate(), response.get(0).getDueDate());
		
		verify(authentication).getName();
		verify(userRepository).getUserModelByUsername(username);
		verify(taskRepository).findByUser(user);
	}
	
	@Test
	void getByUser_shouldThrowException_whenUserNotFound() {
		String username = "user";
		
		when(authentication.getName()).thenReturn(username);
		when(userRepository.getUserModelByUsername(username)).thenReturn(Optional.empty());
		
		UserNotFoundException ex = assertThrows(UserNotFoundException.class, 
				() -> taskService.getByUser(authentication));
		
		assertEquals("User not found!", ex.getMessage());
		
		verify(authentication).getName();
		verify(userRepository).getUserModelByUsername(username);
		verify(taskRepository, never()).findByUser(any(UserModel.class));
	}
	
	@Test
	void updateTask_shouldReturnUpdatedTask_whenTaskBelongsToUser() {
		String username = "user";
		TaskRequestDTO updatetask = new TaskRequestDTO();
		updatetask.setTitle("Feladat2");
		updatetask.setDescription("Leirás2");
		updatetask.setPriority("Fontos2");
		updatetask.setDueDate(new Date());
		UserModel user = new UserModel("user@user.com", "user", "user", "USER");
		TaskModel task = new TaskModel("Feladat", "Leiras", "Fontos", new Date());
		task.setId(1L);
		
		when(authentication.getName()).thenReturn(username);
		when(userRepository.getUserModelByUsername(username)).thenReturn(Optional.of(user));
		when(taskRepository.findByUser(user)).thenReturn(List.of(task));
		
		TaskResponseDTO response = taskService.updateTask(1L, updatetask, authentication);
		
		assertEquals(updatetask.getTitle(), response.getTitle());
		assertEquals(updatetask.getDescription(), response.getDescription());
		assertEquals(updatetask.getPriority(), response.getPriority());
		assertEquals(updatetask.getDueDate(), response.getDueDate());
		
		verify(authentication).getName();
		verify(userRepository).getUserModelByUsername(username);
		verify(taskRepository).findByUser(user);
		verify(taskRepository).save(any(TaskModel.class));
	}
	
	@Test
	void updateTask_shouldThrowException_whenTaskNotFound() {
		String username = "user";
		TaskRequestDTO updatetask = new TaskRequestDTO();
		updatetask.setTitle("Feladat2");
		updatetask.setDescription("Leirás2");
		updatetask.setPriority("Fontos2");
		updatetask.setDueDate(new Date());
		UserModel user = new UserModel("user@user.com", "user", "user", "USER");
		
		when(authentication.getName()).thenReturn(username);
		when(userRepository.getUserModelByUsername(username)).thenReturn(Optional.of(user));
		when(taskRepository.findByUser(user)).thenReturn(List.of());
		
		TaskNotFoundException ex = assertThrows(TaskNotFoundException.class, 
				() -> taskService.updateTask(1L, updatetask, authentication));
		
		assertEquals("Task not found!", ex.getMessage());
		
		verify(authentication).getName();
		verify(userRepository).getUserModelByUsername(username);
		verify(taskRepository).findByUser(user);
		verify(taskRepository, never()).save(any(TaskModel.class));
	}
	
	@Test
	void updateTask_shouldThrowException_whenUsernameNotFound() {
		String username = "user";
	
		when(authentication.getName()).thenReturn(username);
		when(userRepository.getUserModelByUsername(username)).thenReturn(Optional.empty());
		
		UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class, 
				() -> taskService.updateTask(1L, new TaskRequestDTO(), authentication));
		
		assertEquals("User not found!", ex.getMessage());
		
		verify(authentication).getName();
		verify(userRepository).getUserModelByUsername(username);
		verify(taskRepository, never()).findByUser(any(UserModel.class));
		verify(taskRepository, never()).save(any(TaskModel.class));
	}
	
	@Test
	void updateTask_shouldThrowException_whenTaskIdDoesNotMatch() {
	    String username = "user";
	    UserModel user =new UserModel("user@user.com", "user", "user", "USER");
	    TaskModel task =new TaskModel("Feladat", "Leiras", "Fontos", new Date());
	    task.setId(2L);

	    when(authentication.getName()).thenReturn(username);
	    when(userRepository.getUserModelByUsername(username)).thenReturn(Optional.of(user));
	    when(taskRepository.findByUser(user)).thenReturn(List.of(task));

	    TaskNotFoundException ex = assertThrows(TaskNotFoundException.class,
	            () -> taskService.updateTask(1L, new TaskRequestDTO(), authentication));

	    assertEquals("Task not found!", ex.getMessage());

	    verify(authentication).getName();
	    verify(userRepository).getUserModelByUsername(username);
	    verify(taskRepository).findByUser(user);
	    verify(taskRepository, never()).save(any(TaskModel.class));
	}
	
	@Test
	void deleteTask_shouldDeleteTask_whenTaskBelongsToUser() {
		String username = "user";
		UserModel user = new UserModel("user@user.com", "user", "user", "USER");
		TaskModel task = new TaskModel("Feladat", "Leiras", "Fontos", new Date());
		task.setId(1L);
		
		when(authentication.getName()).thenReturn(username);
		when(userRepository.getUserModelByUsername(username)).thenReturn(Optional.of(user));
		when(taskRepository.findByUser(user)).thenReturn(List.of(task));
		
		taskService.deleteTask(1L, authentication);
		
		verify(authentication).getName();
		verify(userRepository).getUserModelByUsername(username);
		verify(taskRepository).findByUser(user);
		verify(taskRepository).delete(task);
	}
	
	@Test
	void deleteTask_shouldThrowException_whenUsernameNotFound() {
		String username = "user";
		
		when(authentication.getName()).thenReturn(username);
		when(userRepository.getUserModelByUsername(username)).thenReturn(Optional.empty());
		
		UserNotFoundException ex = assertThrows(UserNotFoundException.class, 
				() -> taskService.deleteTask(1L, authentication));
		
		assertEquals("User not found!", ex.getMessage());
		
		verify(authentication).getName();
		verify(userRepository).getUserModelByUsername(username);
		verify(taskRepository, never()).findByUser(any(UserModel.class));
		verify(taskRepository, never()).delete(any(TaskModel.class));
	}
	
	@Test
	void deleteTask_shouldThrowException_whenTaskNotFound() {
		String username = "user";
		UserModel user = new UserModel("user@user.com", "user", "user", "USER");
		
		when(authentication.getName()).thenReturn(username);
		when(userRepository.getUserModelByUsername(username)).thenReturn(Optional.of(user));
		when(taskRepository.findByUser(user)).thenReturn(List.of());
		
		TaskNotFoundException ex = assertThrows(TaskNotFoundException.class, 
				() -> taskService.deleteTask(1L, authentication));
		
		assertEquals("Task not found!", ex.getMessage());
		
		verify(authentication).getName();
		verify(userRepository).getUserModelByUsername(username);
		verify(taskRepository).findByUser(user);
		verify(taskRepository, never()).delete(any(TaskModel.class));
	}
	
	@Test
	void deleteTask_shouldThrowException_whenTaskIdDoesNotMatch() {
		String username = "user";
		UserModel user = new UserModel("user@user.com", "user", "user", "USER");
		TaskModel task = new TaskModel("Feladat", "Leiras", "Fontos", new Date());
		task.setId(2L);
		
		when(authentication.getName()).thenReturn(username);
		when(userRepository.getUserModelByUsername(username)).thenReturn(Optional.of(user));
		when(taskRepository.findByUser(user)).thenReturn(List.of(task));
		
		TaskNotFoundException ex = assertThrows(TaskNotFoundException.class, 
				() -> taskService.deleteTask(1L, authentication));
		
		assertEquals("Task not found!", ex.getMessage());
		
		verify(authentication).getName();
		verify(userRepository).getUserModelByUsername(username);
		verify(taskRepository).findByUser(user);
		verify(taskRepository, never()).delete(any(TaskModel.class));
	}
	
	@Test
	void changeComplete_shouldReturnUpdatedTask_whenTaskBelongsToUser() {
		String username = "user";
		UserModel user = new UserModel("user@user.com", "user", "user", "USER");
		TaskModel task = new TaskModel("Feladat", "Leiras", "Fontos", new Date());
		task.setId(1L);
		task.setCompleted(false);
		
		when(authentication.getName()).thenReturn(username);
		when(userRepository.getUserModelByUsername(username)).thenReturn(Optional.of(user));
		when(taskRepository.findByUser(user)).thenReturn(List.of(task));
		
		TaskResponseDTO response = taskService.changeComplete(1L, true, authentication);
		
		assertTrue(response.isCompleted());
		
		verify(authentication).getName();
		verify(userRepository).getUserModelByUsername(username);
		verify(taskRepository).findByUser(user);
		verify(taskRepository).save(task);
	}
	
	@Test
	void changeComplete_shouldNotSaveTask_whenCompletedStatusIsAlreadySame() {
		String username = "user";
		UserModel user = new UserModel("user@user.com", "user", "user", "USER");
		TaskModel task = new TaskModel("Feladat", "Leiras", "Fontos", new Date());
		task.setId(1L);
		task.setCompleted(true);
		
		when(authentication.getName()).thenReturn(username);
		when(userRepository.getUserModelByUsername(username)).thenReturn(Optional.of(user));
		when(taskRepository.findByUser(user)).thenReturn(List.of(task));
		
		TaskResponseDTO response = taskService.changeComplete(1L, true, authentication);
		
		assertTrue(response.isCompleted());
		
		verify(authentication).getName();
		verify(userRepository).getUserModelByUsername(username);
		verify(taskRepository).findByUser(user);
		verify(taskRepository, never()).save(task);
	}
	
	@Test
	void changeComplete_shouldThrowException_whenUsernameNotFound() {
		String username = "user";
		
		when(authentication.getName()).thenReturn(username);
		when(userRepository.getUserModelByUsername(username)).thenReturn(Optional.empty());
		
		UserNotFoundException ex = assertThrows(UserNotFoundException.class, 
				() -> taskService.changeComplete(1L, true, authentication));
		
		assertEquals("User not found!", ex.getMessage());
		
		verify(authentication).getName();
		verify(userRepository).getUserModelByUsername(username);
		verify(taskRepository, never()).findByUser(any(UserModel.class));
		verify(taskRepository, never()).save(any(TaskModel.class));
	}
	
	@Test
	void changeComplete_shouldThrowException_whenTaskNotFound() {
		String username = "user";
		UserModel user = new UserModel("user@user.com", "user", "user", "USER");
		
		when(authentication.getName()).thenReturn(username);
		when(userRepository.getUserModelByUsername(username)).thenReturn(Optional.of(user));
		when(taskRepository.findByUser(user)).thenReturn(List.of());
		
		TaskNotFoundException ex = assertThrows(TaskNotFoundException.class, 
				() -> taskService.changeComplete(1L, true, authentication));
		
		assertEquals("Task not found!", ex.getMessage());
		
		verify(authentication).getName();
		verify(userRepository).getUserModelByUsername(username);
		verify(taskRepository).findByUser(user);
		verify(taskRepository, never()).save(any(TaskModel.class));
	}
	
	@Test
	void getAllTasks_shouldReturnTasks() {
		Pageable pageable = PageRequest.of(0, 10);
		TaskModel task = new TaskModel("Feladat", "Leiras", "Fontos", new Date());
	    task.setId(1L);
	    
	    Page<TaskModel> tasks = new PageImpl<>(List.of(task));
		when(taskRepository.findAll(pageable)).thenReturn(tasks);
		Page<TaskResponseDTO> response = taskService.getAllTasks(pageable);
		
		assertEquals(1, response.getTotalElements());
	    assertEquals("Feladat", response.getContent().get(0).getTitle());

	    verify(taskRepository).findAll(pageable);
	}
}
