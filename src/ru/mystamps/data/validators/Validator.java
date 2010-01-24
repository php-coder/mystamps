package ru.mystamps.data.validators;

public interface Validator {
	
	public boolean isValid(String data);
	
	public void setMessage(String message);
	public String getMessage();
	
}
