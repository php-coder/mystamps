package ru.mystamps.data.validators;

public class PasswordLoginMismatch extends NotEqualFields {
	
	public PasswordLoginMismatch() {
		this("Password and login should be different");
	}
	
	public PasswordLoginMismatch(String message) {
		setFirstFieldName("login");
		setSecondFieldName("pass1");
		setMessage(message);
	}
	
}

