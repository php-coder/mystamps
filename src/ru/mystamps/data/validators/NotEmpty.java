package ru.mystamps.data.validators;

/**
 * @todo throw Validator_Exception
 **/
public class NotEmpty implements Validator {
	
	private String message = "Value should not be empty";
	
	public NotEmpty() {
	}
	
	public NotEmpty(String message) {
		this.message = message;
	}
	
	@Override
	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message;
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
