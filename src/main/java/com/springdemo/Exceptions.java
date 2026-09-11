package com.springdemo;

public class Exceptions{
	
	//Ha egy usert keresünk, de nem található
	public static class UserNotFoundException extends RuntimeException{
		public UserNotFoundException(String message) {
			super(message);
		}
	}
	
	//Regisztrációnál, ha már van ilyen username vagy email
	public static class UserAlreadyExistsException extends RuntimeException{
		public UserAlreadyExistsException(String message) {
			super(message);
		}
	}
}
