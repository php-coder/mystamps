package ru.mystamps.data.validators;

public class PasswordConfirm extends EqualFields {
	
	public PasswordConfirm() {
		this("Password mismatch");
	}
	
	public PasswordConfirm(String message) {
		setFirstFieldName("pass1");
		setSecondFieldName("pass2");
		setMessage(message);
	}
	
}

