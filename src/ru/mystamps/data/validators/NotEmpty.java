package ru.mystamps.data.validators;

import javax.servlet.ServletRequest;

public class NotEmpty extends AbstractValidator {
	
	public NotEmpty() {
		this("Value should not be empty");
	}
	
	public NotEmpty(String message) {
		setMessage(message);
	}
	
	@Override
	public boolean isValid(String data, ServletRequest request) {
		
		if (data == null) {
			throw new IllegalArgumentException("Value cannot be a null!");
		}
		
		if (! data.equals("")) {
			return true;
		}
		
		return false;
	}
	
}
