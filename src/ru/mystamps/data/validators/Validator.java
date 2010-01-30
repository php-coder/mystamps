package ru.mystamps.data.validators;

import javax.servlet.ServletRequest;

public interface Validator {
	
	public boolean isValid(String data, ServletRequest request);
	
	public void setMessage(String message);
	public String getMessage();
	
}
