package ru.mystamps.data.validators;

public abstract class AbstractValidator implements Validator {
	
	private String message;
	
	/**
	 * @throws IllegalArgumentException when message is null or empty
	 **/
	@Override
	public void setMessage(String message) {
		
		if (message == null) {
			throw new IllegalArgumentException("Message is null!");
		}
		
		if (message.equals("")) {
			throw new IllegalArgumentException("Message is empty!");
		}
		
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
	
}

