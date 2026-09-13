package com.springdemo.model;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tasks")
public class TaskModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "title", nullable = false)
	private String title;
	@Column(name = "description", nullable = false)
	private String description;
	@Column(name="completed")
	//@Column(columnDefinition = "boolean default false") de ez nélkül is default false
	private boolean completed;
	@Column(name = "priority", nullable = false)
	private String priority;
	@Column(name = "createdAt")
	@CreationTimestamp
	private Date createdAt;
	@Column(name = "dueDate", nullable = false)
	private Date dueDate;
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private UserModel user;
	
	public TaskModel() {}
	
	public TaskModel(String title, String description, String priority, Date dueDate) {
		this.title = title;
		this.description = description;
		this.priority = priority;
		this.dueDate = dueDate;
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
	public UserModel getUser() {
		return user;
	}
	public void setUser(UserModel user) {
		this.user = user;
	}
}
