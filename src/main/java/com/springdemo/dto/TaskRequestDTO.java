package com.springdemo.dto;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TaskRequestDTO {
	@NotBlank(message = "A cím nem lehet üres!")
	private String title;
	@NotBlank(message = "A leírás nem lehet üres!")
	private String description;
	@NotBlank(message = "A prioritás nem lehet üres!")
	private String priority;
	@NotNull(message = "A határidő nem lehet üres!")
	private Date dueDate;
	private boolean completed;
	
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
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
}
