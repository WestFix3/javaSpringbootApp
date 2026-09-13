package com.springdemo.dto;

import java.util.Date;

import com.springdemo.model.TaskModel;

public class TaskResponseDTO {
	private Long id;
	private String title;
	private String description;
	private boolean completed;
	private String priority;
	private Date createdAt;
	private Date dueDate;
	
	public TaskResponseDTO() {}
	
	public TaskResponseDTO(TaskModel task) {
		this.id = task.getId();
		this.title = task.getTitle();
		this.description = task.getDescription();
		this.completed = task.isCompleted();
		this.priority = task.getPriority();
		this.createdAt = task.getCreatedAt();
		this.dueDate = task.getDueDate();
	}
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
}
