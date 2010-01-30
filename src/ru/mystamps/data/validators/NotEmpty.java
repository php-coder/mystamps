package ru.mystamps.data.validators;

public class NotEmpty extends AbstractValidator {
	
	public NotEmpty() {
		setMessage("Value should not be empty");
	}
	
	public NotEmpty(String message) {
		setMessage(message);
	}
	
	@Override
	public boolean isValid(String data) {
		
		if (data == null) {
			throw new IllegalArgumentException("Value cannot be a null!");
		}
		
		if (! data.equals("")) {
			return true;
		}
		
		return false;
	}
	
}
